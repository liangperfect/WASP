package com.xiaoguo.wasp.mobile.xmpphelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class BaseActivity extends Activity {

	protected Context MContext = null;
	private ProgressDialog pg = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MContext = this;
		pg = new ProgressDialog(MContext);
	}
	
	protected ProgressDialog getProgressDialog() {
		return pg;
	}

}
