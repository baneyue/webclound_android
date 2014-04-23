package com.webcloud.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcloud.R;


public class LoadingDialog extends Dialog {
	private Context context = null;
	private static LoadingDialog loadingDialog = null;
	
	public LoadingDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static LoadingDialog createDialog(Context context){
		loadingDialog = new LoadingDialog(context,R.style.CustomProgressDialog);
		loadingDialog.setContentView(R.layout.dialog_loading);
		loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return loadingDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (loadingDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) loadingDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public LoadingDialog setTitile(String strTitle){
    	return loadingDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public LoadingDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)loadingDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return loadingDialog;
    }
}
