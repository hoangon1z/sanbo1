package com.bizzan.bitrade.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.Comparator;

@Data
public class RiskSetting {

    private BigDecimal profit; //当前盈利

    private BigDecimal loss; //当前亏损

    private BigDecimal spotRate; // 整体盈亏比例

    private BigDecimal maxLossRate; // 最大亏损比例

    private BigDecimal maxProfitRate; // 最大盈利比例

}
