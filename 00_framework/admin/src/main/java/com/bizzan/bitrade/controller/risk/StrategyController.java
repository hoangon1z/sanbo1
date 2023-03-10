package com.bizzan.bitrade.controller.risk;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.RiskControlService;
import com.bizzan.bitrade.service.RiskStrategyService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@RestController
@RequestMapping("risk/strategy")
public class StrategyController extends BaseAdminController {
    @Autowired
    private RiskStrategyService riskStrategyService;

    @RequiresPermissions("risk:strategy:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.RISK, operation = "查看风控策略")
    public MessageResult strategys(PageModel pageModel, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        List<BooleanExpression> booleanExpressions = new ArrayList<>();
        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);
        Page<RiskStrategy> page = riskStrategyService.findAll(predicate, pageModel.getPageable());
        return success(page);
    }

    /**
     * 添加策略
     *
     * @param
     * @return
     */
    @RequiresPermissions("risk:strategy:page-query")
    @PostMapping("add")
    @AccessLog(module = AdminModule.RISK, operation = "添加策略")
    public MessageResult add(@RequestBody @Valid RiskStrategy strategy, @RequestParam(value = "password") String password, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        riskStrategyService.saveWithMembers(strategy);
        return MessageResult.success();
    }

    /**
     * 获取策略
     *
     * @param
     * @return
     */
    @RequiresPermissions("risk:strategy:page-query")
    @PostMapping("get")
    @AccessLog(module = AdminModule.RISK, operation = "获取策略")
    public MessageResult get(@RequestParam(value = "id") Long id, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        return MessageResult.success(riskStrategyService.findOne(id));
    }

    /**
     * 删除策略
     *
     * @param
     * @return
     */
    @RequiresPermissions("risk:strategy:page-query")
    @PostMapping("del")
    @AccessLog(module = AdminModule.RISK, operation = "删除策略")
    public MessageResult del(@RequestParam(value = "id") Long id, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        riskStrategyService.delete(id);
        return MessageResult.success();
    }

    /**
     * 激活停用策略
     *
     * @param
     * @return
     */
    @RequiresPermissions("risk:strategy:page-query")
    @PostMapping("active")
    @AccessLog(module = AdminModule.RISK, operation = "状态策略")
    public MessageResult active(@RequestParam(value = "id") Long id, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        RiskStrategy riskStrategy = riskStrategyService.findOne(id);
        if (riskStrategy.getStatus().equals(1)) {
            riskStrategy.setStatus(0);
        } else {
            riskStrategy.setStatus(1);
        }
        riskStrategyService.save(riskStrategy);
        return MessageResult.success();
    }
}
