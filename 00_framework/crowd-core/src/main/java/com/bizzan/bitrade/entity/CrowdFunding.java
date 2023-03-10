package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

import javax.persistence.*;


/**
 * 众筹心愿和众筹创业（非医疗类） 实体类
 */
@Entity
@Data
public class CrowdFunding {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String Sponsor; // 发起人

    @Column(columnDefinition = "int(2) default 0 comment '状态' comment '状态：0待审核 1已通过 2已拒绝 3已过期'")
    private int status;//状态：0待审核 1已通过 2已拒绝 3已过期

    @Column(nullable = false, columnDefinition = "int(2)  comment '类别： 1心愿 2创业'")
    private int type;

    private Long memberId; // 用户ID

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amountReceived;

    @Column
    private String fundingTitle; //筹款标题

    @Column
    private String instructions; //求助说明

    @Column
    private String picUrl; //图片地址

    @Column(columnDefinition = "int(2)  comment '类别 身份证 2护照'")
    private int documentType;//证件类型证件类型（1.身份证 2护照）


    private String documentNumber; //证件号码


    @Column
    private int times;//次数
    @Column(columnDefinition = "int(2) default 0 comment '删除'")
    private int deleted; //

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column
    private Long addTime; //添加时间

    @Column
    private Long updateTime; //更新时间

    @Column
    private Long passTime; //通过时间

    @Column
    private Long endTime;//结束时间

    @Column(nullable = false, precision = 16, scale = 5)
    private BigDecimal donateRatio;//捐赠返回KB比率

    @Column(nullable = false, precision = 16, scale = 5)
    private BigDecimal cashRatio;//提现费率

    @Column(nullable = false, precision = 16, scale = 5)
    private BigDecimal drawMoney;// //待领取金额


}
