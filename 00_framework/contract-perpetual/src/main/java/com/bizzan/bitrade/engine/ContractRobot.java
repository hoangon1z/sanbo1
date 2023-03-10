package com.bizzan.bitrade.engine;


import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.ContractCoinService;
import com.bizzan.bitrade.service.ContractOrderEntrustService;
import com.bizzan.bitrade.util.MessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContractRobot implements Runnable {

    private ContractCoinService contractCoinService;

    private ContractOrderEntrustService orderEntrustService;

    private ContractCoinMatchFactory factory;

    public Logger logger = LoggerFactory.getLogger(ContractRobot.class);

    private Boolean enabled = true;

    @Override
    public void run() {
        if (!enabled) {
            return;
        }
        List<ContractCoin> contracts = contractCoinService.findAllVisible(ContractCoinType.SECOND);
        for (int i = 0; i < 100000; i++) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Long> memberIds = new ArrayList<Long>() {{
                add(1l);
//                add(2l);
//                add(4l);
            }};
            Random random = new Random();
            for (Long memberId : memberIds) {
                if (random.nextInt(10) > 7) {
                    ContractCoin contract = contracts.get(0);
                    // 开始下订单
                    ContractOrderEntrust entrust = new ContractOrderEntrust();
                    entrust.setFee(BigDecimal.ZERO);
                    entrust.setContractCoinId(contract.getId());
                    entrust.setEntrustType(ContractOrderEntrustType.OPEN);
                    entrust.setType(ContractOrderType.LIMIT_PRICE);
                    entrust.setPatterns(ContractOrderPattren.CROSSED);
                    entrust.setLeverage(BigDecimal.valueOf(random.nextInt(50) + 1));
                    entrust.setMarketPrice(true);
                    if (contract.getType() == ContractCoinType.SECOND) {
                        entrust.setHoldTime(random.nextInt(100) + 1); //秒合约存入持仓时间(秒)
                    }
                    entrust.setEntrustPrice(null);
                    entrust.setShare(random.nextInt(500) + 500);
                    if (random.nextInt(2) == 1) {
                        entrust.setDirection(ContractOrderDirection.BUY);
                    } else {
                        entrust.setDirection(ContractOrderDirection.SELL);
                    }
                    MessageResult mr = orderEntrustService.addOrder(memberId, entrust);
                    if (mr.getCode() == 0) {
                        ContractOrderEntrust data = (ContractOrderEntrust) mr.getData();
                        factory.getContractCoinMatch(contract.getSymbol()).addContranctOrderEntrust(data);
                    }
                    logger.info("用户：{} 下单：{} 结果：{}", memberId, contract.getName(), mr.getMessage());
                }
            }
        }
    }

    public void setContractCoinService(ContractCoinService contractCoinService) {
        this.contractCoinService = contractCoinService;
    }

    public void setOrderEntrustService(ContractOrderEntrustService orderEntrustService) {
        this.orderEntrustService = orderEntrustService;
    }

    public void setFactory(ContractCoinMatchFactory factory) {
        this.factory = factory;
    }
}
