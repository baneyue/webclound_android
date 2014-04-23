package com.funlib.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 保存配置字段
 * 
 * 用法： Config.init(this)，使用前必须初始化
 * 
 * 
 * @author taojianli
 * 
 */
public class PrivateSharedPreferences {

	private static final String CONFIG_NAME = "private_config";

	public static boolean checkKeyExists(String key, Context context) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveInt(String key, int value, Context context) {
		if (!TextUtils.isEmpty(key)) {
			SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(key, value);
			editor.commit();
		}

	}

	/**
	 * 
	 * @param key
	 * @return 默认-1
	 */
	public static int getInt(String key, Context context) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(String key, boolean value, Context context) {
		if (TextUtils.isEmpty(key)) {
			SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.putBoolean(key, value);
			editor.commit();
		}

	}

	/**
	 * 
	 * @param key
	 * @return默认false
	 */
	public static boolean getBoolean(String key, Context context) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveString(String key, String value, Context context) {
		if (!TextUtils.isEmpty(key)) {
			SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * 
	 * @param key
	 * @return默认null
	 */
	public static String getString(String key, Context context) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveFloat(String key, float value, Context context) {
		if (!TextUtils.isEmpty(key)) {
			SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.putFloat(key, value);
			editor.commit();
		}
	}

	/**
	 * 
	 * @param key
	 * @return 默认值-1
	 */
	public static float getFloat(String key, Context context) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getFloat(key, -1);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveLong(String key, long value, Context context) {
		if (!TextUtils.isEmpty(key)) {

			SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();

			editor.putLong(key, value);
			editor.commit();
		}
	}

	/**
	 * 
	 * @param key
	 * @return 默认-1
	 */
	public static long getLong(String key, Context context) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getLong(key, -1);
	}
}
