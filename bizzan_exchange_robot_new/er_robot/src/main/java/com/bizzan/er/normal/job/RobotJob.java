package com.bizzan.er.normal.job;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bizzan.er.normal.service.RobotQuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bizzan.er.normal.entity.RobotParams;
import com.bizzan.er.normal.robot.ExchangeRobot;
import com.bizzan.er.normal.robot.ExchangeRobotFactory;
import com.bizzan.er.normal.robot.ExchangeRobotNormal;
import com.bizzan.er.normal.service.RobotParamService;

@Component
public class RobotJob {
	private final static  Logger logger  =  LoggerFactory.getLogger(RobotJob.class);
	@Autowired
	private ExchangeRobotFactory exchangeRobotFactory;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private RobotParamService robotParamService;

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
	
	
	
	private boolean inited = false;
	
	@Scheduled(fixedDelay = 60000)
	public void synchronizeExchangeCenter(){
		String serviceName = "SERVICE-EXCHANGE-TRADE";
        String url = "http://" + serviceName + "/monitor/engines";
        ResponseEntity<HashMap> resultStr = restTemplate.getForEntity(url, HashMap.class);
        Map<String, Integer> exchangeCenterCoins = (HashMap<String, Integer>)resultStr.getBody();
        for (Map.Entry<String, Integer> coin : exchangeCenterCoins.entrySet()) {
        	String coinName = coin.getKey();
        	if(!exchangeRobotFactory.containsExchangeRobot(coinName)) {
	        	RobotParams params = robotParamService.findOne(coinName);
	        	if(params != null) {
	        		ExchangeRobot robot = new ExchangeRobotNormal(sign, currenOrderURL, cancelOrderURL, commitOrderURL, plateURL);
	    			robot.setRobotParamSevice(robotParamService);
					robot.setRobotQuotaService(robotQuotaService);
	    			robot.setRestTemplate(restTemplate);

	    			
	    			robot.setRobotParams(params);
	    			robot.setPlateSymbol(coinName);
	    			
	    			exchangeRobotFactory.addExchangeRobot(params.getCoinName(), robot);
	    			
	    			new Thread((ExchangeRobotNormal)robot).start();
	        	}
        	}
        }
	}
	
	@Scheduled(fixedDelay = 120000)
	public void checkAllRoot(){
		List <String> runRobotList = new ArrayList<String>();
		List <String> haltRobotList = new ArrayList<String>();
		exchangeRobotFactory.getRobotList().forEach((symbol, robot)->{
			if(robot.getRobotParams().isHalt()) {
				haltRobotList.add("[" + symbol + "]");
			}else {
				Instant timestamp = Instant.ofEpochMilli(robot.getLastSendOrderTime()==null?0:robot.getLastSendOrderTime().toEpochMilli());
			    ZonedDateTime losAngelesTime = timestamp.atZone(ZoneId.of("Asia/Shanghai"));
				runRobotList.add("[" + symbol + "] - 最后提交：" + losAngelesTime.toLocalDateTime());
			}
		});
		
		logger.info("运行中的机器人: " + runRobotList.size());
		for(int i = 0; i < runRobotList.size(); i++) {
			logger.info(runRobotList.get(i));
		}
		
		logger.info("停止中的机器人: " + haltRobotList.size());
		for(int i = 0; i < haltRobotList.size(); i++) {
			logger.info(haltRobotList.get(i));
		}
		
		logger.info("机器人总数共：" +(haltRobotList.size() + runRobotList.size()));
	}
}
