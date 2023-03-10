package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.engine.ContractCoinMatch;
import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import com.bizzan.bitrade.service.ContractCoinService;
import com.bizzan.bitrade.service.ContractOrderEntrustService;
import com.bizzan.bitrade.service.ContractOrderService;
import com.bizzan.bitrade.util.WebSocketConnectionManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.util.MessageResult;

import java.util.List;

/**
 * @author Shaoxianjun
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
            */
@RestController
@RequestMapping("contract-coin")
public class ContractCoinController extends BaseController {

    @Autowired
    private ContractCoinService coinService;

    @Autowired
    private ContractOrderService orderService;

    @Autowired
    private ContractOrderEntrustService orderEntrustService;

    @Autowired
    private ContractCoinMatchFactory factory;

    //获取基币
    @RequestMapping("base-symbol")
    public MessageResult baseSymbol() {
        List<String> baseSymbol = coinService.getBaseSymbol();
        if (baseSymbol != null && baseSymbol.size() > 0) {
            return success(baseSymbol);
        }
        return error("baseSymbol null");
    }

    //获取基币
    @RequestMapping("add-coin")
    public MessageResult addCoin(String symbol) {

        ContractCoinMatch match = new ContractCoinMatch(symbol);
        match.setContractOrderService(orderService);
        match.setContractCoinService(coinService);
        match.setContractOrderEntrustService(orderEntrustService);

        factory.addContractCoinMatch(symbol, match); // 新增引擎
        WebSocketConnectionManage.getWebSocket().subNewCoinPrice(symbol); // 订阅价格

        return success("baseSymbol null");
    }
}
