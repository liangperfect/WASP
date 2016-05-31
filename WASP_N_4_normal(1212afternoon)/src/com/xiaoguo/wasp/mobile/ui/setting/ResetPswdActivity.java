package com.xiaoguo.wasp.mobile.ui.setting;


import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.SHA1Util;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/*
 * 重设密码模块
 * 基本框图已经搭建好，与服务器的交互还未做
 * */
public class ResetPswdActivity extends Activity implements OnClickListener{
	//title
	private TextView titleTextView;
	//right button
	private Button btRight;
		
	//screen
	private EditText oldPswdView;
	private EditText newPswdView;
	private EditText verifyPswdView;
	
	// Values
	private String oldPswd;
	private String newPswd;
	private String verifyPswd;
	
	private UserSettingInfo userInfo=null;
	private String userPass=null;
	private String userID=null;
	
	
	private ProgressDialog dialog;
	
	 MyBroadcastReceiver receiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_pswd);
		
		dialog = new ProgressDialog(this);
		WASPApplication.getInstance().addActivity(this);
		
		userInfo = new UserSettingInfo(ResetPswdActivity.this);
		userPass = userInfo.getPassword();
		userID = userInfo.getAccount();
		setupView();
	}

	private void setupView() {
		titleTextView = (TextView)this.findViewById(R.id.title);
		titleTextView.setText("修改密码");
			
		Button btnBack = (Button)this.findViewById(R.id.bt_left);
		btnBack.setOnClickListener(this);
		btnBack.setBackgroundResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
			
		btRight = (Button)this.findViewById(R.id.bt_right);
		btRight.setVisibility(View.VISIBLE);
		btRight.setBackgroundResource(R.drawable.btn_ok);
		btRight.setOnClickListener(this);
			
		oldPswdView = (EditText)findViewById(R.id.old_pswd);
		newPswdView = (EditText)findViewById(R.id.new_pswd);
		verifyPswdView = (EditText)findViewById(R.id.de_new_pswd);
			
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;			
		case R.id.bt_right:{
			attemptSave();
			break;
		}
		default:
			break;
		}
	}
	private void attemptSave() {
	// Reset errors.
		oldPswdView.setError(null);
		newPswdView.setError(null);
		verifyPswdView.setError(null);

		// Store values at the time of the next attempt.
		oldPswd = oldPswdView.getText().toString();
		newPswd = newPswdView.getText().toString();
		verifyPswd = verifyPswdView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		//check if new password is equal to verify password
		if (TextUtils.isEmpty(verifyPswd)) {
			verifyPswdView.setError(getString(R.string.error_field_required));
			focusView = verifyPswdView;
			cancel = true;
		} else if (!verifyPswd.equals(newPswd)) {
			verifyPswdView.setError(getString(R.string.error_invalid_password_again));
			focusView = verifyPswdView;
			cancel = true;
		}
		// Check for a valid new password.
		if (TextUtils.isEmpty(newPswd)) {
			newPswdView.setError(getString(R.string.error_field_required));
			focusView = newPswdView;
			cancel = true;
		} else if (newPswd.length()<4) {
			newPswdView.setError(getString(R.string.error_invalid_password));
			focusView = newPswdView;
			cancel = true;
		}
		// Check for a valid oldPassword.
		if (TextUtils.isEmpty(oldPswd)) {
			oldPswdView.setError(getString(R.string.error_field_required));
			focusView = oldPswdView;
			cancel = true;
		} else if (oldPswd.length() < 3) {
			oldPswdView.setError(getString(R.string.error_invalid_password));
			focusView = oldPswdView;
			cancel = true;
		}
				
		if (cancel) {
			focusView.requestFocus();
			} else {
				System.out.println("userPass="+userPass);
				System.out.println("oldPass="+oldPswd);
					if(newPswd.equals(verifyPswd)){
						Activity activity = new ResetPswdActivity();
						CommandBase.instance().setCurrActivityContext(ResetPswdActivity.this,activity);
						CommandBase.instance().request(new TaskListener() {
							@Override
							public void start() {
								dialog.setMessage("正在提交数据,请稍后..");
								dialog.show();
							}
							@Override
							public String requestUrl() {
								return "updatepwd";
							}
							@Override
							public JSONObject requestData() {
								JSONObject object = new JSONObject();
								try {
									object.put("account", userInfo.getAccount());
									object.put("oldpassword",SHA1Util.getSHA1EncString(oldPswd));
									object.put("newpassword", SHA1Util.getSHA1EncString(newPswd));
								} catch (JSONException e) {
									e.printStackTrace();
								}
								return object;
							}
							@Override
							public void messageUpdated(JSONObject msg) {
										Toast.makeText(ResetPswdActivity.this, "密码修改成功！", Toast.LENGTH_SHORT).show();
										ResetPswdActivity.this.finish();
							}
							@Override
							public void finish() {
								dialog.dismiss();
							}
							@Override
							public void failure(String str) {
								Toast.makeText(ResetPswdActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
							}
							@Override
							public String contentype() {
								return null;
							}
							@Override
							public String filepath() {
								return null;
							}
							@Override
							public boolean needCacheTask() {
								return false;
							}
							@Override
							public String readCache() {
								return null;
							}
							@Override
							public void updateCacheDate(
									List<HashMap<String, Object>> cacheData) {
								
							}
						});
					}else{
						Toast.makeText(ResetPswdActivity.this, "两次输入的新密码不一致！", Toast.LENGTH_SHORT).show();
					}
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 0, 0, "主页");
		menu.add(1, 1, 0, "设置");
		menu.add(1, 2, 0, "返回");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			break;
		case 1:
			Intent i1 = new Intent(ResetPswdActivity.this,SettingActivity.class);
			startActivity(i1);
			break;
		case 2:
			ResetPswdActivity.this.finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(ResetPswdActivity.this);
//		IntentFilter filter = new IntentFilter();
//		
//		filter.addAction(Constant.ROSTER_ADDED);
//		filter.addAction(Constant.ROSTER_DELETED);
//		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
//		filter.addAction(Constant.ROSTER_UPDATED);
//		// 好友请求
//		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
//		filter.addAction(Constant.NEW_MESSAGE_ACTION);
//		registerReceiver(receiver, filter);
//		super.onResume();
//	}	
}

