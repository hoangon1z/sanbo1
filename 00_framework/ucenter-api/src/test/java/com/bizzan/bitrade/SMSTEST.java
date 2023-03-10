package com.bizzan.bitrade;

import com.bizzan.bitrade.vendor.provider.SMSProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SMSTEST {

    @Autowired
    private SMSProvider smsProvider;

    /**
     * 验证短信
     */
    @Test
    public void sendVerifyMessage() {
        try {
            smsProvider.sendVerifyMessage("18500483017", "1234");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送单条短信
     */
    @Test
    public void sendSingleMessage() {
        try {
            //smsProvider.sendSingleMessage("18500483017", "##用户充值##");
            smsProvider.sendSingleMessage("18500483017", "##新实名申请##");
            smsProvider.sendSingleMessage("18500483017", "##新用户注册(10)##");
            smsProvider.sendSingleMessage("18500483017", "##新提现申请##");
            smsProvider.sendSingleMessage("18500483017", "##收到用户付款标记##");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模板短信
     */
    @Test
    public void sendMessageByTempId() {
        try {
            smsProvider.sendMessageByTempId("18500483017", "尊敬的用户" + "10" + "您已成功购买", "9499");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义短信
     */
    @Test
    public void sendCustomMessage() {
        try {

            smsProvider.sendCustomMessage("18500483017", "尊敬的用户，万分抱歉，您于20200909提取失败，数量为1500，请联系客服处理。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
