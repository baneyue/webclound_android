package com.webcloud.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.View;
import android.widget.TextView;

import com.webcloud.R;

public class MessageDialog {
    
    /** ok button */
    public static final int MESSAGEDIALOG_OK = 0;
    
    /** cancel button */
    public static final int MESSAGEDIALOG_CANCEL = 1;
    
    private AlertDialog dialogOK;
    
    private TextView dialogOKMessage;
    
    private View dialogOKView;
    
    private AlertDialog dialogOKCancel;
    
    private TextView dialogOKCancelMessage;
    
    private View dialogOKCancelView;
    
    private MessageDialogListener dialogListener;
    
    private boolean canTouchOutside;
    
    //默认显示的资源id
    private int titleResId = R.string.messagedialog_title;
    
    private int okResId = R.string.messagedialog_ok;
    
    private int cancelResId = R.string.messagedialog_cancel;
    
    private int iconResId = android.R.drawable.ic_dialog_alert;
    
    /** 
     * 初始化对话框资源，必须在调用showDialog之前设置。
     * 若要使用默认就传小于等于0的值。
     *
     * @param titleResId
     * @param iconResId
     * @param okResId
     * @param cancelResId
     * @param canTouchOutside
     * @see [类、类#方法、类#成员]
     */
    public void initDialogRes(int titleResId, int iconResId, int okResId, int cancelResId, boolean canTouchOutside) {
        if (titleResId > 0)
            this.titleResId = titleResId;
        if (iconResId > 0)
            this.iconResId = iconResId;
        if (okResId > 0)
            this.okResId = okResId;
        if (cancelResId > 0)
            this.cancelResId = cancelResId;
        this.canTouchOutside = canTouchOutside;
    }
    
    public int tag = 0;// 因为界面中listener入口一样，只有通过tag区分
    
    private View.OnClickListener okClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (null != dialogListener) {
                dialogListener.onMessageDialogClick(MessageDialog.this, MESSAGEDIALOG_OK);
            }
            dialogOK.dismiss();
            
        }
    };
    
    private View.OnClickListener okClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            if (null != dialogListener) {
                dialogListener.onMessageDialogClick(MessageDialog.this, MESSAGEDIALOG_OK);
            }
            dialogOKCancel.dismiss();
            
        }
    };
    
    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (null != dialogListener) {
                dialogListener.onMessageDialogClick(MessageDialog.this, MESSAGEDIALOG_CANCEL);
            }
            dialogOKCancel.dismiss();
            
        }
    };
    
    public MessageDialog() {
        canTouchOutside = true;
    }
    
    public MessageDialog(boolean canTouchOutside) {
        this.canTouchOutside = canTouchOutside;
    }
    
    public void showDialogOK(Activity activity, String message, MessageDialogListener listener) {
        
        //		LayoutInflater mInflater = LayoutInflater.from(activity);
        //		dialogOKView = mInflater.inflate(R.layout.common_dialog_ok, null);
        //		dialogOK = new AlertDialog.Builder(activity).create();
        //		dialogOK.setCanceledOnTouchOutside(canTouchOutside);
        //
        //		TextView text = (TextView) dialogOKView
        //				.findViewById(R.id.common_dialog_ok_title);
        //		text.setText(activity.getResources().getString(
        //				R.string.messagedialog_title));
        //		dialogOKMessage = (TextView) dialogOKView
        //				.findViewById(R.id.common_dialog_ok_message);
        //
        //		Button mButton = (Button) dialogOKView
        //				.findViewById(R.id.common_dialog_ok_ok);
        //		mButton.setBackgroundDrawable(Utily.getStateDrawable(
        //				R.drawable.bnt_pop_normal, R.drawable.bnt_pop_normal1, -1));
        //		mButton.setOnClickListener(okClickListener);
        //
        //		dialogListener = listener;
        //		dialogOKMessage.setText(message);
        //		dialogOK.show();
        //		dialogOK.getWindow().setContentView(dialogOKView);
        
        dismissMessageDialog();
        
        dialogListener = listener;
        dialogOK = new AlertDialog.Builder(activity).create();
        dialogOK.setCanceledOnTouchOutside(canTouchOutside);
        dialogOK.setTitle(titleResId);
        dialogOK.setIcon(android.R.drawable.ic_dialog_alert);
        dialogOK.setMessage(message);
        dialogOK.setCanceledOnTouchOutside(canTouchOutside);
        dialogOK.setButton(activity.getString(okResId), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogListener != null)
                    dialogListener.onMessageDialogClick(MessageDialog.this, MESSAGEDIALOG_OK);
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
    
    public void showDialogOKCancel(Activity activity, String message, MessageDialogListener listener) {
        dismissMessageDialog();
        
        dialogOKCancel = new AlertDialog.Builder(activity).create();
        dialogListener = listener;
        dialogOKCancel = new AlertDialog.Builder(activity).create();
        dialogOKCancel.setCanceledOnTouchOutside(canTouchOutside);
        dialogOKCancel.setTitle(titleResId);
        dialogOKCancel.setIcon(iconResId);
        dialogOKCancel.setMessage(message);
        dialogOKCancel.setCanceledOnTouchOutside(canTouchOutside);
        dialogOKCancel.setButton(activity.getString(okResId), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogListener != null)
                    dialogListener.onMessageDialogClick(MessageDialog.this, MESSAGEDIALOG_OK);
            }
        });
        dialogOKCancel.setButton2(activity.getString(cancelResId), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogListener != null)
                    dialogListener.onMessageDialogClick(MessageDialog.this, MESSAGEDIALOG_CANCEL);
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
