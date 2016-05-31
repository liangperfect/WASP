package com.xiaoguo.wasp.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.communication.CommunicationActivity;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.database.pushDb;
import com.xiaoguo.wasp.mobile.forecast.RelevantForecastActivity;
import com.xiaoguo.wasp.mobile.model.Expert;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.PushInfo;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.chat.ChatBriefActivity;
import com.xiaoguo.wasp.mobile.ui.monitor.AllGreenHouseActivity;
import com.xiaoguo.wasp.mobile.ui.setting.SettingActivity;
import com.xiaoguo.wasp.mobile.ui.warning.DisterWarmingActivity;
import com.xiaoguo.wasp.mobile.ui.warning.ProductPromptActivity;
import com.xiaoguo.wasp.mobile.ui.weatherinfo.WeatherActivityBaiDu;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager;

public class MainActivityTab extends Activity implements
		android.view.View.OnClickListener {
	@SuppressWarnings("deprecation")
	private LocalActivityManager mlam;
	private RadioButton tabWeather, tabProduce, tabDiaster, tabIm,
			tabSetting, // 这个setting对应的是交流 ,
			tabMore, tabDataInterfration, tabMonitor, tabMoreSetting,
			tabCommunication;

	private LinearLayout main_lineer, tab, containerBordy;

	private RadioGroup main_radio;

	private TabActivity activityUtil;

	private Button exitBtn;
	private TextView titleView;
	UserSettingInfo userSettingInfo = null;
	String userType = "";
	IMMessageDb messageDb = null;
	MainReceiver receiver = null;
	int unreadMessage = 0;
	// MainAdpater adapter = null;
	ArrayList<HashMap<String, Object>> items = null;
	GridView gridview = null;
	CommandBase commandbase = CommandBase.instance();
	private HashMap<String, Object> productMap;
	private static final int WHAT_DID_LOAD_DATA = 0;
	private ProgressBar progressBar;
	private TextView noticeView;
	private Button commitView;
	private Dialog builder;
	View v = null;
	private ProductDb productDb;
	OFFlineReceiver oFlineReceiver = null;
	private PushInfo pushInfo;
	private pushDb pushdb;
	private Integer unReadCounts;
	private ProgressDialog progressDialog;
	// 弹出更多的选择框
	final PopupWindow pw = new PopupWindow();
	// Radio容器
	private List<RadioButton> radioContainer;
	// private int MOREATABDATAINTERFRAGTION = 1, MORETABMONITOR = 2,
	// MORETABCOMMUNICATION = 3, MORETABSETTING = 4;
	/**
	 * 对radioButton上的图片进行限制
	 */
	private Rect rect;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_tab);
		mlam = new LocalActivityManager(MainActivityTab.this, false);
		mlam.dispatchCreate(savedInstanceState);
		initView();

		pushInfo = new PushInfo();
		pushdb = new pushDb(MainActivityTab.this);
		userSettingInfo = new UserSettingInfo(MainActivityTab.this);
		progressDialog = new ProgressDialog(MainActivityTab.this);
		userType = userSettingInfo.getType();
		System.out.println("当前用户的类型是：" + userType);

		productDb = new ProductDb(this);
		messageDb = new IMMessageDb(MainActivityTab.this);
		unreadMessage = messageDb.getUnreadMessageNum(
				userSettingInfo.getAccount(), "", 0);
		System.out.println("用户未读消息数：" + unreadMessage);

		WASPApplication.getInstance().addActivity(this);
		initView();

		LayoutInflater mInflater = LayoutInflater.from(this);
		v = mInflater.inflate(R.layout.beforemain, null);
		initView(v);

	}

	private void initView(View v) {
		// titleView = (TextView)v.findViewById(R.id.before_main_title);
		progressBar = (ProgressBar) v.findViewById(R.id.load_info_progress);
		progressBar.setVisibility(View.GONE);
		noticeView = (TextView) v.findViewById(R.id.notice_message);
		noticeView.setText("初次登录，需要加载数据");
		commitView = (Button) v.findViewById(R.id.commit_button);
		commitView.setText("下载");
		commitView.setOnClickListener(this);
		builder = new Dialog(MainActivityTab.this);
		builder.setTitle("提示");
		builder.setContentView(v);
		/*
		 * if ((userType.equals("farmer") && productDb.getAllExperts(
		 * userSettingInfo.getAccount()).size() <= 0) ||
		 * (!userType.equals("farmer") && productDb.getAllFarmers(
		 * userSettingInfo.getAccount()).size() <= 0)) { builder.show(); }
		 */
		if (!productDb.isUserLoadInfo(userSettingInfo.getAccount())) {
			initData();
		}

		commandbase.setCurrActivityContext(MainActivityTab.this,
				MainActivityTab.this);

	}

	@SuppressLint("NewApi")
	private void initView() {
		/**
		 * 对radioButton的GettopDrawable的图片进行设置
		 */
		rect = new Rect(0, 0, 65, 65);
		Drawable drawableWeather = getResources().getDrawable(
				R.drawable.tab_weather);
		Drawable drawableProduce = getResources().getDrawable(
				R.drawable.tab_produce);
		Drawable drawableDiaster = getResources().getDrawable(
				R.drawable.tab_diaster);
		Drawable drawableIm = getResources().getDrawable(R.drawable.tab_im);
		Drawable drawableSetting = getResources().getDrawable(
				R.drawable.alarm_bell);
		Drawable drawableMore = getResources().getDrawable(R.drawable.more);
		drawableWeather.setBounds(rect);
		drawableProduce.setBounds(rect);
		drawableDiaster.setBounds(rect);
		drawableIm.setBounds(rect);
		drawableSetting.setBounds(rect);
		drawableMore.setBounds(rect);

		/**
		 * 
		 */
		tabWeather = (RadioButton) findViewById(R.id.tab_weather);
		tabWeather.setCompoundDrawables(null, drawableWeather, null, null);
		tabProduce = (RadioButton) findViewById(R.id.tab_produce);
		tabProduce.setCompoundDrawables(null, drawableProduce, null, null);
		tabDiaster = (RadioButton) findViewById(R.id.tab_diaster);
		tabDiaster.setCompoundDrawables(null, drawableDiaster, null, null);
		tabIm = (RadioButton) findViewById(R.id.tab_im);
		tabIm.setCompoundDrawables(null, drawableIm, null, null);
		tabSetting = (RadioButton) findViewById(R.id.tab_setting);
		tabSetting.setCompoundDrawables(null, drawableSetting, null, null);
		tabMore = (RadioButton) findViewById(R.id.tab_more);
		tabMore.setCompoundDrawables(null, drawableMore, null, null);
		main_lineer = (LinearLayout) findViewById(R.id.main_lineer);
		tab = (LinearLayout) findViewById(R.id.tab);
		main_radio = (RadioGroup) findViewById(R.id.main_radio);
		initRadios();
		containerBordy = (LinearLayout) findViewById(R.id.containerBody);
		tab.getBackground().setAlpha(180);
		activityUtil = new TabActivity();
		tabWeather.setChecked(true);
	}

	private void initRadios() {
		radioContainer = new ArrayList<RadioButton>();
		radioContainer.add(tabWeather);
		radioContainer.add(tabProduce);
		radioContainer.add(tabDiaster);
		radioContainer.add(tabIm);
		radioContainer.add(tabMore);

		tabWeather.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tabChange(buttonView, isChecked);

			}

		});

		tabProduce.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tabChange(buttonView, isChecked);

			}
		});

		tabDiaster.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				tabChange(buttonView, isChecked);
			}
		});

		tabIm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tabChange(buttonView, isChecked);

			}
		});

		tabSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tabChange(buttonView, isChecked);

			}
		});

		tabMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View buttonView) {
				if (pw.isShowing() == false) {
					LayoutInflater li = LayoutInflater
							.from(MainActivityTab.this);
					View moreView = li.inflate(R.layout.main_tab_more, null);
					Drawable drawableDataInteration = getResources()
							.getDrawable(R.drawable.data_integration);
					drawableDataInteration.setBounds(rect);
					Drawable drawableCommunication = getResources()
							.getDrawable(R.drawable.communication);
					drawableCommunication.setBounds(rect);
					Drawable drawableIm = getResources().getDrawable(
							R.drawable.expert);
					drawableIm.setBounds(rect);
					Drawable drawableMoreSetting = getResources().getDrawable(
							R.drawable.tab_setting);
					drawableMoreSetting.setBounds(rect);

					/**
					 * tabDataInterfration, tabMonitor, tabIM, tabCommunication
					 */
					tabDataInterfration = (RadioButton) moreView
							.findViewById(R.id.data_integration);
					tabMonitor = (RadioButton) moreView
							.findViewById(R.id.monitor);
					tabCommunication = (RadioButton) moreView
							.findViewById(R.id.Im);
					tabMoreSetting = (RadioButton) moreView
							.findViewById(R.id.more_setting);
					tabDataInterfration.setCompoundDrawables(null,
							drawableDataInteration, null, null);
					tabMonitor.setCompoundDrawables(null, drawableIm, null,
							null);
					tabMonitor.setVisibility(View.GONE);
					tabCommunication.setCompoundDrawables(null,
							drawableCommunication, null, null);
					tabMoreSetting.setCompoundDrawables(null,
							drawableMoreSetting, null, null);
					// final PopupWindow pw = new PopupWindow(moreView, 500,
					// LayoutParams.WRAP_CONTENT, true);
					pw.setContentView(moreView);
					pw.setWidth(550);
					pw.setHeight(LayoutParams.WRAP_CONTENT);
					pw.showAsDropDown(buttonView, 0, 20);

					System.out.println("radio的高度是多少-->>"
							+ main_radio.getMeasuredHeight()
							+ "   getwidth-->>" + main_radio.getHeight());

					tabDataInterfration
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									System.out.println("你选择了数据集成");
									pw.dismiss();
									// judgePopWindow = true;
								}
							});

					tabMonitor.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onMoreTabChange(2);
							// judgePopWindow = true;
						}
					});

					tabCommunication.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onMoreTabChange(3);

						}
					});

					tabMoreSetting.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							System.out.println("你选择了设置");
							pw.dismiss();
							// judgePopWindow = true;
							onMoreTabChange(4);
						}
					});

				} else {
					pw.dismiss();
				}
			}

		});

	}

	@SuppressWarnings("deprecation")
	private void tabChange(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			containerBordy.removeAllViews();
			Intent intent = null;
			switch (buttonView.getId()) {
			case R.id.tab_weather:
				changRadioTextColor(buttonView.getId(), isChecked);
				intent = new Intent(MainActivityTab.this,
						WeatherActivityBaiDu.class);

				break;
			case R.id.tab_produce:
				changRadioTextColor(buttonView.getId(), isChecked);
				intent = new Intent(MainActivityTab.this,
						ProductPromptActivity.class);
				break;
			case R.id.tab_im:
				changRadioTextColor(buttonView.getId(), isChecked);
				intent = new Intent(MainActivityTab.this,
						CommunicationActivity.class);
				break;

			case R.id.tab_diaster:
				changRadioTextColor(buttonView.getId(), isChecked);
				intent = new Intent(MainActivityTab.this,
						RelevantForecastActivity.class);
				break;
			case R.id.tab_setting:
				changRadioTextColor(buttonView.getId(), isChecked);
				intent = new Intent(MainActivityTab.this,
						DisterWarmingActivity.class);

				break;

			}

			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			Window subActivity = mlam.startActivity("startActivity", intent);
			containerBordy.addView(subActivity.getDecorView());

		}

	}

	// 改变Radio的字体颜色
	private void changRadioTextColor(int id, boolean f) {
		for (int i = 0; i < radioContainer.size(); i++) {
			if (id == radioContainer.get(i).getId() && f)
				radioContainer.get(i).setTextColor(Color.BLACK);
			else
				radioContainer.get(i).setTextColor(Color.GRAY);

		}

	}

	/**
	 * 更多按钮键对应的界面
	 * 
	 * @param moreTabIndex
	 *            按钮索引
	 */
	private void onMoreTabChange(int moreTabIndex) {
		containerBordy.removeAllViews();
		Intent i = null;

		switch (moreTabIndex) {
		case 1:
			// 数据集成
			break;
		case 2:
			// 实时监控
			i = new Intent();
			i.setClass(MainActivityTab.this, AllGreenHouseActivity.class);
			break;
		case 3:
			// 论坛 交流
			i = new Intent();
			i.setClass(MainActivityTab.this, ChatBriefActivity.class);
			break;
		case 4:
			// 更多设置
			i = new Intent();
			i.setClass(MainActivityTab.this, SettingActivity.class);
			break;
		}
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window subActivity = mlam.startActivity("startActivity", i);
		containerBordy.addView(subActivity.getDecorView());
		pw.dismiss();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private long exitTime = 0;

	private void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			// ConnectionUtils.getConnection(MainActivity.this).disconnect();
			// 引起程序卡死
			System.out.println("进行退出...");
			// Cancel();
			finish();
			System.exit(0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			new AlertDialog.Builder(MainActivityTab.this)
					.setTitle("提示")
					.setMessage("确认要退出应用吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// productDb.deleteArticleName();
									off();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create().show();
			break;
		case R.id.commit_button:
			if (commitView.getText().toString().equals("下载")) {
				noticeView.setText("正在加载,请稍后...");
				commitView.setVisibility(View.INVISIBLE);
				initData();
			} else {
				builder.dismiss();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_tab, menu);
		return true;
	}

	private void initData() {
		final List<Expert> list = new ArrayList<Expert>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
			}

			@Override
			public String requestUrl() {
				return "userList";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					if (userType.equals("user_admin")) {
						object.put("role_id", 1);// 专家列表
					} else {
						object.put("role_id", 3);// 农户列表
					}
				} catch (JSONException e) {
					object = null;
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
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void messageUpdated(JSONObject msg) {
				Expert expert = null;
				try {
					JSONObject object = msg.getJSONObject("data");
					JSONArray array = object.getJSONArray("userlist");
					HashMap<String, Object> map = null;
					Friends friends = null;
					for (int i = 0; i < array.length(); i++) {
						object = array.getJSONObject(i);
						String userId = object.getString("user_name");
						String userRemark = object.getString("user_remark");
						String userName = object.getString("user_display_name");
						// String userImg = object.getString("user_img");
						// String host = CommandBase.instance().getHost();
						friends = new Friends();
						if (userName.equals("")) {
							userName = userId;
						}
						expert = new Expert(userId, userName, userRemark, "");
						String jid = CommandBase.instance().getHost();
						jid = jid.substring(0, jid.indexOf(":"));
						jid = expert.getExpertAccount() + "@" + jid;
						if (expert.getExpertName() == null) {
							friends.setName(expert.getExpertAccount());
						} else {
							friends.setName(expert.getExpertName());
						}
						friends.setJID(jid);
						Roster roster = ConnectionUtils.getConnection(
								MainActivityTab.this).getRoster();
						Presence presence = roster.getPresence(jid);
						friends.setFrom(presence.getFrom());
						String state = "离线";
						org.jivesoftware.smack.packet.Presence.Mode usMode = presence
								.getMode();
						if (usMode == org.jivesoftware.smack.packet.Presence.Mode.dnd) {
							state = "忙碌";
						} else if (usMode == org.jivesoftware.smack.packet.Presence.Mode.away
								|| usMode == org.jivesoftware.smack.packet.Presence.Mode.xa) {
							state = "离开";
						} else if (presence.isAvailable()) {
							state = "在线";
						} else {
							state = "离线";
						}
						// friends.setStatus(presence.getStatus());
						friends.setStatus(state);
						// friends.setSize(entry.getGroups().size());
						friends.setAvailable(presence.isAvailable());
						// friends.setType(entry.getType());
						if (userType.equals("user_admin")
								&& !productDb.isExpertSaved(expert,
										userSettingInfo.getAccount())) {
							try {
								createSubscriber(jid, expert.getExpertName(),
										new String[] { "专家" });
								ContacterManager.contacters.put(jid, friends);
								productDb.saveExpert(expert,
										userSettingInfo.getAccount());
								list.add(expert);
							} catch (XMPPException e) {
								e.printStackTrace();
							}
						}
						if (!userType.equals("user_admin")
								&& !productDb.isFarmerSaved(expert,
										userSettingInfo.getAccount())) {
							try {
								createSubscriber(jid, expert.getExpertName(),
										new String[] { "农户" });
								ContacterManager.contacters.put(jid, friends);
								productDb.saveFarmer(expert,
										userSettingInfo.getAccount());
								list.add(expert);
							} catch (XMPPException e) {
								/*
								 * Toast.makeText(MainActivity.this, "添加农户出错了",
								 * Toast.LENGTH_SHORT).show();
								 */
								e.printStackTrace();
							}
						}
					}
					Message message = null;
					message = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					message.obj = list;
					message.sendToTarget();
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("专家列表解析异常");
				}
			}

			@Override
			public void finish() {
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

	private Handler mUIHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				progressBar.setVisibility(View.GONE);
				commitView.setVisibility(View.VISIBLE);
				noticeView.setText("加载完成");
				commitView.setText("确定");
				break;
			}
			}

		}
	};

	protected void createSubscriber(String userJid, String nickname,
			String[] groups) throws XMPPException {

		// ConnectionUtils.getConnection(MainActivityTab.this).getRoster()
		// .createEntry(userJid, nickname, groups);
	}

	private void Cancel() {

		pushInfo = pushdb.getPushInfo();
		commandbase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {

			}

			@Override
			public String requestUrl() {
				return "Push";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("push_user_id", pushInfo.getUserID());
					object.put("channel_id", pushInfo.getChannelID());
					object.put("login_flg", false);
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
			public void messageUpdated(JSONObject msg) {
				System.out.println("返回的数据是" + msg.toString());
			}

			@Override
			public void finish() {

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

	@Override
	protected void onResume() {
		receiver = new MainReceiver();
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constant.ROSTER_ADDED);
		filter.addAction(Constant.ROSTER_DELETED);
		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
		filter.addAction(Constant.ROSTER_UPDATED);
		// 好友请求
		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		registerReceiver(receiver, filter);

		oFlineReceiver = new OFFlineReceiver();
		filter = new IntentFilter();
		filter.addAction(Constant.OFFLINE_MESSAGE_ACTION);
		registerReceiver(oFlineReceiver, filter);
		super.onResume();
	}

	private class OFFlineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.OFFLINE_MESSAGE_ACTION.equals(action)) {

			}
		}

	}

	private class MainReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.ROSTER_ADDED.equals(action)) {
			}

			else if (Constant.ROSTER_DELETED.equals(action)) {
			}

			else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
			}

			else if (Constant.ROSTER_UPDATED.equals(action)) {
			}

			else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {

			} else if (Constant.NEW_MESSAGE_ACTION.equals(intent.getAction())) {

				Friends friends = intent.getParcelableExtra(Friends.userKey);

				NotificationManager mgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification nt = new Notification();
				nt.defaults = Notification.DEFAULT_SOUND;
				int soundId = new Random(System.currentTimeMillis())
						.nextInt(Integer.MAX_VALUE);
				mgr.notify(soundId, nt);

				IMMessage message = intent
						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
				System.out.println("message=" + message);
				System.out.println("MAINAchat---" + message.getChatMode());
				message.setUnReadCount(0);
				String str = message.getFromSubJid();
				str = str.substring(0, str.indexOf("@"));

				System.out.println("Type=" + message.getType());
				messageDb.saveMessage(message, str,
						userSettingInfo.getAccount());
				unreadMessage = messageDb.getUnreadMessageNum(
						userSettingInfo.getAccount(), "", 0);
			}
		}
	}

	private void off() {

		pushInfo = pushdb.getPushInfo();
		commandbase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				progressDialog.setMessage("退出中...");
				progressDialog.show();
			}

			@Override
			public String requestUrl() {
				return "Push";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("push_user_id", pushInfo.getUserID());
					object.put("channel_id", pushInfo.getChannelID());
					object.put("login_flg", false);
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
			public void messageUpdated(JSONObject msg) {
				System.out.println("返回的数据是" + msg.toString());

			}

			@Override
			public void finish() {
				progressDialog.dismiss();
				System.exit(0);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("返回的是这里的onActivityResult");
		CommunicationActivity currentActivity = (CommunicationActivity) mlam
				.getActivity("startActivity");

		// Activity a = mlam.getCurrentActivity()
		System.out.println("currentActivity-->>" + currentActivity);

		// System.out.println("requestCode-->>" + requestCode
		// + "    resultCode-->>" + resultCode + "  data"
		// + data.getIntExtra("blockID", 0));
		currentActivity.onActivityResultFromMainActivity(requestCode,
				resultCode, data);
		// currentActivity.YSOS();
	}

	private Bitmap getBitmap(int drawabledId, int width, int height) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				drawabledId);

		if (bitmap != null) {
			int newWidth = bitmap.getWidth();
			int newHeight = bitmap.getHeight();
			Matrix martrix = new Matrix();
			float scaleWidth = ((float) width / newWidth);
			float scaleHeight = ((float) height / newHeight);
			martrix.postScale(scaleWidth, scaleHeight);
			System.out.println("newWidth--->>" + newWidth + "newHeight-->>"
					+ newHeight);
			Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, newWidth,
					newHeight, martrix, true);
			return newBitmap;
		} else {
			return null;
		}

	}

}
