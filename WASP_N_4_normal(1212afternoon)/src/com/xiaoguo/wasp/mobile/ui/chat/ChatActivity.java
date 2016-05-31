package com.xiaoguo.wasp.mobile.ui.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
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
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.User;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.utils.ExpressionUtil;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.widget.NPullToFreshContainer;
import com.xiaoguo.wasp.mobile.widget.XListView;
import com.xiaoguo.wasp.mobile.widget.NPullToFreshContainer.OnContainerRefreshListener;
import com.xiaoguo.wasp.mobile.widget.XListView.IXListViewListener;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager.MRosterGroup;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ChatActivity extends TabActivity implements OnClickListener
{
	private TextView titleView;
	private Button leftBtView;
	private Button rightView;
	UserSettingInfo userInfo = null;
	ProductDb productDb=null;
	
	View view;
	TextView unreadNum;
	TextView text;
	ImageView img;
	View view1;
	TextView unreadNum1;
	TextView text1;
	ImageView img1;
	
	TabWidget tabWidget=null;
	TabHost tabHost = null;
	
	private IMMessageDb messageDb=null;
	
	XListView xlistView;
	RecentlyAdapter radapter;
	RecentlyAdapter searchAdapter;
	searchAdapter searchAdapter2;
	List<IMMessage> msgList=null;
	List<IMMessage> msgList2=null;
	String subFrom = "";
	Context MContext=null;
	ArrayList<User> userList;
	
	private ExpandableListView contacterList = null;
	private ContacterExpandAdapter expandAdapter = null;
	private ListView inviteList = null;
	private InviteAdapter inviteAdapter = null;
	private List<Friends> inviteUsers = new ArrayList<Friends>();
	
	private ListView recordSearchListView=null;
	private ListView groupSearchLisview=null;
	List<MRosterGroup> groupLists=null;
	List<HashMap<String, Object>> list1=null;
	HashMap<String, Object> map = null;
	
	protected View layoutLoad;
	protected TextView promptInfo;
	protected ProgressBar loading;
	
	private EditText searchInputView;
	private ImageView searchView;
	
	private EditText searchInputView1;
	private ImageView searchView1;
	
	private NPullToFreshContainer iPulltoRefresh;
    ScrollView mScrollView;
    
    private ContacterReceiver receiver = null;
    private List<MRosterGroup> groups=null;
	
    int unreadMessageNum=0;
    LinearLayout layout = null;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {     
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();     
			StrictMode.setThreadPolicy(policy);
		}
		
		WASPApplication.getInstance().addActivity(this);
		
		messageDb = new IMMessageDb(ChatActivity.this);
		userInfo = new UserSettingInfo(this);
		productDb = new ProductDb(this);
		unreadMessageNum = messageDb.getUnreadMessageNum(userInfo.getAccount(), "", 0);
		groupLists = new ArrayList<ContacterManager.MRosterGroup>();
		MContext = ChatActivity.this;
		
		setUpView();
	}

	private void setUpView() {
		titleView = (TextView)findViewById(R.id.title);
		leftBtView = (Button)findViewById(R.id.bt_left);
		leftBtView.setVisibility(View.VISIBLE);
		leftBtView.setOnClickListener(this);
		
		rightView = (Button)findViewById(R.id.bt_right);
		rightView.setBackgroundResource(R.drawable.btn_add);
		rightView.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(this);
		
		view = (View) LayoutInflater.from(this).inflate(R.layout.mytabhost, null);  
		unreadNum = (TextView) view.findViewById(R.id.unreadMessageNum);
		text = (TextView)view.findViewById(R.id.text);
		img = (ImageView)view.findViewById(R.id.item);
		if(unreadMessageNum==0){
			unreadNum.setVisibility(View.GONE);
		}else{
			unreadNum.setText(""+unreadMessageNum);
			unreadNum.setVisibility(View.VISIBLE);
		}
		text.setText("�����¼");
		text.setTextSize(14);
		text.setTextColor(getResources().getColor(R.color.text));
		img.setImageResource(R.drawable.record_selector);
	  
		view1 = (View) LayoutInflater.from(this).inflate(R.layout.mytabhost, null);  
		 unreadNum1 = (TextView) view1.findViewById(R.id.unreadMessageNum);
	  	text1 = (TextView)view1.findViewById(R.id.text);
	  	img1 = (ImageView)view1.findViewById(R.id.item);
	  	unreadNum1.setVisibility(View.GONE);
	  	text1.setText("ͨѶ¼");
	  	text1.setTextSize(14);
	  	text1.setTextColor(getResources().getColor(R.color.text));
	  	img1.setImageResource(R.drawable.contact_selector);
		
		tabHost = this.getTabHost();
		//3����3�����ϵ�д��
		tabHost.addTab(tabHost.newTabSpec("�����¼")  
				.setIndicator(view)  
				  .setContent(R.id.chat_record));  
		tabHost.addTab(tabHost.newTabSpec("ͨѶ¼")  
				 .setIndicator(view1)  
				 .setContent(R.id.chat_group));  
		tabWidget = tabHost.getTabWidget();
		layout = (LinearLayout)tabHost.getChildAt(0);
		System.out.println("���ݣ�"+tabHost.getChildCount());
		init1(layout);
		init2(layout);
		int i=tabHost.getCurrentTab();
		if(i==0){
			titleView.setText("�����¼");
		}
		if(i==1){
			titleView.setText("ͨѶ¼");
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {		
			@Override
			public void onTabChanged(String tabId) {
				if(tabId.equals("�����¼")){
					titleView.setText("�����¼");
					init1(layout);
				}else{
					titleView.setText("ͨѶ¼");
					init2(layout);
				}
			}
		});	
		
	}

	private void init1(LinearLayout view) {
		messageDb = new IMMessageDb(ChatActivity.this);
		userInfo = new UserSettingInfo(ChatActivity.this);
		
		xlistView = (XListView)view.findViewById(R.id.list);
		xlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final IMMessage teImMessage = msgList.get(arg2-1);
				TextView id = (TextView)arg1.findViewById(R.id.userID);
				TextView textView = (TextView)arg1.findViewById(R.id.userRecentMessage);
				if(textView.getText().toString().equals("��Ӻ�������")){
					subFrom = id.getText().toString();
					System.out.println("subForm="+subFrom);
					new AlertDialog.Builder(MContext)
							.setMessage( subFrom+ "���������Ϊ����")
							.setTitle("��ʾ")
							.setPositiveButton("���",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											// ��������
											sendSubscribe(Presence.Type.subscribed,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0������ӳɹ�");
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatActivity.this);
											 xlistView.setAdapter(radapter);
											removeInviteUser(subFrom);
										}
									})
							.setNegativeButton("�ܾ�",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											sendSubscribe(Presence.Type.unsubscribe,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0��Ӻ��������Ѿܾ�");
											list1 = updateView();
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatActivity.this);
											 xlistView.setAdapter(radapter);
											removeInviteUser(subFrom);
										}
									}).show();
				}else{
					map = list1.get(arg2-1);
					String jid = ""+map.get("id");
					jid = jid.substring(0,jid.indexOf("@"));
					System.out.println("�����¼jid="+jid);
					Friends friends = new Friends();
					friends.setJID(jid);
					System.out.println("jid="+jid);
					Intent intent = new Intent();
					intent.setClass(ChatActivity.this, ChatLayoutActivity.class);
					Bundle bd = new Bundle();
					bd.putString("style","from");
					bd.putParcelable("friends", friends);
					intent.putExtra("info", bd);
					startActivityForResult(intent, 10);
				}
			}
		});
		
		xlistView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				status = LoadStatus.loadRefresh;
				requestData();
			}
			@Override
			public void onLoadMore() {
				status = LoadStatus.loadLoadMore;
				requestData();
			}
		});
		xlistView.setPullRefreshEnable(true);
		// Need not to load more.
		xlistView.setPullLoadEnable(false);
		
		searchInputView = (EditText)view.findViewById(R.id.search_input);
		searchInputView.addTextChangedListener(new TextWatcher(){
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
		
		searchView = (ImageView)view.findViewById(R.id.search_img);
		searchView.setOnClickListener(this);
		
		recordSearchListView = (ListView)view.findViewById(R.id.search_list_1);
		recordSearchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final IMMessage teImMessage = msgList.get(arg2-1);
				System.out.println("tempmsg="+teImMessage);
				TextView view = (TextView)arg1.findViewById(R.id.userID);
				TextView textView = (TextView)arg1.findViewById(R.id.userRecentMessage);
				if(textView.getText().toString().equals("��Ӻ�������")){
					subFrom = view.getText().toString();
					new AlertDialog.Builder(MContext)
							.setMessage(subFrom + "���������Ϊ����")
							.setTitle("��ʾ")
							.setPositiveButton("���",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											// ��������
											sendSubscribe(Presence.Type.subscribed,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0������ӳɹ�");
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatActivity.this);
											 xlistView.setAdapter(radapter);
											removeInviteUser(subFrom);
										}
									})
							.setNegativeButton("�ܾ�",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											sendSubscribe(Presence.Type.unsubscribe,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0��Ӻ��������Ѿܾ�");
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatActivity.this);
											 xlistView.setAdapter(radapter);
											removeInviteUser(subFrom);
										}
									}).show();
				}else{
					String jid = view.getText().toString();
					jid = jid.substring(0, jid.indexOf("@"));
					System.out.println("jid="+jid);
					Friends friends = new Friends();
					friends.setJID(jid);
					Intent intent = new Intent();
					intent.setClass(ChatActivity.this, ChatLayoutActivity.class);
					Bundle bd = new Bundle();
					bd.putString("style","from");
					bd.putParcelable("friends", friends);
					intent.putExtra("info", bd);
					startActivityForResult(intent, 10);
				}
			}
		});
		
		// ר��Ϊ����
		layoutLoad = view.findViewById(R.id.layout_load);
		promptInfo = (TextView)view.findViewById(R.id.prompt_info);
		loading = (ProgressBar)view.findViewById(R.id.loading);
		promptInfo.setOnClickListener(this);
		
		userList = new ArrayList<User>();
		list1 = updateView();
		
		xlistView.setVisibility(View.VISIBLE);
		recordSearchListView.setVisibility(View.GONE);
		
		radapter = new RecentlyAdapter(list1, ChatActivity.this);
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
	private void requestData() {
		if (!(status == LoadStatus.loadLoadMore) && !(status == LoadStatus.loadRefresh)) {
			status = LoadStatus.loading;
			loadViewDisplay();
		}
		//��ȡ�б�������Ӧ��״̬
		userList.add(new User("hca","123","456","url","������"));
		userList.add(new User("hca","123","456","url","������"));
		userList.add(new User("hca","123","456","url","������"));
		loadEnd();
		status = LoadStatus.loadSucceed;
		loadViewDisplay();
		radapter.notifyDataSetChanged();
	}
	private void loadViewDisplay() {
		xlistView.setVisibility(status==LoadStatus.loadSucceed?View.VISIBLE:View.INVISIBLE);
		promptInfo.setVisibility(status==LoadStatus.loadError?View.VISIBLE:View.INVISIBLE);
		loading.setVisibility(status==LoadStatus.loading?View.VISIBLE:View.INVISIBLE);
		layoutLoad.setVisibility((status==LoadStatus.loadError||status==LoadStatus.loading)?View.VISIBLE:View.INVISIBLE);
	}
	protected void loadEnd() {
		xlistView.stopRefresh();
		xlistView.stopLoadMore();
	}
