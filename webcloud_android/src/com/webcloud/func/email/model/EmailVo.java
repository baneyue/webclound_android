package com.webcloud.func.email.model;

import java.io.Serializable;

/**
 * 邮件信息模型
 * 
 * @author ZhangZheng
 * @date 2014-01-20
 */
public class EmailVo implements Serializable {

	private static final long serialVersionUID = -6126070893021672329L;

	private String content;
	private String fileList;
	private String flag;
	private String from;
	private boolean hasFile;// 是否有附件
	private int id;
	private String sentDate;
	private String subject;
	private String to;

	public EmailVo() {
		super();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isHasFile() {
		return hasFile;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSentDate() {
		return sentDate;
	}

	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
