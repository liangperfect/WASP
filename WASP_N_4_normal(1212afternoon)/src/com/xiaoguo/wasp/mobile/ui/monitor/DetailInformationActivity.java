package com.xiaoguo.wasp.mobile.ui.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.widget.WiperSwitch;
import com.xiaoguo.wasp.mobile.widget.WiperSwitch.OnChangedListener;

public class DetailInformationActivity extends Activity implements
		OnClickListener, OnChangedListener {

	private Button btnExit;
	private WiperSwitch rollerSwitch;
	private WiperSwitch sluiceSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_information);
		
		WASPApplication.getInstance().addActivity(this);

		btnExit = (Button) findViewById(R.id.bt_left);
		btnExit.setVisibility(View.VISIBLE);
		btnExit.setOnClickListener(this);

		rollerSwitch = (WiperSwitch) findViewById(R.id.rollerSwitch);
		rollerSwitch.setChecked(false);
		rollerSwitch.setOnChangedListener(this);

		sluiceSwitch = (WiperSwitch) findViewById(R.id.sluiceSwitch);
		sluiceSwitch.setChecked(false);
		sluiceSwitch.setOnChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			DetailInformationActivity.this.finish();
			break;
		default:
			break;

		}

	}

	@Override
	public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
		
	}

}