private void init2(final LinearLayout view) {
	groupLists = new ArrayList<ContacterManager.MRosterGroup>();
	searchInputView1 = (EditText)view.findViewById(R.id.search_input1);
	searchInputView1.addTextChangedListener(new TextWatcher(){
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
	
	searchView1 = (ImageView)view.findViewById(R.id.search_img1);
	searchView1.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			String friends = searchInputView1.getText().toString();
			if(friends!=null&&!friends.equals("")){
					List<Friends> list = new ArrayList<Friends>();
					for(int j=0;j<groupLists.size();j++){
						map = new HashMap<String, Object>();
						MRosterGroup tempGroup = groupLists.get(j);
						List<Friends> tempFriends = tempGroup.getFriends();
						Friends friends2 = null;
						for(int k=0;k<tempFriends.size();k++){
							friends2 = tempFriends.get(k);
							String friendsName = friends2.getName();
							if(friendsName.contains(friends)){
								list.add(friends2);
							}
						}
					}
					for(int i=0;i<list.size();i++){
						String temp1 = (String) list.get(i).getName();
						for(int j=i+1;j<list.size();j++){
							String temp2 = (String) list.get(j).getName();
							if(temp1.equals(temp2)){
								list.remove(j);
							}
						}
					}
					System.out.println("list�ĳ���:"+list.size());
					searchAdapter2 = new searchAdapter(list);
					groupSearchLisview.setAdapter(searchAdapter2);
					groupSearchLisview.setVisibility(View.VISIBLE);
					contacterList.setVisibility(View.GONE);
					if(list.size()>0){
					}else{
						String text = "û�������������Ϣ";
						Toast.makeText(ChatActivity.this, text, Toast.LENGTH_SHORT).show();
					}
			}
		}
	});
	
	msgList2 = new ArrayList<IMMessage>();
	iPulltoRefresh =(NPullToFreshContainer)view.findViewById(R.id.pulltofresh);
	iPulltoRefresh.setOnRefreshListener(new OnContainerRefreshListener() {
		@Override
		public void onContainerRefresh() {
			contacterList = (ExpandableListView) view.findViewById(R.id.main_expand_list);
			//ȡ��ͼ��
			contacterList.setGroupIndicator(null);
				if(!NetWorkDetect.detect(ChatActivity.this)){
					Toast.makeText(ChatActivity.this, "��������ʧ��", Toast.LENGTH_SHORT).show();
				}else{
					groupLists = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatActivity.this)
							.getRoster(),ConnectionUtils.getConnection(ChatActivity.this));
					for(int i = 0;i<groupLists.size();i++){
						MRosterGroup group = groupLists.get(i);
						String groupName = group.getName();
						System.out.println("�������ƣ�"+groupName);
						if(!productDb.groupNameIsInMyContact(userInfo.getAccount(), groupName)){
							productDb.saveGroupName(groupName, userInfo.getAccount());
						}
						List<Friends> friends = group.getFriends();
						Friends teFriends = null;
						for(int j=0;j<friends.size();j++){
							teFriends = friends.get(j);
							System.out.println("����jid��"+teFriends.getJID()+" ����name��"+teFriends.getName());
							if(!productDb.friendIsInMyContact(userInfo.getAccount(), teFriends.getJID())){
								productDb.saveFriends(teFriends, groupName, userInfo.getAccount());
							}
						}
					}
					expandAdapter = new ContacterExpandAdapter(groupLists);
					contacterList.setAdapter(expandAdapter);
				}
			
			inviteList = (ListView) view.findViewById(R.id.main_invite_list);
			inviteAdapter = new InviteAdapter();
			inviteList.setAdapter(inviteAdapter);
			inviteList.setOnItemClickListener(inviteListClick);	
			Date vdate = new Date();
			iPulltoRefresh.onComplete(vdate.toLocaleString());
		}
	});
	
	
	contacterList = (ExpandableListView) view.findViewById(R.id.main_expand_list);
	contacterList.setGroupIndicator(this.getResources().getDrawable(R.drawable.indocator));
	if (!NetWorkDetect.detect(ChatActivity.this)) {
		List<String> groupNames = productDb.getAllGroupName(userInfo.getAccount());
		for(int i=0;i<groupNames.size();i++){
			String groupName = groupNames.get(i);
			List<Friends> friends = productDb.getAllFriends(groupName, userInfo.getAccount());
			MRosterGroup group = new MRosterGroup(groupName, friends);
			groupLists.add(group);
		}
	}else{
		groupLists = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatActivity.this)
				.getRoster(),ConnectionUtils.getConnection(ChatActivity.this));
		for(int i = 0;i<groupLists.size();i++){
			MRosterGroup group = groupLists.get(i);
			String groupName = group.getName();
			System.out.println("������"+groupName);
			if(!productDb.groupNameIsInMyContact(userInfo.getAccount(), groupName)){
				System.out.println("xxx");
				productDb.saveGroupName(groupName, userInfo.getAccount());
				System.out.println("yyy");
			}
			List<Friends> friends = group.getFriends();
			Friends teFriends = null;
			for(int j=0;j<friends.size();j++){
				teFriends = friends.get(j);
				System.out.println("friends��jid��"+teFriends.getJID());
				System.out.println("friends��name��"+teFriends.getName());
				if(!productDb.friendIsInMyContact(userInfo.getAccount(), teFriends.getJID())){
					System.out.println("zzz");
					productDb.saveFriends(teFriends, groupName, userInfo.getAccount());
				}
			}
		}
	}
	expandAdapter = new ContacterExpandAdapter(groupLists);
	contacterList.setAdapter(expandAdapter);

	contacterList.setVisibility(View.VISIBLE);
	groupSearchLisview = (ListView)view.findViewById(R.id.search_list_21);
	groupSearchLisview.setVisibility(View.GONE);
	groupSearchLisview.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v,
				int arg2, long arg3) {
			ImageView img = (ImageView)v.findViewById(R.id.userAvatar_1);
			TextView jid= (TextView) v.findViewById(R.id.userID_1);
			Friends friends = new Friends();
			friends.setJID(jid.getText().toString());
			Intent intent = new Intent(MContext, ChatLayoutActivity.class);
			Bundle bd = new Bundle();
			bd.putString("style","to");
			bd.putParcelable("friends", friends);
			intent.putExtra("info", bd);
			startActivityForResult(intent, 11);
		}
	});
	
	inviteList = (ListView)view.findViewById(R.id.main_invite_list);
	inviteAdapter = new InviteAdapter();
	inviteList.setAdapter(inviteAdapter);
	inviteList.setOnItemClickListener(inviteListClick);
	}

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
		i1.setClass(ChatActivity.this, AddFriendsActivity.class);
		i1.putExtra("tab", tabHost.getCurrentTab());
		startActivityForResult(i1, 12);
		break;
	case R.id.search_img:
	String friends = searchInputView.getText().toString();
	if(friends!=null&&!friends.equals("")){
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map = null;
		for(int i=0;i<list1.size();i++){
			map = new HashMap<String, Object>();
			map = list1.get(i);
			String friendsName = (String) map.get("id");
			if(friendsName.contains(friends)){
				list.add(map);
			}
		}
		for(int i=0;i<list.size();i++){
			String temp1 = (String) list.get(i).get("id");
			for(int j=i+1;j<list.size();j++){
				String temp2 = (String) list.get(j).get("id");
				if(temp1.equals(temp2)){
					list.remove(j);
				}
			}
		}
		System.out.println("list�ĳ���:"+list.size());
		searchAdapter = new RecentlyAdapter(list, ChatActivity.this);
		xlistView.setVisibility(View.GONE);
		recordSearchListView.setAdapter(searchAdapter);
		recordSearchListView.setVisibility(View.VISIBLE);
		if(list.size()>0){
		}else{
			String text = "û�������������Ϣ";
			Toast.makeText(ChatActivity.this, text, Toast.LENGTH_SHORT).show();
		}
	}
	break;
	default:
		break;
	}
}
/**
 * �ظ�һ��presence��Ϣ���û�
 * 
 * @param type
 * @param to
 */
