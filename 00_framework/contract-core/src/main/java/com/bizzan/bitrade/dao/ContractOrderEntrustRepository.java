package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.ContractOrderEntrust;
import com.bizzan.bitrade.entity.ContractOrderEntrustStatus;
import com.bizzan.bitrade.entity.ContractOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractOrderEntrustRepository extends JpaRepository<ContractOrderEntrust, Long>, JpaSpecificationExecutor<ContractOrderEntrust>, QueryDslPredicateExecutor<ContractOrderEntrust> {
    @Modifying
    @Query("update ContractOrderEntrust contract set contract.status = :status where contract.contractOrderEntrustId = :contractOrderEntrustId")
    int updateStatus(@Param("contractOrderEntrustId") String contractOrderEntrustId, @Param("status") ContractOrderEntrustStatus status);

    /**
     * 查询委托中的订单
     *
     * @return
     */
    @Query("select a from  ContractOrderEntrust a where a.status = 1")
    List<ContractOrderEntrust> findAllNeedMatch();

    /**
     * 查询委托中的订单
     *
     * @return
     */
    @Query("select a from  ContractOrderEntrust a where a.status = 1 and a.memberId = :memberId")
    List<ContractOrderEntrust> findAllNeedMatch(@Param("memberId") Long memberId);

    /**
     * 查询委托中的订单
     *
     * @return
     */
    @Query("select a from  ContractOrderEntrust a where a.status = 1 and a.memberId = :memberId and a.symbol=:symbol")
    List<ContractOrderEntrust> findAllNeedMatch(@Param("memberId") Long memberId, @Param("symbol") String symbol);

    /**
     * 查询某一币种委托中的订单
     *
     * @return
     */
    @Query("select a from  ContractOrderEntrust a where a.status = 1 and a.symbol=:symbol")
    List<ContractOrderEntrust> findAllNeedMatchBySymbol(@Param("symbol") String symbol);

    List<ContractOrderEntrust> findAllBySymbolAndStatus(String symbol, ContractOrderEntrustStatus status);

    ContractOrderEntrust findByContractOrderEntrustId(String eid);
}
