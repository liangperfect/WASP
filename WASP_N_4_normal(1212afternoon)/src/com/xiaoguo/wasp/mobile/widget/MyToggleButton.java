package com.xiaoguo.wasp.mobile.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MyToggleButton extends Button{
	private int switchKey;
	private String switchState;
	private String deviceId;
	private String switchName;
	private UserSettingInfo userSettingInfo;
	private HashMap<String, Object> map;
	private int controlLocation;
	private MyToggleButton btn1;
	private MyToggleButton btn2;
	private MyToggleButton btn3;
	
	
	Timer timer;
	MyTimeTask mTimeTask;
	Context context;
	int count;
	String requestId;
	String contrlCmd;
	
	private CommandBase commandbase = CommandBase.instance();
	
	public void setRequestInfo(Context context, int switchKey,
			String switchState, String deviceId, String switchName,
			UserSettingInfo userSettingInfo,
			HashMap<String, Object> map,
			int controlLocation){
		this.context = context;
		this.switchKey = switchKey;
		this.switchState = switchState;
		this.deviceId = deviceId;
		this.switchName = switchName;
		this.userSettingInfo = userSettingInfo;
		this.map = map;
		this.controlLocation = controlLocation;
		timer = new Timer(true);
	}
	public void setButtons(MyToggleButton btn1, MyToggleButton btn2, MyToggleButton btn3){
		this.btn1 = btn1;
		this.btn2 = btn2;
		this.btn3 = btn3;
	}
	public void setTimerRequest(
				HashMap<String, Object> map,
				String requestId,
				String contrlCmd){
		this.map = map;
		this.requestId = requestId;
		this.contrlCmd = contrlCmd;
	}
	
	public void sechdule(){
		mTimeTask = new MyTimeTask();
		timer.schedule(mTimeTask,
				0, 1000);
	}
	Handler mHandler = new Handler() {
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
							object.put("id", requestId);
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
										setBackgroundResource(R.drawable.toggle_bg_off);
									}else if("等待执行".equals(nowState)){
										switchState = "等待关闭";
										setBackgroundResource(R.drawable.toggle_bg_off);
									}else if("成功".equals(nowState)){
										switchState = "已关闭";
										setBackgroundResource(R.drawable.toggle_bg_off);
									}else{
										switchState = "已打开";
										setBackgroundResource(R.drawable.toggle_bg_on);
									}
									setSwitchState(switchState);
									setText(switchState);
								}
								if("10".equals(contrlCmd)){
									if("正在执行".equals(nowState)){
										switchState = "正在打开";
										setBackgroundResource(R.drawable.toggle_bg_on);
									}else if("等待执行".equals(nowState)){
										switchState = "等待打开";
										setBackgroundResource(R.drawable.toggle_bg_on);
									}else if("成功".equals(nowState)){
										switchState = "已打开";
										setBackgroundResource(R.drawable.toggle_bg_on);
									}else{
										switchState = "已关闭";
										setBackgroundResource(R.drawable.toggle_bg_off);
									}
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
									System.out.println("tempStates="+tempStates);
									map.remove("states");
									map.put("states", tempStates);
									setSwitchState(switchState);
									setText(switchState);
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
				MyToggleClickListener myToggleClickListener = new MyToggleClickListener();
				myToggleClickListener.setContext(context);
				setOnClickListener(myToggleClickListener);
				btn1.setOnClickListener(myToggleClickListener);
				btn2.setOnClickListener(myToggleClickListener);
				btn3.setOnClickListener(myToggleClickListener);
			} else {
				// Toast.makeText(MainActivity.this, "还剩"+count+"秒",
				// Toast.LENGTH_SHORT).show();
				/*btn.setEnabled(false);
				btn1.setEnabled(false);
				btn2.setEnabled(false);
				btn3.setEnabled(false);*/
				System.out.println("计时器"+count);
				MyCliclListener myCliclListener = new MyCliclListener(count);
				setOnClickListener(myCliclListener);
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
		int i=5;
		@Override
		public void run() {
			Message message = mHandler.obtainMessage();
			message.what = i--;
			mHandler.sendMessage(message);

		}

	}

	/**
	 * @return the timer
	 */
	public Timer getTimer() {
		return timer;
	}
	/**
	 * @param timer the timer to set
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	/**
	 * @return the controlLocation
	 */
	public int getControlLocation() {
		return controlLocation;
	}
	/**
	 * @param controlLocation the controlLocation to set
	 */
	public void setControlLocation(int controlLocation) {
		this.controlLocation = controlLocation;
	}
	/**
	 * @return the map
	 */
	public HashMap<String, Object> getMap() {
		return map;
	}
	/**
	 * @param map the map to set
	 */
	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}
	/**
	 * @return the btn1
	 */
	public MyToggleButton getBtn1() {
		return btn1;
	}
	/**
	 * @param btn1 the btn1 to set
	 */
	public void setBtn1(MyToggleButton btn1) {
		this.btn1 = btn1;
	}
	/**
	 * @return the btn2
	 */
	public MyToggleButton getBtn2() {
		return btn2;
	}
	/**
	 * @param btn2 the btn2 to set
	 */
	public void setBtn2(MyToggleButton btn2) {
		this.btn2 = btn2;
	}
	/**
	 * @return the btn3
	 */
	public MyToggleButton getBtn3() {
		return btn3;
	}
	/**
	 * @param btn3 the btn3 to set
	 */
	public void setBtn3(MyToggleButton btn3) {
		this.btn3 = btn3;
	}
	/**
	 * @return the switchKey
	 */
	public int getSwitchKey() {
		return switchKey;
	}
	/**
	 * @param switchKey the switchKey to set
	 */
	public void setSwitchKey(int switchKey) {
		this.switchKey = switchKey;
	}
	/**
	 * @return the switchState
	 */
	public String getSwitchState() {
		return switchState;
	}
	/**
	 * @param switchState the switchState to set
	 */
	public void setSwitchState(String switchState) {
		this.switchState = switchState;
	}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the switchName
	 */
	public String getSwitchName() {
		return switchName;
	}
	/**
	 * @param switchName the switchName to set
	 */
	public void setSwitchName(String switchName) {
		this.switchName = switchName;
	}
	/**
	 * @return the userSettingInfo
	 */
	public UserSettingInfo getUserSettingInfo() {
		return userSettingInfo;
	}
	/**
	 * @param userSettingInfo the userSettingInfo to set
	 */
	public void setUserSettingInfo(UserSettingInfo userSettingInfo) {
		this.userSettingInfo = userSettingInfo;
	}
	public MyToggleButton(Context context) {
		super(context);
	}
	public MyToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}
}
