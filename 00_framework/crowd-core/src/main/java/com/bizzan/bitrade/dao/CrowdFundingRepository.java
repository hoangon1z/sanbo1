package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.CrowdFunding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface CrowdFundingRepository extends JpaRepository<CrowdFunding, String>, JpaSpecificationExecutor<CrowdFunding>, QueryDslPredicateExecutor<CrowdFunding> {

    CrowdFunding getAllByDeletedAndIdAndType(Integer vo, Long fid, Integer fType);

    //CrowdFunding findByName(String name);
    CrowdFunding findById(Long id);

    List<CrowdFunding> findByStatus(Integer status);

    List<CrowdFunding> findByStatusInOrderByAddTimeDesc(Integer[] status);

    Page<CrowdFunding> findByMemberIdAndDeleted(Long mid, Integer deleted, Pageable pageable);
}
