package com.funlib.utily;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
/**
 * 用法：
 * Utily.init(this)，使用前必须初始化
 * 
 * @author taojianli
 * 
 */
public class Utily {

	private static Context sContext;
	
	/**
	 * init
	 * @param context
	 */
	public static void init(Context context){
		
		sContext = context;
	}
	
	/**
	 * 获取手机号码
	 * 
	 * @return
	 */
	public static String getPhoneNumber() {

		TelephonyManager tm = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	
	/** 
	 * 判断网络是否可用。
	 *
	 * @return
	 */
	public static boolean isNetworkAvailable() {
	    ConnectivityManager mConnMan = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnMan.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return info.isConnected();
    }
	
	/**
	 * 获取设备UUID
	 * 
	 * @return
	 */
	public static String getDeviceImei() {

//		if(uuidString == null){
//			
//			uuidString = UUID.randomUUID().toString();
//		}
//		
//		return uuidString;
		
		// WifiManager wifi = (WifiManager) AMTCMVApplication.getInstance()
		// .getSystemService(Context.WIFI_SERVICE);
		// WifiInfo info = wifi.getConnectionInfo();
		// return info.getMacAddress();

		 TelephonyManager tm = (TelephonyManager)sContext.getSystemService(Context.TELEPHONY_SERVICE);
		 return tm.getDeviceId();
	}

	/**
	 * 
	 * @return
	 */
	public static String getDeviceIMSI(){
		
		TelephonyManager tm = (TelephonyManager)sContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}
	
	public static String getDeviceMSISDN(Context context) {
        String msisdn = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (msisdn == null || "".equals(msisdn) || "null".equals(msisdn)) {
            msisdn = "unknowDeviceid";
        }
        return msisdn;
    }
	
	/**
	 * 设置view状态，可以不用添加xml文件
	 * 
	 * @param normal
	 * @param active
	 * @param disable
	 * @return
	 */
	public static StateListDrawable getStateDrawable(int normal, int active,
			int disable) {

		StateListDrawable states = new StateListDrawable();
		if (active != -1) {
			states.addState(new int[] { android.R.attr.state_pressed },
					sContext.getResources()
							.getDrawable(active));
			states.addState(new int[] { android.R.attr.state_focused },
					sContext.getResources()
							.getDrawable(active));
		}
		if (disable != -1) {
			states.addState(new int[] { -android.R.attr.state_enabled },
					sContext.getResources()
							.getDrawable(disable));
		}

		// 这个段代码一定要放到最后
		if (normal != -1) {
			states.addState(new int[] {}, sContext
					.getResources().getDrawable(normal));
		}

		return states;
	}

	/**
	 * 获取屏幕分辨率320x480
	 * 
	 * @param a
	 * @return
	 */
	public static String getScreenWH(Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay();

		int w = display.getWidth();
		int h = display.getHeight();

		return String.valueOf(w) + "x" + String.valueOf(h);
	}

	/**
	 * 获取屏幕宽度
	 * @param a
	 * @return
	 */
	public static int getScreenW(Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay();

		int w = display.getWidth();
		int h = display.getHeight();

		return w;
	}

	/**
	 * 获取屏幕高度
	 * @param a
	 * @return
	 */
	public static int getScreenH(Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay();

		int w = display.getWidth();
		int h = display.getHeight();

		return h;
	}
	
	public static final String TAG = "Utily";
	
	public static DisplayMetrics getScreenDM(Context context){
	    // 获取屏幕密度（方法2）  
	    DisplayMetrics dm = new DisplayMetrics();  
	    dm = context.getResources().getDisplayMetrics();
	      
	    float density  = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
	    int densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）  
	    float xdpi = dm.xdpi;             
	    float ydpi = dm.ydpi;  
	      
	    Log.e(TAG + "  DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);  
	    Log.e(TAG + "  DisplayMetrics", "density=" + density + "; densityDPI=" + densityDPI);  
	      
	    int screenWidth  = dm.widthPixels;      // 屏幕宽（像素，如：480px）  
	    int screenHeight = dm.heightPixels;     // 屏幕高（像素，如：800px）  
	      
	    Log.e(TAG + "  DisplayMetrics(111)", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);
	    return dm;
	}

	/**
	 * 隐藏输入法
	 * 
	 * @param a
	 */
	public static void hideInputMethod(Activity a) {

		InputMethodManager imm = (InputMethodManager) a
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			View focus = a.getCurrentFocus();
			if (focus != null) {
				IBinder binder = focus.getWindowToken();
				if (binder != null) {
					imm.hideSoftInputFromWindow(binder,
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}

	}

	/**
	 * 退出应用程序
	 * 
	 * @param a
	 */
	public static void quitApplication(Activity a) {

		a.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * 获取系统版本
	 * 
	 * @param a
	 * @return
	 */
	public static String getVersionCode(Activity a) {
		PackageManager pm = a.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(a.getPackageName(), 0);

			return String.valueOf(pi.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 获取系统版本
	 * 
	 * @param a
	 * @return
	 */
	public static String getVersionCode(Context ctx) {
	    PackageManager pm = ctx.getPackageManager();
	    PackageInfo pi;
	    try {
	        pi = pm.getPackageInfo(ctx.getPackageName(), 0);
	        
	        return String.valueOf(pi.versionCode);
	    } catch (NameNotFoundException e) {
	        e.printStackTrace();
	    }
	    
	    return null;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String getVersionName(Activity a) {
		PackageManager pm = a.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(a.getPackageName(), 0);

			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDPath() {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString() + "/";
		}

		return null;
	}

	/**
	 * 检查SD卡空间是否能够存储fileLength大小的文件
	 * 
	 * @param fileLength
	 * @return
	 */
	public static boolean checkSDStorageAvailable(long fileLength) {

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			if (availCount * blockSize >= fileLength)
				return true;
		}

		return false;
	}

	/**
	 * 获取设备系统版本
	 * 
	 * @return
	 */
	public static String getDeviceOS() {

		String os = android.os.Build.VERSION.RELEASE;
		os = os.toLowerCase();
		if (os.contains("android") == false) {
			os = "android" + os;
		}
		return os;
	}

	/**
	 * 获取设备型号
	 * 
	 * @return
	 */
	public static String getDeviceModel() {

		String manu = android.os.Build.MANUFACTURER;
		String model = android.os.Build.MODEL;

		String result = null;
		if (manu != null) {
			result = manu;
		}
		if (model != null) {

			if (manu != null) {
				result = manu + " " + model;
			} else {
				result = model;
			}
		}

		return result;
	}

	/**
	 * 获取CPU信息
	 * 
	 * @return
	 */
	private static String DEVICE_CPU = null;

	public static String getDeviceCPU() {

		if (DEVICE_CPU == null) {

			Process mLogcatProc = null;
			BufferedReader reader = null;
			String result = "";
			try {
				mLogcatProc = Runtime.getRuntime().exec(
						new String[] { "/system/bin/cat", "/proc/cpuinfo" });
				reader = new BufferedReader(new InputStreamReader(
						mLogcatProc.getInputStream()));

				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.toLowerCase();

					if (line.contains("processor")) {
						result += line;
					} else if (line.contains("features")) {
						result += " " + line;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result.length() <= 0) {
				result = null;
			}
			DEVICE_CPU = result;
		}

		return DEVICE_CPU;
	}

	/**
	 * 拨打电话
	 * @param activity
	 * @param telString
	 */
	public static boolean makeCall(Activity activity, String telString) {

		try {

			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + telString));
			activity.startActivity(intent);
			
			return true;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * 检查指定的包应用是否安装
	 * 
	 * @param a
	 * @param packageName
	 * @return
	 */
	public static boolean isAppExists(Activity a, String packageName) {

		PackageInfo packageInfo;
		try {
			packageInfo = a.getPackageManager().getPackageInfo(packageName, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}

		return packageInfo == null ? false : true;
	}

	/**
	 * 解析出指定包中，带有android.intent.category.LAUNCHER属性的activity名称
	 * 
	 * @param activity
	 * @param packageName
	 * @return
	 */
	public static String parserLauncherActivityName(Activity a,
			String packageName) {

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setPackage(packageName);// 如果不指定包名，默认搜索手机中的所有应用
		List<ResolveInfo> appList = a.getPackageManager()
				.queryIntentActivities(intent, 0);
		if (appList == null || appList.size() <= 0)
			return "";

		return appList.get(0).activityInfo.name;
	}
	
	/**
	 * 获取系统中安装的所有应用
	 * @param a
	 * @return
	 */
	public static List<ResolveInfo> parserAllAppList(Activity a){
		
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		return a.getPackageManager().queryIntentActivities(intent, 0);
	}

	/**
	 * 手机像素间的转换
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
    /**获取本地真实ip地址。
     * @return
     */
    public static String getExternalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface netIntf = en.nextElement();
                for (Enumeration<InetAddress> enumIp = netIntf.getInetAddresses(); enumIp.hasMoreElements();) {
                    InetAddress inetAddress = enumIp.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String ip = inetAddress.getHostAddress().toString();
                        Log.d(TAG, "真实ip地址：" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * 获取系统DCIM路径
	 * @param context
	 * @return
	 */
	public static String getSystemDCIMPath(Context context){
		
		String[] projection = { Thumbnails._ID,Thumbnails._ID, Thumbnails.IMAGE_ID,  
                Thumbnails.DATA };  
        Cursor cur = context.getContentResolver().query(Thumbnails.EXTERNAL_CONTENT_URI, projection,  
                null, null, null);  
        if (cur != null && cur.moveToFirst()) {  
        	
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);  
            do {  
            	
            	String	image_path = cur.getString(dataColumn);
            	if(TextUtils.isEmpty(image_path) == false){
            		
            		image_path = image_path.toLowerCase();
            		String[] paths = image_path.split(".thumbnails");
            		if(paths != null && paths.length > 0){
            			
            			String dcimPath = paths[0];
            			if(!TextUtils.isEmpty(dcimPath)){
            				if(!dcimPath.endsWith(File.separator))
            					dcimPath += File.separator;
            			}
            			return dcimPath;
            		}
            	}
            	
                break;
            } while (cur.moveToNext());  
  
        }
        
        return null;
	}
	
	public static boolean isPackageRunning(Context context , String packageName){
		
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = manager.getRunningTasks(Integer.MAX_VALUE);
		for(RunningTaskInfo taskInfo : list){
		
			if(taskInfo.topActivity.getPackageName().equals(packageName) ||
					taskInfo.baseActivity.getPackageName().equals(packageName)){
				
				return true;
			}
		}
		
		return false;
	}
	
	public static  String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}
}
