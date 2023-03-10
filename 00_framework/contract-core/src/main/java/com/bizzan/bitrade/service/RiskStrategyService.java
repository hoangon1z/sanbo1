package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.dao.RiskStrategyMemberRepository;
import com.bizzan.bitrade.dao.RiskStrategyRepository;
import com.bizzan.bitrade.entity.RiskStrategy;
import com.bizzan.bitrade.entity.RiskStrategyMember;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
public class RiskStrategyService {
    @Autowired
    private RiskStrategyRepository riskStrategyRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RiskStrategyMemberRepository riskStrategyMemberRepository;

    @Transactional
    public RiskStrategy saveWithMembers(RiskStrategy strategy) {
        List<RiskStrategyMember> members = strategy.getMembers();
        for (RiskStrategyMember member : members) {
            member.setRiskStrategy(strategy);
        }
        strategy.setUpdateTime(Calendar.getInstance().getTimeInMillis());
        if (strategy.getId() != null) { // 删除原关联用户
            ArrayList<Long> removeMemberIds = new ArrayList<>();
            RiskStrategy ex = riskStrategyRepository.findOne(strategy.getId());
            List<RiskStrategyMember> exMembers = ex.getMembers();
            for (RiskStrategyMember exMember : exMembers) {
                Optional<RiskStrategyMember> op = members.stream().filter(item -> item.getMemberId().equals(exMember.getMemberId())).findFirst();
                if (op.isPresent()) {
                    // 存在
                } else {
                    // 不存在
                    removeMemberIds.add(exMember.getMemberId());
                }
            }
            riskStrategyMemberRepository.deleteByMemberIdIn(removeMemberIds);
        }
        return save(strategy);
    }

    public RiskStrategy save(RiskStrategy strategy) {
        Set<String> keys = redisTemplate.keys(SysConstant.RISK_STRATEGY + "*");
        redisTemplate.delete(keys);
        return riskStrategyRepository.save(strategy);
    }

//    public HashMap<String, Object> findDetail(long id) {
//        HashMap<String, Object> re = new HashMap<>();
//        RiskStrategy riskStrategy =  riskStrategyRepository.findOne(id);
//        List<RiskStrategyMember> members = riskStrategyMemberRepository.findAllByRiskStrategy(riskStrategy);
//        re.put("strategy", riskStrategy);
//        re.put("members", members);
//        return re;
//    }

    public RiskStrategy findOne(long id) {
        return riskStrategyRepository.findOne(id);
    }

    public void delete(long id) {
        riskStrategyRepository.delete(id);
    }

    public Page<RiskStrategy> findAll(Predicate predicate, Pageable pageable) {
        return riskStrategyRepository.findAll(predicate, pageable);
    }

    /**
     * 根据用户ID 该用户适合的风控策略
     *
     * @param memberId
     * @return
     */
    @Transactional
    public RiskStrategy findActiveStrategyByMemberId(Long memberId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.RISK_STRATEGY + memberId;
        if (redisTemplate.hasKey(key)) {
            RiskStrategy riskStrategy = (RiskStrategy) valueOperations.get(key);
            return riskStrategy;
        }
        RiskStrategy riskStrategy = riskStrategyRepository.findByMemberId(memberId);
        if (riskStrategy == null) {
            riskStrategy = riskStrategyRepository.findByIsDefaultAndStatus(true, 1);
        }
        if (riskStrategy != null) {
            riskStrategy.setMembers(null); // 避免序列化
        }
        valueOperations.set(key, riskStrategy);
        return riskStrategy;
    }
}
