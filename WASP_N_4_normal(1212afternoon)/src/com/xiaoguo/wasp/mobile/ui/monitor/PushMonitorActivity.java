package com.xiaoguo.wasp.mobile.ui.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

public class PushMonitorActivity extends Activity implements
		android.view.View.OnClickListener {

	private Button btnExit;

	private TextView tvTitle, tvContent, tvTime;

	private String pushCondition, title, time, content;

	private Intent pushIntent;

	private ArrayList<String> listContent;

	private CommandBase commandBase = CommandBase.instance();

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_monitor);
		inintView();
		pushIntent = getIntent();
		listContent = new ArrayList<String>();
		// pushContent = pushIntent.getStringExtra("content");
		// listContent = pushIntent.getStringArrayListExtra("content");
		pushCondition = pushIntent.getStringExtra("condition");

		String frontTwoStr = pushCondition.substring(0, 2);
		if (frontTwoStr.equals("大鹏")) {
			tvTitle.setText("大棚报警");
			String shedTime = pushIntent.getStringExtra("time");
			String shedDescription = pushIntent.getStringExtra("description");
			tvTime.setText(shedTime);
			tvContent.setText(shedDescription);

		} else {

			String[] message = pushCondition.split(" ");
			title = message[0];
			mSYS("返回回来的title是--->>", title);
			time = message[message.length - 2] + " "
					+ message[message.length - 1];
			mSYS("返回回来的时间", time);
			pd = new ProgressDialog(PushMonitorActivity.this);
			loadData();
		}

	}

	private void loadData() {
		commandBase.setCurrActivityContext(PushMonitorActivity.this,
				PushMonitorActivity.this);
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				pd.setTitle("加载中...");
				pd.show();
			}

			@Override
			public String requestUrl() {
				return "SelectPushArticle";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object1 = new JSONObject();
				try {
					object1.put("articleTitle", title);
					object1.put("publishDate", time);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
				mSYS("从服务器返回的JSONObject", msg.toString());
				tvTime.setText(time);
				try {
					tvTitle.setText("灾害预警");
					JSONObject data = msg.getJSONObject("data");
					JSONArray item = data.getJSONArray("item");
					JSONObject item1 = item.getJSONObject(0);
					content = item1.getString("article_content");
					tvContent.setText(content);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				pd.dismiss();
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
				pd.dismiss();
			}

			@Override
			public String contentype() {
				return null;
			}
		});

	}

	private void inintView() {
		btnExit = (Button) findViewById(R.id.bt_left);
		btnExit.setVisibility(View.VISIBLE);
		btnExit.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.title);

		tvContent = (TextView) findViewById(R.id.tv_content);
		tvTime = (TextView) findViewById(R.id.tv_time);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			PushMonitorActivity.this.finish();
			break;
		default:
			break;

		}

	}

	private void mSYS(String title, String message) {

		System.out.println(title + " " + message);

	}

}
