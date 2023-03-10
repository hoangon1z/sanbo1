package com.bizzan.bitrade.controller.finance;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.ContractTransactionDao;
import com.bizzan.bitrade.entity.ContractTransaction;
import com.bizzan.bitrade.entity.QContractTransaction;
import com.bizzan.bitrade.screen.ContractTransactionScreen;
import com.bizzan.bitrade.service.ContractTransactionService;
import com.bizzan.bitrade.util.DateUtil;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("finance/contract-transaction")
public class ContractTransactionController {

    @Autowired
    private ContractTransactionService contractTransactionService;

    @RequestMapping("/page-query")
    public Page<ContractTransaction> pageQuery(PageModel pageModel, ContractTransactionScreen contractTransaction) {
        List<Predicate> predicates = new ArrayList<>();
        if (contractTransaction.getMemberId() != null) {
            predicates.add((QContractTransaction.contractTransaction.memberId.eq(contractTransaction.getMemberId())));
        }
        if (contractTransaction.getType() != null) {
            predicates.add(QContractTransaction.contractTransaction.type.eq(contractTransaction.getType()));
        }
        if (contractTransaction.getMinMoney() != null) {
            predicates.add(QContractTransaction.contractTransaction.amount.goe(contractTransaction.getMinMoney()));
        }
        if (contractTransaction.getMaxMoney() != null) {
            predicates.add(QContractTransaction.contractTransaction.amount.loe(contractTransaction.getMaxMoney()));
        }
        if (contractTransaction.getStartTime() != null) {
            predicates.add(QContractTransaction.contractTransaction.createTime.goe(contractTransaction.getStartTime()));
        }
        if (contractTransaction.getEndTime() != null) {
            predicates.add(QContractTransaction.contractTransaction.createTime.lt(DateUtil.dateAddDay(contractTransaction.getEndTime(), 1)));
        }
        Page<ContractTransaction> results = contractTransactionService.joinFind(predicates, pageModel);
        return results;
    }

}
