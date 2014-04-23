package com.funlib.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtils {
    public static String getParamData(Map<String, String> params) {
        if(params == null) return null;
        StringBuffer content = new StringBuffer();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) params.get(key);
            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                content.append((i == 0 ? "" : "&") + key + "=");
            }

        }
        return content.toString();
    }
    
    public static Map<String, String> getRequestMap(String param) {
        Map<String, String> map = new HashMap<String, String>();
        if (param != null && !"".equals(param)) {
            //解析为键值对字符串
            String[] arr = param.split("&");
            for (String str1 : arr) {
                if (!"".equals(str1)) {
                    int eqIndex = str1.indexOf("=");
                    if (eqIndex != -1) {
                        String key = str1.substring(0, eqIndex);
                        String value = str1.substring(eqIndex + 1);
                        map.put(key, value);
                    }
                }
            }
        }
        return map;
    }
    
    /** 对get请求的参数进行编码。
     * @param result
     * @return
     */
    public static String encodeRequestParam(String result) {
        StringBuilder encodeParam = new StringBuilder();;
        try {
            if (result != null && !"".equals(result)) {
                //解析为键值对字符串
                String[] arr = result.split("&");
                for (String str1 : arr) {
                    if (!"".equals(str1)) {
                        int eqIndex = str1.indexOf("=");
                        if (eqIndex != -1) {
                            String key = str1.substring(0, eqIndex);
                            String value = str1.substring(eqIndex + 1);
                            //get请求参数双层编码
                            String eKey = URLEncoder.encode(key, "UTF-8");
                            String eValue = URLEncoder.encode(value, "UTF-8");
                            String eeKey = URLEncoder.encode(eKey, "UTF-8");
                            String eeValue = URLEncoder.encode(eValue, "UTF-8");
                            encodeParam.append(eeKey).append("=").append(eeValue).append("&"); 
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeParam.toString();
    }
    
    /** 转换请求参数。
	 * @param map
	 * @return
	 */
    public static List<NameValuePair> getNameValuePair(Map<String, String> map) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (map != null && !map.isEmpty()) {
			// list = new ArrayList<NameValuePair>();
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				list.add(new BasicNameValuePair(key, map.get(key)));
			}
		}
		return list;
	}
    
    public static String stream2string(InputStream is, String encode) {
        if (is != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, encode));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
