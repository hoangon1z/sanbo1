package com.bizzan.bitrade.constant;

public enum ContractTransactionType {
    IN(0, "转入"),
    OUT(1, "转出"),
    CONUT(2, "结算"),
    CLOSE(3, "强平");

    ContractTransactionType(int number, String description) {
        this.code = number;
        this.description = description;
    }

    public static ContractTransactionType valueOfOrdinal(int ordinal) {
        switch (ordinal) {
            case 0:
                return IN;
            case 1:
                return OUT;
            case 2:
                return CONUT;
            case 3:
                return CLOSE;
            default:
                return null;
        }
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
