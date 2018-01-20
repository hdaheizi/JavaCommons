package org.daheiz.base.util;

import java.io.IOException;  
import java.io.UnsupportedEncodingException;  
import java.net.URISyntaxException;  
import java.util.ArrayList;  
import java.util.Map;  

import org.apache.http.HttpEntity;  
import org.apache.http.NameValuePair;  
import org.apache.http.client.ClientProtocolException;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.CloseableHttpResponse;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.client.methods.HttpRequestBase;  
import org.apache.http.client.utils.URIBuilder;  
import org.apache.http.impl.client.CloseableHttpClient;  
import org.apache.http.impl.client.HttpClients;  
  
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.util.EntityUtils;

/**
 * 客户端Http(s)请求工具
 * @author daheiz
 * @Date 2018年1月18日 上午2:56:01
 */
public class HttpClientUtil {
    private static String UTF_8 = "UTF-8";
    
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
    
    public static String httpGetRequest(String url) {  
        HttpGet httpGet = new HttpGet(url);  
        return getResult(httpGet);  
    }  
  
    public static String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException {  
        URIBuilder ub = new URIBuilder();  
        ub.setPath(url);  
  
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        ub.setParameters(pairs);  
  
        HttpGet httpGet = new HttpGet(ub.build());  
        return getResult(httpGet);  
    }  
  
    public static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)  
            throws URISyntaxException {  
        URIBuilder ub = new URIBuilder();  
        ub.setPath(url);  
         
  
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        ub.setParameters(pairs);  
  
        HttpGet httpGet = new HttpGet(ub.build());  
        for (Map.Entry<String, Object> param : headers.entrySet()) {  
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));  
        }  
        return getResult(httpGet);  
    }  
  
    public static String httpPostRequest(String url) {  
        HttpPost httpPost = new HttpPost(url);  
        return getResult(httpPost);  
    }  
  
    public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {  
        HttpPost httpPost = new HttpPost(url);  
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));  
        return getResult(httpPost);  
    }  
  
    public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)  
            throws UnsupportedEncodingException {  
        HttpPost httpPost = new HttpPost(url);  
  
        for (Map.Entry<String, Object> param : headers.entrySet()) {  
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));  
        }  
  
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));  
  
        return getResult(httpPost);  
    }  
  
    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {  
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();  
        for (Map.Entry<String, Object> param : params.entrySet()) {  
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));  
        }  
  
        return pairs;  
    }
    
    private static String getResult(HttpRequestBase request) {  
        // CloseableHttpClient httpClient = HttpClients.createDefault();  
        CloseableHttpClient httpClient = getHttpClient();  
        try {  
            CloseableHttpResponse response = httpClient.execute(request);  
            // response.getStatusLine().getStatusCode();  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                // long len = entity.getContentLength();// -1 表示长度未知  
                String result = EntityUtils.toString(entity);  
                response.close();  
                // httpClient.close();  
                return result;  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
  
        }  
  
        return null;  
    }  
  
}  
