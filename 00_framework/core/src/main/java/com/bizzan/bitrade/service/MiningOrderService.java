package com.bizzan.bitrade.service;

import java.math.BigDecimal;
import java.util.List;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.MiningOrderDao;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiningOrderService extends BaseService {
    @Autowired
    private MiningOrderDao miningOrderDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MiningOrderService miningOrderService;
    @Autowired
    private MemberTransactionService memberTransactionService;
    @Autowired
    private MemberWalletService memberWalletService;

    public MiningOrder findOne(Long id) {
        return miningOrderDao.findOne(id);
    }

    public MiningOrder save(MiningOrder miningOrder) {
//		int day = DateUtil.dayByPeriod(miningOrder.getMiningDays(), miningOrder.getPeriod());
//		miningOrder.setTeamCompute(false); // 待统计算力
        return miningOrderDao.save(miningOrder);
    }

    public MiningOrder saveAndFlush(MiningOrder miningOrder) {
//		int day = DateUtil.dayByPeriod(miningOrder.getMiningDays(), miningOrder.getPeriod());
//		miningOrder.setMiningPower(miningOrder.getMiningDaysprofit().divide(BigDecimal.valueOf(day), 8, BigDecimal.ROUND_DOWN));
//		miningOrder.setTeamCompute(false); // 待统计算力
        return miningOrderDao.saveAndFlush(miningOrder);
    }

    public Page<MiningOrder> findAll(Predicate predicate, Pageable pageable) {
        return miningOrderDao.findAll(predicate, pageable);
    }

    public List<MiningOrder> findAll() {
        return miningOrderDao.findAll();
    }

    public List<MiningOrder> findAllByMemberId(Long memberId) {
        return miningOrderDao.findAllByMemberId(memberId);
    }

    public List<MiningOrder> findAllByActivityId(Long activityId) {
        return miningOrderDao.findAllByActivityId(activityId);
    }

    public List<MiningOrder> findAllByMemberIdAndActivityId(Long memberId, Long activityId) {
        return miningOrderDao.findAllByMemberIdAndActivityId(memberId, activityId);
    }

    public List<MiningOrder> findAllByMiningStatus(int miningStatus) {
        return miningOrderDao.findAllByMiningStatus(miningStatus);
    }

//	public List<Long> findMemberIdNeedCompute() {
//		return miningOrderDao.findMemberIdNeedCompute();
//	}

    public Page<MiningOrder> findAllByMemberIdPage(Long memberId, int pageNo, int pageSize) {
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<MiningOrder> specification = new Criteria<MiningOrder>();
        specification.add(Restrictions.eq("memberId", memberId, false));

        return miningOrderDao.findAll(specification, pageRequest);
    }

//	public BigDecimal findSumPowerByMemberId(Long memberId) {
//		return miningOrderDao.findSumPowerByMemberId(memberId);
//	}
//
//	public void finishComputeByMemberId(Long memberId) {
//		miningOrderDao.finishComputeByMemberId(memberId);
//	}

    /**
     * 增加我的所有矿机产能
     *
     * @param memberId
     */
    public void addCurrentProfitByMemberId(Long memberId, BigDecimal addProfit) {
        List<MiningOrder> orders = findAllByMemberId(memberId);
        for (MiningOrder order : orders) {
            addCurrentProfit(order, addProfit);
        }
    }

    /**
     * 增加邀请者矿机产能
     *
     * @param memberId
     * @param miningId
     */
    public void addInviterCurrentProfit(Long memberId, Long miningId) {
        Member member = memberService.findOne(memberId);
        //判断该用户的父用户是否有相同的矿机
        Long inviterId = member.getInviterId();
        if (inviterId == null || inviterId == 0) {
            return;
        }
        //根据inviterId 与 mingId判断是否有同款矿机(注：方法里ActivityId是矿机Id)
        List<MiningOrder> inviterOrders = miningOrderService.findAllByMemberIdAndActivityId(inviterId, miningId);
        if (inviterOrders == null || inviterOrders.size() == 0) {
            return;
        }
        for (MiningOrder miningOrder : inviterOrders) {
            addCurrentProfit(miningOrder);
        }
    }

    /**
     * 邀请增加单个矿机的产能
     *
     * @param miningOrder
     */
    public void addCurrentProfit(MiningOrder miningOrder) {
        addCurrentProfit(miningOrder, miningOrder.getMiningInvite());
    }

    /**
     * 增加单个矿机的产能
     *
     * @param miningOrder
     * @param addProfit
     */
    @Transactional
    public void addCurrentProfit(MiningOrder miningOrder, BigDecimal addProfit) {
        BigDecimal currentDaysprofit = miningOrder.getCurrentDaysprofit();
        BigDecimal miningDaysprofit = miningOrder.getMiningDaysprofit();
        // 最大产能
        BigDecimal maxDaysprofit = miningDaysprofit.multiply(BigDecimal.ONE.add(miningOrder.getMiningInvitelimit()));
        BigDecimal profit = currentDaysprofit.multiply(BigDecimal.ONE.add(addProfit));
        if (currentDaysprofit.compareTo(maxDaysprofit) > 0) {
            profit = maxDaysprofit;
        }
        miningOrder.setCurrentDaysprofit(profit);
        miningOrderService.save(miningOrder);
    }

    /**
     * 领取矿机收益
     *
     * @param id
     * @return
     */
    @Transactional
    public Boolean fetchProfit(Long id) {
        MiningOrder order = findOne(id);
        if (order == null || order.getCanFetchProfit().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal profit = order.getCanFetchProfit();
        if (profit.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal feeRate = order.getFee();
        BigDecimal fee = profit.multiply(feeRate);
        BigDecimal actualProfit = profit.subtract(fee);
        // 更新余额
        MemberWallet userWallet = memberWalletService.findByCoinUnitAndMemberId(order.getMiningUnit(), order.getMemberId());
        userWallet.setBalance(userWallet.getBalance().add(actualProfit));
        memberWalletService.save(userWallet);
        // 删除矿机待领去
        order.setCanFetchProfit(BigDecimal.ZERO);
        save(order);
        //记录账单
        MemberTransaction memberTransaction1 = new MemberTransaction();
        memberTransaction1.setFee(fee);
        memberTransaction1.setAmount(profit);
        memberTransaction1.setMemberId(order.getMemberId());
        memberTransaction1.setSymbol(order.getMiningUnit());
        memberTransaction1.setType(TransactionType.MINING_OUT);
        memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction1.setRealFee(fee.toString());
        memberTransaction1.setDiscountFee("0");
        memberTransactionService.save(memberTransaction1);
        return true;
    }
}
