package com.funlib.imagefilter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class MyThumbnailUtils {
	private static final int UNCONSTRAINED = -1;

	/* Options used internally. */
	private static final int OPTIONS_NONE = 0x0;
	private static final int OPTIONS_SCALE_UP = 0x1;
	public static final int OPTIONS_RECYCLE_INPUT = 0x2;

	public static Bitmap scaleBitmapFromByte(byte[] datas, int width, int height) {

		if (width == 0 && height == 0) {

			return BitmapFactory.decodeByteArray(datas, 0, datas.length);
		}

		int targetSize = Math.min(width, height);
		int maxPixels = width * height;
		Bitmap bitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(datas, 0, datas.length, options);
		if (options.mCancel || options.outWidth == -1
				|| options.outHeight == -1) {
			return null;
		}
		options.inSampleSize = computeSampleSize1(options, width, height);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length, options);
		bitmap = extractThumbnail(bitmap, width, height, OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public static Bitmap scaleBitmapFromFile(String filePath, int width,
			int height) {

		if (width == 0 && height == 0) {

			return BitmapFactory.decodeFile(filePath);
		}

		int targetSize = Math.min(width, height);
		int maxPixels = width * height;
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		if (options.mCancel || options.outWidth == -1
				|| options.outHeight == -1) {
			return null;
		}
		options.inSampleSize = computeSampleSize1(options, width, height);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		bitmap = BitmapFactory.decodeFile(filePath, options);
		
		bitmap = extractThumbnail(bitmap, width, height, OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public static Bitmap scaleBitmapFromBitmap(Bitmap source, int width,
			int height) {
		return extractThumbnail(source, width, height, OPTIONS_NONE);
	}

	/**
	 * Creates a centered bitmap of the desired size.
	 * 
	 * @param source
	 *            original bitmap source
	 * @param width
	 *            targeted width
	 * @param height
	 *            targeted height
	 * @param options
	 *            options used during thumbnail extraction
	 */
	private static Bitmap extractThumbnail(Bitmap source, int width,
			int height, int options) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap thumbnail = transform(matrix, source, width, height,
				OPTIONS_SCALE_UP | options);
		return thumbnail;
	}

	private static int computeSampleSize1(BitmapFactory.Options options,
			int targetW, int targetH) {

//		int s = 2;
//		while ((options.outWidth / s > targetW) || (options.outHeight / s > targetH)) {
//			s *= 2;
//		}
//		return s;
		
		int yRatio = (int) Math.ceil(options.outHeight / targetH);
        int xRatio = (int) Math.ceil(options.outWidth / targetW);
        if (yRatio > 1 || xRatio > 1) {
            if (yRatio > xRatio) {
                return yRatio;
            } else {
            	return xRatio;
            }
        } 
        
        return 1;
	}

	/*
	 * Compute the sample size as a function of minSideLength and
	 * maxNumOfPixels. minSideLength is used to specify that minimal width or
	 * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
	 * pixels that is tolerable in terms of memory usage.
	 * 
	 * The function returns a sample size based on the constraints. Both size
	 * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
	 * indicates no care of the corresponding constraint. The functions prefers
	 * returning a sample size that generates a smaller bitmap, unless
	 * minSideLength = IImage.UNCONSTRAINED.
	 * 
	 * Also, the function rounds up the sample size to a power of 2 or multiple
	 * of 8 because BitmapFactory only honors sample size this way. For example,
	 * BitmapFactory downsamples an image by 2 even though the request is 3. So
	 * we round up the sample size to avoid OOM.
	 */
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
				.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == UNCONSTRAINED)
				&& (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * Transform source Bitmap to targeted width and height.
	 */
	private static Bitmap transform(Matrix scaler, Bitmap source,
			int targetWidth, int targetHeight, int options) {
		boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
		boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

//		int width = source.getWidth();
//		int height = source.getHeight();
//
//		float scaleWidth = ((float) targetWidth) / width;
//		float scaleHeight = ((float) targetHeight) / height;
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		Bitmap resizedBitmap = Bitmap
//				.createBitmap(source, 0, 0, width, height, matrix, true);
//		if(recycle ){
//			source.recycle();
//			source = null;
//		}
//		return resizedBitmap;
		int srcWidth = source.getWidth();
		int srcHeight = source.getHeight();
		double rate1 = ((double) srcWidth) / (double) targetWidth + 0.1;
		double rate2 = ((double) srcHeight) / (double) targetHeight + 0.1;
		// 根据缩放比率大的进行缩放控制
		double rate = rate1 > rate2 ? rate1 : rate2;
		int newWidth = (int) (((double) srcWidth) / rate);
		int newHeight = (int) (((double) srcHeight) / rate);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
		if(recycle){
			source.recycle();
			source = null;
		}
		return scaledBitmap;
	}

}
