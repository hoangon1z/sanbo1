package com.bizzan.bitrade.vo;

import com.bizzan.bitrade.entity.ContractOrderDirection;
import com.bizzan.bitrade.entity.ContractOrderEntrustType;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.beans.ConstructorProperties;
import java.math.BigDecimal;

@Data
//@AllArgsConstructor
public class PositionVO {

    public PositionVO(String symbol, ContractOrderDirection direction) {
        this.symbol = symbol;
        this.direction = direction;
    }

    private String symbol;//交易对符号

    private String coinSymbol;

    @Enumerated(EnumType.ORDINAL)
    private ContractOrderDirection direction; // 1多 2空

    private BigDecimal multiple = BigDecimal.ZERO;// 杠杆倍数

    private BigDecimal avgOpenPrice = BigDecimal.ZERO; // 开仓均价

    private long positions = (long) 0; // 手数

    private long availablePositions = (long) 0; // 可平数量

    private BigDecimal principalAmount = BigDecimal.ZERO; // 本金数量

}
