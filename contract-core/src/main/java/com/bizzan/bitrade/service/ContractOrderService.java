package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ContractOrderRepository;
import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ContractOrderService extends BaseService {

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private ContractOrderRepository contractOrderRepository;

    public Page<ContractOrder> findAll(Predicate predicate, Pageable pageable) {
        return contractOrderRepository.findAll(predicate, pageable);
    }

    /**
     * 查询所有需要根据价格监控的持仓
     * @return
     */
    public List<ContractOrder> findAllNeedMatch() {
        return contractOrderRepository.findAllNeedMatch();
    }
}
