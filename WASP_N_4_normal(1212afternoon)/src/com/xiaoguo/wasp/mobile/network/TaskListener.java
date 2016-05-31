package com.xiaoguo.wasp.mobile.network;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

public interface TaskListener {

	public abstract void start();

	public abstract String contentype(); // text, file

	public abstract String filepath();

	public abstract String requestUrl();

	public abstract JSONObject requestData();

	public abstract void messageUpdated(JSONObject msg);

	public abstract void failure(String str);

	public abstract void finish();

	public abstract boolean needCacheTask();

	public abstract String readCache();

	public abstract void updateCacheDate(List<HashMap<String, Object>> cacheData);

}
