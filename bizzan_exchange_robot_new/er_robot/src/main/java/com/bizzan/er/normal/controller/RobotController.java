package com.bizzan.er.normal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bizzan.er.normal.entity.RobotQuota;
import com.bizzan.er.normal.service.RobotQuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bizzan.er.normal.entity.RobotParams;
import com.bizzan.er.normal.robot.ExchangeRobot;
import com.bizzan.er.normal.robot.ExchangeRobotFactory;
import com.bizzan.er.normal.robot.ExchangeRobotNormal;
import com.bizzan.er.normal.service.RobotParamService;
import com.bizzan.er.normal.utils.MessageResult;

@RestController
public class RobotController {
	
	private final static  Logger logger  =  LoggerFactory.getLogger(RobotController.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private RobotParamService robotParamService;
	
	@Autowired
	private ExchangeRobotFactory exchangeRobotFactory;

	@Autowired
	private RobotQuotaService robotQuotaService;

	@Value("${robot.sign}")
	protected String sign;

	@Value("${robot.current.order.url}")
	protected String currenOrderURL;

	@Value("${robot.cancel.order.url}")
	protected String cancelOrderURL;

	@Value("${robot.commit.order.url}")
	protected String commitOrderURL;

	@Value("${robot.plate.url}")
	protected String plateURL;
	
	@RequestMapping("robotList")
	public MessageResult robotList(){
		Map<String, RobotParams> retMap = new HashMap<String, RobotParams>();
		Map<String, ExchangeRobot> robotList = exchangeRobotFactory.getRobotList();
		for (Map.Entry<String, ExchangeRobot> entry : robotList.entrySet()) {
			retMap.put(entry.getKey(), entry.getValue().getRobotParams());
		}
		MessageResult mr = new MessageResult(0,"获取成功");
		mr.setData(retMap);
		return mr;
    }
	/**
	 * 设置机器人参数
	 * @param
	 * @return
	 */
	@RequestMapping("setRobotParams")
    public MessageResult setRobotParams(@RequestBody RobotParams params){
		logger.info("设置机器人参数：" + params.getCoinName());
		if(exchangeRobotFactory.containsExchangeRobot(params.getCoinName())) {
			exchangeRobotFactory.setRobotParams(params.getCoinName(), params);
			ExchangeRobot robot = exchangeRobotFactory.getExchangeRobot(params.getCoinName());
			robot.clearQuotaLines();
			MessageResult mr = new MessageResult(0,"设置参数成功");
			return mr;
		}else {
			MessageResult mr = new MessageResult(500, "机器人不存在");
			return mr;
		}
    }
	
	/**
	 * 获取机器人参数
	 * @param coinName
	 * @return
	 */
	@RequestMapping("getRobotParams")
	public MessageResult getRobotParams(String coinName) {
		logger.info("获取机器人参数：" + coinName);
		if(exchangeRobotFactory.containsExchangeRobot(coinName)) {
			MessageResult mr = new MessageResult(0,"获取机器人参数成功");
			mr.setData(exchangeRobotFactory.getRobotParams(coinName));
			return mr;
		}else {
			MessageResult mr = new MessageResult(500, "机器人不存在");
			return mr;
		}
	}
	
	@RequestMapping("createRobot")
	public MessageResult createRobot(@RequestBody RobotParams params) {
		logger.info("创建机器人：" + params.getCoinName());
		if(exchangeRobotFactory.containsExchangeRobot(params.getCoinName())) {
			MessageResult mr = new MessageResult(500, "新建失败，机器人已存在");
			return mr;
		}else {
			ExchangeRobot robot = new ExchangeRobotNormal(sign, currenOrderURL, cancelOrderURL, commitOrderURL, plateURL);
			
			robot.setRobotParamSevice(robotParamService);
			robot.setRobotQuotaService(robotQuotaService);

			robot.setRestTemplate(restTemplate);
			
			robot.setRobotParams(params);
			robot.setPlateSymbol(params.getCoinName());
			
			exchangeRobotFactory.addExchangeRobot(params.getCoinName(), robot);
			
			new Thread((ExchangeRobotNormal)robot).start();
			
			MessageResult mr = new MessageResult(0,"机器人创建成功");
			return mr;
		}
	}
	
	@RequestMapping("startRobot")
	public MessageResult startRobot(String coinName) {
		logger.info("启动机器人：" + coinName);
		if(exchangeRobotFactory.containsExchangeRobot(coinName)) {
			ExchangeRobot robot = exchangeRobotFactory.getExchangeRobot(coinName);
			robot.startRobot();
			MessageResult mr = new MessageResult(0,"机器人启动成功");
			return mr;
		}else {
			MessageResult mr = new MessageResult(500, "启动失败，机器人不存在");
			return mr;
		}
	}
	
	@RequestMapping("stopRobot")
	public MessageResult stopRobot(String coinName) {
		logger.info("停止机器人：" + coinName);
		if(exchangeRobotFactory.containsExchangeRobot(coinName)) {
			ExchangeRobot robot = exchangeRobotFactory.getExchangeRobot(coinName);
			robot.stopRobot();
			MessageResult mr = new MessageResult(0,"机器人停止成功");
			return mr;
		}else {
			MessageResult mr = new MessageResult(500, "停止失败，机器人不存在");
			return mr;
		}
	}

	/**
	 * 设置控盘数据
	 * @param quotas
	 * @return
	 */
	@RequestMapping("setRobotQuotas")
	public MessageResult setRobotParams(@RequestBody List<RobotQuota> quotas) {
		logger.info("设置控盘机器人数据：", quotas);
		if (quotas.size()==0) {
			MessageResult.error("数据错误");
		}
		String symbol = quotas.get(0).getSymbol();
		robotQuotaService.del(symbol, quotas.get(0).getDate());
		for (RobotQuota quota: quotas) {
			robotQuotaService.save(quota);
		}
		if(exchangeRobotFactory.containsExchangeRobot(symbol)) {
			ExchangeRobot robot = exchangeRobotFactory.getExchangeRobot(symbol);
			robot.clearQuotaLines();
		}
		MessageResult mr = new MessageResult(0,"设置控盘数据成功");
		return mr;
	}

	/**
	 * 获取控盘数据
	 * @param quota
	 * @return
	 */
	@RequestMapping("getRobotQuotas")
	public MessageResult getRobotQuotas(@RequestBody RobotQuota quota) {
		logger.info("获取控盘数据：", quota);
		List<RobotQuota> all = robotQuotaService.findAll(quota.getSymbol(), quota.getDate());
		MessageResult mr = new MessageResult(0,"获取控盘数据成功");
		mr.setData(all);
		return mr;
	}
}
