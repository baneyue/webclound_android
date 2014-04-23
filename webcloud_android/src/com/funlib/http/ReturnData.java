package com.funlib.http;

import java.io.InputStream;
import java.io.Serializable;

import org.apache.http.HttpEntity;

/**
 * 请求响应数据处理类。
 * 
 * @author zoubangyue
 */
public class ReturnData implements Serializable{
	@Override
    public String toString() {
        return "ReturnData [status=" + status + ", contentLength=" + contentLength + ", dataType=" + dataType
            + ", content=" + content + ", cacheHeader=" + cacheHeader + ", httpStatus=" + httpStatus + "]";
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private static final long serialVersionUID = -4408770885914141716L;
	/**请求失败常量*/
	public static final int FAIL = 0;
	/**客户端各种异常，主要是403,404*/
	public static final int SC_CLIENT_ERROR = 400;
	/**服务端各种异常，主要是500,504*/
	public static final int SC_SERVER_ERROR = 500;
	/**请求成功常量*/
	public static final int SC_OK = 200;
	/**请求返回状态成功或失败*/
	public int status;
	/**请求响应体的长度*/
	public long contentLength;
	
	/**------------------返回的各种数据分类---------------------*/
	/**数据类型*/
	public int dataType;
	/**字符串*/
	public static final int STRING = 0;
	/**字节数组*/
	public static final int BYTEARRAY = 1;
	/**流*/
	public static final int STREAM = 2;
	
	/**请求响应解析后的字符串数据，utf-8格式*/
	public String content;
	
	/**下载文件返回的响应流*/
	public InputStream is;
	/**如果是下载文件，那么这个实体为非空，在获取文件成功后必须调用consumeResponse方法以标示消费了实体*/
	public HttpEntity entity;
	
	/**字节数组*/
	public byte[] bytes;
	
	/**缓存头部cache-header*/
	public String cacheHeader;
	
	/**http响应码*/
	public int httpStatus;
	
	
}
