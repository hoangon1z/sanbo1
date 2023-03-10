package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class ContractMemberProfit {
    @Id
    private Long memberId;

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '秒合约收益'")
    private BigDecimal secondProfit;

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '永续合约收益'")
    private BigDecimal alwaysProfit;
}
