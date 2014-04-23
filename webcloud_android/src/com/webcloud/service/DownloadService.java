package com.webcloud.service;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.funlib.http.download.DownloadListener;
import com.funlib.http.download.DownloadStatus;
import com.funlib.http.download.UpdateDownloader;
import com.funlib.utily.Utily;
import com.webcloud.R;
import com.webcloud.WebCloudApplication;

/**
 * 下载服务。
 * 服务和活动共享主线程，注意避免ANR。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-18]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DownloadService extends Service {
    public static final int NOTIFICATION_ID = 100;
    
    public static final String START = "start";
    
    public static final String STOP = "stop";
    
    boolean isUpdating = false;
    
    boolean isComplete = false;
    
    NotificationManager mNotifMan;
    
    Notification notification;
    
    UpdateDownloader downloader = new UpdateDownloader();
    
    public static void actionStart(Context ctx, String appurl) {
        Intent i = new Intent(ctx, DownloadService.class);
        i.putExtra("appurl", appurl);
        i.setAction(START);
        i.setFlags(Service.START_STICKY);//连续
        ctx.startService(i);
    }
    
    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, DownloadService.class);
        i.setAction(STOP);
        ctx.startService(i);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            String appurl = intent.getStringExtra("appurl");
            if (START.equals(action)) {
                if (isUpdating || TextUtils.isEmpty(appurl))
                    return super.onStartCommand(intent, flags, startId);
                startDownloadByNotification(appurl);
            } else if (STOP.equals(action)) {
                stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public Notification initNotification() {
        mNotifMan = (NotificationManager)WebCloudApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification(R.drawable.ic_launcher, "应用正在更新…", System.currentTimeMillis());
        //屏幕亮、不可手动清空
        notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_NO_CLEAR;
        //默认声音和振动
        //notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.contentView =
            new RemoteViews(WebCloudApplication.getInstance().getPackageName(), R.layout.comm_update_notification);
        notification.contentView.setProgressBar(R.id.pbLoad, 100, 0, false);
        notification.contentView.setTextViewText(R.id.tvMsg, "已下载" + 0 + "%");
        
        //给一个空的intent,有些机型必须设置
        Intent intent = new Intent();
        PendingIntent contentIntent =
            PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;
        
        //点击取消
        notification.contentView.setOnClickPendingIntent(R.id.btnCancel, canelPendingIntent(downloader));
        return notification;
    }
    
    private PendingIntent canelPendingIntent(UpdateDownloader downloader) {
        //取消下载，关闭服务
        Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        intent.setAction(STOP);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, intent, 0);
        return pi;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
    }
    
    int percent = 0;
    
    Runnable runPros = new Runnable() {
        
        @Override
        public void run() {
            if (isComplete) {
                handler.removeCallbacks(runPros);
                mNotifMan.cancel(NOTIFICATION_ID);
                return;
            }
            //更新状态栏
            notification.contentView.setProgressBar(R.id.pbLoad, 100, percent, false);
            notification.contentView.setTextViewText(R.id.tvMsg, "已下载" + percent + "%");
            notification.defaults = 0;
            mNotifMan.notify(NOTIFICATION_ID, notification);
        }
    };
    
    private synchronized void startDownloadByNotification(String appurl) {
        isUpdating = true;
        downloader.download(null, appurl, new DownloadListener() {
            
            @Override
            public void onDownloadStatusChanged(Object tag, int index, final int status, final int percent,
                final String filePath) {
                //启动下载
                if (status == DownloadStatus.STATUS_STARTDOWNLOADING) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                            mNotifMan.notify(NOTIFICATION_ID, notification);
                        }
                    });
                } else if (status == DownloadStatus.STATUS_DOWNLOADING) {
                    DownloadService.this.percent = percent;
                    handler.post(runPros);
                } else if (status == DownloadStatus.STATUS_COMPLETE) {
                    /*handler.post(new Runnable() {
                        
                        @Override
                        public void run() {*/
                    isComplete = true;
                    handler.postDelayed(new Runnable() {
                        
                        @Override
                        public void run() {
                            handler.removeCallbacks(runPros);
                            mNotifMan.cancel(NOTIFICATION_ID);
                        }
                    }, 1000);
                    Toast.makeText(getBaseContext(), "新版乐搜下载完成啦，等待安装…", Toast.LENGTH_LONG).show();
                    //下载完成
                    Intent apkintent = new Intent(Intent.ACTION_VIEW);
                    final Uri puri = Uri.fromFile(new File(filePath));
                    apkintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    apkintent.setDataAndType(puri, "application/vnd.android.package-archive");
                    startActivity(apkintent);
                    stop();
                    /*}
                    });*/
                } else if (status == DownloadStatus.STATUS_NO_SDCARD || status == DownloadStatus.STATUS_STORAGE_FULL
                    || status == DownloadStatus.STATUS_UNKNOWN || status == DownloadStatus.STATUS_NETERROR) {
                    handler.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            String tip = "Sorry下载失败，未知异常，请稍后重试";
                            if (status == DownloadStatus.STATUS_NO_SDCARD
                                || status == DownloadStatus.STATUS_STORAGE_FULL) {
                                tip = getResources().getString(R.string.is_update_sdcard_error);
                            } else if (status == DownloadStatus.STATUS_NETERROR) {
                                tip = getResources().getString(R.string.is_update_neterror);
                            }
                            Toast.makeText(getBaseContext(), tip, Toast.LENGTH_LONG).show();
                            //下载出状况
                            /*MessageDialog msgDialog = new MessageDialog(false);
                            msgDialog.showDialogOK(LbsMainActivity.this,
                                tip,
                                new MessageDialogListener() {
                                    
                                    @Override
                                    public void onMessageDialogClick(MessageDialog dialog, int which) {
                                        dialog.dismissMessageDialog();
                                        mNotifMan.cancel(UpdateDownloader.NOTIFICATION_ID);
                                    }
                                });*/
                            /*msgDialog.setDialogOKOnKeyListener(new OnKeyListener() {
                                
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        mNotifMan.cancel(UpdateDownloader.NOTIFICATION_ID);
                                        return true;
                                    }
                                    return false;
                                }
                            });*/
                            mNotifMan.cancel(NOTIFICATION_ID);
                            stop();
                        }
                    });
                } else if (status == DownloadStatus.STATUS_CANCELED) {
                    Toast.makeText(getBaseContext(), "您取消了应用更新！", Toast.LENGTH_LONG).show();
                }
            }
        },
            Utily.getSDPath() + "hunanctlbs.apk");
    }
    
    private synchronized void stop() {
        mNotifMan.cancel(NOTIFICATION_ID);
        downloader.canceled();
        isUpdating = false;
        stopSelf();
    }
    
    Handler handler = new Handler();
}
