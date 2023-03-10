package com.bizzan.bitrade.dao;


import com.bizzan.bitrade.entity.CrowdfundingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface CrowdfundingRecordRepostiory extends JpaRepository<CrowdfundingRecord, String>, JpaSpecificationExecutor<CrowdfundingRecord>, QueryDslPredicateExecutor<CrowdfundingRecord> {
    List<CrowdfundingRecord> getAllByDeletedAndFundingIdAndFundingType(Integer vo, Long fid, Integer fType);

    Page<CrowdfundingRecord> getAllByFundingId(Long fid, Pageable pageable);

    List<CrowdfundingRecord> getAllByFundingId(Long fid);

    Page<CrowdfundingRecord> getAllByMemberIdAndDeleted(Long member, Integer deleted, Pageable pageable);

}
