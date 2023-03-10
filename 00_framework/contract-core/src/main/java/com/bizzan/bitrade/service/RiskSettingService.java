package com.bizzan.bitrade.service;

import com.bizzan.bitrade.entity.RiskPeriod;
import com.bizzan.bitrade.entity.RiskSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RiskSettingService {
    @Autowired
    private MongoTemplate mongoTemplate;

    private Logger logger = LoggerFactory.getLogger(RiskSettingService.class);

    public void save(RiskSetting params) {
        RiskSetting setting = findOne();
        if (setting != null) {
            Query query = new Query();
            Update update = new Update();
            update.set("profit", params.getProfit());
            update.set("loss", params.getLoss());
            update.set("spotRate", params.getSpotRate());
            update.set("maxLossRate", params.getMaxLossRate());
            update.set("maxProfitRate", params.getMaxProfitRate());
            mongoTemplate.updateFirst(query, update, "contract_risk_setting");
        } else {
            mongoTemplate.insert(params, "contract_risk_setting");
        }
    }

    public RiskSetting findOne() {
        Query query = new Query();
        return mongoTemplate.findOne(query, RiskSetting.class, "contract_risk_setting");
    }

    public BigDecimal getRiskRate() {
        RiskSetting setting = findOne();
        BigDecimal profitRate = setting.getProfit().divide(setting.getProfit().add(setting.getLoss()), 2, BigDecimal.ROUND_DOWN);
        BigDecimal maxProfitRate = setting.getSpotRate().add(setting.getMaxProfitRate());
        BigDecimal minProfitRate = setting.getSpotRate().subtract(setting.getMaxLossRate());
        if (profitRate.compareTo(maxProfitRate) >= 0) {
            return BigDecimal.ZERO;
        } else if (profitRate.compareTo(minProfitRate) <= 0) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.valueOf(0.5);
        }
    }
}
