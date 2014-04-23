package com.webcloud.func.voice;

/**
 * 语音使用接口。
 * 
 * @author  bangyue
 * @version  [版本号, 2013-11-12]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface VoiceTextListener {
    /** 
     * 获取要读出的文字。
     * 给文字转语音使用的接口。
     *
     * @return
     */
    public String getText();
    
    /** 
     * 设置检测到的文本。
     * 给语音转文字使用的接口。
     *
     * @param txt
     */
    public void setText(String txt);
    
    /** 
     * 追加检测到的文本。
     * 给语音转文字使用的接口。
     *
     * @param txt
     */
    public void addText(String txt);
}
