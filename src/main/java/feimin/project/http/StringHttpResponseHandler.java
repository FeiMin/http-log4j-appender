package feimin.project.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 *
 * <p>请求响应。<p>
 *
 * 创建日期 2016年11月23日<br>
 * @author zhengpengfei<br>
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
public abstract class StringHttpResponseHandler extends HttpResponseHandler {
    public static String DEFAULT_CHARSET = "UTF-8";

    /**
     * 返回编码方式
     * @param headers
     * @return
     */
    private static String extractContentCharset(Map<String, List<String>> headers) {
        List<String> contentTypes = headers.get("Content-Type");
        if (contentTypes != null) {
            String contentType = contentTypes.get(0);
            String charset = null;
            if (contentType != null) {
                for (String param : contentType.replace(" ", "").split(";")) {
                    if (param.startsWith("charset=")) {
                        charset = param.split("=", 2)[1];
                        break;
                    }
                }
            }
            return charset;
        }
        return StringHttpResponseHandler.DEFAULT_CHARSET; // No content type header, return the default
    }

    private static String getContentString(byte[] content, String charset) {
        if ((content == null) || (content.length == 0)) {
            return "";
        }
        if (charset == null) {
            charset = StringHttpResponseHandler.DEFAULT_CHARSET;
        }
        try {
            return new String(content, charset);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public void onSuccess(int statusCode, Map<String, List<String>> headers, byte[] content) {
        onSuccess(
                statusCode,
                headers,
                StringHttpResponseHandler.getContentString(content,
                        StringHttpResponseHandler.extractContentCharset(headers)));
    }

    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, String content);

    @Override
    public void onFailure(int statusCode, Map<String, List<String>> headers, byte[] content) {
        onFailure(
                statusCode,
                headers,
                StringHttpResponseHandler.getContentString(content,
                        StringHttpResponseHandler.extractContentCharset(headers)));
    }

    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, String content);

    @Override
    public abstract void onFailure(Throwable throwable);
}
