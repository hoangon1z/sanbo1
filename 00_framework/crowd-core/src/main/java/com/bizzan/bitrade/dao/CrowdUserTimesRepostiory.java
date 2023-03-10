package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.CrowdUserTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface CrowdUserTimesRepostiory extends JpaRepository<CrowdUserTimes, String>, JpaSpecificationExecutor<CrowdUserTimes>, QueryDslPredicateExecutor<CrowdUserTimes> {
    CrowdUserTimes findByMemberIdAndLevelIdAndType(Long memberId, Long levelId, Integer typeId);


}
