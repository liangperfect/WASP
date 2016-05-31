package com.xiaoguo.wasp.mobile.ui.warning;

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
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

public class ProductGuideDetailedActivity extends Activity implements
		OnClickListener {

	private TextView title;
	private Button disterExit;
	private Intent intent;
	private TextView productGuideTitle, productGuideTime, productGuideContent,
			guidePublisherName;
	private CommandBase commandBase;
	private int productGuideId;
	private ProgressDialog mProgressDialog;
	private String guideTitleStr, guideTimeStr, guideContentStr,
			guidePublisherNameStr;
	private TimeUtil timeUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_guide_detailed);
		overridePendingTransition(R.anim.from_right_into, R.anim.to_left_out);
		initView();
		initData();
		addListenter();

	}

	private void initView() {

		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.guide_detail);
		disterExit = (Button) findViewById(R.id.bt_left);
		disterExit.setVisibility(View.VISIBLE);
		disterExit.setOnClickListener(this);
		productGuideTitle = (TextView) findViewById(R.id.product_guide_title);
		productGuideTime = (TextView) findViewById(R.id.product_guide_time);
		productGuideContent = (TextView) findViewById(R.id.product_guide_content);
		guidePublisherName = (TextView) findViewById(R.id.guide_detail_publisher_name);
		mProgressDialog = new ProgressDialog(ProductGuideDetailedActivity.this);
		mProgressDialog.setTitle("正在获取数据...");
	}

	private void initData() {
		intent = getIntent();
		productGuideId = intent.getIntExtra("productGuideId", 0);
		commandBase = CommandBase.instance();
		timeUtil = TimeUtil.getTimeUtilInstance();
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
				return "selectProduceGuideById";
			}

			@Override
			public JSONObject requestData() {
				JSONObject data = new JSONObject();
				try {
					data.put("produceguide_id", productGuideId);
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
				JSONObject data = new JSONObject();
				try {
					data = msg.getJSONObject("data");
					JSONObject produceguide = new JSONObject();
					produceguide = data.getJSONObject("produceguide");
					guideContentStr = produceguide
							.getString("produceguide_content");

					guideTitleStr = produceguide
							.getString("produceguide_title");
					guidePublisherNameStr = produceguide
							.getString("produceguide_update_name");
					JSONObject timeObject = produceguide
							.getJSONObject("produceguide_update_date");

					guideTimeStr = timeUtil.getDateToString(timeObject
							.getLong("time"));
					System.out.println("guideContentStr-->>" + guideContentStr
							+ "     guideTitleStr-->>" + guideTitleStr
							+ "   guideTimeStr-->>" + guideTimeStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				addDataToView(guideTitleStr, guideTimeStr, guideContentStr,
						guidePublisherNameStr);
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

	private void addListenter() {

	}

	private void addDataToView(String title, String time, String content,
			String name) {

		try {
			productGuideTitle.setText(title);
			productGuideTime.setText(time);
			productGuideContent.setText(Html.fromHtml(content));
			guidePublisherName.setText(name);

		} catch (Exception e) {
			productGuideTitle.setText("");
			productGuideTime.setText("");
			productGuideContent.setText("");
			guidePublisherName.setText("");

		}

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
			ProductGuideDetailedActivity.this.finish();
			overridePendingTransition(0, R.anim.to_left_out);
			break;
		}

	}

}
