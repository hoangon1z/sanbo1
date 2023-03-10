package com.bizzan.bitrade.controller.member;

import com.bizzan.bitrade.constant.PageModel;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.MemberLevel;
import com.bizzan.bitrade.service.MemberLevelService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shaoxianjun
 * @description 会员等级管理类
 * @date 2018/12/27 10:16
 */
@RestController
@Slf4j
@RequestMapping("member/member-level")
public class MemberLevelController extends BaseAdminController {

    @Autowired
    private MemberLevelService memberLevelService;


    /**
     * 查看全部
     *
     * @param pageModel
     * @return
     */
    @RequiresPermissions("member:member-level:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.MEMBER, operation = "所有会员等级MemberLevel")
    public MessageResult findAll(PageModel pageModel) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("id");
            pageModel.setProperty(property);
        }
        Page<MemberLevel> all = memberLevelService.getAllByPage((Predicate) null, pageModel.getPageable());
        MessageResult result = MessageResult.success("ok");
        result.setData(all);
        return result;
    }

    /**
     * 更新会员等级
     *
     * @param memberLevel
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @RequiresPermissions("member:member-level:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.MEMBER, operation = "更新等级MemberLevel")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult update(@Valid MemberLevel memberLevel, BindingResult bindingResult) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
//        memberLevel.setIsDefault(false);
        MemberLevel one = memberLevelService.findOne(memberLevel.getId());
        if (one == null) {
            return error("修改对象不存在");
        }
        List<MemberLevel> memberLevels = memberLevelService.findAll();
        for (MemberLevel level : memberLevels) {
            if (level.getId() != one.getId()) {
                if (level.getName().equals(memberLevel.getName())) {
                    return error("该名字已经存在");
                }
            }
        }
        memberLevelService.save(memberLevel);
        return success("ok");
    }

    /**
     * 新增等级
     *
     * @param memberLevel
     * @return
     */
    @RequiresPermissions("member:member-level:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.MEMBER, operation = "更新全部")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addOne(MemberLevel memberLevel) {
        memberLevel.setIsDefault(false);
        if (memberLevel.getId() == null) {
            return error("会员等级不可以为空");
        }
        if (StringUtils.isEmpty(memberLevel.getName())) {
            return error("会员名不可以为空");
        }

        List<MemberLevel> memberLevels = memberLevelService.findAll();
        for (MemberLevel level : memberLevels) {
            if (level.getId() == memberLevel.getId()) {
                return error("该等级已经存在");
            }
        }
        for (MemberLevel level : memberLevels) {
            if (level.getName().equals(memberLevel.getName())) {
                return error("该名字已经存在");
            }
        }
        memberLevelService.save(memberLevel);
        return MessageResult.success("ok");
    }

    @RequiresPermissions("member:member-level:updatedefault")
    @PostMapping("updatedefault")
    @AccessLog(module = AdminModule.MEMBER, operation = "更新默认")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateDefault(Long id) {
        MemberLevel memberLevel = memberLevelService.findOne(id);
        if (memberLevel == null) {
            return error("不存在此等级");
        }
        memberLevelService.updateDefault();
        memberLevel.setIsDefault(true);
//        memberLevelService。
        return success("ok");
    }


    /**
     * 获取下拉框会员等级
     *
     * @return
     */
    @RequiresPermissions("member:member-level:all")
    @PostMapping("getAllLevelValue")
    @AccessLog(module = AdminModule.MEMBER, operation = "获取下拉框会员等级")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult getAllLevelValue() {
        List<MemberLevel> memberLevels = memberLevelService.findAll();
        List<Map> list = new ArrayList<>();
        for (MemberLevel memberLevel : memberLevels) {
            Map map = new HashMap();
            map.put("label", memberLevel.getName());
            map.put("value", memberLevel.getId());
            list.add(map);
        }
        MessageResult result = MessageResult.success("ok");
        result.setData(list);
        return result;
    }

}
