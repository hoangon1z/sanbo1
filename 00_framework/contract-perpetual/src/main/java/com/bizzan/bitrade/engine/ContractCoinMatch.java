package com.bizzan.bitrade.engine;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.engine.risk.RiskControl;
import com.bizzan.bitrade.handler.MarketHandler;
import com.bizzan.bitrade.job.ExchangePushJob;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.DateUtils;
import com.bizzan.bitrade.util.MessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 合约撮合引擎
 */
public class ContractCoinMatch {

    private Logger logger = LoggerFactory.getLogger(ContractCoinMatch.class);

    private String symbol;                                           // 交易对：BTC/USDT
    private String baseSymbol;                                       // 基币：USDT
    private String coinSymbol;                                       // 币种：BTC
    private int baseCoinScale;                                       // 基币小数精度
    private CoinThumb thumb;                                         // 交易对行情
    private BigDecimal close;                                        // 火币实时价格
    private LinkedList<ContractTrade> lastedTradeList;               // 最新成交明细
    private int lastedTradeListSize = 50;

    private long lastUpdateTime = 0L;                                // 上次价格更新时间（主要用于控制价格刷新周期，因为websokcet获取的价格变化较快）
    private boolean isTriggerComplete = true;                        // 价格刷新是否完成，触发委托及爆仓
    private BigDecimal nowPrice = BigDecimal.ZERO;                   // 当前最新价格

    private ContractCoinService contractCoinService;                  // 合约币种服务
    private ContractOrderEntrustService contractOrderEntrustService;  // 合约委托单服务
    private ContractOrderService contractOrderService;                // 合约订单服务
//    private RiskControlService riskControlService;                    // 风控存储服务

    private List<ContractOrder> contractOrderList;                    // 持仓列表(被动爆仓处理，止盈止损）
    private List<ContractOrderEntrust> contractOrderEntrustList;      // 委托列表(计划委托)

    private List<MarketHandler> handlers;                             // 行情、概要等处理者
    private ExchangePushJob exchangePushJob;                          // 推送任务

    //卖盘盘口信息
    private TradePlate sellTradePlate;
    //买盘盘口信息
    private TradePlate buyTradePlate;

    private boolean isStarted = false;                                // 是否启动完成（用于初始化时，获取一些数据库未处理的订单的，如果没获取完，不允许处理）

    // 风控设置数据
    private RiskControl riskControl;
    private Boolean riskEnabled = true; // 开启分控

    /**
     * 构造函数
     *
     * @param symbol
     */
    public ContractCoinMatch(String symbol, int baseCoinScale) {
        this.symbol = symbol;
        this.baseCoinScale = baseCoinScale;
        this.coinSymbol = symbol.split("/")[0];
        this.baseSymbol = symbol.split("/")[1];
        this.handlers = new ArrayList<>();
        this.lastedTradeList = new LinkedList<>();
        this.buyTradePlate = new TradePlate(symbol, ContractOrderDirection.BUY);
        this.sellTradePlate = new TradePlate(symbol, ContractOrderDirection.SELL);
        // 初始化行情
        this.initializeThumb();
        // 初始化风控
        riskControl = new RiskControl(symbol, BigDecimal.ZERO);
    }

    /**
     * 启动引擎，加载未处理订单
     */
    public void run() {
        // 数据库查找订单，加载到列表中
        logger.info("启动match引擎" + symbol);
        contractOrderList = contractOrderService.findAllNeedMatchBySymbol(symbol);
        contractOrderEntrustList = contractOrderEntrustService.findAllNeedMatchBySymbol(symbol);
        this.isStarted = true;
    }

    /**
     * 更新盘口（买卖盘，火币Websocket获取到的是20条）
     *
     * @param buyPlateItems
     * @param sellPlateItems
     */
    public void refreshPlate(List<TradePlateItem> buyPlateItems, List<TradePlateItem> sellPlateItems) {
        if (!this.isStarted) return;

//        if (tossCoin(BigDecimal.valueOf()))
        if (riskEnabled) riskControl.decoratePlate(close, buyPlateItems, sellPlateItems);
//        System.out.println("火币深度");
//        System.out.println(buyPlateItems);
//        System.out.println(sellPlateItems);

        this.buyTradePlate.setItems(buyPlateItems);
        this.sellTradePlate.setItems(sellPlateItems);
        handleTradePlate(sellTradePlate);
        handleTradePlate(buyTradePlate);
    }

