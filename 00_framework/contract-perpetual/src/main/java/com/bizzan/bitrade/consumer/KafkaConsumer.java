package com.bizzan.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.engine.ContractCoinMatch;
import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import com.bizzan.bitrade.entity.ContractCoin;
import com.bizzan.bitrade.entity.ContractOrder;
import com.bizzan.bitrade.entity.ContractOrderEntrust;
import com.bizzan.bitrade.job.ExchangePushJob;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.WebSocketConnectionManage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KafkaConsumer {

    @Autowired
    private ContractCoinMatchFactory factory;

    @Autowired
    private ExchangePushJob exchangePushJob;                          // 推送任务

    @Autowired
    private ContractCoinService coinService;

    @Autowired
    private ContractOrderService orderService;

    @Autowired
    private ContractOrderEntrustService orderEntrustService;

    @Autowired
    private RiskControlService riskControlService;

    @Autowired
    private RiskStrategyService riskStrategyService;

    @KafkaListener(topics = "contract-entrust-cancel", containerFactory = "kafkaListenerContainerFactory")
    public void onEntrustCancel(List<ConsumerRecord<String, String>> records) {
        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, String> record = records.get(i);
            log.info("取消委托单topic={},key={},size={}", record.topic(), record.key(), records.size());
            ContractOrderEntrust entrust = JSON.parseObject(record.value(), ContractOrderEntrust.class);
            if (entrust == null) {
                return;
            }
            ContractCoinMatch match = factory.getContractCoinMatch(entrust.getSymbol());
            if (match != null) {
                match.removeContractOrderEntrust(entrust.getId());
                match.pushUserOrderChange(entrust.getSymbol(), entrust.getMemberId());
            }
        }
    }

    @KafkaListener(topics = "contract-order-close", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderClose(List<ConsumerRecord<String, String>> records) {
        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, String> record = records.get(i);
            log.info("强制平仓topic={},key={},size={}", record.topic(), record.key(), records.size());
            ContractOrder order = JSON.parseObject(record.value(), ContractOrder.class);
            if (order == null) {
                return;
            }
            ContractCoinMatch match = factory.getContractCoinMatch(order.getSymbol());
            if (match != null) {
                match.removeContractOrder(order.getId());
                match.pushUserOrderChange(order.getSymbol(), order.getMemberId());
            }
        }
    }

    @KafkaListener(topics = "contract-create-coin", containerFactory = "kafkaListenerContainerFactory")
    public void onCreateCoin(List<ConsumerRecord<String, String>> records) {
        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, String> record = records.get(i);
            log.info("创建币种topic={},key={},size={}", record.topic(), record.key(), records.size());
            ContractCoin coin = JSON.parseObject(record.value(), ContractCoin.class);
            if (coin == null) {
                return;
            }
            ContractCoinMatch match = new ContractCoinMatch(coin.getSymbol(), coin.getBaseCoinScale());
            match.setContractOrderService(orderService);
            match.setContractCoinService(coinService);
            match.setContractOrderEntrustService(orderEntrustService);
            match.setRiskControlService(riskControlService);
            match.setRiskStrategyService(riskStrategyService);

            factory.addContractCoinMatch(coin.getSymbol(), match); // 新增引擎
            WebSocketConnectionManage.getWebSocket().subNewCoinPrice(coin.getSymbol()); // 订阅价格
        }
    }
}
