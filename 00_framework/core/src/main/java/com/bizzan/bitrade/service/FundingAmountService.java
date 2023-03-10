package com.bizzan.bitrade.service;


import com.bizzan.bitrade.dao.FundingAmountDao;
import com.bizzan.bitrade.entity.FundingAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FundingAmountService {
    @Autowired
    private FundingAmountDao fundingAmountDao;

    //增加总金额
    public FundingAmount addTotalMoney(BigDecimal monney) {
        List<FundingAmount> fundingAmounts = fundingAmountDao.findAll();
        //捐助总详情只存一条数据
        FundingAmount newFundingAmount = fundingAmounts.get(0);
        newFundingAmount.setTotalMoney(newFundingAmount.getTotalMoney().add(monney));
        newFundingAmount.setTotalTimes(newFundingAmount.getTotalTimes() + 1);
        fundingAmountDao.saveAndFlush(newFundingAmount);
        return newFundingAmount;

    }

    //查询总金额
    public FundingAmount getTotal() {
        FundingAmount newFundingAmount = fundingAmountDao.findAll().get(0);
        return newFundingAmount;

    }

    //新增众筹次数
    public void addFundingTimes() {
        List<FundingAmount> fundingAmounts = fundingAmountDao.findAll();
        FundingAmount newFundingAmount = fundingAmounts.get(0);
        newFundingAmount.setTotalProject(newFundingAmount.getTotalProject() + 1);
        fundingAmountDao.saveAndFlush(newFundingAmount);

    }


}
