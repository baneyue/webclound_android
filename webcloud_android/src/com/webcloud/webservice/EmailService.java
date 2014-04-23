package com.webcloud.webservice;

import java.util.List;

import com.webcloud.func.email.model.EmailVo;

public interface EmailService {
	
	public List<EmailVo> getInboxEmails(String productKey,String ecCode,String mailUser,
			String mailPassword,String currentPage,String pageSize);
}