protected void sendSubscribe(Presence.Type type, String to) {
	Presence presence = new Presence(type);
	presence.setTo(to);
	ConnectionUtils.getConnection(ChatActivity.this).sendPacket(presence);
}

/**
 * ��������������ɾ��һ����Ŀ
 * 
 * @param subFrom
 */
private void removeInviteUser(String subFrom) {
	for (Friends user : inviteUsers) {
		if (subFrom.equals(user.getName())) {
			inviteUsers.remove(user);
			for(int i=0;i<msgList2.size();i++){
				if(msgList2.get(i).getFromSubJid().equals(subFrom)){
					msgList2.remove(i);
				}
			}
			break;
		}
	}
	if(tabHost.getCurrentTab()==1){
		inviteAdapter.notifyDataSetChanged();
	}
}

public class RecentlyAdapter extends BaseAdapter {
	private List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
	private Context context=null;
	public RecentlyAdapter(List<HashMap<String, Object>> list, Context context) {
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
				convertView = getLayoutInflater().inflate(R.layout.user_item, null);
				holder.userAvatar = (ImageView)convertView.findViewById(R.id.userAvatar);
				holder.unReadCount = (TextView)convertView.findViewById(R.id.messageCount);
				holder.userID = (TextView)convertView.findViewById(R.id.userID);
				holder.userName = (TextView)convertView.findViewById(R.id.userName);
				holder.userRecentMessage = (TextView)convertView.findViewById(R.id.userRecentMessage);
				holder.time = (TextView)convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {				
				holder = (ViewHolder)convertView.getTag();
			}
			if(Integer.parseInt(""+map1.get("unRead"))<=0){
				holder.unReadCount.setVisibility(View.GONE);
			}else{
				holder.unReadCount.setText(map1.get("unRead")+"");
			}
			String id = map1.get("id")+"";
			id = id.substring(0,id.indexOf("@"));
			holder.userID.setText(id);
			holder.userName.setText("");
			String zhengze = "f0[0-9]{2}|f10[0-7]";
    		try {
				System.out.println("000");
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(context, ""+map1.get("words"), zhengze);
				holder.userRecentMessage.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			holder.time.setText(""+map1.get("time"));
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
	List<Friends> friendLists=null;
	
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
		 	convertView= ChatActivity.this.getLayoutInflater().inflate(R.layout.group_item, null);
			ImageView imageView1 = (ImageView)convertView.findViewById(R.id.userAvatar_1);
			TextView textView1 = (TextView)convertView.findViewById(R.id.userName_1);
			TextView textView2 = (TextView)convertView.findViewById(R.id.userID_1);
			TextView textView3 = (TextView)convertView.findViewById(R.id.state_and_words);
			ImageView imageView2 = (ImageView)convertView.findViewById(R.id.login_where);
			Friends friends = friendLists.get(arg0);
			System.out.println("friendsJid="+friends.getJID());
			if (friends.isAvailable()) {
				textView1.setText(friends.getName());
				textView2.setText(friends.getJID());
				textView3.setText("["+friends.getStatus()+"]");
			} else {
				textView1.setText(friends.getName());
				String ids = friends.getJID();
				ids = ids.substring(0,ids.indexOf("@"));
				textView2.setText(ids);
				textView3.setText("["+"�뿪"+"]");
				imageView2.setVisibility(View.GONE);
			}
			return convertView;
	}
	
}

private class ContacterExpandAdapter extends BaseExpandableListAdapter {

