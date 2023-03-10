package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.entity.ContractOrderDirection;
import com.bizzan.bitrade.entity.ContractOrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractCoinScreen {
    //交易币种符号
    private String coinSymbol;

    //结算币种符号，如USDT
    private String baseSymbol;
}
