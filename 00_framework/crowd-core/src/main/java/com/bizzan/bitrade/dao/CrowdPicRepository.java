package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.CrowdPic;
import com.bizzan.bitrade.entity.FundingCommon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface CrowdPicRepository extends JpaRepository<CrowdPic, String>, JpaSpecificationExecutor<CrowdPic>, QueryDslPredicateExecutor<CrowdPic> {

    List<CrowdPic> getByFundingIdAndType(Long fundingId, Integer typeId);


}
