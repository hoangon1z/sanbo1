package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.TransferDao;
import com.bizzan.bitrade.dao.TransferRecordDao;
import com.bizzan.bitrade.entity.Transfer;
import com.bizzan.bitrade.entity.TransferRecord;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wmf
 * @date 2020年10月26日10:45:01
 */
@Service
public class TransferService extends BaseService {
    @Autowired
    private TransferDao transferDao;

    public Transfer save(Transfer transfer) {
        return transferDao.save(transfer);
    }

    @Transactional(readOnly = true)
    public Page<Transfer> findAllByMemberId(Long memberId, int page, int pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(page, pageSize, orders);
        Criteria<Transfer> specification = new Criteria<>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        return transferDao.findAll(specification, pageRequest);
    }
}
