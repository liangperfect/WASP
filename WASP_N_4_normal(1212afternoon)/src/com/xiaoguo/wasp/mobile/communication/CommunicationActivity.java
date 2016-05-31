package com.xiaoguo.wasp.mobile.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

public class CommunicationActivity extends Activity implements OnClickListener {
	private TextView title;
	private PullToRefreshListView mPullRefreshListView;
	private ListView contentListView;
	private CommunicationAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> contentList;
	private CommandBase commandBase;
	private Button btnSelect;
	// ListView的总高度 用于判断是否超过手机屏幕
	private int totalHeight = 0;
	// 屏幕的高度
	private int windowHeight;
	// 加载更多footerView
	View footerView;
	// TitleView的高度
	private int titleHeight;
	// 下边菜单栏的高度
	private int tabHeight;
	private ProgressDialog mProgressDialog;
	private TextView showTextView;
	private TimeUtil mTimeUtil;
	// 数据分页的索引
	private int index;
	// 装栏目分类名称
	private ArrayList<String> blockListNames;
	// 栏目分类ID
	private ArrayList<String> blockListID;
	// 当前板块的ID
	private int currentClounmID = 0;
	private int rowCount = 10;

	// 记录当前的板块ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);
		initView();
		initData();
		addListener();
	}

	private void addListener() {
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// new MyTask().execute();
						// 刷新一个index自加一次
						index++;
						if (currentClounmID == 0) {
							commandBase.request(new TaskListener() {

								@Override
								public void updateCacheDate(
										List<HashMap<String, Object>> cacheData) {

								}

								@Override
								public void start() {
								}

								@Override
								public String requestUrl() {
									return "selectExchangeList";
								}

								@Override
								public JSONObject requestData() {

									JSONObject data = new JSONObject();
									try {

										data.put("exchange_block_id", 0);
										data.put("flag", "All");
										data.put("offset", index * rowCount);
										data.put("rowCount", rowCount);

									} catch (JSONException e) {
										e.printStackTrace();
									}

									return data;
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
									try {
										JSONObject data = msg
												.getJSONObject("data");
										JSONArray list = data
												.getJSONArray("list");
										if (list.length() == 0) {
											Toast.makeText(
													CommunicationActivity.this,
													"没有新数据...",
													Toast.LENGTH_SHORT).show();
											// 将索引返回到当前页如果有新数据在当前页就能刷出来
											index--;
										} else {

											for (int i = 0; i < list.length(); i++) {
												HashMap<String, Object> map = new HashMap<String, Object>();
												JSONObject item = new JSONObject();
												item = list.getJSONObject(i);
												map.put("exchange_name",
														item.getString("exchange_name"));
												map.put("commentCount", item
														.getInt("commentCount"));
												map.put("exchange_block_name",
														item.getString("exchange_block_name"));
												JSONObject time = new JSONObject();
												time = item
														.getJSONObject("exchange_publish_date");
												map.put("exchange_time",
														TimeUtil.getDateToString(time
																.getLong("time")));
												map.put("exchange_block_id",
														item.getInt("exchange_block_id"));
												map.put("exchange_id", item
														.getInt("exchange_id"));

												contentList.add(0, map);
											}

											blockListNames.clear();
											blockListID.clear();
											JSONArray blockJsonArray = data
													.getJSONArray("blockList");
											for (int j = 0; j < blockJsonArray
													.length(); j++) {
												JSONObject blockItem = blockJsonArray
														.getJSONObject(j);
												blockListNames.add(blockItem
														.getString("exchange_block_name"));

												// blockListID[j] = blockItem
												// .getInt("exchange_block_id");

												blockListID.add(String.valueOf(blockItem
														.getInt("exchange_block_id")));

											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								}

								@Override
								public void finish() {
									mPullRefreshListView.onRefreshComplete();
									mAdapter.notifyDataSetChanged();
								}

								@Override
								public String filepath() {
									return null;
								}

								@Override
								public void failure(String str) {
									mProgressDialog.dismiss();
									Toast.makeText(CommunicationActivity.this,
											"加载数据失败,请重新刷新", Toast.LENGTH_SHORT)
											.show();
								}

								@Override
								public String contentype() {
									return null;
								}
							});
						} else {
							commandBase.request(new TaskListener() {

								@Override
								public void updateCacheDate(
										List<HashMap<String, Object>> cacheData) {

								}

								@Override
								public void start() {
								}

								@Override
								public String requestUrl() {
									return "selectExchangeList";
								}

								@Override
								public JSONObject requestData() {

									JSONObject data = new JSONObject();
									try {

										data.put("exchange_block_id",
												currentClounmID);
										data.put("flag", "All");
										data.put("offset", index * rowCount);
										data.put("rowCount", rowCount);

									} catch (JSONException e) {
										e.printStackTrace();
									}

									return data;
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
									try {
										JSONObject data = msg
												.getJSONObject("data");
										JSONArray list = data
												.getJSONArray("list");
										if (list.length() == 0) {
											Toast.makeText(
													CommunicationActivity.this,
													"没有新数据...",
													Toast.LENGTH_SHORT).show();
											// 将索引返回到当前页如果有新数据在当前页就能刷出来
											index--;
										} else {

											for (int i = 0; i < list.length(); i++) {
												HashMap<String, Object> map = new HashMap<String, Object>();
												JSONObject item = new JSONObject();
												item = list.getJSONObject(i);
												map.put("exchange_name",
														item.getString("exchange_name"));
												map.put("commentCount", item
														.getInt("commentCount"));
												map.put("exchange_block_name",
														item.getString("exchange_block_name"));
												JSONObject time = new JSONObject();
												time = item
														.getJSONObject("exchange_publish_date");
												map.put("exchange_time",
														TimeUtil.getDateToString(time
																.getLong("time")));
												map.put("exchange_block_id",
														item.getInt("exchange_block_id"));
												map.put("exchange_id", item
														.getInt("exchange_id"));
												map.put("exchange_publisher_name",
														item.getString("exchange_publisher_name"));
												contentList.add(0, map);
											}

											blockListNames.clear();
											blockListID.clear();
											JSONArray blockJsonArray = data
													.getJSONArray("blockList");
											for (int j = 0; j < blockJsonArray
													.length(); j++) {
												JSONObject blockItem = blockJsonArray
														.getJSONObject(j);
												blockListNames.add(blockItem
														.getString("exchange_block_name"));

												// blockListID[j] = blockItem
												// .getInt("exchange_block_id");

												blockListID.add(String.valueOf(blockItem
														.getInt("exchange_block_id")));

											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								}

								@Override
								public void finish() {
									mPullRefreshListView.onRefreshComplete();
									mAdapter.notifyDataSetChanged();
								}

								@Override
								public String filepath() {
									return null;
								}

								@Override
								public void failure(String str) {
									mProgressDialog.dismiss();
									Toast.makeText(CommunicationActivity.this,
											"加载数据失败,请重新刷新", Toast.LENGTH_SHORT)
											.show();
								}

								@Override
								public String contentype() {
									return null;
								}
							});

						}

					}
				});

		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {

					}
				});

		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				System.out.println("被点击中的Id是--->>"
						+ contentList.get(position - 1).get("exchange_id"));

				HashMap<String, Object> item = contentList.get(position - 1);

				int selectExchangeID = ((Integer) item.get("exchange_id"))
						.intValue();
				// exchange_timecommentCount
				String publishDate = (String) item.get("exchange_time");
				int commentCount = (Integer) item.get("commentCount");
				String contentTitle = (String) item.get("exchange_name");
				String name = (String) item.get("exchange_publisher_name");
				// final int selectExchangeID = (Integer) contentList.get(
				// position - 1).get("exchange_id");

				// 获取评论具体内容
				Intent toCommunicationDetailActivityIntent = new Intent();

				toCommunicationDetailActivityIntent.putExtra("exchange_id",
						selectExchangeID);
				toCommunicationDetailActivityIntent.putExtra("commentCount",
						commentCount);
				toCommunicationDetailActivityIntent.putExtra("exchange_time",
						publishDate);
				toCommunicationDetailActivityIntent.putExtra("content_title",
						contentTitle);
				toCommunicationDetailActivityIntent.putExtra(
						"exchange_publisher_name", name);
				toCommunicationDetailActivityIntent.setClass(
						CommunicationActivity.this,
						CommunicationDetailActivity.class);

				startActivity(toCommunicationDetailActivityIntent);

			}

		});

	}

	@SuppressLint("ResourceAsColor")
	private void initView() {
		mTimeUtil = TimeUtil.getTimeUtilInstance();
		// showTextView = (TextView) findViewById(R.id.showContent);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.communication);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.communication_content_list);
		contentListView = mPullRefreshListView.getRefreshableView();
		registerForContextMenu(contentListView);
		contentListView.setVerticalScrollBarEnabled(true);
		btnSelect = (Button) findViewById(R.id.bt_right);
		btnSelect.setVisibility(View.VISIBLE);
		btnSelect.setBackgroundResource(R.drawable.box_menu);
		btnSelect.setOnClickListener(this);
		mProgressDialog = new ProgressDialog(CommunicationActivity.this);
		mProgressDialog.setTitle("获取数据中...");

	}

	private void initData() {
		// 对当前板块名称重置

		blockListID = new ArrayList<String>();
		blockListNames = new ArrayList<String>();
		index = 0;
		// 当前板块ID设置 全部的是0 其余的对应其他的ID

		currentClounmID = 0;

		contentList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		mAdapter = new CommunicationAdapter(contentList,
				CommunicationActivity.this);
		contentListView.setAdapter(mAdapter);
		windowHeight = getWindowHeight();
		System.out.println("totalHeiht--->>" + totalHeight
				+ "   windowHeight-->>" + windowHeight);

		title.measure(MeasureSpec.makeMeasureSpec(0, 0),
				MeasureSpec.makeMeasureSpec(0, 0));
		titleHeight = title.getMeasuredHeight();

		System.out.println("titleHeight--->>" + titleHeight);

		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(CommunicationActivity.this,
				CommunicationActivity.this);
		// 初始化的时候加载
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				mProgressDialog.show();
				index = 0;
			}

			@Override
			public String requestUrl() {
				return "selectExchangeList";
			}

			@Override
			public JSONObject requestData() {

				JSONObject data = new JSONObject();
				try {

					data.put("exchange_block_id", 0);
					data.put("flag", "All");
					data.put("offset", index * rowCount);
					data.put("rowCount", rowCount);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				return data;
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
				// showTextView.setText(msg.toString());
				// System.out.println(" 返回的数据是--->>" + msg.toString());
				System.out.println("返回的数据是--->>" + msg.toString());
				contentList.clear();
				try {
					JSONObject data = msg.getJSONObject("data");
					JSONArray list = data.getJSONArray("list");
					for (int i = 0; i < list.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject item = new JSONObject();
						item = list.getJSONObject(i);
						map.put("exchange_name",
								item.getString("exchange_name"));
						map.put("commentCount", item.getInt("commentCount"));
						map.put("exchange_block_name",
								item.getString("exchange_block_name"));

						System.out.println("exchange_block_name-->>"
								+ item.getString("exchange_block_name"));

						JSONObject time = new JSONObject();
						time = item.getJSONObject("exchange_publish_date");
						map.put("exchange_time",
								TimeUtil.getDateToString(time.getLong("time")));
						map.put("exchange_block_id",
								item.getInt("exchange_block_id"));
						map.put("exchange_id", item.getInt("exchange_id"));
						map.put("exchange_publisher_name",
								item.getString("exchange_publisher_name"));
						contentList.add(map);
					}
					JSONArray blockJsonArray = data.getJSONArray("blockList");

					for (int j = 0; j < blockJsonArray.length(); j++) {
						JSONObject blockItem = blockJsonArray.getJSONObject(j);
						blockListNames.add(blockItem
								.getString("exchange_block_name"));
						System.out.println("返回的数ID 是--->>"
								+ blockItem.getInt("exchange_block_id"));
						blockListID.add(String.valueOf(blockItem
								.getInt("exchange_block_id")));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				mPullRefreshListView.onRefreshComplete();
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
				mProgressDialog.dismiss();
				Toast.makeText(CommunicationActivity.this, "加载数据失败...",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public String contentype() {
				return null;
			}
		});

	}

	private class CommunicationAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> contentList;
		private Context mContext;

		public CommunicationAdapter(ArrayList<HashMap<String, Object>> list,
				Context context) {
			this.contentList = list;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return contentList.size();
		}

		@Override
		public Object getItem(int position) {
			return contentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			HashMap<String, Object> mapItem = this.contentList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.communication_list_item, null);
				holder.itemTitle = (TextView) convertView
						.findViewById(R.id.communication_item_title);
				holder.itemTime = (TextView) convertView
						.findViewById(R.id.communication_item_time);
				holder.itemReponseNums = (TextView) convertView
						.findViewById(R.id.communication_item_response_nums);
				holder.itemPublisherName = (TextView) convertView
						.findViewById(R.id.communication_publisher_name);
				holder.itemBlockName = (TextView) convertView
						.findViewById(R.id.item_block_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 对item一些数据进行绑定
			holder.itemTitle.setText((CharSequence) mapItem
					.get("exchange_name"));
			holder.itemReponseNums.setText(mapItem.get("commentCount") + "");
			holder.itemTime
					.setText((CharSequence) mapItem.get("exchange_time"));
			holder.itemPublisherName.setText((CharSequence) mapItem
					.get("exchange_publisher_name"));

			System.out.println("getView里面的值是--->"
					+ mapItem.get("exchange_block_name"));

			holder.itemBlockName.setText((CharSequence) mapItem
					.get("exchange_block_name"));
			return convertView;

		}
	}

	private class ViewHolder {
		TextView itemTitle;
		TextView itemTime;
		TextView itemReponseNums;
		TextView itemPublisherName;
		TextView itemBlockName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.communication, menu);
		return true;
	}

	// 获取屏幕的高度
	public int getWindowHeight() {
		WindowManager wm = (WindowManager) (getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int mScreenHeigh = dm.heightPixels;
		return mScreenHeigh;

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_right:

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("blockNames", blockListNames);
			bundle.putStringArrayList("blockIDs", blockListID);
			bundle.putInt("currentID", currentClounmID);
			intent.putExtras(bundle);
			intent.setClass(CommunicationActivity.this,
					ExchangeBlockActivity.class);

			// CommunicationActivity.this.startActivityForResult(intent, 2);
			getParent().startActivityForResult(intent, 2);

			// startActivity(intent);
			overridePendingTransition(R.anim.from_right_into,
					R.anim.to_left_out);
			break;
		}

	}

	// public void onActivityResultFromMainActivity(int requestCode,
	// int resultCode, Intent data) {
	//
	// System.out.println("requestCode-->>" + requestCode
	// + "    resultCode-->>" + resultCode + "  data"
	// + data.getIntExtra("blockID", 0));
	//
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return false;

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	// 从 主Activity返回回来的
	public void onActivityResultFromMainActivity(int requestCode,
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * 将数据页数重新返回到0,便于刷新获取更多数据
		 */
		System.out.println("返回回来没有-->>" + resultCode);
		// 刷新后获取新的界面的数据 索引要重置为0
		index = 0;
		switch (resultCode) {
		case 1:
			final int column = data.getIntExtra("blockID", 0);
			commandBase.request(new TaskListener() {

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {
				}

				@Override
				public void start() {
					mProgressDialog.show();
				}

				@Override
				public String requestUrl() {
					return "selectExchangeList";
				}

				@Override
				public JSONObject requestData() {
					JSONObject data = new JSONObject();
					try {

						data.put("exchange_block_id", column);
						data.put("flag", "");
						data.put("offset", index);
						data.put("rowCount", rowCount);

					} catch (JSONException e) {
						e.printStackTrace();
					}

					return data;
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
					// System.out.println("返回的数据是--->>" + msg.toString());
					contentList.clear();
					try {

						JSONObject data = new JSONObject();
						data = msg.getJSONObject("data");
						JSONArray list = data.getJSONArray("list");
						if (list.length() == 0) {
							Toast.makeText(CommunicationActivity.this,
									"该版块没有数据...", Toast.LENGTH_SHORT).show();
							contentList.clear();
							mAdapter.notifyDataSetChanged();

						} else {

							for (int i = 0; i < list.length(); i++) {
								JSONObject jsonItem = list.getJSONObject(i);

								// holder.itemTitle.setText((CharSequence)
								// mapItem
								// .get("exchange_name"));
								// holder.itemReponseNums.setText(mapItem.get("commentCount")
								// + "");
								// holder.itemTime
								// .setText((CharSequence)
								// mapItem.get("exchange_time"));
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("exchange_name",
										jsonItem.getString("exchange_name"));
								map.put("commentCount",
										jsonItem.getInt("commentCount"));
								map.put("exchange_block_name", jsonItem
										.getString("exchange_block_name"));
								map.put("exchange_publisher_name", jsonItem
										.getString("exchange_publisher_name"));
								JSONObject time = new JSONObject();
								time = jsonItem
										.getJSONObject("exchange_publish_date");
								map.put("exchange_time", TimeUtil
										.getDateToString(time.getLong("time")));
								map.put("exchange_block_id",
										jsonItem.getInt("exchange_block_id"));
								map.put("exchange_id",
										jsonItem.getInt("exchange_id"));
								currentClounmID = jsonItem
										.getInt("exchange_id");
								contentList.add(map);
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void finish() {
					mProgressDialog.dismiss();
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {
					mProgressDialog.dismiss();
					Toast.makeText(CommunicationActivity.this, "加载数据失败...",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public String contentype() {
					return null;
				}
			});

			break;
		}

	}
}