    public void handleTradePlate(TradePlate plate) {
        for (MarketHandler storage : handlers) {
            storage.handlePlate(symbol, plate);
        }
    }

    /**
     * 推送系统单发生变化
     *
     * @param symbol
     * @param uid
     */
    public void pushUserOrderChange(String symbol, Long uid) {
        for (MarketHandler storage : handlers) {
            storage.handleUserOrderChange(symbol, uid);
        }
    }

    /**
     * 更新行情
     *
     * @param thumb
     */
    public void refreshThumb(CoinThumb thumb) {
        if (!this.isStarted) return;
        close = thumb.getClose(); // 改价格不受后期修改影响
        // 风控控制价格
        if (riskEnabled) riskControl.decoratePrice(thumb);
        thumb.setClose(thumb.getClose().setScale(baseCoinScale, BigDecimal.ROUND_HALF_UP));
        refreshPrice(thumb.getClose());
        this.thumb.setHigh(thumb.getHigh());
        this.thumb.setLow(thumb.getLow());
        this.thumb.setOpen(thumb.getClose());
        this.thumb.setClose(thumb.getClose());
        this.thumb.setTurnover(thumb.getTurnover());
        this.thumb.setVolume(thumb.getVolume());
        this.thumb.setUsdRate(thumb.getClose());
        this.thumb.setBaseUsdRate(BigDecimal.ONE);
        // 计算变化（变化金额以及变化比例，其中变化比例会出现负数）
        this.thumb.setChange(thumb.getClose().subtract(thumb.getOpen()));
        if (thumb.getOpen().compareTo(BigDecimal.ZERO) > 0) {
            this.thumb.setChg(this.thumb.getChange().divide(this.thumb.getOpen(), 4, RoundingMode.UP));
        }
        // 推送行情
        handleCoinThumb();
    }

    /**
     * 更新k线
     *
     * @param kLine
     */
    public void refreshKline(KLine kLine) {
        // 风控数据修饰k线
        if (riskEnabled) {
            riskControl.decorateRiskKline(kLine);
        }
        handleKLineStorage(kLine);
    }

    /**
     * 更新价格
     * 更新价格时，涉及到计划委托、止盈止损检测、爆仓检查，有一定耗时操作
     *
     * @param newPrice
     */
    public void refreshPrice(BigDecimal newPrice) {
//        if ("BTC/USDT".equals(symbol)) logger.info("最新价格： {}", newPrice);


        if (!this.isStarted) return;

        // 上一次任务尚未完成
        if (!isTriggerComplete) {
            if ("BTC/USDT".equals(symbol)) logger.info("上一次任务未结束: {}", newPrice);
            return;
        }

        long currentTime = Calendar.getInstance().getTimeInMillis();

        // 控制1秒+更新一次
        if (currentTime - lastUpdateTime > 1000) {
            lastUpdateTime = currentTime;

            // 价格未发生变化，无需继续操作
            if (this.nowPrice.compareTo(newPrice) == 0) {
//                if ("BTC/USDT".equals(symbol)) logger.info("价格未变化: {}", newPrice);
                return;
            }
//            logger.info("{} 价格发生改变：{} -> {}", symbol, nowPrice, newPrice);
            this.nowPrice = newPrice;
            // 开始检查委托
            this.isTriggerComplete = false;
            this.process(newPrice);
        }
    }

    /**
     * 更新成交明细
     *
     * @param tradeArrayList
     */
    public void refreshLastedTrade(List<ContractTrade> tradeArrayList) {
        for (ContractTrade trade : tradeArrayList) {

            if (riskEnabled) riskControl.decorateTrade(trade, baseCoinScale);

            if (lastedTradeList.size() > lastedTradeListSize) {
                this.lastedTradeList.removeLast();
                this.lastedTradeList.addFirst(trade);
            } else {
                this.lastedTradeList.addFirst(trade);
            }
        }
        handleTrade(tradeArrayList);
    }

