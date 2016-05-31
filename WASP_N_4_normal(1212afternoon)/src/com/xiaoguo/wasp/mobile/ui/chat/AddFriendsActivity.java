package com.xiaoguo.wasp.mobile.ui.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.Expert;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddFriendsActivity extends Activity implements OnClickListener{
	private Button backView;
	private TextView titleView;
	
	private EditText inputFriendsIdView;
	private Button addFriendsView;
	//查找结果
	private LinearLayout searchFriendLayout;
	private TextView searchResultView;
	private ListView searchListView;
	//专家列表
	private LinearLayout expertLayout;
	private ListView expertListView;
	
	List<Friends> friends=null;
	List<HashMap<String, Object>> fList = null;
	ExpertAdapter adapter =null;
	
	UserSettingInfo userSettingInfo=null;
	IMMessageDb messageDb =null;
	MyBroadcastReceiver receiver = null;
	ProgressDialog dialog =null;
	
	List<HashMap<String, Object>> searchList=null;
	ExpertAdapter searchAdapter =null;
	ProductDb productDb=null;
	int currentTab=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		
		userSettingInfo = new UserSettingInfo(AddFriendsActivity.this);
		messageDb =  new IMMessageDb(AddFriendsActivity.this);
		productDb = new ProductDb(AddFriendsActivity.this);
		dialog = new ProgressDialog(AddFriendsActivity.this);
		WASPApplication.getInstance().addActivity(this);
		currentTab = getIntent().getIntExtra("tab", 0);
		System.out.println("传过来的当前tab是："+currentTab);
		init();
	}
	
	private void init() {
		backView = (Button)findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);
		
		titleView = (TextView)findViewById(R.id.title);
		titleView.setText("添加好友");
		
		inputFriendsIdView = (EditText)findViewById(R.id.input_friends_id);
		
		addFriendsView = (Button)findViewById(R.id.add_friends_ok);
		addFriendsView.setOnClickListener(this);
		
		searchFriendLayout = (LinearLayout)findViewById(R.id.search);
		searchResultView = (TextView)findViewById(R.id.search_result);
		searchListView = (ListView)findViewById(R.id.search_list);
		searchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final TextView textView = (TextView)arg1.findViewById(R.id.userID_1);
				String host = CommandBase.instance().getHost().substring(0,CommandBase.instance().getHost().indexOf(":"));
				if(productDb.friendIsInMyContact(userSettingInfo.getAccount(), textView.getText().toString()+"@"+host)){
					new AlertDialog.Builder(AddFriendsActivity.this)
					.setTitle("提示")
					.setMessage("您已经添加过此好友，点击进行聊天")
					.setPositiveButton("聊天", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Friends friends = new Friends();
							friends.setJID(textView.getText().toString());
//							createChat(friends);
							Intent intent = new Intent(AddFriendsActivity.this, ChatLayoutActivity.class);
							Bundle bd = new Bundle();
							bd.putString("style","to");
							bd.putParcelable("friends", friends);
							intent.putExtra("info", bd);
							startActivity(intent);
						}
					}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}}).create().show();
				}else{
					final TextView textView1 = (TextView)arg1.findViewById(R.id.userName_1);
					//添加好友 或直接聊天
					//Toast.makeText(AddFriendsActivity.this, "开发中", Toast.LENGTH_SHORT).show();
					new AlertDialog.Builder(AddFriendsActivity.this)
					.setTitle("提示")
					.setMessage("确定添加"+textView.getText().toString()+"为好友吗？")
					.setPositiveButton("添加",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									//添加
	//								String n = inputFriendsIdView.getText().toString().split("@")[0];
									String host =  CommandBase.instance().getHost();
									final String jid = textView.getText().toString() + "@"+host.substring(0,host.indexOf(":"));
									String n = textView1.getText().toString();
									try {
										createSubscriber(jid,n, null);
										Intent resultIntent = new Intent();
										AddFriendsActivity.this.setResult(RESULT_OK,resultIntent);
										AddFriendsActivity.this.finish();
									} catch (XMPPException e) {
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
				}
			}
		});
		
		
		expertLayout = (LinearLayout)findViewById(R.id.expert);
		
		searchFriendLayout.setVisibility(View.GONE);
		expertLayout.setVisibility(View.VISIBLE);
		
		expertListView = (ListView)findViewById(R.id.add_friends_list);
		/*friends = new ArrayList<Friends>();
		friends = getFriends();*/
		 fList = new ArrayList<HashMap<String,Object>>();
		 List<Expert> experts = productDb.getAllExperts(userSettingInfo.getAccount());
		 HashMap<String, Object> map=null;
		 Expert expert = null;
		 if(experts.size()>0){
			 for (int i = 0; i < experts.size(); i++) {
				 expert = experts.get(i);
				map = new HashMap<String, Object>();
				map.put("userJID", expert.getExpertAccount());
				map.put("userName", expert.getExpertName());
				map.put("description", expert.getExpertDescription());
				fList.add(map);
				adapter = new ExpertAdapter(fList);
				expertListView.setAdapter(adapter);
			}
		 }else{
			 fList = getFriends();
		 }
