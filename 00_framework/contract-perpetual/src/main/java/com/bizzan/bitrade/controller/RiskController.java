package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.engine.ContractCoinMatch;
import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import com.bizzan.bitrade.engine.risk.RiskControl;
import com.bizzan.bitrade.entity.RiskPeriod;
import com.bizzan.bitrade.entity.RiskStrategy;
import com.bizzan.bitrade.service.RiskStrategyService;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.Vector;

@Slf4j
@RestController
@RequestMapping("/risk")
public class RiskController {
    @Autowired
    private ContractCoinMatchFactory factory;
    @Autowired
    private RiskStrategyService riskStrategyService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取风控数据
     *
     * @param symbol
     * @return
     */
    @GetMapping("periodsdasdasd")
    public MessageResult riskPeriods(String sign, String symbol) {
        if (!sign.equals("987654321asdf")) {
            return MessageResult.error("出现错误");
        }
        ContractCoinMatch contractCoinMatch = factory.getContractCoinMatch(symbol);
        RiskControl riskControl = contractCoinMatch.getRiskControl();
        List<RiskPeriod> riskPeriods = riskControl.getRiskPeriods();
        return MessageResult.success("", riskPeriods);
    }

    /**
     * 添加风控数据
     *
     * @param period
     * @return
     */
    @PostMapping("adderiodjklfhfhk")
    public MessageResult addRiskPeriods(String sign, String symbol, @RequestBody RiskPeriod period) {
        if (!sign.equals("987654321asdf")) {
            return MessageResult.error("出现错误");
        }
        ContractCoinMatch contractCoinMatch = factory.getContractCoinMatch(symbol);
        RiskControl riskControl = contractCoinMatch.getRiskControl();
        riskControl.addForce(period);
        return MessageResult.success();
    }

    @GetMapping("positionddacxzad")
    public MessageResult positions(String sign, String symbol) {
        if (!sign.equals("987654321asdf")) {
            return MessageResult.error("出现错误");
        }
        ContractCoinMatch contractCoinMatch = factory.getContractCoinMatch(symbol);
        return MessageResult.success(contractCoinMatch.getContractOrderList());
    }

    /**
     * 测试
     *
     * @param symbol
     * @return
     */
    @GetMapping("test")
    @Transactional
    public MessageResult test(Long memberId) {
        RiskStrategy strategy = riskStrategyService.findActiveStrategyByMemberId(memberId);
        return MessageResult.success(strategy.getName());
    }

    @GetMapping("test-del")
    public MessageResult testdel(Long memberId) {
        Boolean aBoolean = redisTemplate.hasKey(SysConstant.RISK_STRATEGY + "37");
        Set<String> keys = redisTemplate.keys(SysConstant.RISK_STRATEGY + "*");
        redisTemplate.delete(keys);
        return MessageResult.success();
    }


}
