package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ContractCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractCoinRepository extends JpaRepository<ContractCoin, String>, JpaSpecificationExecutor<ContractCoin>, QueryDslPredicateExecutor<ContractCoin> {
    ContractCoin findBySymbol(String symbol);

    @Query("select distinct a.baseSymbol from  ContractCoin a where a.enable = 1")
    List<String> findBaseSymbol();

    @Query("select distinct a.coinSymbol from  ContractCoin a where a.enable = 1 and a.baseSymbol = :baseSymbol")
    List<String> findCoinSymbol(@Param("baseSymbol")String baseSymbol);

    @Query("select distinct a.coinSymbol from  ContractCoin a where a.enable = 1")
    List<String> findAllCoinSymbol();
}
