package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Comparator;

@Data
public class RiskPeriod {
    private Long start; //开始时间
    private Long end; //结束时间
    private BigDecimal openPrice = BigDecimal.ZERO; // 开仓均价
    private Integer share; // 手数
    @Transient
    private BigDecimal shareNumber; // 合约单价
    private Integer longShare = 0; // 开多仓手数
    private Integer shortShare = 0; // 开空仓手数
    private BigDecimal riskPrice; // 风控价格
    private Long strategyId;
    private String strategyName;
    @Transient
    @JsonIgnore
    private RiskStrategy strategy;


    /**
     * 策略竞选
     *
     * @param strategy
     * @param share
     */
    public synchronized void setElectionStrategy(RiskStrategy strategy, Integer share) {
        if (this.strategy == null) {
            setStrategy(strategy);
        } else if (BigDecimal.valueOf(share).abs().compareTo(BigDecimal.valueOf(this.share).abs()) > 0) { // 谁手数多用谁的策略
            setStrategy(strategy);
        }
    }

    public void setStrategy(RiskStrategy strategy) {
        this.strategy = strategy;
        this.strategyId = strategy.getId();
        this.strategyName = strategy.getName();
    }

    public static class Compare implements Comparator<RiskPeriod> {
        @Override
        public int compare(RiskPeriod o1, RiskPeriod o2) {
            if (o1.getStart() > o2.getStart()) {
                return 1;
            } else if (o1.getStart() < o2.getStart()) {
                return -1;
            } else {
                return 0;
            }
        }
    }


}
