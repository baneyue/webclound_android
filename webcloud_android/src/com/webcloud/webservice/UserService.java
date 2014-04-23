package com.webcloud.webservice;

import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.model.UserAuth;

public interface UserService {
	/**
	 * 获取菜单
	 * @param mobile 手机号
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @param imsi imsi号,移动用户标识
	 * @return
	 */
	public MenuAndAtom getMenuAndAtom(String mobile,String productKey,String ecCode,String imsi);
	
	/**
	 * @param mobile 手机号
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @return
	 */
	public MenuAndAtom getMenuAndAtom(String mobile,String productKey,String ecCode);
	
	/**
	 * 获取短信验证码
	 * @param phoneNum 手机号
	 * @param productKey 产品编号
	 * @return false：失败   true： 成功
	 */
	public String getMsgCheckCode(String mobile,String productKey);

	/**
	 * 用户注册
	 * @param mobile 手机号
	 * @param productKey 产品编号
	 * @param checkCode 短信验证码
	 * @param imsi imsi号,移动用户标识
	 * @param imei 手机imei串号
	 * @param mac 手机mac地址
	 * @return fasle : 注册失败  true:注册成功
	 */
	public String registration(String mobile,String productKey,String checkCode,String imsi,String imei,String mac);

	/**
	 * 
	 * @param mobile  手机号
	 * @param productKey 产品编号
	 * @param checkCode 短信验证码
	 * @return fasle : 注册失败  true:注册成功
	 */
	public String registration(String mobile,String productKey,String checkCode);
	
	/**
	 * 用户鉴权
	 * @param mobile 手机号
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @param imsi imsi号,移动用户标识
	 * @param version 当前客户端的版本
	 * @return 
	 */
	public UserAuth getUserAuth(String mobile,String productKey,String ecCode,String imsi,String version);
	
	/**
	 * 
	 * @param mobile 手机号
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @return
	 */
	public UserAuth getUserAuth(String mobile,String productKey,String ecCode);
	
	
	/**
	 * 获取后台发布的栏目内容
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @param cid 栏目编码
	 * @return
	 */
	public  CategoryAndContent getCategorieOrContent(String productKey,String ecCode,String cid);


	/**
	 * 
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @param startTime 开始时间  2013-09-10 18:21:59
	 * @param endTime 结束时间 2013-09-10 18:21:59
	 * @param flowNum_3g 3G流量
	 * @param flowNum_wifi wifi流量
	 * @param imsi imsi号,移动用户标识
	 * @param mobile 手机号
	 * @return
	 */
	public String uploadFlow(String productKey,String ecCode,String startTime,String endTime,String flowNum_3g,
			String flowNum_wifi,String imsi,String mobile);
}
