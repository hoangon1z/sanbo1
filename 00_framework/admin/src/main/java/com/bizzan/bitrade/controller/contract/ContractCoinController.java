package com.bizzan.bitrade.controller.contract;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.controller.exchange.ExchangeCoinController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.ContractCoinScreen;
import com.bizzan.bitrade.model.screen.ContractOrderScreen;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ContractCoinService;
import com.bizzan.bitrade.service.ContractOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sparkframework.security.Encrypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.notNull;

/**
 * @author wmf
 * @description
 * @date 2020/8/20 10:52
 */
@RestController
@RequestMapping("contract/coin")
public class ContractCoinController extends BaseAdminController {

    private Logger logger = LoggerFactory.getLogger(BaseAdminController.class);

    @Autowired
    private ContractCoinService contractCoinService;

    @Autowired
    private CoinService coinService;

    @Value("${spark.system.md5.key}")
    private String md5Key;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequiresPermissions("contract:coin:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.CONTRACT, operation = "分页查找合约币种")
    public MessageResult page(
            PageModel pageModel,
            ContractCoinScreen screen) {
        if (pageModel.getDirection() == null && pageModel.getProperty() == null) {
            ArrayList<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setDirection(directions);
            List<String> property = new ArrayList<>();
            property.add("sort");
            pageModel.setProperty(property);
        }
        //获取查询条件
        Predicate predicate = getPredicate(screen);
        Page<ContractCoin> all = contractCoinService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    @RequiresPermissions("contract:coin:page-query")
    @PostMapping("query")
    @AccessLog(module = AdminModule.CONTRACT, operation = "查找所有合约")
    public MessageResult queryAll(
            ContractCoinScreen screen) {
        //获取查询条件
        List<ContractCoin> all = contractCoinService.findAll();
        return success(all);
    }

    private Predicate getPredicate(ContractCoinScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        QContractCoin q = QContractCoin.contractCoin;
        if (StringUtils.isNotEmpty(screen.getBaseSymbol())) {
            booleanExpressions.add(q.baseSymbol.equalsIgnoreCase(screen.getBaseSymbol()));
        }
        if (StringUtils.isNotEmpty(screen.getCoinSymbol())) {
            booleanExpressions.add(q.coinSymbol.equalsIgnoreCase(screen.getCoinSymbol()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    @RequiresPermissions("contract:coin:merge")
    @PostMapping("merge")
    @AccessLog(module = AdminModule.CONTRACT, operation = "新增合约币种")
    public MessageResult mergeCoin(
            @Valid ContractCoin contractCoin) {
        logger.info("Add exchange coin: " + JSON.toJSONString(contractCoin));
        ContractCoin findResult = contractCoinService.findBySymbolAndType(contractCoin.getSymbol(), contractCoin.getType());
        if (findResult != null) {
            return error("[" + contractCoin.getSymbol() + "]同类型交易对已存在！");
        }
        Coin c1 = coinService.findByUnit(contractCoin.getBaseSymbol());
        if (c1 == null) {
            return error("[" + contractCoin.getBaseSymbol() + "] 结算币种不存在！");
        }
        Coin c2 = coinService.findByUnit(contractCoin.getCoinSymbol());
        if (c2 == null) {
            return error("[" + contractCoin.getCoinSymbol() + "] 交易币种不存在！");
        }
        contractCoin.setEnable(2);
        contractCoin.setExchangeable(2);
        contractCoin.setVisible(2);
        contractCoin.setBaseCoinScale(8);
        contractCoin.setCrossEnabled(1);
        ContractCoin save = contractCoinService.save(contractCoin);
        // 发送消息至Contract系统
        kafkaTemplate.send("contract-create-coin", JSON.toJSONString(save));
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), save);
    }

    @RequiresPermissions("contract:coin:merge")
    @PostMapping("alter")
    @AccessLog(module = AdminModule.CONTRACT, operation = "修改合约交易对")
    public MessageResult alter(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "openFee", required = false) BigDecimal openFee,
            @RequestParam(value = "takerFee", required = false) BigDecimal takerFee,
            @RequestParam(value = "makerFee", required = false) BigDecimal makerFee,
            @RequestParam(value = "maintenanceMarginRate", required = false) BigDecimal maintenanceMarginRate,
            @RequestParam(value = "minShare", required = false) Integer minShare,
            @RequestParam(value = "maxShare", required = false) Integer maxShare,
            @RequestParam(value = "enable", required = false) Integer enable, // 上下架（1:上,2）
            @RequestParam(value = "visible", required = false) Integer visible, // 是否显示（1:是,2）
            @RequestParam(value = "exchangeable", required = false) Integer exchangeable, // 是否可交易(1:是,2)
            @RequestParam(value = "enableMarketBuy", required = false) Integer enableMarketBuy, // 是否可市价买（1:是,0:否）
            @RequestParam(value = "enableMarketSell", required = false) Integer enableMarketSell, // 是否可市价买（1:是,0:否）
            @RequestParam(value = "sort", required = false) Integer sort,
            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        ContractCoin contractCoin = contractCoinService.findOne(id);
        notNull(contractCoin, "validate symbol!");
        if (name != null) {
            contractCoin.setName(name);//修改合约名称
        }
        if (openFee != null) {
            contractCoin.setOpenFee(openFee);//修改手续费
        }
        if (takerFee != null) {
            contractCoin.setTakerFee(takerFee);//修改手续费
        }
        if (makerFee != null) {
            contractCoin.setMakerFee(makerFee);//修改手续费
        }
        if (maintenanceMarginRate != null) {
            contractCoin.setMaintenanceMarginRate(maintenanceMarginRate);
        }
        if (minShare != null) {
            contractCoin.setMinShare(minShare);
        }
        if (maxShare != null) {
            contractCoin.setMaxShare(maxShare);
        }
        if (sort != null) {
            contractCoin.setSort(sort);//设置排序
        }
        if (enable != null && enable > 0 && enable < 3) {
            contractCoin.setEnable(enable);//设置启用 禁用
        }
        if (visible != null && visible > 0 && visible < 3) {
            contractCoin.setVisible(visible);
        }
        if (exchangeable != null && exchangeable > 0 && exchangeable < 3) {
            contractCoin.setExchangeable(exchangeable);
        }
        if (enableMarketBuy != null && enableMarketBuy >= 0 && enableMarketBuy < 2) {
            contractCoin.setEnableMarketBuy(enableMarketBuy == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        }
        if (enableMarketSell != null && enableMarketSell >= 0 && enableMarketSell < 2) {
            contractCoin.setEnableMarketSell(enableMarketSell == 1 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
        }
        logger.info("Modify exchange coin: " + symbol);
        contractCoinService.save(contractCoin);
        return success(messageSource.getMessage("SUCCESS"));
    }
}
