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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.database.pushDb;
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
import com.xiaoguo.wasp.mobile.ui.monitor.ExpertSerchActivity;
import com.xiaoguo.wasp.mobile.ui.setting.SettingActivity;
import com.xiaoguo.wasp.mobile.ui.warning.ProductWarningActivity;
import com.xiaoguo.wasp.mobile.ui.weatherinfo.WeatherInfoActivity;
import com.xiaoguo.wasp.mobile.widget.MainAdpater;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager;

/*
 * 主界面
 * 
 * */
public class MainActivity extends Activity implements OnClickListener {
	private Button exitBtn;
	private TextView titleView;
	UserSettingInfo userSettingInfo = null;
	String userType = "";
	IMMessageDb messageDb = null;
	MainReceiver receiver = null;
	int unreadMessage = 0;
	MainAdpater adapter = null;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pushInfo = new PushInfo();
		pushdb = new pushDb(MainActivity.this);
		userSettingInfo = new UserSettingInfo(MainActivity.this);
		progressDialog = new ProgressDialog(MainActivity.this);
		userType = userSettingInfo.getType();
		System.out.println("当前用户的类型是：" + userType);

		productDb = new ProductDb(this);
		messageDb = new IMMessageDb(MainActivity.this);
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

		// builder = new AlertDialog.Builder(this).setView(v);
		// builder.create().show();
		builder = new Dialog(MainActivity.this);
		builder.setTitle("提示");
		builder.setContentView(v);
		/*
		 * if ((userType.equals("farmer") && productDb.getAllExperts(
		 * userSettingInfo.getAccount()).size() <= 0) ||
		 * (!userType.equals("farmer") && productDb.getAllFarmers(
		 * userSettingInfo.getAccount()).size() <= 0)) { builder.show(); }
		 */
		if (!productDb.isUserLoadInfo(userSettingInfo.getAccount())) {
			// builder.show();
			initData();
		}
	}

	private void initView() {
		exitBtn = (Button) findViewById(R.id.bt_left);
		exitBtn.setVisibility(View.VISIBLE);
		exitBtn.setOnClickListener(this);
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("三农气象智能服务平台");
		// gridview
		gridview = (GridView) findViewById(R.id.gridview);
		HashMap<String, Object> productMap1 = new HashMap<String, Object>();
		// map = new HashMap<String, Object>();
		productMap1.put("img", R.drawable.produce_tip);
		productMap1.put("str", "生产提示");
		unReadCounts = productDb.getUnReadArticleCount(
				userSettingInfo.getAccount(), 4);
		productMap1.put("num", unReadCounts);
		productMap = productMap1;
		getItems();
		adapter = new MainAdpater(MainActivity.this, items);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:// 气象信息
					Intent intent1 = new Intent();
					intent1.setClass(MainActivity.this,
							WeatherInfoActivity.class);
					// MainActivity.this.startActivity(intent1);
					MainActivity.this.startActivityForResult(intent1, 1);
					break;
				case 1:// 生产提示
					Intent intent2 = new Intent();
					intent2.setClass(MainActivity.this,
							ProductWarningActivity.class);
					// MainActivity.this.startActivity(intent2);
					MainActivity.this.startActivityForResult(intent2, 1);
					break;
				case 2:// 实时监控
					Intent intent3 = new Intent();
					if (userType.equals("farmer")) {
						/*
						 * intent3.setClass(MainActivity.this,
						 * RealtimeMoniterActivity.class);
						 */
						intent3.setClass(MainActivity.this,
								AllGreenHouseActivity.class);
					} else {
						intent3.setClass(MainActivity.this,
								ExpertSerchActivity.class);
					}
					// MainActivity.this.startActivity(intent3);
					MainActivity.this.startActivityForResult(intent3, 1);
					break;
				case 3:// 专家咨询
					unreadMessage = messageDb.getUnreadMessageNum(
							userSettingInfo.getAccount(), "", 0);
					System.out.println("用户未读消息数：" + unreadMessage);
					Intent intent4 = new Intent();
					intent4.setClass(MainActivity.this, ChatBriefActivity.class);
					MainActivity.this.startActivityForResult(intent4, 1);
					break;
				case 4:// 系统设置
					Intent intent5 = new Intent();
					intent5.setClass(MainActivity.this, SettingActivity.class);
					// MainActivity.this.startActivity(intent5);
					MainActivity.this.startActivityForResult(intent5, 1);
					break;
				default:
					break;
				}
			}
		});
	}

	private void getItems() {
		items = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;
		map = new HashMap<String, Object>();
		map.put("img", R.drawable.weatherinfo);
		map.put("str", "气象信息");
		map.put("num", 0);
		items.add(map);

		// map = new HashMap<String, Object>();
		// map.put("img", R.drawable.produce_tip);
		// map.put("str", "生产提示");
		// map.put("num", 0);
		items.add(productMap);

		// 实时监控
		map = new HashMap<String, Object>();
		map.put("img", R.drawable.monitor);
		map.put("str", "实时监控");
		map.put("num", 0);
		items.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.expert);
		if (userType.equals("farmer")) {
			map.put("str", "专家咨询");
		} else {
			map.put("str", "农户指导");
		}
		map.put("num", unreadMessage);
		items.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.btn_setting);
		map.put("str", "设置");
		map.put("num", 0);
		items.add(map);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				System.out.println("执行了Main的回传");
				messageDb = new IMMessageDb(MainActivity.this);
				unreadMessage = messageDb.getUnreadMessageNum(
						userSettingInfo.getAccount(), "", 5);

				HashMap<String, Object> productMap2 = new HashMap<String, Object>();
				// map = new HashMap<String, Object>();
				productMap2.put("img", R.drawable.produce_tip);
				productMap2.put("str", "生产提示");
				unReadCounts = productDb.getUnReadArticleCount(
						userSettingInfo.getAccount(), 4);
				productMap2.put("num", unReadCounts);
				productMap = productMap2;
				getItems();
				adapter = new MainAdpater(MainActivity.this, items);
				gridview.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			new AlertDialog.Builder(MainActivity.this)
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
				// progressBar.setVisibility(View.VISIBLE);
				commitView.setVisibility(View.INVISIBLE);
				initData();
			} else {
				/*
				 * Intent i = new Intent(); i.setClass(MainActivity.this,
				 * MainActivity.class); startActivity(i); this.finish();
				 */
				getItems();
				adapter = new MainAdpater(MainActivity.this, items);
				gridview.setAdapter(adapter);
				builder.dismiss();
			}
			break;
		default:
			break;
		}

	}

	private void initData() {
		final List<Expert> list = new ArrayList<Expert>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				// progressDialog.setMessage("正在初始化数据，请稍候...");
				// progressDialog.show();
			}

			@Override
			public String requestUrl() {
				return "userList";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					if (userType.equals("farmer")) {
						object.put("role_id", 3);// 专家列表
					} else {
						object.put("role_id", 2);// 农户列表
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
								MainActivity.this).getRoster();
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
								/*
								 * Toast.makeText(MainActivity.this, "添加专家出错了",
								 * Toast.LENGTH_SHORT).show();
								 */
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
				// dialog.dismiss();
				// progressDialog.dismiss();
			}

			@Override
			public String filepath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void failure(String str) {
				// TODO Auto-generated method stub

			}

			@Override
			public String contentype() {
				// TODO Auto-generated method stub
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
				/*
				 * getItems(); adapter = new MainAdpater(MainActivity.this,
				 * items); gridview.setAdapter(adapter);
				 */
				break;
			}
			}

		}
	};

	protected void createSubscriber(String userJid, String nickname,
			String[] groups) throws XMPPException {

		ConnectionUtils.getConnection(MainActivity.this).getRoster()
				.createEntry(userJid, nickname, groups);
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 判断导航是否弹出
			// productDb.deleteArticleName();
			off();
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			// ConnectionUtils.getConnection(MainActivity.this).disconnect();
			// 引起程序卡死
			Cancel();
			finish();
			System.exit(0);
		}
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
	protected void onPause() {
		unregisterReceiver(receiver);
		unregisterReceiver(oFlineReceiver);
		super.onPause();
		Log.e("MainActivity", "onPause的方法");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("MainActivity", "onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("MainActivity", "onDestroy()");

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

				getItems();
				adapter = new MainAdpater(MainActivity.this, items);
				gridview.setAdapter(adapter);
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
				getItems();
				adapter = new MainAdpater(MainActivity.this, items);
				gridview.setAdapter(adapter);
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

}
