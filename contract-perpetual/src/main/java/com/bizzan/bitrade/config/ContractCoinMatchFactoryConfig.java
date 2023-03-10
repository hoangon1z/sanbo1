package com.bizzan.bitrade.config;

import com.bizzan.bitrade.engine.ContractCoinMatch;
import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import com.bizzan.bitrade.entity.ContractCoin;
import com.bizzan.bitrade.entity.ContractOrderEntrust;
import com.bizzan.bitrade.service.ContractCoinService;
import com.bizzan.bitrade.service.ContractOrderEntrustService;
import com.bizzan.bitrade.service.ContractOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class ContractCoinMatchFactoryConfig {
    @Bean
    public ContractCoinMatchFactory getContractCoinMatchFactory() {

        ContractCoinMatchFactory factory = new ContractCoinMatchFactory();
        return factory;

    }
}
