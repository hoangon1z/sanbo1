package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.dao.ContractOrderEntrustRepository;
import com.bizzan.bitrade.dao.ContractOrderRepository;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Bidi;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ContractOrderEntrustService {
    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private ContractOrderEntrustRepository contractOrderEntrustRepository;

    @Autowired
    private ContractOrderRepository contractOrderRepository;

    @Autowired
    private ContractOrderService contractOrderService;

    @Autowired
    private ContractCoinService contractCoinService;

    @Autowired
    private ContractWalletService contractWalletService;

    @Autowired
    private MemberWalletService memberWalletService;

    /**
     * 保存合约委托
     *
     * @param contractOrderEntrust
     * @return
     */
    public ContractOrderEntrust save(ContractOrderEntrust contractOrderEntrust) {
        return contractOrderEntrustRepository.save(contractOrderEntrust);
    }

    public ContractOrderEntrust findOne(long id) {
        return contractOrderEntrustRepository.findOne(id);
    }

    public ContractOrderEntrust findByContractOrderEntrustId(String eid) {
        return contractOrderEntrustRepository.findByContractOrderEntrustId(eid);
    }

    public Page<ContractOrderEntrust> findAll(Predicate predicate, Pageable pageable) {
        return contractOrderEntrustRepository.findAll(predicate, pageable);
    }

    /**
     * 查询所有需要处理的委托
     *
     * @return
     */
    public List<ContractOrderEntrust> findAllNeedMatch() {
        return contractOrderEntrustRepository.findAllNeedMatch();
    }

    /**
     * 查询所有需要处理的委托
     *
     * @return
     */
    public List<ContractOrderEntrust> findAllNeedMatch(Long memberId) {
        return contractOrderEntrustRepository.findAllNeedMatch(memberId);
    }

    /**
     * 查询所有需要处理的委托
     *
     * @return
     */
    public List<ContractOrderEntrust> findAllNeedMatch(Long memberId, String symbol) {
        return contractOrderEntrustRepository.findAllNeedMatch(memberId, symbol);
    }

    /**
     * 查询所有需要处理的委托
     *
     * @return
     */
    public List<ContractOrderEntrust> findAllNeedMatchBySymbol(String symbol) {
        return contractOrderEntrustRepository.findAllNeedMatchBySymbol(symbol);
    }

    /**
     * 委托成功转持仓
     *
     * @param entrust
     * @param price
     */
    @Transactional
    public ContractOrder handle(ContractOrderEntrust entrust, BigDecimal price) {
        log.info("处理委托单:" + entrust.getId());
        ContractOrderEntrust entrustDB = findOne(entrust.getId()); //再查询一次状态
        if (entrustDB.getStatus() != ContractOrderEntrustStatus.ENTRUST_ING) {
            log.info("待处理委托单出现严重错误，请排查:" + entrust.getId());
            return null;
        }
        entrust.setStatus(ContractOrderEntrustStatus.ENTRUST_SUCCESS);
        entrust.setStrikePrice(price);
        entrust.setStrikeTime(Calendar.getInstance().getTimeInMillis());
        MessageResult mr;
        ContractOrder order;
        if (entrust.getEntrustType() == ContractOrderEntrustType.OPEN) {
            mr = contractOrderService.open(entrust, price);//开仓
            order = (ContractOrder) mr.getData();
            entrust.setFee(order.getFee());
        } else {
            mr = contractOrderService.close(entrust, price); //平仓
            order = (ContractOrder) mr.getData();
            entrust.setProfit(order.getProfit());
        }
        if (mr.getCode() == 0) {
            contractOrderEntrustRepository.saveAndFlush(entrust);
        } else {
            throw new RuntimeException(mr.getMessage());
        }
        return order;
    }

    /**
     * 添加委托单(以下代码为开仓限价)
     *
     * @param memberId
     * @param entrust
     * @return
     */
    @Transactional
    public MessageResult addOrder(long memberId, ContractOrderEntrust entrust) {
        ContractCoin contractCoin = contractCoinService.findOne(entrust.getContractCoinId());
        entrust.setShareNumber(contractCoin.getShareNumber());
        entrust.setContractCoinType(contractCoin.getType());
        entrust.setContractCoinName(contractCoin.getName());
        entrust.setSymbol(contractCoin.getSymbol());
        entrust.setBaseSymbol(contractCoin.getBaseSymbol());
        entrust.setCoinSymbol(contractCoin.getCoinSymbol());
        entrust.setMemberId(memberId);
        entrust.setStatus(ContractOrderEntrustStatus.ENTRUST_ING); //委托中
        entrust.setCreateTime(Calendar.getInstance().getTimeInMillis());
        entrust.setContractOrderEntrustId(GeneratorUtil.getOrderId("O"));
        entrust.setPrincipalUnit(entrust.getBaseSymbol());
        entrust.setFeeSymbol(entrust.getBaseSymbol());
        //计算价格
        Integer share = entrust.getShare(); //手数
        BigDecimal shareNumber = contractCoin.getShareNumber(); //单手价格
        BigDecimal total = shareNumber.multiply(BigDecimal.valueOf(share)); // 总价格
        BigDecimal leverage = entrust.getLeverage();//杠杆
        BigDecimal principalAmount = total.divide(leverage, contractCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
        entrust.setPrincipalAmount(entrust.getEntrustType() == ContractOrderEntrustType.OPEN ? principalAmount : BigDecimal.ZERO);
        if (entrust.getEntrustType() == ContractOrderEntrustType.OPEN) { //开仓
            ContractWallet wallet = contractWalletService.findByCoinUnitAndMemberId(entrust.getBaseSymbol(), memberId);
            if (wallet.getIsLock().equals(BooleanEnum.IS_TRUE)) {
                return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
            }
            if (wallet.getBalance().compareTo(principalAmount) < 0) {
                return MessageResult.error(500, msService.getMessage("INSUFFICIENT_COIN") + entrust.getBaseSymbol());
            }
            //冻结金额 todo 这里是否需要乐观锁
            MessageResult freezeResult = contractWalletService.freezeBalance(wallet, principalAmount);
            if (freezeResult.getCode() != 0) {
                return MessageResult.error(500, msService.getMessage("INSUFFICIENT_COIN") + entrust.getBaseSymbol());
            }
        } else { //平仓，修改可平金额
            ContractOrderDirection direction = (entrust.getDirection() == ContractOrderDirection.BUY) ? ContractOrderDirection.SELL : ContractOrderDirection.BUY;
            ContractOrder order = contractOrderRepository.findByMemberIdAndContractCoinIdAndStatusAndDirection(entrust.getMemberId(), entrust.getContractCoinId(), ContractOrderStatus.MARKET_IN_THE_POSITION, direction);
            assert order != null;
            if (order.getAvailablePosition() < entrust.getShare()) {
                return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
            }
            order.setAvailablePosition(order.getAvailablePosition() - entrust.getShare());
            contractOrderRepository.save(order);
        }
        log.info("add contract-entrust:{}", entrust);
        ContractOrderEntrust save = contractOrderEntrustRepository.saveAndFlush(entrust);
        MessageResult result = MessageResult.success("success");
        result.setData(save);
        return result;
    }

    /**
     * 查询当前交易中的委托
     *
     * @param uid
     * @param type
     * @param entrustType
     * @param direction
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<ContractOrderEntrust> findCurrent(Long uid, Long contractCoinId, ContractOrderType type, ContractOrderEntrustType entrustType, ContractOrderDirection direction, String startTime, String endTime, int pageNo, int pageSize) {
        Sort orders = new Sort(new Sort.Order(Sort.Direction.DESC, "createTime"));
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        Criteria<ContractOrderEntrust> specification = new Criteria<ContractOrderEntrust>();
        if (contractCoinId != null) {
            specification.add(Restrictions.eq("contractCoinId", contractCoinId, true));
        }
        if (direction != null) {
            specification.add(Restrictions.eq("direction", direction, true));
        }
        if (entrustType != null) {
            specification.add(Restrictions.eq("entrustType", entrustType, true));
        }
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            specification.add(Restrictions.gte("createTime", Long.valueOf(startTime), true));
            specification.add(Restrictions.lte("createTime", Long.valueOf(endTime), true));
        }
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.eq("status", ContractOrderEntrustStatus.ENTRUST_ING, false));
        if (type == ContractOrderType.SPOT_LIMIT) {
            specification.add(Restrictions.eq("type", ContractOrderType.SPOT_LIMIT, true));
        } else {
            specification.add(Restrictions.ne("type", ContractOrderType.SPOT_LIMIT, true));
        }
        Page<ContractOrderEntrust> page = contractOrderEntrustRepository.findAll(specification, pageRequest);
        return page;
    }

    /**
     * 查询历史委托
     *
     * @param uid
     * @param entrustType
     * @param direction
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<ContractOrderEntrust> findHistory(Long uid, Long contractCoinId, ContractOrderType type, ContractOrderEntrustType entrustType, ContractOrderDirection direction, String startTime, String endTime, int pageNo, int pageSize) {
        Sort orders = new Sort(new Sort.Order(Sort.Direction.DESC, "createTime"));
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        Criteria<ContractOrderEntrust> specification = new Criteria<ContractOrderEntrust>();
        if (contractCoinId != null) {
            specification.add(Restrictions.eq("contractCoinId", contractCoinId, true));
        }
        if (direction != null) {
            specification.add(Restrictions.eq("direction", direction, true));
        }
        if (entrustType != null) {
            specification.add(Restrictions.eq("entrustType", entrustType, true));
        }
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            specification.add(Restrictions.gte("createTime", Long.valueOf(startTime), true));
            specification.add(Restrictions.lte("createTime", Long.valueOf(endTime), true));
        }
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.ne("status", ContractOrderEntrustStatus.ENTRUST_ING, false));
        if (type == ContractOrderType.SPOT_LIMIT) {
            specification.add(Restrictions.eq("type", ContractOrderType.SPOT_LIMIT, true));
        } else {
            specification.add(Restrictions.ne("type", ContractOrderType.SPOT_LIMIT, true));
        }
        return contractOrderEntrustRepository.findAll(specification, pageRequest);
    }

    @Transactional
    public MessageResult cancel(Long id) {
        ContractOrderEntrust entrust = contractOrderEntrustRepository.findOne(id);
        if (entrust.getStatus() != ContractOrderEntrustStatus.ENTRUST_ING) {
            return MessageResult.error(500, "操作错误");
        }
        entrust.setStatus(ContractOrderEntrustStatus.ENTRUST_CANCEL);
        if (entrust.getEntrustType() == ContractOrderEntrustType.OPEN) { //开仓
            ContractWallet wallet = contractWalletService.findByCoinUnitAndMemberId(entrust.getBaseSymbol(), entrust.getMemberId());
            //冻结金额 todo 这里是否需要乐观锁
            MessageResult freezeResult = contractWalletService.thawBalance(wallet, entrust.getPrincipalAmount());
            if (freezeResult.getCode() != 0) {
                return MessageResult.error(500, msService.getMessage("INSUFFICIENT_COIN") + entrust.getBaseSymbol());
            }
            /** 退还保证金 kickout **/
            if (entrust.getFee() != null && entrust.getFee().compareTo(BigDecimal.ZERO) > 0) {
                MemberWallet feeWallet = memberWalletService.findByCoinUnitAndMemberId("KICK", entrust.getMemberId());
                memberWalletService.kickIncreaseBalance(feeWallet.getId(), entrust.getFee());
                entrust.setFee(BigDecimal.ZERO);
            }
            /** 退还保证金 kickout end**/
        } else { //平仓，修改可平金额
            ContractOrderDirection direction = (entrust.getDirection() == ContractOrderDirection.BUY) ? ContractOrderDirection.SELL : ContractOrderDirection.BUY;
            ContractOrder order = contractOrderRepository.findByMemberIdAndContractCoinIdAndStatusAndDirection(entrust.getMemberId(), entrust.getContractCoinId(), ContractOrderStatus.MARKET_IN_THE_POSITION, direction);
            assert order != null;
            order.setAvailablePosition(order.getAvailablePosition() + entrust.getShare());
            contractOrderRepository.save(order);
        }
        contractOrderEntrustRepository.save(entrust);
        MessageResult success = MessageResult.success();
        success.setData(entrust.getSymbol());
        return success;
    }

    /**
     * 闪电平仓（增加限价委托单）
     *
     * @param memberId
     * @param orderId
     * @return
     */
    public MessageResult quickClose(long memberId, Long orderId) {
        ContractOrder order = contractOrderRepository.getOne(orderId);
        if (order.getMemberId() != memberId) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        ContractOrderEntrust entrust = new ContractOrderEntrust();
        entrust.setContractCoinId(order.getContractCoinId());
        entrust.setEntrustType(ContractOrderEntrustType.CLOSE);
        entrust.setType(ContractOrderType.LIMIT_PRICE);
        entrust.setPatterns(ContractOrderPattren.CROSSED);
        entrust.setLeverage(order.getMultiple());
        entrust.setMarketPrice(true);
        entrust.setEntrustPrice(null);
        entrust.setShare(order.getAvailablePosition());
        entrust.setDirection(order.getDirection() == ContractOrderDirection.BUY ? ContractOrderDirection.SELL : ContractOrderDirection.BUY);
        return addOrder(memberId, entrust);
    }
}
