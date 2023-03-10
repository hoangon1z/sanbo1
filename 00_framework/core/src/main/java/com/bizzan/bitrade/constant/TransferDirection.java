package com.bizzan.bitrade.constant;

public enum TransferDirection {
    no(0, "无"),
    contract(1, "转入合约账户"),
    exchange(2, "转入币币账户");

    TransferDirection(int number, String description) {
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