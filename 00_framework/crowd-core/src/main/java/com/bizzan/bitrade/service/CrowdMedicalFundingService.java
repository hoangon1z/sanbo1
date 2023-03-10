package com.bizzan.bitrade.service;


import com.bizzan.bitrade.dao.CrowdMedicalFundingRepostiory;
import com.bizzan.bitrade.dao.CrowdPicRepository;
import com.bizzan.bitrade.dao.CrowdfundingRecordRepostiory;
import com.bizzan.bitrade.dao.FundingCommonRepostiory;
import com.bizzan.bitrade.entity.CrowdMedicalFunding;
import com.bizzan.bitrade.entity.CrowdPic;
import com.bizzan.bitrade.entity.CrowdfundingRecord;
import com.bizzan.bitrade.entity.FundingCommon;
import com.bizzan.bitrade.vo.FundingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Path;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrowdMedicalFundingService {

    @Autowired
    private CrowdMedicalFundingRepostiory medicalFundingRepostiory;
    @Autowired
    private CrowdfundingRecordRepostiory recordRepostiory;
    @Autowired
    private FundingCommonRepostiory commonRepostiory;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CrowdPicRepository crowdPicRepository;


    /**
     * 获得所有审核通过医疗众筹
     *
     * @return
     */
    public List<CrowdMedicalFunding> findAllEnabledMedical() {
        Integer[] status = {1, 3};
        return medicalFundingRepostiory.findByStatusInOrderByAddTimeDesc(status);
    }

    /**
     * 新增医疗众筹
     *
     * @param funding
     * @return
     */
    public CrowdMedicalFunding add(CrowdMedicalFunding funding, List<CrowdPic> crowdPics) {
        funding.setAddTime(Calendar.getInstance().getTimeInMillis());
        funding.setDeleted(0);
        funding.setStatus(0);
        funding.setTimes(0);
        funding.setType(FundingType.MEDICAL_FUNDING_TYPE);
        funding.setAmountReceived(new BigDecimal(0.00));
        funding.setDonateRatio(BigDecimal.ZERO);
        funding.setDrawMoney(BigDecimal.ZERO);
        funding.setCashRatio(BigDecimal.ZERO);
        medicalFundingRepostiory.save(funding);
        for (CrowdPic crowdPic : crowdPics) {
            crowdPic.setFundingId(funding.getId());
            crowdPic.setType(funding.getType());
        }
        crowdPicRepository.save(crowdPics);
        //funding.setMemberId(Long.valueOf(10));////authMember.getId()//这里需要改
        return medicalFundingRepostiory.saveAndFlush(funding);
    }

    /**
     * 获取医疗众筹详情
     *
     * @param id
     * @return
     */
    public Map getMdicalFundingDetail(Long id, Integer fundingType) {
        //首先得到该众筹
        CrowdMedicalFunding funding = medicalFundingRepostiory.findById(id);
        //再得到该项目下捐助记录
        List<CrowdfundingRecord> recordList = recordRepostiory.getAllByDeletedAndFundingIdAndFundingType(0, id, fundingType);
        //获取评论
        List<FundingCommon> fundingCommonList = commonRepostiory.getAllByDeletedAndFundingIdAndFundingTypeAndStatus(0, id, fundingType, 1);
        Map map = new HashMap();
        map.put("funding", funding);
        map.put("recordList", recordList);
        map.put("fundingCommonList", fundingCommonList);
        List<CrowdPic> list = crowdPicRepository.getByFundingIdAndType(id, fundingType);
        map.put("picsUrl", list);
        return map;
    }


}
