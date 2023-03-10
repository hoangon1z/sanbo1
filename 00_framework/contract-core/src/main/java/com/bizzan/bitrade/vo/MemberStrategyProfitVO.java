package com.bizzan.bitrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class MemberStrategyProfitVO {

    private BigInteger memberId;

    private String mobilePhone;

    private String realName;

    private BigDecimal alwaysProfit;

    private BigDecimal secondProfit;

    private BigInteger riskStrategyId;

    private String riskStrategyName;

    public MemberStrategyProfitVO(BigInteger memberId, String mobilePhone, String realName, BigDecimal alwaysProfit, BigDecimal secondProfit, BigInteger riskStrategyId, String riskStrategyName) {
        this.memberId = memberId;
        this.mobilePhone = mobilePhone;
        this.realName = realName;
        this.alwaysProfit = alwaysProfit;
        this.secondProfit = secondProfit;
        this.riskStrategyId = riskStrategyId;
        this.riskStrategyName = riskStrategyName;
    }
}
