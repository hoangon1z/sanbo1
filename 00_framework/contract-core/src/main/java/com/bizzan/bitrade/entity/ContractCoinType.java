package com.bizzan.bitrade.entity;

public enum ContractCoinType {
    NULL(0, ""),
    ALWAYS(1, "永续合约"),
    SECOND(2, "秒合约");

    ContractCoinType(int number, String description) {
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
