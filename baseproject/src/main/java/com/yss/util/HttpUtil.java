/**
 *
 */
package com.yss.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shuoshi.yan
 * @Description: HTTP
 * @date 20120/08/06
 */
@Slf4j
public class HttpUtil {

    /**
     * @param url
     * @param json
     * @return String
     * @throws
     * @Description: post请求, 发送json格式
     */
    public static String post(String url, String json) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                //setConnectTimeout：设置连接超时时间，单位毫秒
                .setConnectTimeout(15000)
                //setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒
//                .setConnectionRequestTimeout(5000)
                //setSocketTimeout：请求获取数据的超时时间，单位毫秒
                .setSocketTimeout(15000).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        log.info("请求地址"+url+"请求参数= = ="+json);
        StringEntity stringEntity = new StringEntity(json, Charset.forName("utf-8"));
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "utf-8");
        log.info("返回结果+ + +" + responseContent);
        response.close();
        httpClient.close();
        return responseContent;
    }


    /**
     * @author:shuyu.wang
     * @description:发送key_value形式的post请求
     * @date: 2020/08/06
     */
    public static String postKeyValue(String url, Map<String, String> paramsMap) {
        String result = "";
        try {
            DefaultHttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                parameters.add(basicNameValuePair);
            }
            UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(parameters,"utf-8");
            // 提交表单类型的参数
            post.setEntity(reqEntity);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity resEntity = response.getEntity();
                result = EntityUtils.toString(resEntity);
                log.info("返回结果：" + result);
            }
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
        return result;
    }

    /**
     * get请求
     * @param url
     * @param param
     * @return
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            log.info("urlNameString--->" + urlNameString);
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                log.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
