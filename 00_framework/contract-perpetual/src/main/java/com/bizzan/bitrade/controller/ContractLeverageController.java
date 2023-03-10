package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.ContractLeverage;
import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.ContractOrderEntrust;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.ContractLeverageService;
import com.bizzan.bitrade.service.ContractOrderEntrustService;
import com.bizzan.bitrade.service.ContractOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

@Slf4j
@RestController
@RequestMapping("/contract-leverage")
public class ContractLeverageController {

    @Autowired
    private ContractLeverageService leverageService;
    @Autowired
    private ContractOrderService orderService;
    @Autowired
    private ContractOrderEntrustService orderEntrustService;
    @Autowired
    private LocaleMessageSourceService msService;

    @GetMapping
    public MessageResult getLeverage(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, String symbol) {
        if (!StringUtils.isNotEmpty(symbol)) {
            return MessageResult.error("错误的币种信息");
        }
        ContractLeverage leverage = leverageService.getLeverage(authMember.getId(), symbol);
        MessageResult mr = new MessageResult();
        mr.setData(leverage);
        return mr;
    }

    @PostMapping
    public MessageResult setLeverage(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, String symbol, BigDecimal leverage) {
        List<ContractOrder> orders = orderService.findAllNeedMatch(authMember.getId(), symbol);
        if (orders.size() > 0) { //有持仓单
            return MessageResult.error(msService.getMessage("CANTLEVERAGE"));
        }
        List<ContractOrderEntrust> entrusts = orderEntrustService.findAllNeedMatch(authMember.getId(), symbol);
        if (entrusts.size() > 0) { //有挂单
            return MessageResult.error(msService.getMessage("CANTLEVERAGE"));
        }
        leverageService.setLeverage(authMember.getId(), symbol, leverage);
        return MessageResult.success();
    }
}
