package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.CrowdFundingRepository;
import com.bizzan.bitrade.dao.CrowdPicRepository;
import com.bizzan.bitrade.dao.CrowdWelfareRepostiory;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.screen.CorwdPicScreen;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.FundingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

@RestController
@RequestMapping("/funding")
public class FundingController {

    @Autowired
    private FundingService fundingService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CrowdMedicalFundingService medicalFundingService;
    @Autowired
    private CrowdfundingRecordService recordService;

    @Autowired
    private CrowdCommonService commonService;
    @Autowired
    private CrowdWelfareService crowdWelfareService;
    @Autowired
    private FundingAmountService fundingAmountService;
    @Autowired
    private CrowdPicRepository crowdPicRepository;
    @Autowired
    private CrowdWelfareRepostiory crowdWelfareRepostiory;


    /**
     * 获得首页的各种总数据
     *
     * @return
     */
    @GetMapping("indexAmount")
    public MessageResult getTotalAmount() {
        MessageResult result = MessageResult.success("ok");
        FundingAmount total = fundingAmountService.getTotal();
        result.setData(total);
        return result;
    }


    /**
     * 查询所有审核通过的众筹
     *
     * @return
     */
    @GetMapping("enabled")
    public MessageResult findAllEnabled() {
        Map map = new HashMap();
        List<CrowdFunding> crowdFundings = fundingService.findAllEnabled();
        List<CrowdMedicalFunding> medicalFundings = medicalFundingService.findAllEnabledMedical();
        map.put("funding", crowdFundings);
        map.put("medicalfunding", medicalFundings);
        MessageResult result = MessageResult.success("success");
        result.setData(map);
        System.out.println(map);
        return result;
    }

    /**
     * 获取会员等级权限
     *
     * @param authMember
     * @return
     */
    @PostMapping("getlevel")
    public MessageResult getLevelRight(@SessionAttribute(SESSION_MEMBER) AuthMember authMember) {
        Long id = authMember.getId();
        Member member = memberService.findOne(id);
        MemberLevel memberLevel = member.getLevel();
        String crowdRight = memberLevel.getExtRight1();
        String welfareRight = memberLevel.getExtRight2();
        Map map = new HashMap();
        map.put("crowdRight", crowdRight);
        map.put("welfareRight", welfareRight);
        MessageResult result = MessageResult.success("ok");
        result.setCode(1);
        result.setData(map);
        return result;
    }


    /**
     * 发起非医疗众筹
     *
     * @param //authMember
     * @param body
     * @return
     */
    @PostMapping("addfunding")
    @Transactional
    public MessageResult add(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, @RequestBody String body) {

        JSONObject jsonObject = JSONObject.parseObject(body);
        CrowdFunding funding = jsonObject.getObject("funding", CrowdFunding.class);
        CorwdPicScreen crowdPics = jsonObject.getObject("crowdPics", CorwdPicScreen.class);
        Member member = memberService.findOne(authMember.getId());
        if (fundingService.checkMemberCrowd(member.getId(), member.getLevel().getId(), 0) != null) {
            return MessageResult.error("你已申请众筹，或升级后再提交");
        }
        funding.setMemberId(authMember.getId());
        List<CrowdPic> pics = Arrays.asList(crowdPics.getPics());
        CrowdFunding add = fundingService.add(funding, pics);
        fundingService.addCrowdUserTimes(member.getId(), member.getLevel().getId(), 0);
        return MessageResult.success("申请成功，请等待审核", add);
    }

    /**
     * 发起医疗众筹
     *
     * @param body
     * @return
     */
    @PostMapping("addmedicalfunding")
    @Transactional
    public MessageResult addMdicalFunding(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, @RequestBody String body) {
        //authMember.getId()
        JSONObject jsonObject = JSONObject.parseObject(body);
        CrowdMedicalFunding funding = jsonObject.getObject("funding", CrowdMedicalFunding.class);
        CorwdPicScreen crowdPics = jsonObject.getObject("crowdPics", CorwdPicScreen.class);
        funding.setMemberId(authMember.getId());
        Member member = memberService.findOne(authMember.getId());
        if (fundingService.checkMemberCrowd(member.getId(), member.getLevel().getId(), 0) != null) {
            return MessageResult.error("你已申请众筹，或升级后再提交");
        }
        fundingService.addCrowdUserTimes(member.getId(), member.getLevel().getId(), 0);
        List<CrowdPic> pics = Arrays.asList(crowdPics.getPics());
        CrowdMedicalFunding add = medicalFundingService.add(funding, pics);
        return MessageResult.success("申请成功，请等待审核", add);
    }

    /**
     * 众筹详情
     *
     * @param body(long id int type)
     * @return
     */
    @PostMapping("getdetail")
    public MessageResult getDetail(@RequestBody String body) {
        JSONObject jsonObject = JSONObject.parseObject(body);
        Long id = jsonObject.getLong("id");
        int type = jsonObject.getInteger("type");
        //获取医疗众筹详情
        if (type == FundingType.MEDICAL_FUNDING_TYPE) {
            Map map = medicalFundingService.getMdicalFundingDetail(id, type);
            MessageResult result = MessageResult.success("success");
            result.setData(map);
            return result;
        }
        //获取非医疗众筹详情
        else {
            Map map = fundingService.getFundingDetail(id, type);
            MessageResult result = MessageResult.success("success");
            result.setData(map);
            return result;
        }
    }


