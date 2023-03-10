package com.bizzan.bitrade.entity;


import lombok.Data;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * 医疗众筹 实体类
 */
@Entity
@Data
public class CrowdMedicalFunding {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String Sponsor; // 发起人

    @Column(columnDefinition = "int(2) default 0  comment '状态：0待审核 1已通过 2已拒绝 3已过期'")
    private int status;//状态：0待审核 1已通过 2已拒绝
    @Column(nullable = false, columnDefinition = "int(2) default 3 comment '这里为了在获取详情时区分  所有把3写死'")
    private int type;//这里为了在获取详情时区分  所有把3写死

    private Long memberId; // 用户ID

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal targetAmount;//目标金额

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amountReceived;//已暮金额

    @Column
    private String fundingTitle; //筹款标题


    @Column
    private String fundingUse; //用途

    @Column
    private String plan; //计划

    @Column
    private String story;//故事

    @Column
    private String promise;//承诺

    @Column
    private String patientName;//患者姓名

    @Column
    private int patientDocumentType;//患者证件类型（1.身份证 2护照）

    @Column
    private String patientDocumentNumber; //患者证件号码
    @Column
    private String picUrl; //图片地址

    @Column(columnDefinition = "int(2)  comment '类别 1 身份证 2护照'")
    private int documentType;//证件类型（1.身份证 2护照）

    @Column
    private String documentNumber; //证件号码


    @Column
    private String visitInformation; //就诊信息

    @Column
    private int times;//次数

    @Column(columnDefinition = "int(2) default 0 comment '删除'")
    private int deleted; //

    @Column
    private Long addTime; //添加时间

    @Column
    private Long updateTime; //更新时间
    @Column
    private Long passTime; //通过时间

    @Column
    private Long endTime;//结束时间

    @Column
    private String houseProperty; //房产情况

    @Column
    private String carProperty; //车产情况

    @Column(precision = 16, scale = 2)
    private BigDecimal income;//家庭收入情况

    @Column(precision = 16, scale = 2)
    private BigDecimal property;//资产

    @Column(precision = 16, scale = 2)
    private BigDecimal debt;//债务

    @Column
    private int otherFundType;//是否在其他平台发起筹款 0没有 1有

    @Column(nullable = false, precision = 16, scale = 5)
    private BigDecimal donateRatio;//捐赠返kb

    @Column(nullable = false, precision = 16, scale = 5)
    private BigDecimal cashRatio;//提现费率

    @Column(nullable = false, precision = 16, scale = 5)
    private BigDecimal drawMoney;// //待领取金额


}
