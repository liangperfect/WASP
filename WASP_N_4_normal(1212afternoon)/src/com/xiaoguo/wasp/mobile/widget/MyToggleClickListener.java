package com.xiaoguo.wasp.mobile.widget;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

public class MyToggleClickListener implements OnClickListener {
	private static MyToggleClickListener instance = null;
	private CommandBase commandbase = CommandBase.instance();
	private Context context;
	private ProgressDialog progressdialog;

	private int switchKey;
	private String switchState;
	private String deviceId;
	private String switchName;
	private UserSettingInfo userSettingInfo;
	private HashMap<String, Object> map;
	private int controlLocation;

	View dialogLayout1;
	EditText close_input;
	View dialogLayout2;
	EditText time_input;
	EditText password_input;
	String userAccount;
	String password;
	String btText;
	String currentState = "";
	int count = 10;
	MyToggleButton btn;
	MyToggleButton btn1;
	MyToggleButton btn2;
	MyToggleButton btn3;
	Timer timer;
//	MyTimeTask mTimeTask;
	String id;
	String contrlCmd;

	public MyToggleClickListener() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	public static MyToggleClickListener instance() {
		if (instance == null) {
			instance = new MyToggleClickListener();
		}
		return instance;
	}

	public void setRequestInfo(Context context/*, int switchKey,
			String switchState, String deviceId, String switchName,
			UserSettingInfo userSettingInfo,
			Button btn1, Button btn2, Button btn3*/) {
		this.context = context;
		/*this.switchKey = switchKey;
		this.switchState = switchState;
		this.deviceId = deviceId;
		this.switchName = switchName;
		this.userSettingInfo = userSettingInfo;*/
		/*this.btn1 = btn1;
		this.btn2 = btn2;
		this.btn3 = btn3;*/
//		timer = new Timer(true);
	}

	/*Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			count = msg.what;
			count--;
			if (count <= 0) {
				// timer.purge();
				// timer.cancel();
				timer.purge();
				mTimeTask.cancel();
				System.out.println("计时器"+count);
				commandbase.request(new TaskListener() {
					
					@Override
					public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void start() {
//						System.out.println("开始了");
//						progressdialog.setMessage("执行中..");
//						progressdialog.show();
					}
					
					@Override
					public String requestUrl() {
						// TODO Auto-generated method stub
						return "checkStatus";
					}
					
					@Override
					public JSONObject requestData() {
						JSONObject object = new JSONObject();
						try {
							object.put("id", id);
						} catch (JSONException e) {
							object = null;
							e.printStackTrace();
						}
						return object;
					}
					
					@Override
					public String readCache() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public boolean needCacheTask() {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public void messageUpdated(JSONObject msg) {
						JSONObject object = new JSONObject();
							try {
								object = msg
										.getJSONObject("error");
								object = object
										.getJSONObject("control");
								String nowState = object.getString("state");
								if("01".equals(contrlCmd)){
									if("正在执行".equals(nowState)){
										switchState = "正在关闭";
									}else if("成功".equals(nowState)){
										switchState = "已关闭";
									}else{
										switchState = "已打开";
									}
									btn.setSwitchState(switchState);
									btn.setText(switchState);
								}
								if("10".equals(contrlCmd)){
									if("正在执行".equals(nowState)){
										switchState = "正在打开";
									}else if("成功".equals(nowState)){
										switchState = "已打开";
									}else{
										switchState = "已关闭";
									}
									String states[] = (String[]) map.get("states");
									states[controlLocation] = switchState;
									map.remove("states");
									map.put("states", states);
									btn.setSwitchState(switchState);
									btn.setText(switchState);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
					
					@Override
					public void finish() {
//						progressdialog.dismiss();
						System.out.println("结束了");
					}
					
					@Override
					public String filepath() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public void failure(String str) {
						System.out.println("失败了");
					}
					
					@Override
					public String contentype() {
						// TODO Auto-generated method stub
						return null;
					}
				});
//				btn.setEnabled(true);
				//更新button的值
//				MyToggleClickListener myToggleClickListener = new MyToggleClickListener();
				btn.setOnClickListener(instance);
				btn1.setOnClickListener(instance);
				btn2.setOnClickListener(instance);
				btn3.setOnClickListener(instance);
			} else {
				// Toast.makeText(MainActivity.this, "还剩"+count+"秒",
				// Toast.LENGTH_SHORT).show();
				btn.setEnabled(false);
				btn1.setEnabled(false);
				btn2.setEnabled(false);
				btn3.setEnabled(false);
				System.out.println("计时器"+count);
				MyCliclListener myCliclListener = new MyCliclListener(count);
				btn.setOnClickListener(myCliclListener);
				btn1.setOnClickListener(myCliclListener);
				btn2.setOnClickListener(myCliclListener);
				btn3.setOnClickListener(myCliclListener);
				// System.out.println("还剩" + count + "秒");
			}

			super.handleMessage(msg);
		}
	};
	private class MyCliclListener implements OnClickListener{
		private int i;
		
		public MyCliclListener(int i) {
			super();
			this.i = i;
		}

		@SuppressLint("ShowToast")
		@Override
		public void onClick(View v) {
			Toast t = Toast.makeText(context, i+"秒后才能点击，请稍后", Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
			t.show();
		}
		
	}
	private class MyTimeTask extends TimerTask {
		int i=30;
		@Override
		public void run() {
			Message message = mHandler.obtainMessage();
			message.what = i--;
			mHandler.sendMessage(message);

		}

	}*/

