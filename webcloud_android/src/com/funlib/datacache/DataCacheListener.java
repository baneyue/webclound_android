package com.funlib.datacache;

/**
 * 获取内容监听
 * 
 * @author taojianli
 * 
 */
public interface DataCacheListener {

	/**
	 * 获取数据内容完成
	 * 
	 * @param error
	 *            　错误码，参见WDError
	 * @param listenerID
	 *            如果调用端，同时发送多个也去请求，可以在调用端维护唯一id，并传递，在业务处理结束后，带回
	 * @param data
	 *            　　获取到的数据
	 */
	public void getDataFinished(int error, int listenerID, String responseString , DataCache dataCache);

}
