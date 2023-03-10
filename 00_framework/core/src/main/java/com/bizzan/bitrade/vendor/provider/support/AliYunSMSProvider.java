package com.bizzan.bitrade.vendor.provider.support;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * 阿里云短信接口（https://help.aliyun.com/document_detail/102715.html?spm=a2c4g.11186623.2.9.17af5f30Hs6anZ#concept-t4w-pcs-ggb）
 *
 * @author wmf
 */
@Slf4j
public class AliYunSMSProvider implements SMSProvider {
    private String username;
    private String password;
    private String sign;
    private String gateway;
    private DefaultProfile profile;

    @Autowired
    private SmsService smsService;

    public static String getName() {
        return "aliyun";
    }

    public AliYunSMSProvider(String gateway, String username, String password, String sign) {
        this.username = username;
        this.password = password;
        this.sign = sign;
        this.gateway = gateway;
        this.profile = DefaultProfile.getProfile(this.username, this.password, this.sign);
    }

    @Override
    public MessageResult sendVerifyMessage(String mobile, String verifyCode) throws Exception {
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(this.gateway);
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", this.username);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "美付");
        request.putQueryParameter("TemplateCode", "SMS_198900173");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + verifyCode + "\"}");
        CommonResponse response = client.getCommonResponse(request);
        String data = response.getData();
        log.info(data);
        return MessageResult.success();
    }

    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
        return null;
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        return null;
    }

    @Override
    public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
        return null;
    }
}
