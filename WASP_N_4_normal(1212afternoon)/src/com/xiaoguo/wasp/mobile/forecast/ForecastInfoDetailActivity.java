package com.xiaoguo.wasp.mobile.forecast;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

public class ForecastInfoDetailActivity extends Activity implements
		OnClickListener {
	private TextView title, contentTitleTv, contentTimeTv, contentTv,
			contentPublisherNameTv;
	private Intent receiverIntent;
	private CommandBase commandBase;
	private ProgressDialog mProgressDialog;
	private int forecastId;
	private String contentStr, titleStr, publisherNameStr, contentTitleStr,
			contentTimeStr;
	private Button exitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast_info_detail);
		overridePendingTransition(R.anim.from_right_into, R.anim.to_left_out);
		initView();
		initData();

		commandBase = CommandBase.instance();
		mProgressDialog = new ProgressDialog(ForecastInfoDetailActivity.this);
		mProgressDialog.setTitle("获取数据中...");
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
				return "selectForecastById";
			}

			@Override
			public JSONObject requestData() {

				JSONObject data = new JSONObject();
				try {
					data.put("forecast_id", forecastId);

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

				try {
					JSONObject data = msg.getJSONObject("data");
					JSONObject forecastObject = data.getJSONObject("forecast");
					contentStr = forecastObject.getString("forecast_content");
					publisherNameStr = forecastObject
							.getString("forecast_update_name");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				// contentTitleTv, timeTv, contentTv
				contentTitleTv.setText(contentTitleStr);
				contentTimeTv.setText(contentTimeStr);
				// contentTv.setText("    " + contentStr);
				contentTv.setText(Html.fromHtml(contentStr));
				contentPublisherNameTv.setText(publisherNameStr);
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

	private void initData() {
		receiverIntent = getIntent();
		Bundle contentBundle = this.getIntent().getExtras();
		forecastId = contentBundle.getInt("forcastid", 0);
		titleStr = contentBundle.getString("title");
		title.setText(titleStr);
		contentTitleStr = contentBundle.getString("contentTitleStr");
		contentTimeStr = contentBundle.getString("contentTimeStr");

	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		title.setText(titleStr);
		exitBtn = (Button) findViewById(R.id.bt_left);
		exitBtn.setVisibility(View.VISIBLE);
		exitBtn.setOnClickListener(this);

		contentTitleTv = (TextView) findViewById(R.id.detail_title);
		contentTimeTv = (TextView) findViewById(R.id.detail_time);
		contentPublisherNameTv = (TextView) findViewById(R.id.publisher_name);
		contentTv = (TextView) findViewById(R.id.detail_content);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.forecast_info_detail, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_left:
			ForecastInfoDetailActivity.this.finish();
			overridePendingTransition(0, R.anim.to_left_out);
			break;
		}

	}

}
