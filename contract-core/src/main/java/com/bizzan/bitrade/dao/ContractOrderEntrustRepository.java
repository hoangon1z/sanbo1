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

public interface ContractOrderEntrustRepository extends JpaRepository<ContractOrderEntrust, String>, JpaSpecificationExecutor<ContractOrderEntrust>, QueryDslPredicateExecutor<ContractOrderEntrust> {
    @Modifying
    @Query("update ContractOrderEntrust contract set contract.status = :status where contract.contractOrderEntrustId = :contractOrderEntrustId")
    int updateStatus(@Param("contractOrderEntrustId") String contractOrderEntrustId, @Param("status") ContractOrderEntrustStatus status);

    /**
     * 查询委托中的订单
     * @return
     */
    @Query("select a from  ContractOrderEntrust a where a.status = 1")
    List<ContractOrderEntrust> findAllNeedMatch();
}
