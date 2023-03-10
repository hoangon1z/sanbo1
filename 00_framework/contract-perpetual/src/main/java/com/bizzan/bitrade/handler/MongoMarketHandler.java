package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.ContractMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoMarketHandler implements MarketHandler {
    @Autowired
    private ContractMarketService marketService;

    @Override
    public void handleThumb(String symbol, CoinThumb thumb) {

    }

    @Override
    public void handlePlate(String symbol, TradePlate plate) {

    }

    @Override
    public void handleKLine(String symbol, KLine kLine) {
        marketService.saveKLine(symbol, kLine);
    }

    @Override
    public void handleTrade(String symbol, List<ContractTrade> trades) {

    }

    @Override
    public void handleUserOrderChange(String symbol, Long uid) {

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
}
