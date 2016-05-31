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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductPromptActivity extends Activity {

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
		viewFooter = LayoutInflater.from(ProductPromptActivity.this).inflate(
				R.layout.my_pull_dowm_footer, null);
		mProgressDialog = new ProgressDialog(ProductPromptActivity.this);
		mProgressDialog.setTitle("获取数据中...");
		title = (TextView) findViewById(R.id.title);
		title.setText("生产指导");
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
						content.clear();

					}

					@Override
					public String requestUrl() {
						return "selectProduceGuideList";
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
						System.out.println("生产指导返回的数据是--->>" + msg.toString());

						try {
							JSONObject data = msg.getJSONObject("data");
							JSONArray list = data.getJSONArray("list");
							for (int i = 0; i < list.length(); i++) {
								JSONObject listItem = list.getJSONObject(i);
								HashMap<String, Object> map = new HashMap<String, Object>();
								String alarmTitleStr = listItem
										.getString("produceguide_title");
								map.put("produceguide_title", alarmTitleStr);
								String name = listItem
										.getString("produceguide_update_name");
								map.put("produceguide_publisher_name", name);
								JSONObject alarmTime = listItem
										.getJSONObject("produceguide_update_date");
								String alarmTimeStr = timeUtil
										.getDateToString(alarmTime
												.getLong("time"));
								map.put("produceguide_time", alarmTimeStr);

								int alarmID = listItem
										.getInt("produceguide_id");
								map.put("produceguideID", alarmID);
								content.add( map);

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void finish() {
						disaterList.onRefreshComplete();
						mAdapter.notifyDataSetChanged();
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
						Toast.makeText(ProductPromptActivity.this,
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
								return "selectProduceGuideList";
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

								System.out.println("生产指导返回的数据是--->>"
										+ msg.toString());

								try {
									JSONObject data = msg.getJSONObject("data");
									JSONArray list = data.getJSONArray("list");

									if (list.length() == 0) {

										index--;
										Toast.makeText(
												ProductPromptActivity.this,
												"没有新数据了", Toast.LENGTH_SHORT)
												.show();

										ProductPromptActivity.this.list
												.removeFooterView(viewFooter);

									} else {

										for (int i = 0; i < list.length(); i++) {
											JSONObject listItem = list
													.getJSONObject(i);
											HashMap<String, Object> map = new HashMap<String, Object>();
											String alarmTitleStr = listItem
													.getString("produceguide_title");
											map.put("produceguide_title",
													alarmTitleStr);
											String name = listItem
													.getString("produceguide_update_name");
											map.put("produceguide_publisher_name",
													name);
											JSONObject alarmTime = listItem
													.getJSONObject("produceguide_update_date");
											String alarmTimeStr = timeUtil
													.getDateToString(alarmTime
															.getLong("time"));
											map.put("produceguide_time",
													alarmTimeStr);
											int alarmID = listItem
													.getInt("produceguide_id");
											map.put("produceguideID", alarmID);
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
								Toast.makeText(ProductPromptActivity.this, str,
										Toast.LENGTH_SHORT).show();
								index--;
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

				HashMap<String, Object> intentData = new HashMap<String, Object>();
				intentData = content.get(position - 1);
				int id = (Integer) intentData.get("produceguideID");
				Intent i = new Intent();
				i.putExtra("productGuideId", id);
				i.setClass(ProductPromptActivity.this,
						ProductGuideDetailedActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.from_right_into,
						R.anim.to_left_out);
			}
		});

	}

	private void initData() {
		timeUtil = TimeUtil.getTimeUtilInstance();
		content = new ArrayList<HashMap<String, Object>>();
		mAdapter = new DisasterAdapter(content, ProductPromptActivity.this);
		list.setAdapter(mAdapter);
		list.setVerticalScrollBarEnabled(true);
		// 获取服务器数据
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(ProductPromptActivity.this,
				ProductPromptActivity.this);
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
				return "selectProduceGuideList";
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
				System.out.println("生产指导返回的数据是--->>" + msg.toString());

				try {
					JSONObject data = msg.getJSONObject("data");
					JSONArray list = data.getJSONArray("list");
					for (int i = 0; i < list.length(); i++) {
						JSONObject listItem = list.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						String alarmTitleStr = listItem
								.getString("produceguide_title");
						map.put("produceguide_title", alarmTitleStr);
						String name = listItem
								.getString("produceguide_update_name");
						map.put("produceguide_publisher_name", name);
						JSONObject alarmTime = listItem
								.getJSONObject("produceguide_update_date");
						String alarmTimeStr = timeUtil
								.getDateToString(alarmTime.getLong("time"));
						map.put("produceguide_time", alarmTimeStr);

						int alarmID = listItem.getInt("produceguide_id");
						map.put("produceguideID", alarmID);
						content.add(map);

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				mAdapter.notifyDataSetChanged();
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
				Toast.makeText(ProductPromptActivity.this, "错误提示信息-->>" + str,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public String contentype() {
				return null;
			}
		});

	}

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
				convertView = LayoutInflater.from(ProductPromptActivity.this)
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
					.get("produceguide_title"));
			viewHolder.publishTime.setText((CharSequence) map
					.get("produceguide_time"));

			viewHolder.publisherName.setText((CharSequence) map
					.get("produceguide_publisher_name"));

			return convertView;
		}

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
