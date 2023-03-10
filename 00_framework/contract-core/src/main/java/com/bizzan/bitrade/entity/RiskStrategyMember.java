package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class RiskStrategyMember implements Serializable {

    @Id
    private Long memberId;

    private String memberName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_strategy_id", nullable = false)
    @JsonIgnore
    private RiskStrategy riskStrategy;

    @Override
    public String toString() {
        return "RiskStrategyMember{" +
                "memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                '}';
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public RiskStrategy getRiskStrategy() {
        return riskStrategy;
    }

    public void setRiskStrategy(RiskStrategy riskStrategy) {
        this.riskStrategy = riskStrategy;
    }
}
