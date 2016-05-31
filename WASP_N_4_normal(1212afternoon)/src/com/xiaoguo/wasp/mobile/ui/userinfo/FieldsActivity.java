package com.xiaoguo.wasp.mobile.ui.userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushManager;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.widget.MyAdapter;
import com.xiaoguo.wasp.mobile.widget.MyCanceAttentionBtn;
import com.xiaoguo.wasp.mobile.widget.MyOnclClikcListener;
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FieldsActivity extends Activity implements OnClickListener {
	private Button backView;
	private TextView titleView;
	private Button addView;

	private ImageView searchView;
	private EditText searchInputView;
	private ListView searchListView;
	private MyAdapter searchAdapter;
	private List<HashMap<String, Object>> searchList = null;
	private PullDownView mPullDownView;
	private CommandBase commandbase;
	private ProgressDialog progressdialog;
	UserSettingInfo userInfo = null;
	// item.xml的list
	private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private ListView mListView;
	private MyAdapter adapter;
	private List<String> mStrings = new ArrayList<String>();
	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;
	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH = 1;
	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE = 2;
	MyBroadcastReceiver receiver = null;
	String[] items = null;
	boolean[] bools = null;
	String tag_id = "";
	private UserSettingInfo userSettingInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fields);

		commandbase = CommandBase.instance();
		progressdialog = new ProgressDialog(this);
		userInfo = new UserSettingInfo(this);
		WASPApplication.getInstance().addActivity(this);

		init();

		/*
		 * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
		 */
		mPullDownView = (PullDownView) findViewById(R.id.fields_view);

		mPullDownView.setOnPullDownListener(new OnPullDownListener() {
			@Override
			public void onRefresh() {
				list = new ArrayList<HashMap<String, Object>>();
				loadData(1);
			}

			@Override
			public void onMore() {

			}
		});

		mListView = mPullDownView.getListView();
		mListView.setDividerHeight(18);
		mListView.setDivider(getResources().getDrawable(R.drawable.white));

		new ArrayAdapter<String>(this, R.layout.pulldown_item, mStrings);
		adapter = new MyAdapter(this, list, userInfo, adapter, mListView);
		mListView.setAdapter(adapter);

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

		// 之前 网上很多代码 都会导致刷新事件 跟 上下文菜单同时弹出 这里做测试。。。已经解决
		mListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.add(0, 0, 0, "1");
						menu.add(0, 1, 0, "2");
						menu.add(0, 2, 0, "3");
					}
				});

		// 加载数据 本类使用
		loadData(0);
	}

	private void init() {
		userSettingInfo = new UserSettingInfo(FieldsActivity.this);
		backView = (Button) findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("关注领域");

		addView = (Button) findViewById(R.id.bt_right);
		addView.setBackgroundResource(R.drawable.btn_add);
		addView.setVisibility(View.VISIBLE);
		addView.setOnClickListener(this);

		searchInputView = (EditText) findViewById(R.id.search_input);
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
					mPullDownView.setVisibility(View.VISIBLE);
					searchListView.setVisibility(View.GONE);
				}

			}
		});
		searchListView = (ListView) findViewById(R.id.fields_search_list);
		searchView = (ImageView) findViewById(R.id.search_img);
		searchList = new ArrayList<HashMap<String, Object>>();
		searchView.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			Intent i1 = new Intent();
			i1.setClass(FieldsActivity.this, AboutMeActivity.class);
			this.setResult(RESULT_OK, i1);
			this.finish();
			break;
		case R.id.bt_right:
			getTags();
			break;
		case R.id.search_img:
			searchList = new ArrayList<HashMap<String, Object>>();
			String search = searchInputView.getText().toString();
			HashMap<String, Object> map = null;
			for (int i = 0; i < list.size(); i++) {
				map = new HashMap<String, Object>();
				map = list.get(i);
				String temp = map.get("name").toString();
				System.out.println("temp=" + temp);
				if (temp.contains(search)) {
					searchList.add(map);
				}
			}
			mPullDownView.setVisibility(View.GONE);
