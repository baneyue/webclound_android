package com.funlib.http.upload;

/**
 * 上传监听
 * 
 * @author taojianli
 */
public interface UploadListener {
	
	/**
	 * 上传状态改变。
	 * 1.用来通知ui状态改变，进度条
	 * 2.用来告知上传开始或结束的处理
	 * 
	 * @param statusCode
	 * @param percent
	 * @param obj
	 */
	public void onUploadStatusChanged(int statusCode , int percent , Object obj);
}