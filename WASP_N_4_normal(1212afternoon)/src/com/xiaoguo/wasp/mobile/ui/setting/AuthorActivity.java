package com.xiaoguo.wasp.mobile.ui.setting;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AuthorActivity extends Activity implements OnClickListener {
	private Button backBtView;
	private TextView titleView, tvVersion, tvTeam;
	MyBroadcastReceiver receiver = null;
	private String currentVerName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.author);
		WASPApplication.getInstance().addActivity(this);
		init();
	}

	private void init() {
		backBtView = (Button) findViewById(R.id.bt_left);
		backBtView.setVisibility(View.VISIBLE);
		backBtView.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("联系我们");
		tvVersion = (TextView) findViewById(R.id.tv_version);
		currentVerName = getVerName(getApplicationContext());
		tvVersion.setText(currentVerName);
		tvTeam = (TextView) findViewById(R.id.team);
		tvTeam.setOnClickListener(this);
	}

	/**
	 * 获得版本名称
	 */
	public String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.xiaoguo.wasp", 0).versionName;
		} catch (NameNotFoundException e) {
			System.out.println("版本名称获取异常" + e.getMessage());
		}
		return verName;
	}

	// @Override
	// protected void onPause() {
	// unregisterReceiver(receiver);
	// super.onPause();
	// }
	//
	// @Override
	// protected void onResume() {
	// receiver = new MyBroadcastReceiver(AuthorActivity.this);
	// IntentFilter filter = new IntentFilter();
	//
	// filter.addAction(Constant.ROSTER_ADDED);
	// filter.addAction(Constant.ROSTER_DELETED);
	// filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
	// filter.addAction(Constant.ROSTER_UPDATED);
	// // 好友请求
	// filter.addAction(Constant.ROSTER_SUBSCRIPTION);
	// filter.addAction(Constant.NEW_MESSAGE_ACTION);
	// registerReceiver(receiver, filter);
	// super.onResume();
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.team:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String url = "http://www.yumutech.cn";
			intent.setData(Uri.parse(url));
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