	private List<MRosterGroup> groups = null;

	public ContacterExpandAdapter(List<MRosterGroup> groups) {
		this.groups = groups;
	}

	public void setContacter(List<MRosterGroup> groups) {
		this.groups = groups;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).getFriends().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		 convertView= ChatActivity.this.getLayoutInflater().inflate(R.layout.group_item, null);
		ImageView imageView1 = (ImageView)convertView.findViewById(R.id.userAvatar_1);
		TextView textView1 = (TextView)convertView.findViewById(R.id.userName_1);
		TextView textView2 = (TextView)convertView.findViewById(R.id.userID_1);
		TextView textView3 = (TextView)convertView.findViewById(R.id.state_and_words);
		ImageView imageView2 = (ImageView)convertView.findViewById(R.id.login_where);
		Friends friends = groups.get(groupPosition).getFriends().get(childPosition);
		String  name = friends.getName();
		System.out.println("friendsname="+friends.getName());
		if (name.contains("@")) {
			name = name.substring(0, name.indexOf("@"));
			System.out.println("������name��"+name);
		}
		textView1.setText(name);
		if (friends.isAvailable()) {
			String id = friends.getJID();
			id = id.substring(0,id.indexOf("@"));
			textView2.setText(id);
			System.out.println("id="+id);
			textView3.setText("["+friends.getStatus()+"]");
		} else {
			textView1.setText(name);
			String ids = friends.getJID();
			ids = ids.substring(0,ids.indexOf("@"));
			textView2.setText(ids);
			System.out.println("id="+ids);
			textView3.setText("["+"�뿪"+"]");
			imageView2.setVisibility(View.INVISIBLE);
		}
		if (groupPosition > 1) {
			friends.setGroupName(groups.get(groupPosition).getName());
		}
		convertView.setOnLongClickListener(contacterOnLongClick);
		convertView.setTag(friends);
		convertView.setOnClickListener(contacterOnClick);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).getCount();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition).getFriends();
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView view = new TextView(ChatActivity.this);
		view.setText(groups.get(groupPosition).getName() + " ("
				+ groups.get(groupPosition).getCount() + ")");
		view.setPadding(80, 8, 8, 0);
		view.setTextColor(Color.DKGRAY);
		view.setTextSize(20);
		view.setTag(groups.get(groupPosition).getName());
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}

