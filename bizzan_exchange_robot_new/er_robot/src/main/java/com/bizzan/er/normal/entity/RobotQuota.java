package com.bizzan.er.normal.entity;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 此类修改需要对Admin工程中的ExchangeRobotController进行修改
 * 控盘机器人数据
 * @author wmf
 *
 */
@Data
public class RobotQuota {
	private String symbol = ""; // 如btcusdt
	private String date = ""; //日期
	private Long time = 0l; //时间
	private BigDecimal price = new BigDecimal(0); // 价格

	@Override
	public String toString() {
		return "{" +
				"\"time\":" + "\"" + time + "\"" +
				", \"price\":" + "\"" + price + "\"" +
				'}';
	}

//	public RobotQuota(String symbol, String date, Long time, BigDecimal price) {
//		this.symbol = symbol;
//		this.date = date;
//		this.time = time;
//		this.price = price;
//	}
}
