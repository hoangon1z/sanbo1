package com.bizzan.bitrade.util;

public class HuobiTime {

    /**
     * 根据描述返回秒
     *
     * @param period
     * @return
     */
    public static long getDivideTime(String period) {
        long divideTime = 0;
        if (period.equals("1min")) divideTime = 60; // 1分钟 * 300条
        if (period.equals("5min")) divideTime = 5 * 60;
        if (period.equals("15min")) divideTime = 15 * 60;
        if (period.equals("30min")) divideTime = 30 * 60;
        if (period.equals("60min")) divideTime = 60 * 60;
        if (period.equals("4hour")) divideTime = 4 * 60 * 60;
        if (period.equals("1day")) divideTime = 24 * 60 * 60;
        if (period.equals("1week")) divideTime = 7 * 24 * 60 * 60;
        if (period.equals("1mon")) divideTime = 30 * 24 * 60 * 60;
        return divideTime;
    }

}
