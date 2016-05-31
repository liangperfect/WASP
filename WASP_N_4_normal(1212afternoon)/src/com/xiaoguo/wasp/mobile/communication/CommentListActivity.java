package com.xiaoguo.wasp.mobile.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class CommentListActivity extends Activity implements OnClickListener {

	private TextView showTextView, TitleTextView;
	private CommandBase commandBase;
	private ProgressDialog mProgressDialog;
	private String contentStr;
	private int selectExchangeID;
	private Intent mIntent;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mlistView;
	private CommentAdapter mAdapter;
	private List<HashMap<String, Object>> contentList;
	private Button exitBtn;
	private int index = 0;
	private int rowCount = 10;
	private TimeUtil mTimeUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_list);
		initView();
		initData();
		addListenter();
	}

	private void addListenter() {

		mPullToRefreshListView
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
								return "selectExchangeCommentList";
							}

							@Override
							public JSONObject requestData() {
								JSONObject data = new JSONObject();
								try {
									data.put("exchange_id", selectExchangeID);
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

									System.out.println("list.length--->>"
											+ list.length());

									if (0 == list.length()) {
										index--;
										Toast.makeText(
												CommentListActivity.this,
												"没有新评论...", Toast.LENGTH_SHORT)
												.show();
									} else {
										for (int i = 0; i < list.length(); i++) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											JSONObject item = list
													.getJSONObject(i);
											map.put("ec_publish_cnt",
													item.getString("ec_publish_content"));
											map.put("ec_publisher_name",
													item.getString("ec_publisher_name"));
											JSONObject timeObject = item
													.getJSONObject("ec_publish_time");
											String time = mTimeUtil
													.getDateToString(timeObject
															.getLong("time"));
											map.put("ec_publish_time", time);
											contentList.add(0, map);
										}
									}

								} catch (JSONException e) {
									e.printStackTrace();
								}

							}

							@Override
							public void finish() {
								mAdapter.notifyDataSetChanged();
								mPullToRefreshListView.onRefreshComplete();
							}

							@Override
							public String filepath() {
								return null;
							}

							@Override
							public void failure(String str) {
								mPullToRefreshListView.onRefreshComplete();
							}

							@Override
							public String contentype() {
								return null;
							}
						});

					}

				});

		mPullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {

					}
				});

	}

	private void initData() {
		mTimeUtil = TimeUtil.getTimeUtilInstance();
		/**
		 * 锟斤拷锟斤拷锟�
		 */
		contentList = new ArrayList<HashMap<String, Object>>();
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("123", "1");
		// contentList.add(map);
		// contentList.add(map);

		mProgressDialog.setTitle("正在获取数据中..");
		commandBase = CommandBase.instance();
		mIntent = getIntent();
		selectExchangeID = mIntent.getIntExtra("selectExchangeID", 0);

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
				return "selectExchangeCommentList";
			}

			@Override
			public JSONObject requestData() {
				JSONObject data = new JSONObject();
				try {
					data.put("exchange_id", selectExchangeID);
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
					for (int i = 0; i < list.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject item = list.getJSONObject(i);
						map.put("ec_publish_cnt",
								item.getString("ec_publish_content"));
						map.put("ec_publisher_name",
								item.getString("ec_publisher_name"));
						JSONObject timeObject = item
								.getJSONObject("ec_publish_time");
						String time = mTimeUtil.getDateToString(timeObject
								.getLong("time"));
						map.put("ec_publish_time", time);
						contentList.add(map);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				mAdapter = new CommentAdapter(CommentListActivity.this,
						contentList);
				mPullToRefreshListView.setAdapter(mAdapter);
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
				mProgressDialog.dismiss();
			}

			@Override
			public String contentype() {
				return null;
			}
		});
	}

	private void initView() {
		mProgressDialog = new ProgressDialog(CommentListActivity.this);
		showTextView = (TextView) findViewById(R.id.showTextView);
		exitBtn = (Button) findViewById(R.id.bt_left);
		exitBtn.setVisibility(View.VISIBLE);
		exitBtn.setOnClickListener(this);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.comment_list);
		mlistView = mPullToRefreshListView.getRefreshableView();
		TitleTextView = (TextView) findViewById(R.id.title);
		TitleTextView.setText(R.string.comment_list);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment_list, menu);
		return true;
	}

	private class CommentAdapter extends BaseAdapter {

		private Context mContext;
		private List<HashMap<String, Object>> list;

		public CommentAdapter(Context context,
				List<HashMap<String, Object>> list) {

			this.mContext = context;
			this.list = list;

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
			ViewHolder holder = new ViewHolder();
			HashMap<String, Object> map = list.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.comment_list_item, null);

				holder.userPic = (ImageView) convertView
						.findViewById(R.id.comment_user_pic);
				holder.commentUserName = (TextView) convertView
						.findViewById(R.id.comment_user_name);
				holder.commentTime = (TextView) convertView
						.findViewById(R.id.commtent_time);
				holder.commentContent = (TextView) convertView
						.findViewById(R.id.comment_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.commentUserName.setText((CharSequence) map
					.get("ec_publisher_name"));
			holder.commentContent.setText(Html.fromHtml((String) map
					.get("ec_publish_cnt")));
			holder.commentTime.setText((CharSequence) map
					.get("ec_publish_time"));
			return convertView;
		}
	}

	public class ViewHolder {
		ImageView userPic;
		TextView commentUserName;
		TextView commentTime;
		TextView commentContent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			CommentListActivity.this.finish();
			overridePendingTransition(0, R.anim.to_left_out);
			break;
		}

	}

}
