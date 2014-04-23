package com.webcloud;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.wbtech.ums.UmsAgent;
import com.webcloud.define.Constants;
import com.webcloud.define.ModelStyle;
import com.webcloud.func.template.HomeMetroActivity;
import com.webcloud.func.template.LoginActivity;
import com.webcloud.webservice.impl.ILoginService;
import com.webcloud.webservice.impl.IUserService;

public class MainActivity extends BaseActivity implements View.OnClickListener{
	
	 private Intent intent;
	 private Button btn1,btn2,btn3;
	 private ILoginService iLoginService;
	 private IUserService iUserService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iLoginService = new ILoginService();
		iUserService = IUserService.getInstant();
//		initUMS();
//		initView();
		intent = new Intent(this,LoginActivity.class);
		this.startActivity(intent);
		this.finish();
		System.gc();
	}

	private void initView(){
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
	}
	
    /**
     * 初始化数据统计，在应用的入口activity的onCreate方法中调用一次即可
     */
    private void initUMS() {
        UmsAgent.setBaseURL(Constants.HTTP_DATA_URL);
        //UmsAgent.update(this);//应用更新推送
        UmsAgent.onError(this);
        UmsAgent.setDefaultReportPolicy(this, 0);//0或者1，其中1表示实时发送，0表示启动时发送。
        // UmsAgent.bindUserIdentifier(this, "xd..");//用户唯一标示
        UmsAgent.postClientData(this);
        UmsAgent.updateOnlineConfig(this);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					//iLoginService.getMsgCheckCode(TestClass.mobile, TestClass.productKey);
//					UserAuth userAuth=  iLoginService.getUserAuth(TestClass.mobile, TestClass.productKey,TestClass.ecCode);
//					if(userAuth!=null){
//						System.out.println(userAuth.getAppKey());
//						System.out.println(userAuth.getVersion().getMsg());
//					}
					//iUserService.getMenuAndAtom(TestClass.ecCode, TestClass.productKey, TestClass.ecCode);
//					iUserService.getCategorieOrContent(TestClass.mobile, TestClass.ecCode, "1");
				}
			}).start();
			break;
		case R.id.button2:
			this.getSharedPreferences("xml_login", 0).edit().putString("ECCode", Constants.ECCODE).commit();
			intent = new Intent(this,HomeMetroActivity.class);
			this.startActivity(intent);
			break;
		case R.id.button3:
			intent = new Intent(this,LoginActivity.class);
			this.startActivity(intent);
			break;

		default:
			break;
		}
		
	}

}
