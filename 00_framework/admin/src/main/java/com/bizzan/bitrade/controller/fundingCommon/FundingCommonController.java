package com.bizzan.bitrade.controller.fundingCommon;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.FundingCommonRepostiory;
import com.bizzan.bitrade.entity.FundingCommon;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fundingCommon")
public class FundingCommonController {


    @Autowired
    private FundingCommonRepostiory commonRepostiory;


    /**
     * 分页查询评论
     *
     * @param pageModel
     * @return
     */
    @PostMapping("page-query")
    public MessageResult page(PageModel pageModel) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("commonTime");
            pageModel.setProperty(property);

        }
        //获取查询条件
        Page<FundingCommon> all = commonRepostiory.findAll((Predicate) null, pageModel.getPageable());
        //List<CrowdFunding> crowdFundings  =fundingRepository.findAll();
        MessageResult result = MessageResult.success("ok");
        result.setData(all);
        return result;
    }


    /**
     * 通过审核
     *
     * @param id
     * @return
     */
    @PostMapping("passCheck")
    public MessageResult passCheck(Long id) {
        FundingCommon fundingCommon = commonRepostiory.getById(id);
        fundingCommon.setStatus(1);
        commonRepostiory.saveAndFlush(fundingCommon);
        return MessageResult.success("ok");
    }

    /**
     * 拒绝审核
     *
     * @param id
     * @return
     */
    @PostMapping("delineCheck")
    public MessageResult delineCheck(Long id) {
        FundingCommon fundingCommon = commonRepostiory.getById(id);
        fundingCommon.setStatus(2);
        commonRepostiory.saveAndFlush(fundingCommon);
        return MessageResult.success("ok");
    }


}
