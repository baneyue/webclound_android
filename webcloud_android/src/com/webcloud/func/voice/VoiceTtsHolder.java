package com.webcloud.func.voice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.speech.SynthesizerPlayerListener;
import com.iflytek.ui.SynthesizerDialog;
import com.webcloud.R;
import com.webcloud.WebCloudApplication;
import com.webcloud.component.NewToast;

/**
 * 文字合成语音。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-7]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class VoiceTtsHolder implements SynthesizerPlayerListener{
    private Application context;
    
    private VoiceTextListener textLisr;
    
    public VoiceTtsHolder(Activity context,VoiceTextListener textLisr){
        this.context = WebCloudApplication.getInstance();
        this.textLisr = textLisr;
        initTts();
    }
    
    /** 
     * 初始化语音tts,语音合成.
     *
     */
    public void initTts() {
        mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        //初始化合成Dialog.
        ttsDialog = new SynthesizerDialog(context, "appid=" + context.getString(R.string.flytek_key));
    }
    
    /**
     * 使用SynthesizerPlayer合成语音，不弹出合成Dialog.
     * @param
     */
    private void synthetizeInSilence() {
        if (null == mSynthesizerPlayer) {
            //创建合成对象.
            mSynthesizerPlayer =
                SynthesizerPlayer.createSynthesizerPlayer(context, "appid=" + context.getString(R.string.flytek_key));
        }
        
        //设置合成发音人.
        String role =
            mSharedPreferences.getString(context.getString(R.string.preference_key_tts_role),
                context.getString(R.string.preference_default_tts_role));
        mSynthesizerPlayer.setVoiceName(role);
        
        //设置发音人语速
        int speed = mSharedPreferences.getInt(context.getString(R.string.preference_key_tts_speed), 50);
        mSynthesizerPlayer.setSpeed(speed);
        
        //设置音量.
        int volume = mSharedPreferences.getInt(context.getString(R.string.preference_key_tts_volume), 50);
        mSynthesizerPlayer.setVolume(volume);
        
        //设置背景音.
        String music =
            mSharedPreferences.getString(context.getString(R.string.preference_key_tts_music),
                context.getString(R.string.preference_default_tts_music));
        mSynthesizerPlayer.setBackgroundSound(music);
        
        String source = textLisr.getText();
        
        //进行语音合成.
        mSynthesizerPlayer.playText(source, null, this);
        NewToast.show(context, String.format(context.getString(R.string.tts_toast_format), 0, 0));
    }
    
    /**
     * 弹出合成Dialog，进行语音合成
     * @param
     */
    public void showSynDialog() {
        
        String source = textLisr.getText();
        //设置合成文本.
        ttsDialog.setText(source, null);
        
        //设置发音人.
        String role =
            mSharedPreferences.getString(context.getString(R.string.preference_key_tts_role),
                context.getString(R.string.preference_default_tts_role));
        ttsDialog.setVoiceName(role);
        
        //设置语速.
        int speed = mSharedPreferences.getInt(context.getString(R.string.preference_key_tts_speed), 50);
        ttsDialog.setSpeed(speed);
        
        //设置音量.
        int volume = mSharedPreferences.getInt(context.getString(R.string.preference_key_tts_volume), 50);
        ttsDialog.setVolume(volume);
        
        //设置背景音.
        String music =
            mSharedPreferences.getString(context.getString(R.string.preference_key_tts_music),
                context.getString(R.string.preference_default_tts_music));
        ttsDialog.setBackgroundSound(music);
        
        //弹出合成Dialog
        ttsDialog.show();
    }
    
    //缓存对象.
    private SharedPreferences mSharedPreferences;
    
    //合成对象.
    private SynthesizerPlayer mSynthesizerPlayer;
    
    //缓冲进度
    private int mPercentForBuffering = 0;
    
    //播放进度
    private int mPercentForPlaying = 0;
    
    //合成Dialog
    private SynthesizerDialog ttsDialog;
    
    /**
     * SynthesizerPlayerListener的"播放进度"回调接口.
     * @param percent,beginPos,endPos
     */
    @Override
    public void onBufferPercent(int percent, int beginPos, int endPos) {
        mPercentForBuffering = percent;
        NewToast.show(context,
            String.format(context.getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));
    }
    
    /**
     * SynthesizerPlayerListener的"开始播放"回调接口.
     * @param 
     */
    @Override
    public void onPlayBegin() {
    }
    
    /**
     * SynthesizerPlayerListener的"暂停播放"回调接口.
     * @param 
     */
    @Override
    public void onPlayPaused() {
    }
    
    /**
     * SynthesizerPlayerListener的"播放进度"回调接口.
     * @param percent,beginPos,endPos
     */
    @Override
    public void onPlayPercent(int percent, int beginPos, int endPos) {
        mPercentForPlaying = percent;
        NewToast.show(context,
            String.format(context.getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));
    }
    
    /**
     * SynthesizerPlayerListener的"恢复播放"回调接口，对应onPlayPaused
     * @param 
     */
    @Override
    public void onPlayResumed() {
    }
    
    /**
     * SynthesizerPlayerListener的"结束会话"回调接口.
     * @param error
     */
    @Override
    public void onEnd(SpeechError error) {
    }
    
    public void showTts(){
        boolean show = mSharedPreferences.getBoolean(context.getString(R.string.preference_key_tts_show), true);
        if (show) {
            //显示合成Dialog.
            showSynDialog();
        } else {
            //不显示Dialog，后台合成语音.
            synthetizeInSilence();
        }
    }
    
    public void release(){
        if (null != mSynthesizerPlayer) {
            try {
                if(ttsDialog != null){
                    ttsDialog.destory();
                }
                mSynthesizerPlayer.cancel();
                mSynthesizerPlayer.destory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        context = null;
    }
}
