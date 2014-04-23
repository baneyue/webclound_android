package com.webcloud.model;

import java.util.List;

public class CategoryAndContent {
	private int retCode;
	private String retMsg;
	private int renderStyle; // 1:列表风格 2：缩略图
	private String type;// categories contents
	private List<Category> categories;
	private List<Content> contents;

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public int getRenderStyle() {
		return renderStyle;
	}

	public void setRenderStyle(int renderStyle) {
		this.renderStyle = renderStyle;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> contents) {
		this.contents = contents;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
