package com.xiaoguo.wasp.mobile.forecast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

public class YearForecastFragment extends Fragment {
	private PullToRefreshListView mPullRefreshListView;
	private CommandBase commandBase;
	private View rootView;
	private Activity parentActivity;
	private ListView mListView;
	private ArrayList<HashMap<String, Object>> content;
	private MonthForecastContentAdapter mAdapter;
	private TimeUtil timeUtil;
	// 每次获取预报的条数
	private int rowCount = 10;
	private int index = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.year_forecast, container, false);
		initView();
		addListener();
		return rootView;
	}

	private void addListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				System.out.println("你点击了第" + position + "个item");
				int forecastID = (Integer) content.get(position - 1).get(
						"forecastID");
				String contentTitleStr = (String) content.get(position - 1)
						.get("title");
				String cotentTimeStr = (String) content.get(position - 1).get(
						"time");
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putInt("forcastid", forecastID);
				b.putString("title", "年度预报详情");
				b.putString("contentTitleStr", contentTitleStr);
				b.putString("contentTimeStr", cotentTimeStr);
				i.putExtras(b);
				i.setClass(parentActivity, ForecastInfoDetailActivity.class);
				startActivity(i);
			}
		});

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						index++;

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
								return "selectForecastList";
							}

							@Override
							public JSONObject requestData() {
								JSONObject data = new JSONObject();
								try {
									data.put("type", "年度预报");
									data.put("rowCount", rowCount);
									data.put("offset", index * rowCount);
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
								System.out.println("从服务器返回的数据-->>"
										+ msg.toString());
								/**
								 * 对5天的天气预报数据做解析
								 */

								try {
									JSONObject data = msg.getJSONObject("data");
									JSONArray list = data.getJSONArray("list");
									if (0 == list.length()) {
										Toast.makeText(getActivity(),
												"没有新数据了...", Toast.LENGTH_SHORT)
												.show();
										index--;
									} else {
										for (int i = 0; i < list.length(); i++) {
											JSONObject item = list
													.getJSONObject(i);
											System.out.println("item-->>"
													+ item.toString());
											HashMap<String, Object> map = new HashMap<String, Object>();
											JSONObject publishDate = item
													.getJSONObject("forecast_update_date");

											map.put("time",
													timeUtil.getDateToString(publishDate
															.getLong(("time"))));
											// System.out.println("publishDate.getInt(time)-->>"
											// + timeUtil
											// .getDateToString(publishDate
											// .getInt("time")));
											map.put("title",
													item.getString("forecast_title"));
											map.put("forecastID",
													item.getInt("forecast_id"));
											map.put("forecast_publisher_name",
													item.get("forecast_update_name"));
											content.add(0, map);
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
								mPullRefreshListView.onRefreshComplete();
							}

							@Override
							public String contentype() {
								return null;
							}
						});

					}
				});
	}

	private void initView() {
		commandBase = CommandBase.instance();
		timeUtil = TimeUtil.getTimeUtilInstance();
		mPullRefreshListView = (PullToRefreshListView) rootView
				.findViewById(R.id.year_forecast_content_list);
		mListView = mPullRefreshListView.getRefreshableView();
		parentActivity = this.getActivity();
		registerForContextMenu(mListView);

		// 假数据
		content = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		// map1.put("title", "年度预报冰雹");
		// map1.put("time", "2014/12/29");
		// map1.put("forecastID", 1);
		// HashMap<String, Object> map2 = new HashMap<String, Object>();
		// map2.put("title", "年度预报雨加雪");
		// map2.put("time", "2014/12/29");
		// map2.put("forecastID", 1);
		// content.add(map1);
		// content.add(map2);
		// ------------------------
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {

			}

			@Override
			public String requestUrl() {
				return "selectForecastList";
			}

			@Override
			public JSONObject requestData() {
				JSONObject data = new JSONObject();
				try {
					data.put("type", "年度预报");
					data.put("rowCount", rowCount);
					data.put("offset", 0);
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
				System.out.println("从服务器返回的数据-->>" + msg.toString());
				/**
				 * 对5天的天气预报数据做解析
				 */

				try {
					JSONObject data = msg.getJSONObject("data");
					JSONArray list = data.getJSONArray("list");
					if (0 == list.length()) {
						Toast.makeText(getActivity(), "没有新数据了...",
								Toast.LENGTH_SHORT).show();
						index--;
					} else {
						for (int i = 0; i < list.length(); i++) {
							JSONObject item = list.getJSONObject(i);
							System.out.println("item-->>" + item.toString());
							HashMap<String, Object> map = new HashMap<String, Object>();
							JSONObject publishDate = item
									.getJSONObject("forecast_update_date");

							map.put("time", timeUtil
									.getDateToString(publishDate
											.getLong(("time"))));
							map.put("title", item.getString("forecast_title"));
							map.put("forecastID", item.getInt("forecast_id"));
							map.put("forecast_publisher_name",
									item.get("forecast_update_name"));
							content.add(map);
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
				mPullRefreshListView.onRefreshComplete();
			}

			@Override
			public String contentype() {
				return null;
			}
		});
		mAdapter = new MonthForecastContentAdapter(parentActivity, content);
		mListView.setAdapter(mAdapter);
		mListView.setVerticalScrollBarEnabled(true);

	}

	private class MonthForecastContentAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> contentList;
		private Context context;

		public MonthForecastContentAdapter(Context context,
				ArrayList<HashMap<String, Object>> contentList) {
			this.context = context;
			this.contentList = contentList;
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
			HashMap<String, Object> itemContent = contentList.get(position);
			System.out.println("temContent.get(time)--->>"
					+ itemContent.get("time"));
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.disaster_item, null);
				holder.publishTime = (TextView) convertView
						.findViewById(R.id.publish_time);
				holder.title = (TextView) convertView
						.findViewById(R.id.disaster_item_title);
				holder.itemCue = (ImageView) convertView
						.findViewById(R.id.disaster_item_cue);
				holder.publisherName = (TextView) convertView
						.findViewById(R.id.publisher_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (itemContent != null) {
				holder.publishTime.setText((CharSequence) itemContent
						.get("time"));
				holder.title.setText((CharSequence) itemContent.get("title"));
				holder.publisherName.setText((CharSequence) itemContent
						.get("forecast_publisher_name"));
			}
			return convertView;
		}
	}

	public class ViewHolder {
		private TextView publishTime;
		private ImageView itemCue;
		private TextView title;
		private TextView publisherName;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
