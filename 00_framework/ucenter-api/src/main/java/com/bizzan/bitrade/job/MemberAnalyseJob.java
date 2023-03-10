package com.bizzan.bitrade.job;

import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberLevel;
import com.bizzan.bitrade.service.MemberLevelService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MiningOrderService;
import com.bizzan.bitrade.util.MemberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MemberAnalyseJob {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private MiningOrderService miningOrderService;

    /**
     * 每天凌晨3点统计个人算力，团队算力，直推人数，团队人数，并自动升级会员
     */
    @Scheduled(cron = "0 0 3 * * *")
//    @Scheduled(cron = "0 0 * * * *") //每小时
//    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0 */2 * * * ?")
    @Transactional
    public void analyse() {
        log.info("开始计算会员");
        List<Member> members = memberService.findAll();
        List<Member> trees = MemberUtil.buildTree(members); // 所有根节点
//        List<Long> needComputePowerMemberIds = miningOrderService.findMemberIdNeedCompute();
        List<MemberLevel> levels = memberLevelService.findAllDesc();
        for (Member tree : trees) {
            if (tree.getId() < 10) { // 机器人不处理
                continue;
            }
            compute(tree, levels);
        }
        log.info("结束计算会员");
    }

    /**
     * 计算团队人数，直推人数，团队算力，并升级等级
     *
     * @param member
     * @return
     */
    private Member compute(Member member, List<MemberLevel> levels) {
        int teamLevel = 1; // 团队人数
        int firstLevel = 0; // 直推人数
        BigDecimal teamPower = member.getPower(); //团队算力初始值
        HashMap<Long, Integer> downLevels = new HashMap<>(); // 直属下级会员等级
        List<Member> childrens = member.getChildren();
        if (childrens != null && childrens.size() != 0) {
            for (Member c : childrens) {
                Member child = compute(c, levels);
                firstLevel++;
                teamLevel += child.getTeamLevel();
                teamPower = teamPower.add(child.getTeamPower());
                if (child.getLevel() != null) {
                    addOneToLevelMap(downLevels, child.getLevel().getId());
                }
            }
        }
        member.setFirstLevel(firstLevel); // 直推人数
        member.setTeamLevel(teamLevel); // 团队人数
        member.setTeamPower(teamPower); // 团队算力
        setLevel(member, levels, downLevels);
        memberService.save(member);
        return member;
    }

    private void addOneToLevelMap(HashMap<Long, Integer> downLevels, Long lid) {
        Integer num = downLevels.get(lid) != null ? downLevels.get(lid) : 0;
        downLevels.put(lid, num + 1);
        Iterator<Map.Entry<Long, Integer>> entries = downLevels.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, Integer> entry = entries.next();
            Long key = entry.getKey();
            Integer value = entry.getValue();
            if (key.longValue() < lid.longValue()) {
                Integer numLower = downLevels.get(key) != null ? downLevels.get(key) : 0;
                downLevels.put(key, numLower + 1);
            }
        }
    }

    /**
     * 根据会员条件设置等级(kick的规矩)
     *
     * @param member
     * @param levels
     */
    private void setLevel(Member member, List<MemberLevel> levels, HashMap<Long, Integer> downLevels) {
        for (MemberLevel level : levels) {
            if (level.getId().intValue() <= member.getLevel().getId().intValue()) {
                break;
            }
            if (!StringUtils.isEmpty(level.getExtTerm1()) && !StringUtils.isEmpty(level.getExtTerm2())) { // 直属下级等级个数限制
                Long lid = Long.valueOf(level.getExtTerm2());
                Integer dNum = downLevels.get(lid);
                if (dNum == null || dNum.intValue() < Integer.valueOf(level.getExtTerm1()).intValue()) { // 不满足下级等级个数限制
                    continue;
                }
            }

            if (!StringUtils.isEmpty(level.getExtTerm3())) { // 算力限制
                if (member.getTeamPower().compareTo(new BigDecimal(level.getExtTerm3())) < 0) { // 不满足算力限制
                    continue;
                }
            }

            if (!StringUtils.isEmpty(level.getExtTerm4())) { // 团队人数限制
                if (member.getTeamLevel() < Integer.valueOf(level.getExtTerm4()).intValue()) { //不满足团队人数限制
                    continue;
                }
            }

            member.setLevel(level);
            // 过关，设置等级，跳出
            break;
        }
    }
}
