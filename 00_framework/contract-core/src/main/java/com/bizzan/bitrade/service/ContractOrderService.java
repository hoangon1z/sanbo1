package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.ContractTransactionType;
import com.bizzan.bitrade.constant.TransferDirection;
import com.bizzan.bitrade.dao.ContractOrderRepository;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.FormulaUtil;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vo.PositionVO;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ContractOrderService extends BaseService {

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private ContractOrderRepository contractOrderRepository;

    @Autowired
    private ContractCoinService contractCoinService;

    @Autowired
    private ContractWalletService contractWalletService;

    @Autowired
    private ContractTransactionService contractTransactionService;

    @Autowired
    private ContractOrderEntrustService contractOrderEntrustService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private ContractMemberProfitService contractMemberProfitService;

    public Page<ContractOrder> findAll(Predicate predicate, Pageable pageable) {
        return contractOrderRepository.findAll(predicate, pageable);
    }

    public ContractOrder findOne(long id) {
        return contractOrderRepository.findOne(id);
    }

    /**
     * 开仓
     *
     * @param entrust
     * @param price
     */
    @Transactional
    public MessageResult open(ContractOrderEntrust entrust, BigDecimal price) {
        ContractOrder order = null;
        if (entrust.getContractCoinType() == ContractCoinType.ALWAYS) {
            ContractOrder old = contractOrderRepository.findByMemberIdAndContractCoinIdAndStatusAndDirection(entrust.getMemberId(), entrust.getContractCoinId(), ContractOrderStatus.MARKET_IN_THE_POSITION, entrust.getDirection());
            if (old != null) {
                order = old;
            }
        }
        if (order != null) {// 已有当前仓位+永续合约
            BigDecimal divide = FormulaUtil.getAvgOpenPrice(order.getOpenPrice(), order.getPosition(), entrust.getStrikePrice(), entrust.getShare());
            order.setPosition(order.getPosition() + entrust.getShare());
            order.setAvailablePosition(order.getAvailablePosition() + entrust.getShare());
            order.setOpenPrice(divide); //开仓均价
            order.setPrincipalAmount(order.getPrincipalAmount().add(entrust.getPrincipalAmount()));
            order.setContractOrderEntrustId(entrust.getContractOrderEntrustId()); //覆盖
        } else {
            order = new ContractOrder();
            order.setContractOrderId(GeneratorUtil.getOrderId("C"));
            order.setContractOrderEntrustId(entrust.getContractOrderEntrustId());
            order.setContractCoinId(entrust.getContractCoinId());
            order.setContractCoinName(entrust.getContractCoinName());
            order.setContractCoinType(entrust.getContractCoinType());
            order.setShareNumber(entrust.getShareNumber());
            order.setSymbol(entrust.getSymbol());
            order.setBaseSymbol(entrust.getBaseSymbol());
            order.setCoinSymbol(entrust.getCoinSymbol());
            order.setDirection(entrust.getDirection());
            order.setCreateTime(Calendar.getInstance().getTimeInMillis());
            order.setMemberId(entrust.getMemberId());
            order.setOpenPrice(price);
            order.setPatterns(entrust.getPatterns());
            order.setMultiple(entrust.getLeverage()); // 杠杆
            order.setPosition(entrust.getShare()); // 持仓数量
            order.setAvailablePosition(entrust.getShare()); // 可平仓数量
            order.setPrincipalUnit(entrust.getPrincipalUnit());
            order.setPrincipalAmount(entrust.getPrincipalAmount());
            if (entrust.getHoldTime() != null) {
                order.setHoldTime(entrust.getHoldTime());
                order.setPlanCloseTime(order.getCreateTime() + (entrust.getHoldTime() * 1000)); //设置自动平仓时间
            }
            order.setStatus(ContractOrderStatus.MARKET_IN_THE_POSITION);
        }

        ContractCoin contractCoin = contractCoinService.findOne(entrust.getContractCoinId());
        BigDecimal marginRate = contractCoin.getMaintenanceMarginRate();
        //强平点位
        BigDecimal point = (marginRate.subtract(BigDecimal.valueOf(1))).divide(order.getMultiple(), contractCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN).multiply(order.getOpenPrice());
        if (order.getDirection() == ContractOrderDirection.BUY) { //做多
            order.setLiquidationPrice(point.add(order.getOpenPrice()));
        } else { //做空
            order.setLiquidationPrice(order.getOpenPrice().subtract(point));
        }
        openHandler(order, entrust.getShare());
        ContractOrder re = contractOrderRepository.save(order);
        MessageResult success = MessageResult.success("success");
        success.setData(re);
        return success;
    }

    /**
     * 开仓金额处理
     *
     * @param order
     * @param share
     */
    private void openHandler(ContractOrder order, Integer share) {
        ContractCoin contractCoin = contractCoinService.findOne(order.getContractCoinId());
//        ContractWallet wallet = contractWalletService.findByCoinUnitAndMemberId(order.getBaseSymbol(), order.getMemberId()); kickout
        MemberWallet feeWallet = memberWalletService.findByCoinUnitAndMemberId("KICK", order.getMemberId()); // kickout
        //开仓 计算手续费
        BigDecimal rate = contractCoin.getTakerFee() != null ? contractCoin.getTakerFee() : contractCoin.getOpenFee();
        BigDecimal fee = contractCoin.getShareNumber().multiply(BigDecimal.valueOf(share)).divide(order.getMultiple(), 4, BigDecimal.ROUND_DOWN).multiply(rate);
        order.setFee(fee);
        //减少余额
//        contractWalletService.increaseBalance(wallet.getId(), fee.negate()); kickout

    }

    /**
     * 平仓
     *
     * @param entrust
     * @param price
     */
    @Transactional
    public MessageResult close(ContractOrderEntrust entrust, BigDecimal price) {
        ContractOrderDirection direction = (entrust.getDirection() == ContractOrderDirection.SELL) ? ContractOrderDirection.BUY : ContractOrderDirection.SELL;
        return close(entrust.getContractCoinId(), entrust.getMemberId(), direction, entrust.getShare(), price, false);
    }

    /**
     * 强制平仓,包括秒合约的清仓,这种状态下没有平仓委托单，收益显示在最后一个开仓委托单上
     *
     * @param order
     * @param price
     */
    @Transactional
    public MessageResult close(ContractOrder order, BigDecimal price, Boolean force) {
        if (order.getStatus() != ContractOrderStatus.MARKET_IN_THE_POSITION) {
            return MessageResult.error("订单非当前持仓单");
        }
        if (order == null) {
            return MessageResult.error("仓位出现错误，已无可平仓位");
        }
        BigDecimal returnPrincipalAmount;
        returnPrincipalAmount = order.getPrincipalAmount(); // 保证金全部退还
        BigDecimal profit = closeHandler(order, returnPrincipalAmount, order.getPosition(), price);
        order.setClosePrice(price); // 强制平仓价格
        order.setPosition(0); //剩余手数
        order.setPrincipalAmount(BigDecimal.ZERO); //全部清除保证金
        order.setStatus(force ? ContractOrderStatus.MARKET_FORCED_CLOSE_OUT : ContractOrderStatus.MARKET_USER_CLOSE_OUT);
        order.setCloseTime(Calendar.getInstance().getTimeInMillis());
        ContractOrder re = contractOrderRepository.save(order);
        if (order.getContractCoinType().equals(ContractCoinType.SECOND)) { // 秒合约收益记录再开仓委托上
            ContractOrderEntrust entrust = contractOrderEntrustService.findByContractOrderEntrustId(order.getContractOrderEntrustId()); // 委托单记录收益，只针对秒合约有效
            entrust.setProfit(profit);
            contractOrderEntrustService.save(entrust);
        }
        MessageResult success = MessageResult.success("success");
        success.setData(re);
        return success;
    }

    /**
     * 平仓
     *
     * @param contractCoinId
     * @param memberId
     * @param direction
     * @param share
     * @param price
     * @return
     */
    @Transactional
    public MessageResult close(Long contractCoinId, long memberId, ContractOrderDirection direction, Integer share, BigDecimal price, Boolean force) {
        ContractOrder order = contractOrderRepository.findByMemberIdAndContractCoinIdAndStatusAndDirection(memberId, contractCoinId, ContractOrderStatus.MARKET_IN_THE_POSITION, direction);
        if (order.getStatus() != ContractOrderStatus.MARKET_IN_THE_POSITION) {
            return MessageResult.error("订单非当前持仓单");
        }
        if (!order.getMemberId().equals(memberId)) {
            return MessageResult.error("出现严重错误");
        }
        if (order == null) {
            return MessageResult.error("仓位出现错误，已无可平仓位");
        }
        if (order.getPosition() < share) { //仓位不足 todo 拆单情况
            return MessageResult.error("仓位出现错误，仓位不足");
        }
        ContractCoin contractCoin = contractCoinService.findOne(contractCoinId);
        BigDecimal returnPrincipalAmount;
        order.setPosition(order.getPosition() - share); //剩余手数
        if (order.getPosition() == 0) { //订单标记完成
            order.setStatus(force ? ContractOrderStatus.MARKET_FORCED_CLOSE_OUT : ContractOrderStatus.MARKET_USER_CLOSE_OUT);
            returnPrincipalAmount = order.getPrincipalAmount(); // 保证金全部退还
            order.setPrincipalAmount(BigDecimal.ZERO); //全部清除保证金
            order.setCloseTime(Calendar.getInstance().getTimeInMillis());
        } else {
            BigDecimal principalAmount = order.getPrincipalAmount(); //原保证金
            returnPrincipalAmount = contractCoin.getShareNumber().multiply(BigDecimal.valueOf(share)).divide(order.getMultiple(), contractCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN); // 返还保证金
            order.setPrincipalAmount(principalAmount.subtract(returnPrincipalAmount)); //剩余保证金
        }
        closeHandler(order, returnPrincipalAmount, share, price);
        ContractOrder re = contractOrderRepository.save(order);
        MessageResult success = MessageResult.success("success");
        success.setData(re);
        return success;
    }

    /**
     * 平仓的后续计算(余额收益)
     *
     * @param order
     * @param returnPrincipalAmount
     */
    private BigDecimal closeHandler(ContractOrder order, BigDecimal returnPrincipalAmount, Integer share, BigDecimal closePrice) {
//        ContractCoin contractCoin = contractCoinService.findBySymbol(order.getSymbol());
        ContractWallet wallet = contractWalletService.findByCoinUnitAndMemberId(order.getBaseSymbol(), order.getMemberId());
        //解冻钱包
        MessageResult freezeResult = contractWalletService.thawBalance(wallet, returnPrincipalAmount);
        if (freezeResult.getCode() != 0) {
            throw new RuntimeException(msService.getMessage("INSUFFICIENT_COIN") + order.getBaseSymbol());
        }
        //计算收益
        BigDecimal profit = computeProfit(order.getContractCoinId(), order.getOpenPrice(), closePrice, share, order.getDirection());
        //记录收益
        order.setProfit(profit);
        //增加余额 + 收益
        contractWalletService.increaseBalance(wallet.getId(), profit);
        contractTransactionService.record(order.getMemberId(), order.getBaseSymbol(), profit, ContractTransactionType.CONUT);
        contractMemberProfitService.record(order, profit);
        return profit;
    }

    @Test
    public void computeProfit() {
        BigDecimal profit = BigDecimal.valueOf(10883.33).subtract(BigDecimal.valueOf(10881.97)).multiply(BigDecimal.valueOf(10)).multiply(BigDecimal.ONE).divide(BigDecimal.valueOf(10881.97), 8, BigDecimal.ROUND_DOWN);
        System.out.println(profit);
    }


    /**
     * 计算收益
     *
     * @param contractCoinId
     * @param openPirce
     * @param closePrice
     * @param share
     * @param direction
     * @return
     */
    public BigDecimal computeProfit(Long contractCoinId, BigDecimal openPirce, BigDecimal closePrice, Integer share, ContractOrderDirection direction) {
        ContractCoin contractCoin = contractCoinService.findOne(contractCoinId);
        if (direction == ContractOrderDirection.BUY) { // 平多仓
            BigDecimal profit = closePrice.subtract(openPirce).multiply(BigDecimal.valueOf(share)).multiply(contractCoin.getShareNumber()).divide(openPirce, contractCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            return profit;
        } else { // 平空仓
            BigDecimal profit = openPirce.subtract(closePrice).multiply(BigDecimal.valueOf(share)).multiply(contractCoin.getShareNumber()).divide(openPirce, contractCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            return profit;
        }
    }

    /**
     * 查询所有需要根据价格监控的持仓
     *
     * @return
     */
    public List<ContractOrder> findAllNeedMatch() {
        return contractOrderRepository.findAllNeedMatch();
    }

    /**
     * 查询所有需要根据价格监控的持仓
     *
     * @return
     */
    public List<ContractOrder> findAllNeedMatch(Long memberId) {
        return contractOrderRepository.findAllNeedMatch(memberId);
    }

    /**
     * 查询所有需要根据价格监控的持仓
     *
     * @return
     */
    public List<ContractOrder> findAllNeedMatch(Long memberId, String symbol) {
        return contractOrderRepository.findAllNeedMatch(memberId, symbol);
    }

    /**
     * 查询某币种所有需要根据价格监控的持仓
     *
     * @return
     */
    public List<ContractOrder> findAllNeedMatchBySymbol(String symbol) {
        return contractOrderRepository.findAllNeedMatchBySymbol(symbol);
    }

    /**
     * 查询用户当前持仓
     *
     * @param memberId
     * @param contractCoinId
     */
    public List<ContractOrder> findCurrent(long memberId, long contractCoinId) {
        return contractOrderRepository.findAllByMemberIdAndContractCoinIdAndStatus(memberId, contractCoinId, ContractOrderStatus.MARKET_IN_THE_POSITION);
//        ArrayList<PositionVO> list = new ArrayList<>();
//        //多仓
//        List<ContractOrder> buys = contractOrderRepository.findAllByMemberIdAndSymbolAndStatusAndDirection(memberId, symbol, ContractOrderStatus.MARKET_IN_THE_POSITION, ContractOrderDirection.BUY);
//        if (buys.size()>0) {
//            PositionVO buyVO = new PositionVO(symbol, ContractOrderDirection.BUY);
//            BigDecimal le = BigDecimal.ZERO; //计算因子
//            for (ContractOrder buy: buys) {
//                buyVO.setMultiple(buy.getMultiple());
//                buyVO.setCoinSymbol(buy.getCoinSymbol());
//                buyVO.setPositions(buy.getPosition()+buyVO.getPositions());
//                buyVO.setAvailablePositions(buy.getAvailablePosition()+buyVO.getAvailablePositions());
//                buyVO.setPrincipalAmount(buy.getPrincipalAmount().add(buyVO.getPrincipalAmount()));
//                le = le.add(BigDecimal.valueOf(buy.getPosition()).divide(buy.getOpenPrice(), 20,BigDecimal.ROUND_DOWN));
//            }
//            buyVO.setAvgOpenPrice(BigDecimal.valueOf(buyVO.getPositions()).divide(le, 2,BigDecimal.ROUND_DOWN));
//            list.add(buyVO);
//        }
//        //空仓
//        List<ContractOrder> sells = contractOrderRepository.findAllByMemberIdAndSymbolAndStatusAndDirection(memberId, symbol, ContractOrderStatus.MARKET_IN_THE_POSITION, ContractOrderDirection.SELL);
//        if (buys.size()>0) {
//            PositionVO sellVO = new PositionVO(symbol, ContractOrderDirection.SELL);
//            BigDecimal le = BigDecimal.ZERO; //计算因子
//            for (ContractOrder sell: sells) {
//                sellVO.setMultiple(sell.getMultiple());
//                sellVO.setCoinSymbol(sell.getCoinSymbol());
//                sellVO.setPositions(sell.getPosition()+sellVO.getPositions());
//                sellVO.setAvailablePositions(sell.getAvailablePosition()+sellVO.getAvailablePositions());
//                sellVO.setPrincipalAmount(sell.getPrincipalAmount().add(sellVO.getPrincipalAmount()));
//                le = le.add(BigDecimal.valueOf(sell.getPosition()).divide(sell.getOpenPrice(), 20,BigDecimal.ROUND_DOWN));
//            }
//            sellVO.setAvgOpenPrice(BigDecimal.valueOf(sellVO.getPositions()).divide(le, 2,BigDecimal.ROUND_DOWN));
//            list.add(sellVO);
//        }
//        return list;
    }


}
