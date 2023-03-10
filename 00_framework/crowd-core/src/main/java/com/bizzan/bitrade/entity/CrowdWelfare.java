package com.bizzan.bitrade.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * 这是线下公益的实体类
 */
@Entity
@Data
public class CrowdWelfare {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String Sponsor; // 发起人

    @Column(columnDefinition = "int(2) default 0 comment '状态'")
    private int status;//状态：0待审核 1已通过 2已拒绝

    private Long memberId; // 用户ID

    @Column(precision = 16, scale = 2)
    private BigDecimal applyAmount;//申请金额
    @Column(precision = 16, scale = 2)
    private BigDecimal amountReceived;//收到金额


    @Column
    private String fundingTitle; //公益标题

    @Column
    private String instructions; //说明

    @Column
    private String picUrl; //图片地址

    @Column(columnDefinition = "int(2)  comment '类别 身份证 2护照'")
    private int documentType;//证件类型证件类型（1.身份证 2护照）

    @Column
    private String documentNumber; //证件号码


    @Column
    private String refuceReason;//拒绝理由

    @Column(columnDefinition = "int(2) default 0 comment '删除'")
    private int deleted; //
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column
    private Long addTime; //添加时间
    @Column
    private Long updateTime; //更新时间
    @Column
    private Long passTime; //通过时间

}
