package com.bizzan.bitrade.model.screen;

import lombok.Data;

@Data
public class FundingScreen {

    Long id;
    Long memberId;
    Integer status;//0待审核 1审核通过 2审核未通过
    String fundingTitle;
}