private class InviteAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		return inviteUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return inviteUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = new TextView(ChatActivity.this);
		Friends friends = inviteUsers.get(position);
		view.setPadding(10, 8, 8, 0);
		view.setText(friends.getName() + "	��������");
		view.setTextColor(Color.BLACK);
		view.setTextSize(23);
		view.setTag(friends.getName());
		return view;
	}

}
private Friends clickUser = null;
private OnLongClickListener contacterOnLongClick = new OnLongClickListener() {
	@Override
	public boolean onLongClick(View v) {
		String[] longClickItems = null;
		clickUser = (Friends) v.getTag();
		if (clickUser.getGroupName() != null) {
			longClickItems = new String[] { "�����ǳ�", "��Ӻ���", "ɾ������", "������",
					"��������", "�˳�����" };
		} else {
			longClickItems = new String[] { "�����ǳ�", "��Ӻ���", "ɾ������", "������" };
		}
		new AlertDialog.Builder(MContext)
				.setItems(longClickItems,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								// �����ǳ�
								case 0:
									setNickname(clickUser);
									break;
								// ��Ӻ���
								case 1:
									addSubscriber();
									break;
								// ɾ������
								case 2:
									try {
										Roster roast = ConnectionUtils.getConnection(ChatActivity.this).getRoster();
										roast.removeEntry(roast.getEntry(clickUser.getJID()));
									} catch (XMPPException e) {
									}
									break;
								// ������
								case 3:
									addToGroup(clickUser);
									break;
								// ��������
								case 4:
									updateGroupNameA(clickUser
											.getGroupName() + "");
									break;
								// �Ƴ���
								case 5:
									ContacterManager.removeUserFromGroup(clickUser, clickUser.getGroupName(),
											ConnectionUtils.getConnection(ChatActivity.this));
									break;
								}
							}
						}).setTitle("ѡ��").show();
		return false;
	}
};
/**
 * �����ǳ�
 * 
 * @param user
 */
