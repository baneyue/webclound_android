package com.webcloud.component;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.Window;

import com.webcloud.R;

/**
 * 等待界面
 * 
 * @author taojianli
 * 
 */
public class WaittingDialog {

	public static final int STYLE_SPINNER = 0;
	public static final int STYLE_HORIZONTAL = 1;
	
	private Activity mContext;
	private ProgressDialog sWaittingDialog;
	private int mShowStyle = ProgressDialog.STYLE_SPINNER;
	
	public void setShowStyle(int style){
		
		if(style == STYLE_SPINNER){
			
			mShowStyle = ProgressDialog.STYLE_SPINNER;
		}else{
			
			mShowStyle = ProgressDialog.STYLE_HORIZONTAL;
		}
	}
	
	public void show(Activity context, final WaitCancelListener listener) {
	    
		mContext = context;
		
		sWaittingDialog = new ProgressDialog(context);
//		sWaittingDialog.setTitle(R.string.messagedialog_title);
		sWaittingDialog.setProgressStyle(mShowStyle);
		sWaittingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		sWaittingDialog.setMessage(context.getString(R.string.requesting));
//		sWaittingDialog.setButton(context.getString(R.string.messagedialog_cancel), new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				if(listener != null)
//					listener.onWaittingCancel();
//			}
//		});
		
		sWaittingDialog.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK){
					sWaittingDialog.dismiss();
					
					if(listener != null)
						listener.onWaittingCancel();
				}
				
				return false;
			}
			
		});
		
		try {
			
			sWaittingDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void setKeyEvent(OnKeyListener onKeyListener) {
		sWaittingDialog.setOnKeyListener(onKeyListener);
	}

	public void dismiss() {
		if(sWaittingDialog != null&&isShowing())
			sWaittingDialog.dismiss();
	}
	
	public boolean isShowing(){
		
		if(sWaittingDialog != null)
			return sWaittingDialog.isShowing();
		
		return false;
	}
	
	public void setProgress(int percentShow) {
		sWaittingDialog.setProgress(percentShow);
	}

	public void setMax(int max){
		
		sWaittingDialog.setMax(max);
	}
	
	public void show(){
	    try {
            if(sWaittingDialog != null){
                sWaittingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public ProgressDialog getDialog(){
	    return sWaittingDialog;
	}
}

