package com.bizzan.bitrade.controller.risk;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.RiskPeriodGarbage;
import com.bizzan.bitrade.entity.RiskStrategy;
import com.bizzan.bitrade.service.RiskPeriodGarbageService;
import com.bizzan.bitrade.service.RiskStrategyService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("risk/garbage")
public class RiskGarbageController extends BaseAdminController {
    @Autowired
    private RiskPeriodGarbageService riskPeriodGarbageService;

    @RequiresPermissions("risk:data:view")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.RISK, operation = "查看风控垃圾站")
    public MessageResult strategys(PageModel pageModel, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        List<BooleanExpression> booleanExpressions = new ArrayList<>();
        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);
        List<String> list = new ArrayList<>();
        list.add("id");
        List<Sort.Direction> directions = new ArrayList<>();
        directions.add(Sort.Direction.DESC);
        pageModel.setProperty(list);
        pageModel.setDirection(directions);
        Page<RiskPeriodGarbage> page = riskPeriodGarbageService.findAll(predicate, pageModel.getPageable());
        return success(page);
    }
}
