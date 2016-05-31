package com.xiaoguo.wasp.mobile.ui.monitor;

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
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.model.condititon;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;

public class AllGreenHouseActivity extends Activity implements OnClickListener {
	private Button backView;
	private TextView titleView;

	private PullDownView pullDownView;
	private ListView listView;

	private List<HashMap<String, Object>> list = null;
	private HashMap<String, Object> map = null;
	private GreenHouseAdapter adapter = null;

	private CommandBase commandBase;
	private ProgressDialog dialog = null;
	private UserSettingInfo userSettingInfo = null;
	private int offset = 0;
	private int row_count = 5;
	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_MORE = 1;
	private String userType = "";
	int provinceID, townID, tagID, villageID;
	condititon con = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_greenhouse);

		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(AllGreenHouseActivity.this,
				AllGreenHouseActivity.this);
		dialog = new ProgressDialog(this);
		userSettingInfo = new UserSettingInfo(this);
		userType = userSettingInfo.getType();
		/**
		 * 暂时屏蔽掉的内容,在权限的时候加上
		 */
		// if (!userType.equals("farmer")) {
		// Intent data = getIntent();
		// Bundle dataBunlde = data.getBundleExtra("data");
		// con = new condititon();
		// con = dataBunlde.getParcelable("condition");
		// provinceID = con.getProvinceID();
		// townID = con.getTownID();
		// tagID = con.getTagID();
		// villageID = con.getVillageID();
		// }

		initView();
	}

	private void initView() {
		backView = (Button) findViewById(R.id.bt_left);
		// backView.setVisibility(View.VISIBLE);
		// backView.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("场站列表");

		pullDownView = (PullDownView) findViewById(R.id.green_house_list);
		listView = pullDownView.getListView();
		listView.setDividerHeight(25);
		listView.setDivider(getResources().getDrawable(R.drawable.white));
		listView.setBackgroundColor(getResources().getColor(R.color.white));
		listView.setVerticalScrollBarEnabled(false);

		list = new ArrayList<HashMap<String, Object>>();
		getList("load");
		adapter = new GreenHouseAdapter(list, AllGreenHouseActivity.this);
		listView.setAdapter(adapter);
		pullDownView.notifyDidMore();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				map = new HashMap<String, Object>();
				map = list.get(arg2 - 1);
				Intent i = new Intent();
				Bundle bundle = new Bundle();
				System.out.println("传过去的大棚id="
						+ Integer.parseInt(map.get("houseid").toString()));
				bundle.putInt("houseid",
						Integer.parseInt(map.get("houseid").toString()));
				bundle.putString("housename", map.get("housename").toString());
				bundle.putString("houselocation", map.get("houselocation")
						.toString());
				bundle.putString("houseowner", map.get("houseowner").toString());
				bundle.putString("housetag", map.get("housetag").toString());
				bundle.putInt("houseTagId",
						Integer.parseInt(map.get("houseTagId").toString()));
				bundle.putInt("fromListWhere", arg2 - 1);
				i.putExtra("bundle", bundle);
				i.setClass(AllGreenHouseActivity.this,
						RealtimeMoniterActivity.class);
				// startActivity(i);
				startActivityForResult(i, 1);
			}
		});

		pullDownView.setOnPullDownListener(new OnPullDownListener() {
			@Override
			public void onRefresh() {
			}

			@Override
			public void onMore() {
				getList("more");
			}
		});
		pullDownView.enableAutoFetchMore(true, 5);
		// 隐藏 并禁用尾部
		// pullDownView.setHideFooter();
		// 显示并启用自动获取更多
		pullDownView.setShowFooter();
		// 隐藏并且禁用头部刷新
		pullDownView.setHideHeader();
		// 显示并且可以使用头部刷新
		// pullDownView.setShowHeader();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private List<HashMap<String, Object>> getList(final String loadType) {
		final List<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
		commandBase.request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				dialog.setMessage("正在请求数据，请稍等...");
				// dialog.show();
			}

			@Override
			public String requestUrl() {
				if ("farmer".equals(userType)) {
					return "selectAllAreaForUser";
				} else {
					return "selectAreaCondition";
				}
			}

			@Override
			public JSONObject requestData() {
				if ("load".equals(loadType)) {
					offset = 0;
					row_count = 5;
				} else {
					offset = list.size();
					row_count = 5;
				}
				JSONObject object = new JSONObject();
				try {
					if ("farmer".equals(userType)) {
						object.put("account", userSettingInfo.getAccount());
						object.put("offset", offset);
						object.put("row_count", row_count);
					} else {
						object.put("area_tag_id", tagID);
						object.put("xian_id", townID);
						object.put("city_id", 0);
						object.put("province_id", provinceID);
						object.put("village_id", villageID);
						object.put("account", userSettingInfo.getAccount());
						object.put("offset", offset);
						object.put("row_count", 5);
					}
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
				JSONObject jsonObject = new JSONObject();
				JSONArray array = new JSONArray();
				// 区域id
				int area_id = 0;
				// 区域表签名
				String area_tag_name = "";
				// 大棚名
				String area_name = "";
				// 大棚用户名
				String area_user_name = "";
				// 大棚镇名
				String village_name = "";
				// 大棚标签id
				int houseTagId = 0;
				try {
					jsonObject = msg.getJSONObject("data");
					array = jsonObject.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						jsonObject = array.getJSONObject(i);
						area_id = jsonObject.getInt("area_id");
						area_tag_name = jsonObject.getString("area_tag_name");
						area_name = jsonObject.getString("area_name");
						area_user_name = jsonObject.getString("area_user_name");
						village_name = jsonObject.getString("village_name");
						houseTagId = jsonObject.getInt("area_tag_id");
						map = new HashMap<String, Object>();
						map.put("housename", area_name);
						map.put("houselocation", village_name);
						map.put("houseowner", area_user_name);
						map.put("housetag", area_tag_name);
						map.put("houseid", area_id);
						map.put("houseTagId", houseTagId);
						tempList.add(map);
					}
					Message message = new Message();
					if ("load".equals(loadType)) {
						message = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
						message.obj = tempList;
						message.sendToTarget();
					} else {
						// System.out.println("加载更多的数据");
						message = mUIHandler.obtainMessage(WHAT_DID_MORE);
						message.obj = tempList;
						System.out.println("获取list有多长" + tempList.size() + "");
						message.sendToTarget();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				dialog.dismiss();
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
		return null;
	}

	private Handler mUIHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				List<HashMap<String, Object>> lists = (List<HashMap<String, Object>>) msg.obj;
				list = new ArrayList<HashMap<String, Object>>();
				list.addAll(lists);
				// adapter.notifyDataSetChanged();
				adapter = new GreenHouseAdapter(list,
						AllGreenHouseActivity.this);
				listView.setAdapter(adapter);
				if (lists.size() < 5) {
					pullDownView.setHideFooter();
				}
				break;
			}
			case WHAT_DID_MORE: {
				List<HashMap<String, Object>> lists = (List<HashMap<String, Object>>) msg.obj;
				if (lists.size() == 0) {
					pullDownView.setHideFooter();
					Toast.makeText(AllGreenHouseActivity.this, "没有更多的大棚", 1)
							.show();
				}

				list.addAll(lists);
				adapter.notifyDataSetChanged();
				break;
			}
			}
		}
	};

	private class GreenHouseAdapter extends BaseAdapter {
		// private List<HashMap<String, Object>> list = null;
		private Context context = null;
		private HashMap<String, Object> map = null;

		public GreenHouseAdapter(List<HashMap<String, Object>> list1,
				Context context) {
			super();
			list = list1;
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
			map = list.get(position);
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			convertView = _LayoutInflater.inflate(R.layout.green_house_item,
					null);
			TextView houseNameView = (TextView) convertView
					.findViewById(R.id.green_house_name);
			TextView houseLocationView = (TextView) convertView
					.findViewById(R.id.greenhouse_location);
			TextView houseOwnerView = (TextView) convertView
					.findViewById(R.id.greenhouse_owner);
			TextView houseTagView = (TextView) convertView
					.findViewById(R.id.greenhouse_tag);
			houseNameView.setText(map.get("housename").toString());
			houseLocationView.setText(map.get("houselocation").toString());
			houseOwnerView.setText(map.get("houseowner").toString());
			houseTagView.setText(map.get("housetag").toString());
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				Bundle b = data.getBundleExtra("bundle");
				int fromWhere = b.getInt("fromListWhere");
				String tagName = b.getString("tagName");
				map = new HashMap<String, Object>();
				map = list.get(fromWhere);
				map.put("housetag", tagName);
				list.remove(fromWhere);
				list.add(fromWhere, map);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
