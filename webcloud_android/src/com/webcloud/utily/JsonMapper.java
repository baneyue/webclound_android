package com.webcloud.utily;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.TypeReference;
import com.funlib.json.JsonFriend;

public class JsonMapper {
    public static JsonFriend<String> jfJson = new JsonFriend<String>(String.class);
    public static TypeReference<Map<String,Object>> typeRefMap = new TypeReference<Map<String,Object>>(){};
    public static TypeReference<Map<Object,List<Object>>> typeRefCityMapList = new TypeReference<Map<Object,List<Object>>>(){};
}
