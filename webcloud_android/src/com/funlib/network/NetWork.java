package com.funlib.network;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 网络设置
 * 
 * 如果是wap连接，必须要在请求前设置网络代理
 * 用法：
 * if(NetWork.isDefaultWap(CTMeetingApplication.getInstance())){
        	HttpHost proxy = new HttpHost(NetWork.getDefaultWapProxy(), NetWork.getDefaultWapPort());  
        	httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);  
        }
 * 
 * @author taojianli
 * 
 */
public class NetWork {
	
	private static final int GSM_DATA_TYPE = 0;
	private static final int CDMA_DATA_TYPE = 1;
	private static final int TD_SCDMA_DATA_TYPE = 2;
	
	public static final String CTWAP_PROXY_ENDS = "200";	//电信代理端口10.0.0.200
	public static final String CMWAP_PROXY_ENDS = "172";	//移动代理端口10.0.0.172
	public static final String CUWAP_PROXY_ENDS = "172";	//联通代理端口10.0.0.172
	
	public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	public static Uri GSM_PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn2");
	public static Uri CDMA_PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	
	/**
	 * 获取网络运营商
	 * 
	 * * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志， 
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成， 
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成， 
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。 
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可 
         * 
         * 
         * //因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
         * 
	 * @param context
	 * @return
	 */
	public static NetWorkCarrier getNetWorkCarrier(Context context){
		
	    TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
        String imsi = telManager.getSubscriberId();  
        if(imsi!=null){  
        	if(imsi.startsWith("46000") || imsi.startsWith("46002")){
        		
        		return NetWorkCarrier.CARRIER_CM;
        	}else if(imsi.startsWith("46001")){  
        		
        		return NetWorkCarrier.CARRIER_CU;
        	}else if(imsi.startsWith("46003")){  
        		
        		return NetWorkCarrier.CARRIER_CT;
        	}
        }
        
        return NetWorkCarrier.CARRIER_NONE;
	}
	
	/**
	 * 获取wifi接入点名称
	 * @param context
	 * @return
	 */
	public static String getWifiName(Context context){
		
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if(wm.isWifiEnabled()){
			
			return wm.getConnectionInfo().getSSID();
		}
		
		return null;
	}
	
	/**
	 * wifi
	 * @param a
	 * @return
	 */
	public static boolean isDefaultWifi(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null)
			return false;
		if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}

		return false;
	}
	
	/**
	 * wap
	 * @param context
	 * @return
	 */
	public static boolean isDefaultWap(Context context){
		
		if(isDefaultWifi(context))
			return false;
		
		ApnNode apnNode = getDefaultApnNode(context);
		if(apnNode != null){

			String proxy = apnNode.proxy;
			if(proxy != null && proxy.length() > 0){
				
				if(proxy.endsWith(CMWAP_PROXY_ENDS) ||
						proxy.endsWith(CTWAP_PROXY_ENDS) ||
						proxy.endsWith(CUWAP_PROXY_ENDS))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * net
	 * @param context
	 * @return
	 */
	public static boolean isDefaultNet(Context context){
		
		if(isDefaultWifi(context))
			return false;
		
		boolean ret = isDefaultWap(context);
		if(ret == false){
			
			ApnNode apnNode = getDefaultApnNode(context);
			if(apnNode == null)
				return false;
		}else{
			
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 获取默认网络制式
	 * @param context
	 * @return
	 */
	public static int getDefaultDataNetwork(Context context){
		
//	    String str = Settings.System.getString(context.getContentResolver(), "default_data_network");
//	    if ("none".equals(str))
//	      str = Settings.System.getString(context.getContentResolver(), "saved_data_network");
//	    
//	    if(str == null){
//	    	
//	    }else if (!GSM_DATA_TYPE.equals(str))
//	      str = CDMA_DATA_TYPE;
//	    
//	    return str;
		
		int defaultNet = GSM_DATA_TYPE;
		
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = tm.getPhoneType();
		if(type == TelephonyManager.PHONE_TYPE_CDMA)
			defaultNet = CDMA_DATA_TYPE;
		else
			defaultNet = GSM_DATA_TYPE;
		
		return defaultNet;
	  }
	
	/**
	 * 获取缺省的网络类型
	 * @param context
	 * @return
	 */
	public static ApnNode sDefaultApnNode = null; 
	public static ApnNode getDefaultApnNode(Context context){

		Uri tmpUri = GSM_PREFERRED_APN_URI;
		int dataType = getDefaultDataNetwork(context);
		if(dataType == GSM_DATA_TYPE){
			tmpUri = PREFERRED_APN_URI;
		}else if(dataType == CDMA_DATA_TYPE){
			tmpUri = CDMA_PREFERRED_APN_URI;
		}else{
			tmpUri = GSM_PREFERRED_APN_URI;
		}
		
		sDefaultApnNode = null;
		ContentResolver cResolver = context.getContentResolver();  
        Cursor cr = cResolver.query(tmpUri, null, null, null, null);
        if(cr != null && cr.moveToFirst()){
        	
        	sDefaultApnNode = new NetWork(). new ApnNode();
        	try {
				
        		sDefaultApnNode.apnType = cr.getString(cr.getColumnIndex("user"));
            	sDefaultApnNode.proxy =  cr.getString(cr.getColumnIndex("proxy"));
            	int index = cr.getColumnIndex("port");
            	if(index != -1)
            		sDefaultApnNode.port = cr.getInt(index);
			} catch (Exception e) {
				// TODO: handle exception
			}
        }
        
		return sDefaultApnNode;
	}
	
	/**
	 * 获取wap代理地址
	 * @return
	 */
	public static String getDefaultWapProxy(){
		
		return sDefaultApnNode == null?"":sDefaultApnNode.proxy;
	}
	
	/**
	 * 获取wap代理端口
	 * @return
	 */
	public static int getDefaultWapPort(){
		
		return sDefaultApnNode == null?0:sDefaultApnNode.port;
	}
	
	public class ApnNode{
		
		public String apnType;
		public String proxy;
		public int port;
	}
}
