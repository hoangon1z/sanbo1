package com.bizzan.bitrade.vo;

import com.bizzan.bitrade.annotation.NativeQuery;
import com.bizzan.bitrade.annotation.Query;
import lombok.Data;

@Data
public class MemberStrategyProfitQuery implements NativeQuery {

    @Query(nativeName = "member.mobile_phone", type = Query.Type.INNER_LIKE)
    private String mobilePhone;

    @Query(nativeName = "risk_strategy.id", type = Query.Type.EQUAL)
    private Long riskStrategyId;

}