package com.webcloud.func.email.model;

import java.io.Serializable;

public class ReturnDataVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887918853561237871L;

	private int resultCode;
	private String retmsg;

	public ReturnDataVo() {
		super();
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getRetmsg() {
		return retmsg;
	}

	public void setRetmsg(String retmsg) {
		this.retmsg = retmsg;
	}

}
