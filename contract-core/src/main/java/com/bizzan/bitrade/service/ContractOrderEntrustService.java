package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ContractOrderEntrustRepository;
import com.bizzan.bitrade.dao.ContractOrderRepository;
import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.ContractOrderEntrust;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ContractOrderEntrustService {
    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private ContractOrderEntrustRepository contractOrderEntrustRepository;

    public Page<ContractOrderEntrust> findAll(Predicate predicate, Pageable pageable) {
        return contractOrderEntrustRepository.findAll(predicate, pageable);
    }

    /**
     * 查询所有需要处理的委托
     * @return
     */
    public List<ContractOrderEntrust> findAllNeedMatch() {
        return contractOrderEntrustRepository.findAllNeedMatch();
    }
}
