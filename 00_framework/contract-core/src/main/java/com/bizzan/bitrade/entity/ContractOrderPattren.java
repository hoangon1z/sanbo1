package com.bizzan.bitrade.entity;

public enum ContractOrderPattren {
    NULL(0, ""),
    CROSSED(1, "全仓"),
    FIXED(2, "逐仓");

    ContractOrderPattren(int number, String description) {
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
