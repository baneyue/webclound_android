package com.funlib.http.upload;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;

import com.funlib.http.ReturnData;
import com.funlib.http.upload.CountMultipartEntity.ProgressListener;
import com.funlib.utily.FlowController;

/**
 * 新的经过优化的上传器。
 * 1.上传文件列表
 * 2.上传流列表
 * 3.支持上传进度消息通知
 * 4.对网络访问返回状态和数据进行封装
 * 
 * 注意：使用时每次上传都应当创建新的实例，以防串号。
 * 
 * @author zoubangyue
 */
public class Uploader extends BaseUploader {
	private UploaderTask mTask;
	
	private String mUploadUrl;
	
	private File[] files;
	
	private String[] fileNames;
	private InputStream[] iss;
	private Map<String,String> params;
	
	public static final int UPLOAD_FILE = 2;
	public static final int UPLOAD_STREAM = 1;
	private int uploadType;

	public Uploader(Context mContext, UploadListener mUploadlistener) {
		super(mContext, mUploadlistener);
	}

	@Override
	public void upLoadStream(String serverUrl, Map<String,String> params, InputStream[] iss, String[] fileNames) {
		if (mUploadlistener != null) {
			mUploadlistener.onUploadStatusChanged(UploadStatus.STARTUPLOADING,
					0, null);
		}
		this.iss = iss;
		this.params = params;
		this.fileNames = fileNames;
		this.uploadType = UPLOAD_STREAM;
		mTask = new UploaderTask();
		mTask.execute();
	}

	@Override
	public void upLoadFile(String serverUrl, Map<String,String> params, File[] files, String[] fileNames) {
		if (mUploadlistener != null) {
			mUploadlistener.onUploadStatusChanged(UploadStatus.STARTUPLOADING,
					0, null);
		}
		this.files = files;
		this.params = params;
		this.fileNames = fileNames;
		this.uploadType = UPLOAD_FILE;
		mTask = new UploaderTask();
		mTask.execute();
	}

	protected class UploaderTask extends AsyncTask<Object, Integer, ReturnData> {
		//上传体总长度
		long totalSize;

		@Override
		protected void onPreExecute() {
			
			if(mUploadlistener != null)
				mUploadlistener.onUploadStatusChanged(UploadStatus.STARTUPLOADING, 0, null);
		}

		@Override
		protected ReturnData doInBackground(Object... arg0) {
			ReturnData data = null;
			try {
				if(UPLOAD_FILE == uploadType){
					for(File file : files){
						totalSize += file.length();
					}
					data = Uploader.this.mRequest.doPostUploadFileRequest(mUploadUrl, params,new ProgressListener() {
						@Override
						public void transferred(long transferred,long totalSize) {
							int percent = (int)(((float)transferred / (float)totalSize) * 100);
							publishProgress(percent);
						}
					}, files, fileNames);
				}else if(UPLOAD_STREAM == uploadType){
					for(InputStream is : iss){
						totalSize += is.available();
					}
					data = Uploader.this.mRequest.doPostUploadStreamRequest(mUploadUrl, params,new ProgressListener() {
						@Override
						public void transferred(long transferred,long totalSize) {
							int percent = (int)(((float)transferred / (float)totalSize) * 100);
							publishProgress(percent);
						}
					}, iss, fileNames);
				}
				
				//上传成功记录上传的总流量
				if(data != null && data.status == ReturnData.SC_OK){
					FlowController.count(FlowController.FLOW_UP, totalSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return data;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			if(mUploadlistener != null)
				mUploadlistener.onUploadStatusChanged(UploadStatus.UPLOADING, (int)progress[0], null);
		}

		@Override
		protected void onPostExecute(ReturnData data) {
			if(mUploadlistener != null){
				if(data == null)
					data = new ReturnData();
				mUploadlistener.onUploadStatusChanged(UploadStatus.FINISH, 100, data);
			}
		}
	}
}
