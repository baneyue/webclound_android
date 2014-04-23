package com.webcloud.func.template;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.define.Constants;
import com.webcloud.define.ModelStyle;
import com.webcloud.model.UserAuth;
import com.webcloud.webservice.UserService;
import com.webcloud.webservice.impl.IUserService;

/**
 * @class:LoginActivity.java
 * @author：Wangwei
 * @date：Dec 17, 2013
 * @comment：
 */
@SuppressLint("CommitPrefEdits")
public class LoginActivity extends BaseActivity implements OnClickListener {
    
    private EditText t_username;
    
    private EditText t_password;
    
    private EditText t_productkey;
    
    private String validateCode;
    
    private String productKey;
    
    private UserService iLoginService;
    
    private UserAuth ua;
    
    private SharedPreferences settings;
    
    private SharedPreferences.Editor localEditor;
    
    private long exitTime = 0;//按返回键间隔时间
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ModelStyle.theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iLoginService = IUserService.getInstant();
        t_username = (EditText)this.findViewById(R.id.username);
        t_password = (EditText)this.findViewById(R.id.password);
        t_productkey = (EditText)this.findViewById(R.id.productkey);
        settings = this.getSharedPreferences("xml_login", 0);
        localEditor = settings.edit();
        t_username.setText(settings.getString("phoneNum", Constants.PHONE_NUMBER));
        t_productkey.setText(settings.getString("ProductKey", Constants.PRODUCTKEY));
        String ECCode = settings.getString("ECCode", "");
        if(!TextUtils.isEmpty(ECCode)){
            t_password.setText("已注册，直接登录");
            t_password.setEnabled(false);
        }
        Button btnGetPwd = (Button)this.findViewById(R.id.btnGetPwd);
        Button login = (Button)this.findViewById(R.id.login);
        btnGetPwd.setOnClickListener(this);
        login.setOnClickListener(this);
    }
    
    /**
     * 检查数据
     * 
     * @param type
     *            ： 1-只检查手机号，2-只检查码证码，3-检查全部
     */
    private boolean checkData(int type) {
        String mobile = this.t_username.getText().toString().trim();
        String validateCode = this.t_password.getText().toString();
        String productKey = this.t_productkey.getText().toString();
        if (type == 1 || type == 3) {
            if (TextUtils.isEmpty(mobile)) {
                showMessage(getString(R.string.request_phone_error));
                return false;
            }
            if (!mobile.matches("^1\\d{10}$")) {
                showMessage("您输入的手机号码格式不正确");
                return false;
            }
        }
        if (type == 2 || type == 3) {
            if (TextUtils.isEmpty(validateCode)) {
                Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (TextUtils.isEmpty(productKey)) {
                showMessage(getString(R.string.request_product_error));
                return false;
            }
        }
        return true;
    }
    
    /** 
     * 通过产品号进行登录注册，得到企业编码。
     *
     * @return
     */
    private boolean login() {
        String ECCode = settings.getString("ECCode", "");
        productKey = this.t_productkey.getText().toString().trim();
        if ("".equals(ECCode)) {
            String ret =
                iLoginService.registration((this.t_username.getText().toString()).trim(),
                    productKey,
                    t_password.getText().toString().trim());
            if (!"".equals(ret)) {
                localEditor.putString("ECCode", ret);
                localEditor.commit();
                ECCode = ret;
            }
        }
        Constants.ECCODE = ECCode;
        if ("".equals(ECCode)) {
            showMessage("注册失败!");
            return false;
        }
        return true;
    }
    
    /** 
     * 由登录注册得到的企业编码进行鉴权活动，得到产品权利。
     */
    private boolean auth() {
        String ECCode = settings.getString("ECCode", Constants.ECCODE);
        ua = iLoginService.getUserAuth(t_username.getText().toString().trim(), productKey, ECCode);
        return ua != null ? true : false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void showMessage(String str) {
        Message msg = Message.obtain();
        msg.what = 201;
        msg.obj = str;
        handler.sendMessage(msg);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetPwd:
                productKey = this.t_productkey.getText().toString().trim();
                if (checkData(1)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            validateCode =
                                iLoginService.getMsgCheckCode((t_username.getText().toString()).trim(), productKey);
                            handler.sendEmptyMessage(101);
                        }
                    }).start();
                }
                
                break;
            case R.id.login:
                if (checkData(3)) {
                    new Thread(runnable_login).start();
                }
                break;
            default:
                break;
        }
    }
    
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 101: //验证码发送成功
                    if (validateCode != null && "0".equals(validateCode)) {
                        showMessage("注册验证码已经通过短信下发到您的手机中");
                    } else if (validateCode != null) {
                        showMessage(validateCode);
                    }
                    break;
                case 102:
                    if ((Boolean)msg.obj) { //如果登陆成功
                        Constants.PHONE_NUMBER = t_username.getText().toString().trim();
                        localEditor.putString("phoneNum", t_username.getText().toString().trim());
                        if (!TextUtils.isEmpty(productKey)) {
                            Constants.PRODUCTKEY = productKey;
                            localEditor.putString("ProductKey", productKey);
                        }
                        localEditor.commit();
                        //启动不同主题主页
                        if(ModelStyle.theme == R.style.AppGridTheme){
                            Intent intent = new Intent(LoginActivity.this, HomeGridViewActivity.class);
                            startActivity(intent);
                        } else if(ModelStyle.theme == R.style.AppMetroTheme){
                            Intent intent = new Intent(LoginActivity.this, HomeMetroActivity.class);
                            startActivity(intent);
                        } else if(ModelStyle.theme == R.style.AppSlideTheme){
//                            Intent intent = new Intent(LoginActivity.this, HomeSlideMenuActivity.class);
                            //Intent intent = new Intent(LoginActivity.this, HomeSlideFragDrawActivity.class);
                            Intent intent = new Intent(LoginActivity.this, HomeSlidePanelFragActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                    break;
                case 201:
                    Toast toast = Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 420);
                    toast.show();
                    break;
                default:
                    break;
            }
        }
    };
    
    Runnable runnable_login = new Runnable() {
        
        @Override
        public void run() {
            boolean result = login();
            if(result){
                result = auth();
                Message msg = Message.obtain();
                msg.what = 102;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }
    };
    
    /**
     * 按返回键退出程序提示
     * @param keyCode 按键的码
     * @param event 按键事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
