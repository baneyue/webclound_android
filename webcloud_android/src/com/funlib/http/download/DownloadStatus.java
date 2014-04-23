package com.funlib.http.download;

/**
 * 下载状态
 * @author feng
 *
 */
public class DownloadStatus {

    public static final int STATUS_STORAGE_FULL         				=       0;  // 没有足够的存储空间
    public static final int STATUS_NO_SDCARD            				=       1;  // 没有插入SD卡
    public static final int STATUS_PAUSE                				=       2;  // 暂停
    public static final int STATUS_DOWNLOADING          				=       3;  // 正在下载
    public static final int STATUS_WAITTING             				=       4;  // 等待下载
    public static final int STATUS_COMPLETE             				=       5;  // 下载完成
    public static final int STATUS_UNKNOWN              				=       6;  // 未知错误
    public static final int STATUS_CANCELED             				=       7;  // 取消下载任务
    public static final int STATUS_NOT_EXISTS           				=       8;  // 下载任务不存在,可以添加下载
    public static final int STATUS_DOWNLOAD_FILE_NOT_FOUND             	=       9;  // 文件不存在
    public static final int STATUS_STARTDOWNLOADING						=		10; // 将要开始下载
    public static final int STATUS_NETERROR         					=		11; // 将要开始下载
    
}
