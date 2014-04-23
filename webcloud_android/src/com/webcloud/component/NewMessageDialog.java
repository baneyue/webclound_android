package com.webcloud.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.funlib.utily.Utily;
import com.webcloud.R;

public class NewMessageDialog {

	/** ok button */
	public static final int MESSAGEDIALOG_OK = 0;

	/** cancel button */
	public static final int MESSAGEDIALOG_CANCEL = 1;
	
	/** ok other button*/
	public static final int MESSAGEDIALOG_OK_OTHER = 2;//ok的第二种情况，有三个按钮的情况

	private AlertDialog dialogOK;
	private TextView dialogOKMessage;
	private View dialogOKView;

	private AlertDialog dialogOKCancel;
	private TextView dialogOKCancelMessage;
	private View dialogOKCancelView;

	private NewMessageDialogListener dialogListener;
	private boolean canTouchOutside;
	private int titleResId;

	public int tag = 0;// 因为界面中listener入口一样，只有通过tag区分

	private View.OnClickListener okClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (null != dialogListener) {
				dialogListener.onMessageDialogClick(NewMessageDialog.this,
						MESSAGEDIALOG_OK);
			}
			dialogOK.dismiss();
		}
	};

	private View.OnClickListener okClickListener1 = new View.OnClickListener() {
		public void onClick(View v) {
			if (null != dialogListener) {
				dialogListener.onMessageDialogClick(NewMessageDialog.this,
						MESSAGEDIALOG_OK);
			}
			dialogOKCancel.dismiss();
		}
	};

	private View.OnClickListener cancelClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			if (null != dialogListener) {
				dialogListener.onMessageDialogClick(NewMessageDialog.this,
						MESSAGEDIALOG_CANCEL);
			}
			dialogOKCancel.dismiss();
		}
	};

	public NewMessageDialog() {
		canTouchOutside = true;
		titleResId = R.string.messagedialog_title;
	}

	public void setCanTouchOutside(boolean value) {
		canTouchOutside = value;
	}
	
	public void setTitle(int resId){
		
		titleResId = resId;
	}

	public void showDialogOK(Activity activity, String message,
			NewMessageDialogListener listener) {

		LayoutInflater mInflater = LayoutInflater.from(activity);
		dialogOKView = mInflater.inflate(R.layout.common_dialog_ok, null);
		dialogOK = new AlertDialog.Builder(activity).create();
		dialogOK.setCanceledOnTouchOutside(canTouchOutside);

		TextView text = (TextView) dialogOKView
				.findViewById(R.id.common_dialog_ok_title);
		text.setText(activity.getResources().getString(
				R.string.messagedialog_title));
		dialogOKMessage = (TextView) dialogOKView
				.findViewById(R.id.common_dialog_ok_message);

		Button mButton = (Button) dialogOKView
				.findViewById(R.id.common_dialog_ok_ok);
		mButton.setBackgroundDrawable(Utily.getStateDrawable(
				R.drawable.bnt_pop_normal, R.drawable.bnt_pop_normal1, -1));
		mButton.setOnClickListener(okClickListener);

		dialogListener = listener;
		dialogOKMessage.setText(message);
		dialogOK.show();
		dialogOK.getWindow().setContentView(dialogOKView);
		
		dismissMessageDialog();
		
		dialogListener = listener;
		dialogOK = new AlertDialog.Builder(activity).create();
		dialogOK.setCanceledOnTouchOutside(canTouchOutside);
		dialogOK.setTitle(titleResId);
		dialogOK.setIcon(android.R.drawable.ic_dialog_alert);
		dialogOK.setMessage(message);
		dialogOK.setCanceledOnTouchOutside(canTouchOutside);
		dialogOK.setButton(activity.getString(R.string.messagedialog_ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialogListener != null)
					dialogListener.onMessageDialogClick(NewMessageDialog.this, MESSAGEDIALOG_OK);
			}
		});
		
		try {
			
			dialogOK.show();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public void setDialogOKOnKeyListener(OnKeyListener onKeyListener) {
		if (dialogOK != null) {
			dialogOK.setOnKeyListener(onKeyListener);
		}
	}

	public void showDialogOKCancel(Activity activity, String message,
	    NewMessageDialogListener listener) {

		LayoutInflater mInflater = LayoutInflater.from(activity);
		dialogOKCancelView = mInflater.inflate(
				R.layout.common_dialog_ok_cancel, null);
		dialogOKCancel = new AlertDialog.Builder(activity).create();
		dialogOKCancel.setCanceledOnTouchOutside(canTouchOutside);

		TextView text = (TextView) dialogOKCancelView
				.findViewById(R.id.common_dialog_ok_cancel_title);
		text.setText(activity.getResources().getString(
				R.string.messagedialog_title));
		dialogOKCancelMessage = (TextView) dialogOKCancelView
				.findViewById(R.id.common_dialog_ok_cancel_message);

		Button mButton = (Button) dialogOKCancelView
				.findViewById(R.id.common_dialog_ok_cancel_ok_btn);
		mButton.setBackgroundDrawable(Utily.getStateDrawable(
				R.drawable.bnt_pop_normal, R.drawable.bnt_pop_normal1, -1));
		mButton.setOnClickListener(okClickListener1);

		Button mButton2 = (Button) dialogOKCancelView
				.findViewById(R.id.common_dialog_ok_cancel_cancel_btn);
		mButton2.setBackgroundDrawable(Utily.getStateDrawable(
				R.drawable.bnt_pop_normal, R.drawable.bnt_pop_normal1, -1));
		mButton2.setOnClickListener(cancelClickListener);

		dialogListener = listener;
		dialogOKCancelMessage.setText(message);
		dialogOKCancel.show();
		dialogOKCancel.getWindow().setContentView(dialogOKCancelView);
		
		dismissMessageDialog();
		
		//
		dialogOKCancel = new AlertDialog.Builder(activity).create();
		dialogListener = listener;
		dialogOKCancel = new AlertDialog.Builder(activity).create();
		dialogOKCancel.setCanceledOnTouchOutside(canTouchOutside);
		dialogOKCancel.setTitle(titleResId);
		dialogOKCancel.setIcon(android.R.drawable.ic_dialog_alert);
		dialogOKCancel.setMessage(message);
		dialogOKCancel.setCanceledOnTouchOutside(canTouchOutside);
		dialogOKCancel.setButton(activity.getString(R.string.messagedialog_ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialogListener != null)
					dialogListener.onMessageDialogClick(NewMessageDialog.this, MESSAGEDIALOG_OK);
			}
		});
		dialogOKCancel.setButton2(activity.getString(R.string.messagedialog_cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialogListener != null)
					dialogListener.onMessageDialogClick(NewMessageDialog.this, MESSAGEDIALOG_CANCEL);
			}
		});
		
		try {
			
			dialogOKCancel.show();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public void setDialogOKCancelOnKeyListener(OnKeyListener onKeyListener) {
		if (dialogOKCancel != null) {
			dialogOKCancel.setOnKeyListener(onKeyListener);
		}
	}

	public void dismissMessageDialog() {

		if (dialogOKCancel != null) {
			if (dialogOKCancel.isShowing())
				dialogOKCancel.dismiss();
		}

		if (dialogOK != null) {
			if (dialogOK.isShowing())
				dialogOK.dismiss();
		}

	}

	public void clear() {
		dialogOK = null;
		dialogOKView = null;
		dialogOKCancel = null;
		dialogOKCancelView = null;
	}

	public boolean isDialogOKShow() {
		if (dialogOK != null) {
			return dialogOK.isShowing();
		}

		return false;
	}

	public boolean isDialogOKCancelShow() {
		if (dialogOKCancel != null) {
			return dialogOKCancel.isShowing();
		}

		return false;
	}

}
