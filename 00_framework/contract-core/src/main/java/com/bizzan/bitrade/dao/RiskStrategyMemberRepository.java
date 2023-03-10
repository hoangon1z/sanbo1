package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.RiskStrategy;
import com.bizzan.bitrade.entity.RiskStrategyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface RiskStrategyMemberRepository extends JpaRepository<RiskStrategyMember, Long>, JpaSpecificationExecutor<RiskStrategyMember>, QueryDslPredicateExecutor<RiskStrategyMember> {
    void deleteAllByRiskStrategy(RiskStrategy riskStrategy);

    void deleteByMemberIdIn(List<Long> memberIds);

    List<RiskStrategyMember> findAllByRiskStrategy(RiskStrategy riskStrategy);
}
