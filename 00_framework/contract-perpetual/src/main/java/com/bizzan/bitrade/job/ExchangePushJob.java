package com.bizzan.bitrade.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqmd.netty.common.NettyCacheUtils;
import com.aqmd.netty.push.HawkPushServiceApi;
import com.bizzan.bitrade.constant.NettyCommand;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.handler.NettyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExchangePushJob {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private HawkPushServiceApi hawkPushService;

    private Map<String, List<ContractTrade>> tradesQueue = new HashMap<>();
    private Map<String, List<TradePlate>> plateQueue = new HashMap<>();
    private Map<String, List<CoinThumb>> thumbQueue = new HashMap<>();

    // 推送消息类型
    // 1、最新成交：
    // 2、单币种行情：{"symbol":"EOS/USDT","open":2.6,"high":2.0,"low":2.9,"close":2.9,"chg":0.9,"change":0.9,"volume":9.9,"turnover":9.9,"lastDayClose":2.9,"usdRate":2.9,"baseUsdRate":1,"zone":0}"
    // 3、最新更新K线：{"openPrice":9.9,"highestPrice":9.9,"lowestPrice":9.9,"closePrice":9.9,"time":1594234140000,"period":"1min","count":15,"volume":57.9,"turnover":9.9}"
    // 4、盘口：{"minAmount":0.00200000,"highestPrice":9411.310000,"symbol":"BTC/USDT","lowestPrice":9000.2,"maxAmount":17.2,"items":[{"price":9318.2,"amount":8.2},{"price":9317.2,"amount":0.2}],"direction":"BUY"}"

    public void addTrades(String symbol, List<ContractTrade> trades) {
        List<ContractTrade> list = tradesQueue.get(symbol);
        if (list == null) {
            list = new ArrayList<>();
            tradesQueue.put(symbol, list);
        }
        synchronized (list) {
            list.addAll(trades);
        }
    }

    public void addThumb(String symbol, CoinThumb thumb) {
        List<CoinThumb> list = thumbQueue.get(symbol);
        if (list == null) {
            list = new ArrayList<>();
            thumbQueue.put(symbol, list);
        }
        synchronized (list) {
            list.add(thumb);
        }
    }

    public void addPlates(String symbol, TradePlate plate) {
        List<TradePlate> list = plateQueue.get(symbol);
        if (list == null) {
            list = new ArrayList<>();
            plateQueue.put(symbol, list);
        }
        synchronized (list) {
            list.add(plate);
        }
    }

    /**
     * 订单变化
     */
    public void pushUserOrderChange(String symbol, Long uid) {
        messagingTemplate.convertAndSend("/topic/swap/refresh/" + symbol + "/" + uid, true);
        messagingTemplate.convertAndSend("/topic/swap/refresh/" + symbol, true);
    }

    // 推送K线
    public void pushTickKline(String symbol, KLine line) {
//        System.out.println("websocket 推送k线数据" + line);
        messagingTemplate.convertAndSend("/topic/swap/kline/" + symbol, line);
    }

    @Scheduled(fixedRate = 500)
    public void pushTrade() {
        Iterator<Map.Entry<String, List<ContractTrade>>> entryIterator = tradesQueue.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<ContractTrade>> entry = entryIterator.next();
            String symbol = entry.getKey();
            List<ContractTrade> trades = entry.getValue();
            if (trades.size() > 0) {
                synchronized (trades) {
                    messagingTemplate.convertAndSend("/topic/swap/trade/" + symbol, trades);
                    // netty推送
                    hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol), NettyCommand.CONTRACT_PUSH_EXCHANGE_TRADE, JSONObject.toJSONString(trades).getBytes());
                    trades.clear();
                }
            }
        }
    }


    @Scheduled(fixedDelay = 2000)
    public void pushPlate() {
        Iterator<Map.Entry<String, List<TradePlate>>> entryIterator = plateQueue.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<TradePlate>> entry = entryIterator.next();
            String symbol = entry.getKey();
            List<TradePlate> plates = entry.getValue();
            if (plates.size() > 0) {
                boolean hasPushAskPlate = false;
                boolean hasPushBidPlate = false;
                synchronized (plates) {
                    for (TradePlate plate : plates) {
                        if (plate.getDirection() == ContractOrderDirection.BUY && !hasPushBidPlate) {
                            hasPushBidPlate = true;
                        } else if (plate.getDirection() == ContractOrderDirection.SELL && !hasPushAskPlate) {
                            hasPushAskPlate = true;
                        } else {
                            continue;
                        }
                        //websocket推送盘口信息
                        messagingTemplate.convertAndSend("/topic/swap/trade-plate/" + symbol, plate.toJSON(24));
                        //websocket推送深度信息
                        messagingTemplate.convertAndSend("/topic/swap/trade-depth/" + symbol, plate.toJSON(50));
                        //netty推送
                        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol), NettyCommand.CONTRACT_PUSH_EXCHANGE_PLATE, plate.toJSON(24).toJSONString().getBytes());
                        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol), NettyCommand.CONTRACT_PUSH_EXCHANGE_DEPTH, plate.toJSON(50).toJSONString().getBytes());
                    }
                    plates.clear();
                }
            }
        }
    }

    @Scheduled(fixedRate = 500)
    public void pushThumb() {
        Iterator<Map.Entry<String, List<CoinThumb>>> entryIterator = thumbQueue.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<CoinThumb>> entry = entryIterator.next();
            String symbol = entry.getKey();
            List<CoinThumb> thumbs = entry.getValue();
            if (thumbs.size() > 0) {
                synchronized (thumbs) {
                    messagingTemplate.convertAndSend("/topic/swap/thumb", thumbs.get(thumbs.size() - 1));
                    byte[] body = JSON.toJSONString(thumbs.get(thumbs.size() - 1)).getBytes();
                    hawkPushService.pushMsg(NettyCacheUtils.getChannel("CONTRACT_SYMBOL_THUMB"), NettyCommand.CONTRACT_PUSH_SYMBOL_THUMB, body);
                    //  log.info("推送Thumb:"+JSON.toJSONString(thumb));
                    byte[] bytes = JSONObject.toJSONString(thumbs.get(thumbs.size() - 1)).getBytes();
                    hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol), NettyCommand.CONTRACT_PUSH_SYMBOL_THUMB, bytes);
                    thumbs.clear();
                }
            }
        }
    }
}
