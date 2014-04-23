package com.webcloud.utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.NameValuePair;

import com.webcloud.WebCloudApplication;
import com.webcloud.define.Constants;
import com.webcloud.webservice.impl.IUserService;


public class FlowUtil {

	private static String FLOW_START_TIME = "flowtime";//统计开始时间，每次发送统计完成后重置为当前时间
	private static String G3_FLOW_COUNT = "flowcount_3g";
	private static String WIFI_FLOW_COUNT = "flowcount_wifi";

	private final static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final static Lock read = readWriteLock.readLock();
	private final static Lock write = readWriteLock.writeLock();
	private static long g3;
	private static long wifi;
	private static String startTime = "";
	private static String endTime = "";
	/**
	 * 
	 * @param flag 
	 */
	public static void reset() {
		Config.saveLong(G3_FLOW_COUNT, 0);
		Config.saveLong(WIFI_FLOW_COUNT, 0);
		g3 = 0;
		wifi = 0;
		startTime = "";
		endTime = "";
		Config.saveLong(FLOW_START_TIME, System.currentTimeMillis());
	}

	/**
	 * @param count bytes
	 */
	public static void count(long count) {
		if(count <= 0) return;
		write.lock();
		if (NetWork.isDefaultWifi(WebCloudApplication.getInstance())) {
			long tmpCnt = Config.getLong(WIFI_FLOW_COUNT);
			count += tmpCnt;
			Config.saveLong(WIFI_FLOW_COUNT, count);
		} else {
			long tmpCnt = Config.getLong(G3_FLOW_COUNT);
			count += tmpCnt;
			Config.saveLong(G3_FLOW_COUNT, count);
		}
		write.unlock();
	}

	public static void sendCount() {
		read.lock();
		wifi = Config.getLong(WIFI_FLOW_COUNT);
		g3 = Config.getLong(G3_FLOW_COUNT);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		/*
		 * productKey：产品编号 orgCode：企业编码 startTime：开始时间 endTime：结束时间 type：类型
		 * ，0:3G，1：wifi flowNum：流量 （单位KB） imsi：imsi号,移动用户标识 mobile：手机号
		 */
		long flowTime = Config.getLong(FLOW_START_TIME);
		if(flowTime <= 0){
			flowTime = System.currentTimeMillis() - 1000*60*60;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startTime = sdf.format(new Date(flowTime));
			endTime = sdf.format(new Date());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		read.unlock();
		String result = IUserService.getInstant().uploadFlow(Constants.PRODUCTKEY, Constants.ECCODE, startTime, endTime,String.valueOf(g3/1024), String.valueOf(wifi/1024), SysUtil.getImsi(), Constants.PHONE_NUMBER);
		if("0".equals(result)){
			reset();
		}
	}
}
