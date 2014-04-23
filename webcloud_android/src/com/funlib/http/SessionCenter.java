package com.funlib.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;

import android.text.TextUtils;

/**
 * session管理。
 * 
 * @author zoubangyue
 */
public class SessionCenter {
	private static final String TAG = "SessionCenter";

	public static String JSESSION_ID = "";

	/**
	 * 从响应头中解析出sessionid字段。
	 * 
	 * @param response
	 * @return
	 */
	public static String parserJSESSIONID(HttpResponse response) {
		String header = "";
		if (response != null) {
			Header cacheHeader = response.getFirstHeader("sessionid");
			if (cacheHeader != null){
				header = cacheHeader.getValue();
				JSESSION_ID = header;
			}
		}
		return header;
	}
	
	/**
	 * 解析出cache-header字段
	 * 
	 * @param response
	 * @return
	 */
	public static String parserCacheHeader(HttpResponse response) {

		String header = "";
		Header cacheHeader = response.getFirstHeader("cache-header");
		if (cacheHeader != null)
			header = cacheHeader.getValue();
		return header;
	}

	/**
	 * 把sessionid添加到请求头中。
	 * 
	 * @param url
	 * @param httpRequest
	 */
	public static void putSessionToHeader(String url,HttpRequestBase httpRequest) {
		if (isNeedSession(url) && httpRequest != null) {
			BasicHeader header = new BasicHeader("Cookie", "JSESSIONID="+JSESSION_ID);
			httpRequest.addHeader(header);
		}
	}
	
	/**
	 * 向url中加sessionid。
	 * 
	 * @param url
	 * @return
	 */
	public static String addSessionToUrl(String url) {
			if (isNeedSession(url)) {
				String[] s = url.split("\\?");
				if (s != null && s.length >= 2) {
					url = s[0] + ";jsessionid=" + JSESSION_ID + "?";
					url += s[1];
				} else {
					url += ";jsessionid=" + JSESSION_ID + "?";
				}
			}
		return url;
	}
	
	/**
	 * 判断是否需要添加session到请求地址中。
	 * 
	 * 1.包含show.action的地址是请求图片服务器所以无需session
	 * 2.缓存的sessionid为空无需添加sessionid
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isNeedSession(String url){
		if (url != null && !url.contains("show.action")) {
			if(!TextUtils.isEmpty(JSESSION_ID))
				return true;
		}
		return false;
	}
}
