package com.xiaoguo.wasp.mobile.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	JSONObject requsetJsonUser = null;
	
	public JSONObject getRegisterJson(){
		JSONObject object = new JSONObject();
		
		return object;
	}
	/*
	 * 创建注册请求Json
	 * params：account
	 *        passwd
	 *        type
	 *  return：json      
	 * */
	public static JSONObject getLoginJson(String account,String password){
		//JSONObject object = new JSONObject();
		JSONObject object1 = new JSONObject();
	//	JSONObject object2 = new JSONObject();
		try {
			object1.put("account", account);
			object1.put("passwd", password);
			/*object2.put("account", account);
			object2.put("session", "");
			object.put("user", object2);
			object.put("data", object1);*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object1;
	}
	/*
	 * 判断联网是否正确，正确返回json数据
	 * */
	public static JSONObject decodeResponse(JSONObject json){
		JSONObject object = new JSONObject();
		int code = 0;
		try {
			object = json.getJSONObject("error");
			code = object.getInt("code");
			if(code == 0){
				object = object.getJSONObject("data");
				System.out.println("object="+object);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			object=null;
		}
		
		return object;
	}
/*
 * 登陆后的信息保存
 * */
	public JSONObject getRequestJson(String account,JSONObject json){
		try {
			String myAccount = json.getString("account");
			String session = json.getString("session");
		} catch (JSONException e) {
			e.printStackTrace();
			requsetJsonUser=null;
		}
		
		return requsetJsonUser;
	}
}
