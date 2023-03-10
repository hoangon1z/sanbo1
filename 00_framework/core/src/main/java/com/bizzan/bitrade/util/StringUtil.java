package com.bizzan.bitrade.util;

public class StringUtil {


    //获取
    public static String getLastSixNum(String orderSn) {

        String newOrderSn = orderSn.substring(orderSn.length() - 6, orderSn.length());
        return newOrderSn;

    }

    //首末位除外改成*
    public static String changeTelnumBeRequest(String str) {
        String phoneNumber = str.substring(0, str.indexOf(" ") + 2) + "*********" + str.substring(str.length() - 1);
        //System.out.print(phoneNumber);
        return phoneNumber;
    }


}
