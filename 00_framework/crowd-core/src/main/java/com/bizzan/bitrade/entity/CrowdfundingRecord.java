package com.bizzan.bitrade.entity;


import lombok.Data;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * 捐助详情实体类
 */

@Entity
@Data
public class CrowdfundingRecord {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(columnDefinition = "int(11)  comment '用户id'")
    private Long memberId;

    @Column(columnDefinition = "varchar(255)  comment '用户名'")
    private String memberName;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal fundingMoney;

    @Column(nullable = false, columnDefinition = "dateTime  comment '捐助时间'")
    private Long fundingTime;

    @Column(columnDefinition = "tinyint(1)  comment '删除'")
    private int deleted;

    @Column
    private String fundingTitle;

    @Column
    private Long fundingId;

    @Column
    private int fundingType;


}
