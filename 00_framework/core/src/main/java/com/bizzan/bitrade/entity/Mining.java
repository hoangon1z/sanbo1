package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table
public class Mining {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 矿机名称
     */
    @NotNull
    @Column
    private String miningName;
    /**
     * 矿机简介
     */
    @Column
    private String detail;


    /**
     * 启用禁用（决定是否显示在前端）
     */
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private BooleanEnum status = BooleanEnum.IS_FALSE;


    /**
     * 要求一级好友不能低于该数（0：不限制，>0:限制）
     */
    @NotNull
    private int leveloneCount = 0;

    /**
     * 发行价格
     */
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 ")
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * 价格精度
     */
    private int priceScale = 0;


    /**
     * 接受币种（如BTC、ETH等
     */
    @NotNull
    private String acceptUnit;

    /**
     * 数量精度
     */
    private int amountScale = 0;

    /**
     * 单人限购总量上限（0：不限制）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal maxLimitAmout = BigDecimal.ZERO;

    /**
     * 单人限购总量下限（0：不限制）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal minLimitAmout = BigDecimal.ZERO;


    /**
     * 限制购买次数（0：不限制）
     */
    private int limitTimes = 0;


    /**
     * 挖矿产出周期（0：日，1：周，2：月，3：年）
     */
    private int miningPeriod = 0;

    /**
     * 云矿机：挖矿持续周期数（多少个周期挖一次）
     * 参数命名与实际意义不同，请注意
     */
    private int miningDays = 0;

    /**
     * 挖矿周期次数（挖矿次数）
     */
    private int miningTimes;

    /**
     * 云矿机：每天/周/月产出多少（每次产出多少）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal miningDaysprofit = BigDecimal.ZERO;

    /**
     * 云矿机：云矿机所挖币种
     */
    @NotNull
    private String miningUnit;

    /**
     * 邀请好友（且购买矿机）产能增加百分比（为0则不增加）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal miningInvite = BigDecimal.ZERO;

    /**
     * 产能增加上限百分比（为0则无上限）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal miningInvitelimit = BigDecimal.ZERO;

    /**
     * 创建时间（用于排序使用）
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 图片地址
     */
    private String smallImageUrl;

    /**
     * banner地址
     */
    private String bannerImageUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 增加算力
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal power = BigDecimal.ZERO;

    /**
     * 手续费
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal fee = BigDecimal.ZERO;
}
