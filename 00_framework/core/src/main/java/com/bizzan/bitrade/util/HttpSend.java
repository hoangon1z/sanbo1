package com.bizzan.bitrade.util;


import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * @author tansitao
 * @time 2018-04-05
 * http短信接口访问工具
 */
public class HttpSend {


    /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

    public static String yunpianPost(String url, Map<String, String> paramsMap) {
        HttpClient client = new HttpClient();
        try {
            PostMethod method = new PostMethod(url);
            if (paramsMap != null) {
                NameValuePair[] namePairs = new NameValuePair[paramsMap.size()];
                int i = 0;
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new NameValuePair(param.getKey(),
                            param.getValue());
                    namePairs[i++] = pair;
                }
                method.setRequestBody(namePairs);
                HttpMethodParams param = method.getParams();
                param.setContentCharset("utf-8");
            }
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String LiasmartPost(String urlStr, String params) {
        HttpURLConnection connection = null;
        InputStream is = null;
        InputStreamReader rsd = null;
        BufferedReader br = null;
        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        StringBuffer sb = null;

        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false); //不缓存
            connection.setRequestProperty("connection", "Keep-Alive");  //设置保活连接
            connection.setRequestProperty("charset", "UTF-8");          //提交的数据编码
            connection.setRequestProperty("Content-type", "application/json");  //提交的数据格式
            connection.setRequestProperty("accept", "application/json");        //接收的数据格式
            connection.setConnectTimeout(30000);    //30秒连接超时
            connection.setReadTimeout(30000);       //30秒读取超时
            connection.connect();
            if (params != null && !"".equals(params)) {
                os = connection.getOutputStream();
                osw = new OutputStreamWriter(os);
                bw = new BufferedWriter(osw);
                bw.write(params);
                bw.flush();
            }
            int status = connection.getResponseCode();
            if (status == 200) {
                is = connection.getInputStream();
                rsd = new InputStreamReader(is, "UTF-8");
                br = new BufferedReader(rsd);
                sb = new StringBuffer();
                String s;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
            } else {
                return "{\"ResuleState\":\"-1\",\"Msg\":\"连接异常\"}";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (rsd != null) rsd.close();
                if (is != null) is.close();
                if (bw != null) bw.close();
                if (osw != null) osw.close();
                if (os != null) os.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
