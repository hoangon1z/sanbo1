package com.bizzan.bitrade.controller.risk;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.service.ContractMemberProfitService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.MemberStrategyProfitQuery;
import com.bizzan.bitrade.vo.MemberStrategyProfitVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("risk/member")
public class RiskMemberController extends BaseAdminController {
    @Autowired
    private ContractMemberProfitService contractMemberProfitService;

    @RequiresPermissions("risk:member:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.RISK, operation = "风控查看所有用户")
    public MessageResult members(MemberStrategyProfitQuery query, PageModel pageModel, @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        Page<MemberStrategyProfitVO> page = contractMemberProfitService.getMembers(query, pageModel.getPageable());
        return success(page);
    }
}
