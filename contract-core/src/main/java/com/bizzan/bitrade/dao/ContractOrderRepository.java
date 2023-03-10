package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.ContractOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractOrderRepository extends JpaRepository<ContractOrder, String>, JpaSpecificationExecutor<ContractOrder>, QueryDslPredicateExecutor<ContractOrder> {
    @Modifying
    @Query("update ContractOrder contract set contract.status = :status where contract.contractOrderId = :contractOrderId")
    int updateStatus(@Param("contractOrderId") String contractOrderId, @Param("status") ContractOrderStatus status);

    /**
     * 查询所有需要监控的委托单
     * @return
     */
    @Query("select a from  ContractOrder a where a.status = 1 or a.status = 4")
    List<ContractOrder> findAllNeedMatch();
}
