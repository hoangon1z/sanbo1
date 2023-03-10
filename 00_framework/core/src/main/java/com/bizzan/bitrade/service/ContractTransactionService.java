package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.ContractTransactionType;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.ContractTransactionDao;
import com.bizzan.bitrade.dao.MemberTransactionDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.vo.MemberTransactionVO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeJava.from;

@Service
public class ContractTransactionService extends BaseService {
    @Autowired
    private ContractTransactionDao contractTransactionDao;

    /**
     * 生成一条新的账单
     *
     * @param memberId
     * @param symbol
     * @param amount
     * @param type
     */
    public void record(Long memberId, String symbol, BigDecimal amount, ContractTransactionType type) {
        ContractTransaction contractTransaction = new ContractTransaction();
        contractTransaction.setAmount(amount);
        contractTransaction.setMemberId(memberId);
        contractTransaction.setSymbol(symbol);
        contractTransaction.setType(type);
        Date date = new Date();
        contractTransaction.setCreateTime(date);
        save(contractTransaction);
    }

    /**
     * 根据userId查询账单信息
     *
     * @param uid
     * @param pageNo
     * @param pageSize
     * @param type
     * @param startDate
     * @param endDate
     * @param symbol
     * @return
     * @throws ParseException
     */
    public Page<ContractTransaction> queryByMember(Long uid, Integer pageNo, Integer pageSize, ContractTransactionType type, String startDate, String endDate, String symbol) throws ParseException {
        //控制排序
        Sort orders = Criteria.sortStatic("createTime.desc");
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<ContractTransaction> specification = new Criteria<ContractTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        if (type != null) {
            specification.add(Restrictions.eq("type", type, false));
        }

        //开始时间结束时间不为空时
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            specification.add(Restrictions.gte("createTime", DateUtil.YYYY_MM_DD_MM_HH_SS.parse(startDate + " 00:00:00"), false));
            specification.add(Restrictions.lte("createTime", DateUtil.YYYY_MM_DD_MM_HH_SS.parse(endDate + " 23:59:59"), false));
        }
        //symbol不是空时
        if (StringUtils.isNotEmpty(symbol)) {
            specification.add(Restrictions.eq("symbol", symbol, false));
        }
        return contractTransactionDao.findAll(specification, pageRequest);
    }

    //重载
    public List<ContractTransaction> queryByMember(Long uid, ContractTransactionType type) {
        Criteria<ContractTransaction> specification = new Criteria<ContractTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.eq("type", type, false));
        return contractTransactionDao.findAll(specification);
    }

    public Page<ContractTransaction> queryByMember(Long uid, Integer pageNo, Integer pageSize, ContractTransactionType type) {
        //排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<ContractTransaction> specification = new Criteria<ContractTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.eq("type", type, false));
        return contractTransactionDao.findAll(specification, pageRequest);
    }

    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param booleanExpressionList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<ContractTransaction> queryWhereOrPage(List<BooleanExpression> booleanExpressionList, Integer pageNo, Integer pageSize) {
        List<ContractTransaction> list;
        JPAQuery<ContractTransaction> jpaQuery = queryFactory.selectFrom(QContractTransaction.contractTransaction);
        if (booleanExpressionList != null) {
            jpaQuery.where(booleanExpressionList.toArray(new BooleanExpression[booleanExpressionList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }

    /**
     * 保存交易记录
     *
     * @param transaction
     * @return
     */
    public ContractTransaction save(ContractTransaction transaction) {
        return contractTransactionDao.save(transaction);
    }

    @Override
    public List<ContractTransaction> findAll() {
        return contractTransactionDao.findAll();
    }

    /**
     * 查询单个记录
     *
     * @param id
     * @return
     */
    public ContractTransaction findOne(Long id) {
        ContractTransaction contractTransaction = contractTransactionDao.findOne(id);
        return contractTransaction;
    }


    public Page<ContractTransaction> joinFind(List<Predicate> predicates, PageModel pageModel) {
        List<OrderSpecifier> orderSpecifiers = pageModel.getOrderSpecifiers();
        JPAQuery<ContractTransaction> query = queryFactory.select(Projections.fields(ContractTransaction.class,
                QContractTransaction.contractTransaction.amount,
                QContractTransaction.contractTransaction.symbol,
                QContractTransaction.contractTransaction.id.as("id"),
                QContractTransaction.contractTransaction.createTime.as("createTime"),
                QContractTransaction.contractTransaction.type,
                QContractTransaction.contractTransaction.memberId))
                .from(QContractTransaction.contractTransaction);
//        predicates.add(QContractTransaction.contractTransaction.memberId.eq(QMember.member.id));
        query.where(predicates.toArray(new BooleanExpression[predicates.size()]));
        query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
        List<ContractTransaction> list = query.offset((pageModel.getPageNo() - 1) * pageModel.getPageSize()).limit(pageModel.getPageSize()).fetch();
        long total = query.fetchCount();
        return new PageImpl<>(list, pageModel.getPageable(), total);
    }
}
