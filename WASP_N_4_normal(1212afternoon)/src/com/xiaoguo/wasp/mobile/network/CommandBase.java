package com.xiaoguo.wasp.mobile.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.xiaoguo.wasp.mobile.LoginActivity;
import com.xiaoguo.wasp.mobile.model.UploadItem;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;

public class CommandBase {

	private static CommandBase m_instance = null;

	// storage the task
	private HashMap<AsyncHttpResponseHandler, TaskListener> m_task = new HashMap<AsyncHttpResponseHandler, TaskListener>();

	private AsyncHttpClient m_httpClient = new AsyncHttpClient();

	private UploadQueue m_uploadQueue = null;
	private HashMap<AsyncHttpResponseHandler, UploadItem> m_tempRequest = new HashMap<AsyncHttpResponseHandler, UploadItem>();

	private static String m_account = "";
	private static String m_session = "";

	// private static String HostUrl = "222.18.162.149:8080";//赵老师
	// private static String HostUrl = "222.18.162.144:8080";
	// private static String HostUrl = "222.18.162.135:8080";// 利利
	// private static String HostUrl = "210.41.229.90:8080";
	// private static String HostUrl = "203.195.137.44:10080";
	/**
	 * 重庆服务器地址
	 */
	// private static String HostUrl = "203.195.137.44:10080";

	// private static String HostUrl = "222.18.162.132:8080"; // 赵老师地址

	// 禹慕科技的服务器地址
	// private static String HostUrl = "60.221.243.52:10080";
	//
	//private static String HostUrl = "182.254.167.232:8080";
	
	private static String HostUrl = "218.65.88.29:10080";
	
	private static JSONObject m_postData = new JSONObject();

	Context m_context;
	Context m_currContext;
	Activity m_currActivity;
	static UserSettingInfo userSettingInfo = null;

	public static void setHost(String host) {
		HostUrl = host;
		System.out.println("host=" + host);
	}

	public String getHost() {
		return CommandBase.HostUrl;
	}

	public void setCurrActivityContext(Context context, Activity activity) {
		m_currContext = context;
		m_currActivity = activity;
	}

	public void setMainActivityContext(Context context) {
		m_context = context;

		if (m_uploadQueue == null)
			m_uploadQueue = new UploadQueue(m_context);
	}

	public static CommandBase instance() {
		if (m_instance == null)
			m_instance = new CommandBase();

		return m_instance;
	}

	private CommandBase() {
		m_httpClient.addHeader("Content-Type", "application/json");
		m_httpClient.addHeader("Request-Client", "mobile/1.0.0");
	}

	public void setUserInfo(String name, String session) {
		m_account = name;
		m_session = session;
		System.out.println("setUser");
		System.out.println("account=" + m_account);
		JSONObject userData = new JSONObject();
		try {
			userData.put("account", m_account);
			userData.put("session", m_session);
			m_postData.put("user", userData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getSession() {
		return m_session;
	}

	CacheManager manager = new CacheManager();

	public void readCache(final TaskListener taskListener) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String queryStr = taskListener.readCache();
		list = manager.getCacheData(queryStr);
		taskListener.updateCacheDate(list);
		request(taskListener);
	}

	public void request(final TaskListener task) {
		RequestParams postParam = new RequestParams();
		postParam.put("req", requestData(task.requestData()));

		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				task.failure(arg1);
				super.onFailure(arg0, arg1);
				System.out.println("网络连接失败..");
				if (task.needCacheTask() && m_uploadQueue != null
						&& m_tempRequest.containsKey(this))
					m_uploadQueue.addItemToQueue(m_tempRequest.get(this));
				m_tempRequest.remove(this);
				Toast.makeText(m_currContext, "网络连接失败", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFinish() {
				task.finish();
				super.onFinish();
				System.out.println("请求结束");
			}

			@Override
			public void onSuccess(JSONObject arg1) {
				super.onSuccess(arg1);
				System.out.println("连接服务器成功！");
				System.out.println(task.requestUrl() + "结果=" + arg1);
				readData(arg1, this);
			}

			@Override
			public void onStart() {
				task.start();
				super.onStart();
				System.out.println("请求开始");
			}
		};

		if (task.needCacheTask()) {
			m_tempRequest.put(
					handler,
					new UploadItem(0, requestUrl(task.requestUrl()),
							requestData(task.requestData()),
							task.contentype() == "text" ? 0 : 1, task
									.filepath()));

		}

		m_task.put(handler, task);
		m_httpClient.post(requestUrl(task.requestUrl()), postParam, handler);

	}

	private String requestUrl(String ap) {
		return "http://" + HostUrl + "/wasp_jiangxi/webservice/ap/" + ap
				+ ".action";
	}

	private String requestData(JSONObject data) {
		JSONObject postData = new JSONObject();
		postData = m_postData;
		try {
			postData.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("postData=" + postData);
		return postData.toString();
	}

	private void readData(JSONObject data, AsyncHttpResponseHandler handler) {
		TaskListener task = m_task.get(handler);

		if (task == null) {
			m_task.remove(handler);
			m_tempRequest.remove(handler);
			return;
		}
		try {
			JSONObject object = data.getJSONObject("error");
			String code = object.getString("code");
			String errorTag = object.getString("string");
			System.out.println("code=" + code);
			System.out.println("error=" + errorTag);
			if (code.equals("0")) {
				object = data.getJSONObject("data");
				task.messageUpdated(data);
				manager.updateCacheData(data);
				m_tempRequest.remove(handler);
			} else if (code.equals("-99")) {
				Toast.makeText(m_currContext, "用户信息已过期,请重新登陆",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent();
				i.setClass(m_currActivity, LoginActivity.class);
				m_currContext.startActivity(i);
				m_currActivity.finish();
			} else {
				if (task.needCacheTask() && m_uploadQueue != null
						&& m_tempRequest.containsKey(handler)) {
					m_uploadQueue.addItemToQueue(m_tempRequest.get(handler));
				}
				m_tempRequest.remove(handler);
				Toast.makeText(m_currContext, errorTag, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			if (task.needCacheTask() && m_uploadQueue != null
					&& m_tempRequest.containsKey(handler)) {
				m_uploadQueue.addItemToQueue(m_tempRequest.get(handler));
			}
			m_tempRequest.remove(handler);

		}

		m_task.remove(handler);
	}

}
