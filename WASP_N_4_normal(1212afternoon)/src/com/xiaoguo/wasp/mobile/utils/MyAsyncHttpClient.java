package com.xiaoguo.wasp.mobile.utils;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyAsyncHttpClient {
//public static final String URL = "http://203.195.137.44:10080/wasp/webservice/ap/";
	//public static final String URL = "http://222.18.162.150:8080/wasp/webservice/ap/";
public static final String URL = "http://218.65.88.29:10080/wasp_linfen/webservice/ap/";
	
	public static final String URL2="http://www.bjtime.cn";
	
	public static final AsyncHttpClient client = new AsyncHttpClient();
	//获得绝对URL
	private static String getAbsoluteUrl(String url2) {
		String url = URL+url2;
		return url;
	}
	//用get方法获得数据
	public static void get(String url,RequestParams params,AsyncHttpResponseHandler handler){
		//client.setTimeout(3000);
		client.get(getAbsoluteUrl(url), params, handler);
	}
	//用post方法获得数据
	public static void post(String url,RequestParams params,AsyncHttpResponseHandler handler){
		client.setTimeout(3000);
		client.addHeader("Content-Type","application/json");
		client.post(getAbsoluteUrl(url), params, handler);
	}
	//获取当前时间
	public static void get(AsyncHttpResponseHandler handler){
		client.get(URL2, handler);
	}
}
