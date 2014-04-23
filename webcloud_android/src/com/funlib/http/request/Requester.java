package com.funlib.http.request;

import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.funlib.http.HttpRequestImpl;
import com.funlib.http.ReturnData;
import com.funlib.log.Log;
import com.funlib.utily.Utily;
import com.webcloud.component.MessageDialog;
import com.webcloud.component.MessageDialogListener;
import com.webcloud.define.HttpUrlImpl;

/**
 * 普通无缓存请求。
 */
public class Requester implements Runnable {
    private static final String TAG = "Requester";
    
    private HttpRequestImpl mRequest;
    
    /** 失败重试次数 */
    private int mFailRetryCount = 1;
    
    private Context mContext;
    
    //static MessageDialog dialog = new MessageDialog();
    
    //static ActivityManager mActManager;
    
    private MyHandler mHandler = new MyHandler(this);
    
    static class MyHandler extends Handler {
        public MyHandler(Requester requester) {
            super(Looper.myLooper());
            this.requester = requester;
        }
        
        Requester requester;
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            String result = (String)msg.obj;
            if (TextUtils.isEmpty(result)) {
                result = "";
            }
            if (requester.mRequestListener != null) {
                if (msg.what == RequestStatus.SUCCESS || msg.what == RequestStatus.FAIL)
                    Log.d("Requester", "mRequestUrl:" + requester.mRequestUrl + ",what:" + msg.what + ",result:"
                        + result);
                requester.mRequestListener.requestStatusChanged(msg.what, requester.mRequestId, result, requester.mRequestParams);
            }
            
            if (msg.what == RequestStatus.FAIL) {
                //当前上下文为activity,且在前台运行，则判断网络等状况
                boolean insofAct = requester.mContext instanceof Activity;
                if (insofAct) {
                    Activity act = (Activity)requester.mContext;
                    if (act.hasWindowFocus() && !Utily.isNetworkAvailable()) {
                        MessageDialog dialog = new MessageDialog();
                        //前台运行
                        dialog.showDialogOKCancel((Activity)requester.mContext,
                            "网络未连接，可否先连接网络？",
                            new MessageDialogListener() {
                                
                                @Override
                                public void onMessageDialogClick(MessageDialog dialog, int which) {
                                    if (which == MessageDialog.MESSAGEDIALOG_OK) {
                                        dialog.dismissMessageDialog();
                                        //启动网络设置界面
                                        try {
                                            Intent intent = null;
                                            //判断手机系统的版本  即API大于10 就是3.0或以上版本 
                                            if (android.os.Build.VERSION.SDK_INT > 10) {
                                                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                            } else {
                                                intent = new Intent();
                                                ComponentName component =
                                                    new ComponentName("com.android.settings",
                                                        "com.android.settings.WirelessSettings");
                                                intent.setComponent(component);
                                                intent.setAction("android.intent.action.VIEW");
                                            }
                                            requester.mContext.startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        dialog.dismissMessageDialog();
                                    }
                                }
                            });
                    } else {
                        //NewToast.show(requester.mContext, " (×_×) 请求超时", false);
                    }
                }
            }
        }
    };
    
    private RequestListener mRequestListener;
    
    private String mRequestUrl;
    
    private Map<String, String> mRequestParams;
    
    private HttpUrlImpl mRequestId;
    
    private int mNowRetryCount;
    
    private boolean bCanceled;
    
    /**请求方式*/
    private int mRequestType;
    
    /**是否需要登录*/
    private boolean needSession;
    
    public Requester(Context context) {
        
        mContext = context;
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
     * 发起http请求。
     * 默认发post请求。
     * 
     * @param listener
     * @param listenerId
     *            如果调用端，同时发送多个也去请求，可以在调用端维护唯一id，并传递，在业务处理结束后，带回
     * @param requestUrl
     */
    @Deprecated
    public void request(RequestListener listener, HttpUrlImpl requestId, String requestUrl, Map<String, String> params) {
        
        this.mRequestListener = listener;
        this.mRequestParams = params;
        this.mRequestUrl = requestUrl;
        this.mRequestId = requestId;
        new Thread(this).start();
    }
    
    /**
     * 发起http请求。
     * 指定请求方式和是否需要维持session.
     * 
     * @param listener
     * @param listenerId
     *            如果调用端，同时发送多个也去请求，可以在调用端维护唯一id，并传递，在业务处理结束后，带回
     * @param requestUrl
     * @param mRequestType
     * @param needSession
     */
    public void request(RequestListener listener, HttpUrlImpl requestId, String requestUrl,
        Map<String, String> params, int requestType, boolean needSession) {
        
        this.mRequestListener = listener;
        this.mRequestParams = params;
        this.mRequestUrl = requestUrl;
        this.mRequestId = requestId;
        this.mRequestType = requestType;
        this.needSession = needSession;
        new Thread(this).start();
    }
    
    /**
     * 取消请求
     */
    public void cancel() {
        
        bCanceled = true;
        if (mRequest != null)
            mRequest.cancel();
    }
    
    @Override
    public void run() {
        mRequest = new HttpRequestImpl(mContext);
        bCanceled = false;
        mNowRetryCount = 0;
        Message msg = mHandler.obtainMessage();
        //发送开始
        msg.what = RequestStatus.START;
        msg.obj = null;
        mHandler.sendMessage(msg);
        
        ReturnData data = null;
        do {
            Log.d("Requester", mNowRetryCount + "次请求");
            Log.d("Requester", "beginRequest" + mNowRetryCount + ":" + System.currentTimeMillis());
            if (HttpRequestImpl.POST == this.mRequestType) {
                data = mRequest.doPostRequest(mRequestUrl, mRequestParams);
            } else {
                data = mRequest.doGetRequest(mRequestUrl, mRequestParams);
            }
            Log.d("Requester", "endRequest" + mNowRetryCount + ":" + System.currentTimeMillis());
            if (data != null && data.status == ReturnData.SC_OK) {
                break;
            } else {
                ++mNowRetryCount;
            }
            if (bCanceled == true)
                break;
        } while (mNowRetryCount < mFailRetryCount);
        Log.d("Requester", "请求结束");
        msg = mHandler.obtainMessage();
        if (bCanceled == true) {
            Log.d("Requester", "请求被取消");
            msg.what = RequestStatus.CANCELED;
            msg.obj = null;
        } else {
            if (data == null || data.status != ReturnData.SC_OK) {
                Log.d("Requester", "请求失败");
                msg.what = RequestStatus.FAIL;
                msg.obj = null;
            } else {
                Log.d("Requester", "请求成功");
                msg.what = RequestStatus.SUCCESS;
                msg.obj = data.content;
            }
        }
        mHandler.sendMessage(msg);
    }
}
