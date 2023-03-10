package com.bizzan.bitrade.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MiningOrder;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MiningOrderDao extends BaseDao<MiningOrder> {

    List<MiningOrder> findAllByMemberId(Long memberId);

    List<MiningOrder> findAllByMemberIdAndActivityId(Long memberId, Long activityId);

    List<MiningOrder> findAllByActivityId(Long activityId);

    List<MiningOrder> findAllByMiningStatus(int miningStatus);

//	/**
//	 * 查询所有变动算力的矿机对应的会员ID
//	 * @return
//	 */
//	@Query("select distinct a.memberId from  MiningOrder a where a.teamCompute = false")
//	List<Long> findMemberIdNeedCompute();

//	@Query("select sum(a.miningPower) from  MiningOrder a where a.memberId = :memberId and a.miningTimes > a.miningedTimes")
//	BigDecimal findSumPowerByMemberId(@Param("memberId") Long memberId);

//	/**
//	 * 统计完个人算力
//	 * @return
//	 */
//	@Modifying
//	@Transactional(rollbackFor = Exception.class)
//	@Query("update MiningOrder set teamCompute = true where memberId = :memberId")
//	void finishComputeByMemberId(@Param("memberId") Long memberId);
}
