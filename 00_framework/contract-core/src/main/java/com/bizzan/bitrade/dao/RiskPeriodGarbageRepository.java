package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.RiskPeriodGarbage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface RiskPeriodGarbageRepository extends JpaRepository<RiskPeriodGarbage, Long>, JpaSpecificationExecutor<RiskPeriodGarbage>, QueryDslPredicateExecutor<RiskPeriodGarbage> {
}
