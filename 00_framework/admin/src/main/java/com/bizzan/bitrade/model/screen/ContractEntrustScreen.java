package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.*;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
public class ContractEntrustScreen {
    ContractOrderType type;
    String symbol;//交易对
    String coinSymbol;//币单位
    String baseSymbol;//结算单位
    ContractType entrustType;
    ContractOrderEntrustStatus status;//1:委托中/2:已撤销/3:委托失败/4:委托成功
    Long memberId;
    //成交价
    BigDecimal minPrice;
    BigDecimal maxPrice;
    //成交量
    BigDecimal minAmount;
    BigDecimal maxAmount;
    //收益
    BigDecimal minProfit;
    BigDecimal maxProfit;
    String contractOrderEntrustId;
    /**
     * 01（委托订单  历史订单）
     */
    BooleanEnum completed;
}
