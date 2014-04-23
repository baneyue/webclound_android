package com.funlib.http.request;

public class RequestStatus {
    /** 业务请求开始 */
    public static final int START 		=   -1; 
	/** 业务请求受理失败 */
	public static final int FAIL 		= 	0; 
	/** 业务请求受理成功 */
	public static final int SUCCESS 	= 	1; 
	/** 取消业务请求 */
	public static final int CANCELED 	= 	2; 
}
