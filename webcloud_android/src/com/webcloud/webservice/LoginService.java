package com.webcloud.webservice;



/** 
* @class:LoginService.java  
* @author：Wangwei
* @date：2013-12-20     
* @comment：        
*/

import com.webcloud.model.UserAuth;


public interface LoginService {
	/**
	 * 获取短信验证码
	 * @param phoneNum 手机号
	 * @param productKey 产品编号
	 * @return    0： 成功
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
	 */
	public String registration(String mobile,String productKey,String checkCode,String imsi,String imei,String mac);

	/**
	 * 用户注册
	 * @param mobile  手机号
	 * @param productKey 产品编号
	 * @param checkCode 短信验证码
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
	 * 用户鉴权
	 * @param mobile 手机号
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @return
	 */
	public UserAuth getUserAuth(String mobile,String productKey,String ecCode);
	

	/**
	 * 流量日志，上传流量使用情况
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
