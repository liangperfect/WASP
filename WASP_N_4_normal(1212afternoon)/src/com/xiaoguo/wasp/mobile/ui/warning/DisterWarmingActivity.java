package com.xiaoguo.wasp.mobile.ui.warning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.R.layout;
import com.xiaoguo.wasp.R.menu;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisterWarmingActivity extends Activity {

	private PullToRefreshListView disaterList;
	private ListView list;
	private DisasterAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> content;
	private int screemHeight, screemWidth;
	private TextView title;
	private CommandBase commandBase;
	private ProgressDialog mProgressDialog;
	private TimeUtil timeUtil;
	private int index = 0;
	private int rowCount = 10;
	private View viewFooter;
	int visibleItemCountNums = 0;
	int totalItemCountNums = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dister_warming);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screemHeight = dm.heightPixels;
		screemWidth = dm.widthPixels;
		initView();
		initData();
	}

	private void initView() {
		// 上拉刷新脚
		viewFooter = LayoutInflater.from(DisterWarmingActivity.this).inflate(
				R.layout.my_pull_dowm_footer, null);
		mProgressDialog = new ProgressDialog(DisterWarmingActivity.this);
		mProgressDialog.setTitle("获取数据中...");
		title = (TextView) findViewById(R.id.title);
		title.setText("预警服务");
		disaterList = (PullToRefreshListView) findViewById(R.id.disater_list);
		disaterList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				index = 0;
				commandBase.request(new TaskListener() {

					@Override
					public void updateCacheDate(
							List<HashMap<String, Object>> cacheData) {

					}

					@Override
					public void start() {
						// mProgressDialog.show();
						content.clear();

					}

					@Override
					public String requestUrl() {
						return "selectAlarmList";
					}

					@Override
					public JSONObject requestData() {
						JSONObject data = new JSONObject();
						try {
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
						// 返回的数据
						System.out.println("灾害预警返回的数据是--->>" + msg.toString());

						try {
							JSONObject data = msg.getJSONObject("data");
							JSONArray list = data.getJSONArray("list");
							for (int i = 0; i < list.length(); i++) {
								JSONObject listItem = list.getJSONObject(i);
								HashMap<String, Object> map = new HashMap<String, Object>();
								String alarmTitleStr = listItem
										.getString("alarm_title");
								map.put("alarm_title", alarmTitleStr);
								map.put("alarm_publisher_name",
										listItem.get("alarm_publisher_name"));
								JSONObject alarmTime = listItem
										.getJSONObject("alarm_publish_date");
								String alarmTimeStr = timeUtil
										.getDateToString(alarmTime
												.getLong("time"));
								map.put("alarm_time", alarmTimeStr);
								int alarmID = listItem.getInt("alarm_id");
								map.put("alarmID", alarmID);
								content.add(map);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void finish() {
						// mProgressDialog.dismiss();
						// list.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
						disaterList.onRefreshComplete();
						final ListView copyListView = list;
						/**
						 * 当ListView中的数据超过一个屏幕的时候显示更多加载
						 */
						list.post(new Runnable() {

							@Override
							public void run() {

								visibleItemCountNums = copyListView
										.getChildCount();
								totalItemCountNums = copyListView.getCount();
								System.out.println("visibleItemCountNums-->>"
										+ visibleItemCountNums
										+ "   totalItemCountNums--->>"
										+ totalItemCountNums);
								if (visibleItemCountNums != totalItemCountNums) {

									list.removeFooterView(viewFooter);
									list.addFooterView(viewFooter);

								} else {
									list.removeFooterView(viewFooter);

								}

							}
						});
					}

					@Override
					public String filepath() {
						return null;
					}

					@Override
					public void failure(String str) {
						mProgressDialog.dismiss();
						Toast.makeText(DisterWarmingActivity.this,
								"错误提示信息-->>" + str, Toast.LENGTH_SHORT).show();
					}

					@Override
					public String contentype() {
						return null;
					}
				});
			}
		});

		disaterList
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// 获取更多的数据,假数据
						// commandBase.request(new TaskListener() {
						//
						// @Override
						// public void updateCacheDate(
						// List<HashMap<String, Object>> cacheData) {
						//
						// }
						//
						// @Override
						// public void start() {
						// }
						//
						// @Override
						// public String requestUrl() {
						// return "selectAlarmList";
						// }
						//
						// @Override
						// public JSONObject requestData() {
						// JSONObject data = new JSONObject();
						// try {
						// data.put("offset", 0);
						// data.put("rowCount", rowCount);
						// } catch (JSONException e) {
						// e.printStackTrace();
						// }
						//
						// return data;
						// }
						//
						// @Override
						// public String readCache() {
						// return null;
						// }
						//
						// @Override
						// public boolean needCacheTask() {
						// return false;
						// }
						//
						// @Override
						// public void messageUpdated(JSONObject msg) {
						// // 返回的数据
						// System.out.println("灾害预警返回的数据是--->>"
						// + msg.toString());
						//
						// try {
						// JSONObject data = msg.getJSONObject("data");
						// JSONArray list = data.getJSONArray("list");
						// for (int i = 0; i < list.length(); i++) {
						// JSONObject listItem = list
						// .getJSONObject(i);
						// HashMap<String, Object> map = new HashMap<String,
						// Object>();
						// String alarmTitleStr = listItem
						// .getString("alarm_title");
						// map.put("alarm_title", alarmTitleStr);
						// map.put("alarm_publisher_name",
						// listItem.get("alarm_publisher_name"));
						// JSONObject alarmTime = listItem
						// .getJSONObject("alarm_publish_date");
						// String alarmTimeStr = timeUtil
						// .getDateToString(alarmTime
						// .getLong("time"));
						// map.put("alarm_time", alarmTimeStr);
						// int alarmID = listItem
						// .getInt("alarm_id");
						// map.put("alarmID", alarmID);
						// content.add(map);
						//
						// }
						//
						// } catch (JSONException e) {
						// e.printStackTrace();
						// }
						//
						// }
						//
						// @Override
						// public void finish() {
						// mAdapter.notifyDataSetChanged();
						// /**
						// * 当ListView中的数据超过一个屏幕的时候显示更多加载
						// */
						// hintFooter();
						// showFooter();
						// }
						//
						// @Override
						// public String filepath() {
						// return null;
						// }
						//
						// @Override
						// public void failure(String str) {
						// mProgressDialog.dismiss();
						// Toast.makeText(DisterWarmingActivity.this,
						// "错误提示信息-->>" + str, Toast.LENGTH_SHORT)
						// .show();
						// }
						//
						// @Override
						// public String contentype() {
						// return null;
						// }
						// });
						index++;
						commandBase.request(new TaskListener() {

							@Override
							public void updateCacheDate(
									List<HashMap<String, Object>> cacheData) {

							}

							@Override
							public void start() {
								index++;
							}

							@Override
							public String requestUrl() {
								return "selectAlarmList";
							}

							@Override
							public JSONObject requestData() {
								JSONObject data = new JSONObject();
								try {
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
									JSONObject data = msg.getJSONObject("data");
									JSONArray list = data.getJSONArray("list");

									if (list.length() == 0) {

										index--;
										Toast.makeText(
												DisterWarmingActivity.this,
												"没有新数据了", Toast.LENGTH_SHORT)
												.show();
										DisterWarmingActivity.this.list
												.removeFooterView(viewFooter);

									} else {

										for (int i = 0; i < list.length(); i++) {
											JSONObject listItem = list
													.getJSONObject(i);
											HashMap<String, Object> map = new HashMap<String, Object>();
											String alarmTitleStr = listItem
													.getString("alarm_title");
											map.put("alarm_title",
													alarmTitleStr);
											map.put("alarm_publisher_name",
													listItem.get("alarm_publisher_name"));
											JSONObject alarmTime = listItem
													.getJSONObject("alarm_publish_date");
											String alarmTimeStr = timeUtil
													.getDateToString(alarmTime
															.getLong("time"));
											map.put("alarm_time", alarmTimeStr);
											int alarmID = listItem
													.getInt("alarm_id");
											map.put("alarmID", alarmID);
											content.add(map);
										}

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							@Override
							public void finish() {
								disaterList.onRefreshComplete();
								mAdapter.notifyDataSetChanged();
							}

							@Override
							public String filepath() {
								return null;
							}

							@Override
							public void failure(String str) {
								disaterList.onRefreshComplete();
								Toast.makeText(DisterWarmingActivity.this, str,
										Toast.LENGTH_SHORT).show();

							}

							@Override
							public String contentype() {
								return null;

							}
						});

					}
				});
		disaterList.setMinimumHeight(screemHeight);
		list = disaterList.getRefreshableView();
		registerForContextMenu(list);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				/**
				 * 避免点击末尾的时候报错
				 */
				if (position - 1 <= content.size()) {
					Intent i = new Intent();
					i.setClass(DisterWarmingActivity.this,
							DisterWarmingDetailedActivity.class);
					int alarmID = (Integer) content.get(position - 1).get(
							"alarmID");
					i.putExtra("alarmID", alarmID);
					startActivity(i);
					overridePendingTransition(R.anim.from_right_into,
							R.anim.to_left_out);
				}
			}
		});

	}

	private void initData() {
		timeUtil = TimeUtil.getTimeUtilInstance();
		content = new ArrayList<HashMap<String, Object>>();
		mAdapter = new DisasterAdapter(content, DisterWarmingActivity.this);
		// list.setAdapter(mAdapter);
		list.setVerticalScrollBarEnabled(true);

		// 获取服务器数据
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(DisterWarmingActivity.this,
				DisterWarmingActivity.this);
		getListData();
	}

	// 初始化数据/获取最新的数据
	private void getListData() {

		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				mProgressDialog.show();
				content.clear();
				index = 0;
			}

			@Override
			public String requestUrl() {
				return "selectAlarmList";
			}

			@Override
			public JSONObject requestData() {
				JSONObject data = new JSONObject();
				try {
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
				// 返回的数据
				System.out.println("灾害预警返回的数据是--->>" + msg.toString());

				try {
					JSONObject data = msg.getJSONObject("data");
					JSONArray list = data.getJSONArray("list");
					for (int i = 0; i < list.length(); i++) {
						JSONObject listItem = list.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						String alarmTitleStr = listItem
								.getString("alarm_title");
						map.put("alarm_title", alarmTitleStr);
						map.put("alarm_publisher_name",
								listItem.get("alarm_publisher_name"));
						JSONObject alarmTime = listItem
								.getJSONObject("alarm_publish_date");
						String alarmTimeStr = timeUtil
								.getDateToString(alarmTime.getLong("time"));
						map.put("alarm_time", alarmTimeStr);
						int alarmID = listItem.getInt("alarm_id");
						map.put("alarmID", alarmID);
						content.add(map);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				list.setAdapter(mAdapter);
				final ListView copyListView = list;
				/**
				 * 当ListView中的数据超过一个屏幕的时候显示更多加载
				 */
				list.post(new Runnable() {

					@Override
					public void run() {

						visibleItemCountNums = copyListView.getChildCount();
						totalItemCountNums = copyListView.getCount();
						System.out.println("visibleItemCountNums-->>"
								+ visibleItemCountNums
								+ "   totalItemCountNums--->>"
								+ totalItemCountNums);
						if (visibleItemCountNums != totalItemCountNums) {
							list.addFooterView(viewFooter);

						}

					}
				});
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
				mProgressDialog.dismiss();
				Toast.makeText(DisterWarmingActivity.this, "错误提示信息-->>" + str,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public String contentype() {
				return null;
			}
		});

	}

	// public class MyTask extends AsyncTask<Void, Void, String> {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// }
	//
	// @Override
	// protected String doInBackground(Void... params) {
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// return "更新成功了";
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("content", "B");
	// content.add(map);
	// mAdapter.notifyDataSetChanged();
	// // disaterList.onRefreshComplete();
	//
	// }
	//
	// @SuppressLint("NewApi")
	// @Override
	// protected void onCancelled(String result) {
	// super.onCancelled(result);
	// }
	//
	// }

	private class DisasterAdapter extends BaseAdapter {

		List<HashMap<String, Object>> listContent = null;
		Context mContext;

		public DisasterAdapter(List<HashMap<String, Object>> Content,
				Context mContext) {
			this.listContent = Content;
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return listContent.size();
		}

		@Override
		public Object getItem(int position) {
			return listContent.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HashMap<String, Object> map = listContent.get(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(DisterWarmingActivity.this)
						.inflate(R.layout.disaster_item, null, false);

				viewHolder.disasterTitile = (TextView) convertView
						.findViewById(R.id.disaster_item_title);
				viewHolder.publishTime = (TextView) convertView
						.findViewById(R.id.publish_time);
				viewHolder.publisherName = (TextView) convertView
						.findViewById(R.id.publisher_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.disasterTitile.setText((CharSequence) map
					.get("alarm_title"));
			viewHolder.publishTime
					.setText((CharSequence) map.get("alarm_time"));
			viewHolder.publisherName.setText((CharSequence) map
					.get("alarm_publisher_name"));
			return convertView;
		}

	}

	/**
	 * 显示列表的脚
	 */
	private void showFooter() {

		list.addFooterView(viewFooter);
	}

	/**
	 * 隐藏列表的脚
	 */
	private void hintFooter() {

		list.removeFooterView(viewFooter);
	}

	public class ViewHolder {
		private TextView publishTime;
		private TextView disasterTitile;
		private ImageView disasterImageCue;
		private ImageView disasterMarking;
		private TextView publisherName;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dister_warming, menu);
		return true;
	}

}
