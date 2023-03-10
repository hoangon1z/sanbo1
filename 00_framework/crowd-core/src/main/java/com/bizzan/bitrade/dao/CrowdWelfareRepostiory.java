package com.bizzan.bitrade.dao;


import com.bizzan.bitrade.entity.CrowdWelfare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface CrowdWelfareRepostiory extends JpaRepository<CrowdWelfare, String>, JpaSpecificationExecutor<CrowdWelfare>, QueryDslPredicateExecutor<CrowdWelfare> {
    CrowdWelfare findById(Long id);

    Page<CrowdWelfare> findByMemberIdAndDeleted(Long memberId, Integer deleted, Pageable pageable);

}
