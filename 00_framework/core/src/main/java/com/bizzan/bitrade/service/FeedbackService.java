package com.bizzan.bitrade.service;

import com.bizzan.bitrade.entity.MemberApplication;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.FeedbackDao;
import com.bizzan.bitrade.entity.Feedback;
import com.bizzan.bitrade.service.Base.BaseService;

/**
 * @author GS
 * @date 2018年03月19日
 */
@Service
public class FeedbackService extends BaseService {
    @Autowired
    private FeedbackDao feedbackDao;

    public Feedback save(Feedback feedback) {
        return feedbackDao.save(feedback);
    }

    public Page<Feedback> findAll(Predicate predicate, Pageable pageable) {
        return feedbackDao.findAll(predicate, pageable);
    }
}
