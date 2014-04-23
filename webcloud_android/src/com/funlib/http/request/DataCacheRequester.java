package com.funlib.http.request;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;

import com.funlib.file.FileUtily;
import com.funlib.http.HttpRequestImpl;
import com.funlib.http.ReturnData;
import com.funlib.log.Log;

public class DataCacheRequester {

	private int mFailRetryCount = 2;
	/** 失败重试次数 */

	private Context mContext;
	private HttpRequestImpl mRequest;
	private DataCacheRequesterListener mDataCacheListener;
	private String mRequestUrl;
	private Map<String, String> mRequestParams;
	private int mListenerID;

	private int mNowRetryCount;

	private boolean bCanceled;

	private boolean bForceFromNet;
	/** 强制从网络获取新数据 */

	private static String sDataCachePath = "";

	static {

		String appPath = FileUtily.getAppSDPath();
		if (appPath != null) {

			sDataCachePath = appPath + File.separator + "datacache" + File.separator;
			FileUtily.mkDir(sDataCachePath);
		}
	}

	public DataCacheRequester(Context context) {
		this.bForceFromNet = false;
		this.mContext = context;
	}

	public DataCacheRequester(Context context, boolean bForceFromNet) {
		this.bForceFromNet = bForceFromNet;
		this.mContext = context;
	}

	/**
	 * 获取数据对应的hashcode
	 * 
	 * @return
	 */
	private static String hashString(String dataUrl) {

		return dataUrl.hashCode() + ".dat";
	}

	/**
	 * 设置超时失败后的重试次数，默认2次
	 * 
	 * @param cnt
	 */
	public void setFailRetryCount(int cnt) {

		mFailRetryCount = cnt;
	}

	/**
	 * 
	 * @param listener
	 * @param listenerId
	 *            如果调用端，同时发送多个也去请求，可以在调用端维护唯一id，并传递，在业务处理结束后，带回
	 * @param requestUrl
	 */
	public void request(DataCacheRequesterListener listener, int listenerId, String requestUrl,
			Map<String, String> params) {

		this.mDataCacheListener = listener;
		this.mRequestParams = params;
		this.mRequestUrl = requestUrl;
		this.mListenerID = listenerId;

		try {
			new DataCacheTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("DataCacheRequester", "请求异常", e);
		}
	}

	/**
	 * 取消请求
	 */
	public void cancel() {
		bCanceled = true;
		if (mRequest != null)
			mRequest.cancel();
	}

	/**
	 * 强制从网络获取数据
	 * 
	 * @param force
	 */
	public void setForceFromNet(boolean force) {

		this.bForceFromNet = force;
	}

	/**
	 * 在本地缓存文件中查找
	 * 
	 * @param dataUrl
	 * @return
	 */
	private DataCacheModel lookupInFiles(String dataUrl) {

		return (DataCacheModel) FileUtily.getObject(sDataCachePath + hashString(dataUrl));
	}

	/**
	 * 存储缓存到本地
	 * 
	 * @param model
	 * @param dataUrl
	 */
	private void storeDataCahe(DataCacheModel model, String dataUrl) {

		FileUtily.saveObject(sDataCachePath + hashString(dataUrl), model);
	}

	/**
	 * 清除所有缓存
	 */
	public static void clearCache(Context context) {

	}

	/**
	 * 从http头里解析出最长有效时间
	 * 
	 * @param response
	 * @return
	 */
	private long parserMaxAvailableTime(HttpResponse response) {

		String str = null;
		Header[] tmpCacheControl = response.getHeaders("Cache-Control");
		for (int i = 0; i < tmpCacheControl.length; ++i) {
			Header header = tmpCacheControl[i];
			String tmp = header.getValue();
			if (tmp.contains("max-age")) {
				str = tmp;
				break;
			}
		}
		long time = 0;
		if (str != null) {
			time = Long.parseLong(str);
		}
		return time;
	}

	/**
	 * 从http头里解析出上次修改时间
	 * 
	 * @param response
	 * @return
	 */
	private long parserLastModifiedTime(HttpResponse response) {

		Header lastModifiedTime = response.getFirstHeader("Last-Modified");
		if (lastModifiedTime != null) {
			return Date.parse(lastModifiedTime.getValue());
		}

		return 0;
	}

	/**
	 * 解析出cache-header字段
	 * 
	 * @param response
	 * @return
	 */
	private String parserCacheHeader(HttpResponse response) {

		String header = "";
		Header cacheHeader = response.getFirstHeader("cache-header");
		if (cacheHeader != null)
			header = cacheHeader.getValue();

		return header;
	}

