package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.util.SqlHelp;
import com.bizzan.bitrade.vo.MemberStrategyProfitQuery;
import com.bizzan.bitrade.vo.MemberStrategyProfitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ContractMemberProfitAndStrategyImpl {

    @Autowired
    private SqlHelp<MemberStrategyProfitVO> sqlHelp;

    public Page<MemberStrategyProfitVO> getMembers(MemberStrategyProfitQuery criteria, Pageable pageable) {
        String sql = "SELECT\n" +
                "\tmember.id as member_id,\n" +
                "\tmember.mobile_phone,\n" +
                "\tmember.real_name,\n" +
                "  contract_member_profit.always_profit,\n" +
                "  contract_member_profit.second_profit,\n" +
                "  risk_strategy.id as risk_strategy_id,\n" +
                "\trisk_strategy.name as risk_strategy_name\n" +
                "FROM\n" +
                "\tmember\n" +
                "LEFT JOIN risk_strategy_member ON risk_strategy_member.member_id = member.id\n" +
                "LEFT JOIN risk_strategy ON risk_strategy_id = risk_strategy.id\n" +
                "LEFT JOIN contract_member_profit on contract_member_profit.member_id = member.id\n" +
                "ORDER BY (always_profit+second_profit) desc";
        return sqlHelp.getBySql(criteria, MemberStrategyProfitVO.class, sql, pageable);
    }
}
