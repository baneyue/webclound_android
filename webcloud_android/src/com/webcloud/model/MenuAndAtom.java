package com.webcloud.model;

import java.util.List;

public class MenuAndAtom {
	private int retCode;
	private String retMsg;
	private List<Menu> menus;
	private List<Atom> atoms;
	private String appKey;
	public int getRetCode() {
		return retCode;
	}
	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public List<Menu> getMenus() {
		return menus;
	}
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	public List<Atom> getAtoms() {
		return atoms;
	}
	public void setAtoms(List<Atom> atoms) {
		this.atoms = atoms;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
}
