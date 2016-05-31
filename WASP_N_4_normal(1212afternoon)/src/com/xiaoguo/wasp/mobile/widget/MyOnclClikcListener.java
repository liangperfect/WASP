package com.xiaoguo.wasp.mobile.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.baidu.android.pushservice.PushManager;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.widget.MyCanceAttentionBtn;

public class MyOnclClikcListener implements OnClickListener {

	private static MyOnclClikcListener instance = null;

	private UserSettingInfo userset;

	private Context context;
	private CommandBase commandbase = CommandBase.instance();
	private MyCanceAttentionBtn btn;
	private ProgressDialog progressdialog;
	private String addUrl;
	private JSONObject addObject;
	private String cancelUrl;
	private JSONObject cancelObject;
	private List<HashMap<String, Object>> list;
	private MyAdapter adapter;
	private ListView listView;

	/**
	 * @return the userset
	 */
	public UserSettingInfo getUserset() {
		return userset;
	}

	/**
	 * @param userset
	 *            the userset to set
	 */
	public void setUserset(UserSettingInfo userset) {
		this.userset = userset;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	public MyOnclClikcListener() {
	}

	public static MyOnclClikcListener instance() {
		if (instance == null) {

			instance = new MyOnclClikcListener();
		}
		return instance;
	}

	@Override
	public void onClick(View v) {
		btn = (MyCanceAttentionBtn) v;
		String text = btn.getText().toString().trim();
		progressdialog = new ProgressDialog(context);
		btn.getUserName();
		addUrl = btn.getAddUrl();
		addObject = btn.getAddObject();
		cancelUrl = btn.getCancelUrl();
		cancelObject = btn.getCancelObject();
		list = btn.getList();
		adapter = btn.getAdapter();
		listView = btn.getListView();
		if (text.equals("取消关注")) {

			commandbase.request(new TaskListener() {

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}

				@Override
				public void start() {
					progressdialog.setMessage("取消中");
					progressdialog.show();
				}

				@Override
				public String requestUrl() {
					return cancelUrl;
				}

				@Override
				public JSONObject requestData() {
					return cancelObject;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					btn.setText("添加关注");
					for (int i = 0; i < list.size(); i++) {
						int id = Integer.parseInt(list.get(i).get("id")
								.toString());
						String tagName = list.get(i).get("name").toString();
						List<String> tagBaiDu = new ArrayList<String>();
						String xianName = userset.getUserXianName() + "_";
						tagBaiDu.add(xianName + tagName);

						int id2;
						try {
							id2 = cancelObject.getInt("tag_idList");
							if (id == id2) {
								System.out.println(xianName + tagName);
								PushManager.delTags(context, tagBaiDu);
								list.remove(i);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// adapter.notifyDataSetChanged();
					adapter = new MyAdapter(context, list, userset, adapter,
							listView);
					listView.setAdapter(adapter);
				}

				@Override
				public void finish() {
					progressdialog.dismiss();
				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {
				}

				@Override
				public String contentype() {
					return null;
				}
			});

		}
		if (text.equals("添加关注")) {
			commandbase.request(new TaskListener() {

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}

				@Override
				public void start() {
					progressdialog.setMessage("添加关注中");

					progressdialog.show();
				}

				@Override
				public String requestUrl() {
					return addUrl;
				}

				@Override
				public JSONObject requestData() {
					return addObject;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					btn.setText("取消关注");
				}

				@Override
				public void finish() {
					progressdialog.dismiss();
				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {

				}

				@Override
				public String contentype() {
					return null;
				}
			});

		}

	}

}