//		adapter = new ExpertAdapter(friends);
		 /*adapter = new ExpertAdapter(fList);
		expertListView.setAdapter(adapter);*/
		expertListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final TextView textView = (TextView)arg1.findViewById(R.id.userID_1);
				String host = CommandBase.instance().getHost().substring(0,CommandBase.instance().getHost().indexOf(":"));
				if(productDb.friendIsInMyContact(userSettingInfo.getAccount(), textView.getText().toString()+"@"+host)){
					new AlertDialog.Builder(AddFriendsActivity.this)
					.setTitle("提示")
					.setMessage("您已经添加过此好友，点击进行聊天")
					.setPositiveButton("聊天", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Friends friends = new Friends();
							friends.setJID(textView.getText().toString());
//							createChat(friends);
							Intent intent = new Intent(AddFriendsActivity.this, ChatLayoutActivity.class);
							Bundle bd = new Bundle();
							bd.putString("style","to");
							bd.putParcelable("friends", friends);
							intent.putExtra("info", bd);
							startActivity(intent);
						}
					}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}}).create().show();
				}else{
					final TextView textView1 = (TextView)arg1.findViewById(R.id.userName_1);
					//添加好友 或直接聊天
					//Toast.makeText(AddFriendsActivity.this, "开发中", Toast.LENGTH_SHORT).show();
					new AlertDialog.Builder(AddFriendsActivity.this)
					.setTitle("提示")
					.setMessage("确定添加"+textView.getText().toString()+"为好友吗？")
					.setPositiveButton("添加",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									//添加
	//								String n = inputFriendsIdView.getText().toString().split("@")[0];
									String host =  CommandBase.instance().getHost();
									final String jid = textView.getText().toString() + "@"+host.substring(0,host.indexOf(":"));
									String n = textView1.getText().toString();
									try {
										createSubscriber(jid,n, null);
										Intent resultIntent = new Intent();
										resultIntent.putExtra("tab", currentTab);
										AddFriendsActivity.this.setResult(RESULT_OK,resultIntent);
										AddFriendsActivity.this.finish();
									} catch (XMPPException e) {
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			//判断导航是否弹出
			if(searchFriendLayout.getVisibility()==0){
				searchFriendLayout.setVisibility(View.GONE);
				expertLayout.setVisibility(View.VISIBLE);
			}else{
				this.finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	private List<HashMap<String, Object>> getFriends() {
		final List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
				
			}
			@Override
			public void start() {
				dialog.setMessage("正在请求数据，请稍后...");
				dialog.show();
			}
			@Override
			public String requestUrl() {
				return "userList";
			}
			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("role_id",3);
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
				productDb.deleteExpert(userSettingInfo.getAccount());
				Expert expert = null;
				try {
					JSONObject object = msg.getJSONObject("data");
					JSONArray array = object.getJSONArray("userlist");
					HashMap<String, Object> map = null;
					for(int i =0;i<array.length();i++){
						object = array.getJSONObject(i);
						String userId = object.getString("user_name");
						String userRemark = object.getString("user_remark");
						String userName = object.getString("user_display_name");
						map = new HashMap<String, Object>();
						map.put("userJID", userId);
						if(userName.equals("")){
							userName = userId;
						}
						expert = new Expert(userId, userName, userRemark, "");
						productDb.saveExpert(expert,userSettingInfo.getAccount());
						map.put("userName", userName);
						map.put("description", userRemark);
						list.add(map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("专家列表解析异常");
				}
			}
			@Override
			public void finish() {
				dialog.dismiss();
				fList = list;
				adapter = new ExpertAdapter(fList);
				expertListView.setAdapter(adapter);
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
		System.out.println("专家列表为："+fList.size());
		
		return list;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			if(searchFriendLayout.getVisibility()==0){
				searchFriendLayout.setVisibility(View.GONE);
				expertLayout.setVisibility(View.VISIBLE);
			}else{
				this.finish();
			}
			break;
		case R.id.add_friends_ok:
			final String n = inputFriendsIdView.getText().toString();
			if ("".equals(n)) {
				Toast.makeText(AddFriendsActivity.this, "好友账号不能为空！", Toast.LENGTH_SHORT).show();
			}else{
				String host =  CommandBase.instance().getHost();
				final String jid = inputFriendsIdView.getText().toString() + "@"+host.substring(0,host.indexOf(":"));
				System.out.println("添加的好友jid:"+jid);
				expertLayout.setVisibility(View.GONE);
				expertListView.setVisibility(View.GONE);
				searchList = new ArrayList<HashMap<String,Object>>();
				CommandBase.instance().request(new TaskListener() {
					@Override
					public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
					}
					@Override
					public void start() {
						dialog.setMessage("正在查找，请稍后...");
						dialog.show();
					}
					@Override
					public String requestUrl() {
						return "UserInfoForName";
					}
					@Override
					public JSONObject requestData() {
						JSONObject object = new JSONObject();
						try {
							object.put("account", inputFriendsIdView.getText().toString());
						} catch (JSONException e) {
							object=null;
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
						//解析
						System.out.println("msg="+msg);
						HashMap<String, Object> map=null;
						try {
							JSONObject object = msg.getJSONObject("data");
							object = object.getJSONObject("user");
							String userName = object.getString("user_name");
							String nickName = object.getString("user_display_name");
							String user_remark = object.getString("user_remark");
							map = new HashMap<String, Object>();
							map.put("userJID", userName);
							if(nickName.equals("")){
								nickName = userName;
							}
							map.put("userName", nickName);
							map.put("description", user_remark);
							searchList.add(map);
						} catch (JSONException e) {
							e.printStackTrace();
							System.out.println("解析异常");
						}
					}
					@Override
					public void finish() {
						dialog.dismiss();
						searchFriendLayout.setVisibility(View.VISIBLE);
						if(searchList.size()>0){
							searchAdapter = new ExpertAdapter(searchList);
							searchListView.setAdapter(searchAdapter);
							searchResultView.setVisibility(View.GONE);
							searchListView.setVisibility(View.VISIBLE);
						}else{
							searchResultView.setVisibility(View.VISIBLE);
							searchListView.setVisibility(View.GONE);
						}
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
			break;
		default:
			break;
		}
		
	}
	private class ExpertAdapter extends BaseAdapter{
		private List<HashMap<String, Object>> friends= null;
		
		public ExpertAdapter(List<HashMap<String, Object>> friends) {
			this.friends = friends;
		}
		@Override
		public int getCount() {
			return friends.size();
		}
		@Override
		public Object getItem(int arg0) {
			return friends.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			convertView= AddFriendsActivity.this.getLayoutInflater().inflate(R.layout.group_item, null);
			ImageView imageView1 = (ImageView)convertView.findViewById(R.id.userAvatar_1);
			TextView textView1 = (TextView)convertView.findViewById(R.id.userName_1);
			TextView textView2 = (TextView)convertView.findViewById(R.id.userID_1);
			TextView textView3 = (TextView)convertView.findViewById(R.id.state_and_words);
			ImageView imageView2 = (ImageView)convertView.findViewById(R.id.login_where);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = friends.get(arg0);
			textView1.setText(map.get("userName").toString());
			textView2.setText(map.get("userJID").toString());
			imageView2.setVisibility(View.INVISIBLE);
			if ((map.get("description").toString()!=null) || !map.get("description").toString().equals("")) {
				textView3.setText("简介："+map.get("description"));
			} else {
				textView3.setText("简介：无");
			}
			return convertView;
		}
		
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
		if(NetWorkDetect.detect(AddFriendsActivity.this)){
			ConnectionUtils.getConnection(AddFriendsActivity.this).getRoster()
					.createEntry(userJid, nickname, groups);
		}else{
			Toast.makeText(AddFriendsActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
		}
	}
//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(AddFriendsActivity.this);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 0, 0, "刷新");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId()==0){
			getFriends();
		}
		return super.onMenuItemSelected(featureId, item);
	}	
	
	/*private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				break;
			}
		}
	}*/
	
}