	private class DataCacheTask extends AsyncTask<Void, Integer, Message> {

		@Override
		protected Message doInBackground(Void... params) {
			DataCacheModel ret = null;
			if (bForceFromNet == false) {
				ret = lookupInFiles(mRequestUrl);
			}

			mRequest = new HttpRequestImpl(mContext);
			if (ret != null) {
				if (!TextUtils.isEmpty(ret.cacheHeader)) {
					mRequest.addHeader("cache-header", ret.cacheHeader);
				}
			}

			bCanceled = false;
			mNowRetryCount = 0;
			ReturnData data;
			do {
				Log.d("DataCacheTask", mNowRetryCount + "次请求");
				Log.d("DataCacheTask",
						"beginRequest" + mNowRetryCount + ":" + System.currentTimeMillis());
				data = mRequest.doPostRequest(mRequestUrl, mRequestParams);
				Log.d("DataCacheTask",
						"endRequest" + mNowRetryCount + ":" + System.currentTimeMillis());
				if (data != null && (data.httpStatus == HttpStatus.SC_NOT_MODIFIED || data.httpStatus == HttpStatus.SC_OK)) {
					break;
				} else {
					++mNowRetryCount;
				}
				if (bCanceled == true)
					break;
			} while (mNowRetryCount < mFailRetryCount);
			Message msg = Message.obtain();
			Log.d("DataCacheTask.doInBackground()", "请求结束");
			if (bCanceled == true) {
				Log.d("DataCacheTask.doInBackground()", "请求被取消");
				msg.what = DataCacheRequesterStatus.CANCELED;
				msg.obj = null;
			} else {
				if (data == null) {
					Log.d("DataCacheTask.doInBackground()", "请求失败");
					msg.what = DataCacheRequesterStatus.FAIL;
					msg.obj = null;
					if (ret != null && ret.content != null) {
						Log.d("DataCacheTask.doInBackground()", "请求失败，使用缓存结果，ret:" + ret.content);
						msg.what = DataCacheRequesterStatus.SUCCESS;
						msg.obj = ret.content;
					}
				} else {
					//缓存头有值，响应缓存头也有值，返回状态无内容改变
					if (ret != null
							&& ret.cacheHeader != null
							&& (data.httpStatus == HttpStatus.SC_NOT_MODIFIED || data.cacheHeader.equals(ret.cacheHeader))) {

						Log.d("DataCacheTask.doInBackground()", "请求目标内容没有改变，使用缓存结果，ret:" + ret.content);
						msg.what = DataCacheRequesterStatus.SUCCESS;
						msg.obj = ret.content;
					} else if (data.httpStatus != HttpStatus.SC_OK) {
						//请求失败的情形
						Log.d("DataCacheTask.doInBackground()", "请求状态值非200");
						msg.what = DataCacheRequesterStatus.FAIL;
						msg.obj = null;

						if (ret != null && ret.content != null) {
							Log.d("DataCacheTask.doInBackground()", "请求状态值非200，使用缓存结果，ret:" + ret.content);
							msg.what = DataCacheRequesterStatus.SUCCESS;
							msg.obj = ret.content;
						}
					} else {
						//请求成功的情形
						try {
							msg.what = DataCacheRequesterStatus.SUCCESS;
							msg.obj = data.content;

							ret = new DataCacheModel();
							
							ret.content = data.content;
							ret.cacheHeader = data.cacheHeader;

							//把数据缓存起来
							storeDataCahe(ret, mRequestUrl);

							Log.d("DataCacheTask.doInBackground()", "请求成功，ret:" + ret.content);
						} catch (Exception e) {
							Log.d("DataCacheTask.doInBackground()", "请求成功，但处理数据失败，", e);
							e.printStackTrace();
							msg.what = DataCacheRequesterStatus.FAIL;
							msg.obj = null;
						}
					}
				}
			}
			return msg;
		}

		@Override
		protected void onPostExecute(Message msg) {
			super.onPostExecute(msg);

			String result = (String) msg.obj;
			if (TextUtils.isEmpty(result)) {
				result = "";
			}
			if (mDataCacheListener != null) {
				Log.d("DataCacheTask", "mRequestUrl:" + mRequestUrl + ",reqParams:"
						+ mRequestParams + "," + "what=" + msg.what + ",content=" + msg.obj);
				mDataCacheListener.getDataFinished(msg.what, mListenerID, result, DataCacheRequester.this);
			}
		}

	}
}
