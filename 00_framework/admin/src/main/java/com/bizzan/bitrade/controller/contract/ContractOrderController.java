package com.bizzan.bitrade.controller.contract;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.ContractOrderScreen;
import com.bizzan.bitrade.model.screen.ExchangeOrderScreen;
import com.bizzan.bitrade.model.screen.ExchangeTradeScreen;
import com.bizzan.bitrade.service.ContractOrderEntrustService;
import com.bizzan.bitrade.service.ContractOrderService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.FileUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wmf
 * @description
 * @date 2020/8/20 10:52
 */
@RestController
@RequestMapping("contract/order")
public class ContractOrderController extends BaseAdminController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ContractOrderService contractOrderService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("contract:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACT, operation = "分页查找持仓单")
    public MessageResult page(
            PageModel pageModel,
            ContractOrderScreen screen) {
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
        Page<ContractOrder> all = contractOrderService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    private Predicate getPredicate(ContractOrderScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QContractOrder q = QContractOrder.contractOrder;
        if (screen.getDirt() != null) {
            booleanExpressions.add(q.direction.eq(screen.getDirt()));
        }
        if (StringUtils.isNotEmpty(screen.getContractOrderId())) {
            booleanExpressions.add(q.contractOrderId.eq(screen.getContractOrderId()));
        }
        if (screen.getMemberId() != null) {
            booleanExpressions.add(q.memberId.eq(screen.getMemberId()));
        }
        if (StringUtils.isNotBlank(screen.getSymbol())) {
            booleanExpressions.add(q.symbol.equalsIgnoreCase(screen.getSymbol()));
        }
        if (StringUtils.isNotBlank(screen.getCoinSymbol())) {
            booleanExpressions.add(q.coinSymbol.equalsIgnoreCase(screen.getCoinSymbol()));
        }
        if (StringUtils.isNotBlank(screen.getBaseSymbol())) {
            booleanExpressions.add(q.baseSymbol.equalsIgnoreCase(screen.getBaseSymbol()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(q.status.eq(screen.getStatus()));
        }
        if (screen.getMinPrice() != null) {
            booleanExpressions.add(q.openPrice.goe(screen.getMinPrice()));
        }
        if (screen.getMaxPrice() != null) {
            booleanExpressions.add(q.openPrice.loe(screen.getMaxPrice()));
        }
        if (screen.getMinAmount() != null) {
            booleanExpressions.add(q.position.goe(screen.getMinAmount()));
        }
        if (screen.getMaxAmount() != null) {
            booleanExpressions.add(q.position.loe(screen.getMaxAmount()));
        }
//        booleanExpressions.add(q.status.eq(ContractOrderStatus.MARKET_IN_THE_POSITION));
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    @RequiresPermissions("contract:order:close")
    @PostMapping("close")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "闪电平仓")
    public MessageResult close(long orderId, BigDecimal price) {
        ContractOrder order = contractOrderService.findOne(orderId);
        if (order.getStatus() != ContractOrderStatus.MARKET_IN_THE_POSITION) {
            return MessageResult.error(500, "order not in trading");
        }
        contractOrderService.close(order, price, false);
        // 发送消息至Exchange系统
        kafkaTemplate.send("contract-order-close", JSON.toJSONString(order));
        return MessageResult.success(messageSource.getMessage("SUCCESS"));
    }
}
