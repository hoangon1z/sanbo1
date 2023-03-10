package com.bizzan.bitrade.controller.contract;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.ContractEntrustScreen;
import com.bizzan.bitrade.model.screen.ExchangeOrderScreen;
import com.bizzan.bitrade.service.ContractOrderEntrustService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wmf
 * @description
 * @date 2020/8/20 10:52
 */
@RestController
@RequestMapping("contract/entrust")
public class ContractEntrustController extends BaseAdminController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ContractOrderEntrustService contractOrderEntrustService;
    @Autowired
    private LocaleMessageSourceService messageSource;


    @RequiresPermissions("contract:entrust:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACT, operation = "分页查找委托单")
    public MessageResult page(PageModel pageModel, ContractEntrustScreen screen) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("createTime");
            pageModel.setProperty(property);
        }
        //获取查询条件
        Predicate predicate = getPredicate(screen);
        Page<ContractOrderEntrust> all = contractOrderEntrustService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    private Predicate getPredicate(ContractEntrustScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QContractOrderEntrust qContractOrderEntrust = QContractOrderEntrust.contractOrderEntrust;
        if (screen.getEntrustType() != null) {
            if (screen.getEntrustType() == ContractType.OPENUP) {
                booleanExpressions.add(qContractOrderEntrust.direction.eq(ContractOrderDirection.BUY));
                booleanExpressions.add(qContractOrderEntrust.entrustType.eq(ContractOrderEntrustType.OPEN));
            } else if (screen.getEntrustType() == ContractType.OPENDOWN) {
                booleanExpressions.add(qContractOrderEntrust.direction.eq(ContractOrderDirection.SELL));
                booleanExpressions.add(qContractOrderEntrust.entrustType.eq(ContractOrderEntrustType.OPEN));
            } else if (screen.getEntrustType() == ContractType.CLOSEUP) {
                booleanExpressions.add(qContractOrderEntrust.direction.eq(ContractOrderDirection.BUY));
                booleanExpressions.add(qContractOrderEntrust.entrustType.eq(ContractOrderEntrustType.CLOSE));
            } else if (screen.getEntrustType() == ContractType.CLOSEDOWN) {
                booleanExpressions.add(qContractOrderEntrust.direction.eq(ContractOrderDirection.SELL));
                booleanExpressions.add(qContractOrderEntrust.entrustType.eq(ContractOrderEntrustType.CLOSE));
            }
        }
        if (StringUtils.isNotEmpty(screen.getContractOrderEntrustId())) {
            booleanExpressions.add(qContractOrderEntrust.contractOrderEntrustId.eq(screen.getContractOrderEntrustId()));
        }
        if (screen.getMemberId() != null) {
            booleanExpressions.add(qContractOrderEntrust.memberId.eq(screen.getMemberId()));
        }
        if (screen.getType() != null) {
            booleanExpressions.add(qContractOrderEntrust.type.eq(screen.getType()));
        }
        if (StringUtils.isNotBlank(screen.getSymbol())) {
            booleanExpressions.add(qContractOrderEntrust.symbol.equalsIgnoreCase(screen.getSymbol()));
        }
        if (StringUtils.isNotBlank(screen.getCoinSymbol())) {
            booleanExpressions.add(qContractOrderEntrust.coinSymbol.equalsIgnoreCase(screen.getCoinSymbol()));
        }
        if (StringUtils.isNotBlank(screen.getBaseSymbol())) {
            booleanExpressions.add(qContractOrderEntrust.baseSymbol.equalsIgnoreCase(screen.getBaseSymbol()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(qContractOrderEntrust.status.eq(screen.getStatus()));
        }
        if (screen.getMinPrice() != null) {
            booleanExpressions.add(qContractOrderEntrust.strikePrice.goe(screen.getMinPrice()));
        }
        if (screen.getMaxPrice() != null) {
            booleanExpressions.add(qContractOrderEntrust.strikePrice.loe(screen.getMaxPrice()));
        }
        if (screen.getMinAmount() != null) {
            booleanExpressions.add(qContractOrderEntrust.share.goe(screen.getMinAmount()));
        }
        if (screen.getMaxAmount() != null) {
            booleanExpressions.add(qContractOrderEntrust.share.loe(screen.getMaxAmount()));
        }
        if (screen.getMinProfit() != null) {
            booleanExpressions.add(qContractOrderEntrust.profit.goe(screen.getMinProfit()));
        }
        if (screen.getMaxProfit() != null) {
            booleanExpressions.add(qContractOrderEntrust.profit.loe(screen.getMaxProfit()));
        }
        if (screen.getCompleted() != null) {
            if (screen.getCompleted() == BooleanEnum.IS_FALSE) { // 当前委托
                booleanExpressions.add(qContractOrderEntrust.status.eq(ContractOrderEntrustStatus.ENTRUST_ING));
            } else { //历史委托
                booleanExpressions.add(qContractOrderEntrust.status.ne(ContractOrderEntrustStatus.ENTRUST_ING));
            }
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }


    @RequiresPermissions("contract:entrust:cancel")
    @PostMapping("cancel")
    @AccessLog(module = AdminModule.CONTRACT, operation = "取消委托")
    public MessageResult cancelContractEntrust(long id) {
        ContractOrderEntrust entrust = contractOrderEntrustService.findOne(id);
        if (entrust.getStatus() != ContractOrderEntrustStatus.ENTRUST_ING) {
            return MessageResult.error(500, "order not in trading");
        }
        MessageResult mr = contractOrderEntrustService.cancel(id);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, mr.getMessage());
        }
        // 发送消息至Contract系统
        kafkaTemplate.send("contract-entrust-cancel", JSON.toJSONString(entrust));
        return MessageResult.success(messageSource.getMessage("SUCCESS"));
    }
}
