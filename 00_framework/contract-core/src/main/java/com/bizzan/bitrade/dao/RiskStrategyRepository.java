package com.bizzan.bitrade.dao;

import org.springframework.data.jpa.repository.Query;
import com.bizzan.bitrade.entity.RiskStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface RiskStrategyRepository extends JpaRepository<RiskStrategy, Long>, JpaSpecificationExecutor<RiskStrategy>, QueryDslPredicateExecutor<RiskStrategy> {
    RiskStrategy findByIsDefault(Boolean de);

    RiskStrategy findByIsDefaultAndStatus(Boolean de, Integer status);

    @Query(value = "select s.* from risk_strategy s INNER JOIN risk_strategy_member m on m.risk_strategy_id = s.id where member_id = :memberId", nativeQuery = true)
    RiskStrategy findByMemberId(@Param("memberId") Long memberId);
}
