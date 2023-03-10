package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.*;

import java.util.List;

public interface MarketHandler {

    /**
     * 存储币种信息
     */
    void handleThumb(String symbol, CoinThumb thumb);

    /**
     * 处理深度信息
     */
    void handlePlate(String symbol, TradePlate plate);

    /**
     * 存储K线信息
     *
     * @param kLine
     */
    void handleKLine(String symbol, KLine kLine);

    /**
     * 存储交易
     */
    void handleTrade(String symbol, List<ContractTrade> trades);

    /**
     * 推送系统实际交易变化
     */
    void handleUserOrderChange(String symbol, Long uid);

    /**
     * 推送风控数据区间增加
     */
    void handleAddRiskPeriod(String symbol, RiskPeriod period);

    /**
     * 推送风控数据区间废弃
     */
    void handleDelRiskPeriod(String symbol, RiskPeriod period);

    /**
     * 推送历史风控数据区间增加
     */
    void handleAddHistoryRiskPeriod(String symbol, RiskPeriod period);
}
