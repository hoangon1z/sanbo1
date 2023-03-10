package com.bizzan.bitrade;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 描述:获取下两个月当天.
     *
     * @return
     */
    public static Long getPreMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, 2);
        Long num = cal.getTimeInMillis();
        return num;
    }

    public static void main(String[] args) {
        System.out.println(getPreMonth());
    }


}
