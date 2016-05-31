package com.xiaoguo.wasp.mobile.ui.monitor;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.R.layout;
import com.xiaoguo.wasp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class MessageMointerActivity extends Activity {

	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_mointer);
		textView = (TextView) findViewById(R.id.tv_show);
		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		textView.setText("返回的消息数据是===>>>" + content);
	}

}
