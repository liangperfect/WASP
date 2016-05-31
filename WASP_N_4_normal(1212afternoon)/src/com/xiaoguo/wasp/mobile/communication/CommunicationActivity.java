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
	// ListView���ܸ߶� �����ж��Ƿ񳬹��ֻ���Ļ
	private int totalHeight = 0;
	// ��Ļ�ĸ߶�
	private int windowHeight;
	// ���ظ���footerView
	View footerView;
	// TitleView�ĸ߶�
	private int titleHeight;
	// �±߲˵����ĸ߶�
	private int tabHeight;
	private ProgressDialog mProgressDialog;
	private TextView showTextView;
	private TimeUtil mTimeUtil;
	// ���ݷ�ҳ������
	private int index;
	// װ��Ŀ��������
	private ArrayList<String> blockListNames;
	// ��Ŀ����ID
	private ArrayList<String> blockListID;
	// ��ǰ����ID
	private int currentClounmID = 0;
	private int rowCount = 10;

	// ��¼��ǰ�İ��ID

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
						// ˢ��һ��index�Լ�һ��
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
													"û��������...",
													Toast.LENGTH_SHORT).show();
											// ���������ص���ǰҳ������������ڵ�ǰҳ����ˢ����
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
											"��������ʧ��,������ˢ��", Toast.LENGTH_SHORT)
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
													"û��������...",
													Toast.LENGTH_SHORT).show();
											// ���������ص���ǰҳ������������ڵ�ǰҳ����ˢ����
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
											"��������ʧ��,������ˢ��", Toast.LENGTH_SHORT)
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
				System.out.println("������е�Id��--->>"
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

				// ��ȡ���۾�������
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
		mProgressDialog.setTitle("��ȡ������...");

	}

	private void initData() {
		// �Ե�ǰ�����������

		blockListID = new ArrayList<String>();
		blockListNames = new ArrayList<String>();
		index = 0;
		// ��ǰ���ID���� ȫ������0 ����Ķ�Ӧ������ID

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
		// ��ʼ����ʱ�����
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
				// System.out.println(" ���ص�������--->>" + msg.toString());
				System.out.println("���ص�������--->>" + msg.toString());
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
						System.out.println("���ص���ID ��--->>"
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
				Toast.makeText(CommunicationActivity.this, "��������ʧ��...",
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

			// ��itemһЩ���ݽ��а�
			holder.itemTitle.setText((CharSequence) mapItem
					.get("exchange_name"));
			holder.itemReponseNums.setText(mapItem.get("commentCount") + "");
			holder.itemTime
					.setText((CharSequence) mapItem.get("exchange_time"));
			holder.itemPublisherName.setText((CharSequence) mapItem
					.get("exchange_publisher_name"));

			System.out.println("getView�����ֵ��--->"
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

	// ��ȡ��Ļ�ĸ߶�
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

	// �� ��Activity���ػ�����
	public void onActivityResultFromMainActivity(int requestCode,
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * ������ҳ�����·��ص�0,����ˢ�»�ȡ��������
		 */
		System.out.println("���ػ���û��-->>" + resultCode);
		// ˢ�º��ȡ�µĽ�������� ����Ҫ����Ϊ0
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
					// System.out.println("���ص�������--->>" + msg.toString());
					contentList.clear();
					try {

						JSONObject data = new JSONObject();
						data = msg.getJSONObject("data");
						JSONArray list = data.getJSONArray("list");
						if (list.length() == 0) {
							Toast.makeText(CommunicationActivity.this,
									"�ð��û������...", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(CommunicationActivity.this, "��������ʧ��...",
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
