package com.webcloud.func.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.funlib.http.HttpRequest;
import com.funlib.http.request.RequestListener;
import com.funlib.http.request.RequestStatus;
import com.funlib.http.request.Requester;
import com.funlib.json.JsonFriend;
import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.define.HttpUrlImpl;
import com.webcloud.func.email.fragment.OutboxFragment.onOutboxLoadedListener;
import com.webcloud.func.email.model.EmailDetailVo;
import com.webcloud.func.email.model.EmailVo;
import com.webcloud.func.email.model.ReturnDataVo;

public class EmailEditActivity extends BaseActivity implements OnClickListener{
	private ImageView mReturn;
	private ImageView mSentEmail;
	private EditText etSentTo;
	private EditText etSentOthers;
	private EditText etFromTo;
	private EditText etTheme;
	private EditText etContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_edit);		
		initViews();
		
	}
	private void initViews() {
		mReturn = (ImageView) findViewById(R.id.ivReturn);
		mReturn.setOnClickListener(this);
		mSentEmail = (ImageView) findViewById(R.id.ivEmailSent);
		mSentEmail.setOnClickListener(this);
		etSentTo = (EditText) findViewById(R.id.tvEmailSentTo);
		etSentOthers = (EditText) findViewById(R.id.tvEmailOthers);
		etFromTo = (EditText) findViewById(R.id.tvEmailFrom);
		etTheme = (EditText) findViewById(R.id.tvEmailTheme);
		etContent = (EditText) findViewById(R.id.tvEmailContent);		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivReturn:
			this.finish();
			break;
		case R.id.ivEmailSent:			
			String sentTo = etSentTo.getText().toString().trim();
			String sentOthers = etSentOthers.getText().toString().trim();
			String mailUser = etFromTo.getText().toString().trim();//"055162681081@189.cn"
			String subject = etTheme.getText().toString().trim();
			String content = etContent.getText().toString().trim();
			Map<String, String> params = new HashMap<String, String>();
			params.put("productKey", "meeting");
			params.put("ecCode", "340100900686669");
			params.put("mailTo", sentTo);
			params.put("mailccTo",sentOthers);
			params.put("subject", subject);
			params.put("msgContent", content);
			params.put("mailUser", mailUser);
			params.put("mailPassword", "62681081");
			new Requester(this).request(requestSentListener,
	                 HttpUrlImpl.EMAIL.SENT_EMAIL,
	                 HttpUrlImpl.EMAIL.SENT_EMAIL.getUrl(),
	                 params,
	                 HttpRequest.GET,
	                 false);
			break;

		default:
			break;
		}
	}
	
	RequestListener requestSentListener = new RequestListener(){
		@Override
		public void requestStatusChanged(int statusCode, HttpUrlImpl requestId,
				String responseString, Map<String, String> requestParams) {			
			if (!(requestId instanceof HttpUrlImpl.EMAIL)) {
                return;
            }
            // 若无需业务处理就直接返回
            if (statusCode != RequestStatus.SUCCESS) {
                return;
            }            
            try {
				HttpUrlImpl.EMAIL v1 = (HttpUrlImpl.EMAIL)requestId;
				 switch (v1) {
				 	case SENT_EMAIL://
				 		if (!TextUtils.isEmpty(responseString)) {
							try {
								JSONObject jsRoot = JsonFriend.parseJSONObject(responseString);
								JsonFriend<ReturnDataVo> jsDataList = new JsonFriend<ReturnDataVo>(ReturnDataVo.class);
								String retCode = jsRoot.getString("retcode");
								if ("0".equals(retCode)){
									createDialog("发送成功", 0);
									
//									List<ReturnDataVo> returnDataList = jsDataList.parseArray(
//											jsRoot.getString("Data"));
//									for(ReturnDataVo  vo:returnDataList){
//										int code = vo.getResultCode();
//										
//									}
								}else{
									createDialog("发送失败", 1);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
				 		}
				 	break;
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}   
		}
	};
	/**
	 * 根据信息创建对话框
	 * @param msg
	 * @param flag
	 */
	private void createDialog(String msg,final int flag) {
		Dialog dialog = new AlertDialog.Builder(this).
				setTitle("提示信息").
				setMessage(msg).
				setPositiveButton("确定",new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(flag==0){
							EmailEditActivity.this.finish();
						}
						if(flag==1){
							return;
						}
					}
				}).
				create();
		dialog.show();
	}
}
