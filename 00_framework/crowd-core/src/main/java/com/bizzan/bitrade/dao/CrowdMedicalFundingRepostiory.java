package com.bizzan.bitrade.dao;


import com.bizzan.bitrade.entity.CrowdMedicalFunding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;


public interface CrowdMedicalFundingRepostiory extends JpaRepository<CrowdMedicalFunding, String>, JpaSpecificationExecutor<CrowdMedicalFunding>, QueryDslPredicateExecutor<CrowdMedicalFunding> {
    CrowdMedicalFunding findById(Long id);

    List<CrowdMedicalFunding> findByStatus(Integer status);

    List<CrowdMedicalFunding> findByStatusInOrderByAddTimeDesc(Integer[] status);

    Page<CrowdMedicalFunding> findByMemberIdAndDeleted(Long memberId, Integer deleted, Pageable pageable);

}
