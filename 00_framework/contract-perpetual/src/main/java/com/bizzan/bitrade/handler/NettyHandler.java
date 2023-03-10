package com.bizzan.bitrade.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqmd.netty.annotation.HawkBean;
import com.aqmd.netty.annotation.HawkMethod;
import com.aqmd.netty.common.NettyCacheUtils;
import com.aqmd.netty.push.HawkPushServiceApi;
import com.bizzan.bitrade.constant.NettyCommand;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.netty.QuoteMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 处理Netty订阅与取消订阅
 */
@HawkBean
@Slf4j
public class NettyHandler implements MarketHandler {
    @Autowired
    private HawkPushServiceApi hawkPushService;
    private String topicOfSymbol = "CONTRACT_SYMBOL_THUMB";

    public void subscribeTopic(Channel channel, String topic) {
        String userKey = channel.id().asLongText();
        if (!NettyCacheUtils.keyChannelCache.containsKey(channel)) {
            NettyCacheUtils.keyChannelCache.put(channel, userKey);
        }
        NettyCacheUtils.storeChannel(topic, channel);
        if (NettyCacheUtils.userKey.containsKey(userKey)) {
            NettyCacheUtils.userKey.get(userKey).add(topic);
        } else {
            Set<String> userkeys = new HashSet<>();
            userkeys.add(topic);
            NettyCacheUtils.userKey.put(userKey, userkeys);
        }
    }

    public void unsubscribeTopic(Channel channel, String topic) {
        String userKey = channel.id().asLongText();
        if (NettyCacheUtils.userKey.containsKey(userKey)) {
            NettyCacheUtils.userKey.get(userKey).remove(topic);
        }
        NettyCacheUtils.keyChannelCache.remove(channel);
    }

    @HawkMethod(cmd = NettyCommand.CONTRACT_SUBSCRIBE_SYMBOL_THUMB, version = NettyCommand.COMMANDS_VERSION)
    public QuoteMessage.SimpleResponse subscribeSymbolThumb(byte[] body, ChannelHandlerContext ctx) {
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        subscribeTopic(ctx.channel(), topicOfSymbol);
        response.setCode(0).setMessage("订阅成功");
        return response.build();
    }

    @HawkMethod(cmd = NettyCommand.CONTRACT_UNSUBSCRIBE_SYMBOL_THUMB)
    public QuoteMessage.SimpleResponse unsubscribeSymbolThumb(byte[] body, ChannelHandlerContext ctx) {
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        unsubscribeTopic(ctx.channel(), topicOfSymbol);
        response.setCode(0).setMessage("取消成功");
        return response.build();
    }

    @HawkMethod(cmd = NettyCommand.CONTRACT_SUBSCRIBE_EXCHANGE)
    public QuoteMessage.SimpleResponse subscribeExchange(byte[] body, ChannelHandlerContext ctx) {
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        JSONObject json = JSON.parseObject(new String(body));
        String symbol = json.getString("symbol");
        String uid = json.getString("uid");
        if (StringUtils.isNotEmpty(uid)) {
            subscribeTopic(ctx.channel(), symbol + "-" + uid);
        }
        subscribeTopic(ctx.channel(), symbol);
        response.setCode(0).setMessage("合约" + symbol + "交易订阅成功");
        return response.build();
    }

    @HawkMethod(cmd = NettyCommand.CONTRACT_UNSUBSCRIBE_EXCHANGE)
    public QuoteMessage.SimpleResponse unsubscribeExchange(byte[] body, ChannelHandlerContext ctx) {
        QuoteMessage.SimpleResponse.Builder response = QuoteMessage.SimpleResponse.newBuilder();
        JSONObject json = JSON.parseObject(new String(body));
        log.info("取消订阅Exchange：" + json.toJSONString());
        String symbol = json.getString("symbol");
        String uid = json.getString("uid");
        if (StringUtils.isNotEmpty(uid)) {
            unsubscribeTopic(ctx.channel(), symbol + "-" + uid);
        }
        unsubscribeTopic(ctx.channel(), symbol);
        response.setCode(0).setMessage("取消订阅成功");
        return response.build();
    }

    @Override
    public void handleThumb(String symbol, CoinThumb thumb) {
        // 为保持和PC基本一致，修改为job统一push

//        byte[] body = JSON.toJSONString(thumb).getBytes();
//        hawkPushService.pushMsg(NettyCacheUtils.getChannel(topicOfSymbol),NettyCommand.CONTRACT_PUSH_SYMBOL_THUMB, body);
////        log.info("推送Thumb:"+JSON.toJSONString(thumb));
//        byte[] bytes = JSONObject.toJSONString(thumb).getBytes();
//        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol),NettyCommand.CONTRACT_PUSH_SYMBOL_THUMB, bytes);
    }

    @Override
    public void handleKLine(String symbol, KLine kLine) {
        // 推送k线
//        log.info("推送k线>>>>>:"+JSON.toJSONString(kLine));
        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol), NettyCommand.CONTRACT_PUSH_EXCHANGE_KLINE, JSONObject.toJSONString(kLine).getBytes());
    }

    @Override
    public void handleTrade(String symbol, List<ContractTrade> trades) {
        // 为保持和PC基本一致，修改为job统一push
        //推送交易
//        log.info("推送交易>>>>>:"+JSON.toJSONString(trades));
//        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol),NettyCommand.CONTRACT_PUSH_EXCHANGE_TRADE, JSONObject.toJSONString(trades).getBytes());

    }

    @Override
    public void handleUserOrderChange(String symbol, Long uid) {
        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol + "-" + uid), NettyCommand.CONTRACT_PUSH_ORDER_CHANGE, new byte[0]);
    }

    @Override
    public void handleAddRiskPeriod(String symbol, RiskPeriod period) {

    }

    @Override
    public void handleDelRiskPeriod(String symbol, RiskPeriod period) {

    }

    @Override
    public void handleAddHistoryRiskPeriod(String symbol, RiskPeriod period) {

    }

    /**
     * 推送盘口
     *
     * @param symbol
     * @param plate
     */
    @Override
    public void handlePlate(String symbol, TradePlate plate) {
        // 为保持和PC基本一致，修改为job统一push
        //log.info("推送盘口>>>>>:"+JSON.toJSONString(plate));
        //推送盘口
//        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol),NettyCommand.CONTRACT_PUSH_EXCHANGE_PLATE, plate.toJSON(24).toJSONString().getBytes());
//        //推送深度
//        hawkPushService.pushMsg(NettyCacheUtils.getChannel(symbol),NettyCommand.CONTRACT_PUSH_EXCHANGE_DEPTH, plate.toJSON(50).toJSONString().getBytes());
    }

//    public void handleOrder(short command, ExchangeOrder order){
//        try {
//            String topic = order.getSymbol() + "-" + order.getMemberId();
//            log.info("推送订单:" + JSON.toJSONString(order));
//            hawkPushService.pushMsg(NettyCacheUtils.getChannel(topic), command, JSON.toJSONString(order).getBytes());
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            log.info("推送出错"+e);
//        }
//    }
}
