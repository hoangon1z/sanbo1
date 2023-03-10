package com.bizzan.bitrade.controller;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.service.MemberWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.MiningOrderDetailService;
import com.bizzan.bitrade.service.MiningOrderService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;

@RestController
@RequestMapping("miningorder")
public class MiningOrderController extends BaseController {
    @Autowired
    private MiningOrderService miningOrderService;

    @Autowired
    private MiningOrderDetailService miningOrderDetailService;

    @Autowired
    private MemberWalletService memberWalletService;

    /**
     * 我的矿机列表
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("my-minings")
    public MessageResult page(@SessionAttribute(SESSION_MEMBER) AuthMember member, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Assert.notNull(member, "The login timeout!");
        MessageResult mr = new MessageResult();
        Page<MiningOrder> all = miningOrderService.findAllByMemberIdPage(member.getId(), pageNo, pageSize);
        mr.setCode(0);
        mr.setData(all);
        return mr;
    }

    /**
     * 获取矿机收益
     *
     * @param member
     * @return
     */
    @RequestMapping("fetch-profit")
    public MessageResult fetchProfit(@SessionAttribute(SESSION_MEMBER) AuthMember member, Long id) {
        MiningOrder order = miningOrderService.findOne(id);
        BigDecimal profit = order.getCanFetchProfit();
        BigDecimal fee = profit.multiply(order.getFee());
        MemberWallet userWallet = memberWalletService.findByCoinUnitAndMemberId(order.getMiningUnit(), order.getMemberId());
        if (userWallet.getBalance().compareTo(fee) < 0) {
            return error("您账户可用来抵扣手续费的K币不够，参与众筹捐款即可获得相应K币");
        }
        Boolean re = miningOrderService.fetchProfit(id);
        if (!re) {
            return error("出现错误，请联系管理员");
        }
        MessageResult mr = new MessageResult();
        mr.setCode(0);
        return mr;
    }

    /**
     * 获取指定矿机详情
     *
     * @param member
     * @param miningId
     * @return
     */
    @RequestMapping("my-mining-detail")
    public MessageResult miningDetail(@SessionAttribute(SESSION_MEMBER) AuthMember member, Long miningId) {
        Assert.notNull(member, "The login timeout!");
        Assert.notNull(miningId, "矿机不存在!");
        MiningOrder mo = miningOrderService.findOne(miningId);
        if (mo != null) {
            if (mo.getMemberId().longValue() != member.getId()) {
                return error("非法访问");
            }
            return success(mo);
        } else {
            return error("矿机不存在");
        }
    }

    /**
     * 矿机产出明细
     *
     * @param member
     * @param miningId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("mining-detail")
    public MessageResult miningDetail(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                      @RequestParam(value = "miningId", defaultValue = "1") Long miningId,
                                      @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Assert.notNull(member, "The login timeout!");
        Assert.notNull(miningId, "矿机不存在!");
        MiningOrder mining = miningOrderService.findOne(miningId);
        Assert.notNull(mining, "矿机不存在!");
        if (mining.getMemberId().longValue() != member.getId()) {
            return error("非法访问");
        }
        Page<MiningOrderDetail> all = miningOrderDetailService.findAllByMiningOrderId(miningId, pageNo, pageSize);
        return success(all);
    }
}
