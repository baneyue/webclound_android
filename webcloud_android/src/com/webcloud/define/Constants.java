package com.webcloud.define;

public class Constants {
	
	public  static final boolean Debug = true; //调试模式，打印日志
	
	
	public static final String HTTP_DATA_URL = "http://61.191.44.251/cloudservise/";
	/**
	 * 获取菜单
	 */
	public static String HTTP_MENUANDATOM_URL = "/sso/sso_getMenuAndAtom.action"; 
	/**
	 * 用户鉴权
	 */
	public static String HTTP_AUTH_URL = "/sso/sso_auth.action";
	/**
	 * 获取短信验证码
	 */
	public static String HTTP_MSGCHECKCODE_URL = "/sso/sso_obtainCode.action";
	
	/**
	 * 用户注册
	 */
	public static String HTTP_REGISTRATION_URL = "/sso/sso_register.action";
	
	/**
	 * 栏目和内容获取
	 */
	public static String HTTP_CATEGORIES_URL = "/client/api/categories.action";
	
	
	/**
	 * 流量日志
	 */
	public static String HTTP_FLOW_URL ="/sso/client_flowLog.action";
	
	/**
	 * 清除7天前的图片缓存
	 */
	public static final int EXPRIED_TIME = 7;
	
	/**
	 * 用户登陆手机号码
	 */
	public static String PHONE_NUMBER = "15856967011";
	
	/**
	 * 产品编号
	 */
	public static String PRODUCTKEY ="meeting";
	
	/**
	 *企业编码
	 */
	public static String ECCODE ="";

}
