package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ContractCoin;
import com.bizzan.bitrade.entity.ContractLeverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ContractLeverageRepository extends JpaRepository<ContractLeverage, Long>, JpaSpecificationExecutor<ContractLeverage>, QueryDslPredicateExecutor<ContractLeverage> {
    ContractLeverage findByAndMemberIdAndSymbol(long memberId, String symbol);
}
