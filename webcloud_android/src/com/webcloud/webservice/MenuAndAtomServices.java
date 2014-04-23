package com.webcloud.webservice;


import com.webcloud.model.MenuAndAtom;

public interface MenuAndAtomServices {
	
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
}
