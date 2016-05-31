package com.xiaoguo.wasp.mobile.ui.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.MainActivity;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.Expert;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.User;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.ExpressionUtil;
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager;

@SuppressWarnings("deprecation")
public class ChatBriefActivity extends TabActivity implements OnClickListener {
	private TextView titleView;
	private Button leftBtView;
	// private Button rightView;
	UserSettingInfo userInfo = null;
	ProductDb productDb = null;

	View view;
	TextView unreadNum;
	TextView text;
	ImageView img;
	View view1;
	TextView unreadNum1;
	TextView text1;
	ImageView img1;

	TabWidget tabWidget = null;
	TabHost tabHost = null;

	private IMMessageDb messageDb = null;

	ListView xlistView;
	RecentlyAdapter radapter;
	RecentlyAdapter searchAdapter;
	searchAdapter searchAdapter2;
	List<IMMessage> msgList = null;
	List<IMMessage> msgList2 = null;
	String subFrom = "";
	Context MContext = null;
	ArrayList<User> userList;
	CommandBase commandBase;
	private PullDownView mPullDownView;
	private ListView contacterList = null;
	private ContacterAdapter contactAdapter = null;
	private List<Expert> expertList = null;

	private ListView recordSearchListView = null;
	private ListView groupSearchLisview = null;
	List<HashMap<String, Object>> list1 = null;
	HashMap<String, Object> map = null;

	protected View layoutLoad;
	protected TextView promptInfo;
	protected ProgressBar loading;

	private EditText searchInputView;
	private ImageView searchView;

	private EditText searchInputView1;
	private ImageView searchView1;

	ScrollView mScrollView;

	private ContacterReceiver receiver = null;

	int unreadMessageNum = 0;
	LinearLayout layout = null;

	ProgressDialog dialog = null;

	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;
	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH = 1;

	String userType = "";
	List<String> nickNameList = null;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		MContext = ChatBriefActivity.this;

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		WASPApplication.getInstance().addActivity(this);

		messageDb = new IMMessageDb(MContext);
		userInfo = new UserSettingInfo(this);
		productDb = new ProductDb(this);
		unreadMessageNum = messageDb.getUnreadMessageNum(userInfo.getAccount(),
				"", 0);
		expertList = new ArrayList<Expert>();
		dialog = new ProgressDialog(MContext);
		msgList2 = new ArrayList<IMMessage>();

		userType = userInfo.getType();

