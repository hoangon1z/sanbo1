package com.bizzan.bitrade.job;

import com.bizzan.bitrade.dao.CrowdFundingRepository;
import com.bizzan.bitrade.dao.CrowdMedicalFundingRepostiory;
import com.bizzan.bitrade.entity.CrowdFunding;
import com.bizzan.bitrade.entity.CrowdMedicalFunding;
import com.bizzan.bitrade.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class CrowdNumJob {

    @Autowired
    private CrowdFundingRepository fundingRepository;
    @Autowired
    private CrowdMedicalFundingRepostiory medicalFundingRepostiory;

    /**
     * 每天判断一次到期众筹
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void checkExpired() {
        log.info("查询医疗众筹是否过期");
        List<CrowdMedicalFunding> medicalFundings = medicalFundingRepostiory.findByStatus(1);
        for (CrowdMedicalFunding medicalFunding : medicalFundings) {
            Long endTime = medicalFunding.getEndTime();
            Date endDate = new Date(endTime);
            if (DateUtil.compare(endDate, new Date()) == -1 || DateUtil.compare(endDate, new Date()) == 0) {
                medicalFunding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                //状态改为已过期
                medicalFunding.setStatus(3);
                medicalFundingRepostiory.saveAndFlush(medicalFunding);
                System.out.print("更新" + medicalFunding.getFundingTitle());
            }
        }
        log.info("查询非医疗众筹是否过期");
        List<CrowdFunding> crowdFundings = fundingRepository.findByStatus(1);
        for (CrowdFunding crowdFunding : crowdFundings) {
            //过期时间
            Long FudingEndTime = crowdFunding.getEndTime();
            Date FudingEndDate = new Date(FudingEndTime);
            if (DateUtil.compare(FudingEndDate, new Date()) == -1 || DateUtil.compare(FudingEndDate, new Date()) == 0) {
                crowdFunding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                //状态改为已过期
                crowdFunding.setStatus(3);
                fundingRepository.saveAndFlush(crowdFunding);
                System.out.print("更新" + crowdFunding.getFundingTitle());
            }
        }


    }
}
