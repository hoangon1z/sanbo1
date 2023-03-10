package com.bizzan.bitrade.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RiskConsumer {
    //    @KafkaListener(topics = {"risk-period-change"})
    public void resetAddress(ConsumerRecord<String, String> record) {
        System.out.println("风控区间数据变化");
        System.out.println(record);
    }
}
