package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ContractMemberProfitAndStrategyImpl;
import com.bizzan.bitrade.dao.ContractMemberProfitRepository;
import com.bizzan.bitrade.entity.ContractMemberProfit;
import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.RiskSetting;
import com.bizzan.bitrade.vo.MemberStrategyProfitQuery;
import com.bizzan.bitrade.vo.MemberStrategyProfitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class ContractMemberProfitService {

    @Autowired
    private ContractMemberProfitAndStrategyImpl contractMemberProfitAndStrategy;

    @Autowired
    private ContractMemberProfitRepository contractMemberProfitRepository;

    @Autowired
    private RiskSettingService riskSettingService;

    public Page<MemberStrategyProfitVO> getMembers(MemberStrategyProfitQuery query, Pageable pageable) {
        return contractMemberProfitAndStrategy.getMembers(query, pageable);
    }

    @Transactional
    public void recordMemberProfit(Long memberId, BigDecimal secondProfit, BigDecimal alwaysProfit) {
        ContractMemberProfit profit = contractMemberProfitRepository.findByMemberId(memberId);
        if (profit == null) {
            profit = new ContractMemberProfit();
            profit.setAlwaysProfit(alwaysProfit);
            profit.setSecondProfit(secondProfit);
            profit.setMemberId(memberId);
        } else {
            profit.setSecondProfit(profit.getSecondProfit().add(secondProfit));
            profit.setAlwaysProfit(profit.getAlwaysProfit().add(alwaysProfit));
        }
        contractMemberProfitRepository.save(profit);
    }

    @Async
    public void record(ContractOrder order, BigDecimal profit) {
        BigDecimal secondProfit = BigDecimal.ZERO;
        BigDecimal alwaysProfit = BigDecimal.ZERO;
        if (order.getPlanCloseTime() == null) {
            alwaysProfit = profit;
        } else {
            secondProfit = profit;
        }
        recordMemberProfit(order.getMemberId(), secondProfit, alwaysProfit);
        RiskSetting riskSetting = riskSettingService.findOne();
        if (profit.compareTo(BigDecimal.ZERO) > 0) {
            riskSetting.setLoss(riskSetting.getLoss().add(profit.abs()));
        } else {
            riskSetting.setProfit(riskSetting.getProfit().add(profit.abs()));
        }
        riskSettingService.save(riskSetting);
    }
}
