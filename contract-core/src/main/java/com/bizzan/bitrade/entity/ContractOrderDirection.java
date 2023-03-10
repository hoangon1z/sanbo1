package com.bizzan.bitrade.entity;

public enum ContractOrderDirection {
    BUY(1, "做多"),
    SELL(2, "做空");

    ContractOrderDirection(int number, String description) {
        this.code = number;
        this.description = description;
    }
    private int code;
    private String description;
    public int getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}
