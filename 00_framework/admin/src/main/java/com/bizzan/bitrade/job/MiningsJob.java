package com.bizzan.bitrade.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.MiningOrderDetailService;
import com.bizzan.bitrade.service.MiningOrderService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.vendor.provider.SMSProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MiningsJob {

    @Autowired
    private SMSProvider smsProvider;

    @Autowired
    private MiningOrderDetailService miningOrderDetailService;

    @Autowired
    private MiningOrderService miningOrderService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private MemberTransactionService memberTransactionService;

    @Autowired
    private MemberService memberService;

    /**
     * 每天晚上10点30发放矿工收益
     */
    @Scheduled(cron = "0 30 22 * * *")
//	@Scheduled(cron = "0 0 * * * *")
//	@Scheduled(cron = "0 */10 * * * ?")
    public void minings() {
        log.info("计算矿机收益");
        List<MiningOrder> list = miningOrderService.findAllByMiningStatus(1);

        Date currentDate = DateUtil.getCurrentDate();
        for (MiningOrder item : list) {
            if (currentDate.compareTo(item.getMiningTime()) > 0) {
                Member member = memberService.findOne(item.getMemberId());
                // 生成收益
                // 更新矿机数据
                item.setTotalProfit(item.getTotalProfit().add(item.getCurrentDaysprofit()));
                item.setCanFetchProfit(item.getCanFetchProfit().add(item.getCurrentDaysprofit())); // 可领取受益
                item.setMiningedTimes(item.getMiningedTimes() + 1);
                item.setMiningTime(DateUtil.dateAddByPeriod(currentDate, item.getMiningDays(), item.getPeriod()));
                if (item.getMiningedTimes() >= item.getMiningTimes()) { //达到最大次数, 结束
                    item.setMiningStatus(2);
                    item.setEndTime(currentDate);
                }
                miningOrderService.saveAndFlush(item);

                // 添加矿机产出数据
                MiningOrderDetail detail = new MiningOrderDetail();
                detail.setMemberId(item.getMemberId());
                detail.setCreateTime(DateUtil.getCurrentDate());
                detail.setMiningOrderId(item.getId());
                detail.setMiningUnit(item.getMiningUnit());
                detail.setOutput(item.getCurrentDaysprofit());
                miningOrderDetailService.saveAndFlush(detail);

                // 发送短信通知用户
                try {
                    smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您的[" + item.getTitle() + "]今日产出：" + item.getCurrentDaysprofit() + "，请查收！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
