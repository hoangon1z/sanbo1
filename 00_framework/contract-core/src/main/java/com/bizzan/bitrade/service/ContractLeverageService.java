package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ContractLeverageRepository;
import com.bizzan.bitrade.dao.ContractOrderRepository;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ContractLeverageService extends BaseService {

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private ContractLeverageRepository leverageRepository;

    /**
     * 获取个人杠杆, 没有新建一个
     *
     * @param memberId
     * @param symbol
     * @return
     */
    public ContractLeverage getLeverage(long memberId, String symbol) {
        ContractLeverage le = leverageRepository.findByAndMemberIdAndSymbol(memberId, symbol);
        if (le == null) {
            le = new ContractLeverage();
            le.setLeverage(BigDecimal.TEN);
            le.setMemberId(memberId);
            le.setSymbol(symbol);
            le = leverageRepository.save(le);
        }
        return le;
    }

    /**
     * 设置杠杆
     *
     * @param memberId
     * @param symbol
     * @param leverage
     * @return
     */
    public ContractLeverage setLeverage(long memberId, String symbol, BigDecimal leverage) {
        ContractLeverage le = leverageRepository.findByAndMemberIdAndSymbol(memberId, symbol);
        le.setLeverage(leverage);
        return leverageRepository.save(le);
    }
}
