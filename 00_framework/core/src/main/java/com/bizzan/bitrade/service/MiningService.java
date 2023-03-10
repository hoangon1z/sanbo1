package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MiningDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.Mining;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MiningService extends BaseService {

    @Autowired
    private MiningDao miningDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MiningOrderService miningOrderService;


    public Mining findOne(Long id) {
        return miningDao.findOne(id);
    }

    public Mining save(Mining Mining) {
        return miningDao.save(Mining);
    }

    public Mining saveAndFlush(Mining Mining) {
        return miningDao.saveAndFlush(Mining);
    }

    public Mining findById(Long id) {
        return miningDao.findOne(id);
    }

    public Page<Mining> findAll(Predicate predicate, Pageable pageable) {
        return miningDao.findAll(predicate, pageable);
    }

    public Page<Mining> queryAll(int pageNo, int pageSize) {
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<Mining> specification = new Criteria<Mining>();
        specification.add(Restrictions.eq("status", 1, false));
        return miningDao.findAll(specification, pageRequest);
    }


}
