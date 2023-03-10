package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.*;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.FundingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FundingService {

    @Autowired
    private CrowdFundingRepository fundingRepository;
    @Autowired
    private CrowdfundingRecordRepostiory recordRepostiory;
    @Autowired
    private FundingCommonRepostiory commonRepostiory;
    @Autowired
    private CrowdMedicalFundingRepostiory medicalFundingRepostiory;
    @Autowired
    private CrowdWelfareRepostiory welfareRepostiory;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private CrowdUserTimesRepostiory crowdUserTimesRepostiory;
    @Autowired
    private CrowdPicRepository crowdPicRepository;


    /**
     * 获取所有已通过或者已过期的众筹
     *
     * @return
     */
    public List<CrowdFunding> findAllEnabled() {
        Integer[] status = {1, 3};
        return fundingRepository.findByStatusInOrderByAddTimeDesc(status);
    }

    /**
     * 新增非医疗众筹
     *
     * @param funding
     * @return
     */
    @Transactional
    public CrowdFunding add(CrowdFunding funding, List<CrowdPic> crowdPics) {
        funding.setAddTime(Calendar.getInstance().getTimeInMillis());
        funding.setDeleted(0);
        funding.setStatus(0);
        funding.setTimes(0);
        funding.setAmountReceived(new BigDecimal(0.00));
        funding.setDonateRatio(BigDecimal.ZERO);
        funding.setDrawMoney(BigDecimal.ZERO);
        funding.setCashRatio(BigDecimal.ZERO);
        fundingRepository.save(funding);
        for (CrowdPic crowdPic : crowdPics) {
            crowdPic.setFundingId(funding.getId());
            crowdPic.setType(funding.getType());
        }
        crowdPicRepository.save(crowdPics);
        //funding.setMemberId(Long.valueOf(10));////authMember.getId()//这里需要改
        return fundingRepository.saveAndFlush(funding);
    }

    /**
     * 获取非医疗众筹详情
     *
     * @param id
     * @return
     */
    public Map getFundingDetail(Long id, Integer fundingType) {
        //首先得到该众筹
        CrowdFunding funding = fundingRepository.getAllByDeletedAndIdAndType(0, id, fundingType);
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


    /**
     * 审核非医疗众筹
     *
     * @param id
     */
    public void check(Long id) {
        CrowdFunding funding = fundingRepository.findById(id);
        if (funding != null) {
            funding.setPassTime(Calendar.getInstance().getTimeInMillis());
            funding.setStatus(1);
            //计算结束时间
            Calendar cal = Calendar.getInstance();
            cal.add(cal.MONTH, 1);
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
            String preMonth = dft.format(cal.getTime());
            //Calendar.getInstance().getTimeInMillis()+
            funding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
        }
    }


    //查看医疗众筹信息
    public Page<CrowdFunding> getMemberFunding(PageModel pageModel, Long memberId) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("addTime");
            pageModel.setProperty(property);
        }
        Page<CrowdFunding> fundings = fundingRepository.findByMemberIdAndDeleted(memberId, 0, pageModel.getPageable());
        return fundings;

    }

    //个人中心查看获取医疗众筹
    public Page<CrowdMedicalFunding> getMemberMedicaLFunding(PageModel pageModel, Long memberId) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("addTime");
            pageModel.setProperty(property);
        }
        Page<CrowdMedicalFunding> medicalFundings = medicalFundingRepostiory.findByMemberIdAndDeleted(memberId, 0, pageModel.getPageable());
        return medicalFundings;

    }

    //个人中心查看获取线下公益
    public Page<CrowdWelfare> getMemberCrowdWelfare(PageModel pageModel, Long memberId) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("addTime");
            pageModel.setProperty(property);
        }
        Page<CrowdWelfare> welfares = welfareRepostiory.findByMemberIdAndDeleted(memberId, 0, pageModel.getPageable());
        return welfares;

    }

    //个人中心查看捐助记录详情
    public Page<CrowdfundingRecord> getCrowdfundingRecord(PageModel pageModel, Long memberId) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("fundingTime");
            pageModel.setProperty(property);
        }
        Page<CrowdfundingRecord> crowdfundingRecords = recordRepostiory.getAllByMemberIdAndDeleted(memberId, 0, pageModel.getPageable());
        return crowdfundingRecords;
    }

    //提现金额
    @Transactional
    public MessageResult getCash(Long memberId, Long fundingId, Integer type) {
        if (type == FundingType.MEDICAL_FUNDING_TYPE) {
            CrowdMedicalFunding crowdMedicalFunding = medicalFundingRepostiory.findById(fundingId);
            BigDecimal drawMoney = crowdMedicalFunding.getDrawMoney();
            BigDecimal cashRatio = crowdMedicalFunding.getCashRatio();
            MemberWallet kick = walletService.findByCoinUnitAndMemberId("KICK", memberId);
            MemberWallet usdt = walletService.findByCoinUnitAndMemberId("USDT", memberId);
            //计算手续费
            BigDecimal fee = drawMoney.multiply(cashRatio);
            if (fee.compareTo(kick.getBalance()) > 0) {
                return MessageResult.error("您的KICK币余额不足");
            } else {
                //扣除用户余额
                crowdMedicalFunding.setDrawMoney(BigDecimal.ZERO);
                walletService.deductBalance(kick, fee);
                walletService.increaseBalance(usdt.getId(), drawMoney);
                medicalFundingRepostiory.saveAndFlush(crowdMedicalFunding);
                return MessageResult.success("已成功提现");
            }
        } else {
            MemberWallet usdt = walletService.findByCoinUnitAndMemberId("USDT", memberId);
            CrowdFunding crowdFunding = fundingRepository.findById(fundingId);
            BigDecimal drawMoney = crowdFunding.getDrawMoney();
            BigDecimal cashRatio = crowdFunding.getCashRatio();
            MemberWallet kick = walletService.findByCoinUnitAndMemberId("KICK", memberId);

            //计算手续费
            BigDecimal fee = drawMoney.multiply(cashRatio);
            if (fee.compareTo(kick.getBalance()) > 0) {
                return MessageResult.error("您的KICK币余额不足");
            } else {
                //修改用户余额
                walletService.deductBalance(kick, fee);
                walletService.increaseBalance(usdt.getId(), drawMoney);
                crowdFunding.setDrawMoney(BigDecimal.ZERO);
                fundingRepository.saveAndFlush(crowdFunding);
                return MessageResult.success("已成功提现");
            }

        }

    }

    /**
     * 判断用户某个等级下是否有捐助或者公益
     */
    public CrowdUserTimes checkMemberCrowd(Long memberId, Long levelId, Integer tyoeId) {
        return crowdUserTimesRepostiory.findByMemberIdAndLevelIdAndType(memberId, levelId, tyoeId);
    }

    /**
     * 新增用户发起众筹公益记录
     */
    public void addCrowdUserTimes(Long memberId, Long levelId, Integer typeId) {
        CrowdUserTimes crowdUserTimes = new CrowdUserTimes();
        crowdUserTimes.setMemberId(memberId);
        crowdUserTimes.setLevelId(levelId);
        crowdUserTimes.setType(typeId);
        crowdUserTimesRepostiory.save(crowdUserTimes);

    }

    /**
     * 删除用户发起众筹公益记录
     */
    public void deleteCrowdUserTimes(CrowdUserTimes crowdUserTimes) {
        crowdUserTimesRepostiory.delete(crowdUserTimes);
    }


    /**
     * 后台管理查看图片详情
     */
    public List<CrowdPic> getCrowdPic(Long fundingId, Integer tyoeId) {
        List<CrowdPic> pics = crowdPicRepository.getByFundingIdAndType(fundingId, tyoeId);
        return pics;

    }

    /**
     * 根据用户id 等级 类别找出用户记录表
     */
    public CrowdUserTimes getMemberCrowd(Long memberId, Long levelId, Integer typeId) {
        return crowdUserTimesRepostiory.findByMemberIdAndLevelIdAndType(memberId, levelId, typeId);
    }


}
