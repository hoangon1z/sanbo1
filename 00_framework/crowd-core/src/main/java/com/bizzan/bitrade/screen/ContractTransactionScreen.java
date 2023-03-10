package com.bizzan.bitrade.screen;

import com.bizzan.bitrade.constant.ContractTransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractTransactionScreen {


    private Long id;
    private Long memberId;

    /**
     * 交易时间搜索
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-mm-dd hh:mm:ss")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-mm-dd hh:mm:ss")
    private Date endTime;

    /**
     * 交易金额搜索
     */
    private BigDecimal minMoney;
    private BigDecimal maxMoney;

    /**
     * 交易金额搜索
     */
    private BigDecimal amount;

    /**
     * 交易类型搜索
     */
    private ContractTransactionType type;

}