		setUpView();
	}

	private void setUpView() {
		titleView = (TextView) findViewById(R.id.title);
		leftBtView = (Button) findViewById(R.id.bt_left);
		// leftBtView.setVisibility(View.VISIBLE);
		// leftBtView.setOnClickListener(this);

		view = (View) LayoutInflater.from(this).inflate(R.layout.mytabhost,
				null);
		unreadNum = (TextView) view.findViewById(R.id.unreadMessageNum);
		text = (TextView) view.findViewById(R.id.text);
		img = (ImageView) view.findViewById(R.id.item);
		if (unreadMessageNum == 0) {
			unreadNum.setVisibility(View.GONE);
		} else {
			unreadNum.setText("" + unreadMessageNum);
			unreadNum.setVisibility(View.VISIBLE);
		}
		text.setText("聊天记录");
		text.setTextSize(14);
		text.setTextColor(getResources().getColor(R.color.text));
		img.setImageResource(R.drawable.record_selector);

		view1 = (View) LayoutInflater.from(this).inflate(R.layout.mytabhost,
				null);
		unreadNum1 = (TextView) view1.findViewById(R.id.unreadMessageNum);
		text1 = (TextView) view1.findViewById(R.id.text);
		img1 = (ImageView) view1.findViewById(R.id.item);
		unreadNum1.setVisibility(View.GONE);
		if (userType.equals("user_admin")) {
			text1.setText("专家列表");
		} else {
			text1.setText("农户列表");
		}
		text1.setTextSize(14);
		text1.setTextColor(getResources().getColor(R.color.text));
		img1.setImageResource(R.drawable.contact_selector);

		tabHost = this.getTabHost();
		// 3个及3个以上的写法
		tabHost.addTab(tabHost.newTabSpec("聊天记录").setIndicator(view)
				.setContent(R.id.chat_record));
		if (userType.equals("user_admin")) {
			tabHost.addTab(tabHost.newTabSpec("专家列表").setIndicator(view1)
					.setContent(R.id.chat_group));
		} else {
			tabHost.addTab(tabHost.newTabSpec("农户列表").setIndicator(view1)
					.setContent(R.id.chat_group));
		}
		tabWidget = tabHost.getTabWidget();
		layout = (LinearLayout) tabHost.getChildAt(0);
		System.out.println("数据：" + tabHost.getChildCount());
		init1(layout);
		init2(layout);
		int i = tabHost.getCurrentTab();
		if (i == 0) {
			titleView.setText("聊天记录");
		}
		if (i == 1) {
			if (userType.equals("user_admin")) {
				titleView.setText("专家列表");
			} else {
				titleView.setText("农户列表");
			}
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("聊天记录")) {
					titleView.setText("聊天记录");
					init1(layout);
				} else {
					if (userType.equals("user_admin")) {
						titleView.setText("专家列表");
					} else {
						titleView.setText("农户列表");
					}
					init2(layout);
				}
			}
		});

	}

	private void init1(LinearLayout view) {
		messageDb = new IMMessageDb(MContext);
		userInfo = new UserSettingInfo(MContext);

		xlistView = (ListView) view.findViewById(R.id.list);
		xlistView.setVerticalScrollBarEnabled(false);
		xlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final IMMessage teImMessage = msgList.get(arg2);
				TextView id = (TextView) arg1.findViewById(R.id.userID);
				TextView textView = (TextView) arg1
						.findViewById(R.id.userRecentMessage);
				if (textView.getText().toString().equals("添加好友请求")) {
					subFrom = id.getText().toString();
					System.out.println("subForm=" + subFrom);
					new AlertDialog.Builder(MContext)
							.setMessage(subFrom + "请求添加您为好友")
							.setTitle("提示")
							.setPositiveButton("添加",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance()
													.getHost();
											jid = subFrom
													+ "@"
													+ jid.substring(0,
															jid.indexOf(":"));
											// 接受请求
											sendSubscribe(
													Presence.Type.subscribed,
													jid);
											messageDb.updateMessage(
													userInfo.getAccount(),
													subFrom, teImMessage, 1,
													";0好友添加成功");
											xlistView
													.setVisibility(View.VISIBLE);
											recordSearchListView
													.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(
													list1, MContext);
											xlistView.setAdapter(radapter);
										}
									})
							.setNegativeButton("拒绝",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance()
													.getHost();
											jid = subFrom
													+ "@"
													+ jid.substring(0,
															jid.indexOf(":"));
											sendSubscribe(
													Presence.Type.unsubscribe,
													jid);
											messageDb.updateMessage(
													userInfo.getAccount(),
													subFrom, teImMessage, 1,
													";0添加好友请求已拒绝");
											list1 = updateView();
											xlistView
													.setVisibility(View.VISIBLE);
											recordSearchListView
													.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(
													list1, MContext);
											xlistView.setAdapter(radapter);
										}
									}).show();
				} else {
					String name = id.getText().toString();
					map = list1.get(arg2);
					String jid = "" + map.get("id");
					jid = jid.substring(0, jid.indexOf("@"));
					System.out.println("聊天记录jid=" + jid);
					Friends friends = new Friends();
					friends.setJID(jid);
					friends.setName(name);
					System.out.println("jid=" + jid);
					Intent intent = new Intent();
					intent.setClass(MContext, ChatLayoutActivity.class);
					Bundle bd = new Bundle();
					bd.putString("style", "from");
					bd.putParcelable("friends", friends);
					intent.putExtra("info", bd);
					startActivityForResult(intent, 10);
				}
			}
		});

		searchInputView = (EditText) view.findViewById(R.id.search_input);
		searchInputView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String str = searchInputView.getText().toString();
				if (str.equals("")) {
					xlistView.setVisibility(View.VISIBLE);
					recordSearchListView.setVisibility(View.GONE);
				}

			}
		});

		searchView = (ImageView) view.findViewById(R.id.search_img);
		searchView.setOnClickListener(this);

		recordSearchListView = (ListView) view.findViewById(R.id.search_list_1);
		recordSearchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final IMMessage teImMessage = msgList.get(arg2 - 1);
				System.out.println("tempmsg=" + teImMessage);
				TextView view = (TextView) arg1.findViewById(R.id.userID);
				TextView textView = (TextView) arg1
						.findViewById(R.id.userRecentMessage);
				if (textView.getText().toString().equals("添加好友请求")) {
					subFrom = view.getText().toString();
					new AlertDialog.Builder(MContext)
							.setMessage(subFrom + "请求添加您为好友")
							.setTitle("提示")
							.setPositiveButton("添加",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance()
													.getHost();
											jid = subFrom
													+ "@"
													+ jid.substring(0,
															jid.indexOf(":"));
											// 接受请求
											sendSubscribe(
													Presence.Type.subscribed,
													jid);
											messageDb.updateMessage(
													userInfo.getAccount(),
													subFrom, teImMessage, 1,
													";0好友添加成功");
											xlistView
													.setVisibility(View.VISIBLE);
											recordSearchListView
													.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(
													list1, MContext);
											xlistView.setAdapter(radapter);
										}
									})
							.setNegativeButton("拒绝",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance()
													.getHost();
											jid = subFrom
													+ "@"
													+ jid.substring(0,
															jid.indexOf(":"));
											sendSubscribe(
													Presence.Type.unsubscribe,
													jid);
											messageDb.updateMessage(
													userInfo.getAccount(),
													subFrom, teImMessage, 1,
													";0添加好友请求已拒绝");
											xlistView
													.setVisibility(View.VISIBLE);
											recordSearchListView
													.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(
													list1, MContext);
											xlistView.setAdapter(radapter);
										}
									}).show();
				} else {
					String name = view.getText().toString();
					String jid = list1.get(arg2 - 1).get("id").toString();
					jid = jid.substring(0, jid.indexOf("@"));
					System.out.println("jid=" + jid);
					Friends friends = new Friends();
					friends.setJID(jid);
					friends.setName(name);
					Intent intent = new Intent();
					intent.setClass(MContext, ChatLayoutActivity.class);
					Bundle bd = new Bundle();
					bd.putString("style", "from");
					bd.putParcelable("friends", friends);
					intent.putExtra("info", bd);
					startActivityForResult(intent, 10);
				}
			}
		});

		// 专门为下拉
		layoutLoad = view.findViewById(R.id.layout_load);
		promptInfo = (TextView) view.findViewById(R.id.prompt_info);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		promptInfo.setOnClickListener(this);

		userList = new ArrayList<User>();
		list1 = updateView();

		xlistView.setVisibility(View.VISIBLE);
		recordSearchListView.setVisibility(View.GONE);

		radapter = new RecentlyAdapter(list1, MContext);
		xlistView.setAdapter(radapter);
	}

	protected int status = LoadStatus.loadSucceed;

	protected class LoadStatus {
		public static final int loading = 0;
		public static final int loadError = 1;
		public static final int loadSucceed = 2;

		public static final int loadRefresh = 3;
		public static final int loadLoadMore = 4;
	}

	private void init2(final LinearLayout view) {
		mPullDownView = (PullDownView) findViewById(R.id.expert_view);
		mPullDownView.setOnPullDownListener(new OnPullDownListener() {
			@Override
			public void onRefresh() {
				loadData(1);
			}

			@Override
			public void onMore() {

			}
		});

		contacterList = mPullDownView.getListView();
		contacterList.setVerticalScrollBarEnabled(false);

		List<Expert> tempList = new ArrayList<Expert>();
		List<Expert> tempList1 = new ArrayList<Expert>();
		if (userType.equals("user_admin")) {
			tempList1 = productDb.getAllExperts(userInfo.getAccount());
		} else {
			tempList1 = productDb.getAllFarmers(userInfo.getAccount());
		}
		expertList = new ArrayList<Expert>();
		Roster roster = ConnectionUtils.getConnection(ChatBriefActivity.this)
				.getRoster();
		String host = CommandBase.instance().getHost();
		host = host.substring(0, host.indexOf(":"));
		System.out.println("host=" + host);
		String expertJid = "";
		org.jivesoftware.smack.packet.Presence presence = roster
				.getPresence(expertJid);
		for (int i = 0; i < tempList1.size(); i++) {
			expertJid = tempList1.get(i).getExpertAccount() + "@" + host;
			presence = roster.getPresence(expertJid);
			if (presence.isAvailable()) {
				expertList.add(tempList1.get(i));
			} else {
				tempList.add(tempList1.get(i));
			}
		}
		expertList.addAll(tempList);
		contactAdapter = new ContacterAdapter(MContext, expertList);
		contacterList.setAdapter(contactAdapter);

		searchInputView1 = (EditText) view.findViewById(R.id.search_input1);
		searchInputView1.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String str = searchInputView.getText().toString();
				if (str.equals("")) {
					contacterList.setVisibility(View.VISIBLE);
					groupSearchLisview.setVisibility(View.GONE);
				}

			}
		});

		searchView1 = (ImageView) view.findViewById(R.id.search_img1);
		searchView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String searchExpertName = searchInputView1.getText().toString();
				if (searchExpertName != null && !searchExpertName.equals("")) {
					List<Friends> list = new ArrayList<Friends>();
					Expert expert = new Expert();
					Friends friends = null;
					for (int j = 0; j < expertList.size(); j++) {
						expert = expertList.get(j);
						String expertName = expert.getExpertName();
						String jid = expert.getExpertAccount();
						if (expertName.contains(searchExpertName)
								|| jid.contains(searchExpertName)) {
							friends = new Friends();
							friends.setName(expertName);
							friends.setJID(expert.getExpertAccount());
							list.add(friends);
						}
					}
					for (int i = 0; i < list.size(); i++) {
						String temp1 = (String) list.get(i).getName();
						for (int j = i + 1; j < list.size(); j++) {
							String temp2 = (String) list.get(j).getName();
							if (temp1.equals(temp2)) {
								list.remove(j);
							}
						}
					}
					System.out.println("list的长度:" + list.size());
					searchAdapter2 = new searchAdapter(list);
					groupSearchLisview.setAdapter(searchAdapter2);
					groupSearchLisview.setVisibility(View.VISIBLE);
					contacterList.setVisibility(View.GONE);
					if (list.size() > 0) {
					} else {
						String text = "没有搜索到相关信息";
						Toast.makeText(MContext, text, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});

		contacterList.setVisibility(View.VISIBLE);
		groupSearchLisview = (ListView) view.findViewById(R.id.search_list_21);
		groupSearchLisview.setVisibility(View.GONE);
		groupSearchLisview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				ImageView img = (ImageView) v.findViewById(R.id.userAvatar_1);
				TextView jid = (TextView) v.findViewById(R.id.userID_1);
				TextView name = (TextView) v.findViewById(R.id.userName_1);
				Friends friends = new Friends();
				friends.setJID(jid.getText().toString());
				friends.setName(name.getText().toString());
				Intent intent = new Intent(MContext, ChatLayoutActivity.class);
				Bundle bd = new Bundle();
				bd.putString("style", "to");
				bd.putParcelable("friends", friends);
				intent.putExtra("info", bd);
				startActivityForResult(intent, 11);
			}
		});

		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		mPullDownView.enableAutoFetchMore(true, 1);
		// 隐藏 并禁用尾部
		mPullDownView.setHideFooter();
		// 显示并启用自动获取更多
		// mPullDownView.setShowFooter();
		// 隐藏并且禁用头部刷新
		mPullDownView.setHideHeader();
		// 显示并且可以使用头部刷新
		mPullDownView.setShowHeader();
		// 加载数据 本类使用
		if (expertList.size() <= 0) {
			loadData(0);
		}
	}

	private void loadData(final int i) {
		final List<Expert> list = new ArrayList<Expert>();
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(ChatBriefActivity.this,
				ChatBriefActivity.this);
		commandBase.request(new TaskListener() {
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
								ChatBriefActivity.this).getRoster();
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
						friends.setStatus(state);
						friends.setAvailable(presence.isAvailable());
						// if(userType.equals("farmer") &&
						// !productDb.isExpertSaved(expert,userInfo.getAccount())){
						if (userType.equals("user_admin")) {
							System.out.println("11");
							if (!productDb.isExpertSaved(expert,
									userInfo.getAccount())) {
								System.out.println("22");
								/*
								 * if(i == 1){//刷新 System.out.println("33");
								 */
								try {
									createSubscriber(jid,
											expert.getExpertName(),
											new String[] { "专家" });
									System.out.println("44");
									ContacterManager.contacters.put(jid,
											friends);
									System.out.println("55");
									productDb.saveExpert(expert,
											userInfo.getAccount());
									System.out.println("66");
									list.add(expert);
									System.out.println("77");
								} catch (XMPPException e) {
									System.out.println("88");
									e.printStackTrace();
									Toast.makeText(ChatBriefActivity.this,
											"添加专家出错了", Toast.LENGTH_SHORT)
											.show();
								}
								/*
								 * }else{//下载
								 * ContacterManager.contacters.put(jid,
								 * friends);
								 * productDb.saveExpert(expert,userInfo
								 * .getAccount()); list.add(expert); }
								 */
							} else {
								productDb.updateExperts(expert,
										userInfo.getAccount());
							}
						}
						// if(!userType.equals("farmer") &&
						// !productDb.isFarmerSaved(expert,userInfo.getAccount())){
						if (!userType.equals("user_admin")) {
							if (!productDb.isFarmerSaved(expert,
									userInfo.getAccount())) {
								// if(i == 1){
								try {
									System.out.println("1111");
									createSubscriber(jid,
											expert.getExpertName(),
											new String[] { "农户" });
									System.out.println("2222");
									ContacterManager.contacters.put(jid,
											friends);
									System.out.println("3333");
									productDb.saveFarmer(expert,
											userInfo.getAccount());
									System.out.println("4444");
									list.add(expert);
									System.out.println("5555");
								} catch (XMPPException e) {
									Toast.makeText(ChatBriefActivity.this,
											"添加农户出错了", Toast.LENGTH_SHORT)
											.show();
									e.printStackTrace();
								}
								/*
								 * }else{ ContacterManager.contacters.put(jid,
								 * friends);
								 * productDb.saveFarmer(expert,userInfo
								 * .getAccount()); list.add(expert); }
								 */
							} else {
								productDb.updateFarmers(expert,
										userInfo.getAccount());
							}
						}
					}
					Message message = null;
					if (i == 0) {
						expertList = new ArrayList<Expert>();
						List<Expert> tempList = new ArrayList<Expert>();
						Roster roster = ConnectionUtils.getConnection(
								ChatBriefActivity.this).getRoster();
						String host = CommandBase.instance().getHost();
						host = host.substring(0, host.indexOf(":"));
						System.out.println("host=" + host);
						String expertJid = "";
						org.jivesoftware.smack.packet.Presence presence = roster
								.getPresence(expertJid);
						for (int i = 0; i < list.size(); i++) {
							expertJid = list.get(i).getExpertAccount() + "@"
									+ host;
							presence = roster.getPresence(expertJid);
							if (presence.isAvailable()) {
								expertList.add(list.get(i));
							} else {
								tempList.add(list.get(i));
							}
						}
						expertList.addAll(tempList);
						ContacterManager.setContacters(MContext, expertList);
						contactAdapter = new ContacterAdapter(MContext,
								expertList);
						contacterList.setAdapter(contactAdapter);
						mPullDownView.RefreshComplete();
						mPullDownView
								.setOnPullDownListener(new OnPullDownListener() {
									public void onRefresh() {
										loadData(1);
									}

									@Override
									public void onMore() {

									}
								});
						/* mListView.setAdapter(mAdapter); */
						// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
						mPullDownView.enableAutoFetchMore(true, 1);
						// 隐藏 并禁用尾部
						mPullDownView.setHideFooter();
						// 显示并启用自动获取更多
						// mPullDownView.setShowFooter();
						// 隐藏并且禁用头部刷新
						mPullDownView.setHideHeader();
						// 显示并且可以使用头部刷新
						mPullDownView.setShowHeader();
						// 加载数据 本类使用
					}
					if (i == 1) {
						mPullDownView.RefreshComplete();
						message = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
						message.obj = list;
						message.sendToTarget();
					}
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
				contactAdapter.notifyDataSetChanged();
				break;
			}
			case WHAT_DID_REFRESH: {
				List<Expert> tempList = new ArrayList<Expert>();
				List<Expert> tempList1 = new ArrayList<Expert>();
				if (userType.equals("user_admin")) {
					tempList1 = productDb.getAllExperts(userInfo.getAccount());
				} else {
					tempList1 = productDb.getAllFarmers(userInfo.getAccount());
				}
				expertList = new ArrayList<Expert>();
				Roster roster = ConnectionUtils.getConnection(
						ChatBriefActivity.this).getRoster();
				String host = CommandBase.instance().getHost();
				host = host.substring(0, host.indexOf(":"));
				System.out.println("host=" + host);
				String expertJid = "";
				org.jivesoftware.smack.packet.Presence presence = roster
						.getPresence(expertJid);
				for (int i = 0; i < tempList1.size(); i++) {
					expertJid = tempList1.get(i).getExpertAccount() + "@"
							+ host;
					presence = roster.getPresence(expertJid);
					if (presence.isAvailable()) {
						expertList.add(tempList1.get(i));
					} else {
						tempList.add(tempList1.get(i));
					}
				}
				expertList.addAll(tempList);
				contactAdapter = new ContacterAdapter(MContext, expertList);
				contacterList.setAdapter(contactAdapter);
				break;
			}
			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			this.setResult(RESULT_OK, intent);
			this.finish();
			break;
		case R.id.bt_right:
			Intent i1 = new Intent();
			i1.setClass(MContext, AddFriendsActivity.class);
			i1.putExtra("tab", tabHost.getCurrentTab());
			startActivityForResult(i1, 12);
			break;
		case R.id.search_img:
			String friends = searchInputView.getText().toString();
			if (friends != null && !friends.equals("")) {
				List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map = null;
				for (int i = 0; i < list1.size(); i++) {
					map = new HashMap<String, Object>();
					map = list1.get(i);
					String friendsName = map.get("id").toString();
					String name = map.get("nickname").toString();
					if (friendsName.contains(friends) || name.contains(friends)) {
						list.add(map);
					}
				}
				for (int i = 0; i < list.size(); i++) {
					String temp1 = (String) list.get(i).get("id");
					for (int j = i + 1; j < list.size(); j++) {
						String temp2 = (String) list.get(j).get("id");
						if (temp1.equals(temp2)) {
							list.remove(j);
						}
					}
				}
				System.out.println("list的长度:" + list.size());
				searchAdapter = new RecentlyAdapter(list, MContext);
				xlistView.setVisibility(View.GONE);
				recordSearchListView.setAdapter(searchAdapter);
				recordSearchListView.setVisibility(View.VISIBLE);
				if (list.size() > 0) {
				} else {
					String text = "没有搜索到相关信息";
					Toast.makeText(MContext, text, Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 回复一个presence信息给用户
	 * 
	 * @param type
	 * @param to
	 */
	protected void sendSubscribe(Presence.Type type, String to) {
		Presence presence = new Presence(type);
		presence.setTo(to);
		ConnectionUtils.getConnection(MContext).sendPacket(presence);
	}

	public class RecentlyAdapter extends BaseAdapter {
		private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		private Context context = null;

		public RecentlyAdapter(List<HashMap<String, Object>> list,
				Context context) {
			super();
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			HashMap<String, Object> map1 = list.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.user_item,
						null);
				holder.userAvatar = (ImageView) convertView
						.findViewById(R.id.userAvatar);
				holder.unReadCount = (TextView) convertView
						.findViewById(R.id.messageCount);
				holder.userID = (TextView) convertView
						.findViewById(R.id.userID);
				holder.userName = (TextView) convertView
						.findViewById(R.id.userName);
				holder.userRecentMessage = (TextView) convertView
						.findViewById(R.id.userRecentMessage);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			System.out.println("Integer.parseInt(''+map1.get('unRead'))="
					+ Integer.parseInt("" + map1.get("unRead")));
			if (Integer.parseInt("" + map1.get("unRead")) <= 0) {
				holder.unReadCount.setVisibility(View.INVISIBLE);
			} else {
				holder.unReadCount.setVisibility(View.VISIBLE);
				holder.unReadCount.setText(map1.get("unRead").toString());
			}
			String id = map1.get("id").toString();
			id = id.substring(0, id.indexOf("@"));
			// 显示用户昵称
			String nickName = map1.get("nickname").toString();
			/*
			 * if(userType.equals("farmer")){ nickName =
			 * productDb.getExpertNickName(id, userInfo.getAccount()); }else{
			 * nickName = productDb.getFarmerNickName(id,
			 * userInfo.getAccount()); }
			 */
			// holder.userID.setText(id);
			holder.userID.setText(nickName);
			holder.userName.setText("");
			String zhengze = "f0[0-9]{2}|f10[0-7]";
			try {
				System.out.println("000");
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(context, "" + map1.get("words"),
								zhengze);
				holder.userRecentMessage.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			holder.time.setText("" + map1.get("time"));
			return convertView;
		}

		public class ViewHolder {
			public ImageView userAvatar;
			public TextView unReadCount;
			public TextView userID, userName;
			public TextView userRecentMessage;
			public TextView time;
		}
	}

	public class searchAdapter extends BaseAdapter {
		List<Friends> friendLists = null;

		public searchAdapter(List<Friends> friendLists) {
			super();
			this.friendLists = friendLists;
		}

		@Override
		public int getCount() {
			return friendLists.size();
		}

		@Override
		public Object getItem(int arg0) {
			return friendLists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			convertView = ChatBriefActivity.this.getLayoutInflater().inflate(
					R.layout.group_item, null);
			ImageView imageView1 = (ImageView) convertView
					.findViewById(R.id.userAvatar_1);
			TextView textView1 = (TextView) convertView
					.findViewById(R.id.userName_1);
			TextView textView2 = (TextView) convertView
					.findViewById(R.id.userID_1);
			TextView textView3 = (TextView) convertView
					.findViewById(R.id.state_and_words);
			ImageView imageView2 = (ImageView) convertView
					.findViewById(R.id.login_where);
			Friends friends = friendLists.get(arg0);
			System.out.println("friendsJid=" + friends.getJID());
			if (friends.isAvailable()) {
				textView1.setText(friends.getName());
				textView2.setText(friends.getJID());
				textView3.setText("[" + friends.getStatus() + "]");
			} else {
				textView1.setText(friends.getName());
				String ids = friends.getJID();
				if (ids.contains("@")) {
					ids = ids.substring(0, ids.indexOf("@"));
				}
				textView2.setText(ids);
				textView3.setText("[" + "离开" + "]");
				imageView2.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

	}

	private class ContacterAdapter extends BaseAdapter {
		private Context context;
		private List<Expert> expertList = null;

		public ContacterAdapter(Context context, List<Expert> expertList) {
			super();
			this.context = context;
			this.expertList = expertList;
		}

		@Override
		public int getCount() {
			return expertList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return expertList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			convertView = ChatBriefActivity.this.getLayoutInflater().inflate(
					R.layout.group_item, null);
			ImageView imageView1 = (ImageView) convertView
					.findViewById(R.id.userAvatar_1);
			TextView textView1 = (TextView) convertView
					.findViewById(R.id.userName_1);
			TextView textView2 = (TextView) convertView
					.findViewById(R.id.userID_1);
			TextView textView3 = (TextView) convertView
					.findViewById(R.id.state_and_words);
			ImageView imageView2 = (ImageView) convertView
					.findViewById(R.id.login_where);
			Expert expert = expertList.get(arg0);
			System.out.println("expert=" + expert);
			String name = expert.getExpertName();
			System.out.println("expertName=" + name);
			if (name.contains("@")) {
				name = name.substring(0, name.indexOf("@"));
				System.out.println("处理后的name：" + name);
			}
			String id = expert.getExpertAccount();
			textView2.setText(id);
			textView1.setText(name);
			Roster roster = ConnectionUtils.getConnection(
					ChatBriefActivity.this).getRoster();
			String host = CommandBase.instance().getHost();
			host = host.substring(0, host.indexOf(":"));
			System.out.println("host=" + host);
			String expertJid = expert.getExpertAccount() + "@" + host;
			System.out.println("expertJid=" + expertJid);
			org.jivesoftware.smack.packet.Presence presence = roster
					.getPresence(expertJid);
			if (presence.isAvailable()) {
				textView3.setText("[在线]");
				textView3.setTextColor(ChatBriefActivity.this.getResources()
						.getColor(R.color.green));
			} else {
				textView3.setText("[" + "离开" + "]");
				imageView2.setVisibility(View.INVISIBLE);
			}
			convertView.setOnClickListener(contacterOnClick);
			return convertView;
		}
	}

	public static int retrieveState_mode(Mode userMode, boolean isOnline) {
		int userState = 0;
		/** 0 for offline, 1 for online, 2 for away,3 for busy */
		if (userMode == Mode.dnd) {
			userState = 3;
		} else if (userMode == Mode.away || userMode == Mode.xa) {
			userState = 2;
		} else if (isOnline) {
			userState = 1;
		}
		return userState;
	}

	private List<HashMap<String, Object>> updateView() {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String str1 = CommandBase.instance().getHost();
		str1 = str1.substring(0, str1.indexOf(":"));
		System.out.println("str1=" + str1);
		List<String> testlst = messageDb.getAllFriends(userInfo.getAccount(),
				userInfo.getAccount() + "@" + str1);
		System.out.println("testlstSize=" + testlst.size());
		for (int i = 0; i < testlst.size(); i++) {
			System.out.println(testlst.get(i));
		}
		List<IMMessage> listmsg = new ArrayList<IMMessage>();
		IMMessage msg = null;
		msgList = new ArrayList<IMMessage>();
		for (int i = 0; i < testlst.size(); i++) {
			System.out.println(testlst.get(i));
			String temp = testlst.get(i);
			System.out.println("temp=" + temp);
			temp = temp.substring(0, temp.indexOf("@"));
			msg = new IMMessage();
			listmsg = messageDb
					.getAllMessage(userInfo.getAccount(), temp, null);
			if (listmsg.size() > 0) {
				msg = listmsg.get(listmsg.size() - 1);
				msgList.add(msg);
				System.out.println("msg=" + msg.toString());
			}
			int unReadNum = messageDb.getUnreadMessageNum(
					userInfo.getAccount(), temp, 0);
			map = new HashMap<String, Object>();
			map.put("img", R.drawable.default_avatar);
			System.out.println("未读消息数：" + unReadNum);
			map.put("unRead", unReadNum);
			map.put("id", testlst.get(i));
			String nickName = "";
			String id = testlst.get(i);
			id = id.substring(0, id.indexOf("@"));
			if (userType.equals("user_admin")) {
				nickName = productDb.getExpertNickName(id,
						userInfo.getAccount());
			} else {
				nickName = productDb.getFarmerNickName(id,
						userInfo.getAccount());
			}
			map.put("nickname", nickName);
			String tempStr = msg.getContent();
			System.out.println("服务器最新消息为：" + tempStr);
			System.out.println("服务器消息模式为：" + msg.getChatMode());
			int chatMode = msg.getChatMode();
			if (tempStr != null && !tempStr.equals("")) {
				if (tempStr.startsWith(";")) {
					chatMode = Integer.parseInt(tempStr.substring(1, 2));
					tempStr = tempStr.substring(2);
				}
				if (chatMode == 0) {
					map.put("words", tempStr);
				}
				if (chatMode == 1) {
					map.put("words", "[图片]");
				}
				if (chatMode == 2) {
					map.put("words", "[文件]");
				}
				if (chatMode == 3) {
					map.put("words", "[语音]");
				}
				if (chatMode == 5) {
					map.put("words", "添加好友请求");
				}
			}
			map.put("time", msg.getTime());
			list.add(map);
			HashMap<String, Object> tempMap = null;
			for (int j = 0; j < list.size() - 1; j++) {
				for (int k = j + 1; k < list.size(); k++) {
					if (list.get(j).get("time").toString()
							.compareTo(list.get(k).get("time").toString()) < 0) {
						tempMap = new HashMap<String, Object>();
						tempMap = list.get(j);
						list.remove(j);
						list.add(j, list.get(k - 1));
						list.remove(k);
						list.add(k, tempMap);
					}
				}
			}

		}

		return list;
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		receiver = new ContacterReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);

		filter.addAction(Constant.ROSTER_ADDED);
		filter.addAction(Constant.ROSTER_DELETED);
		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
		filter.addAction(Constant.ROSTER_UPDATED);
		// 好友请求
		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
		registerReceiver(receiver, filter);
		super.onResume();
	}

	private class ContacterReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
				Friends friends = intent.getParcelableExtra(Friends.userKey);

				NotificationManager mgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification nt = new Notification();
				nt.defaults = Notification.DEFAULT_SOUND;
				int soundId = new Random(System.currentTimeMillis())
						.nextInt(Integer.MAX_VALUE);
				mgr.notify(soundId, nt);
				if (tabHost.getCurrentTab() == 1) {
					refreshList(friends);
				}
			} else if (Constant.NEW_MESSAGE_ACTION.equals(intent.getAction())) {
				Friends friends = intent.getParcelableExtra(Friends.userKey);

				NotificationManager mgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification nt = new Notification();
				nt.defaults = Notification.DEFAULT_SOUND;
				int soundId = new Random(System.currentTimeMillis())
						.nextInt(Integer.MAX_VALUE);
				mgr.notify(soundId, nt);
				System.out.println("hhahahahhahah");

				IMMessage message = intent
						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
				System.out.println("Achat---" + message.getChatMode());
				message.setUnReadCount(0);
				String str = message.getFromSubJid();
				str = str.substring(0, str.indexOf("@"));

				System.out.println("msgType=" + message.getMsgType());

				messageDb.saveMessage(message, str, userInfo.getAccount());
				unreadMessageNum = messageDb.getUnreadMessageNum(
						userInfo.getAccount(), "", 0);
				if (unreadMessageNum == 0) {
					unreadNum.setVisibility(View.GONE);
				} else {
					unreadNum.setText("" + unreadMessageNum);
					unreadNum.setVisibility(View.VISIBLE);
				}

				if (tabHost.getCurrentTab() == 0) {
					list1 = updateView();
					radapter = new RecentlyAdapter(list1, MContext);
					xlistView.setAdapter(radapter);
				}
			}

		}
	}

	/**
	 * 刷新当前的列表
	 */
	private void refreshList(Friends friends) {
		int j = -1;
		Expert tempExpert = new Expert();
		String account = friends.getJID();
		account = account.substring(0, account.indexOf("@"));
		tempExpert.setExpertAccount(account);
		String nickName = friends.getName();
		if ("".equals(nickName)) {
			nickName = account;
		}
		tempExpert.setExpertName(nickName);

		if (friends != null) {
			String jid = friends.getJID();
			System.out.println("jid=" + jid);
			jid = jid.substring(0, jid.indexOf("@"));
			System.out.println("处理后的jid:" + jid);
			for (int i = 0; i < expertList.size(); i++) {
				if (expertList.get(i).getExpertAccount().equals(jid)) {
					j = i;
					expertList.get(i).setExpertDescription(
							friends.isAvailable() + "");
					tempExpert = expertList.get(i);
				}
			}
			if (j != -1) {
				expertList.remove(j);
			}
			if (friends.isAvailable()) {
				expertList.add(tempExpert);
			} else {
				expertList.add(0, tempExpert);
			}
		}
		contactAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加一个联系人
	 * 
	 * @param userJid
	 *            联系人JID
	 * @param nickname
	 *            联系人昵称
	 * @param groups
	 *            联系人添加到哪些组
	 * @throws XMPPException
	 */
	protected void createSubscriber(String userJid, String nickname,
			String[] groups) throws XMPPException {
		ConnectionUtils.getConnection(MContext).getRoster()
				.createEntry(userJid, nickname, groups);
	}

	private OnClickListener contacterOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ImageView img = (ImageView) v.findViewById(R.id.userAvatar_1);
			TextView jid = (TextView) v.findViewById(R.id.userID_1);
			TextView name = (TextView) v.findViewById(R.id.userName_1);
			Friends friends = new Friends();
			friends.setJID(jid.getText().toString());
			friends.setName(name.getText().toString());
			Intent intent = new Intent(MContext, ChatLayoutActivity.class);
			Bundle bd = new Bundle();
			bd.putString("style", "to");
			bd.putParcelable("friends", friends);
			intent.putExtra("info", bd);
			startActivityForResult(intent, 11);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 10:
				list1 = updateView();
				radapter = new RecentlyAdapter(list1, MContext);
				xlistView.setAdapter(radapter);
				recordSearchListView.setVisibility(View.GONE);
				xlistView.setVisibility(View.VISIBLE);
				unreadMessageNum = messageDb.getUnreadMessageNum(
						userInfo.getAccount(), "", 0);
				if (unreadMessageNum == 0) {
					unreadNum.setVisibility(View.GONE);
				} else {
					unreadNum.setText("" + unreadMessageNum);
					unreadNum.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int i = tabHost.getCurrentTab();
			if (i == 0) {
				if (recordSearchListView.getVisibility() == 0) {
					recordSearchListView.setVisibility(View.GONE);
					xlistView.setVisibility(View.VISIBLE);
				} else {
					ChatBriefActivity.this.finish();
				}
			} else {
				if (groupSearchLisview.getVisibility() == 0) {
					groupSearchLisview.setVisibility(View.GONE);
					contacterList.setVisibility(View.VISIBLE);
				} else {
					ChatBriefActivity.this.finish();
				}
			}
		}
		return false;
	}
}
