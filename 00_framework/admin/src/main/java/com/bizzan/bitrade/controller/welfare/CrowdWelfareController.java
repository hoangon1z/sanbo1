package com.bizzan.bitrade.controller.welfare;


import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.CrowdWelfareRepostiory;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.FundingScreen;
import com.bizzan.bitrade.model.screen.WelfareScreen;
import com.bizzan.bitrade.service.CrowdWelfareService;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.constant.PageModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/welfare")
public class CrowdWelfareController {

    @Autowired
    private CrowdWelfareRepostiory welfareRepostiory;
    @Autowired
    private CrowdWelfareService welfareService;


    /**
     * 带条件分页查询
     *
     * @param pageModel
     * @param screen
     * @return
     */
    @PostMapping("querywelfare")
    public MessageResult querywelfare(PageModel pageModel, WelfareScreen screen) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("addTime");
            pageModel.setProperty(property);
        }
        //获取查询条件
        Predicate predicate = getPredicate(screen);
        Page<CrowdWelfare> all = welfareRepostiory.findAll(predicate, pageModel.getPageable());
        //List<CrowdFunding> crowdFundings  =fundingRepository.findAll();
        MessageResult result = MessageResult.success("ok");
        result.setData(all);
        return result;
    }

    private Predicate getPredicate(WelfareScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QCrowdWelfare qCrowdWelfare = QCrowdWelfare.crowdWelfare;
        if (screen.getId() != null) {
            booleanExpressions.add(qCrowdWelfare.id.eq(screen.getId()));
        }
        if (StringUtils.isNotEmpty(screen.getFundingTitle())) {
            booleanExpressions.add(qCrowdWelfare.fundingTitle.like(screen.getFundingTitle()));
        }
        if (screen.getMemberId() != null) {
            booleanExpressions.add(qCrowdWelfare.memberId.eq(screen.getMemberId()));
        }
        if (screen.getStatus() != null) {
            booleanExpressions.add(qCrowdWelfare.status.eq(screen.getStatus()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);

    }

    /**
     * 通过线下公益审核
     *
     * @param mid   用户id
     * @param money 打款金额
     * @param wid   公益id
     * @return
     */
    @PostMapping("passwelfare")
    public MessageResult passWelfare(Long mid, BigDecimal money, Long wid) {
        welfareService.passWelfare(mid, money, wid);
        return MessageResult.success("ok");

    }

    /**
     * 拒绝线下公益审核
     *
     * @param wid  公益id
     * @param text 拒绝理由
     * @return
     */
    @PostMapping("refucewelfare")
    public MessageResult refuceWelfare(Long wid, String text) {
        welfareService.refuceWelfare(wid, text);
        return MessageResult.success("ok");

    }


}
