package com.webcloud.webservice;

import com.webcloud.model.CategoryAndContent;

public interface CategoryAndContentService {
	/**
	 * 获取后台发布的栏目内容
	 * @param productKey 产品编号
	 * @param ecCode 企业编码
	 * @param cid 栏目编码
	 * @return
	 */
	public  CategoryAndContent getCategorieOrContent(String productKey,String ecCode,String cid);
}
