package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.ContractWalletDao;
import com.bizzan.bitrade.dao.CrowdPicRepository;
import com.bizzan.bitrade.dao.CrowdWelfareRepostiory;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Service
public class CrowdWelfareService extends BaseService {

    @Autowired
    private CrowdWelfareRepostiory welfareRepostiory;
    @Autowired
    private MemberService memberService;

    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private CrowdPicRepository crowdPicRepository;
    @Autowired
    private FundingService fundingService;

    @Transactional
    public CrowdWelfare add(CrowdWelfare crowdWelfare, List<CrowdPic> crowdPics) {
        crowdWelfare.setAddTime(Calendar.getInstance().getTimeInMillis());
        crowdWelfare.setDeleted(0);
        crowdWelfare.setStatus(0);
        CrowdWelfare add = welfareRepostiory.save(crowdWelfare);
        for (CrowdPic crowdPic : crowdPics) {
            crowdPic.setFundingId(crowdWelfare.getId());
            crowdPic.setType(4);
        }
        crowdPicRepository.save(crowdPics);
        return add;
    }

    public List<CrowdWelfare> getEnable() {
        Specification<CrowdWelfare> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> status = root.get("status");
            criteriaQuery.where(criteriaBuilder.equal(status, 1));
            return null;
        };
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Sort sort = new Sort(order);
        return welfareRepostiory.findAll(spec, sort);
    }

    /**
     * 通过线下公益审核
     *
     * @param mid   用户id
     * @param money 打款金额
     * @param wid   公益id
     * @return
     */
    @Transactional
    public void passWelfare(Long mid, BigDecimal money, Long wid) {
        CrowdWelfare welfare = welfareRepostiory.findById(wid);
        welfare.setStatus(1);
        welfare.setDeleted(0);
        welfare.setPassTime(Calendar.getInstance().getTimeInMillis());
        welfare.setUpdateTime(Calendar.getInstance().getTimeInMillis());
        welfare.setAmountReceived(money);
        welfareRepostiory.saveAndFlush(welfare);
        Coin coin = coinService.findOne("USDT");
        Member member = memberService.findOne(mid);
        MemberWallet wallet = memberWalletService.findByCoinAndMember(coin, member);
        memberWalletService.increaseBalance(wallet.getId(), money);
        //增加资金记录
        MemberTransaction memberTransaction1 = new MemberTransaction();
        memberTransaction1.setFee(BigDecimal.ZERO);
        memberTransaction1.setAmount(money);
        memberTransaction1.setMemberId(welfare.getMemberId());
        memberTransaction1.setSymbol("USDT");
        memberTransaction1.setType(TransactionType.WELFARE_IN);
        memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction1.setRealFee("0");
        memberTransaction1.setDiscountFee("0");
        memberTransaction1 = memberTransactionService.save(memberTransaction1);
    }

    @Transactional
    public void refuceWelfare(Long wid, String text) {
        CrowdWelfare welfare = welfareRepostiory.findById(wid);
        welfare.setStatus(2);
        welfare.setUpdateTime(Calendar.getInstance().getTimeInMillis());
        welfare.setRefuceReason(text);
        //删除用户众筹记录表
        Long memberId = welfare.getMemberId();
        Member member = memberService.findOne(memberId);
        Long levelId = member.getLevel().getId();
        Integer tyoeId = 1;
        CrowdUserTimes crowdUserTimes = fundingService.checkMemberCrowd(memberId, levelId, tyoeId);
        fundingService.deleteCrowdUserTimes(crowdUserTimes);
        welfareRepostiory.saveAndFlush(welfare);
    }
}
