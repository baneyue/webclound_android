package com.webcloud.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.funlib.log.Log;
import com.webcloud.R;

/**
 * 弹出进度对话框，预留动画扩展，暂时只显示文字信息无动画。
 * 
 * @author  zoubangyue
 * @version  [版本号, 2012-5-4]
 */
public class NewLoadingDialog extends Dialog{
    
    private static final String TAG = "NewLoadingDialog";
    
    String message;
    
    private TextView tvMsg;
    
    private ImageView ivClose;
    
    private Handler handler = new MyHandler(this); 
    
    static class MyHandler extends Handler{
        NewLoadingDialog dialog;
        public MyHandler(NewLoadingDialog d){
            dialog = d;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 1) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    };;

    WaitCancelListener listener;
    
    Context context;
    
    public NewLoadingDialog(Context context,WaitCancelListener listener) {
        super(context, R.style.dialogTheme);
        this.listener = listener;
        this.context = context;
    }
    
    public NewLoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }
    
    public NewLoadingDialog(Context context, int theme) {
        super(context, R.style.dialogTheme);
        this.context = context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_loading_dialog);
        findViewById(R.id.layProgress).getBackground().setAlpha(240);
        tvMsg = (TextView)findViewById(R.id.tvMsg);
        ivClose = (ImageView)findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onWaittingCancel();
                NewLoadingDialog.this.cancel();
            }
        });
        /*ImageView localImageView1 = (ImageView)findViewById(2131099761);
        this.iv_cancle = localImageView1;
        this.iv_cancle.setOnClickListener(this);
        ImageView localImageView2 = (ImageView)findViewById(2131099759);
        this.iv_loading = localImageView2;
        this.iv_loading.setBackgroundResource(2130968577);
        AnimationDrawable localAnimationDrawable = (AnimationDrawable)this.iv_loading.getBackground();
        this.drawable = localAnimationDrawable;*/
    }
    
    protected void startAnimation(AnimationDrawable animDraw) {
        if ((animDraw != null) && (!animDraw.isRunning()))
            animDraw.run();
    }
    
    protected void stopAnimation(AnimationDrawable animDraw) {
        if ((animDraw != null) && (animDraw.isRunning()))
            animDraw.stop();
    }
    
    public void setMessage(CharSequence str) {
        if(TextUtils.isEmpty(str)){
            message = "";
            return;
        }
        message = str.toString();
    }

    public String getMessage() {
        return message;
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        tvMsg.setText(message);
    }
    
    @Override
    public void setOnCancelListener(DialogInterface.OnCancelListener cancel) {
        super.setOnCancelListener(cancel);
    }
    
    @Override
    public void show() {
        super.show();
        if(TextUtils.isEmpty(message)){
            message = context.getResources().getString(R.string.loading);
        }
        tvMsg.setText(message);
        /*TimeThread timeThread = new TimeThread();
        timeThread.start();*/
        //handler.sendEmptyMessage(1);
        //把前面的消息移除
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(1, 20 * 1000);
    }
    
    @Override
    public void dismiss() {
        try {
            if(this.isShowing())
            super.dismiss();
        } catch (Exception e) {
            Log.d(TAG, "dismiss时失败了，窗口已经不在了");
            //e.printStackTrace();
        }
    }
}
