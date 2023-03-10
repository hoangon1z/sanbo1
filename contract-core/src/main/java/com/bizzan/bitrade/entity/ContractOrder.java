package com.bizzan.bitrade.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
public class ContractOrder implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String contractOrderId; // 合约持仓订单ID

    @Column(columnDefinition = "int(4) default 1 comment '仓位模式'")
    private ContractOrderPattren patterns; // 1逐仓 2全仓

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '方向'")
    private ContractOrderDirection direction; // 1多 2空

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(11) default 1 comment '委托订单类型'")
    private ContractOrderEntrustType entrustType; // 1 OPEN 开仓    2 CLOSE平仓 ｜ 与 direction 配合（）

    private Long memberId; // 用户ID

    private String symbol;//交易对符号
    private String coinSymbol;//币单位
    private String baseSymbol;//结算单位

    private Long entrustId; //委托单ID（主动平仓、强制平仓、止盈止损平仓）

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '开仓价格'")
    private BigDecimal openPrice;// 开仓均价（成交均价）
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '平仓价格'")
    private BigDecimal closePrice;// 平仓均价

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '持仓数量'")
    private BigDecimal position;// 持仓数量

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '可平数量'")
    private BigDecimal availablePosition;// 可平数量（持仓数量 - 委托平仓中的数量

    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.005 comment '维持保证金率'")
    private BigDecimal maintenanceMarginRate;// 维持保证金率（低于该比例将触发强制平仓，默认0.5%）

    @Column(columnDefinition = "int(11) default 1 comment '是否止盈'")
    private int stopProfitType;// 是否止盈 1、不止盈 2、自定义 输入金额
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '止盈价格'")
    private BigDecimal stopProfitPrice;// 止盈价格

    @Column(columnDefinition = "int(11) default 1 comment '是否止损'")
    private int stopLossType;// 是否止损 1、不止损 2、自定义 输入金额
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '止损价格'")
    private BigDecimal stopLossPrice;// 止损价格

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '倍数'")
    private BigDecimal multiple;// 杠杆倍数

    private String principalUnit;// 本金单位（如：USDT）
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '本金数量'")
    private BigDecimal principalAmount;// 本金数量（冻结保证金）

    private String feeSymbol;// 手续费币种单位（默认USDT）
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0.001 comment '交易手续费'")
    private BigDecimal openFee;// 开仓手续费(默认千分之一)
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0.001 comment '交易手续费'")
    private BigDecimal closeFee;// 平仓手续费(默认千分之一)

    @Column(columnDefinition = "int(11) default 1 comment '点差类型'")
    private int intervalHour;// 资金费用结算时间间隔，小时
    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.001 comment '隔夜费百分比'")
    private BigDecimal feePercent;// 资金费百分比
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0.001 comment '隔夜费金额'")
    private BigDecimal totalFee;// 总资金费金额

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '方向'")
    private ContractOrderStatus status;// 1:持仓中/2:用户平仓/3:强制平仓/4:委托持仓中/5:止盈止损平仓/6:跟随持仓中/7:跟随平仓

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '预估强平价'")
    private BigDecimal liquidationPrice; // 预估强平价

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long createTime;//创建时间

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
