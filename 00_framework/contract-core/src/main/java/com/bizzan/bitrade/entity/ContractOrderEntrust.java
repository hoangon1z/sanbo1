package com.bizzan.bitrade.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
public class ContractOrderEntrust implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String contractOrderEntrustId; // 合约委托订单ID

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '类型'")
    private ContractCoinType contractCoinType; // 1永续 2秒合约

    private Long contractCoinId; //合约ID

    private String contractCoinName; //合约名称

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(11) default 1 comment '仓位模式'")
    private ContractOrderPattren patterns; // 1逐仓 2全仓

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(11) default 1 comment '方向'")
    private ContractOrderDirection direction; // 1买 2卖

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(11) default 1 comment '委托订单类型'")
    private ContractOrderEntrustType entrustType; // 1 开仓 2 平仓 ｜ 与 direction 配合（）

    @Enumerated(EnumType.ORDINAL)
    private ContractOrderType type; //委托类型 2限价 3计划委托

    @Column(columnDefinition = "tinyint(1) default 0 comment '对手价 1是 0否'")
    private Boolean marketPrice; // 对手价

    private Long memberId; // 用户ID

    private String symbol;//交易对符号
    private String coinSymbol;//币单位
    private String baseSymbol;//结算单位

    private BigDecimal triggerPrice;// 触发价(委托方式是计划/限价必填)
    private BigDecimal entrustPrice;// 委托价(委托方式是计划/限价必填)

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '倍数'")
    private BigDecimal leverage;// 杠杆倍数

    private String principalUnit;// 本金单位（如：USDT）
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '本金数量'")
    private BigDecimal principalAmount;// 本金数量（保证金）

    @Column(columnDefinition = "int(11) default 1 comment '委托张数'")
    private Integer share;// 手数

    @Column(columnDefinition = "decimal(8,4) DEFAULT 0 comment '单位手数'")
    private BigDecimal shareNumber;// 合约乘数/一手等于多少个（如 1手 = 10 USD）

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '成交价'")
    private BigDecimal strikePrice;// 成交价

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long strikeTime;//成交时间
    //    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '平仓价格'")
//    private BigDecimal closePrice;// 平仓价格（平仓时）
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '当前价格'")
    private BigDecimal currentPrice;// 当前价格

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '收益'")
    private BigDecimal profit;// 收益

    @Column(columnDefinition = "int(11) default 1 comment '是否止盈'")
    private int stopProfitType;// 是否止盈 1、不止盈 2、自定义 输入金额
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '止盈价格'")
    private BigDecimal stopProfitPrice;// 止盈价格

    @Column(columnDefinition = "int(11) default 1 comment '是否止损'")
    private int stopLossType;// 是否止损 1、不止损 2、自定义 输入金额
    @Column(columnDefinition = "decimal(18,8) comment '止损价格'")
    private BigDecimal stopLossPrice;// 止损价格

    private String feeSymbol;// 手续费币种单位（默认USDT）
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '手续费'")
    private BigDecimal fee;// 手续费

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '委托订单状态'")
    private ContractOrderEntrustStatus status;// 1:委托中/2:已撤销/3:委托失败/4:委托成功

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '计划委托方向'")// 1:大于/2:小于
    private ContractOrderEntrustTriggerType triggerType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long createTime;//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long triggeringTime;//触发时间

    private Integer holdTime; //时间，秒合约专用

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
