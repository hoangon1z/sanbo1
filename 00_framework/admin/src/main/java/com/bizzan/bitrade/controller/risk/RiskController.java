package com.bizzan.bitrade.controller.risk;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.RiskPeriod;
import com.bizzan.bitrade.entity.RiskSetting;
import com.bizzan.bitrade.service.RiskControlService;
import com.bizzan.bitrade.service.RiskSettingService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("risk")
public class RiskController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RiskControlService riskControlService;

    @Autowired
    private RiskSettingService riskSettingService;

    @Value("${robot.sign}")
    protected String sign;

    @RequiresPermissions("risk:data:view")
    @RequestMapping("risk-data-periods")
    public MessageResult riskPeriods(
            String symbol,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        String serviceName = "CONTRACT-API";
        String url = "http://" + serviceName + "/swap/risk/periodsdasdasd?sign=" + sign + "&symbol=" + symbol;
        try {
            ResponseEntity<MessageResult> resultStr = restTemplate.getForEntity(url, MessageResult.class);
            MessageResult result = resultStr.getBody();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error("获取失败！");
        }
    }

    @RequiresPermissions("risk:data:view")
    @RequestMapping("risk-history-periods")
    public MessageResult riskHistoryPeriods(
            String symbol,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        List<RiskPeriod> re = riskControlService.findRiskBySymbol(symbol);
        return MessageResult.success(re);
    }


    @RequiresPermissions("risk:setting:query")
    @RequestMapping("risk-setting-query")
    public MessageResult riskSettingQuery() {
        return MessageResult.success(riskSettingService.findOne());
    }

    @RequiresPermissions("risk:setting:query")
    @RequestMapping("risk-setting-save")
    public MessageResult riskSetting(
            RiskSetting setting) {
        riskSettingService.save(setting);
        return MessageResult.success();
    }

    @RequiresPermissions("risk:data:position")
    @RequestMapping("risk-data-position")
    public MessageResult positions(
            String symbol,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        String serviceName = "CONTRACT-API";
        String url = "http://" + serviceName + "/swap/risk/positionddacxzad?sign=" + sign + "&symbol=" + symbol;
        try {
            ResponseEntity<MessageResult> resultStr = restTemplate.getForEntity(url, MessageResult.class);
            MessageResult result = resultStr.getBody();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error("获取失败！");
        }
    }

    @RequiresPermissions("risk:data:position")
    @RequestMapping("risk-data-add")
    public MessageResult addRiskData(
            String symbol,
            String start,
            Integer last,
            BigDecimal riskPrice,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        String serviceName = "CONTRACT-API";
        String url = "http://" + serviceName + "/swap/risk/adderiodjklfhfhk?sign=" + sign + "&symbol=" + symbol;
        try {
            RiskPeriod period = new RiskPeriod();
            Long startTime = getTimestamp(start) / 1000;
            if (startTime < (DateUtil.getTimeMillis() / 1000)) {
                throw new Exception("时间已过期");
            }
            Long endTime = startTime + last;
            period.setStart(startTime);
            period.setEnd(endTime);
            period.setRiskPrice(riskPrice);
            ResponseEntity<MessageResult> resultStr = restTemplate.postForEntity(url, period, MessageResult.class);
            MessageResult result = resultStr.getBody();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(e.getMessage());
        }
    }

    private Long getTimestamp(String start) throws ParseException {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(dt);
        String str = day + " " + start;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(str);
        long ts = date.getTime();
        return ts;
    }
}
