package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.RiskPeriodGarbageRepository;
import com.bizzan.bitrade.entity.RiskPeriod;
import com.bizzan.bitrade.entity.RiskPeriodGarbage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RiskControlService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RiskPeriodGarbageRepository riskPeriodHistoryRepository;

    private Logger logger = LoggerFactory.getLogger(RiskControlService.class);

    public void saveRisk(String symbol, RiskPeriod period) {
        mongoTemplate.insert(period, "contract_risk_" + symbol);
    }

    public List<RiskPeriod> findRiskBySymbol(String symbol) {
        Query query = new Query();
        return mongoTemplate.find(query, RiskPeriod.class, "contract_risk_" + symbol);
    }

    public RiskPeriod findRiskByTime(String symbol, long time) {
        Query query = new Query();
        Criteria criteria = Criteria.where("start").lte(time).and("end").gte(time);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, RiskPeriod.class, "contract_risk_" + symbol);
    }

    public List<RiskPeriod> findInvolveByTime(String symbol, long start, long end) {
        Query query = new Query();
        Criteria criteria = Criteria.where("start").lte(end).and("end").gte(start);
        query.addCriteria(criteria);
        List<RiskPeriod> periods = mongoTemplate.find(query, RiskPeriod.class, "contract_risk_" + symbol);
        return periods;
    }

    public void historyDelete(String symbol, long beforeTime) {
        Query query = new Query();
        Criteria criteria = Criteria.where("start").lte(beforeTime);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, RiskPeriod.class, "contract_risk_" + symbol);
    }

    @Async
    @Transactional
    public void saveHistory(RiskPeriod period, Boolean on, String reason) {
        RiskPeriodGarbage riskPeriodHistory = new RiskPeriodGarbage(period);
        riskPeriodHistory.setIsOn(on);
        riskPeriodHistory.setReason(reason);
        riskPeriodHistoryRepository.save(riskPeriodHistory);
    }
}
