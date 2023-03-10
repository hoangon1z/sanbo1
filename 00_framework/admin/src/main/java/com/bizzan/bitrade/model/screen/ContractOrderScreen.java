package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractOrderScreen {
    String symbol;//交易对
    String coinSymbol;//币单位
    String baseSymbol;//结算单位
    ContractOrderDirection dirt;
    ContractOrderStatus status; //1:持仓中/2:用户平仓/3:强制平仓/4:委托持仓中/5:止盈止损平仓/6:跟随持仓中/7:跟随平仓
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
    String contractOrderId;
}
