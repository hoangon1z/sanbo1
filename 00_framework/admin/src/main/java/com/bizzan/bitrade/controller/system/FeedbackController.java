package com.bizzan.bitrade.controller.system;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.core.Menu;
import com.bizzan.bitrade.entity.Feedback;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.SysRole;
import com.bizzan.bitrade.service.FeedbackService;
import com.bizzan.bitrade.service.SysPermissionService;
import com.bizzan.bitrade.service.SysRoleService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.bizzan.bitrade.vo.InviteManagementVO;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaoxianjun
 * @date 2018年12月20日
 */
@RestController
@RequestMapping(value = "system/feedback")
public class FeedbackController extends BaseAdminController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 条件查询
     */
    @AccessLog(module = AdminModule.CMS, operation = "查询意见反馈")
    @RequestMapping(value = "query", method = RequestMethod.POST)
    public MessageResult queryPage(PageModel pageModel) {
        List<BooleanExpression> booleanExpressions = new ArrayList<>();
        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);
        Page<Feedback> page = feedbackService.findAll(predicate, pageModel.getPageable());
        return success(page);
    }


}
