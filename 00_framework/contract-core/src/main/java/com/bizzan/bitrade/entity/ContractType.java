package com.bizzan.bitrade.entity;

public enum ContractType {
    NULL(0, ""),
    OPENUP(1, "买入开多"),
    OPENDOWN(2, "卖出开空"),
    CLOSEUP(3, "买入平空"),
    CLOSEDOWN(4, "卖出平多");

    ContractType(int number, String description) {
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
