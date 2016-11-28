package feimin.project.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * <p>请求参数。<p>
 *
 * 创建日期 2016年11月23日<br>
 * @author zhengpengfei<br>
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
public class RequestParams {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private ConcurrentHashMap<String, String> stringParams = new ConcurrentHashMap<String, String>();
    private Charset charset;

    /**
     * 编码方式
     */
    public RequestParams() {
        charset = RequestParams.DEFAULT_CHARSET;
    }

    public RequestParams(String key, String value) {
        this();
        stringParams.put(key, value);
    }

    public boolean containsKey(String key) {
        return stringParams.containsKey(key);
    }

    public void put(String key, String value) {
        stringParams.put(key, value);
    }

    public void put(String key, short value) {
        stringParams.put(key, Short.toString(value));
    }

    public void put(String key, int value) {
        stringParams.put(key, Integer.toString(value));
    }

    public void put(String key, double value) {
        stringParams.put(key, Double.toString(value));
    }

    public void put(String key, float value) {
        stringParams.put(key, Float.toString(value));
    }

    public void put(String key, long value) {
        stringParams.put(key, Long.toString(value));
    }

    public void put(String key, boolean value) {
        stringParams.put(key, Boolean.toString(value));
    }

    public void put(String key, char value) {
        stringParams.put(key, Character.toString(value));
    }

    public void put(Map<String, String> otherMap) {
        stringParams.putAll(otherMap);
    }

    public String getString(String key) {
        return stringParams.get(key);
    }

    public void remove(String key) {
        stringParams.remove(key);
    }

    public int size() {
        return stringParams.size();
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String toEncodedString() {
        try {
            StringBuilder encoded = new StringBuilder();
            for (ConcurrentHashMap.Entry<String, String> param : stringParams.entrySet()) {
                if (encoded.length() > 0) {
                    encoded.append("&");
                }
                encoded.append(URLEncoder.encode(param.getKey(), charset.name()));
                encoded.append("=");
                encoded.append(URLEncoder.encode(param.getValue(), charset.name()));
            }
            return encoded.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
