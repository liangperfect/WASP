package com.xiaoguo.wasp.mobile.widget;

import com.xiaoguo.wasp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 自定义按钮
 * */

public class My2TextButton extends LinearLayout {
	private TextView bt_tx01;
	private TextView bt_tx02;
	private ImageView img_refresh;

	public My2TextButton(Context context) {
		this(context, null);
	}

	public My2TextButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.text2button, this, true);
		bt_tx01 = (TextView) findViewById(R.id.bt_text1);
		bt_tx02 = (TextView) findViewById(R.id.bt_text2);
		img_refresh = (ImageView) findViewById(R.id.imgB_refresh);
	}

	public ImageView getRefreshImage() {

		return img_refresh;
	}

	public void setTextView1Text(String text1) {
		bt_tx01.setText(text1);
	}

	public String getTextView1Text() {
		return bt_tx01.getText().toString();
	}

	public void setTextView2Text(String text2) {
		bt_tx02.setText(text2);
	}

	public String getTextView2Text() {
		return bt_tx02.getText().toString();
	}
}
