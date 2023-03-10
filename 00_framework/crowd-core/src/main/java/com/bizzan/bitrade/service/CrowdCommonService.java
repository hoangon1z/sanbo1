package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.FundingCommonRepostiory;
import com.bizzan.bitrade.entity.FundingCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class CrowdCommonService {

    @Autowired
    private FundingCommonRepostiory commonRepostiory;

    //新增评论
    public FundingCommon addOne(FundingCommon fundingCommon) {
        fundingCommon.setCommonTime(Calendar.getInstance().getTimeInMillis());
        fundingCommon.setDeleted(0);
        return commonRepostiory.save(fundingCommon);
    }


}
