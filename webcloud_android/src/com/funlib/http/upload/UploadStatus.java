package com.funlib.http.upload;

/**
 * 上传状态。 
 * 
 * @author zoubangyue
 */
public class UploadStatus {
	/** 上传完成 */
	public static final int FINISH 				= 	0; 
	/** 取消上传 */
	public static final int CANCELED 			= 	2; 
	/** 正在上传 */
	public static final int UPLOADING			=	3; 
	/** 开始上传 */
	public static final int STARTUPLOADING 		= 	4;	
	
}
