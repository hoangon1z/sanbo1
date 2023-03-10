package com.bizzan.bitrade.controller.crowd;

import com.bizzan.bitrade.DateUtil;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.CrowdFundingRepository;
import com.bizzan.bitrade.dao.CrowdMedicalFundingRepostiory;
import com.bizzan.bitrade.dao.CrowdfundingRecordRepostiory;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.FundingScreen;
import com.bizzan.bitrade.model.screen.MedicalFundingScreen;
import com.bizzan.bitrade.service.FundingAmountService;
import com.bizzan.bitrade.service.FundingService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/crowd")
public class CrowdController {


    @Autowired
    private FundingService fundingService;
    @Autowired
    private CrowdFundingRepository fundingRepository;
    @Autowired
    private CrowdfundingRecordRepostiory recordRepostiory;

    @Autowired
    private CrowdMedicalFundingRepostiory medicalFundingRepostiory;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FundingAmountService amountService;


    /**
     * 分页条件查询所有非医疗众筹
     *
     * @param pageModel
     * @return
     */
    @PostMapping("page-query")
    public MessageResult page(PageModel pageModel, FundingScreen screen) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("addTime");
            pageModel.setProperty(property);
        }
        //获取查询条件
        Predicate predicate = getPredicate(screen);
        Page<CrowdFunding> all = fundingRepository.findAll(predicate, pageModel.getPageable());
        //List<CrowdFunding> crowdFundings  =fundingRepository.findAll();
        MessageResult result = MessageResult.success("ok");
        result.setData(all);
        return result;
    }

    private Predicate getPredicate(FundingScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QCrowdFunding qCrowdFunding = QCrowdFunding.crowdFunding;
        if (screen.getId() != null) {
            booleanExpressions.add(qCrowdFunding.id.eq(screen.getId()));
        }
        if (StringUtils.isNotEmpty(screen.getFundingTitle())) {
            booleanExpressions.add(qCrowdFunding.fundingTitle.like(screen.getFundingTitle()));
        }
        if (screen.getMemberId() != null) {
            booleanExpressions.add(qCrowdFunding.memberId.eq(screen.getMemberId()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(qCrowdFunding.status.eq(screen.getStatus()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);

    }

    /**
     * 非医疗通过审核
     *
     * @param id
     * @return
     */
    @PostMapping("passcrow")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult passCrow(Long id, BigDecimal donateRatio, BigDecimal cashRatio) {
        try {
            CrowdFunding funding = fundingRepository.findById(id);
            if (donateRatio == null) {
                return MessageResult.error("费率不可以为空");
            }
            if (cashRatio == null) {
                return MessageResult.error("手续费不可以为空");
            }
            if (funding != null) {
                funding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                funding.setPassTime(Calendar.getInstance().getTimeInMillis());
                Long endTime = DateUtil.getPreMonth();
                funding.setEndTime(endTime);
                funding.setStatus(1);
                funding.setCashRatio(cashRatio);
                funding.setDonateRatio(donateRatio);
                fundingRepository.saveAndFlush(funding);
                String code = "0";
                MessageResult result = MessageResult.success("ok");
                result.setData(code);
                amountService.addFundingTimes();
                return result;
            }
            return MessageResult.error(-1, "参数错误");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return MessageResult.error(-1, "数据更新异常");
        }

    }

    /**
     * 非医疗拒绝审核
     *
     * @param id
     * @return
     */
    @PostMapping("refusescrow")
    @Transactional
    public MessageResult refuceCrow(Long id) {
        CrowdFunding funding = fundingRepository.findById(id);
        if (funding != null) {
            funding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            funding.setStatus(2);
            fundingRepository.saveAndFlush(funding);
            String code = "0";
            MessageResult result = MessageResult.success("ok");
            result.setData(code);
            //删除用户捐助记录表
            Long memberId = funding.getMemberId();
            Member member = memberService.findOne(memberId);
            CrowdUserTimes userTimes = fundingService.getMemberCrowd(memberId, member.getLevel().getId(), 0);
            fundingService.deleteCrowdUserTimes(userTimes);
            return result;
        }
        return MessageResult.error(-1, "参数错误");
    }


    /**
     * 非医疗机构详情
     *
     * @param pageModel
     * @param id
     * @return
     */
    @PostMapping("getdetail")
    public MessageResult getdetail(PageModel pageModel, Long id) {
        CrowdFunding funding = fundingRepository.findById(id);
        if (funding != null) {
            List<CrowdfundingRecord> recordList = recordRepostiory.getAllByFundingId(id);
            Page<CrowdfundingRecord> records = recordRepostiory.getAllByFundingId(id, pageModel.getPageable());
            MessageResult result = MessageResult.success("ok");
            Map map = new HashMap();
            map.put("data", records);
            map.put("count", recordList.size());
            result.setData(map);
            return result;
        }

        return MessageResult.error(-1, "参数错误");
    }

    /**
     * 分页条件查询所有非医疗众筹
     *
     * @param pageModel
     * @param screen
     * @return
     */
    @PostMapping("querymedicallist")
    public MessageResult querymedicallist(PageModel pageModel, MedicalFundingScreen screen) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("addTime");
            pageModel.setProperty(property);
        }
        //获取查询条件
        Predicate predicate = getPredicate(screen);
        Page<CrowdMedicalFunding> all = medicalFundingRepostiory.findAll(predicate, pageModel.getPageable());
        //List<CrowdFunding> crowdFundings  =fundingRepository.findAll();
        MessageResult result = MessageResult.success("ok");
        result.setData(all);
        return result;
    }

    private Predicate getPredicate(MedicalFundingScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QCrowdMedicalFunding qCrowdFunding = QCrowdMedicalFunding.crowdMedicalFunding;
        if (screen.getId() != null) {
            booleanExpressions.add(qCrowdFunding.id.eq(screen.getId()));
        }
        if (StringUtils.isNotEmpty(screen.getFundingTitle())) {
            booleanExpressions.add(qCrowdFunding.fundingTitle.like(screen.getFundingTitle()));
        }
        if (screen.getMemberId() != null) {
            booleanExpressions.add(qCrowdFunding.memberId.eq(screen.getMemberId()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(qCrowdFunding.status.eq(screen.getStatus()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);

    }

    /**
     * 医疗通过审核
     *
     * @param id
     * @return
     */
    @PostMapping("passmedicalcrow")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult passMedicalCrow(Long id, BigDecimal donateRatio, BigDecimal cashRatio) {

        try {
            CrowdMedicalFunding funding = medicalFundingRepostiory.findById(id);
            if (donateRatio == null) {
                return MessageResult.error("费率不可以为空");
            }
            if (cashRatio == null) {
                return MessageResult.error("手续费不可以为空");
            }
            if (funding != null) {
                funding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
                funding.setPassTime(Calendar.getInstance().getTimeInMillis());
                Long endTime = DateUtil.getPreMonth();
                funding.setEndTime(endTime);
                funding.setStatus(1);
                funding.setCashRatio(cashRatio);
                funding.setDonateRatio(donateRatio);
                medicalFundingRepostiory.saveAndFlush(funding);
                String code = "0";
                MessageResult result = MessageResult.success("ok");
                amountService.addFundingTimes();
                result.setData(code);
                return result;
            }

            return MessageResult.error(-1, "参数错误");
        } catch (Exception e) {
            return MessageResult.error(-1, "数据更新异常");
        }
    }

    /**
     * 医疗拒绝审核
     *
     * @param id
     * @return
     */
    @PostMapping("refusemedicalscrow")
    public MessageResult refuceMedicalCrow(Long id) {
        CrowdMedicalFunding funding = medicalFundingRepostiory.findById(id);
        if (funding != null) {
            funding.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            funding.setStatus(2);
            medicalFundingRepostiory.saveAndFlush(funding);
            String code = "0";
            MessageResult result = MessageResult.success("ok");
            result.setData(code);

            return result;
        }
        return MessageResult.error(-1, "参数错误");
    }

    /**
     * 非医疗机构详情
     *
     * @param pageModel
     * @param id
     * @return
     */
    @PostMapping("getmedicaldetail")
    public MessageResult getMedicaldetail(PageModel pageModel, Long id) {
        CrowdMedicalFunding funding = medicalFundingRepostiory.findById(id);
        if (funding != null) {
            List<CrowdfundingRecord> recordList = recordRepostiory.getAllByFundingId(id);
            Page<CrowdfundingRecord> records = recordRepostiory.getAllByFundingId(id, pageModel.getPageable());
            MessageResult result = MessageResult.success("ok");
            Map map = new HashMap();
            map.put("data", records);
            map.put("count", recordList.size());
            result.setData(map);
            return result;
        }
        return MessageResult.error(-1, "参数错误");
    }

    /**
     * 获取图片详情
     *
     * @param id
     * @param type
     * @return
     */
    @PostMapping("getPicDetail")
    public MessageResult getPicDetail(Long id, Integer type) {

        List<CrowdPic> crowdPic = fundingService.getCrowdPic(id, type);
        MessageResult result = MessageResult.success("ok");
        result.setData(crowdPic);
        return result;
    }


}

