package com.funlib.imagefilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;

import com.funlib.log.Log;

public class ImageUtily {

	/**
	 * 修改图片亮度
	 * 
	 * @param bmp
	 * @param degress
	 *            亮度[-255, 255]
	 * @return
	 */
	public static Bitmap changeBrightness(Bitmap bmp, int brightness) {

		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				Config.RGB_565);
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0,
				brightness,// 改变亮度
				0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(bmp, 0, 0, paint);

		return bitmap;
	}

	/**
	 * 修改对比度
	 * 
	 * @param bmp
	 * @param contrast
	 *            [0-1]
	 * @return
	 */
	public static Bitmap changeContrast(Bitmap bmp, float contrast) {

		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				Config.RGB_565);
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0,// 改变对比度
				0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(bmp, 0, 0, paint);
		return bitmap;
	}
	
	/**
	 * 生成圆角图片
	 * 
	 * @param bitmap
	 * @param corner
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float corner) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.RGB_565);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight()));
			final float roundPx = corner;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

	/**
	 * byte数组转换成Bitmap
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] bitmap2Byte(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//bmp.compress(Bitmap.CompressFormat.JPEG, 95, baos);
		bmp.compress(Bitmap.CompressFormat.PNG, 95, baos);
		return baos.toByteArray();
	}

	/**
	 * 数组转换成Bitmap
	 * 
	 * @param buffer
	 * @return
	 */
	public static Bitmap byte2Bitmap(byte[] buffer) {
		return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
	}

	/**
	 * Bitmap转换成Drawable
	 * 
	 * @param bmp
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bmp) {
		Drawable drawable= new BitmapDrawable(bmp);
		return drawable;
	}

	/**
	 * BitmapDrawable转换成Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(BitmapDrawable drawable) {
		return drawable.getBitmap();
	}

	/**
	 * 图片旋转
	 * 
	 * @param bmp
	 *            要旋转的图片
	 * @param degree
	 *            图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree) {

		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
				matrix, true);
	}

	/**
	 * 图片缩放
	 * 
	 * @param bm
	 * @param scale
	 *            值小于1则为缩小，否则为放大
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bm, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
				matrix, true);
	}

	/**
	 * 图片等比缩放
	 * 
	 * @param bm
	 * @param w
	 *            缩小或放大成的宽
	 * @param h
	 *            缩小或放大成的高
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bm, int w, int h) {
		Bitmap BitmapOrg = bm;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap
				.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
	}

	/**
	 * 图片反转
	 * 
	 * @param bm
	 * @param flag
	 *            0为水平反转，1为垂直反转
	 * @return
	 */
	public static Bitmap reverseBitmap(Bitmap bmp, int flag) {
		float[] floats = null;
		switch (flag) {
		case 0: // 水平反转
			floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
			break;
		case 1: // 垂直反转
			floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
			break;
		}

		if (floats != null) {
			Matrix matrix = new Matrix();
			matrix.setValues(floats);
			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrix, true);
		}

		return null;
	}

	/**
	 * 给图片添加相框，mask要是png格式，中间部分支持透明 如果src图片尺寸小于mask，会对src按照mask比例缩放
	 * 
	 * @param src
	 * @param mask
	 *            相框图片
	 * @return
	 */
	public static Bitmap addFrame(Bitmap src, Bitmap mask) {

		int src_w = src.getWidth();
		int src_h = src.getHeight();
		int mask_w = mask.getWidth();
		int mask_h = mask.getHeight();

		Bitmap tmpMask = mask;
		if (src_w != mask_w || src_h != mask_h) {

			tmpMask = ImageUtily.resizeBitmap(mask, src_w, src_h);
		}

		Drawable[] array = new Drawable[2];
		array[0] = new BitmapDrawable(src);
		array[1] = new BitmapDrawable(tmpMask);

		LayerDrawable layer = new LayerDrawable(array);
		Bitmap bitmap = Bitmap
				.createBitmap(
						src_w,
						src_h,
						layer.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.RGB_565
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		layer.setBounds(0, 0, src_w, src_h);
		layer.draw(canvas);
		if(tmpMask.isRecycled() == false){
			tmpMask.recycle();
		}
		return bitmap;
	}
	
	public static Bitmap scaleBitmapFromByte(byte[] datas,
			int width, int height) {
		return MyThumbnailUtils.scaleBitmapFromByte(datas, width, height);
	}
	
	/*public static Bitmap scaleBitmapFromFile(String filePath,
			int width, int height , int quality) {
//		return MyThumbnailUtils.scaleBitmapFromFile(filePath, width, height);
		Bitmap bmp = null;
		try {
			File f = new File(filePath);
			String tmpDestPath = f.getParent();
			if(TextUtils.isEmpty(tmpDestPath)){
				return null;
			}
			if(!tmpDestPath.endsWith(File.separator))
				tmpDestPath += File.separator;
			tmpDestPath += "tmp.jpg";
			int ret = nativeResizeBitmap(filePath, tmpDestPath, width, height, quality);
			if(ret == 0){
				
				bmp = BitmapFactory.decodeFile(tmpDestPath);
			}
			f = new File(tmpDestPath);
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}*/
	
	/*public static Bitmap scaleBitmapFromFile(String filePath,
			int width, int height , int quality, String fileDestPath) {
//		return MyThumbnailUtils.scaleBitmapFromFile(filePath, width, height);
		
		File f = new File(filePath);
		int ret = nativeResizeBitmap(filePath, fileDestPath, width, height , quality);
		Bitmap bmp = null;
		if(ret == 0){
			
			bmp = BitmapFactory.decodeFile(fileDestPath);
		}

		return bmp;
	}*/
	
	public static Bitmap scaleBitmapFromBitmap(Bitmap source, int width, int height) {
		return MyThumbnailUtils.scaleBitmapFromBitmap(source, width, height);
	}
	
	/*static{
		try {
			System.loadLibrary("imagefilter");
		} catch (Exception e) {
			e.printStackTrace();
			Log.s(null,"ImageUtily", null, e, "static imagefilter");
		}
	}*/

	public static void saveBitmapToFile(String fileName, Bitmap bmp) throws IOException
	{
		File f = new File("/sdcard/DCIM/TEMP/" + fileName + ".jpg");
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		bmp.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBitmapToFile2(String filepath, Bitmap bmp) throws IOException
	{
	    File f = new File(filepath);
	    if(f.exists()){
	        f.delete();
	    }
	    f.createNewFile();
	    FileOutputStream fOut = null;
	    try {
	        fOut = new FileOutputStream(f);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    
	    bmp.compress(Bitmap.CompressFormat.PNG, 95, fOut);
	    try {
	        fOut.flush();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    try {
	        fOut.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	//public static native int nativeResizeBitmap(String srcPath , String destPath, int w, int h , int quality);
}
