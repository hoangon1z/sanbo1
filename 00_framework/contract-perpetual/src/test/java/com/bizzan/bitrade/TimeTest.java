package com.bizzan.bitrade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TimeTest {
    public static void main(String[] args) throws ParseException {
        List<String> list = new ArrayList<String>();
        list.add("2016-11-25 00:00:00");
        list.add("2016-11-25 00:05:00");
        // list.add("2016-11-25 00:10:00");
        // list.add("2016-11-25 00:15:00");
        list.add("2016-11-25 00:20:00");
        list.add("2016-11-25 00:25:00");
        // list.add("2016-11-25 00:30:00");
        // list.add("2016-11-25 00:35:00");
        list.add("2016-11-25 00:40:00");
        list.add("2016-11-25 00:45:00");
        list.add("2016-11-25 00:50:00");
        list.add("2016-11-25 00:55:00");
        list.add("2016-11-25 01:00:00");
        list.add("2016-11-25 01:05:00");

        Calendar cal = Calendar.getInstance();

        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> tempList = new ArrayList<String>();
        List<String> tempList2 = null;
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                tempList.add(list.get(i));
                map.put(String.valueOf(i), tempList);
                break;
            }
            String currTime = list.get(i);
            String nextTime = list.get(i + 1);
            Date preDate = simple.parse(currTime);
            cal.setTime(preDate);
            cal.add(Calendar.MINUTE, 5);
            String preTime = String.valueOf(simple.format(cal.getTime()));
            if (nextTime.equals(preTime)) {
                tempList.add(currTime);

            } else {
                tempList.add(currTime);
                tempList2 = new ArrayList<String>();
                tempList2.addAll(tempList);
                map.put(String.valueOf(i), tempList2);
                tempList.clear();
            }
        }


        for (Entry<String, List<String>> entry : map.entrySet()) {
            List<String> lis = entry.getValue();
            for (String createtime : lis) {
                System.out.print(createtime + "  ,");
            }
            System.out.println();
        }

    }
}