    public void handleTrade(List<ContractTrade> trades) {
        for (MarketHandler storage : handlers) {
            storage.handleTrade(symbol, trades);
        }
    }

    // 处理持仓单和委托单
    public void process(BigDecimal newPrice) {
//        CountDownLatch countDownLatch = new CountDownLatch(2);
        this.processContractOrderList(newPrice);
        this.processContractOrderEntrustList(newPrice);
        this.isTriggerComplete = true; //todo 实际上并没有起到作用
    }

    /**
     * 处理持仓订单
     * todo 有个比较严重的问题，就是手动平仓和自动平仓的并发问题
     */
    public void processContractOrderList(BigDecimal newPrice) {
        // 启动线程进行计算
        if (contractOrderList.size() == 0) {
            return;
        }
//        if ("BTC/USDT".equals(symbol)) System.out.println("处理持仓订单"+symbol+":"+contractOrderList.size());
        new Thread(new Runnable() {
            public void run() {
//                System.out.println(contractOrderList);
                List<ContractOrder> closeOrders = new ArrayList<>();
                List<ContractOrder> forceCloseOrders = new ArrayList<>();
                HashSet<Long> removeIds = new HashSet<>();
                synchronized (contractOrderList) {
                    long now = Calendar.getInstance().getTimeInMillis();
                    for (ContractOrder order : contractOrderList) {
                        Boolean force = false;
                        if (order.getContractCoinType() == ContractCoinType.SECOND && now >= order.getPlanCloseTime()) {
                            // 秒合约时间到
                            force = false;
                        } else if (order.getDirection() == ContractOrderDirection.BUY && newPrice.compareTo(order.getLiquidationPrice()) <= 0) {
                            // 多单强制平仓
                            force = true;
                        } else if (order.getDirection() == ContractOrderDirection.SELL && newPrice.compareTo(order.getLiquidationPrice()) >= 0) {
                            // 空单强制平仓
                            force = true;
                        } else {
                            continue;
                        }
                        removeIds.add(order.getId());
                        if (force) {
                            forceCloseOrders.add(order);
                        } else {
                            closeOrders.add(order);
                        }
                    }
                }
                for (Long id : removeIds) { // todo 优化
                    removeContractOrder(id);
                }
                for (ContractOrder order : closeOrders) {
                    if ("BTC/USDT".equals(symbol))
                        logger.info("处理订单： {}  应处理时间: {}", order.getId(), DateUtils.getDateStr(order.getPlanCloseTime()));
                    //强制平仓
                    MessageResult rs = contractOrderService.close(order, newPrice, false); // todo 异常处理
                    pushUserOrderChange(order.getSymbol(), order.getMemberId());
                }
                for (ContractOrder order : forceCloseOrders) {
                    if ("BTC/USDT".equals(symbol))
                        logger.info("处理订单： {}  应处理时间: {}", order.getId(), DateUtils.getDateStr(order.getPlanCloseTime()));
                    //强制平仓
                    MessageResult rs = contractOrderService.close(order, newPrice, true); // todo 异常处理
                    pushUserOrderChange(order.getSymbol(), order.getMemberId());
                }

            }
        }).start();
    }

    class EntrustHandler {
        private BigDecimal price;
        private ContractOrderEntrust entrust;

        public EntrustHandler(BigDecimal price, ContractOrderEntrust entrust) {
            this.price = price;
            this.entrust = entrust;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public ContractOrderEntrust getEntrust() {
            return entrust;
        }
    }