	@Override
	public void onClick(View arg0) {
		btn = (MyToggleButton) arg0;
		switchKey = btn.getSwitchKey();
		switchState = btn.getSwitchState();
		deviceId = btn.getDeviceId();
		switchName = btn.getSwitchName();
		userSettingInfo = btn.getUserSettingInfo();
		map = btn.getMap();
		controlLocation = btn.getControlLocation();
		btn1 = btn.getBtn1();
		btn2 = btn.getBtn2();
		btn3 = btn.getBtn3();
		timer = btn.getTimer();
//		btText = btn.getText().toString();
		btText = switchState;
		password = userSettingInfo.getPassword();
		userAccount = userSettingInfo.getAccount();
		progressdialog = new ProgressDialog(context);
		System.out.println("onClick:"+switchKey);
		if (btText.equals("已打开")) {
			dialogLayout1 = LayoutInflater.from(context).inflate(
					R.layout.close, null);
			close_input = (EditText) dialogLayout1
					.findViewById(R.id.close_input);
			new AlertDialog.Builder(context)
					.setTitle("提示:")
					// .setMessage("是否确定关闭"+switchName).setPositiveButton("确定",
					// new DialogInterface.OnClickListener() {
					.setView(dialogLayout1)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									close_input.setError(null);
									boolean cancel = false;
									View focusView = null;
									if (TextUtils.isEmpty(close_input.getText()
											.toString())) {
										close_input.setError("此项不能为空");
										focusView = close_input;
										cancel = true;
									} else if (!password.equals(close_input
											.getText().toString())) {
										close_input.setError("密码错误");
										focusView = close_input;
										cancel = true;
									}
									if (cancel) {
										focusView.requestFocus();
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
											dialog.dismiss();
										} catch (Exception e) {

										}
									} else {
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
											dialog.dismiss();
										} catch (Exception e) {

										}
										commandbase.request(new TaskListener() {
											@Override
											public void start() {

												progressdialog
														.setMessage("关闭中...");
												progressdialog.show();
											}

											@Override
											public String requestUrl() {
												return "updateStatus";
											}

											@Override
											public JSONObject requestData() {
												JSONObject object = new JSONObject();
												try {
													object.put("swithKey",
															switchKey);
													object.put("cmdValue", "01");
													object.put("relayTime", 20);
													object.put("deviceId",
															deviceId);
													object.put("account",
															userAccount);
												} catch (JSONException e) {
													e.printStackTrace();
												}
												return object;
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
											public void messageUpdated(
													JSONObject msg) {
												JSONObject object = new JSONObject();
												try {
													object = msg
															.getJSONObject("error");
													object = object
															.getJSONObject("control");
													String relayState = object
															.getString("state");
													id =  object.getString("id");
													System.out
															.println("relayState="
																	+ relayState);
													if ("正在执行"
															.equals(relayState)) {
														currentState = "正在关闭";
														// iChangeState.state1(gridList,currentState);
														System.out.println("1");
														btn.setBackgroundResource(R.drawable.toggle_bg_off);
													} else if ("等待执行"
															.equals(relayState)) {
														currentState = "等待关闭";
														// iChangeState.state1(gridList,currentState);
														System.out.println("2");
														btn.setBackgroundResource(R.drawable.toggle_bg_off);
													} else if ("成功"
															.equals(currentState)) {
														currentState = "已关闭";
														// iChangeState.state1(gridList,currentState);
														System.out.println("3");
														btn.setBackgroundResource(R.drawable.toggle_bg_off);
													} else {
														currentState = "关闭失败";
														// iChangeState.state1(gridList,currentState);
														System.out.println("4");
														btn.setBackgroundResource(R.drawable.toggle_bg_on);
													}

													// Toast.makeText(context,
													// "执行到这里来了",
													// Toast.LENGTH_SHORT).show();
													contrlCmd = "01";
													switchState = currentState;
													/*String states[] = (String[]) map.get("states");
													states[controlLocation] = switchState;
													map.remove("states");
													map.put("states", states);*/
													String tempStates = "";
													String states[] = map.get("states").toString().split("@");
													states[controlLocation] = switchState;
													for(int i=0;i<states.length;i++){
														if(0==i){
															tempStates = states[i];
														}else{
															tempStates += "@" + states[i]; 
														}
													}
													map.remove("states");
													map.put("states", tempStates);
													btn.setSwitchState(switchState);
													btn.setText(currentState);
													
													btn.setTimerRequest(map, id, contrlCmd);
													btn.sechdule();
													/*mTimeTask = new MyTimeTask();
													timer.schedule(mTimeTask,
															0, 1000);*/

													/*
													 * map = gridList.get(i);
													 * map
													 * .remove("switchState");
													 * map.put("switchState",
													 * currentState); for(int
													 * j=0
													 * ;j<gridList.size();j++){
													 * System
													 * .out.println(gridList
													 * .get(j)); } Message msg1
													 * =
													 * handler.obtainMessage(1);
													 * msg1.obj = gridList;
													 * msg1.sendToTarget();
													 * gridList.remove(i);
													 * gridList.add(i, map);
													 */
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}

											@Override
											public void finish() {
												progressdialog.dismiss();
											}

											@Override
											public String filepath() {
												return null;
											}

											@Override
											public void failure(String str) {

											}

											@Override
											public String contentype() {
												return null;
											}

											@Override
											public void updateCacheDate(
													List<HashMap<String, Object>> cacheData) {

											}
										});
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										Field field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);
										dialog.dismiss();
									} catch (Exception e) {

									}
								}
							}).create().show();
		} else if(btText.equals("已关闭")){
			dialogLayout2 = LayoutInflater.from(context).inflate(
					R.layout.input_time, null);
			time_input = (EditText) dialogLayout2.findViewById(R.id.input_time);
			password_input = (EditText) dialogLayout2
					.findViewById(R.id.input_pswd);
			new AlertDialog.Builder(context)
					.setTitle("提示")
					.setView(dialogLayout2)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									View focusView = null;
									boolean cancel = false;
									password_input.setError(null);
									if (TextUtils.isEmpty(password_input
											.getText().toString())) {
										password_input.setError("此项不能为空");
										focusView = password_input;
										cancel = true;
									} else if (!password.equals(password_input
											.getText().toString())) {
										password_input.setError("密码错误");
										focusView = password_input;
										cancel = true;
									}

									if (TextUtils.isEmpty(time_input.getText()
											.toString())) {
										time_input.setError("此项不能为空");
										focusView = time_input;
										cancel = true;
									}else if(Integer.parseInt(time_input.getText().toString())>20){
										time_input.setError("最大时间不能超过20分钟");
										focusView = time_input;
										cancel = true;
									}
									if (cancel) {
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
											dialog.dismiss();
										} catch (Exception e) {

										}
										focusView.requestFocus();
									} else {
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
											dialog.dismiss();
										} catch (Exception e) {

										}
										commandbase.request(new TaskListener() {
											@Override
											public void updateCacheDate(
													List<HashMap<String, Object>> cacheData) {
											}

											@Override
											public void start() {
												progressdialog
														.setMessage("打开中...");
												progressdialog.show();
											}

											@Override
											public String requestUrl() {
												return "updateStatus";
											}

											@Override
											public JSONObject requestData() {
												JSONObject object = new JSONObject();
												try {
													object.put("swithKey",
															switchKey);
													object.put("cmdValue", "10");
													object.put("relayTime",
															time_input
																	.getText()
																	.toString());
													object.put("deviceId",
															deviceId);
													object.put("account",
															userAccount);
												} catch (JSONException e) {
													e.printStackTrace();
												}
												return object;
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
											public void messageUpdated(
													JSONObject msg) {
												JSONObject object = new JSONObject();
												// String currentState="";

												try {
													object = msg
															.getJSONObject("error");
													object = object
															.getJSONObject("control");
													String relayState = object
															.getString("state");
													id =  object.getString("id");
													System.out
															.println("relayState:"
																	+ relayState);
													if ("正在执行"
															.equals(relayState)) {
														currentState = "正在打开";
														// iChangeState.state1(gridList,currentState);
														System.out.println("7");
														btn.setBackgroundResource(R.drawable.toggle_bg_on);
													} else if ("等待执行"
															.equals(relayState)) {
														currentState = "等待打开";
														// iChangeState.state1(gridList,currentState);
														System.out.println("8");
														btn.setBackgroundResource(R.drawable.toggle_bg_on);
													} else if ("成功"
															.equals(currentState)) {
														currentState = "已打开";
														// iChangeState.state1(gridList,currentState);
														System.out.println("9");
														btn.setBackgroundResource(R.drawable.toggle_bg_on);
													} else {
														currentState = "打开失败";
														// iChangeState.state1(gridList,currentState);
														System.out
																.println("10");
														btn.setBackgroundResource(R.drawable.toggle_bg_off);
													}
													// Toast.makeText(context,
													// "执行到这里来了",
													// Toast.LENGTH_SHORT).show();
													contrlCmd = "10";
													switchState = currentState;
													String tempStates = "";
													String states[] = map.get("states").toString().split("@");
													states[controlLocation] = switchState;
													for(int i=0;i<states.length;i++){
														if(0==i){
															tempStates = states[i];
														}else{
															tempStates += "@" + states[i]; 
														}
													}
													map.remove("states");
													map.put("states", tempStates);
													btn.setSwitchState(switchState);
													btn.setText(currentState);
													
													btn.setTimerRequest(map, id, contrlCmd);
													btn.sechdule();
													/*mTimeTask = new MyTimeTask();
													timer.schedule(mTimeTask,
															0, 1000);*/
													/*
													 * map = gridList.get(i);
													 * map
													 * .remove("switchState");
													 * map.put("switchState",
													 * currentState);
													 * gridList.remove(i);
													 * gridList.add(i, map);
													 * for(int
													 * j=0;j<gridList.size
													 * ();j++){
													 * System.out.println
													 * (gridList.get(j)); }
													 * Message msg1 =
													 * handler.obtainMessage(1);
													 * msg1.obj = gridList;
													 * msg1.sendToTarget();
													 */
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}

											@Override
											public void finish() {
												progressdialog.dismiss();
											}

											@Override
											public String filepath() {
												return null;
											}

											@Override
											public void failure(String str) {

											}

											@Override
											public String contentype() {
												return null;
											}
										});
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										Field field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);
										dialog.dismiss();
									} catch (Exception e) {

									}
								}
							}).create().show();
		}

	}

}
