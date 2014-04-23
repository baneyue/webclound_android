package com.webcloud.webservice.impl;

import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.model.UserAuth;
import com.webcloud.webservice.UserService;

public class IUserService implements UserService {

	private static IUserService instants;
	private static ILoginService iLoginService;
	private static IMenuAndAtomServices iMenuAndAtomServices;
	private static ICategoryAndContentService iCategoryAndContentService;
	private IUserService(){
	}
	
	public static IUserService getInstant() {
		if (null == instants) {
			instants = new IUserService();
			iLoginService = new ILoginService();
			iMenuAndAtomServices = new IMenuAndAtomServices();
			iCategoryAndContentService = new ICategoryAndContentService();
		}
		return instants;
	}

	@Override
	public MenuAndAtom getMenuAndAtom(String mobile, String productKey,
			String ecCode, String imsi) {
		// TODO Auto-generated method stub
		MenuAndAtom menuAndAtom = iMenuAndAtomServices.getMenuAndAtom(mobile, productKey, ecCode,
				imsi);
		// 发起一个请求，发送流量统计数据
		//FlowUtil.sendCount();
		return menuAndAtom;
	
	}

	@Override
	public MenuAndAtom getMenuAndAtom(String mobile, String productKey,
			String ecCode) {
		// TODO Auto-generated method stub
		MenuAndAtom menuAndAtom = iMenuAndAtomServices.getMenuAndAtom(mobile, productKey, ecCode);
		// 发起一个请求，发送流量统计数据
		return menuAndAtom;
	}

	@Override
	public String getMsgCheckCode(String mobile, String productKey) {
		// TODO Auto-generated method stub
		String result = iLoginService.getMsgCheckCode(mobile, productKey);
		//FlowUtil.sendCount();
		return result;
	}

	@Override
	public String registration(String mobile, String productKey,
			String checkCode, String imsi, String imei, String mac) {
		// TODO Auto-generated method stub
		String result =  iLoginService.registration(mobile, productKey, checkCode, imsi,
				imei, mac);
		//FlowUtil.sendCount();
		return result;
	}

	@Override
	public String registration(String mobile, String productKey,
			String checkCode) {
		// TODO Auto-generated method stub
		String result =  iLoginService.registration(mobile, productKey, checkCode);
		//FlowUtil.sendCount();
		return result;
	}

	@Override
	public UserAuth getUserAuth(String mobile, String productKey,
			String ecCode, String imsi, String version) {
		// TODO Auto-generated method stub
		UserAuth result = iLoginService.getUserAuth(mobile, productKey, ecCode, imsi,
				version);
		//FlowUtil.sendCount();
		return result;
	}

	@Override
	public UserAuth getUserAuth(String mobile, String productKey, String ecCode) {
		// TODO Auto-generated method stub
		UserAuth result = iLoginService.getUserAuth(mobile, productKey, ecCode);
		//FlowUtil.sendCount();
		return result;
	}

	@Override
	public CategoryAndContent getCategorieOrContent(String productKey,
			String ecCode, String cid) {
		// TODO Auto-generated method stub
		CategoryAndContent result = iCategoryAndContentService.getCategorieOrContent(productKey, ecCode, cid);
		//FlowUtil.sendCount();
		return result;
	}

	@Override
	public String uploadFlow(String productKey,String ecCode,String startTime,String endTime,String flowNum_3g,
			String flowNum_wifi,String imsi,String mobile) {
		// TODO Auto-generated method stub
		return  iLoginService.uploadFlow(productKey, ecCode, startTime, endTime, flowNum_3g, flowNum_wifi, imsi, mobile);
	}

}
