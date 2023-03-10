package com.bizzan.er.normal.service;

import com.bizzan.er.normal.entity.RobotQuota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotQuotaService {
	@Autowired
    private MongoTemplate mongoTemplate;

	public RobotQuota findAfter(String symbol, String date, Long time){
        Query query = new Query();
        Criteria criteria = Criteria.where("symbol").is(symbol).and("date").is(date).and("time").gte(time);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.ASC, "time"));
        return mongoTemplate.findOne(query, RobotQuota.class, "robot_quota");
    }

    public RobotQuota findBefore(String symbol, String date, Long time) {
        Query query = new Query();
        Criteria criteria = Criteria.where("symbol").is(symbol).and("date").is(date).and("time").lte(time);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "time"));
        return mongoTemplate.findOne(query, RobotQuota.class, "robot_quota");
    }

    public List<RobotQuota> findAll(String symbol, String date) {
        Query query = new Query();
        Criteria criteria = Criteria.where("symbol").is(symbol).and("date").is(date);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "time"));
        return mongoTemplate.find(query, RobotQuota.class, "robot_quota");
    }

    public void save(RobotQuota quota) {
        mongoTemplate.insert(quota, "robot_quota");
    }

    public void del(String symbol, String date) {
        Query query = Query.query(Criteria.where("symbol").is(symbol).and("date").is(date));
        mongoTemplate.remove(query, RobotQuota.class, "robot_quota");
    }
}
