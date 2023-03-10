package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.MessageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * 委托订单处理类
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ContractOrderService contractOrderService;

    /**
     * 用户当前持仓
     *
     * @param authMember
     * @param contractCoinId
     * @return
     */
    @RequestMapping("position")
    public MessageResult position(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, Long contractCoinId) {
        MessageResult result = MessageResult.success("success");
        result.setData(contractOrderService.findCurrent(authMember.getId(), contractCoinId));
        return result;
    }

//    @RequestMapping("test")
//    public MessageResult test() {
//        List<RiskPeriod> pers = riskControlService.findInvolveByTime("BTC/USDT", 1604390520l, 1604390580l);
//        MessageResult result = MessageResult.success("success");
//        result.setData(pers);
//        return result;
//    }

}
