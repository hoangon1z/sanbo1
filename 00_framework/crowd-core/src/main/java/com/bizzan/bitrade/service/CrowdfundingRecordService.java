package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.CrowdFundingRepository;
import com.bizzan.bitrade.dao.CrowdMedicalFundingRepostiory;
import com.bizzan.bitrade.dao.CrowdfundingRecordRepostiory;
import com.bizzan.bitrade.dao.MemberWalletDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.FundingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;

@Service
public class CrowdfundingRecordService {

    @Autowired
    private CrowdFundingRepository fundingRepository;
    @Autowired
    private CrowdMedicalFundingRepostiory medicalFundingRepostiory;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private CrowdfundingRecordRepostiory recordRepostiory;
    @Autowired
    private MemberWalletDao memberWalletDao;
    @Autowired
    private FundingAmountService fundingAmountService;


    @Transactional
    public MessageResult addRecord(CrowdfundingRecord record, Long id) {
        //用户钱包操作
        //判断是否有余额
        FundingAmount fundingAmount = fundingAmountService.addTotalMoney(record.getFundingMoney());
        BigDecimal fundingMoney = record.getFundingMoney();
        MemberWallet acceptCoinWallet = walletService.findByCoinUnitAndMemberId("USDT", id);
        BigDecimal acceptUSDT = acceptCoinWallet.getBalance();
        if (acceptUSDT.compareTo(fundingMoney) < 0) {
            return MessageResult.error("您的余额不足");
        } else {


            //资金记录
            MemberTransaction addMemberTransaction = new MemberTransaction();
            addMemberTransaction.setFee(BigDecimal.ZERO);
            addMemberTransaction.setAmount(fundingMoney.negate());//相反数
            addMemberTransaction.setMemberId(id);
            addMemberTransaction.setType(TransactionType.DONATE_IN);
            addMemberTransaction.setSymbol("USDT");
            addMemberTransaction.setCreateTime(DateUtil.getCurrentDate());
            addMemberTransaction.setRealFee("0");
            addMemberTransaction.setDiscountFee("0");
            memberTransactionService.save(addMemberTransaction);

            //捐助详情表操作
            record.setDeleted(0);
            record.setFundingTime(Calendar.getInstance().getTimeInMillis());

            if (record.getFundingType() == FundingType.MEDICAL_FUNDING_TYPE) {

                CrowdMedicalFunding medicalFunding = medicalFundingRepostiory.findById(record.getFundingId());
                medicalFunding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                medicalFunding.setTimes(medicalFunding.getTimes() + 1);
                medicalFunding.setAmountReceived(medicalFunding.getAmountReceived().add(record.getFundingMoney()));
                medicalFunding.setDrawMoney(medicalFunding.getDrawMoney().add(fundingMoney));
                if (medicalFunding.getAmountReceived().equals(medicalFunding.getTargetAmount())) {
                    medicalFunding.setStatus(3);
                }
                medicalFundingRepostiory.saveAndFlush(medicalFunding);
                recordRepostiory.save(record);
                //扣用户usdt
                walletService.deductBalance(acceptCoinWallet, fundingMoney);
                //加用户KickBit
                MemberWallet acceptKbCoinWallet = walletService.findByCoinUnitAndMemberId("Kick", id);
                BigDecimal donateRatio = medicalFunding.getDonateRatio();
                BigDecimal userNewKICK = fundingMoney.multiply(donateRatio);
                memberWalletDao.increaseBalance(acceptKbCoinWallet.getId(), userNewKICK);

                MemberTransaction decreaseMemberTransaction = new MemberTransaction();
                decreaseMemberTransaction.setFee(BigDecimal.ZERO);
                decreaseMemberTransaction.setAmount(userNewKICK);
                decreaseMemberTransaction.setMemberId(id);
                decreaseMemberTransaction.setType(TransactionType.DONATE_OUT);
                decreaseMemberTransaction.setSymbol("KICK");
                decreaseMemberTransaction.setCreateTime(DateUtil.getCurrentDate());
                decreaseMemberTransaction.setRealFee("0");
                decreaseMemberTransaction.setDiscountFee("0");
                memberTransactionService.save(decreaseMemberTransaction);
                MessageResult result = MessageResult.success("成功");
                result.setData(fundingAmount);
                return result;
            } else {
                CrowdFunding Funding = fundingRepository.findById(record.getFundingId());
                Funding.setTimes(Funding.getTimes() + 1);
                Funding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                Funding.setAmountReceived(Funding.getAmountReceived().add(record.getFundingMoney()));
                Funding.setDrawMoney(Funding.getDrawMoney().add(fundingMoney));
                if (Funding.getAmountReceived().equals(Funding.getTargetAmount())) {
                    Funding.setStatus(3);
                }
                fundingRepository.saveAndFlush(Funding);
                //System.out.print(record);
                //扣用户usdt
                walletService.deductBalance(acceptCoinWallet, fundingMoney);
                //加用户KickBit
                MemberWallet acceptKbCoinWallet = walletService.findByCoinUnitAndMemberId("Kick", id);
                BigDecimal donateRatio = Funding.getDonateRatio();
                BigDecimal userNewKICK = fundingMoney.multiply(donateRatio);
                memberWalletDao.increaseBalance(acceptKbCoinWallet.getId(), userNewKICK);
                recordRepostiory.save(record);

                MemberTransaction decreaseMemberTransaction = new MemberTransaction();
                decreaseMemberTransaction.setFee(BigDecimal.ZERO);
                decreaseMemberTransaction.setAmount(userNewKICK);
                decreaseMemberTransaction.setMemberId(id);
                decreaseMemberTransaction.setType(TransactionType.DONATE_OUT);
                decreaseMemberTransaction.setSymbol("KICK");
                decreaseMemberTransaction.setCreateTime(DateUtil.getCurrentDate());
                decreaseMemberTransaction.setRealFee("0");
                decreaseMemberTransaction.setDiscountFee("0");
                memberTransactionService.save(decreaseMemberTransaction);
                MessageResult result = MessageResult.success("捐助成功");
                result.setData(fundingAmount);
                return result;
            }

        }


    }


}
