package com.xiaoguo.wasp.mobile.ui.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.widget.NPullToFreshContainer;
import com.xiaoguo.wasp.mobile.widget.NPullToFreshContainer.OnContainerRefreshListener;
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
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatContactorActivity extends Activity implements OnClickListener{
	protected Context MContext = null;
	private NPullToFreshContainer iPulltoRefresh;
	ScrollView mScrollView;
	List<MRosterGroup> groupLists=null;
	private EditText searchInputView;
	private ImageView searchView;
	private ExpandableListView contacterList = null;
	private ContacterExpandAdapter expandAdapter = null;
	private ListView inviteList = null;
	private InviteAdapter inviteAdapter = null;
	private List<Friends> inviteUsers = new ArrayList<Friends>();
	private List<MRosterGroup> groups=null;
	private ListView groupSearchLisview=null;
	List<IMMessage> msgList2=null;
	ProductDb productDb=null;
	private IMMessageDb messageDb=null;
	UserSettingInfo userInfo = null;
	//消息
	String subFrom = "";
	private ContacterReceiver receiver = null;
	HashMap<String, Object> map = null;
	searchAdapter searchAdapter2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_group);
		
		userInfo = new UserSettingInfo(ChatContactorActivity.this);
		productDb = new ProductDb(ChatContactorActivity.this);
		messageDb = new IMMessageDb(ChatContactorActivity.this);
		MContext = ChatContactorActivity.this;
		WASPApplication.getInstance().addActivity(this);
		initView();
	}

	private void initView() {
		groupLists = new ArrayList<ContacterManager.MRosterGroup>();
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
					contacterList.setVisibility(View.VISIBLE);
					groupSearchLisview.setVisibility(View.GONE);
				}
				
			}
		});
		searchView = (ImageView)findViewById(R.id.search_img);
		searchView.setOnClickListener(this);
		msgList2 = new ArrayList<IMMessage>();
		iPulltoRefresh =(NPullToFreshContainer)findViewById(R.id.pulltofresh);
    	iPulltoRefresh.setOnRefreshListener(new OnContainerRefreshListener() {
			@Override
			public void onContainerRefresh() {
				contacterList = (ExpandableListView)findViewById(R.id.main_expand_list);
				//取消图标
				contacterList.setGroupIndicator(null);
					if(!NetWorkDetect.detect(ChatContactorActivity.this)){
						Toast.makeText(ChatContactorActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
					}else{
						groupLists = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatContactorActivity.this)
								.getRoster(),ConnectionUtils.getConnection(ChatContactorActivity.this));
						System.out.println("11");
						for(int i = 0;i<groupLists.size();i++){
							System.out.println("22");
							MRosterGroup group = groupLists.get(i);
							String groupName = group.getName();
							System.out.println("33:"+groupName);
							if(!productDb.groupNameIsInMyContact(userInfo.getAccount(), groupName)){
								System.out.println("44");
								productDb.saveGroupName(groupName, userInfo.getAccount());
							}
							List<Friends> friends = group.getFriends();
							Friends teFriends = null;
							for(int j=0;j<friends.size();j++){
								System.out.println("55");
								teFriends = friends.get(j);
								if(!productDb.friendIsInMyContact(userInfo.getAccount(), teFriends.getJID())){
									System.out.println("66");
									productDb.saveFriends(teFriends, groupName, userInfo.getAccount());
								}
							}
						}
						expandAdapter = new ContacterExpandAdapter(groupLists);
						contacterList.setAdapter(expandAdapter);
					}
				
				inviteList = (ListView) findViewById(R.id.main_invite_list);
				inviteAdapter = new InviteAdapter();
				inviteList.setAdapter(inviteAdapter);
				inviteList.setOnItemClickListener(inviteListClick);	
				Date vdate = new Date();
				iPulltoRefresh.onComplete(vdate.toLocaleString());
			}
		});
    	
    	
    	contacterList = (ExpandableListView) findViewById(R.id.main_expand_list);
		contacterList.setGroupIndicator(this.getResources().getDrawable(R.drawable.indocator));
		if (!NetWorkDetect.detect(ChatContactorActivity.this)) {
			List<String> groupNames = productDb.getAllGroupName(userInfo.getAccount());
			for(int i=0;i<groupNames.size();i++){
				String groupName = groupNames.get(i);
				List<Friends> friends = productDb.getAllFriends(groupName, userInfo.getAccount());
				MRosterGroup group = new MRosterGroup(groupName, friends);
				groupLists.add(group);
			}
		}else{
			groupLists = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatContactorActivity.this)
					.getRoster(),ConnectionUtils.getConnection(ChatContactorActivity.this));
			for(int i = 0;i<groupLists.size();i++){
				MRosterGroup group = groupLists.get(i);
				String groupName = group.getName();
				System.out.println("组名："+groupName);
				if(!productDb.groupNameIsInMyContact(userInfo.getAccount(), groupName)){
					System.out.println("xxx");
					productDb.saveGroupName(groupName, userInfo.getAccount());
					System.out.println("yyy");
				}
				List<Friends> friends = group.getFriends();
				Friends teFriends = null;
				for(int j=0;j<friends.size();j++){
					teFriends = friends.get(j);
					System.out.println("friends的jid："+teFriends.getJID());
					System.out.println("friends的name："+teFriends.getName());
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
		groupSearchLisview = (ListView)findViewById(R.id.search_list_2);
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
		
		inviteList = (ListView)findViewById(R.id.main_invite_list);
		inviteAdapter = new InviteAdapter();
		inviteList.setAdapter(inviteAdapter);
		inviteList.setOnItemClickListener(inviteListClick);
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
			 convertView= ChatContactorActivity.this.getLayoutInflater().inflate(R.layout.group_item, null);
			ImageView imageView1 = (ImageView)convertView.findViewById(R.id.userAvatar_1);
			TextView textView1 = (TextView)convertView.findViewById(R.id.userName_1);
			TextView textView2 = (TextView)convertView.findViewById(R.id.userID_1);
			TextView textView3 = (TextView)convertView.findViewById(R.id.state_and_words);
			ImageView imageView2 = (ImageView)convertView.findViewById(R.id.login_where);
			Friends friends = groups.get(groupPosition).getFriends().get(childPosition);
			System.out.println("friendsJid="+friends.getJID());
			if (friends.isAvailable()) {
				textView1.setText(friends.getName());
				String id = friends.getJID();
				id = id.substring(0,id.indexOf("@"));
				textView2.setText(id);
				System.out.println("id="+id);
				textView3.setText("["+friends.getStatus()+"]");
			} else {
				textView1.setText(friends.getName());
				String ids = friends.getJID();
				ids = ids.substring(0,ids.indexOf("@"));
				textView2.setText(ids);
				System.out.println("id="+ids);
				textView3.setText("["+"离开"+"]");
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
			TextView view = new TextView(ChatContactorActivity.this);
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
			TextView view = new TextView(ChatContactorActivity.this);
			Friends friends = inviteUsers.get(position);
			view.setPadding(10, 8, 8, 0);
			view.setText(friends.getName() + "	邀请聊天");
			view.setTextColor(Color.BLACK);
			view.setTextSize(23);
			view.setTag(friends.getName());
			return view;
		}

	}
	
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
	
	
	private OnItemClickListener inviteListClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			System.out.println("arg2="+arg2);
			System.out.println("msg的长度为："+msgList2.size());
			final IMMessage teImMessage = msgList2.get(arg2);
			System.out.println("tempmsg="+teImMessage);
			subFrom = view.getTag().toString();
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
									removeInviteUser(subFrom);
									messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0好友添加成功");
									
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
									removeInviteUser(subFrom);
									messageDb.updateMessage(userInfo.getAccount(), subFrom, teImMessage, 1,";0添加好友请求已拒绝");
								}
							}).show();
		}
	};
	
	private Friends clickUser = null;
	private OnLongClickListener contacterOnLongClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			String[] longClickItems = null;
			clickUser = (Friends) v.getTag();
			if (clickUser.getGroupName() != null) {
				longClickItems = new String[] { "设置昵称", "添加好友", "删除好友", "加入组",
						"更改组名", "退出该组" };
			} else {
				longClickItems = new String[] { "设置昵称", "添加好友", "删除好友", "加入组" };
			}
			new AlertDialog.Builder(MContext)
					.setItems(longClickItems,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									// 设置昵称
									case 0:
										setNickname(clickUser);
										break;
									// 添加好友
									case 1:
										addSubscriber();
										break;
									// 删除好友
									case 2:
										try {
											Roster roast = ConnectionUtils.getConnection(ChatContactorActivity.this).getRoster();
											roast.removeEntry(roast.getEntry(clickUser.getJID()));
										} catch (XMPPException e) {
										}
										break;
									// 加入组
									case 3:
										addToGroup(clickUser);
										break;
									// 更改组名
									case 4:
										updateGroupNameA(clickUser
												.getGroupName() + "");
										break;
									// 移出组
									case 5:
										ContacterManager.removeUserFromGroup(clickUser, clickUser.getGroupName(),
												ConnectionUtils.getConnection(ChatContactorActivity.this));
										break;
									}
								}
							}).setTitle("选项").show();
			return false;
		}
	};
	
	/**
	 * 设置昵称
	 * 
	 * @param user
	 */
	private void setNickname(final Friends friends) {
		final EditText name_input = new EditText(ChatContactorActivity.this);
		name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		name_input.setHint("输入昵称");
		new AlertDialog.Builder(ChatContactorActivity.this).setTitle("修改昵称")
				.setView(name_input)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = name_input.getText().toString();
						if (!"".equals(name))
						ContacterManager.setNickname(friends, name,
								ConnectionUtils.getConnection(ChatContactorActivity.this));
					}
				}).setNegativeButton("取消", null).show();
	}
	/**
	 * 添加好友
	 */
	private void addSubscriber() {
		final EditText name_input = new EditText(ChatContactorActivity.this);
		name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		name_input.setHint("输入JID");
		final EditText nickname = new EditText(ChatContactorActivity.this);
		nickname.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		nickname.setHint("输入昵称");
		LinearLayout layout = new LinearLayout(ChatContactorActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		layout.addView(name_input);
		layout.addView(nickname);
		new AlertDialog.Builder(ChatContactorActivity.this).setTitle("添加好友")
				.setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

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
				}).setNegativeButton("取消", null).show();
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
		ConnectionUtils.getConnection(ChatContactorActivity.this).getRoster()
				.createEntry(userJid, nickname, groups);
	}
	
	/**
	 * 加入组
	 * 
	 * @param user
	 */
	private void addToGroup(final Friends friends) {
		final EditText name_input = new EditText(ChatContactorActivity.this);
		name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		name_input.setHint("输入组名");
		new AlertDialog.Builder(ChatContactorActivity.this).setTitle("加入组")
				.setView(name_input)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String groupName = name_input.getText().toString();
						if (!"".equals(groupName))
							ContacterManager.addUserToGroup(friends, groupName,
									ConnectionUtils.getConnection(ChatContactorActivity.this));
					}
				}).setNegativeButton("取消", null).show();
	}
	/**
	 * 修改组名
	 * 
	 * @param user
	 */
	private void updateGroupNameA(final String groupName) {
		final EditText name_input = new EditText(ChatContactorActivity.this);
		name_input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		name_input.setHint("输入组名");
		new AlertDialog.Builder(ChatContactorActivity.this).setTitle("修改组名")
				.setView(name_input)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ConnectionUtils.getConnection(ChatContactorActivity.this).getRoster().getGroup(groupName)
						.setName(name_input.getText().toString());
					}
				}).setNegativeButton("取消", null).show();
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
		ConnectionUtils.getConnection(ChatContactorActivity.this).sendPacket(presence);
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
				for(int i=0;i<msgList2.size();i++){
					if(msgList2.get(i).getFromSubJid().equals(subFrom)){
						msgList2.remove(i);
					}
				}
				break;
			}
		}
		inviteAdapter.notifyDataSetChanged();
	}
	@Override
	public void onClick(View v) {
	switch (v.getId()) {
	case R.id.search_img:
		String friends = searchInputView.getText().toString();
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
			System.out.println("list的长度:"+list.size());
			searchAdapter2 = new searchAdapter(list);
			groupSearchLisview.setAdapter(searchAdapter2);
			groupSearchLisview.setVisibility(View.VISIBLE);
			contacterList.setVisibility(View.GONE);
			if(list.size()>0){
			}else{
				String text = "没有搜索到相关信息";
				Toast.makeText(ChatContactorActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		}
		break;
	default:
		break;
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
			 	convertView= ChatContactorActivity.this.getLayoutInflater().inflate(R.layout.group_item, null);
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
					refreshList();
			}

			else if (Constant.ROSTER_DELETED.equals(action)) {
				Toast.makeText(MContext,
						(friends.getName() == null) ? friends.getJID() : friends.getName()
								+ "被删除了", Toast.LENGTH_SHORT).show();
					refreshList();
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
					refreshList();
			}

			else if (Constant.ROSTER_UPDATED.equals(action)) {
					refreshList();
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
				Friends friends1 = new Friends();
				String name = intent.getStringExtra(Constant.ROSTER_SUB_FROM);
				name = name.substring(0,name.indexOf("@"));
				friends1.setName(name);
				inviteUsers.add(friends1);
				msgList2.add(message);
				System.out.println("msgList2长度："+msgList2.size());
				inviteAdapter.notifyDataSetChanged();
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
			}
		}
	}
	/**
	 * 刷新当前的列表
	 */
	private void refreshList() {
		groups = ContacterManager.getGroups(ConnectionUtils.getConnection(ChatContactorActivity.this).getRoster(),ConnectionUtils.getConnection(ChatContactorActivity.this));
		if(groups.size()<=0){
			Toast.makeText(ChatContactorActivity.this, "你还没有好友！", Toast.LENGTH_SHORT).show();
		}else{
			expandAdapter.setContacter(groups);
			expandAdapter.notifyDataSetChanged();
		}
	}
	
}
