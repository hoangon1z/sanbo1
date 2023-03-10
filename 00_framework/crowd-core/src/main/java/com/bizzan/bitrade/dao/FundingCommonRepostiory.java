package com.bizzan.bitrade.dao;


import com.bizzan.bitrade.entity.FundingCommon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface FundingCommonRepostiory extends JpaRepository<FundingCommon, String>, JpaSpecificationExecutor<FundingCommon>, QueryDslPredicateExecutor<FundingCommon> {
    List<FundingCommon> getAllByDeletedAndFundingIdAndFundingTypeAndStatus(Integer vo, Long fid, Integer fType, Integer status);

    FundingCommon getById(Long id);


}
