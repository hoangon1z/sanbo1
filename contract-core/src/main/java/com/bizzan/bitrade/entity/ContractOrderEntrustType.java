package com.bizzan.bitrade.entity;

public enum ContractOrderEntrustType {
    OPEN(1, "开仓"),
    CLOSE(2, "平仓");

    ContractOrderEntrustType(int number, String description) {
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
