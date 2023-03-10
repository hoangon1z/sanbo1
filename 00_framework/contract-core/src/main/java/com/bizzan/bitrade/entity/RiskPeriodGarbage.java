package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Setter
public class RiskPeriodGarbage implements Serializable {

    public RiskPeriodGarbage() {
    }

    public RiskPeriodGarbage(RiskPeriod period) {
        this.start = period.getStart();
        this.end = period.getEnd();
        this.openPrice = period.getOpenPrice();
        this.share = period.getShare();
        this.longShare = period.getLongShare();
        this.shortShare = period.getShortShare();
        this.riskPrice = period.getRiskPrice();
        this.strategyId = period.getStrategyId();
        this.strategyName = period.getStrategyName();
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long start; //开始时间
    private Long end; //结束时间
    private BigDecimal openPrice = BigDecimal.ZERO; // 开仓均价
    private Integer share; // 手数
    private Integer longShare = 0; // 开多仓手数
    private Integer shortShare = 0; // 开空仓手数
    private BigDecimal riskPrice; // 风控价格
    private Long strategyId;
    private String strategyName;

    private String reason; // 未启动原因

    private Boolean isOn; //是否启动
}
