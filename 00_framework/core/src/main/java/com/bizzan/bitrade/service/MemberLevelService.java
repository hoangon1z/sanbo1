package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.entity.Member;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.MemberLevelDao;
import com.bizzan.bitrade.entity.MemberLevel;
import com.bizzan.bitrade.service.Base.BaseService;

import java.util.List;

/**
 * @author GS
 * @description
 * @date 2017/12/26 17:26
 */
@Service
public class MemberLevelService extends BaseService {
    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public List<MemberLevel> findAll() {
        return memberLevelDao.findAll();
    }


    //分页查询
    public Page<MemberLevel> getAllByPage(Predicate predicate, Pageable pageModel) {
        Page<MemberLevel> all = memberLevelDao.findAll(predicate, pageModel);
        return all;
    }

    public List<MemberLevel> findAllDesc() {
        return memberLevelDao.findAllDesc();
    }


    /**
     * @author GS
     * @description id查询一个
     * @date 2017/12/27 10:54
     */
    public MemberLevel findOne(Long id) {
        return memberLevelDao.findOne(id);
    }

    /**
     * @author GS
     * @description 查询默认会员的等级
     * @date 2017/12/26 17:58
     */
    public MemberLevel findDefault() {
        return memberLevelDao.findOneByIsDefault(true);
    }

    /**
     * @author GS
     * @description 更新状态为false 不包括
     * @date 2017/12/27 11:02
     */
    public int updateDefault() {
        return memberLevelDao.updateDefault();
    }

    public MemberLevel save(MemberLevel memberLevel) {
        return memberLevelDao.save(memberLevel);
    }
}
