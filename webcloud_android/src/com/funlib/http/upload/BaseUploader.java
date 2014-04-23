package com.funlib.http.upload;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import android.content.Context;

import com.funlib.http.HttpRequestImpl;

/**
 * 上传基类。
 * 
 * @author zoubangyue
 */
public abstract class BaseUploader {

	protected UploadListener mUploadlistener;

	protected Context mContext;

	protected HttpRequestImpl mRequest;

	public BaseUploader(Context mContext, UploadListener mUploadlistener) {
		super();
		this.mUploadlistener = mUploadlistener;
		this.mContext = mContext;
		this.mRequest = new HttpRequestImpl(mContext);
	}

	public abstract void upLoadFile(String serverUrl, Map<String, String> params, File[] files, String[] fileNames);

	public abstract void upLoadStream(String serverUrl, Map<String, String> params, InputStream[] iss, String[] fileNames);

	public void cancel() {
		mRequest.cancel();
	}
}
