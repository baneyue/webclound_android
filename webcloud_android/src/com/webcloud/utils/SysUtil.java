package com.webcloud.utils;

import java.util.UUID;

import com.webcloud.WebCloudApplication;
import com.webcloud.utily.Utily;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 获取系统属性
 * 
 * @author Administrator
 * 
 */
public class SysUtil {

	/**
	 * 获取手机Imsi号
	 * 
	 * @return
	 */
	public static String getImsi() {
		String imsi = "";
		TelephonyManager mTelephonyMgr = (TelephonyManager) WebCloudApplication.getInstance().getSystemService(
				Context.TELEPHONY_SERVICE);
		imsi = mTelephonyMgr.getSubscriberId();
		return imsi;
	}

	/**
	 * 获取手机Imei号
	 * 
	 * @return
	 */
	public static String getImei() {
		String imei = "";
		TelephonyManager mTelephonyMgr = (TelephonyManager) WebCloudApplication.getInstance().getSystemService(
				Context.TELEPHONY_SERVICE);
		imei = mTelephonyMgr.getDeviceId();
		return imei;
	}
	
	private static String uuidString = null;

	/**
	 * 获取Mac地址
	 * 
	 * @return
	 */
	public static String getMac() {
		if (uuidString == null) {
			uuidString = UUID.randomUUID().toString();
		}
		
		WifiManager wifi = (WifiManager) WebCloudApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
		if (wifi == null) {
			return uuidString;
		}
		WifiInfo info = wifi.getConnectionInfo();
		final String mac = info.getMacAddress();
		if (Utily.isStringEmpty(mac)) {
			return uuidString;
		}

		return mac;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return
	 */
	public static String getVersionCode() {
		String version = "";
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = WebCloudApplication.getInstance().getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(WebCloudApplication.getInstance().getPackageName(), 0);
			version = String.valueOf(packInfo.versionCode);
			LogUtil.d("version:  " + version);
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	public static String getVersionName() {
		String version = "";
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = WebCloudApplication.getInstance().getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(WebCloudApplication.getInstance().getPackageName(), 0);
			version = packInfo.versionName;
			LogUtil.d("version:  " + version);
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

}
