package com.funlib.datacache;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;

/*import com.funlib.basehttprequest.BaseHttpRequest;
import com.funlib.file.FileUtily;
import com.funlib.log.Log;*/

public class DataCache{}
    /*
	private int mReadTimeout 			= 	7000;		*//** 读取超时时间默认值 *//*
	private int mConnectionTimeout 		= 	7000;		*//** 连接超时时间默认值 *//*
	private int mFailRetryCount			=	2;			*//** 失败重试次数 *//*
	
	private Context mContext;
	private BaseHttpRequest mBaseHttpRequest;
	private DataCacheListener mDataCacheListener;
	private String mRequestUrl;
	private List<NameValuePair> mRequestParams;
	private int mListenerID;
	
	private int mNowRetryCount;
	private boolean bCanceled;
	
	private boolean bForceFromNet;	*//** 强制从网络获取新数据 *//*
	
	private static String sDataCachePath = "";
	static{
		
		String appPath = FileUtily.getAppSDPath();
		if(appPath != null){
			
			sDataCachePath = appPath + File.separator + "datacache" + File.separator;
			FileUtily.mkDir(sDataCachePath);
		}
	}
	
	*//**
	 * 
	 *//*
	public DataCache(){
		
		bForceFromNet = false;
	}
	
	*//**
	 * 获取数据对应的hashcode
	 * @return
	 *//*
	private static String hashString(String dataUrl){
		
		return dataUrl.hashCode()+".dat" ;
	}
	
	*//**
	 * 设置读操作超时时间，默认5s
	 * @param timeout
	 *//*
	public void setReadTimeout(int timeout){
		
		mReadTimeout = timeout;
	}
	
	*//**
	 * 设置链接操作超时时间，默认5s
	 * @param timeout
	 *//*
	public void setConnectionTimeout(int timeout){
		
		mConnectionTimeout = timeout;
	}
	
	*//**
	 * 设置超时失败后的重试次数，默认3次
	 * @param cnt
	 *//*
	public void setFailRetryCount(int cnt){
		
		mFailRetryCount = cnt;
	}
	
	*//**
	 * 
	 * @param listener
	 * @param listenerId
	 *            如果调用端，同时发送多个也去请求，可以在调用端维护唯一id，并传递，在业务处理结束后，带回
	 * @param requestUrl
	 *//*
	public void request(Context context, DataCacheListener listener, int listenerId,
			String requestUrl , List<NameValuePair> params) {
		
		this.mContext = context;
		this.mDataCacheListener = listener;
		this.mRequestParams = params;
		this.mRequestUrl = requestUrl;
		this.mListenerID = listenerId;
		
		try {
			
			new DataCacheTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
			//Log.e(this, "DataCacheTask", e);
		}
	}
	
	*//**
	 * 取消请求
	 *//*
	public void cancel(){
		
		bCanceled = true;
		if(mBaseHttpRequest != null)
			mBaseHttpRequest.cancel();
		
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	*//**
	 * 强制从网络获取数据
	 * @param force
	 *//*
	public void setForceFromNet(boolean force){
		
		this.bForceFromNet = force;
	}
	
	*//**
	 * 在本地缓存文件中查找
	 * @param dataUrl
	 * @return
	 *//*
	private DataCacheModel lookupInFiles(String dataUrl){
		
		return (DataCacheModel) FileUtily.getObject(sDataCachePath + hashString(dataUrl));
	}
	
	*//**
	 * 存储缓存到本地
	 * @param model
	 * @param dataUrl
	 *//*
	private void storeDataCahe(DataCacheModel model , String dataUrl){
		
		FileUtily.saveObject(sDataCachePath + hashString(dataUrl), model);
	}
	
	*//**
	 * 清除所有缓存
	 *//*
	public static void clearCache(Context context){

	}
	
	*//**
	 * 从http头里解析处最长有效时间
	 * @param response
	 * @return
	 *//*
	private long parserMaxAvailableTime(HttpResponse response){
		
		String str = null;
		Header[] tmpCacheControl = response.getHeaders("Cache-Control");
		for(int i = 0 ; i < tmpCacheControl.length ; ++i){
		    
		    Header header = tmpCacheControl[i];
		    String tmp = header.getValue();
		    if(tmp.contains("max-age")){
		        str = tmp;
		        break;
		    }
		}
		
		long time = 0;
		if(str != null){
			
			time = Long.parseLong(str);
		}
			
		return time;
	}
	
	*//**
	 * 从http头里解析处上次修改时间
	 * @param response
	 * @return
	 *//*
	private long parserLastModifiedTime(HttpResponse response){
	
		Header lastModifiedTime = response
				.getFirstHeader("Last-Modified");
		if (lastModifiedTime != null){
		    return Date.parse(lastModifiedTime.getValue());
		}
		
		return 0;
	}

	*//**
	 * 解析处cache-header字段
	 * @param response
	 * @return
	 *//*
	private String parserCacheHeader(HttpResponse response){
		
		String header = "";
		Header cacheHeader = response.getFirstHeader("cache-header");
		if(cacheHeader != null)
			header = cacheHeader.getValue();
		
		return header;
	}

	private class DataCacheTask extends AsyncTask<Void,Integer,Message>{

		@Override
		protected Message doInBackground(Void... params) {
			DataCacheModel ret = null;
			if(bForceFromNet == false){
				
				ret = lookupInFiles(mRequestUrl);
			}
			
			mBaseHttpRequest = new BaseHttpRequest(mContext);
			mBaseHttpRequest.setConnectionTimeout(mConnectionTimeout);
			mBaseHttpRequest.setReadTimeout(mReadTimeout);
			if(ret != null){
				
				if(TextUtils.isEmpty(ret.cacheHeader) == false){
					mBaseHttpRequest.setHeaderParam("cache-header", ret.cacheHeader);
				}
			}
			
			bCanceled = false;
			mNowRetryCount = 0;
			HttpResponse response = null;
			do {
				Log.d("DataCacheTask", "beginRequest" + mNowRetryCount+":"+System.currentTimeMillis());
				response = mBaseHttpRequest.request(mRequestUrl, mRequestParams);
				Log.d("DataCacheTask", "endRequest" + mNowRetryCount+":"+System.currentTimeMillis());
				if(response != null){
					break;
				}else{
					
					++mNowRetryCount;
				}
				
				if(bCanceled == true)
					break;
				
			} while (mNowRetryCount < mFailRetryCount);
			
			Message msg = Message.obtain();
			Log.d("DataCacheTask", "请求结束");
			if(bCanceled == true){
				Log.d("DataCacheTask", "请求被取消");
				
				msg.what = DataCacheError.CANCELED;
				msg.obj = null;
				
			}else{
				
				if(response == null){

					Log.d("DataCacheTask", "请求返回为空");
					
					msg.what = DataCacheError.FAIL;
					msg.obj = null;
					
					if(ret != null && ret.content != null){
						
						Log.d("DataCacheTask", "请求返回为空，使用缓存结果，ret:" + ret.content);
						msg.what = DataCacheError.SUCCESS;
						msg.obj = ret.content;
					}
				}else{
					
					int responseCode = response.getStatusLine().getStatusCode();
					Log.d("DataCacheTask", "请求返回，responseCode:" + responseCode);
					String cacheHeader = parserCacheHeader(response);
					if(ret != null && ret.cacheHeader != null && (responseCode == HttpStatus.SC_NOT_MODIFIED || (cacheHeader.equals(ret.cacheHeader) == true))){
						
						Log.d("DataCacheTask", "请求目标内容没有改变，使用缓存结果，ret:" + ret.content);
						msg.what = DataCacheError.SUCCESS;
						msg.obj = ret.content;
					}else if(responseCode != HttpStatus.SC_OK){
						
						Log.d("DataCacheTask", "请求状态值非200");
						msg.what = DataCacheError.FAIL;
						msg.obj = null;
						
						if(ret != null && ret.content != null){
							Log.d("DataCacheTask", "请求状态值非200，使用缓存结果，ret:" + ret.content);
							msg.what = DataCacheError.SUCCESS;
							msg.obj = ret.content;
						}
					}else{
						
						try {
							
							msg.what = DataCacheError.SUCCESS;
							msg.obj = EntityUtils.toString(response.getEntity(),"UTF-8");

							ret = new DataCacheModel();
							ret.content = (String)msg.obj;
							ret.cacheHeader = cacheHeader;
							
							storeDataCahe(ret, mRequestUrl);
							
							Log.d("DataCacheTask", "请求成功，ret:" + ret.content);
						} catch (Exception e) {
							Log.d("DataCacheTask", "请求成功，但处理数据失败，" , e);
//							e.printStackTrace();
							
							msg.what = DataCacheError.FAIL;
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
			
			String result = (String)msg.obj;
			if(TextUtils.isEmpty(result)){
				result = "";
			}
            if(mDataCacheListener != null){
            	Log.d("DataCacheTask", "mRequestUrl:"+mRequestUrl+",reqParams:"+ mRequestParams + ","+"what="+msg.what+",content="+msg.obj);
            	mDataCacheListener.getDataFinished(msg.what, mListenerID, result, DataCache.this);
            }
		}
		
	}
}*/
