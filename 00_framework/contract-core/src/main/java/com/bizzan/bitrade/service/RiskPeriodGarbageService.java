package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.RiskPeriodGarbageRepository;
import com.bizzan.bitrade.entity.RiskPeriodGarbage;
import com.bizzan.bitrade.entity.RiskStrategy;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RiskPeriodGarbageService {
    @Autowired
    private RiskPeriodGarbageRepository riskPeriodGarbageRepository;

    public Page<RiskPeriodGarbage> findAll(Predicate predicate, Pageable pageable) {
        return riskPeriodGarbageRepository.findAll(predicate, pageable);
    }
}
