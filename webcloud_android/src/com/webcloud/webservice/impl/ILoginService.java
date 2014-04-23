package com.webcloud.webservice.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.webcloud.define.Constants;
import com.webcloud.model.UserAuth;
import com.webcloud.model.Version;
import com.webcloud.utils.HttpUtil;
import com.webcloud.utils.LogUtil;
import com.webcloud.utils.SysUtil;
import com.webcloud.webservice.LoginService;


/** 
* @class:ILoginService.java  
* @author：Wangwei
* @date：2013-12-20     
* @comment：        
*/
public class ILoginService implements LoginService {

	@Override
	public String getMsgCheckCode(String mobile, String productKey) {
		String url = Constants.HTTP_DATA_URL + Constants.HTTP_MSGCHECKCODE_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("productKey", productKey);
		String result = HttpUtil.doPost(url, params);
		LogUtil.d(result);
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject jsonObject_Root = new JSONObject(result);
				int code = jsonObject_Root.getInt("retcode");
				String retMsg = jsonObject_Root.getString("retmsg");
				if (code == 0) {
					return "0";
				}else{
					return retMsg;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String registration(String mobile, String productKey,
			String checkCode, String imsi, String imei, String mac) {
		// TODO Auto-generated method stub
		String ECCode = "";
		String url = Constants.HTTP_DATA_URL+Constants.HTTP_REGISTRATION_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("productKey", productKey);
		params.put("checkCode", checkCode);
		params.put("imsi", imsi);
		params.put("imei", imei);
		params.put("mac", mac);
		String result = HttpUtil.doPost(url, params);
		LogUtil.d(result);
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject jsonObject_Root = new JSONObject(result);
				int code = jsonObject_Root.getInt("retcode");
				if(0==code){
					JSONObject jsonObject_Retdata = jsonObject_Root.getJSONObject("retdata");
					ECCode = String.valueOf(jsonObject_Retdata.getString("ECCode"));
				}else{
					 return ECCode;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ECCode;
	}

	@Override
	public String registration(String mobile, String productKey,
			String checkCode) {
		// TODO Auto-generated method stub
		return registration(mobile, productKey, checkCode,SysUtil.getImsi(),SysUtil.getImei(),SysUtil.getMac());
	}

	@Override
	public UserAuth getUserAuth(String mobile, String productKey, String ecCode,
			String imsi, String version) {
		String url = Constants.HTTP_DATA_URL + Constants.HTTP_AUTH_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("productKey", productKey);
		params.put("ecCode", ecCode);
		params.put("imsi", imsi);
		params.put("version", version);
		String result = HttpUtil.doPost(url, params);
		try {
			if(!TextUtils.isEmpty(result)){
				JSONObject jsonObject_Root = new JSONObject(result);
				if(0==jsonObject_Root.getInt("retcode")){
					JSONObject jsonObject_Retdata = jsonObject_Root.getJSONObject("retdata");
					UserAuth userAuth = new UserAuth();
					JSONObject jsonObject_Version = jsonObject_Retdata.getJSONObject("version");
					Version version1 = new Version();
					version1.setIsNeedUpdate(jsonObject_Version.getInt("isNeedUpdate"));
					version1.setNumber(jsonObject_Version.getString("number"));
					version1.setMsg(jsonObject_Version.getString("msg"));
					version1.setReleaseLog(jsonObject_Version.getString("releaseLog"));
					version1.setUrl(jsonObject_Version.getString("url"));
					userAuth.setVersion(version1);
					userAuth.setAppKey(jsonObject_Retdata.getString("appKey"));
					return userAuth;
				}
			}
		} catch (Exception e) {
			LogUtil.e(e);
		}
		return null;
	}

	@Override
	public UserAuth getUserAuth(String mobile, String productKey, String ecCode) {
		// TODO Auto-generated method stub
		return getUserAuth(mobile, productKey, ecCode, SysUtil.getImsi(),
				SysUtil.getVersionName());
	}

	@Override
	public String uploadFlow(String productKey,String ecCode,String startTime,String endTime,String flowNum_3g,
			String flowNum_wifi,String imsi,String mobile) {
		String url = Constants.HTTP_DATA_URL + Constants.HTTP_FLOW_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("productKey", productKey);
		params.put("ecCode", ecCode);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("flowNum_3g", flowNum_3g);
		params.put("flowNum_wifi", flowNum_wifi);
		params.put("imsi", imsi);
		params.put("mobile", mobile);
		String result = HttpUtil.doPost(url, params);
		LogUtil.d(result);
		return "";
	}

}
