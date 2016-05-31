package com.xiaoguo.wasp.mobile.ui.monitor;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.model.charData;
import com.xiaoguo.wasp.mobile.widget.MultipleChart;

public class MoniterChartActivity extends TabActivity implements
		OnClickListener {
	private TextView tvTitle;
	private Button btnExit;
	private Intent charIntent;
	private charData chardata;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moniter_chart);
		Intent intent1 = getIntent();
		Bundle bundle = intent1.getExtras();
		chardata = bundle.getParcelable("data");
		float[] newCharData = dataReversal(chardata.getData());
		String[] newCharTime = timeRversal(chardata.getTime());
		MultipleChart chart = new MultipleChart(MoniterChartActivity.this,
		// chardata.getData(), chardata.getTime(),
				newCharData, newCharTime, chardata.getxTitle(),
				chardata.getyTitle(), chardata.getTitle());
		type = chardata.getyTitle();
		charIntent = chart.execute();
		initView();
	}

	private float[] dataReversal(float[] data) {
		System.out.println("");
		float[] data1 = data;

		int len = data.length;
		for (int i = 0; i < len / 2; i++) {
			float temp = data1[i];
			data1[i] = data1[len - i - 1];
			data1[len - i - 1] = temp;
		}

		return data1;

	}

	private String[] timeRversal(String[] time) {
		String[] time1 = time;
		int len = time.length;
		System.out.println("方法中的第二个数据--->>" + time[1]);
		for (int i = 0; i < len / 2; i++) {
			String temp = time1[i];
			time1[i] = time1[len - i - 1];
			time1[len - i - 1] = temp;
		}
		System.out.println(" 方法中翻转后的第二个数据--->>" + time[1]);
		return time1;

	}

	private void initView() {

		tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setText(type + "曲线");
		tvTitle.setVisibility(View.VISIBLE);
		btnExit = (Button) findViewById(R.id.bt_left);
		btnExit.setVisibility(View.VISIBLE);
		btnExit.setOnClickListener(this);
		TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("曲线").setIndicator("曲线")
				.setContent(charIntent));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			MoniterChartActivity.this.finish();
			break;
		default:
			break;

		}
	}

}
