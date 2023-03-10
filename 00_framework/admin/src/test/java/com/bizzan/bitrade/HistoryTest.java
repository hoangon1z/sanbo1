package com.bizzan.bitrade;

import com.bizzan.bitrade.entity.ExchangeOrder;
import com.bizzan.bitrade.service.ExchangeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class HistoryTest {

    @Autowired
    private ExchangeOrderService exchangeOrderService;

    @Test
    public void history() {
        log.info("开始清理交易历史数据");
        long beforeTime = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000); // 2天前
        log.info("清除指定时间之前的订单：" + beforeTime);
        List<ExchangeOrder> list = exchangeOrderService.queryHistoryDelete(beforeTime);
        System.out.println(list.size());
    }
}
