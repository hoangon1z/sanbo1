package com.bizzan.bitrade.controller.mining;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Mining;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.service.MiningOrderService;
import com.bizzan.bitrade.service.MiningService;
import com.bizzan.bitrade.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("mining/order")
public class MiningOrderController extends BaseAdminController {
    @Autowired
    private MiningOrderService miningOrderService;

    /**
     * 分页查询
     *
     * @param pageModel
     * @return
     */
    @RequiresPermissions("mining:order:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.MINING, operation = "分页查看矿机订单列表")
    public MessageResult list(PageModel pageModel) {
        if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<MiningOrder> all = miningOrderService.findAll(null, pageModel.getPageable());
        return success(all);
    }


}
