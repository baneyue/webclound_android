package com.funlib.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

import com.webcloud.R;
import com.webcloud.WebCloudApplication;

/**
 * 配置文件读取，设置log配置。
 * 
 * @author  zoubangyue
 * @version  [版本号, 2013-8-29]
 */
public class Configuration {
    
    private static Properties properties = new Properties();
    
    static {
        InputStream config = null;
        try {
            Context context = WebCloudApplication.getInstance();
            config = context.getResources().openRawResource(R.raw.logconfig);
            properties = new Properties();
            properties.load(config);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(config != null){
                try {
                    config.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static Boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, null);
    }
    
    public static Integer getIntegerProperty(String key) {
        return getIntegerProperty(key, null);
    }
    
    public static String getProperty(String key) {
        return getProperty(key, null);
    }
    
    /** 获取布尔型配置。
     * @param key
     * @param defautValue
     * @return
     */
    public static Boolean getBooleanProperty(String key, Boolean defautValue) {
        String value = getProperty(key);
        Boolean result = null;
        if (value == null)
            return defautValue;
        else {
            try {
                result = Boolean.valueOf(value);
            } catch (Exception e) {
            	e.printStackTrace();
                result = defautValue;
            }
        }
        return result;
    }
    
    /** 获取整形配置。
     * @param key
     * @param defautValue
     * @return
     */
    public static Integer getIntegerProperty(String key, Integer defautValue) {
        String value = getProperty(key);
        Integer result = null;
        if (value == null)
            return defautValue;
        else {
            try {
                result = Integer.valueOf(value);
            } catch (Exception e) {
            	e.printStackTrace();
                result = defautValue;
            }
        }
        return result;
    }
    
    /**获取字符串配置。
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        String value = null;
        if (properties != null)
            value = properties.getProperty(key);
        if (value == null)
            value = defaultValue;
        return value;
    }
}
