package com.bizzan.bitrade.job;

import com.bizzan.bitrade.engine.ContractCoinMatch;
import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import com.bizzan.bitrade.entity.ContractCoin;
import com.bizzan.bitrade.entity.RiskSetting;
import com.bizzan.bitrade.service.ContractCoinService;
import com.bizzan.bitrade.service.RiskControlService;
import com.bizzan.bitrade.service.RiskSettingService;
import com.bizzan.bitrade.util.WebSocketConnectionManage;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.enums.ReadyState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Component
@Slf4j
public class RiskJob {

    @Autowired
    private RiskControlService riskControlService;

    @Autowired
    private ContractCoinService contractCoinService;

    @Autowired
    private RiskSettingService riskSettingService;

    @Autowired
    private ContractCoinMatchFactory matchFactory;

    // 风控强度
    private BigDecimal rate;

    @PostConstruct
    public void init() {
//        rate = riskSettingService.getRiskRate();
        rate = BigDecimal.ZERO;
//        setRiskRate();
//        log.info("初始化风控强度： {}", rate);
    }

    /**
     * 两分钟执行一次，查看系统收益率调整风控强度
     */
    @Scheduled(cron = "0 */2 * * * *")
    public void setRiskRate() {
        RiskSetting setting = riskSettingService.findOne();
        if (setting == null) {
            return;
        }
        BigDecimal profitRate = setting.getProfit().divide(setting.getProfit().add(setting.getLoss()), 2, BigDecimal.ROUND_DOWN);
        BigDecimal maxProfitRate = setting.getSpotRate().add(setting.getMaxProfitRate());
        BigDecimal minProfitRate = setting.getSpotRate().subtract(setting.getMaxLossRate());
        BigDecimal toRate;
        if (profitRate.compareTo(maxProfitRate) >= 0) {
            toRate = BigDecimal.ZERO;
        } else if (profitRate.compareTo(minProfitRate) <= 0) {
            toRate = BigDecimal.ONE;
        } else if (rate.compareTo(BigDecimal.ZERO) == 0 && profitRate.compareTo(setting.getSpotRate()) <= 0) { // 之前停止但收益已低于期望值
            toRate = BigDecimal.valueOf(0.4);
        } else if (rate.compareTo(BigDecimal.ONE) == 0 && profitRate.compareTo(setting.getSpotRate()) >= 0) { // 之前加强收益到达期望值
            toRate = BigDecimal.valueOf(0.6);
        } else {
            toRate = rate;
        }
        log.info("风控强度变化： from: {} to: {}", rate, toRate);
        if (toRate.compareTo(rate) == 0) { // 强度未改变
            return;
        }
        rate = toRate;
        log.info("风控强度变化： {}", rate);
        System.out.println(this.matchFactory.getMatchMap().values());
        for (ContractCoinMatch match : this.matchFactory.getMatchMap().values()) {
            log.info("{} 设置风控强度： {}", match.getSymbol(), rate);
            match.setRate(rate);
        }
    }

    /**
     * 每天2点20分执行一次
     * 注意，删除三天前的风控数据
     */
//    @Scheduled(cron = "0 */2 * * * *")
    @Scheduled(cron = "0 20 2 * * *")
    public void deleteHistory() {
        log.info("开始清理历史风控数据");
        long beforeTime = (System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)) / 1000; // 3天前
        log.info("清除指定时间之前的订单：" + beforeTime);
        List<ContractCoin> coins = contractCoinService.findAll();
        for (ContractCoin coin : coins) {
            riskControlService.historyDelete(coin.getSymbol(), beforeTime);
        }
    }
}
