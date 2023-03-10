package com.bizzan.er.market;

import com.bizzan.er.normal.Robot;
import com.bizzan.er.normal.utils.HttpClientUtil;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest(classes = Robot.class)
public class HttpTest {

    protected String commitOrderURL = "http://127.0.0.1/exchange/order/test";

    Thread t2 = new Thread() {
        @Override
        public void run() {
            try {
                System.out.println("ok");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Test
    public void test1() {
        Map<String, String> param = new HashMap<String, String>();
        for (int i=0;i<2;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = HttpClientUtil.doHttpPost(commitOrderURL, param, null);
                        System.out.println("发送订单结果:"+result);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
