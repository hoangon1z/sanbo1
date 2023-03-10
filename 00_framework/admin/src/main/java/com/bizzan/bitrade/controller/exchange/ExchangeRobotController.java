package com.bizzan.bitrade.controller.exchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.model.screen.ExchangeCoinScreen;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.FileUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sparkframework.security.Encrypt;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

import static org.springframework.util.Assert.notNull;

/**
 * @author Shaoxianjun
 * @description 币币交易手续费
 * @date 2019/1/19 15:16
 */
@RestController
@RequestMapping("exchange/exchange-robot")
public class ExchangeRobotController extends BaseAdminController {

    private Logger logger = LoggerFactory.getLogger(ExchangeRobotController.class);

    @Autowired
    private LocaleMessageSourceService messageSource;

    @Autowired
    private ExchangeCoinService exchangeCoinService;

    @Autowired
    private RestTemplate restTemplate;


    /**
     * 新增控盘机器人数据
     *
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-robot:merge")
    @PostMapping("robot-quota-merge")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "存储控盘机器人数据")
    public MessageResult alterRobotConfig(
            @RequestBody RobotQuota[] quotas,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findOne(quotas[0].getSymbol());
        if (exchangeCoin == null) {
            return error("请选择交易对！");
        }
        if (!StringUtils.isNotEmpty(quotas[0].getDate()) || quotas[0].getDate().equals("NaN-NaN-NaN")) {
            return error("请选择控盘时间！");
        }

        String serviceName = "ROBOT-TRADE-NORMAL";
        String url = "http://" + serviceName + "/ernormal/setRobotQuotas";
        try {
            ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, quotas, JSONObject.class);
            logger.info("Set robot config: " + resultStr.toString());
            JSONObject result = (JSONObject) resultStr.getBody();
            if (result.getIntValue("code") == 0) {
                return success(messageSource.getMessage("SUCCESS"), result);
            } else {
                return error("存储价格数据失败！");
            }
        } catch (Exception e) {
            return error("存储价格数据失败！");
        }
    }

    /**
     * 获取控盘机器人数据
     *
     * @param admin
     * @return
     */
    @RequiresPermissions("exchange:exchange-robot:merge")
    @PostMapping("robot-quota-query")
    @AccessLog(module = AdminModule.EXCHANGE, operation = "获取控盘机器人数据")
    public MessageResult getRobotConfig(
            @RequestBody RobotQuota quota,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        ExchangeCoin exchangeCoin = exchangeCoinService.findOne(quota.getSymbol());
        if (exchangeCoin == null) {
            return error("请选择交易对！");
        }
        if (!StringUtils.isNotEmpty(quota.getDate()) || quota.getDate().equals("NaN-NaN-NaN")) {
            return error("请选择控盘时间！");
        }

        String serviceName = "ROBOT-TRADE-NORMAL";
        String url = "http://" + serviceName + "/ernormal/getRobotQuotas";
        try {
            ResponseEntity<JSONObject> resultStr = restTemplate.postForEntity(url, quota, JSONObject.class);
            JSONObject result = (JSONObject) resultStr.getBody();
            if (result.getIntValue("code") == 0) {
                return success(messageSource.getMessage("SUCCESS"), result.getJSONArray("data"));
            } else {
                return error("获取价格数据失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return error("获取价格数据失败！");
        }
    }

    @Data
    static class RobotQuota {
        private String symbol = ""; // 如btcusdt
        private String date = ""; //日期
        private Long time = 0l; //时间
        private BigDecimal price = new BigDecimal(0); // 价格

        public RobotQuota() {
        }
    }
}
