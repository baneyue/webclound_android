package com.webcloud.webservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.webcloud.define.Constants;
import com.webcloud.model.Category;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Content;
import com.webcloud.utils.HttpUtil;
import com.webcloud.utils.LogUtil;
import com.webcloud.webservice.CategoryAndContentService;

public class ICategoryAndContentService implements CategoryAndContentService {

	@Override
	public CategoryAndContent getCategorieOrContent(String productKey,String ecCode,String cid) {
		String url = Constants.HTTP_DATA_URL + Constants.HTTP_CATEGORIES_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put("productKey", productKey);
		params.put("ecCode", ecCode);
		params.put("cid", cid);
		String result = HttpUtil.doPost(url, params);
		LogUtil.d(result);
		if (!TextUtils.isEmpty(result)) {
			try {
				CategoryAndContent categoryAndContent = new CategoryAndContent();
				JSONObject jsonObject_Root = new JSONObject(result);
				int code = jsonObject_Root.getInt("retcode");
				categoryAndContent.setRetCode(code);
				String retMsg = jsonObject_Root.getString("retmsg");
				categoryAndContent.setRetMsg(retMsg);
				if (code == 0) {
					JSONObject jsonObject_retData = jsonObject_Root.getJSONObject("retdata");
					int renderStyle = jsonObject_retData.getInt("renderStyle");
					categoryAndContent.setRenderStyle(renderStyle);
					String type = jsonObject_retData.getString("type");
					categoryAndContent.setType(type);
					if("categories".equalsIgnoreCase(type)){
						List<Category> categories = new ArrayList<Category>();
						JSONArray jsonArray_categories = (JSONArray) jsonObject_retData.getJSONArray("categories");
						for(int i = 0; i < jsonArray_categories.length(); i++){
							Category category = new Category();
							JSONObject jsonObject_Catgorie = (JSONObject) jsonArray_categories
									.opt(i);
							category.setId(jsonObject_Catgorie.getString("id"));
							category.setTitle(jsonObject_Catgorie.getString("title"));
							category.setIcon(jsonObject_Catgorie.getString("icon"));
							categories.add(category);
							categoryAndContent.setCategories(categories);
						}
					}else if("contents".equalsIgnoreCase(type)){
						List<Content> contents = new ArrayList<Content>();
						JSONArray jsonArray_contents = (JSONArray) jsonObject_retData.getJSONArray("contents");
						for(int i = 0; i < jsonArray_contents.length(); i++){
							Content content = new Content();
							JSONObject jsonObject_content = (JSONObject) jsonArray_contents
									.opt(i);
							content.setId(jsonObject_content.getString("id"));
							content.setTitle(jsonObject_content.getString("title"));
							content.setIcon(jsonObject_content.getString("icon"));
							content.setContent(jsonObject_content.getString("content"));
							contents.add(content);
							categoryAndContent.setContents(contents);
						}
					}
					return categoryAndContent;
				}else{
					return categoryAndContent;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