private void setNickname(final Friends friends) {
	final EditText name_input = new EditText(ChatActivity.this);
	name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT));
	name_input.setHint("�����ǳ�");
	new AlertDialog.Builder(ChatActivity.this).setTitle("�޸��ǳ�")
			.setView(name_input)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = name_input.getText().toString();
					if (!"".equals(name))
					ContacterManager.setNickname(friends, name,
							ConnectionUtils.getConnection(ChatActivity.this));
				}
			}).setNegativeButton("ȡ��", null).show();
}
/**
 * ��Ӻ���
 */
private void addSubscriber() {
	final EditText name_input = new EditText(ChatActivity.this);
	name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT));
	name_input.setHint("����JID");
	final EditText nickname = new EditText(ChatActivity.this);
	nickname.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT));
	nickname.setHint("�����ǳ�");
	LinearLayout layout = new LinearLayout(ChatActivity.this);
	layout.setOrientation(LinearLayout.VERTICAL);
	layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT));
	layout.addView(name_input);
	layout.addView(nickname);
	new AlertDialog.Builder(ChatActivity.this).setTitle("��Ӻ���")
			.setView(layout)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String n = nickname.getText().toString();
					if ("".equals(n)) {
						n = null;
					}
					try {
						createSubscriber(name_input.getText().toString(),
								n, null);
					} catch (XMPPException e) {
					}
				}
			}).setNegativeButton("ȡ��", null).show();
}
/**
 * ������
 * 
 * @param user
 */
private void addToGroup(final Friends friends) {
	final EditText name_input = new EditText(ChatActivity.this);
	name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT));
	name_input.setHint("��������");
	new AlertDialog.Builder(ChatActivity.this).setTitle("������")
			.setView(name_input)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String groupName = name_input.getText().toString();
					if (!"".equals(groupName))
						ContacterManager.addUserToGroup(friends, groupName,
								ConnectionUtils.getConnection(ChatActivity.this));
				}
			}).setNegativeButton("ȡ��", null).show();
}
/**
 * �޸�����
 * 
 * @param user
 */
