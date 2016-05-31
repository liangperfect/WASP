package com.xiaoguo.wasp.mobile.network;


import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaoguo.wasp.mobile.utils.JsonUtils;
import com.xiaoguo.wasp.mobile.utils.MyAsyncHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public abstract class BasicActivity extends Activity{
	protected Context MContext = null;
	private ProgressDialog dialog = null;
	JSONObject object = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MContext = this;
		dialog = new ProgressDialog(MContext);
	}
	
	protected ProgressDialog getProgressDialog() {
		return dialog;
	}
	public JSONObject doLogin(String username,String password){
		JSONObject tempJsonObject = JsonUtils.getLoginJson(username, password);
		RequestParams params2 = new RequestParams();
		params2.put("req", tempJsonObject.toString());
		JSONObject object3 = getNetJSONObject("userlogin.action", params2);
		System.out.println("object3="+object3);
		return object3;
	}
	
	
	public JSONObject doRegister(String username,String password,Long type){
		JSONObject tempJsonObject = JsonUtils.getLoginJson(username, password);
		RequestParams params2 = new RequestParams();
		params2.put("req", tempJsonObject.toString());
		JSONObject object3 = getNetJSONObject("userregister.action", params2);
		System.out.println("object3="+object3);
		return object3;
	}
	
//	protected abstract void doLoginSuccess(String username, String password);
	
	public JSONObject getNetJSONObject(String url,RequestParams params){
		object = new JSONObject();
		
		MyAsyncHttpClient.post(url, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, JSONObject arg1) {
				super.onSuccess(arg0, arg1);
				object = arg1;
				System.out.println("结果"+arg1.toString());
			}
			@Override
			public void onFailure(Throwable arg0, JSONObject arg1) {
				super.onFailure(arg0, arg1);
				System.out.println("失败了");
			}
			@Override
			public void onFinish() {
				super.onFinish();
				System.out.println("结束了");
				dialog.dismiss();
			}
			@Override
			public void onStart() {
				super.onStart();
				System.out.println("开始了");
				dialog.setMessage("正在登陆，请稍后...");
				dialog.show();
			}
		});
		return object;
	}
}
