package top.daheizi.commons.util;

import java.io.IOException;  
import java.util.Map;  

import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
  
import org.apache.http.util.EntityUtils;

/**
 * 客户端Http(s)请求工具
 * @author daheizi
 * @Date 2018年1月18日 上午2:56:01
 */
public class HttpClientUtil {
    private static final String UTF_8 = "UTF-8";
    private static final String ERROR_STR = "";
    
    /**
     * 发送Get请求
     * @param url
     * @param headers
     * @return
     * @Date 2018年1月26日 上午1:00:31
     */
    public static String doGet(String url, Map<String, String> headers) {
        // 创建GET请求
        HttpGet httpGet = new HttpGet(url);
        // 添加请求头
        setHeaders(httpGet, headers);
        return getResult(httpGet);
    }
    
    /**
     * 发送Post请求
     * @param url
     * @param headers
     * @param content
     * @param contentType
     * @return
     * @Date 2018年1月27日 上午4:17:44
     */
    public static String doPost(String url, Map<String, String> headers, 
            String content, ContentType contentType) {
        // 创建Http Post请求
        HttpPost httpPost = new HttpPost(url);
        // 添加请求头
        setHeaders(httpPost, headers);
        // 添加内容
        StringEntity entity = new StringEntity(content, contentType);
        httpPost.setEntity(entity);
        return getResult(httpPost);
    }
    
    /**
     * 获取HttpClient
     * @return
     * @Date 2018年1月27日 上午2:35:39
     */
    private static CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
    
    /**
     * 设置请求头
     * @param request
     * @param headers
     * @Date 2018年1月26日 上午1:43:17
     */
    private static void setHeaders(HttpRequest request, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.addHeader(header.getKey(), header.getValue());
            }
        }
    }
    
    /**
     * 执行请求并获取返回消息
     * @param request
     * @return
     * @Date 2018年1月27日 上午2:54:27
     */
    private static String getResult(HttpRequestBase request) {
        String resultMsg = ERROR_STR;
        // 获取httpClient
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(request);
            // 200 OK
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                resultMsg = EntityUtils.toString(response.getEntity(), UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMsg;  
    }
}  
