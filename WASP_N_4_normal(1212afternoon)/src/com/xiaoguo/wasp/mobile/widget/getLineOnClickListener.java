package com.xiaoguo.wasp.mobile.widget;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.xiaoguo.wasp.mobile.model.charData;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.monitor.MoniterChartActivity;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

public class getLineOnClickListener implements OnClickListener {

	CommandBase commandBase = CommandBase.instance();

	private Context context;

	private float[] data;

	private String[] time;

	private String xTitle;

	private String yTitle;

	private String title;

	String type;

	String deviceId;

	private charData chardata;

	/**
	 * @param title
	 *            the title to set
	 */

	public getLineOnClickListener(String type1, String deviceId, Context context1,
			String xTitle1, String yTitle1, String title1) {
		this.type = type1;
		this.deviceId = deviceId;
		this.context = context1;
		this.xTitle = xTitle1;
		this.yTitle = yTitle1;
		this.title = title1;
	}

	@Override
	public void onClick(View v) {

		commandBase.readCache(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {

			}

			@Override
			public String requestUrl() {
				return "selRealDataForDate";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object1 = new JSONObject();
				try {
					object1.put("check", type);
					object1.put("deviceId", deviceId);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return object1;
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
				System.out.println("曲线要返回数据了的" + msg.toString());
				JSONObject data1 = new JSONObject();
				JSONArray list = new JSONArray();

				try {
					data1 = msg.getJSONObject("data");
					System.out.println("data---->>" + data1.toString());
					list = data1.getJSONArray("list");
					data = new float[list.length()];
					time = new String[list.length()];
					System.out.println("list---->>" + list.toString());
					for (int j = 0; j < list.length(); j++) {
						JSONObject assistObject = new JSONObject();
						assistObject = list.getJSONObject(j);
						data[j] = assistObject.getInt(type);
						JSONObject assistObject2 = new JSONObject();
						assistObject2 = assistObject
								.getJSONObject("addTime");
						System.out.println("时间是否获取得到====>>"
								+ assistObject2.getLong("time"));
						time[j] = TimeUtil.getTimeUtilInstance()
								.TimeStamp2Date(
										assistObject2.getLong("time") + "");

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// MultipleChart chart = new MultipleChart(context, data, time,
				// xTitle, yTitle, title);
				chardata = new charData(data, time, xTitle, yTitle, title);
				Intent intent1 = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", chardata);
				intent1.setClass(context, MoniterChartActivity.class);
				// Intent charIntent = chart.execute();
				// intent1.putExtras(charIntent);
				intent1.putExtras(bundle);
				context.startActivity(intent1);
			}

			@Override
			public void finish() {

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