private void updateGroupNameA(final String groupName) {
	final EditText name_input = new EditText(ChatActivity.this);
	name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
			LayoutParams.WRAP_CONTENT));
	name_input.setHint("��������");
	new AlertDialog.Builder(ChatActivity.this).setTitle("�޸�����")
			.setView(name_input)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ConnectionUtils.getConnection(ChatActivity.this).getRoster().getGroup(groupName)
					.setName(name_input.getText().toString());
				}
			}).setNegativeButton("ȡ��", null).show();
}

	private List<HashMap<String, Object>> updateView() {
		List<HashMap<String, Object>>list = new ArrayList<HashMap<String,Object>>();
        String str1 = CommandBase.instance().getHost();
        str1 = str1.substring(0,str1.indexOf(":"));
        System.out.println("str1="+str1);
        List<String> testlst = messageDb.getAllFriends(userInfo.getAccount(),userInfo.getAccount()+"@"+str1);
        System.out.println("testlstSize="+testlst.size());
        for(int i=0;i<testlst.size();i++){
        	System.out.println(testlst.get(i));
        }
        List<IMMessage> listmsg = new ArrayList<IMMessage>();
        IMMessage msg = null;
        msgList = new ArrayList<IMMessage>();
        for(int i=0;i<testlst.size();i++){
        	System.out.println(testlst.get(i));
        	String temp = testlst.get(i);
        	System.out.println("temp="+temp);
        	temp = temp.substring(0,temp.indexOf("@"));
        	msg = new IMMessage();
        	listmsg = messageDb.getAllMessage(userInfo.getAccount(), temp, null);
        	msg = listmsg.get(listmsg.size()-1);
        	msgList.add(msg);
        	int unReadNum = messageDb.getUnreadMessageNum(userInfo.getAccount(), temp, 0);
        	System.out.println("msg="+msg.toString());
        	map = new HashMap<String, Object>();
        	map.put("img", R.drawable.default_avatar);
        	System.out.println("δ����Ϣ����"+unReadNum);
        	map.put("unRead", unReadNum);
        	map.put("id", testlst.get(i));
        	String tempStr = msg.getContent();
        	System.out.println("������������ϢΪ��"+tempStr);
        	System.out.println("��������ϢģʽΪ��"+msg.getChatMode());
        	int chatMode = msg.getChatMode();
        	if(tempStr!=null && !tempStr.equals("")){
	        	if(tempStr.startsWith(";")){
	        		chatMode = Integer.parseInt(tempStr.substring(1,2));
	        		tempStr = tempStr.substring(2);
	        	}
	        	if(chatMode == 0){
	        		map.put("words", tempStr);
	        	}
	        	if(chatMode == 1){
	        		map.put("words", "[ͼƬ]");
	        	}
	        	if(chatMode == 2){
	        		map.put("words", "[�ļ�]");
	        	}
	        	if(chatMode == 3){
	        		map.put("words", "[����]");
	        	}
	        	if(chatMode == 5){
	        		map.put("words", "��Ӻ�������");
	        	}
        	}
        	map.put("time", msg.getTime());
        	list.add(map);
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
	// ��������
	filter.addAction(Constant.ROSTER_SUBSCRIPTION);
	registerReceiver(receiver, filter);
	super.onResume();
}

private class ContacterReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		Friends friends = intent.getParcelableExtra(Friends.userKey);
		
		NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	  Notification nt = new Notification();
	  nt.defaults = Notification.DEFAULT_SOUND;
	  int soundId = new Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE);
	  mgr.notify(soundId, nt);

		if (Constant.ROSTER_ADDED.equals(action)) {
			if(tabHost.getCurrentTab()==1){
				refreshList();
			}
		}

		else if (Constant.ROSTER_DELETED.equals(action)) {
			Toast.makeText(MContext,
					(friends.getName() == null) ? friends.getJID() : friends.getName()
							+ "��ɾ����", Toast.LENGTH_SHORT).show();
			if(tabHost.getCurrentTab()==1){
				refreshList();
			}
		}

		else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
			// ����
			if (!friends.isAvailable())
				if (ContacterManager.contacters.get(friends.getJID()).isAvailable())
					Toast.makeText(
							MContext,
							(friends.getName() == null) ? friends.getJID() : friends
									.getName() + "������", Toast.LENGTH_SHORT).show();
			// ����
			if (friends.isAvailable())
				if (!ContacterManager.contacters.get(friends.getJID()).isAvailable())
					Toast.makeText(
							MContext,
							(friends.getName() == null) ? friends.getJID() : friends
									.getName() + "������", Toast.LENGTH_SHORT).show();
			if(tabHost.getCurrentTab()==1){
				refreshList();
			}
		}

		else if (Constant.ROSTER_UPDATED.equals(action)) {
			if(tabHost.getCurrentTab()==1){
				refreshList();
			}
		}

		else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
			IMMessage message = new IMMessage();
			message.setUnReadCount(0);
			IMMessageDb messageDb = new IMMessageDb(context);
			UserSettingInfo userInfo = new UserSettingInfo(context);
			String friendJid = intent.getStringExtra(Constant.ROSTER_SUB_FROM);
			message.setChatMode(5);
			message.setContent(";5������Ӻ���");
			message.setFromSubJid(friendJid);
			String host=CommandBase.instance().getHost();
			message.setToSubJid(userInfo.getAccount()+"@"+host.substring(0,host.indexOf(":")));
			friendJid = friendJid.substring(0,friendJid.indexOf("@"));
			String time = ConnectionUtils.getStringTime();
			message.setTime(time);
			messageDb.saveMessage(message, friendJid, userInfo.getAccount());
			if(tabHost.getCurrentTab()==1){
				Friends friends1 = new Friends();
				String name = intent.getStringExtra(Constant.ROSTER_SUB_FROM);
				name = name.substring(0,name.indexOf("@"));
				friends1.setName(name);
				inviteUsers.add(friends1);
				msgList2.add(message);
				System.out.println("msgList2���ȣ�"+msgList2.size());
				inviteAdapter.notifyDataSetChanged();
			}else{
				list1 = updateView();
				radapter = new RecentlyAdapter(list1, ChatActivity.this);
				xlistView.setAdapter(radapter);
			}
		}
		else if (Constant.NEW_MESSAGE_ACTION.equals(intent.getAction())) {
			System.out.println("hhahahahhahah");
		    
			IMMessage message = intent
					.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
			System.out.println("Achat---"+message.getChatMode());
			message.setUnReadCount(0);
			String str = message.getFromSubJid();
			str = str.substring(0,str.indexOf("@"));
			
			messageDb.saveMessage(message, str, userInfo.getAccount());
			unreadMessageNum = messageDb.getUnreadMessageNum(userInfo.getAccount(), "", 0);
			if(unreadMessageNum==0){
				unreadNum.setVisibility(View.GONE);
			}else{
				unreadNum.setText(""+unreadMessageNum);
				unreadNum.setVisibility(View.VISIBLE);
			}
			
			if(tabHost.getCurrentTab()==0){
				list1 = updateView();
				radapter = new RecentlyAdapter(list1, ChatActivity.this);
				xlistView.setAdapter(radapter);
			}
		}
		

	}
}

/**
 * ˢ�µ�ǰ���б�
 */
private void refreshList() {
	groups = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatActivity.this).getRoster(),ConnectionUtils.getConnection(ChatActivity.this));
	if(groups.size()<=0){
		Toast.makeText(ChatActivity.this, "�㻹û�к��ѣ�", Toast.LENGTH_SHORT).show();
	}else{
		expandAdapter.setContacter(groups);
		expandAdapter.notifyDataSetChanged();
	}
}
/**
 * ���һ����ϵ��
 * 
 * @param userJid
 *            ��ϵ��JID
 * @param nickname
 *            ��ϵ���ǳ�
 * @param groups
 *            ��ϵ����ӵ���Щ��
 * @throws XMPPException
 */
