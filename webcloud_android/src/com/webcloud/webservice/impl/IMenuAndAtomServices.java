package com.webcloud.webservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.webcloud.define.Constants;
import com.webcloud.model.Atom;
import com.webcloud.model.Menu;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.utils.HttpUtil;
import com.webcloud.utils.LogUtil;
import com.webcloud.utils.SysUtil;
import com.webcloud.webservice.MenuAndAtomServices;

public class IMenuAndAtomServices implements MenuAndAtomServices {

	@Override
	public MenuAndAtom getMenuAndAtom(String mobile, String productKey,
			String ecCode, String imsi) {
		try {
			Map<String,String> params = new HashMap<String, String>();
			params.put("mobile", mobile);
			params.put("productKey", productKey);
			params.put("ecCode", ecCode);
			params.put("imsi", imsi);
			Log.i("imsi", imsi);
			Log.i("eccode", ecCode);		
			
			String result = HttpUtil.doGet(Constants.HTTP_DATA_URL+Constants.HTTP_MENUANDATOM_URL,params);
			LogUtil.d(result);
			if(!TextUtils.isEmpty(result)){
				try {
					MenuAndAtom menuAndAtom = new MenuAndAtom();
					List<Menu> menus = new ArrayList<Menu>();
					JSONObject jsonObject_Root = new JSONObject(result);
					menuAndAtom.setRetCode(jsonObject_Root.getInt("retcode"));
					menuAndAtom.setRetMsg(jsonObject_Root.getString("retmsg"));
					if(0==menuAndAtom.getRetCode()){
						JSONObject jsonObject_Retdata = jsonObject_Root.getJSONObject("retdata");
						JSONArray jsonArray_Menu = (JSONArray) jsonObject_Retdata.getJSONArray("menuData");
						for(int i = 0; i < jsonArray_Menu.length(); i++){
							JSONObject jsonObject_Meun = (JSONObject) jsonArray_Menu
									.opt(i);
							Menu menu  = new Menu();
							menu.setDataUrl(jsonObject_Meun.getString("dataUrl"));
							menu.setIconUrl(jsonObject_Meun.getString("iconUrl"));
							menu.setName(jsonObject_Meun.getString("name"));
							menu.setMpackage(jsonObject_Meun.getString("package"));
							menu.setMenuType(jsonObject_Meun.getString("menuType"));
							menu.setMsg(jsonObject_Meun.getString("msg"));
							menu.setMenuKey(jsonObject_Meun.getString("menuKey"));
							menus.add(menu);
						}
						menuAndAtom.setMenus(menus);
						JSONArray jsonArray_Atom = (JSONArray) jsonObject_Retdata.getJSONArray("atomList");
						List<Atom> atoms = new ArrayList<Atom>();
						for(int i = 0; i < jsonArray_Atom.length(); i++){
							try {
								JSONObject jsonObject_Atom = (JSONObject) jsonArray_Atom
										.opt(i);
								Atom atom  = new Atom();
								atom.setCreateTime(jsonObject_Atom.getString("createTime"));
								atom.setComments(jsonObject_Atom.getString("comments"));
								atom.setCreateTime(jsonObject_Atom.getString("createTime"));
								atom.setIconUrl(jsonObject_Atom.getString("iconUrl"));
								atom.setName(jsonObject_Atom.getString("name"));
								atoms.add(atom);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						menuAndAtom.setAtoms(atoms);
						menuAndAtom.setAppKey(jsonObject_Retdata.getString("appKey"));
					}
					return menuAndAtom;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public MenuAndAtom getMenuAndAtom(String mobile, String productKey,
			String ecCode) {
		// TODO Auto-generated method stub
		return getMenuAndAtom(mobile,productKey,ecCode,SysUtil.getImsi());
	}
}
