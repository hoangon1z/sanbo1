package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Data
public class ContractCoin {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull(message = "交易对不能为空")
    private String symbol;//交易币种名称，格式：BTC/USDT
    private String name; // 合约名称（如：BTC/USDT永续合约）
    private String coinSymbol;//交易币种符号，如BTC
    private String baseSymbol;//结算币种符号，如USDT

    private int sort;//排序，从小到大
    private int coinScale;//交易币小数精度
    private int baseCoinScale;//基币小数精度

    @Column(columnDefinition = "int(2) default 0 ")
    private int zone = 0; // 交易区 0主区 1创新区

    @Column(columnDefinition = "int(2) default 1 comment '状态'")
    private int enable;//状态：1启用 2禁止

    @Column(columnDefinition = "int(2) default 1 comment '前台可见状态'")
    private int visible;//前台可见状态，1：可见，2：不可见

    @Column(columnDefinition = "int(2) default 1 comment '是否可交易'")
    private int exchangeable;//是否可交易，1：可交易，2：不可交易

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(2) default 1 comment '是否允许开仓做空'")
    private BooleanEnum enableOpenSell = BooleanEnum.IS_TRUE;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(2) default 1 comment '是否允许开仓做多'")
    private BooleanEnum enableOpenBuy = BooleanEnum.IS_TRUE;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(2) default 1 comment '是否启用市价开仓做空'")
    private BooleanEnum enableMarketSell = BooleanEnum.IS_TRUE;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(2) default 1 comment '是否启用市价开仓做多'")
    private BooleanEnum enableMarketBuy = BooleanEnum.IS_TRUE;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(2) default 1 comment '是否启用开仓计划委托'")
    private BooleanEnum enableTriggerEntrust = BooleanEnum.IS_TRUE;

    @Column(columnDefinition = "int(2) default 1 comment '点差类型'")
    private Integer spreadType;// 点差类型 1百分比2固定额
    @Column(columnDefinition = "decimal(8,4) DEFAULT 0 comment '点差'")
    private BigDecimal spread;// 点差(滑点）

    @Column(columnDefinition = "int(2) default 1 comment '点差类型'")
    private int leverageType;// 倍数类型 1: 分离倍数（20,30,50,100），只能选择指定倍数  2: 连续倍数（20,100)，可动态选择带小数倍数
    private String leverage;// 倍数(10,20,30,50,100 / 5,100)

    @Column(columnDefinition = "decimal(8,4) DEFAULT 0 comment '单位手数'")
    private BigDecimal shareNumber;// 合约乘数/一手等于多少个（如 1手 = 10 USD）

    @Column(columnDefinition = "int(11) default 1 comment '最小手数'")
    private int minShare;// 最小手数
    @Column(columnDefinition = "int(11) default 1 comment '最大手数'")
    private int maxShare;// 最大手数

    @Column(columnDefinition = "int(11) default 1 comment '点差类型'")
    private Integer intervalHour;// 资金费用结算时间间隔，小时数
    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.001 comment '隔夜费率'")
    private BigDecimal feePercent;// 资金费率(默认千分之一)

    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.005 comment '维持保证金率'")
    private BigDecimal maintenanceMarginRate;// 维持保证金率（低于该比例将触发强制平仓，默认0.5%）

    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.001 comment '开仓手续费'")
    private BigDecimal openFee;// 开仓手续费(默认千分之一)
    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.001 comment '买入手续费'")
    private BigDecimal takerFee;// 买入手续费（买入平空）(默认千分之一)
    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.001 comment '卖出手续费'")
    private BigDecimal makerFee;// 卖出手续费（卖出平多）(默认千分之一)

    @Column(columnDefinition = "int(11) default 1 comment '是否支持全仓模式'")
    private int crossEnabled;//全仓模式 1支持 2不支持

    @Column(columnDefinition = "int(11) default 1 comment '是否支持逐仓模式'")
    private int fixedEnabled;//逐仓模式，1支持 2不支持

    @Column(columnDefinition = "int(11) default 1 comment '第三方参考价格方向'")
    private int priceDirection;//第三方价格方向（1：基础行情+  2：基础行情-）

    @Column(columnDefinition = "decimal(8,4) DEFAULT 0.001 comment '第三方参考价格比例'")
    private BigDecimal priceRate;//第三方参考价格比例（如设置为0.01，则在第三方价格基础上增加或减少1%）

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '类型'")
    private ContractCoinType type; // 1永续 2秒合约
    /**
     * 服务器当前市价戳
     */
    @Transient
    private Long currentTime;
}