    // 处理委托订单(撮合委托单)
    public void processContractOrderEntrustList(BigDecimal newPrice) {
        // 启动线程进行计算
        if (contractOrderEntrustList.size() == 0) {
            return;
        }
//        System.out.println("处理委托订单"+symbol+":"+contractOrderEntrustList.size());
        new Thread(new Runnable() {
            public void run() {
//                System.out.println(contractOrderEntrustList);
                HashSet<Long> removeIds = new HashSet<>();
                List<EntrustHandler> handlers = new ArrayList<>();
                synchronized (contractOrderEntrustList) {
                    for (ContractOrderEntrust entrust : contractOrderEntrustList) { // todo 性能优化，排序减少循环
                        BigDecimal entrustPrice = entrust.getEntrustPrice();
                        if (entrust.getMarketPrice()) { //市价
                            entrustPrice = newPrice;
                        }
                        if (entrust.getType() == ContractOrderType.SPOT_LIMIT && entrust.getTriggeringTime() == null) { // 计划委托未触发
                            if (entrust.getTriggerPrice().subtract(newPrice).abs().compareTo(BigDecimal.ONE) < 0) { // 差价小于1 todo
                                entrust.setTriggeringTime(Calendar.getInstance().getTimeInMillis());
                            } else {
                                continue;
                            }
                        }

                        BigDecimal buyPrice;
                        BigDecimal sellPrice;
                        BigDecimal handlePrice;
                        if (entrust.getDirection() == ContractOrderDirection.BUY) { // 买
                            buyPrice = entrustPrice;
                            sellPrice = newPrice;
                        } else { //卖
                            buyPrice = newPrice;
                            sellPrice = entrustPrice;
                        }
                        if (buyPrice.compareTo(sellPrice) < 0) {
                            continue;
                        } else if (buyPrice.compareTo(sellPrice) == 0) { //买入委托价格与最低卖出委托价格相同，以该价格为成交价格
                            handlePrice = newPrice;
                        } else { // 买入委托价格高于当前价格的，以当前价格为成交价格；//todo 这里简化了
                            handlePrice = newPrice;
                        }
                        removeIds.add(entrust.getId());
                        handlers.add(new EntrustHandler(handlePrice, entrust));
                    }
                }
                for (Long id : removeIds) { // todo 优化
                    removeContractOrderEntrust(id);
                }
                for (EntrustHandler handler : handlers) {
                    ContractOrder order = contractOrderEntrustService.handle(handler.getEntrust(), handler.getPrice()); //委托转持仓，分开仓和平仓
                    handleContranctOrder(order);
                    pushUserOrderChange(order.getSymbol(), order.getMemberId());
                    /** 处理风控 **/
                    if (riskEnabled && order.getPlanCloseTime() != null) {
                        riskControl.add(order.getMemberId(), handler.getPrice(), order.getPlanCloseTime() / 1000, order.getPosition(), order.getDirection(), order.getShareNumber());
                    }
                }
            }
        }).start();

    }


    // 维护订单（持仓单）todo 并发问题
    public void handleContranctOrder(ContractOrder resource) {
        Long id = null;
        for (ContractOrder order : contractOrderList) {
            if (order.getId().equals(resource.getId())) {
                id = order.getId();
            }
        }
        if (id == null) { // 新增
            addContranctOrder(resource);
            // 新增持仓单时如果是秒合约则初始化分控数据

        } else if (resource.getPosition().equals(0)) { //删除
            removeContractOrder(id);
        } else { //修改
            removeContractOrder(id);
            addContranctOrder(resource);
        }
    }

    // 移除订单（持仓单）
    public void removeContractOrder(long id) {
        int tagIndex = -1;
        int index = 0;
        for (ContractOrder order : contractOrderList) {
            if (order.getId() == id) {
                tagIndex = index;
            }
            index++;
        }
        // 多线程安全操作 todo 这种写法并不能保证线程安全
        List<ContractOrder> synchronizedList = Collections.synchronizedList(contractOrderList);
        if (tagIndex > -1) synchronizedList.remove(tagIndex);
    }

    // 添加订单（持仓单）
    public void addContranctOrder(ContractOrder order) {
        // 多线程安全操作
        List<ContractOrder> synchronizedList = Collections.synchronizedList(contractOrderList);
        synchronizedList.add(order);
    }

    // 移除订单（委托单）
    public void removeContractOrderEntrust(long id) {
        int tagIndex = -1;
        int index = 0;
        for (ContractOrderEntrust order : contractOrderEntrustList) {
            if (order.getId() == id) {
                tagIndex = index;
            }
            index++;
        }
        List<ContractOrderEntrust> synchronizedList = Collections.synchronizedList(contractOrderEntrustList);
        if (tagIndex > -1) synchronizedList.remove(tagIndex);
    }

