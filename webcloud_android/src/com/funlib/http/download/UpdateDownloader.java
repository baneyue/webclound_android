package com.funlib.http.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.os.Handler;
import android.os.Message;

import com.funlib.log.Log;
import com.funlib.network.NetWork;
import com.funlib.utily.FlowController;
import com.funlib.utily.Utily;
import com.webcloud.WebCloudApplication;

public class UpdateDownloader implements Runnable {

    public static final int HTTP_CONNECTION_TIMEOUT = 40*1000;
	private static final int BUFFER_SIZE = 1024;//缓冲大小
	private static final int HTTP_CONNECTION_RETRY_COUNT = 2;//缓冲大小
	private static final String TAG = "UpdateDownloader";

	private boolean bCanceled;
	private boolean bPaused;
	private String mUrl;
	private DownloadListener mDownloadListener;
	private String mFilePath;
	private Handler mHandler;
	private int mDownloadPercent;
	private int mPreDownloadPercent;
	private InputStream mDownloadInputStream = null;
	
	private Object mUpdateTag;

	public UpdateDownloader() {
		bCanceled = false;
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if(mDownloadListener != null)
				    mDownloadListener.onDownloadStatusChanged(mUpdateTag , 0, msg.what, (Integer) msg.obj, mFilePath);
			}
		};
	}

	public void download(Object tag, String url, DownloadListener listener, String filePath) {

		mUpdateTag = tag;
		mUrl = url;
		mDownloadListener = listener;
		mFilePath = filePath;
		bCanceled = false;
		bPaused = false;
		mDownloadPercent = 0;
        mPreDownloadPercent = 0;

		new Thread(this).start();
	}

	public void canceled() {
		bCanceled = true;
		
		try{
		    
		    if(mDownloadInputStream != null)
	            mDownloadInputStream.close();
		}catch(Exception e){
		    
		}
	}

	public void pause() {
		bPaused = true;
	}

	public void resume() {
		bPaused = false;
	}
	
    public void run() {
    	long readTotalCnt = 0;//流量统计
    	BufferedInputStream bis = null;
    	RandomAccessFile accessFile = null;
    	FileChannel fileChannel = null;
    	MappedByteBuffer mbbo = null;
    	File file = null;
    	HttpURLConnection conn = null;
    	boolean downloadComplete = false;
        try {
			Message msg = mHandler.obtainMessage();
			if (null == Utily.getSDPath()) {
			    msg.what = DownloadStatus.STATUS_NO_SDCARD;
			    msg.obj = mDownloadPercent;
			    mHandler.sendMessage(msg);
			    return;
			}
			

			//线程一开始运行，即可发送STATUS_DOWNLOADING消息
			msg = mHandler.obtainMessage();
			msg.what = DownloadStatus.STATUS_STARTDOWNLOADING;
			msg.obj = mDownloadPercent;
			mHandler.sendMessage(msg);

			file = new File(mFilePath);
			URL url = null;
			boolean bInitConnection = false;
			byte[] buffer = new byte[BUFFER_SIZE];
			int retryCount = 0;
			int fileSize = 0;
			int blockCount = 0;
			boolean errorOccur = true;
			do {

			    try {

			    	//没有初始化相关资源，开始初始化
			        if (bInitConnection == false) {

			            if (accessFile != null)
			                accessFile.close();

			            if (mDownloadInputStream != null)
			                mDownloadInputStream.close();
			            if (conn != null)
			                conn.disconnect();

			            if(file.exists())
			                file.delete();
			            
			            url = new URL(mUrl);
			            if(NetWork.isDefaultWap(WebCloudApplication.getInstance())){
			            	Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,new InetSocketAddress(NetWork.getDefaultWapProxy(), NetWork.getDefaultWapPort()));
			            	conn = (HttpURLConnection) url.openConnection(proxy);
			            }else{
			            	conn = (HttpURLConnection) url.openConnection();
			            }
			            conn.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
			            conn.setReadTimeout(HTTP_CONNECTION_TIMEOUT);
			            conn.connect();
			            
			            mDownloadInputStream = conn.getInputStream();
			            bis = new BufferedInputStream(mDownloadInputStream);

			            fileSize = conn.getContentLength();
			            //创建随机访问文件
			            try {
			                accessFile = new RandomAccessFile(file, "rwd");
			                fileChannel = accessFile.getChannel();
			                //缓冲大小为即将下载的文件大小
			                mbbo = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
			            } catch (Exception e) {
			                e.printStackTrace();

			                msg = mHandler.obtainMessage();
			                msg.what = DownloadStatus.STATUS_UNKNOWN;
			                msg.obj = mDownloadPercent;
			                mHandler.sendMessage(msg);
			                return;
			            }
			            
			            Log.d(TAG, "file size " + fileSize);
			            if (false == Utily.checkSDStorageAvailable(fileSize)) {
			                
			            	msg = mHandler.obtainMessage();
			                msg.what = DownloadStatus.STATUS_STORAGE_FULL;
			                msg.obj = mDownloadPercent;
			                mHandler.sendMessage(msg);
			                
			                return;
			            }

			            //初始化完毕
			            bInitConnection = true;
			        } else {

			            if (bPaused == false) {
			                int realReadCnt = bis.read(buffer);
			                if (realReadCnt == -1) {// 读完

			                    retryCount = 0;// 重置retryCount
			                    /*msg = mHandler.obtainMessage();
			                    msg.what = DownloadStatus.STATUS_COMPLETE;
			                    msg.obj = 100;
			                    mHandler.sendMessage(msg);*/
			                    downloadComplete = true;
			                    errorOccur = false;
			                    break;
			                } else {

			                    //retryCount = 0;// 重置retryCount
			                    blockCount++;
			                    readTotalCnt += realReadCnt;
			                    mbbo.put(buffer, 0, realReadCnt);
			                    //accessFile.write(buffer, 0, realReadCnt);

			                    //更新状态太过频繁
			                    /*mDownloadPercent = (int) ((readTotalCnt*100 / fileSize));
			                    msg.what = DownloadStatus.STATUS_DOWNLOADING;
			                    msg.obj = mDownloadPercent;

			                    if(mDownloadPercent > 100){
			                    	
			                    	mDownloadPercent = 100;
			                    	mPreDownloadPercent = 100;
			                    }
			                    //tjianli比较两次下载进度，有变化，才会通知界面更新，尽量避免ANR
			                    if (mPreDownloadPercent != mDownloadPercent) {

			                        mPreDownloadPercent = mDownloadPercent;
			                        mHandler.sendMessage(msg);
			                    }*/
			                    //每读取8块数据才判断更新状态
			                    if(blockCount % 16 == 0){
			                    	mDownloadPercent = (int) ((readTotalCnt*100 / fileSize));

			                        if(mDownloadPercent > 100){
			                        	mDownloadPercent = 100;
			                        	mPreDownloadPercent = 100;
			                        }
			                        //tjianli比较两次下载进度，有变化，才会通知界面更新，尽量避免ANR
			                        if (mPreDownloadPercent != mDownloadPercent) {
			                        	msg = mHandler.obtainMessage();
			                        	msg.what = DownloadStatus.STATUS_DOWNLOADING;
			                        	msg.obj = mDownloadPercent;
			                            mPreDownloadPercent = mDownloadPercent;
			                            mHandler.sendMessage(msg);
			                        }
			                    }
			                }
			            } else {

			            	msg = mHandler.obtainMessage();
			                msg.what = DownloadStatus.STATUS_PAUSE;
			                msg.obj = mDownloadPercent;
			                mHandler.sendMessage(msg);
			                
			                errorOccur = false;
			                break;
			            }

			        }

			    } catch (Exception e) {

			    	e.printStackTrace();
			    	if(!Utily.isNetworkAvailable()){
			    	    msg = mHandler.obtainMessage();
	                    msg.what = DownloadStatus.STATUS_NETERROR;
	                    msg.obj = mDownloadPercent;
	                    mHandler.sendMessage(msg);
	                    errorOccur = false;
	                    break;//跳出循环
			    	}
			    	
			        // 发生异常后,重连
			        Log.d(TAG,">>>>>download exception:" + e.toString());
			        if (retryCount >= HTTP_CONNECTION_RETRY_COUNT) {
			            errorOccur = true;
			            break;
			        }

			        Log.d(TAG,">>>>download process will retry for :" + (retryCount + 1));
			        ++retryCount;

			        bInitConnection = false;
			    }
			    
			    if (bCanceled == true) {
			        
			    	msg = mHandler.obtainMessage();
			        msg.what = DownloadStatus.STATUS_CANCELED;
			        msg.obj = mDownloadPercent;
			        mHandler.sendMessage(msg);

			        errorOccur = false;
			        break;
			    }

			} while (true);
			if (errorOccur == true) {
			    //发生错误
			    msg = mHandler.obtainMessage();
			    msg.what = DownloadStatus.STATUS_UNKNOWN;
				msg.obj = mDownloadPercent;
			    mHandler.sendMessage(msg);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FlowController.count(FlowController.FLOW_DOWN,readTotalCnt);
			try {
			    if (mDownloadInputStream != null) {
			    	if(bis != null){
			    	    bis.close();
			    	    bis = null;
			    	}
			        mDownloadInputStream.close();
			        mDownloadInputStream = null;
			    }

			    if (fileChannel != null){
			    	fileChannel.close();
			    	fileChannel = null;
			    }
			    if (accessFile != null) {
			        accessFile.close();
			        accessFile = null;
			    }
			    if (conn != null) {
			    	conn.disconnect();
			    	conn = null;
			    }
			    //文件流关闭完成后，才提示安装
			    if(downloadComplete) {
			        Message msg = mHandler.obtainMessage();
	                msg = mHandler.obtainMessage();
	                msg.what = DownloadStatus.STATUS_COMPLETE;
	                msg.obj = 100;
	                mHandler.sendMessage(msg);
	            }
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
    }
}
