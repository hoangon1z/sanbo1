package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ContractLeverage;
import com.bizzan.bitrade.entity.ContractMemberProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ContractMemberProfitRepository extends JpaRepository<ContractMemberProfit, Long>, JpaSpecificationExecutor<ContractMemberProfit>, QueryDslPredicateExecutor<ContractMemberProfit> {
    ContractMemberProfit findByMemberId(Long memberId);
}