package com.bizzan.bitrade.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractCoinThumb extends CoinThumb {
    // 为返回多种合约用
    private Long id;
    // 为返回多种合约用
    private String name;
    //合约类型
    private int type;

    public ContractCoinThumb(Long id, String name, int type, CoinThumb thumb) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.setSymbol(thumb.getSymbol());
        this.setOpen(thumb.getOpen());
        this.setHigh(thumb.getHigh());
        this.setLow(thumb.getLow());
        this.setClose(thumb.getClose());
        this.setChg(thumb.getChg());
        this.setChange(thumb.getChange());
        this.setVolume(thumb.getVolume());
        this.setTurnover(thumb.getTurnover());
        this.setLastDayClose(thumb.getLastDayClose());
        this.setUsdRate(thumb.getUsdRate());
        this.setBaseUsdRate(thumb.getBaseUsdRate());
        this.setZone(thumb.getZone());
    }
}
