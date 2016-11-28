package feimin.project.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * <p>Http请求。<p>
 *
 * 创建日期 2016年11月23日<br>
 * @author zhengpengfei<br>
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
public abstract class HttpClient {
    public static final String DEFAULT_USER_AGENT = "Java-Async-Http";
    private final Map<String, String> headers;
    // milliseconds
    private int connectionTimeout = 10000;
    // milliseconds
    private int dataRetrievalTimeout = 10000;
    // 重定向
    private boolean followRedirects = true;

    public HttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        setUserAgent(HttpClient.DEFAULT_USER_AGENT);
    }

    /**
     * 构造http请求
     * @param url
     * @param method
     * @param params
     * @param handler
     */
    protected void request(String url, HttpRequestMethod method, RequestParams params, HttpResponseHandler handler) {
        HttpURLConnection urlConnection = null;
        if (params == null) {
            params = new RequestParams();
        }
        // 非post和get请求，将参数添加至url
        if ((method != HttpRequestMethod.POST) && (method != HttpRequestMethod.PUT)) {
            if (params.size() > 0) {
                url = url + "?" + params.toEncodedString();
            }
        }
        try {
            URL resourceUrl = new URL(url);
            urlConnection = (HttpURLConnection) resourceUrl.openConnection();
            // 请求设置
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.setReadTimeout(dataRetrievalTimeout);
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(followRedirects);
            urlConnection.setRequestMethod(method.toString());
            urlConnection.setDoInput(true);
            // 请求头
            for (Map.Entry<String, String> header : headers.entrySet()) {
                urlConnection.setRequestProperty(header.getKey(), header.getValue());
            }
            System.out.println("请求头------>>>:" + headers + "  " + urlConnection.getConnectTimeout());
            handler.onStart(urlConnection);
            // 请求体，param中有json,则传输json
            if ((method == HttpRequestMethod.POST) || (method == HttpRequestMethod.PUT)) {
                urlConnection.setDoOutput(true);
                //
                byte[] content = null;
                if (params.containsKey("json")) {
                    // json
                    String data = params.getString("json");
                    if (data == null) {
                        return;
                    }
                    content = data.getBytes();
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset="
                            + params.getCharset().name());
                    urlConnection.setRequestProperty("Content-Length", Long.toString(content.length));
                    urlConnection.setFixedLengthStreamingMode(content.length);
                } else {
                    // form-urlencoded
                    content = params.toEncodedString().getBytes();
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset="
                            + params.getCharset().name());
                    urlConnection.setRequestProperty("Content-Length", Long.toString(content.length));
                    urlConnection.setFixedLengthStreamingMode(content.length); // Stream the data so we don't run
                    // out of
                }
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(content);
                }
            }
            handler.processResponse(urlConnection);
            // Request finished
            handler.onFinish(urlConnection);
        } catch (IOException e) {
            handler.onFailure(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * delete请求
     * @param url
     * @param handler
     */
    public void delete(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.DELETE, null, handler);
    }

    /**
     * delete请求，含请求体
     * @param url
     * @param handler
     */
    public void delete(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.DELETE, params, handler);
    }

    /**
     * get请求
     * @param url
     * @param handler
     */
    public void get(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.GET, null, handler);
    }

    /**
     * get请求，param中存放url参数
     * @param url
     * @param params
     * @param handler
     */
    public void get(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.GET, params, handler);
    }

    /**
     * post请求
     * @param url
     * @param handler
     */
    public void post(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.POST, null, handler);
    }

    /**
     * post请求，参数
     * @param url
     * @param params
     * @param handler
     */
    public void post(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.POST, params, handler);
    }

    public void put(String url, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.PUT, null, handler);
    }

    public void put(String url, RequestParams params, HttpResponseHandler handler) {
        request(url, HttpRequestMethod.PUT, params, handler);
    }

    /**
     * 全局请求头
     * @param name
     * @param value
     */
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * 删除指定请求头
     * @param name
     */
    public void removeHeader(String name) {
        headers.remove(name);
    }

    /**
     * 获取user-agent
     * @return
     */
    public String getUserAgent() {
        return headers.get("User-Agent");
    }

    /**
     * 设置user-agent
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
    }

    /**
     * 获取connectionTimeout
     * @return
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置connectionTimeout
     * @param connectionTimeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 获取readTimeOut
     * @return
     */
    public int getDataRetrievalTimeout() {
        return dataRetrievalTimeout;
    }

    /**
     * 设置readTimeOut
     * @param dataRetrievalTimeout
     */
    public void setDataRetrievalTimeout(int dataRetrievalTimeout) {
        this.dataRetrievalTimeout = dataRetrievalTimeout;
    }

    /**
     * 获取重定向
     * @return
     */
    public boolean getFollowRedirects() {
        return followRedirects;
    }

    /**
     * 设置重定向
     * @param followRedirects
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * 设置认证
     * @param username
     * @param password
     */
    public void setBasicAuth(String username, String password) {
        String encoded = javax.xml.bind.DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
        headers.put("Authorization", "Basic " + encoded);
    }

    /**
     * 清空认证
     */
    public void clearBasicAuth() {
        headers.remove("Authorization");
    }
}
