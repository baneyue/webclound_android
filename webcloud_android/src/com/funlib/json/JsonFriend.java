package com.funlib.json;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

/**
 * json解析朋友。
 * 捕获json解析过程中的各种异常,避免重复编写捕获异常的代码。
 * 1.对于列表数据解析，抛异常时返回空list.
 * 2.对于基本对象类型，抛异常时返回null.
 * 依赖{@link com.alibaba.fastjson.JSON}
 * 
 * @author  zoubangyue
 * @version  [版本号, 2012-6-14]
 */
public class JsonFriend<T> {
    Class<T> clazz;
    
    public JsonFriend(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    public List<T> parseArray(String jsonList) {
        List<T> valueList = null;
        try {
            valueList = JSON.parseArray(jsonList, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (valueList == null) {
                valueList = new ArrayList<T>();
            }
        }
        return valueList;
    }
    
    public T parseObject(String jsonObj) {
        T obj = null;
        try {
            obj = JSON.parseObject(jsonObj, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    public T parseObject(String jsonObj,TypeReference<T> type){
        T obj = null;
        try {
            obj = JSON.parseObject(jsonObj, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    public static JSONObject parseJSONObject(String jsonObj){
        JSONObject json = null;
        try {
            json = JSON.parseObject(jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    public static JSONArray parseJSONArray(String jsonObj){
        JSONArray json = null;
        try {
            json = JSON.parseArray(jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
