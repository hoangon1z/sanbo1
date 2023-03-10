package com.bizzan.bitrade.dao;

import java.math.BigDecimal;
import java.util.List;

import com.bizzan.bitrade.entity.ActivityDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Activity;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.MemberTransaction;

@Repository
public interface ActivityDao extends BaseDao<Activity> {

    List<Activity> findAllByStep(int step);

    @Query(value = "select discount from activity_discount where activity_id = :id and member_level_id = :levelId limit 0,1", nativeQuery = true)
    BigDecimal findDiscountByLevel(@Param("id") Long id, @Param("levelId") Long levelId);
}
