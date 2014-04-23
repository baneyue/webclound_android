package com.webcloud.model;

public class Version {
	/**
	 * isNeedUpdate=0不需要更新 1 需要更新 2强制更新 3强制回退
	 */
	private int isNeedUpdate;
	private String number;
	private String msg;
	private String releaseLog;
	/**
	 * 新版本APK下载地址
	 */
	private String url;

	public int getIsNeedUpdate() {
		return isNeedUpdate;
	}

	public void setIsNeedUpdate(int isNeedUpdate) {
		this.isNeedUpdate = isNeedUpdate;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReleaseLog() {
		return releaseLog;
	}

	public void setReleaseLog(String releaseLog) {
		this.releaseLog = releaseLog;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
