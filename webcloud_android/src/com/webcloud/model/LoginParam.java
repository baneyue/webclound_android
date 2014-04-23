package com.webcloud.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 
 * @author  zoubangyue
 * @version  [版本号, 2013-9-8]
 */
public class LoginParam implements Serializable {
    /**注释内容*/
    private static final long serialVersionUID = -1431065847718345624L;
    /**------------------网络查询到的数据----------------------*/
    
    public String imei;
    public String uid;
    public String mobile;
    public String cversion;
    public String imsi;
    public String msisdn;
    
    private void readObject(ObjectInputStream ois)
        throws Exception {
        parse((String)ois.readObject());
    }
    
    private void writeObject(ObjectOutputStream oos)
        throws Exception {
        oos.writeObject(toJsonObject().toString());
    }
    
    public void parse(String data)
        throws Exception {
        if ((data != null) && (data.length() != 0))
            parse(JSON.parseObject(data));
    }
    
    /** 分别解析字段。
     * @param json
     * @throws Exception
     */
    public void parse(JSONObject json)
        throws Exception {
        if (json != null) {
            if (json.containsKey("imei"))
                this.imei = json.getString("imei");
        }
    }
    
    /** 对选择日期参数生成json对象。
     * @return
     * @throws Exception
     */
    public JSONObject toJsonObject()
        throws Exception {
        JSONObject json = new JSONObject();
        json.put("imei", this.imei);
        return json;
    }
    
    public String toJsonString()
        throws Exception {
        return toJsonObject().toJSONString();
    }
    
    @Override
    public String toString() {
        try {
            return toJsonString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
