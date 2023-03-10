package com.bizzan.bitrade.controller.mining;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.*;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("mining/mining")
public class MiningController extends BaseAdminController {
    @Autowired
    private MiningService miningService;

    /**
     * 分页查询
     *
     * @param pageModel
     * @return
     */
    @RequiresPermissions("mining:mining:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.MINING, operation = "分页查看矿机列表")
    public MessageResult list(PageModel pageModel) {
        if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<Mining> all = miningService.findAll(null, pageModel.getPageable());
        return success(all);
    }

    /**
     * 添加矿机
     *
     * @param mining
     * @return
     */
    @RequiresPermissions("mining:mining:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.MINING, operation = "新增矿机")
    public MessageResult add(@Valid Mining mining) {
        miningService.save(mining);
        return MessageResult.success();
    }


    @RequiresPermissions("mining:mining:modify")
    @PostMapping("modify")
    @AccessLog(module = AdminModule.MINING, operation = "修改矿机信息")
    public MessageResult alter(@Valid Mining mining) {
        if (mining != null) {
            miningService.saveAndFlush(mining);
            return MessageResult.success();
        }
        return error("参数错误");
    }

    @RequiresPermissions("mining:mining:updateshow")
    @PostMapping("updateshow")
    @AccessLog(module = AdminModule.MINING, operation = "修改矿机信息")
    public MessageResult updateShow(Long id) {
        Mining mining = miningService.findOne(id);
        if (mining != null && mining.getStatus() == BooleanEnum.IS_TRUE) {
            mining.setStatus(BooleanEnum.IS_FALSE);
            miningService.saveAndFlush(mining);
            return MessageResult.success("ok");
        }
        if (mining != null && mining.getStatus() == BooleanEnum.IS_FALSE) {
            mining.setStatus(BooleanEnum.IS_TRUE);
            miningService.saveAndFlush(mining);
            return MessageResult.success("ok");
        } else return error("不存在此矿机");
    }


    @PostMapping("getOne")
    @AccessLog(module = AdminModule.MINING, operation = "跳转修改页")
    public MessageResult getOneById(Long id) {
        Mining mining = miningService.findOne(id);
        if (mining != null) {
            MessageResult result = MessageResult.success("ok");
            result.setData(mining);
            return result;
        } else return error("不存在此矿机");


    }


}
