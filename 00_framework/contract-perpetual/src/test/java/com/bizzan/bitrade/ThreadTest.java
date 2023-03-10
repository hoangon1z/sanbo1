package com.bizzan.bitrade;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 合约委托单、持仓单单元测试
 */
public class ThreadTest {
    private List<Integer> orders = new ArrayList<>();

    @Test
    public void test1() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        orders.add(1003);
        orders.add(1004);
        // 线程B查看合约订单
        new Thread(() -> {
            Iterator<Integer> iterator = orders.iterator();
            while (iterator.hasNext()) {
                Integer integer = iterator.next();
                System.out.println("秒合约B:" + integer);
            }
        }).start();
        // 线程A添加合约订单

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                add(i);
            }
            countDownLatch.countDown();
        }).start();
        // 线程D添加合约订单
        new Thread(() -> {
            for (int i = 10000; i < 20000; i++) {
                add(i);
            }
            countDownLatch.countDown();
        }).start();
        //线程C查看合约订单
        new Thread(() -> {
            Iterator<Integer> iterator = orders.iterator();
            while (iterator.hasNext()) {
                Integer integer = iterator.next();
                System.out.println("秒合约C:" + integer);
            }
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("size" + orders.size());
        System.out.println(orders);
    }

    private void add(int i) {
        List<Integer> os = Collections.synchronizedList(orders);
        os.add(i);
    }
}
