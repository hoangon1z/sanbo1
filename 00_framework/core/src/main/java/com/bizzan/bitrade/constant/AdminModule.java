package com.bizzan.bitrade.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GS
 * @date 2017年12月19日
 */
@AllArgsConstructor
@Getter
public enum AdminModule {
    CMS("CMS"),
    COMMON("COMMON"),
    EXCHANGE("EXCHANGE"),
    CONTRACT("CONTRACT"),
    FINANCE("FINANCE"),
    MEMBER("MEMBER"),
    OTC("OTC"),
    SYSTEM("SYSTEM"),
    PROMOTION("PROMOTION"),
    INDEX("INDEX"),
    ACTIVITY("ACTIVITY"),
    MINING("MINING"),
    CTC("CTC"),
    REDENVELOPE("REDENVELOPE"),
    RISK("RISK");

    @Setter
    private String title;
}