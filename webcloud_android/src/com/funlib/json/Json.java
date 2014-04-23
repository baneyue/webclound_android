package com.funlib.json;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Json解析类
 * 
 * 利用java反射机制，实现对类对象的解析
 * 
 * @author taojianli
 * 
 */
public class Json {

	/**
	 * 生成JSONObject对象
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject newJsonObject(String json) {

		JSONObject obj = null;
		try {
			obj = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * 获取int类型
	 * 
	 * @param obj
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static int getInteger(JSONObject obj, String key, int defaultValue) {

		int result = defaultValue;
		try {
			result = obj.getInt(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取字符串类型
	 * 
	 * @param obj
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getString(JSONObject obj, String key,
			String defaultValue) {

		String ret = defaultValue;
		try {
			ret = obj.getString(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 获取long类型
	 * 
	 * @param obj
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static long getLong(JSONObject obj, String key, Long defaultValue) {

		Long ret = defaultValue;
		try {
			ret = obj.getLong(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 获取Boolean类型
	 * 
	 * @param obj
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(JSONObject obj, String key,
			boolean defaultValue) {

		boolean ret = defaultValue;
		try {
			ret = obj.getBoolean(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 获取Double类型
	 * 
	 * @param obj
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static double getDouble(JSONObject obj, String key,
			double defaultValue) {

		double ret = defaultValue;
		try {
			ret = obj.getDouble(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
	
	/**
	 * 解析出类对象
	 * @param obj
	 * @param c
	 * @return
	 */
	private static Object getObjectImp(JSONObject obj , Class<?> c){
		
		try {
			
			Object ret = c.newInstance();
			Field[] fs = c.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				
				Field f = fs[i];
				f.setAccessible(true);
				Type type = f.getGenericType();
				String value;
				try {
					value = obj.getString(f.getName());
				} catch (Exception e) {
					value = null;
				}
				
				if (type.equals(int.class)) {
					
					try {
						
						f.setInt(ret,
								value == null ? 0 : Integer.valueOf(value));
					} catch (Exception e) {
						// TODO: handle exception
						f.setInt(ret,0);
					}
					
				} else if (type.equals(double.class)) {
					
					try {
						
						f.setDouble(ret,
								value == null ? 0 : Double.valueOf(value));
					} catch (Exception e) {
						// TODO: handle exception
						f.setDouble(ret,0);
					}
				} else if(type.equals(Long.class)){
					
					try {
						
						f.setLong(ret,
								value == null ? 0 : Long.valueOf(value));
					} catch (Exception e) {
						// TODO: handle exception
						f.setLong(ret,0);
					}
				}else if (type.getClass().equals(java.util.List.class)) {

				} else if(type.equals(Boolean.class)){
					
					try {
						
						f.setBoolean(ret,
								value == null ? false : Boolean.valueOf(value));
					} catch (Exception e) {
						// TODO: handle exception
						f.setBoolean(ret,false);
					}
				} else {
					try {
						
						f.set(ret, value);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			
		}
		
		return null;
	}

	/**
	 * 获取Object对象，比如类结构对象
	 * 
	 * int double long默认-1
	 * @param obj
	 * @param key
	 * @param c
	 * @param defaultValue
	 * @return
	 */
	public static Object getObject(JSONObject obj, String key, Class<?> c) {

		Object ret = null;
		try {

			if (c == null) {

				ret = obj.get(key);
				return ret;
			} else {
				
				ret = getObjectImp(obj.getJSONObject(key) , c);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;
	}
	
	/**
	 * 获取类数组对象
	 * @param obj
	 * @param key
	 * @param c
	 * @return
	 */
	public static <T extends Object> ArrayList<T> getList(JSONObject obj, String key ,Class<?> c){  
		
		ArrayList<T> list = new ArrayList<T>();
		try {
			
			JSONArray array = obj.getJSONArray(key);
			int size = array.length();
			for(int i = 0 ; i < size ; ++i){
				
				JSONObject objValue = array.getJSONObject(i);
				list.add((T) getObjectImp(objValue, c));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			list = null;
		}
		
		return list;  
	  
	}  
}
