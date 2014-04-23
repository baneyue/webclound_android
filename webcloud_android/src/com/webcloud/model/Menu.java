package com.webcloud.model;

public class Menu {
	
	/**
	 * 接口地址
	 */
	private String dataUrl;
	/**
	 * 图标地址
	 */
	private String iconUrl;
	/**
	 * 菜单标题
	 */
	private String name;
	/**
	 * package 第三方应用包名
	 */
	private String mpackage;
	/**
	 * 菜单类型local(基础菜单)、webview(自定义菜单)、app(第三方应用)
	 */
	private String menuType;
	/**
	 * 描述信息（可选）
	 */
	private String msg;
	/**
	 * 菜单唯一标识，用于和客户端activity对应
	 */
	private String menuKey;
	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMpackage() {
		return mpackage;
	}
	public void setMpackage(String mpackage) {
		this.mpackage = mpackage;
	}
	public String getMenuType() {
		return menuType;
	}
	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMenuKey() {
		return menuKey;
	}
	public void setMenuKey(String menuKey) {
		this.menuKey = menuKey;
	}

}
