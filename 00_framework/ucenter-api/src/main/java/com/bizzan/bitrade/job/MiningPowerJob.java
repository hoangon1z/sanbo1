package com.bizzan.bitrade.job;

import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MiningOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class MiningPowerJob {
    @Autowired
    private MiningOrderService miningOrderService;
    @Autowired
    private MemberService memberService;

    private void updateUpTeamPower(Member member, BigDecimal addPower) {
        Long inviterId = member.getInviterId();
        if (inviterId == null) {
            return;
        }
        Member up = memberService.findOne(inviterId);
        if (up == null) {
            return;
        }
        memberService.addOneTeamMiningPower(up.getId(), addPower);
        updateUpTeamPower(up, addPower);
    }

    /**
     * 每日3点处理，统计用户的个人算力(0 0 3 * * *) todo 弃用，实时计算
     */
//    @Scheduled(cron = "0 0 3 * * *")
//	@Scheduled(cron = "0 */1 * * * *")
//    @Transactional
    public void computePower() {
        log.info("开始统计算力");
//        List<Long> memberIds = miningOrderService.findMemberIdNeedCompute();
//        for (Long memberId:memberIds) {
//            Member member = memberService.findOne(memberId);
//            BigDecimal power = miningOrderService.findSumPowerByMemberId(memberId);
////            BigDecimal addPower = power.subtract(member.getMiningPower());
//            member.setMiningPower(power);
//            memberService.save(member);
//            miningOrderService.finishComputeByMemberId(member.getId());
////            updateUpTeamPower(member, addPower);
//        }
        log.info("统计算力结束");
    }
}
