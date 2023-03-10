package com.bizzan.bitrade.entity;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "Funding_amount")
public class FundingAmount {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal totalMoney;//总金额

    private Integer totalTimes;//帮助次数

    private Integer totalProject;//加入


}