//			System.out.println("查找列表的长度：" + searchList.size());
			searchAdapter = new MyAdapter(FieldsActivity.this, searchList,
					userInfo, searchAdapter, searchListView);
			System.out.println("22");
			searchListView.setAdapter(searchAdapter);
			searchListView.setVisibility(View.VISIBLE);
			System.out.println("11");
			if (searchList.size() > 0) {
			} else {
				Toast.makeText(FieldsActivity.this, "没有找到您要查询的标签",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	// 获取标签
	private void getTags() {
		final List<HashMap<String, Object>> templist = new ArrayList<HashMap<String, Object>>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {

			}

			@Override
			public String requestUrl() {
				return "TagSelList";
			}

			@Override
			public JSONObject requestData() {
				return null;
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
				System.out.println("标签msg=" + msg);
				JSONObject object;
				HashMap<String, Object> map = null;
				try {
					object = msg.getJSONObject("data");
					JSONArray array = object.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						map = new HashMap<String, Object>();
						object = array.getJSONObject(i);
						String labelName = object.getString("tag_name");
						int labelId = object.getInt("tag_id");
						int id = 0;
						int k = 0;
						for (; k < list.size(); k++) {
							id = Integer.parseInt(list.get(k).get("id")
									.toString());
							if (id == labelId) {
								break;
							}
						}
						if (k >= list.size()) {
							map.put("name", labelName);
							map.put("id", labelId);
							templist.add(map);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finish() {
				System.out.println("标签个数：" + list.size());
				// for (int j = 0; j < templist.size(); j++) {
				// System.out.println(templist.get(j));
				// }
				if (templist.size() == 0) {
					Toast.makeText(FieldsActivity.this, "没有新的领域",
							Toast.LENGTH_SHORT).show();
				} else {
					items = new String[templist.size()];
					bools = new boolean[templist.size()];
					if (templist.size() > 0) {
						for (int i = 0; i < templist.size(); i++) {
							items[i] = templist.get(i).get("name").toString();
							bools[i] = false;
						}
						final List<String> chooseResultID = new ArrayList<String>();
						final List<String> chooserReultName = new ArrayList<String>();
						new AlertDialog.Builder(FieldsActivity.this)
								.setCancelable(false)
								.setTitle("更多关注领域")
								.setMultiChoiceItems(
										items,
										bools,
										new DialogInterface.OnMultiChoiceClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which, boolean isChecked) {
												if (isChecked) {
													chooseResultID.add(templist
															.get(which).get(
																	"id")
															+ "");

													chooserReultName
															.add(templist.get(
																	which).get(
																	"name")
																	+ "");
												}
											}
										})
								.setPositiveButton("确认",
										new DialogInterface.OnClickListener() {
											// 提交给服务器的标签
											String ss = "";
											public void onClick(
													DialogInterface dialoginterface,
													int i) {
												if (chooseResultID.size() > 0) {

													// 添加区县
													String xian = userSettingInfo
															.getUserXianName()
															+ "_";

													ss = chooseResultID.get(0);
													List<String> tagBaiDu = new ArrayList<String>();
													tagBaiDu.add(xian
															+ chooserReultName
																	.get(0));
													for (int j = 1; j < chooseResultID
															.size(); j++) {
														ss = ss
																+ ","
																+ chooseResultID
																		.get(j);
														System.out.println("添加的标签是--->>"
																+ xian
																+ chooserReultName
																		.get(j));
														tagBaiDu.add(xian
																+ chooserReultName
																		.get(j));

													}

													// 往百度云提交数据
													PushManager
															.setTags(
																	FieldsActivity.this,
																	tagBaiDu);
													// 往服务器提交数据
													submitMyTag(ss);
												}
												dialoginterface.dismiss();
											}
										}).setNegativeButton("取消", null).show();// 显示对话框
					}
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

	private void submitMyTag(final String ss2) {
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {

			}

			@Override
			public String requestUrl() {
				return "FollowTag";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("tag_idList", ss2);
				} catch (JSONException e) {
					e.printStackTrace();
					object = null;
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
				System.out.println("往服务器提交数据后返回的数据是--->>" + msg.toString());
			}

			@Override
			public void finish() {
				list = new ArrayList<HashMap<String, Object>>();
				loadData(1);
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

	/*
	 * public class MyAdapter extends BaseAdapter { Context context;
	 * List<HashMap<String, Object>> list = null; public MyAdapter(Context
	 * context,List<HashMap<String, Object>> list) { this.context = context;
	 * this.list = list; }
	 * 
	 * @Override public int getCount() {
	 * System.out.println("list的长度为:"+list.size()); return list.size(); }
	 * 
	 * @Override public Object getItem(int position) { return
	 * list.get(position); }
	 * 
	 * @Override public long getItemId(int position) { return position; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { View view =
	 * LayoutInflater.from(context).inflate(R.layout.field_item,null); TextView
	 * tv1 = (TextView) view.findViewById(R.id.tv1);
	 * tv1.setText(list.get(position).get("name").toString()); JSONObject object
	 * = new JSONObject(); try { object.put("tag_idList",
	 * list.get(position).get("id").toString());
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); } MyCanceAttentionBtn
	 * btn = (MyCanceAttentionBtn) view.findViewById(R.id.cancelAttention);
	 * String tag = list.get(position).get("tag").toString();
	 * if(tag.equals("no")){ btn.setVisibility(View.INVISIBLE); }else{
	 * btn.setUserName(userInfo.getAccount());
	 * btn.setRequestInfo("FollowTag",object,"RemoveFollowTag",object);
	 * btn.setOnClickListener(MyOnclClikcListener.instance());
	 * MyOnclClikcListener.instance().setContext(FieldsActivity.this);
	 * MyOnclClikcListener.instance().setUserset(userInfo); }
	 * 
	 * return view; } }
	 */
	private Handler mUIHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				list = new ArrayList<HashMap<String, Object>>();
				list.addAll((List<HashMap<String, Object>>) msg.obj);
				adapter = new MyAdapter(FieldsActivity.this, list, userInfo,
						adapter, mListView);
				mListView.setAdapter(adapter);
				break;
			}
			case WHAT_DID_REFRESH: {
				list.addAll((List<HashMap<String, Object>>) msg.obj);
				adapter = new MyAdapter(FieldsActivity.this, list, userInfo,
						adapter, mListView);
				mListView.setAdapter(adapter);
				// 告诉它更新完毕
				break;
			}

			case WHAT_DID_MORE: {
				adapter.notifyDataSetChanged();
				break;
			}
			}
		}

	};

	private void loadData(final int i) {
		final List<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
		final List<HashMap<String, Object>> tempList1 = new ArrayList<HashMap<String, Object>>();
		final List<HashMap<String, Object>> tempList2 = new ArrayList<HashMap<String, Object>>();
		commandbase.request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				if (i == 0) {
					progressdialog.setMessage("加载中...");
					progressdialog.show();
				}
			}

			@Override
			public String requestUrl() {
				return "selectTagByUser";
			}

			@Override
			public JSONObject requestData() {

				return null;
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
				// json解析
				JSONObject data = new JSONObject();
				try {
					data = msg.getJSONObject("data");
					// JSONArray array = data.getJSONArray("list");
					JSONArray array1 = data.getJSONArray("listNO");
					JSONArray array2 = data.getJSONArray("listOK");
					String tagName = "";
					int tagId = 0;
					HashMap<String, Object> map = null;
					for (int i = 0; i < array1.length(); i++) {
						map = new HashMap<String, Object>();
						data = array1.getJSONObject(i);
						tagName = data.getString("tag_name");
						tagId = data.getInt("tag_id");
						map.put("name", tagName);
						map.put("id", tagId);
						map.put("tag", "no");
						tempList1.add(map);
					}
					for (int i = 0; i < array2.length(); i++) {
						map = new HashMap<String, Object>();
						data = array2.getJSONObject(i);
						tagName = data.getString("tag_name");
						tagId = data.getInt("tag_id");
						map.put("name", tagName);
						map.put("id", tagId);
						map.put("tag", "ok");
						tempList2.add(map);
					}
					tempList.addAll(tempList1);
					tempList.addAll(tempList2);
					Message msg1 = null;
					if (i == 0) {
						msg1 = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					} else {
						msg1 = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
					}
					msg1.obj = tempList;
					msg1.sendToTarget();
					mPullDownView.RefreshComplete();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				for (int i = 0; i < list.size(); i++) {
					System.out.println(list.get(i));
				}
				if (i == 0) {
					progressdialog.dismiss();
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

	/*
	 * @Override protected void onPause() { unregisterReceiver(receiver);
	 * super.onPause(); }
	 * 
	 * @Override protected void onResume() { receiver = new
	 * MyBroadcastReceiver(FieldsActivity.this); IntentFilter filter = new
	 * IntentFilter();
	 * 
	 * filter.addAction(Constant.ROSTER_ADDED);
	 * filter.addAction(Constant.ROSTER_DELETED);
	 * filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
	 * filter.addAction(Constant.ROSTER_UPDATED); // 好友请求
	 * filter.addAction(Constant.ROSTER_SUBSCRIPTION);
	 * filter.addAction(Constant.NEW_MESSAGE_ACTION); registerReceiver(receiver,
	 * filter); super.onResume(); }
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (searchListView.getVisibility() == 0) {
				searchListView.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
			} else {
				Intent i1 = new Intent();
				i1.setClass(FieldsActivity.this, AboutMeActivity.class);
				this.setResult(RESULT_OK, i1);
				this.finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
