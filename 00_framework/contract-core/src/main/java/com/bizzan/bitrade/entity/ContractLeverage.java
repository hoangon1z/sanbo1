package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"memberId", "symbol"})})
public class ContractLeverage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long memberId;

    private String symbol;

    @Column(columnDefinition = "decimal(18,8) DEFAULT 10 comment '倍数'")
    private BigDecimal leverage;// 杠杆倍数

}
