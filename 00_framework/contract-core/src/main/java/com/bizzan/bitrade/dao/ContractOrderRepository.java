package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.ContractOrderDirection;
import com.bizzan.bitrade.entity.ContractOrderStatus;
import com.bizzan.bitrade.vo.PositionVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractOrderRepository extends JpaRepository<ContractOrder, Long>, JpaSpecificationExecutor<ContractOrder>, QueryDslPredicateExecutor<ContractOrder> {
    @Modifying
    @Query("update ContractOrder contract set contract.status = :status where contract.contractOrderId = :contractOrderId")
    int updateStatus(@Param("contractOrderId") String contractOrderId, @Param("status") ContractOrderStatus status);

    /**
     * 查询所有需要监控的委托单
     *
     * @return
     */
    @Query("select a from  ContractOrder a where a.status = 1 or a.status = 4")
    List<ContractOrder> findAllNeedMatch();

    /**
     * 查询所有需要监控的委托单
     *
     * @return
     */
    @Query("select a from  ContractOrder a where (a.status = 1 or a.status = 4) and a.memberId = :memberId")
    List<ContractOrder> findAllNeedMatch(@Param("memberId") Long memberId);

    /**
     * 查询所有需要监控的委托单
     *
     * @return
     */
    @Query("select a from  ContractOrder a where (a.status = 1 or a.status = 4) and a.memberId = :memberId and a.symbol = :symbol ")
    List<ContractOrder> findAllNeedMatch(@Param("memberId") Long memberId, @Param("symbol") String symbol);

    /**
     * 查询所有需要监控的委托单
     *
     * @return
     */
    @Query("select a from  ContractOrder a where a.symbol = :symbol and (a.status = 1 or a.status = 4)")
    List<ContractOrder> findAllNeedMatchBySymbol(@Param("symbol") String symbol);


//    @Query("SELECT new com.bizzan.bitrade.vo.PositionVO(c.symbol, c.coinSymbol, c.direction, c.multiple, sum(c.position/c.openPrice), sum(c.position), sum(c.availablePosition), sum(c.principalAmount)) FROM ContractOrder c WHERE c.memberId = :memberId AND c.symbol = :symbol GROUP BY c.direction")
//    List<PositionVO> findCurrent(@Param("memberId") long memberId, @Param("symbol") String symbol);


    List<ContractOrder> findAllByMemberIdAndContractCoinIdAndStatus(long memberId, long contractCoinId, ContractOrderStatus status);

    ContractOrder findByMemberIdAndContractCoinIdAndStatusAndDirection(long memberId, long contractCoinId, ContractOrderStatus status, ContractOrderDirection direction);
}
