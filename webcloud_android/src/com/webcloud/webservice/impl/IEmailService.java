package com.webcloud.webservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.webcloud.define.Constants;
import com.webcloud.func.email.model.EmailVo;
import com.webcloud.utils.HttpUtil;
import com.webcloud.utils.LogUtil;
import com.webcloud.webservice.EmailService;

public class IEmailService implements EmailService{
	
	@Override
	public List<EmailVo> getInboxEmails(String productKey, String ecCode,
			String mailUser, String mailPassword, String currentPage,
			String pageSize) {
		List<EmailVo> mailList = new ArrayList<EmailVo>();
		String url = "http://61.191.44.251:8080/cloudservise/sso/mail_list.action";		
		Map<String, String> params = new HashMap<String, String>();
		params.put("productKey", productKey);
		params.put("ecCode", ecCode);
		params.put("mailUser", mailUser);
		params.put("mailPassword", mailPassword);
		params.put("currentPage", currentPage);
		params.put("pageSize", pageSize);
//		params.put("productKey", "meeting");
//		params.put("ecCode", "340100900686669");
//		params.put("mailUser", "055162681081@189.cn");
//		params.put("mailPassword", "62681081");
//		params.put("currentPage", "1");
//		params.put("pageSize", "10");
		String result = HttpUtil.doPost(url, params);
		LogUtil.d(result);
		
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject jsonObject_Root = new JSONObject(result);
				String code = jsonObject_Root.getString("retcode");
				if("0".equals(code)){
					JSONObject jsonObject_Retdata = jsonObject_Root.getJSONObject("retdata");
				}
//				JSONObject jsRoot = JsonFriend.parseJSONObject(result);
//				JsonFriend<EmailVo> jsDataList = new JsonFriend<EmailVo>(EmailVo.class);
//				String retCode = jsRoot.getString("retcode");
//				if ("0".equals(retCode)) {
//					mailList = jsDataList.parseArray(
//							jsRoot.getJSONObject("retdata").getString("mailList"));
//					return mailList;
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mailList;
	}
}
