package com.funlib.http.request;

import java.util.Map;

import com.webcloud.define.HttpUrlImpl;

/**
 * 业务http请求监听
 * 
 * @author taojianli
 * 
 */
public interface RequestListener{

	/**
	 * 请求回调。
	 * 
	 * @param statusCode
	 *            状态码
	 * @param requestId
	 *            区分请求号
	 * @param responseString
	 */
	public void requestStatusChanged(int statusCode, HttpUrlImpl requestId,
			String responseString,Map<String,String> requestParams);
	
}
