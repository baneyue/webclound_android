package com.funlib.imagefilter;

public class ImageFilter {

	/*static {

		try {
			System.loadLibrary("imagefilter");

		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public static native int nativeEffectColorMatrix(int[] srcBuf,int[] matrixBuf, int w, int h);
	public static native int nativeEffectSketch(int[] srcBuf, int w, int h);
	public static native int nativeEffectEmboss(int[] srcBuf, int w, int h);
	public static native int nativeEffectGrayscale(int[] srcBuf , int w, int h);
	public static native int nativeEffectOld(int[] srcBuf , int[] maskBuf , int w , int h);
	public static native int nativeAdjustChannel(int[] srcBuf , int w , int h , int[] ptBuf , int adjustChannel);
	public static native int nativeEffectAdjustContrast(int[] srcBuf , float value , int w , int h);*//** value[-50-100] **//*
	public static native int nativeEffectAdjustBrightness(int[] srcBuf , float value , int w , int h);*//** value[-255-255] **//*
	public static native int nativeEffectAdjustGamma(int[] srcBuf , float value , int w , int h);*//** value[9.99-0.01] **//*
	
	public static native int nativeEffectSunShine(int[] srcBuf , int w , int h , int centerX, int centerY, int radius, int strength);
	public static native int nativeEffectFangDaJing(int[] srcBuf , int width, int height,int centerX, int centerY, int radius, float multiple);
	public static native int nativeEffectHaHaJing(int[] srcBuf , int width, int height,int centerX, int centerY, int radius, float multiple);
	
	public static native int nativeEffectLomo1(int[] srcBuf , int[] maskBuf , int w , int h);
	public static native int nativeEffectLomo2(int[] srcBuf , int w , int h);
	public static native int nativeEffectLomo3(int[] srcBuf , int w , int h);
	public static native int nativeEffectFishEye(int[] srcBuf , int[] mask , int w , int h);
	public static native int nativeEffectAbaose(int[] srcBuf , int w , int h);
	
	
	public static native int nativeEffectMeibai(int[] srcBuf , int w , int h);//美白效果
	public static native int nativeEffectInvert(int[] srcBuf , int w , int h);//反相
	public static native int nativeEffectFeixue(int[] srcBuf , int[] maskBuf , int w , int h);//飞雪
	public static native int nativeEffectRixi(int[] srcBuf , int w , int h);//日系
*/}