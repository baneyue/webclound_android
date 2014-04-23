package com.webcloud.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.webcloud.imgcache.CacheManager;

public class ImageTool {

	/**
	 * 
	 * 2013-7-19
	 * 
	 * @param context
	 * @param view
	 * @param imgurl
	 * @param isImg
	 *            当isImg= true 设置ImageVIew的图片，否则设置View的背景 
	 */
	public static void setImgView(Context context, View view, String imgurl,
			boolean isImg) {
		AsyLoadImg asyImg = new AsyLoadImg(context, view, isImg);
		asyImg.execute(imgurl);
	}

	private static HashMap<String , SoftReference<Drawable>> sBitampPools = new HashMap<String , SoftReference<Drawable>>();
	private static void addBitmapDrawable(String imgUrl , Drawable d){
        
        if(sBitampPools == null){
            
            sBitampPools = new HashMap<String , SoftReference<Drawable>>();
        }
        
        sBitampPools.put(imgUrl , new SoftReference<Drawable>(d));
    }
    
	private static Drawable getBitmapDrawable(String imgUrl){
        
        if(sBitampPools == null)
            return null;

        SoftReference<Drawable> sr = sBitampPools.get(imgUrl);
        if(sr == null)
            return null;
        
        return sr.get();
    }
	
	public static Drawable getDrawableByUrl(Context context, String url) {
//		if(null !=getBitmapDrawable(url)){ //如果内存中有
//			LogUtil.d("内存中有");
//			return getBitmapDrawable(url);
//		}
		Drawable drawable = CacheManager.getImage(url);
		if(null != drawable){ //如果本地有
			LogUtil.d("本地有");
//			addBitmapDrawable(url,drawable);
			return drawable;
		}else{ //从网上获取
			LogUtil.d("网上获取");
			URL myFileUrl = null;
			BitmapDrawable bmpDrawable = null;
			Bitmap bitmap = null;
			InputStream is = null;
			HttpURLConnection conn = null;
			try {
				myFileUrl = new URL(url);
				conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.setConnectTimeout(8000);
				conn.setReadTimeout(20000);
				conn.connect();
				is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				if (bitmap != null) {
					is.close();
					conn.disconnect();
					bmpDrawable = new BitmapDrawable(context.getResources(), bitmap);
//					addBitmapDrawable(url,bmpDrawable);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					CacheManager.saveImage(url, baos.toByteArray());
					return bmpDrawable;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
		return null;
	}

	private static class AsyLoadImg extends AsyncTask<String, Void, Drawable> {

		private View pimgView;
		private Context context;
		private boolean isImgView;

		public AsyLoadImg(Context context, View view, boolean isImg) {
			this.context = context;
			this.pimgView = view;
			this.isImgView = isImg;
		}

		@Override
		protected Drawable doInBackground(String... params) {
			return getDrawableByUrl(context, params[0]);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				if (isImgView && pimgView instanceof ImageView) {
					((ImageView) pimgView).setImageDrawable(result);
				} else {
					pimgView.setBackgroundDrawable(result);
				}
			}
		}
	}

}
