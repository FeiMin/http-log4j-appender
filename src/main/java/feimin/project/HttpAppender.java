package feimin.project;

import java.util.List;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import feimin.project.http.AsyncHttpClient;
import feimin.project.http.HttpClient;
import feimin.project.http.RequestParams;
import feimin.project.http.StringHttpResponseHandler;

/**
 *
 * <p>httpAppender。<p>
 *
 * 创建日期 2016年11月23日<br>
 * @author zhengpengfei<br>
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
public class HttpAppender extends AppenderSkeleton {
    private final HttpClient httpClient;
    // 直接配置服务端url
    private String url;
    // 毫秒
    private int connectionTimeout;
    // 毫秒
    private int readTimeOut;
    // 用户名
    private String userName;
    // 密码
    private String password;

    public HttpAppender() {
        httpClient = new AsyncHttpClient();
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    protected void append(LoggingEvent event) {
        if (!checkConditions()) {
            return;
        }
        StringBuilder builder = new StringBuilder(1024);
        builder.append(layout.format(event));
        if (layout.ignoresThrowable()) {
            String[] throwableStrRep = event.getThrowableStrRep();
            if (throwableStrRep != null) {
                for (String line : throwableStrRep) {
                    builder.append(line);
                    builder.append(Layout.LINE_SEP);
                }
            }
        }
        sendToHttpServer(builder.toString());
    }

    /**
     * 检查必要条件
     * @return
     */
    private boolean checkConditions() {
        if (httpClient == null) {
            LogLog.warn("HttpClient not initialized.");
            return false;
        }
        if ((url == null) || url.trim().isEmpty()) {
            LogLog.warn("url not initialized.");
            return false;
        }
        return true;
    }

    /**
     * 发送数据
     * @param data
     */
    private void sendToHttpServer(String data) {
        RequestParams params = new RequestParams();
        params.put("json", data);
        httpClient.post(url, params, new StringHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {}

            @Override
            public void onFailure(Throwable throwable) {}

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {}
        });
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        if (connectionTimeout > 1000) {
            httpClient.setConnectionTimeout(connectionTimeout);
        }
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeOut(int readTimeOut) {
        if (readTimeOut > 1000) {
            httpClient.setDataRetrievalTimeout(readTimeOut);
        }
        this.readTimeOut = readTimeOut;
    }

    public void setUserName(String userName) {
        if ((userName != null) && !userName.trim().isEmpty()) {
            httpClient.setHeader("userName", userName);
        }
        this.userName = userName;
    }

    public void setPassword(String password) {
        if ((password != null) && !password.trim().isEmpty()) {
            httpClient.setHeader("password", password);
        }
        this.password = password;
    }
}
