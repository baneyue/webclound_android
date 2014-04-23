package com.funlib.http.download;

/**
 * 下载进度，状态，监听
 * 
 * @author taojianli
 * 
 */
public interface DownloadListener {

	/**
	 * 下载状态改变
	 * @param tag TODO
	 * @param index 下载任务的索引
	 * @param status
	 * @param filePath
	 *            文件保存路径
	 */
	public void onDownloadStatusChanged(Object tag , int index, int status , int percent, String filePath);

}
