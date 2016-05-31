package com.xiaoguo.wasp.mobile.ui.warning;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.R.layout;
import com.xiaoguo.wasp.R.menu;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DisterWarmingDetailedActivity extends Activity implements
		OnClickListener {

	private TextView title, disaterTitle, disaterTime, disaterPublisherName,
			disaterContent;
	private Button disterExit;
	private Intent intent;
	private int alarmID;
	private CommandBase commandBase;
	private String disaterTitleStr, disaterTimeStr, disaterPublisherNameStr,
			disaterContentStr;
	private ProgressDialog mProgressDialog;
	private TimeUtil mTimeUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dister_warming_detailed);
		overridePendingTransition(R.anim.from_right_into, R.anim.to_left_out);
		initView();
		initData();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.title_product_detatil);
		disterExit = (Button) findViewById(R.id.bt_left);
		disterExit.setVisibility(View.VISIBLE);
		disterExit.setOnClickListener(this);
		disaterTitle = (TextView) findViewById(R.id.disater_title);
		disaterTime = (TextView) findViewById(R.id.dister_time);
		disaterPublisherName = (TextView) findViewById(R.id.disater_detail_publisher_name);
		disaterContent = (TextView) findViewById(R.id.defense_guidelines);
		mProgressDialog = new ProgressDialog(DisterWarmingDetailedActivity.this);
		mProgressDialog.setTitle("获取数据中...");
		mTimeUtil = TimeUtil.getTimeUtilInstance();
	}

	private void initData() {

		intent = getIntent();
		alarmID = intent.getIntExtra("alarmID", 0);
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(DisterWarmingDetailedActivity.this,
				DisterWarmingDetailedActivity.this);
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				mProgressDialog.show();
			}

			@Override
			public String requestUrl() {
				return "productGuideId";
			}

			@Override
			public JSONObject requestData() {
				JSONObject data = new JSONObject();
				try {
					data.put("alarm_id", alarmID);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return data;
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

				System.out.println("返回的数据是--->>" + msg.toString());
				try {
					JSONObject data = msg.getJSONObject("data");
					JSONObject alarm = data.getJSONObject("alarm");
					disaterContentStr = alarm.getString("alarm_content");
					disaterTitleStr = alarm.getString("alarm_title");
					disaterPublisherNameStr = alarm
							.getString("alarm_publisher_name");
					JSONObject alarmDate = alarm
							.getJSONObject("alarm_publish_date");
					long time = alarmDate.getLong("time");
					disaterTimeStr = mTimeUtil.getDateToString(time);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				setContent(disaterTitleStr, disaterTimeStr,
						disaterPublisherNameStr, disaterContentStr);
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
				mProgressDialog.dismiss();
			}

			@Override
			public String contentype() {
				return null;
			}
		});

	}

	@SuppressLint("ResourceAsColor")
	private void setContent(String title, String time, String name,
			String content) {

		disaterTitle.setText(title);
		disaterPublisherName.setText(name);
		//
		// if (content.equals("")) {
		// disaterContent.setText("没有内容");
		// disaterContent.setTextColor(R.color.gray_tip);
		// } else {

		// }

		System.out.println("推送content的内容--->>" + content);
		if (content.equals("")) {
			disaterContent.setText("");
		} else {
			disaterContent.setText(Html.fromHtml(content));
		}

		disaterTime.setText(time);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dister_warming_detailed, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			DisterWarmingDetailedActivity.this.finish();
			overridePendingTransition(0, R.anim.to_left_out);
			break;
		}

	}

}
