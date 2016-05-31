package com.xiaoguo.wasp.mobile.ui.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.packet.Presence;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.User;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.utils.ExpressionUtil;
import com.xiaoguo.wasp.mobile.widget.XListView;
import com.xiaoguo.wasp.mobile.widget.XListView.IXListViewListener;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager.MRosterGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatRecordActivity extends Activity implements OnClickListener{
	private IMMessageDb messageDb=null;
	UserSettingInfo userInfo = null;
	XListView xlistView;
	List<IMMessage> msgList=null;
	protected Context MContext = null;
	private ListView recordSearchListView=null;
	List<MRosterGroup> groupLists=null;
	List<HashMap<String, Object>> list1=null;
	HashMap<String, Object> map = null;
	RecentlyAdapter radapter;
	RecentlyAdapter searchAdapter;
	searchAdapter searchAdapter2;
	private List<Friends> inviteUsers = new ArrayList<Friends>();
	protected int status = LoadStatus.loadSucceed;
	ArrayList<User> userList;
	private EditText searchInputView;
	private ImageView searchView;
	protected View layoutLoad;
	protected TextView promptInfo;
	protected ProgressBar loading;
	List<IMMessage> msgList2=null;
	//消息
	String subFrom = "";
	private ContacterReceiver receiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_record);
		
		WASPApplication.getInstance().addActivity(this);
		MContext = ChatRecordActivity.this;
		
		initView();
	}

	private void initView() {
		messageDb = new IMMessageDb(this);
		userInfo = new UserSettingInfo(this);
		
		
		xlistView = (XListView)findViewById(R.id.list);
		xlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final IMMessage teImMessage = msgList.get(arg2-1);
				TextView id = (TextView)arg1.findViewById(R.id.userID);
				TextView textView = (TextView)arg1.findViewById(R.id.userRecentMessage);
				if(textView.getText().toString().equals("添加好友请求")){
					subFrom = id.getText().toString();
					System.out.println("subForm="+subFrom);
					new AlertDialog.Builder(MContext)
							.setMessage( subFrom+ "请求添加您为好友")
							.setTitle("提示")
							.setPositiveButton("添加",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											// 接受请求
											sendSubscribe(Presence.Type.subscribed,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0好友添加成功");
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
											 xlistView.setAdapter(radapter);
											removeInviteUser(subFrom);
										}
									})
							.setNegativeButton("拒绝",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											sendSubscribe(Presence.Type.unsubscribe,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0添加好友请求已拒绝");
											list1 = updateView();
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
											 xlistView.setAdapter(radapter);
//											radapter.notifyDataSetChanged();
											removeInviteUser(subFrom);
										}
									}).show();
				}else{
					map = list1.get(arg2-1);
					String jid = ""+map.get("id");
					jid = jid.substring(0,jid.indexOf("@"));
					System.out.println("聊天记录jid="+jid);
					Friends friends = new Friends();
					friends.setJID(jid);
					System.out.println("jid="+jid);
					Intent intent = new Intent();
					intent.setClass(ChatRecordActivity.this, ChatLayoutActivity.class);
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
		searchInputView = (EditText)findViewById(R.id.search_input);
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
		searchView = (ImageView)findViewById(R.id.search_img);
		searchView.setOnClickListener(this);
		
		recordSearchListView = (ListView)findViewById(R.id.search_list_1);
		recordSearchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final IMMessage teImMessage = msgList.get(arg2-1);
				System.out.println("tempmsg="+teImMessage);
				TextView view = (TextView)arg1.findViewById(R.id.userID);
				TextView textView = (TextView)arg1.findViewById(R.id.userRecentMessage);
				if(textView.getText().toString().equals("添加好友请求")){
					subFrom = view.getText().toString();
					new AlertDialog.Builder(MContext)
							.setMessage(subFrom + "请求添加您为好友")
							.setTitle("提示")
							.setPositiveButton("添加",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											// 接受请求
											sendSubscribe(Presence.Type.subscribed,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0好友添加成功");
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
											 xlistView.setAdapter(radapter);
											removeInviteUser(subFrom);
										}
									})
							.setNegativeButton("拒绝",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											String jid = CommandBase.instance().getHost();
											jid = subFrom+"@"+jid.substring(0,jid.indexOf(":"));
											sendSubscribe(Presence.Type.unsubscribe,
													jid);
											messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0添加好友请求已拒绝");
											xlistView.setVisibility(View.VISIBLE);
											recordSearchListView.setVisibility(View.GONE);
											list1 = updateView();
											radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
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
					intent.setClass(ChatRecordActivity.this, ChatLayoutActivity.class);
					Bundle bd = new Bundle();
					bd.putString("style","from");
					bd.putParcelable("friends", friends);
					intent.putExtra("info", bd);
					startActivityForResult(intent, 10);
				}
			}
		});
		
		// 专门为下拉
		layoutLoad = findViewById(R.id.layout_load);
		promptInfo = (TextView)findViewById(R.id.prompt_info);
		loading = (ProgressBar)findViewById(R.id.loading);
		promptInfo.setOnClickListener(this);
		
		userList = new ArrayList<User>();
		list1 = updateView();
		
		xlistView.setVisibility(View.VISIBLE);
		recordSearchListView.setVisibility(View.GONE);
		
		radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
		 xlistView.setAdapter(radapter);
	}
	private List<HashMap<String, Object>> updateView() {
		List<HashMap<String, Object>>list = new ArrayList<HashMap<String,Object>>();
            System.out.println("444"); 
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
	        	System.out.println("未读消息数："+unReadNum);
	        	map.put("unRead", unReadNum);
	        	map.put("id", testlst.get(i));
	        	String tempStr = msg.getContent();
	        	System.out.println("服务器最新消息为："+tempStr);
	        	System.out.println("服务器消息模式为："+msg.getChatMode());
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
		        		map.put("words", "[图片]");
		        	}
		        	if(chatMode == 2){
		        		map.put("words", "[文件]");
		        	}
		        	if(chatMode == 3){
		        		map.put("words", "[语音]");
		        	}
		        	if(chatMode == 5){
		        		map.put("words", "添加好友请求");
		        	}
	        	}
	        	map.put("time", msg.getTime());
	        	list.add(map);
            }
            System.out.println("555");
			return list;
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
			 	convertView= ChatRecordActivity.this.getLayoutInflater().inflate(R.layout.group_item, null);
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
					textView3.setText("["+"离开"+"]");
					imageView2.setVisibility(View.GONE);
				}
				return convertView;
		}
		
	}
	
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
		//获取列表，更改相应的状态
		userList.add(new User("hca","123","456","url","开会了"));
		userList.add(new User("hca","123","456","url","开会了"));
		userList.add(new User("hca","123","456","url","开会了"));
		loadEnd();
		status = LoadStatus.loadSucceed;
		loadViewDisplay();
		radapter.notifyDataSetChanged();
	}
	protected void loadEnd() {
		xlistView.stopRefresh();
		xlistView.stopLoadMore();
	}
	private void loadViewDisplay() {
		xlistView.setVisibility(status==LoadStatus.loadSucceed?View.VISIBLE:View.INVISIBLE);
		promptInfo.setVisibility(status==LoadStatus.loadError?View.VISIBLE:View.INVISIBLE);
		loading.setVisibility(status==LoadStatus.loading?View.VISIBLE:View.INVISIBLE);
		layoutLoad.setVisibility((status==LoadStatus.loadError||status==LoadStatus.loading)?View.VISIBLE:View.INVISIBLE);
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
		ConnectionUtils.getConnection(this).sendPacket(presence);
	}
	
	/**
	 * 从聊天邀请人中删除一个条目
	 * 
	 * @param subFrom
	 */
	private void removeInviteUser(String subFrom) {
		for (Friends user : inviteUsers) {
			if (subFrom.equals(user.getName())) {
				inviteUsers.remove(user);
				/*for(int i=0;i<msgList2.size();i++){
					if(msgList2.get(i).getFromSubJid().equals(subFrom)){
						msgList2.remove(i);
					}
				}*/
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
	switch (v.getId()) {
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
			System.out.println("list的长度:"+list.size());
			searchAdapter = new RecentlyAdapter(list, ChatRecordActivity.this);
			xlistView.setVisibility(View.GONE);
			recordSearchListView.setAdapter(searchAdapter);
			recordSearchListView.setVisibility(View.VISIBLE);
			if(list.size()>0){
			}else{
				String text = "没有搜索到相关信息";
				Toast.makeText(ChatRecordActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		}
		break;

	default:
		break;
	}
		
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

			Friends friends = intent.getParcelableExtra(Friends.userKey);
			
			NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		  Notification nt = new Notification();
		  nt.defaults = Notification.DEFAULT_SOUND;
		  int soundId = new Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE);
		  mgr.notify(soundId, nt);

			if (Constant.ROSTER_ADDED.equals(action)) {
			}

			else if (Constant.ROSTER_DELETED.equals(action)) {
				Toast.makeText(MContext,
						(friends.getName() == null) ? friends.getJID() : friends.getName()
								+ "被删除了", Toast.LENGTH_SHORT).show();
			}

			else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
				// 下线
				if (!friends.isAvailable())
					if (ContacterManager.contacters.get(friends.getJID()).isAvailable())
						Toast.makeText(
								MContext,
								(friends.getName() == null) ? friends.getJID() : friends
										.getName() + "上线了", Toast.LENGTH_SHORT).show();
				// 上线
				if (friends.isAvailable())
					if (!ContacterManager.contacters.get(friends.getJID()).isAvailable())
						Toast.makeText(
								MContext,
								(friends.getName() == null) ? friends.getJID() : friends
										.getName() + "下线了", Toast.LENGTH_SHORT).show();
			}

			else if (Constant.ROSTER_UPDATED.equals(action)) {
			}

			else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
				IMMessage message = new IMMessage();
				message.setUnReadCount(0);
				IMMessageDb messageDb = new IMMessageDb(context);
				UserSettingInfo userInfo = new UserSettingInfo(context);
				String friendJid = intent.getStringExtra(Constant.ROSTER_SUB_FROM);
				message.setChatMode(5);
				message.setContent(";5请求添加好友");
				message.setFromSubJid(friendJid);
				String host=CommandBase.instance().getHost();
				message.setToSubJid(userInfo.getAccount()+"@"+host.substring(0,host.indexOf(":")));
				friendJid = friendJid.substring(0,friendJid.indexOf("@"));
				String time = ConnectionUtils.getStringTime();
				message.setTime(time);
				messageDb.saveMessage(message, friendJid, userInfo.getAccount());
					list1 = updateView();
					radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
					xlistView.setAdapter(radapter);
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
				
				list1 = updateView();
				radapter = new RecentlyAdapter(list1, ChatRecordActivity.this);
				xlistView.setAdapter(radapter);
			}
			

		}
	}
	
}