    // 添加订单（委托单）
    public void addContranctOrderEntrust(ContractOrderEntrust orderEntrust) {
        List<ContractOrderEntrust> synchronizedList = Collections.synchronizedList(contractOrderEntrustList);
        synchronizedList.add(orderEntrust);
    }

    /**
     * 初始化Thumb
     */
    public void initializeThumb() {
        this.thumb = new CoinThumb();
        this.thumb.setChg(BigDecimal.ZERO);                 // 变化百分比（例：4%）
        this.thumb.setChange(BigDecimal.ZERO);              // 变化金额
        this.thumb.setOpen(BigDecimal.ZERO);                // 开盘价
        this.thumb.setClose(BigDecimal.ZERO);               // 收盘价
        this.thumb.setHigh(BigDecimal.ZERO);                // 最高价
        this.thumb.setLow(BigDecimal.ZERO);                 // 最低价
        this.thumb.setBaseUsdRate(BigDecimal.ONE);          // 基础USDT汇率
        this.thumb.setLastDayClose(BigDecimal.ZERO);        // 前日收盘价
        this.thumb.setSymbol(this.symbol);                  // 交易对符号
        this.thumb.setUsdRate(BigDecimal.valueOf(7.0));     // USDT汇率
        this.thumb.setZone(0);                              // 交易区
        this.thumb.setVolume(BigDecimal.ZERO);              // 成交量
        this.thumb.setTurnover(BigDecimal.ZERO);            // 成交额
    }

    public void handleCoinThumb() {
        for (MarketHandler storage : handlers) {
            storage.handleThumb(symbol, thumb);
        }
    }

    public void handleKLineStorage(KLine kLine) {
        for (MarketHandler storage : handlers) {
            storage.handleKLine(symbol, kLine);
        }
    }


    // 获取交易对符号
    public String getSymbol() {
        return this.symbol;
    }

    // 获取币种符号
    public String getCoinSymbol() {
        return this.coinSymbol;
    }

    // 获取基币符号
    public String getBaseSymbol() {
        return this.baseSymbol;
    }

    // 获取交易对最新报价
    public BigDecimal getNowPrice() {
        return this.nowPrice;
    }

    // 获取交易对最新行情
    public CoinThumb getThumb() {
        return this.thumb;
    }

    // 获取最新成交明细
    public List<ContractTrade> getLastedTradeList() {
        return this.lastedTradeList;
    }

    // 获取盘口数据
    public TradePlate getTradePlate(ContractOrderDirection direction) {
        if (direction == ContractOrderDirection.BUY) {
            return buyTradePlate;
        } else {
            return sellTradePlate;
        }
    }

    // 设置合约币种服务
    public void setContractCoinService(ContractCoinService contractCoinService) {
        this.contractCoinService = contractCoinService;
    }

    // 设置合约订单委托服务
    public void setContractOrderEntrustService(ContractOrderEntrustService contractOrderEntrustService) {
        this.contractOrderEntrustService = contractOrderEntrustService;
    }

    // 设置合约订单服务
    public void setContractOrderService(ContractOrderService contractOrderService) {
        this.contractOrderService = contractOrderService;
    }

    // 设置风控服务
    public void setRiskControlService(RiskControlService riskControlService) {
        this.riskControl.setRiskControlService(riskControlService);
    }

    // 设置风控策略服务
    public void setRiskStrategyService(RiskStrategyService riskStrategyService) {
        this.riskControl.setRiskStrategyService(riskStrategyService);
    }

    // 添加处理者
    public void addHandler(MarketHandler storage) {
        handlers.add(storage);
        if (riskEnabled) this.riskControl.handlers.add(storage);
    }

    public void setExchangePushJob(ExchangePushJob job) {
        this.exchangePushJob = job;
    }

    public List<ContractOrder> getContractOrderList() {
        return contractOrderList;
    }

    public RiskControl getRiskControl() {
        return riskControl;
    }

    public void setRate(BigDecimal rate) {
        this.riskControl.setRate(rate);
    }

    public BigDecimal getRate() {
        return this.riskControl.getRate();
    }
}