    /**
     * 捐款众筹
     *
     * @param record
     * @return
     */
    @PostMapping("addfundingrecord")
    @Transactional
    public MessageResult addFundingRecord(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, @RequestBody CrowdfundingRecord record) {
        record.setMemberId(authMember.getId());
        record.setMemberName(authMember.getName());
        record.setFundingTime(Calendar.getInstance().getTimeInMillis());
        return recordService.addRecord(record, record.getMemberId());
    }

    /**
     * 某个众筹详情下评论
     */
    @PostMapping("addcommon")//这里需要改
    public MessageResult addCommon(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, @RequestBody FundingCommon fundingCommon) {
        fundingCommon.setMemberId(authMember.getId());
        fundingCommon.setMemberName(authMember.getName());
        commonService.addOne(fundingCommon);
        return MessageResult.success();
    }


    /**
     * 新增公益
     *
     * @param
     * @param authMember
     * @return
     */
    @RequestMapping("/addwelfare")
    @Transactional
    public MessageResult addWelfare(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, @RequestBody String body) {
        JSONObject jsonObject = JSONObject.parseObject(body);
        CrowdWelfare crowdWelfare = jsonObject.getObject("crowdWelfare", CrowdWelfare.class);
        CorwdPicScreen crowdPics = jsonObject.getObject("crowdPics", CorwdPicScreen.class);
        crowdWelfare.setMemberId(authMember.getId());
        Member member = memberService.findOne(authMember.getId());
        List<CrowdPic> pics = Arrays.asList(crowdPics.getPics());
        //crowdWelfare.setMemberName(authMember.getName());
        if (fundingService.checkMemberCrowd(member.getId(), member.getLevel().getId(), 1) != null) {
            return MessageResult.error("你已申请众筹，或升级后再提交");
        }
        fundingService.addCrowdUserTimes(member.getId(), member.getLevel().getId(), 1);
        CrowdWelfare welfare = crowdWelfareService.add(crowdWelfare, pics);
        return MessageResult.success("申请成功，请等待审核", welfare);
    }


    /**
     * 所有公益
     *
     * @return
     */
    @RequestMapping("/enablewelfare")
    public MessageResult getWelfareEnabel() {
        List<CrowdWelfare> welfareList = crowdWelfareService.getEnable();
        MessageResult result = MessageResult.success("ok");
        result.setData(welfareList);
        return result;
    }

    /**
     * 公益详情
     */
    @PostMapping("/getWelfareDetail")
    public MessageResult getWelfareDetail(Long id) {
        List<CrowdPic> list = crowdPicRepository.getByFundingIdAndType(id, 4);
        CrowdWelfare crowdWelfare = crowdWelfareRepostiory.findById(id);
        Map map = new HashMap();
        map.put("picsUrl", list);
        map.put("crowdWelfare", crowdWelfare);
        MessageResult result = MessageResult.success("ok");
        result.setData(map);
        return result;
    }


    /**
     * 个人中心查看我的非医疗众筹信息
     *
     * @param pageModel
     * @return
     */
    @PostMapping("/getMemberFunding")
    public MessageResult getMemberFunding(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, PageModel pageModel) {
        Long memberId = authMember.getId();
        if (memberId != null) {
            Page<CrowdFunding> fundings = fundingService.getMemberFunding(pageModel, memberId);
            MessageResult result = MessageResult.success("ok");
            result.setData(fundings);
            return result;
        } else return MessageResult.error("参数错误");

    }


    /**
     * 个人中心查看我的医疗众筹信息
     *
     * @param pageModel
     * @return
     */
    @PostMapping("/getMemberMedicalFunding")
    public MessageResult getMemberMedicalFunding(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, PageModel pageModel) {
        Long memberId = authMember.getId();
        if (memberId != null) {
            Page<CrowdMedicalFunding> medicalFundings = fundingService.getMemberMedicaLFunding(pageModel, memberId);
            MessageResult result = MessageResult.success("ok");
            result.setData(medicalFundings);
            return result;
        } else return MessageResult.error("参数错误");
    }

    /**
     * 个人中心查看我的公益
     *
     * @param pageModel
     * @return
     */
    @PostMapping("/getMemberWelfare")
    //@SessionAttribute(SESSION_MEMBER) AuthMember authMember
    public MessageResult getMemberWelfare(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, PageModel pageModel) {
        Long memberId = authMember.getId();
        if (memberId != null) {
            Page<CrowdWelfare> welfares = fundingService.getMemberCrowdWelfare(pageModel, memberId);
            MessageResult result = MessageResult.success("ok");
            result.setData(welfares);
            return result;
        } else return MessageResult.error("参数错误");
    }


    /**
     * 个人中心查看我的捐助记录
     *
     * @return
     */
    @PostMapping("/getFundingRecord")
    public MessageResult getFundingRecord(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, PageModel pageModel) {
        Long memberId = authMember.getId();
        if (memberId != null) {
            Page<CrowdfundingRecord> records = fundingService.getCrowdfundingRecord(pageModel, memberId);
            MessageResult result = MessageResult.success("ok");
            result.setData(records);
            return result;
        } else return MessageResult.error("参数错误");
    }

    /**
     * 用户提现当前捐助
     *
     * @param authMember
     * @param fundingId
     * @param type
     * @return
     */
    @PostMapping("/getCash")
    // @SessionAttribute(SESSION_MEMBER)AuthMember authMember,
    public MessageResult getCash(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, Long fundingId, Integer type) {
        Long memberId = authMember.getId();
        return fundingService.getCash(memberId, fundingId, type);
    }
}
