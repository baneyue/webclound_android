package com.webcloud.func.map.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.android.common.logging.Log;
import com.funlib.utily.Utily;
import com.webcloud.R;

/**
 * 起点或终点选择对话框
 * 
 * @author  bangyue
 * @version  [版本号, 2013-12-1]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class StartEndMessageDialog {
    
    public int getCheckedId() {
        return vh.btnStartEndGp.getCheckedRadioButtonId();
    }
    
    /** ok button */
    public static final int MESSAGEDIALOG_OK = 0;
    
    /** cancel button */
    public static final int MESSAGEDIALOG_CANCEL = 1;
    
    private AlertDialog dialogOKCancel;
    
    private TextView dialogOKCancelMessage;
    
    private View dialogOKCancelView;
    
    private StartEndMessageDialogListener dialogListener;
    
    private boolean canTouchOutside;
    
    private int titleResId;
    
    public int tag = 0;// 因为界面中listener入口一样，只有通过tag区分
        
    private View.OnClickListener okClickListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            if (null != dialogListener) {
                dialogListener.onMessageDialogClick(StartEndMessageDialog.this, MESSAGEDIALOG_OK);
            }
            dialogOKCancel.dismiss();
        }
    };
    
    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (null != dialogListener) {
                dialogListener.onMessageDialogClick(StartEndMessageDialog.this, MESSAGEDIALOG_CANCEL);
            }
            dialogOKCancel.dismiss();
        }
    };
    
    public StartEndMessageDialog() {
        canTouchOutside = true;
        titleResId = R.string.messagedialog_title;
    }
    
    public void setCanTouchOutside(boolean value) {
        canTouchOutside = value;
    }
    
    public void setTitle(int resId) {
        
        titleResId = resId;
    }
    
    public void showDialogOKCancel(Activity activity, String message, StartEndMessageDialogListener listener) {
        
        LayoutInflater mInflater = LayoutInflater.from(activity);
        dialogOKCancelView = mInflater.inflate(R.layout.vehicle_start_end_dialog_ok_cancel, null);
        dialogOKCancel = new AlertDialog.Builder(activity).create();
        dialogOKCancel.setCanceledOnTouchOutside(canTouchOutside);
        
        TextView text = (TextView)dialogOKCancelView.findViewById(R.id.common_dialog_ok_cancel_title);
        text.setText(activity.getResources().getString(R.string.messagedialog_title));
        dialogOKCancelMessage = (TextView)dialogOKCancelView.findViewById(R.id.common_dialog_ok_cancel_message);
        
        Button mButton = (Button)dialogOKCancelView.findViewById(R.id.common_dialog_ok_cancel_ok_btn);
        mButton.setBackgroundDrawable(Utily.getStateDrawable(R.drawable.bnt_pop_normal, R.drawable.bnt_pop_normal1, -1));
        mButton.setOnClickListener(okClickListener1);
        
        Button mButton2 = (Button)dialogOKCancelView.findViewById(R.id.common_dialog_ok_cancel_cancel_btn);
        mButton2.setBackgroundDrawable(Utily.getStateDrawable(R.drawable.bnt_pop_normal, R.drawable.bnt_pop_normal1, -1));
        mButton2.setOnClickListener(cancelClickListener);
        
        initRadioButton();
        
        dialogListener = listener;
        dialogOKCancelMessage.setText(message);
        dialogOKCancel.show();
        dialogOKCancel.getWindow().setContentView(dialogOKCancelView);
    }
    
    private void initRadioButton() {
        vh = new ViewHolder();
        vh.btnStart = (RadioButton)dialogOKCancelView.findViewById(R.id.btnStart);
        vh.btnEnd = (RadioButton)dialogOKCancelView.findViewById(R.id.btnEnd);
        vh.btnStartEndGp = (RadioGroup)dialogOKCancelView.findViewById(R.id.btnStartEndGp);
        //按钮组切换
        vh.btnStartEndGp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //StartEndMessageDialog.this.checkedId = group.getId();
                Log.d("checked", checkedId + "");
            }
        });
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
    }
    
    public void clear() {
        dialogOKCancel = null;
        dialogOKCancelView = null;
    }
    
    public boolean isDialogOKCancelShow() {
        if (dialogOKCancel != null) {
            return dialogOKCancel.isShowing();
        }
        
        return false;
    }
    
    ViewHolder vh;
    
    static class ViewHolder {
        RadioButton btnStart;
        
        RadioButton btnEnd;
        
        RadioGroup btnStartEndGp;
    }
    
}
