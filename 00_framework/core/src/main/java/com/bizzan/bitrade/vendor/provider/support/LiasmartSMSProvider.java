package com.bizzan.bitrade.vendor.provider.support;

import com.bizzan.bitrade.util.HttpSend;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author ai
 * @time 2018-04-05
 * Liasmart短信验证码发送类
 */
@Slf4j
public class LiasmartSMSProvider implements SMSProvider {

    private String username;
    private String password;
    private String gateway;

    public LiasmartSMSProvider(String username, String password, String gateway) {
        this.username = username;
        this.password = password;
        this.gateway = gateway;
    }

    private static Pattern RESPONSE_PATTERN = Pattern.compile("<response><error>(-?\\d+)</error><message>(.*[\\\\u4e00-\\\\u9fa5]*)</message></response>");


    public static String getName() {
        return "liasmart";
    }

    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws UnirestException {
        String textmessage = "【启动器KICK】" + content;

        String phonenumber = "86" + mobile;
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_id", username);
        map.put("api_password", password);
        map.put("phonenumber", phonenumber);
        map.put("sms_type", "T");
        map.put("encoding", "U");
        map.put("textmessage", textmessage);
        map.put("sender_id", "a\t【利亚智联】");
        JSONObject paramsMap = JSONObject.fromObject(map);
        String params = paramsMap.toString();
        String resultXml = HttpSend.LiasmartPost(gateway + "/api/SendSMS", params);
        log.info("result = {}", resultXml);

        return parseXml(resultXml);
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        String phonenumber = "86" + mobile;
        String textmessage = "【启动器KICK】" + content;
        System.out.print("!###############" + textmessage);
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_id", username);
        map.put("api_password", password);
        map.put("phonenumber", phonenumber);
        map.put("sms_type", "T");
        map.put("encoding", "U");
        map.put("textmessage", textmessage);
        map.put("sender_id", "a\t【利亚智联】");
        JSONObject paramsMap = JSONObject.fromObject(map);
        String params = paramsMap.toString();
        String resultXml = HttpSend.LiasmartPost(gateway + "/api/SendSMS", params);
        log.info("result = {}", resultXml);
        return parseXml(resultXml);
    }

    @Override
    public MessageResult sendInternationalMessage(String mobile, String code) throws IOException, DocumentException {
        return null;
    }

    /**
     * 获取验证码信息格式
     *
     * @param code
     * @return
     */
    @Override
    public String formatVerifyCode(String code) {
        return String.format("【启动器KICK】您的验证码是%s,5分钟内有效", code);
    }


    @Override
    public MessageResult sendVerifyMessage(String mobile, String verifyCode) throws Exception {
        String content = formatVerifyCode(verifyCode);
        String phonenumber = "86" + mobile;
        System.out.print("!###############" + content);
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_id", username);
        map.put("api_password", password);
        map.put("phonenumber", phonenumber);
        map.put("sms_type", "T");
        map.put("encoding", "U");
        map.put("textmessage", content);
        map.put("sender_id", "a\t【利亚智联】");
        JSONObject paramsMap = JSONObject.fromObject(map);
        String params = paramsMap.toString();
        String resultXml = HttpSend.LiasmartPost(gateway + "/api/SendSMS", params);
        log.info("result = {}", resultXml);
        return parseXml(resultXml);

    }

    private MessageResult parseXml(String xml) {
        JSONObject myJsonObject = JSONObject.fromObject(xml);
        String code = myJsonObject.getString("status");
        String msg = myJsonObject.getString("remarks");
        MessageResult result = new MessageResult(500, "系统错误");
        if (code.equals("S")) {
            result.setCode(0);
            result.setMessage(msg);
        }
        return result;
    }

    @Override
    public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
        String phonenumber = "86" + mobile;
        String textmessage = "【启动器KICK】" + content;
        System.out.print("!###############" + textmessage);
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_id", username);
        map.put("api_password", password);
        map.put("phonenumber", phonenumber);
        map.put("sms_type", "T");
        map.put("encoding", "U");
        map.put("textmessage", textmessage);
        map.put("sender_id", "a\t【利亚智联】");
        JSONObject paramsMap = JSONObject.fromObject(map);
        String params = paramsMap.toString();
        String resultXml = HttpSend.LiasmartPost(gateway + "/api/SendSMS", params);
        log.info("result = {}", resultXml);
        return parseXml(resultXml);
    }


}