protected void createSubscriber(String userJid, String nickname,
		String[] groups) throws XMPPException {
	ConnectionUtils.getConnection(ChatActivity.this).getRoster()
			.createEntry(userJid, nickname, groups);
}

private OnItemClickListener inviteListClick = new OnItemClickListener() {
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2,
			long arg3) {
		System.out.println("arg2="+arg2);
		System.out.println("msg�ĳ���Ϊ��"+msgList2.size());
		final IMMessage teImMessage = msgList2.get(arg2);
		System.out.println("tempmsg="+teImMessage);
		subFrom = view.getTag().toString();
		new AlertDialog.Builder(MContext)
				.setMessage(subFrom + "���������Ϊ����")
				.setTitle("��ʾ")
				.setPositiveButton("���",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String jid = CommandBase.instance().getHost();
								jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
								// ��������
								sendSubscribe(Presence.Type.subscribed,
										jid);
								removeInviteUser(subFrom);
								messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0������ӳɹ�");
								unreadMessageNum = messageDb.getUnreadMessageNum(userInfo.getAccount(), "", 0);
								if(unreadMessageNum==0){
									unreadNum.setVisibility(View.GONE);
								}else{
									unreadNum.setText(""+unreadMessageNum);
									unreadNum.setVisibility(View.VISIBLE);
								}
							}
						})
				.setNegativeButton("�ܾ�",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String jid = CommandBase.instance().getHost();
								jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
								sendSubscribe(Presence.Type.unsubscribe,
										jid);
								removeInviteUser(subFrom);
								messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0��Ӻ��������Ѿܾ�");
								unreadMessageNum = messageDb.getUnreadMessageNum(userInfo.getAccount(), "", 0);
								if(unreadMessageNum==0){
									unreadNum.setVisibility(View.GONE);
								}else{
									unreadNum.setText(""+unreadMessageNum);
									unreadNum.setVisibility(View.VISIBLE);
								}
							}
						}).show();
	}
};
private OnClickListener contacterOnClick = new OnClickListener() {
	@Override
	public void onClick(View v) {
		ImageView img = (ImageView)v.findViewById(R.id.userAvatar_1);
		TextView jid= (TextView) v.findViewById(R.id.userID_1);
		Friends friends = new Friends();
		friends.setJID(jid.getText().toString());
		Intent intent = new Intent(MContext, ChatLayoutActivity.class);
		Bundle bd = new Bundle();
		bd.putString("style","to");
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
			radapter = new RecentlyAdapter(list1, ChatActivity.this);
			xlistView.setAdapter(radapter);
			recordSearchListView.setVisibility(View.GONE);
			xlistView.setVisibility(View.VISIBLE);
			unreadMessageNum = messageDb.getUnreadMessageNum(userInfo.getAccount(), "", 0);
			if(unreadMessageNum==0){
				unreadNum.setVisibility(View.GONE);
			}else{
				unreadNum.setText(""+unreadMessageNum);
				unreadNum.setVisibility(View.VISIBLE);
			}
      	  break;
        case 11:
      	  if (NetWorkDetect.detect(ChatActivity.this)) {
          	  groupLists = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatActivity.this)
							.getRoster(),ConnectionUtils.getConnection(ChatActivity.this));
					expandAdapter = new ContacterExpandAdapter(groupLists);
					contacterList.setAdapter(expandAdapter);
					for(int i = 0;i<groupLists.size();i++){
						MRosterGroup group = groupLists.get(i);
						String groupName = group.getName();
						if(!productDb.groupNameIsInMyContact(userInfo.getAccount(), groupName)){
							productDb.saveGroupName(groupName, userInfo.getAccount());
						}
						List<Friends> friends = group.getFriends();
						Friends teFriends = null;
						for(int j=0;j<friends.size();j++){
							teFriends = friends.get(j);
							if(!productDb.friendIsInMyContact(userInfo.getAccount(), teFriends.getJID())){
								productDb.saveFriends(teFriends, groupName, userInfo.getAccount());
							}
						}
					}
      	  }
			contacterList.setVisibility(View.VISIBLE);
			groupSearchLisview.setVisibility(View.GONE);
      	  break;
        case 12:
        	int currentTabId = data.getIntExtra("tab", 0);
        	tabHost.setCurrentTab(currentTabId);
      	  if(tabHost.getCurrentTab()==0){
      		 list1 = updateView();
    			 radapter.notifyDataSetChanged();
      	  }else{
      		  if (NetWorkDetect.detect(ChatActivity.this)) {
          		  groupLists = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatActivity.this)
								.getRoster(),ConnectionUtils.getConnection(ChatActivity.this));
						for(int i = 0;i<groupLists.size();i++){
							MRosterGroup group = groupLists.get(i);
							String groupName = group.getName();
							if(!productDb.groupNameIsInMyContact(userInfo.getAccount(), groupName)){
								productDb.saveGroupName(groupName, userInfo.getAccount());
							}
							List<Friends> friends = group.getFriends();
							Friends teFriends = null;
							for(int j=0;j<friends.size();j++){
								teFriends = friends.get(j);
								if(!productDb.friendIsInMyContact(userInfo.getAccount(), teFriends.getJID())){
									productDb.saveFriends(teFriends, groupName, userInfo.getAccount());
								}
							}
						}
          		  expandAdapter.notifyDataSetChanged();
      		  }
      	  }
      	  break;
        }
  }
	super.onActivityResult(requestCode, resultCode, data);
}


}

