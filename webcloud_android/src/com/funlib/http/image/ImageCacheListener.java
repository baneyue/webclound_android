package com.funlib.http.image;

import android.graphics.Bitmap;

/**
 * 获取图片的监听器
 * @author zoubangyue
 * 
 */
public interface ImageCacheListener {

	/**
	 * 图片获取结果
	 * @param statusCode 状态码，见{@link ImageCacheStatus}
	 * @param target 需要图片的对象
	 * @param bitmap 获取到的图像
	 * @param imageCache
	 */
	public void getImageFinished(int statusCode, Object target, Bitmap bitmap , ImageCache imageCache);

}
