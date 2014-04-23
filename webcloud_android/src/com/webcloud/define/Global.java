package com.webcloud.define;

import com.webcloud.model.LoginParam;


public class Global {
    
    public static final int DEFAULT_IMG_WIDTH = 800;
    
    public static final int DEFAULT_IMG_HEIGHT = 600;
    
    public static final int DEFAULT_AVATAR_WIDTH = 100;
    
    public static final int DEFAULT_AVATAR_HEIGHT = 100;
    
    /** 手势滑动距离 */
    public static final int SNAP_VELOCITY = 600;
    
    public static final long SD_SEARCH_FILE_SIZE_LIMIT = 10 * 1024;// 本地相册图片大小限制值
    
    public static final String SD_SEARCH_FILE_SUFFIX = ".jpg;.jpeg;.png;.bmp";// 本地相册搜索的文件格式
    
    public static final String EFFECT_FILE_NAME = "effect.tmp";
    
    public static final String DIR = "lbs";// 默认文件夹名
    
    public static final String CAMERA = "Camera";// 相机文件夹名
    
    public static LoginParam sLoginParam;// 登陆数据结构
    
    public static String sLocalAlbumPath;// 本地存储路径
    
    public static String sSystemDCIMPath;// 系统默认照相机路径
    
    public static String sSystemCameraPath;
    
    public static String pathOfLocalPhotoForUpload; // 从本地选取的照片的路径
    
    public static String username = "";
    
    public static String userid = "1";
    
    public static String weatherTemp = "";
    
    public static String weatherPic = "";
    
    public static String weatherAqi = "";
    
    public static String carClean = "";//洗车
    
    public static String SOSO_KEY = "f726fa9a6cb5f3c9a1385a7bfb827e58";
            
    public static final String YIXINAPPID = "yx7c697106ef8241cdbb28fc7c07cfb4d2";
}
