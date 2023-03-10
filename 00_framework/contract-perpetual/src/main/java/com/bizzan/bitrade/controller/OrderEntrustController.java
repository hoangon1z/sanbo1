package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * 委托订单处理类
 */
@Slf4j
@RestController
@RequestMapping("/order-entrust")
public class OrderEntrustController {

    @Autowired
    private ContractOrderEntrustService orderEntrustService;

    @Autowired
    private ContractOrderService contractOrderService;

    @Autowired
    private ContractCoinService contractCoinService;

    @Autowired
    private LocaleMessageSourceService msService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private ContractWalletService contractWalletService;

    @Autowired
    private ContractCoinMatchFactory factory;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private ContractLeverageService leverageService;

    /**
     * 添加委托订单
     *
     * @param authMember
     * @return
     */
    @RequestMapping("add")
    public MessageResult addOrder(@SessionAttribute(SESSION_MEMBER) AuthMember authMember,
                                  Long contractCoinId,
                                  String symbol,
                                  ContractOrderEntrustType entrustType,
                                  ContractOrderType type,
                                  ContractOrderPattren patterns,
                                  BigDecimal leverage,
                                  Integer holdTime,
                                  BigDecimal entrustPrice,
                                  BigDecimal triggerPrice,
                                  Boolean marketPrice,
                                  Integer share,
                                  ContractOrderDirection direction,
                                  ContractOrderEntrustTriggerType triggerType) {
        /**基础判断**/
        if (contractCoinId == null || direction == null || type == null) {
            return MessageResult.error(500, msService.getMessage("ILLEGAL_ARGUMENT"));
        }
        Member member = memberService.findOne(authMember.getId());
        //是否被禁止交易
        if (member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)) {
            return MessageResult.error(500, msService.getMessage("CANNOT_TRADE"));
        }
        //判断限价输入值是否小于零
        if ((marketPrice == null || !marketPrice) && entrustPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        //判断手数小于零
        if (share <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        /**币种设置判断**/
        //根据交易对名称（symbol）获取交易对儿信息
        ContractCoin contractCoin = contractCoinService.findOne(contractCoinId);
        if (contractCoin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (contractCoin.getEnable() != 1 || contractCoin.getExchangeable() != 1) {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        //秒合约未传时间
        if (contractCoin.getType() == ContractCoinType.SECOND && (holdTime == null || holdTime <= 0)) {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        if (patterns == ContractOrderPattren.CROSSED) { //全仓
            if (contractCoin.getCrossEnabled() == 2) {
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
        } else if (patterns == ContractOrderPattren.FIXED) { //逐仓
            if (contractCoin.getFixedEnabled() == 2) {
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
        } else {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        if (marketPrice) { //市价委托 已被对手价替代
            if (entrustType == ContractOrderEntrustType.OPEN && direction == ContractOrderDirection.BUY && contractCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE) { // 市价开仓做多
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
            if (entrustType == ContractOrderEntrustType.OPEN && direction == ContractOrderDirection.SELL && contractCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE) { // 市价开仓做空
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
        }

        if (type == ContractOrderType.LIMIT_PRICE) { //限价委托
            if (entrustType == ContractOrderEntrustType.OPEN && direction == ContractOrderDirection.BUY && contractCoin.getEnableOpenBuy() == BooleanEnum.IS_FALSE) { // 开仓做多
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
            if (entrustType == ContractOrderEntrustType.OPEN && direction == ContractOrderDirection.SELL && contractCoin.getEnableOpenSell() == BooleanEnum.IS_FALSE) { // 开仓做空
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
        } else if (type == ContractOrderType.SPOT_LIMIT) { //计划委托
            if (contractCoin.getEnableTriggerEntrust() == BooleanEnum.IS_FALSE) {
                return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
            }
            if (triggerPrice == null || triggerType == null) {
                return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
            }
        } else { //其它委托方式
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        if (share > contractCoin.getMaxShare()) {
            return MessageResult.error(500, msService.getMessage("AMOUNT_OVER_SIZE"));
        }
        if (share < contractCoin.getMinShare()) {
            return MessageResult.error(500, msService.getMessage("AMOUNT_TOO_SMALL"));
        }
        /**币种基础判断**/
        //获取基准币
        String baseSymbol = contractCoin.getBaseSymbol();
        //获取交易币
        String coinSymbol = contractCoin.getCoinSymbol();
//        Coin coin;
        //查询交易币种信息(暂时指定usd交易)
//        coin = coinService.findByUnit(baseSymbol);
//        if (coin == null) {
//            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
//        }
        /**钱包判断**/
        // 查询钱包
        ContractWallet wallet = contractWalletService.findByCoinUnitAndMemberId(baseSymbol, member.getId());
        if (wallet == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        // 钱包被锁
        if (wallet.getIsLock() == BooleanEnum.IS_TRUE) {
            return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
        }
        // 杠杆后端存储不能以前端提交为准
        ContractLeverage contractLeverage = leverageService.getLeverage(authMember.getId(), symbol);
        if (contractLeverage!=null) {
            leverage = contractLeverage.getLeverage();
        }
        ContractOrderEntrust entrust = new ContractOrderEntrust();
        /*** kickout ***/
        if (entrustType == ContractOrderEntrustType.OPEN) { //判断开仓手续费
            BigDecimal rate = contractCoin.getTakerFee() != null ? contractCoin.getTakerFee() : contractCoin.getOpenFee();
            BigDecimal fee = contractCoin.getShareNumber().multiply(BigDecimal.valueOf(share)).divide(leverage, 2, BigDecimal.ROUND_DOWN).multiply(rate);
            MemberWallet feeWallet = memberWalletService.findByCoinUnitAndMemberId("KICK", authMember.getId());
            if (feeWallet.getBalance().compareTo(fee) < 0) {
                return MessageResult.error("手续费不足，所需：" + fee + "KICK");
            }
            memberWalletService.kickIncreaseBalance(feeWallet.getId(), fee.negate()); // kickout 直接扣除手续费
            memberService.addKickFeeHandler(authMember.getId(), fee);
            entrust.setFee(fee); // kick用户记录以便于退回手续费
        }
        /*** kickout end ***/

        entrust.setContractCoinId(contractCoinId);
        entrust.setEntrustType(entrustType);
        entrust.setType(type);
        entrust.setPatterns(patterns);
        entrust.setLeverage(leverage);
        entrust.setMarketPrice(marketPrice);
        if (contractCoin.getType() == ContractCoinType.SECOND) {
            entrust.setHoldTime(holdTime); //秒合约存入持仓时间(秒)
        }
        if (!marketPrice) {
            entrust.setEntrustPrice(entrustPrice.setScale(contractCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN));
        } else {
            entrust.setEntrustPrice(null);
        }
        entrust.setShare(share);
        entrust.setDirection(direction);
        entrust.setTriggerPrice(triggerPrice);
        entrust.setTriggerType(triggerType);
        MessageResult mr = orderEntrustService.addOrder(authMember.getId(), entrust);
        if (mr.getCode() != 0) {
            return mr;
        }
        ContractOrderEntrust data = (ContractOrderEntrust) mr.getData();
        factory.getContractCoinMatch(symbol).addContranctOrderEntrust(data);
        log.info(">>>>>>>>>>合约委托单提交完成>>>>>>>>>>");
        MessageResult result = MessageResult.success("success");
        return result;
    }

    /**
     * 当前委托
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("current")
    public Page<ContractOrderEntrust> currentOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                                   Long contractCoinId,
                                                   ContractOrderType type,
                                                   ContractOrderEntrustType entrustType,
                                                   ContractOrderDirection direction,
                                                   String startTime,
                                                   String endTime,
                                                   int pageNo,
                                                   int pageSize) {
        Page<ContractOrderEntrust> page = orderEntrustService.findCurrent(member.getId(), contractCoinId, type, entrustType, direction, startTime, endTime, pageNo, pageSize);
        return page;
    }

    /**
     * 历史委托
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("history")
    public Page<ContractOrderEntrust> historyOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                                   Long contractCoinId,
                                                   ContractOrderType type,
                                                   ContractOrderEntrustType entrustType,
                                                   ContractOrderDirection direction,
                                                   String startTime,
                                                   String endTime,
                                                   int pageNo,
                                                   int pageSize) {
        Page<ContractOrderEntrust> page = orderEntrustService.findHistory(member.getId(), contractCoinId, type, entrustType, direction, startTime, endTime, pageNo, pageSize);
        return page;
    }

    /**
     * 取消委托
     *
     * @param member
     * @param id
     * @return
     */
    @RequestMapping("cancel/{id}")
    public MessageResult cancel(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable Long id) {
        MessageResult mr = orderEntrustService.cancel(id);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, mr.getMessage());
        }
        factory.getContractCoinMatch((String) mr.getData()).removeContractOrderEntrust(id);
        log.info(">>>>>>>>>>合约委托单取消完成>>>>>>>>>>");
        return MessageResult.success("success");
    }

    @RequestMapping("quick-close/{id}")
    public MessageResult quickClose(@SessionAttribute(SESSION_MEMBER) AuthMember authMember, @PathVariable Long id) {
        MessageResult mr = orderEntrustService.quickClose(authMember.getId(), id);
        if (mr.getCode() != 0) {
            return mr;
        }
        ContractOrderEntrust data = (ContractOrderEntrust) mr.getData();
        factory.getContractCoinMatch(data.getSymbol()).addContranctOrderEntrust(data);
        log.info(">>>>>>>>>>闪电平单委托单提交完成>>>>>>>>>>");
        MessageResult result = MessageResult.success("success");
        return result;
    }
}
