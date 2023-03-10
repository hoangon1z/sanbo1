package com.bizzan.bitrade;

import java.util.ArrayList;
import java.util.Iterator;

public class ListTest {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(2);
        list.add(45);
        list.add(2);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer == 2)
                iterator.remove();
//                    list.remove(integer);
        }
            /*for (Integer integer : list) {
                if(integer==2)
                    list.remove(integer);
            }*/
        System.out.println(list);
    }
}
