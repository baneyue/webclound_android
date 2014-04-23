package com.webcloud.func.voice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.speech.DataDownloader;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechListener;
import com.iflytek.speech.SpeechUser;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.iflytek.ui.UploadDialog;
import com.webcloud.R;
import com.webcloud.WebCloudApplication;
import com.webcloud.component.NewToast;

/**
 * 语音转换文字。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-7]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class VoiceIatHolder implements RecognizerDialogListener {
    private Context context;
    
    private VoiceTextListener textLisr;
    
    public VoiceIatHolder(Context context,VoiceTextListener textLisr) {
        this.context = WebCloudApplication.getInstance();
        this.textLisr = textLisr;
        initIat();
    }
    
    //缓存，保存当前的引擎参数到下一次启动应用程序使用.
    private SharedPreferences mSharedPreferences;
    
    //识别Dialog
    private RecognizerDialog iatDialog;
    
    //上传Dialog
    private UploadDialog uploadDialog;
    
    //下载用户词表
    private DataDownloader dataDownloader;
    
    //用户词表下载结果
    private StringBuilder mDownloadResult;
    
    //初始化参数
    private String mInitParams;
    
    public void initIat() {
        mInitParams = "appid=" + context.getString(R.string.flytek_key);
        
        uploadDialog = new UploadDialog(context);
        //初始化转写Dialog, appid需要在http://open.voicecloud.cn获取.
        iatDialog = new RecognizerDialog(context, mInitParams);
        iatDialog.setListener(this);
        mDownloadResult = new StringBuilder();
        
        //dataDownloader = new DataDownloader();
        SpeechUser.getUser().login(context, null, null, mInitParams, listener);
        
        //初始化缓存对象.
        mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }
    
    /**
     * 用户登录回调监听器.
     */
    private SpeechListener listener = new SpeechListener() {
        
        @Override
        public void onData(byte[] arg0) {
        }
        
        @Override
        public void onEnd(SpeechError error) {
            if (error != null) {
                NewToast.show(context, context.getString(R.string.text_login_fail));
            }
        }
        
        @Override
        public void onEvent(int arg0, Bundle arg1) {
        }
    };
    
    /**
     * 获取字节流对应的字符串,文件默认编码为UTF-8
     * @param inputStream
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private String readStringFromInputStream(InputStream inputStream)
        throws UnsupportedEncodingException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
    
    // 互斥锁
    protected Object mSynObj = new Object();
    
    public void showIat(){
        showIatDialog();
    }
    
    /**
     * 显示转写对话框.
     * @param
     */
    public void showIatDialog() {
        //获取引擎参数
        String engine =
            mSharedPreferences.getString(context.getString(R.string.preference_key_iat_engine),
                context.getString(R.string.preference_default_iat_engine));
        
        //获取area参数，POI搜索时需要传入.
        String area = null;
        if (IatPreferenceActivity.ENGINE_POI.equals(engine)) {
            final String defaultProvince = context.getString(R.string.preference_default_poi_province);
            String province =
                mSharedPreferences.getString(context.getString(R.string.preference_key_poi_province), defaultProvince);
            final String defaultCity = context.getString(R.string.preference_default_poi_city);
            String city =
                mSharedPreferences.getString(context.getString(R.string.preference_key_poi_city), defaultCity);
            
            if (!defaultProvince.equals(province)) {
                area = "search_area=" + province;
                if (!defaultCity.equals(city)) {
                    area += city;
                }
            }
        }
        
        if (TextUtils.isEmpty(area))
            area = "";
        else
            area += ",";
        //设置转写Dialog的引擎和poi参数.
        iatDialog.setEngine(engine, area, null);
        
        //设置采样率参数，由于绝大部分手机只支持8K和16K，所以设置11K和22K采样率将无法启动录音. 
        String rate =
            mSharedPreferences.getString(context.getString(R.string.preference_key_iat_rate),
                context.getString(R.string.preference_default_iat_rate));
        if (rate.equals("rate8k"))
            iatDialog.setSampleRate(RATE.rate8k);
        else if (rate.equals("rate11k"))
            iatDialog.setSampleRate(RATE.rate11k);
        else if (rate.equals("rate16k"))
            iatDialog.setSampleRate(RATE.rate16k);
        else if (rate.equals("rate22k"))
            iatDialog.setSampleRate(RATE.rate22k);
        textLisr.setText(null);
        //弹出转写Dialog.
        iatDialog.show();
    }
    
    /**
     * RecognizerDialogListener的"结束识别会话"回调接口.
     * 参数详见<MSC开发指南>.
     * @param error
     */
    @Override
    public void onEnd(SpeechError error) {
    }
    
    /**
     * RecognizerDialogListener的"返回识别结果"回调接口.
     * 通常服务端会多次返回结果调用此接口，结果需要进行累加.
     * @param results
     * @param isLast
     */
    @Override
    public void onResults(ArrayList<RecognizerResult> results, boolean isLast) {
        StringBuilder builder = new StringBuilder();
        for (RecognizerResult recognizerResult : results) {
            builder.append(recognizerResult.text);
        }
        textLisr.setText(builder.toString());
    }
    
    public void release(){
        try {
            uploadDialog.cancel();
            uploadDialog.destory();
            iatDialog.cancel();
            iatDialog.destory();
            SpeechUser.getUser().cancel();
            SpeechUser.getUser().destory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
