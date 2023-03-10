package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberLevel;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author GS
 * @description 会员等级Dao
 * @date 2017/12/26 17:24
 */
public interface MemberLevelDao extends BaseDao<MemberLevel> {

    MemberLevel findOneByIsDefault(Boolean isDefault);


    @Query("update MemberLevel set isDefault = false  where isDefault = true ")
    @Modifying
    int updateDefault();

    /**
     * 倒叙查找
     *
     * @return
     */
    @Query(value = "select * from member_level ORDER by id desc", nativeQuery = true)
    List<MemberLevel> findAllDesc();
}
