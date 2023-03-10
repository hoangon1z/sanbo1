package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.job.ExchangePushJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WebsocketMarketHandler implements MarketHandler {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ExchangePushJob pushJob;

    /**
     * 推送币种简化信息
     *
     * @param symbol
     * @param thumb
     */
    @Override
    public void handleThumb(String symbol, CoinThumb thumb) {
        //推送缩略行情
        pushJob.addThumb(symbol, thumb);
    }

    @Override
    public void handlePlate(String symbol, TradePlate plate) {
        pushJob.addPlates(symbol, plate);
    }

    @Override
    public void handleKLine(String symbol, KLine kLine) {
        //推送K线数据
        pushJob.pushTickKline(symbol, kLine);
//        messagingTemplate.convertAndSend("/topic/swap/kline/"+symbol, kLine);
    }

    @Override
    public void handleTrade(String symbol, List<ContractTrade> trades) {
        pushJob.addTrades(symbol, trades);
    }

    @Override
    public void handleUserOrderChange(String symbol, Long uid) {
        pushJob.pushUserOrderChange(symbol, uid);
    }

    @Override
    public void handleAddRiskPeriod(String symbol, RiskPeriod period) {
        messagingTemplate.convertAndSend("/topic/swap/risk/add/" + symbol, period);
    }

    @Override
    public void handleDelRiskPeriod(String symbol, RiskPeriod period) {
        messagingTemplate.convertAndSend("/topic/swap/risk/del/" + symbol, period);
    }

    @Override
    public void handleAddHistoryRiskPeriod(String symbol, RiskPeriod period) {
        messagingTemplate.convertAndSend("/topic/swap/risk/history/" + symbol, period);
    }
}
