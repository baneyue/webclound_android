package com.webcloud.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.webcloud.define.Constants;

public class LogUtil {

	private static final String TAG = "WD";
	private static final String INTENT = "com.wd.webcloud.log";

	public static void i(Context context, String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.i(TAG, msg);
				Intent intent = new Intent(INTENT);
				intent.putExtra("msg", msg);
				context.sendBroadcast(intent);
			}
		}
	}

	public static void i(String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.i(TAG, msg);
			}
		}
	}

	public static void d(String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.d(TAG, msg);
			}
		}
	}

	public static void d(Context context, String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.d(TAG, msg);
				Intent intent = new Intent(INTENT);
				intent.putExtra("msg", msg);
				context.sendBroadcast(intent);
			}
		}
	}

	public static void w(String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.w(TAG, msg);
			}
		}
	}

	public static void w(Exception e) {
		if (Constants.Debug) {
			Log.w(TAG, null == e ? "null" : e.toString());
		}
	}

	public static void w(Context context, String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.w(TAG, msg);
				Intent intent = new Intent(INTENT);
				intent.putExtra("msg", msg);
				context.sendBroadcast(intent);
			}
		}
	}

	public static void e(String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.e(TAG, msg);
			}
		}
	}

	public static void e(Exception e) {
		if (Constants.Debug) {
			Log.e(TAG, null == e ? "null" : e.toString());
			if (null != e) {
				e.printStackTrace();
			}
		}
	}

	public static void e(Context context, String msg) {
		if (Constants.Debug) {
			if(!TextUtils.isEmpty(msg)){
				Log.e(TAG, msg);
				Intent intent = new Intent(INTENT);
				intent.putExtra("msg", msg);
				context.sendBroadcast(intent);
			}
		}
	}
}
