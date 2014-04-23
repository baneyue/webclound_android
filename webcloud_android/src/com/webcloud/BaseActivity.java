package com.webcloud;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.funlib.http.request.RequestListener;
import com.funlib.http.request.RequestStatus;
import com.webcloud.component.NewLoadingDialog;
import com.webcloud.component.NewToast;
import com.webcloud.define.HttpUrlImpl;
import com.webcloud.define.ModelStyle;
import com.webcloud.func.template.HomeGridViewActivity;
import com.webcloud.func.template.HomeMetroActivity;
import com.webcloud.func.template.HomeSlideMenuActivity;
import com.webcloud.manager.SystemManager;
import com.webcloud.utily.BackUtil;

/**
 * 基础活动，定义活动的基本行为。
 * 
 * @author  zoubangyue
 * @version  [版本号, 2012-4-27]
 */
public abstract class BaseActivity extends Activity implements RequestListener{
    private final String TAG = "BaseActivity";
    
    protected SystemManager mgr;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ModelStyle.theme);
        super.onCreate(savedInstanceState);
        ActivitysManager.getInstance().pushActivity(this);
        mgr = SystemManager.getInstance(this);
    }
    
    //不要阻塞本方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    
    @Override
    protected void onDestroy() {
        NewToast.release();
        if(loadingDialog != null) loadingDialog.dismiss();
        ActivitysManager.getInstance().popFinishActivity(this);
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //MobileProbe.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        //MobileProbe.onPause(this);
    }
    
    NewLoadingDialog loadingDialog;
    
    @Override
    public void requestStatusChanged(int statusCode, HttpUrlImpl requestId, String responseString,
        Map<String, String> requestParams) {
        switch (statusCode) {
            case RequestStatus.START:
                //显示加载进度条
                if(loadingDialog != null) loadingDialog.show();
                break;
            case RequestStatus.FAIL:
                if(loadingDialog != null) loadingDialog.dismiss();
                break;
            case RequestStatus.SUCCESS:
                if(loadingDialog != null) loadingDialog.dismiss();
                break;
            default:
                break;
        }
    }

}
