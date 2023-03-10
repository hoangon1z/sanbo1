package com.bizzan.bitrade.entity;

/**
 * 用户委托单状态
 */
public enum ContractOrderEntrustTriggerType {

    // 用户计划委托类型
    //1:大于/2:小于
    NULL(0, ""),
    BIG_THAN(1, "大于"),
    LESS_THAN(2, "小于");

    ContractOrderEntrustTriggerType(int number, String description) {
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
