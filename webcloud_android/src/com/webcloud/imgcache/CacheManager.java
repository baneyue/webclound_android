package com.webcloud.imgcache;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.webcloud.WebCloudApplication;
import com.webcloud.utils.LogUtil;

/**
 * 缓存管理
 * 
 * @author 
 * 
 */
public class CacheManager { 

	private static Object sObject;

	private static String CACHE_PATH;
	private static String CACHE_IMAGE_PATH;
	private static String CACHE_DATA_PATH;

	public static void init(Context context) {

		sObject = new Object();

		if (true) {
			CACHE_PATH = WebCloudApplication.getInstance().getCacheDir().getAbsolutePath() + "/";
		} else {
			// sd card
		}

		CACHE_IMAGE_PATH = CACHE_PATH + "/image/";
		CACHE_DATA_PATH = CACHE_PATH + "/data/";

		File dir = new File(CACHE_PATH);
		if (dir.exists() == false)
			dir.mkdirs();

		dir = new File(CACHE_IMAGE_PATH);
		if (dir.exists() == false)
			dir.mkdirs();

		dir = new File(CACHE_DATA_PATH);
		if (dir.exists() == false)
			dir.mkdirs();
	}

	// 图片缓存->>>begin
	public static void saveImage(String picUrl, byte[] img) {

		synchronized (sObject) {

			if (img == null)
				return;
			// 压缩bytes
			//byte[] zipBytes = ZipUtily.zipByte(img);

			String key = String.valueOf(picUrl.hashCode());
			String path = CACHE_IMAGE_PATH + key;
			try {

				File f = new File(path);
				if (f.exists() == true)
					f.delete();

				DataOutputStream dos = new DataOutputStream(
						new FileOutputStream(path));
				dos.write(img);

				dos.flush();
				dos.close();
				
				//修改文件最后修改时间，以备清理图片缓存时使用
				long lastModifiedTime = System.currentTimeMillis();
				f.setLastModified(lastModifiedTime);
				
				dos = null;
				f = null;

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static Drawable getImage(String picUrl) {

		synchronized (sObject) {
			String key = String.valueOf(picUrl.hashCode());
			String path = CACHE_IMAGE_PATH + key;

			File file = new File(path);
			if (file.exists() == false){
			    file = null;
				return null;
			}

			Drawable drawable = null;
			try {
				drawable = new BitmapDrawable(BitmapFactory.decodeFile(path));
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
			}

			return drawable;
		}

	}
	
	/**
	 * 清理10天前的图片缓存
	 */
	public static void clearImageCacheByTime(long currentTime){
	    
	    final int EXPRIED_TIME = 6;//清除10天前的图片缓存

	    File f = new File(CACHE_IMAGE_PATH);
	    File []fs = f.listFiles();
	    
	    if(fs != null){
	        
	        for(int i = 0 ; i < fs.length ; ++i){
	            
	            File tmpFile = fs[i];
	            
	            LogUtil.i(">>>>>image cache check file:" + tmpFile.getAbsolutePath());
	            long fileTime = tmpFile.lastModified();
	            long dayDiffer = (currentTime - fileTime)/(24 * 60 * 60 * 1000);
	            if(dayDiffer >= EXPRIED_TIME){
	            	LogUtil.i(">>>>>>image cache delete file:" + tmpFile.getAbsolutePath() + ">>day differ:" + dayDiffer);
	                tmpFile.delete();
	            }
	            
	        }
	    }
	}

	// 图片缓存->>>end
}
