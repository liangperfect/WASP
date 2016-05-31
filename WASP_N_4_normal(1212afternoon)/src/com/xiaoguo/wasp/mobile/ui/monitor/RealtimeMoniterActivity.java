package com.xiaoguo.wasp.mobile.ui.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.model.condititon;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.widget.DeviceStatusOnCheckChangeListener;
import com.xiaoguo.wasp.mobile.widget.ListButton;
import com.xiaoguo.wasp.mobile.widget.ListTextView;
import com.xiaoguo.wasp.mobile.widget.MyToggleClickListener;
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;
import com.xiaoguo.wasp.mobile.widget.getLineOnClickListener;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

public class RealtimeMoniterActivity extends Activity implements
		OnClickListener {
	CommandBase commandBase = CommandBase.instance();
	private LayoutInflater inflater;
	private int itemLength;
	private TextView currentTime;
	PullDownView mPullDownView;
	ListView listView;
	List<HashMap<String, Object>> realItem;
	HashMap<String, Object> realMap = null;
	private RealAdpter realAdapter;

	private DeviceStatusOnCheckChangeListener deviceStatusOnCheckChangeListener;

	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;
	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH = 1;
	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE = 2;

	UserSettingInfo usInfo = null;
	ViewHolder holder;
	LinearLayout titleImgLayout = null;
	ImageView imageView1 = null;
	ImageView imageView2 = null;
	ImageView imageView3 = null;
	ImageView imageView4 = null;
	ImageView imageView5 = null;
	LinearLayout imgMoreLayout = null;
	ImageView imageView6 = null;

	ProgressDialog progressDialog = null;
	MyBroadcastReceiver receiver = null;
	private Button refreshBtn;
	private getLineOnClickListener LineOnClickListener;
	float[] data;
	String[] time;
	private Button backView;
	private TextView titleView;
	String type = null;
	condititon con = null;
	int provinceID, townID, tagID, villageID;
	List<HashMap<String, Object>> labelList = new ArrayList<HashMap<String, Object>>();

	private TextView houseLoactionView;
	private TextView houseOwnerView;
	private TextView houseTagView;
	private Button resetHouseInfoView;

	private int houseId = 0;
	private String houseName;
	private String houseLocation;
	private String houseOwner;
	private String houseTag;
	private int houseTagId;
	private Bundle houseBundle;
	Animation refreshAnimation;
	private int fromListWhere = 0;
	private String farmerName = "";
	private String farmerPhone = "";
	private String farmerAddress = "";
	private ProgressDialog progressdialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.realtime_moniter);

		usInfo = new UserSettingInfo(RealtimeMoniterActivity.this);
		type = usInfo.getType();
		progressDialog = new ProgressDialog(RealtimeMoniterActivity.this);
		WASPApplication.getInstance().addActivity(this);
		progressdialog = new ProgressDialog(RealtimeMoniterActivity.this);

		Intent intent = getIntent();
		houseBundle = intent.getBundleExtra("bundle");
		houseId = houseBundle.getInt("houseid");
		System.out.println("大鹏id=" + houseId);
		houseName = houseBundle.getString("housename");
		houseLocation = houseBundle.getString("houselocation");
		houseOwner = houseBundle.getString("houseowner");
		houseTag = houseBundle.getString("housetag");
		houseTagId = houseBundle.getInt("houseTagId");
		fromListWhere = houseBundle.getInt("fromListWhere");
		/*
		 * if (!type.equals("farmer")) { Intent data = getIntent(); Bundle
		 * dataBunlde = data.getBundleExtra("data"); con = new condititon(); con
		 * = dataBunlde.getParcelable("condition"); provinceID =
		 * con.getProvinceID(); townID = con.getTownID(); tagID =
		 * con.getTagID(); villageID = con.getVillageID(); }
		 */
		refreshAnimation = rotateAnimation();
		init1();
		getLabels();
		if (!"farmer".equals(type)) {
			getFarmerInfo();
		}
	}

	private void getFarmerInfo() {
		commandBase.request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
				// TODO Auto-generated method stub

			}

			@Override
			public void start() {
				// TODO Auto-generated method stub

			}

			@Override
			public String requestUrl() {
				return "UserInfoForName";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object1 = new JSONObject();
				try {
					object1.put("account", houseOwner);
				} catch (JSONException e) {
					e.printStackTrace();
					object1 = null;
				}
				return object1;
			}

			@Override
			public String readCache() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean needCacheTask() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void messageUpdated(JSONObject msg) {
				System.out.println("userinfoMSG=" + msg);
				JSONObject object = new JSONObject();
				try {
					object = msg.getJSONObject("data").getJSONObject("user");
					farmerName = object.getString("user_display_name");
					if ("".equals(farmerName)) {
						farmerName = object.getString("user_name");
					}
					farmerPhone = object.getString("user_mobile_phone");
					farmerAddress = object.getString("user_address");
					System.out.println("farmerName=" + farmerName);
					System.out.println("farmerOwner=" + farmerPhone);
					System.out.println("farmerAddress=" + farmerAddress);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

			@Override
			public String filepath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void failure(String str) {
				// TODO Auto-generated method stub

			}

			@Override
			public String contentype() {
				// TODO Auto-generated method stub
				return null;
			}
		});

	}

	private void getLabels() {
		final List<HashMap<String, Object>> templist = new ArrayList<HashMap<String, Object>>();
		commandBase.request(new TaskListener() {
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
						map.put("name", labelName);
						map.put("id", labelId);
						templist.add(map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finish() {
				labelList = templist;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			Intent i = new Intent(RealtimeMoniterActivity.this,
					AllGreenHouseActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("fromListWhere", fromListWhere);
			bundle.putString("tagName", houseTag);
			i.putExtra("bundle", bundle);
			RealtimeMoniterActivity.this.setResult(RESULT_OK, i);
			this.finish();
			break;
		case R.id.bt_right:
			// realItem.clear();
			refreshBtn.startAnimation(refreshAnimation);
			loadData("refresh");

			break;
		case R.id.house_owner:
			System.out.println("farmerName=" + farmerName);
			System.out.println("farmerOwner=" + farmerPhone);
			System.out.println("farmerAddress=" + farmerAddress);
			LayoutInflater mInflater = LayoutInflater
					.from(RealtimeMoniterActivity.this);
			View view = mInflater.inflate(R.layout.farmer_card, null);
			TextView name = (TextView) view.findViewById(R.id.farmer_name);
			name.setText(farmerName);
			TextView phone = (TextView) view.findViewById(R.id.farmer_phone);
			phone.setText(farmerPhone);
			TextView address = (TextView) view
					.findViewById(R.id.farmer_address);
			address.setText(farmerAddress);
			new AlertDialog.Builder(RealtimeMoniterActivity.this)
					.setTitle("农户名片")
					.setView(view)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		default:
			break;
		}
	}

	public Animation rotateAnimation() {
		Animation aa = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		aa.setInterpolator(new LinearInterpolator());
		aa.setRepeatCount(1000);
		aa.setFillBefore(true);
		aa.setDuration(2000);
		return aa;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "ResourceAsColor" })
	private void init1() {
		backView = (Button) findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);
		titleView = (TextView) findViewById(R.id.title);
		// titleView.setText("实时监控");
		titleView.setText(houseName);
		refreshBtn = (Button) findViewById(R.id.bt_right);
		refreshBtn.setVisibility(View.VISIBLE);

		/*
		 * refreshBtn.setBackgroundDrawable(RealtimeMoniterActivity.this.
		 * getResources() .getDrawable(R.drawable.toolbar_right));
		 */
		refreshBtn.setBackgroundResource(R.drawable.toolbar_right);
		// refreshBtn.setBackground(RealtimeMoniterActivity.this.getResources()
		// .getDrawable(R.drawable.toolbar_right));
		refreshBtn.setOnClickListener(this);
		currentTime = (TextView) findViewById(R.id.current_time);
		String time = TimeUtil.getTimeUtilInstance().TimeStamp2Date(
				System.currentTimeMillis() + "");
		currentTime.setText(time);

		houseLoactionView = (TextView) findViewById(R.id.house_location);
		houseLoactionView.setText(houseLocation);
		houseOwnerView = (TextView) findViewById(R.id.house_owner);
		houseOwnerView.setText(houseOwner);
		if (!"farmer".equals(type)) {
			houseOwnerView.setOnClickListener(this);
			houseOwnerView.setTextColor(getResources().getColor(R.color.green));
		} else {
			houseOwnerView.setTextColor(getResources().getColor(R.color.black));
		}

		houseTagView = (TextView) findViewById(R.id.house_tag);
		houseTagView.setText(houseTag);
		resetHouseInfoView = (Button) findViewById(R.id.reset_house);
		if ("farmer".equals(type)) {
			resetHouseInfoView.setVisibility(View.VISIBLE);
		} else {
			resetHouseInfoView.setVisibility(View.GONE);
		}
		resetHouseInfoView.setOnClickListener(new setTagOnClickListener());

		mPullDownView = (PullDownView) findViewById(R.id.realtime_list);
		mPullDownView.setHideFooter();
		listView = mPullDownView.getListView();
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		listView.setDividerHeight(25);
		listView.setDivider(getResources().getDrawable(R.drawable.white));
		listView.setVerticalScrollBarEnabled(false);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {

			}
		});
		loadData("load");
		realItem = getList();

	}

	private List<HashMap<String, Object>> getList() {
		List<HashMap<String, Object>> list1 = new ArrayList<HashMap<String, Object>>();
		return list1;
	}

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;

		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

	}

	static class ViewHolder {

		// 属性
		TextView tempertureTextView;
		TextView moistTextView;
		TextView soilTemperatureTextView;
		TextView photosyntheticTextView;
		TextView soilhumidityTextView;
		TextView radiationTextView;
		TextView carbondioxideTextView;
		// TextView tagTextView;
		ListTextView tagTextView;

		// 个属性的值
		TextView temperture;
		TextView moist;
		TextView soilTemperature;
		TextView photosynthetic;
		TextView soilhumidity;
		TextView radiation;
		TextView carbondioxide;
		TextView lanName;
		TextView time;

		LinearLayout controlBar1;
		TextView switch1;
		com.xiaoguo.wasp.mobile.widget.MyToggleButton switchValue1;
		TextView switchValueText1;
		TextView switch2;
		com.xiaoguo.wasp.mobile.widget.MyToggleButton switchValue2;
		TextView switchValueText2;
		LinearLayout controlBar2;
		TextView switch3;
		com.xiaoguo.wasp.mobile.widget.MyToggleButton switchValue3;
		TextView switchValueText3;
		TextView switch4;
		com.xiaoguo.wasp.mobile.widget.MyToggleButton switchValue4;
		TextView switchValueText4;

		// Button setTagButton;
		ListButton setTagButton;

		// 各个属性的范围值
		TextView temperture_wide;
		TextView moist_wide;
		TextView soilTemperature_wide;
		TextView photosynthetic_wide;
		TextView soilhumidity_wide;
		TextView radiation_wide;
		TextView carbondioxide_wide;

		// 设备状态
		TextView deviceState;
		// 电池电量text
		// device_Battery
		TextView deviceBattery;
		// 判断设备的好坏
		TextView deviceErrorFlg;

	}

	int tagId = 0;
	String tagName = "";

	class setTagOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			System.out.println("当前标签id：" + houseTagId);
			String[] itemStr = new String[labelList.size()];
			for (int i = 0; i < labelList.size(); i++) {
				itemStr[i] = labelList.get(i).get("name").toString();
			}
			tagId = houseTagId;
			new AlertDialog.Builder(RealtimeMoniterActivity.this)
					.setTitle("请选择标签")
					.setSingleChoiceItems(itemStr, houseTagId - 1,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									tagId = Integer.parseInt(labelList
											.get(item).get("id").toString());
									tagName = labelList.get(item).get("name")
											.toString();
									System.out.println("tagid=" + tagId);
									System.out.println("tagName=" + tagName);
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int arg1) {
									if (tagId != houseTagId) {
										CommandBase.instance().request(
												new TaskListener() {
													@Override
													public void updateCacheDate(
															List<HashMap<String, Object>> cacheData) {
													}

													@Override
													public void start() {
													}

													@Override
													public String requestUrl() {
														return "updateAreaTag";
													}

													@Override
													public JSONObject requestData() {
														JSONObject object = new JSONObject();
														try {
															object.put(
																	"area_id",
																	houseId);
															object.put(
																	"tag_id",
																	tagId);
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
													public void messageUpdated(
															JSONObject msg) {
														// tagTextView
														// .setText(tagName);
														houseTagView
																.setText(tagName);
														houseTag = tagName;
														houseTagId = tagId;
													}

													@Override
													public void finish() {

													}

													@Override
													public String filepath() {
														return null;
													}

													@Override
													public void failure(
															String str) {

													}

													@Override
													public String contentype() {
														return null;
													}
												});
										dialog.cancel();
									}
								}
							}).show();
		}
	}

	private class RealAdpter extends BaseAdapter {
		private List<HashMap<String, Object>> items = null;
		private Context context;
		private ListView adapterList;
		boolean[][] switchCheck;

		public RealAdpter(Context context,
				List<HashMap<String, Object>> items123, ListView adapterList) {
			this.context = context;
			this.items = items123;
			this.adapterList = adapterList;
			// 用于记录是否是打开状态
			switchCheck = new boolean[itemLength][4];

		}

		public void refreshList(List<HashMap<String, Object>> items) {
			System.out.println("44");
			this.items = items;
			this.notifyDataSetChanged();
			adapterList.setSelection(items.size() - 1);
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			final HashMap<String, Object> map = items.get(position);
			System.out.println("map=" + map);
			ViewHolder holder = null;
			int sensorUserflg = Integer.parseInt(map.get(
					"realDate_SensorUseFlag").toString());
			final String swithName[] = map.get("swithName").toString()
					.split("@");
			final String states[] = map.get("states").toString().split("@");
			String tempTags[] = map.get("swithTags").toString().split("@");
			int displayNum = Integer.parseInt(map.get("displayNum").toString());
			final int swithTags[] = new int[4];
			for (int i = 0; i < tempTags.length; i++) {
				if (tempTags[i] != null && !"".equals(tempTags[i])) {
					swithTags[i] = Integer.parseInt(tempTags[i]);
				}
			}
			if (convertView == null) {
				inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.moniter_item, null);
				holder = new ViewHolder();

				// 温度传感器
				holder.tempertureTextView = (TextView) convertView
						.findViewById(R.id.tempure);
				holder.temperture = (TextView) convertView
						.findViewById(R.id.tempure_now);
				holder.temperture_wide = (TextView) convertView
						.findViewById(R.id.tempure_wide);

				// 湿度传感器
				holder.moistTextView = (TextView) convertView
						.findViewById(R.id.moist);
				holder.moist = (TextView) convertView
						.findViewById(R.id.moist_now);
				holder.moist_wide = (TextView) convertView
						.findViewById(R.id.moist_wide);

				// 土壤温度传感器
				holder.soilTemperatureTextView = (TextView) convertView
						.findViewById(R.id.soilTemperature);
				holder.soilTemperature = (TextView) convertView
						.findViewById(R.id.soilTemperature_now);
				holder.soilTemperature_wide = (TextView) convertView
						.findViewById(R.id.soilTemperature_wide);

				// 土壤湿度传感器
				holder.soilhumidityTextView = (TextView) convertView
						.findViewById(R.id.soilhumidity);
				holder.soilhumidity = (TextView) convertView
						.findViewById(R.id.soilumidity_now);
				holder.soilhumidity_wide = (TextView) convertView
						.findViewById(R.id.soilumidity_wide);

				// 光合作用传感器
				holder.photosyntheticTextView = (TextView) convertView
						.findViewById(R.id.photosynthetic);
				holder.photosynthetic = (TextView) convertView
						.findViewById(R.id.photosynthetic_now);
				holder.photosynthetic_wide = (TextView) convertView
						.findViewById(R.id.photosynthetic_wide);

				// 硅光辐射
				holder.radiationTextView = (TextView) convertView
						.findViewById(R.id.radiation);
				holder.radiation = (TextView) convertView
						.findViewById(R.id.radiation_now);
				holder.radiation_wide = (TextView) convertView
						.findViewById(R.id.radiation_wide);

				// 二氧化碳传感器
				holder.carbondioxideTextView = (TextView) convertView
						.findViewById(R.id.carbondioxide);
				holder.carbondioxide = (TextView) convertView
						.findViewById(R.id.carbondioxide_now);
				holder.carbondioxide_wide = (TextView) convertView
						.findViewById(R.id.carbondioxide_wide);

				// 设备名称
				holder.lanName = (TextView) convertView
						.findViewById(R.id.lan_num);
				// 控制条
				holder.controlBar1 = (LinearLayout) convertView
						.findViewById(R.id.monitor_control_bar_1);
				// 控制条
				holder.controlBar2 = (LinearLayout) convertView
						.findViewById(R.id.monitor_control_bar_2);
				// 采样时间
				holder.time = (TextView) convertView
						.findViewById(R.id.tv_gather_time);
				// 电池电量
				holder.deviceBattery = (TextView) convertView
						.findViewById(R.id.tv_device_battery);
				// 设备状态
				holder.deviceState = (TextView) convertView
						.findViewById(R.id.tv_device_state);
				// 设置标签按钮 目前已经屏蔽
				holder.setTagButton = (ListButton) convertView
						.findViewById(R.id.btn_setTag);
				// 标签，目前已经屏蔽
				holder.tagTextView = (ListTextView) convertView
						.findViewById(R.id.tv_tag);

				// 水闸
				holder.switchValue1 = (com.xiaoguo.wasp.mobile.widget.MyToggleButton) holder.controlBar1
						.findViewById(R.id.toggle_value_1);
				holder.switchValueText1 = (TextView) holder.controlBar1
						.findViewById(R.id.toggle_value_text_1);
				holder.switch1 = (TextView) holder.controlBar1
						.findViewById(R.id.toggle_1);
				// 卷帘
				holder.switchValue2 = (com.xiaoguo.wasp.mobile.widget.MyToggleButton) holder.controlBar1
						.findViewById(R.id.toggle_value_2);
				holder.switchValueText2 = (TextView) holder.controlBar1
						.findViewById(R.id.toggle_value_text_2);
				holder.switch2 = (TextView) holder.controlBar1
						.findViewById(R.id.toggle_2);

				holder.switchValue3 = (com.xiaoguo.wasp.mobile.widget.MyToggleButton) holder.controlBar2
						.findViewById(R.id.toggle_value_3);
				holder.switchValueText3 = (TextView) holder.controlBar2
						.findViewById(R.id.toggle_value_text_3);
				holder.switch3 = (TextView) holder.controlBar2
						.findViewById(R.id.toggle_3);

				holder.switchValue4 = (com.xiaoguo.wasp.mobile.widget.MyToggleButton) holder.controlBar2
						.findViewById(R.id.toggle_value_4);
				holder.switchValueText4 = (TextView) holder.controlBar2
						.findViewById(R.id.toggle_value_text_4);
				holder.switch4 = (TextView) holder.controlBar2
						.findViewById(R.id.toggle_4);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				if (haveSensor(sensorUserflg, "Sensor1")) {
					System.out.println("有温度");
					holder.tempertureTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor1", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "温度",
									"温度值"));
					holder.tempertureTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("tempture").toString())) {
						System.out.println("111");
						holder.temperture.setText("异常");
						holder.temperture.setTextColor(getResources().getColor(
								R.color.red));
						System.out.println("222");
					} else {
						holder.temperture.setText(map.get("tempture")
								.toString());
						holder.temperture.setText(getResources().getColor(
								R.color.black));
						System.out.println("333");
					}
					holder.temperture_wide.setText(map.get("temptureWide")
							.toString());

					holder.tempertureTextView.setVisibility(View.VISIBLE);
					holder.temperture.setVisibility(View.VISIBLE);
					holder.temperture_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无温度");
					holder.tempertureTextView.setVisibility(View.GONE);
					holder.temperture.setVisibility(View.GONE);
					holder.temperture_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无温度传感器异常");
				e1.printStackTrace();
			}
			try {
				if (haveSensor(sensorUserflg, "Sensor2")) {
					System.out.println("有湿度");
					holder.moistTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor2", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "湿度",
									"湿度值"));
					holder.moistTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("moist").toString())) {
						System.out.println("111");
						holder.moist.setText("异常");
						holder.moist.setTextColor(getResources().getColor(
								R.color.red));
						System.out.println("222");
					} else {
						holder.moist.setText(map.get("moist").toString());
						holder.moist.setTextColor(getResources().getColor(
								R.color.black));
						System.out.println("333");
					}
					holder.moist_wide.setText(map.get("moistWide").toString());

					holder.moistTextView.setVisibility(View.VISIBLE);
					holder.moist.setVisibility(View.VISIBLE);
					holder.moist_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无湿度");
					holder.moistTextView.setVisibility(View.GONE);
					holder.moist.setVisibility(View.GONE);
					holder.moist_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无湿度传感器异常");
				e1.printStackTrace();
			}
			try {
				if (haveSensor(sensorUserflg, "Sensor3")) {
					System.out.println("有光合作用");
					holder.photosyntheticTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor3", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "光合作用",
									"光合作用有效值"));
					holder.photosyntheticTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("photosynthetic").toString())) {
						System.out.println("111");
						holder.photosynthetic.setText("异常");
						holder.photosynthetic.setTextColor(getResources()
								.getColor(R.color.red));
						System.out.println("222");
					} else {
						holder.photosynthetic.setText(map.get("photosynthetic")
								.toString());
						holder.photosynthetic.setTextColor(getResources()
								.getColor(R.color.black));
						System.out.println("333");
					}
					holder.photosynthetic_wide.setText(map.get(
							"photosyntheticWide").toString());

					holder.photosyntheticTextView.setVisibility(View.VISIBLE);
					holder.photosynthetic.setVisibility(View.VISIBLE);
					holder.photosynthetic_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无光合作用");
					holder.photosyntheticTextView.setVisibility(View.GONE);
					holder.photosynthetic.setVisibility(View.GONE);
					holder.photosynthetic_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无光合作用传感器异常");
				e1.printStackTrace();
			}

			try {
				if (haveSensor(sensorUserflg, "Sensor4")) {
					System.out.println("有硅光辐射");
					holder.radiationTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor4", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "硅光辐射",
									"硅光辐射有效值"));
					holder.radiationTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("radiation").toString())) {
						System.out.println("111");
						holder.radiation.setText("异常");
						holder.radiation.setTextColor(getResources().getColor(
								R.color.red));
						System.out.println("222");
					} else {
						holder.radiation.setText(map.get("radiation")
								.toString());
						holder.radiation.setTextColor(getResources().getColor(
								R.color.black));
						System.out.println("333");
					}
					holder.radiation_wide.setText(map.get("radiationWide")
							.toString());

					holder.radiationTextView.setVisibility(View.VISIBLE);
					holder.radiation.setVisibility(View.VISIBLE);
					holder.radiation_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无硅光辐射");
					holder.radiationTextView.setVisibility(View.GONE);
					holder.radiation.setVisibility(View.GONE);
					holder.radiation_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无硅光传感器异常");
				e1.printStackTrace();
			}
			try {
				if (haveSensor(sensorUserflg, "Sensor5")) {
					System.out.println("有土壤温度");
					holder.soilTemperatureTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor5", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "土壤温度",
									"土壤温度值"));
					holder.soilTemperatureTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("soilTemperature").toString())) {
						System.out.println("111");
						holder.soilTemperature.setText("异常");
						holder.soilTemperature.setTextColor(getResources()
								.getColor(R.color.red));
						System.out.println("222");
					} else {
						holder.soilTemperature.setText(map.get(
								"soilTemperature").toString());
						holder.soilTemperature.setTextColor(getResources()
								.getColor(R.color.black));
						System.out.println("333");
					}
					holder.soilTemperature_wide.setText(map.get(
							"soilTemperatureWide").toString());

					holder.soilTemperatureTextView.setVisibility(View.VISIBLE);
					holder.soilTemperature.setVisibility(View.VISIBLE);
					holder.soilTemperature_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无土壤温度");
					holder.soilTemperatureTextView.setVisibility(View.GONE);
					holder.soilTemperature.setVisibility(View.GONE);
					holder.soilTemperature_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无土壤温度异常");
				e1.printStackTrace();
			}

			try {
				if (haveSensor(sensorUserflg, "Sensor6")) {
					System.out.println("有土壤湿度");
					holder.soilhumidityTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor6", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "土壤湿度",
									"土壤湿度值"));
					holder.soilhumidityTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("soilhumidity").toString())) {
						System.out.println("111");
						holder.soilhumidity.setText("异常");
						holder.soilhumidity.setTextColor(getResources()
								.getColor(R.color.red));
						System.out.println("222");
					} else {
						holder.soilhumidity.setText(map.get("soilhumidity")
								.toString());
						holder.soilhumidity.setTextColor(getResources()
								.getColor(R.color.black));
						System.out.println("333");
					}
					holder.soilhumidity_wide.setText(map
							.get("soilhumidityWide").toString());

					holder.soilhumidityTextView.setVisibility(View.VISIBLE);
					holder.soilhumidity.setVisibility(View.VISIBLE);
					holder.soilhumidity_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无土壤湿度");
					holder.soilhumidityTextView.setVisibility(View.GONE);
					holder.soilhumidity.setVisibility(View.GONE);
					holder.soilhumidity_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无土壤湿度异常");
				e1.printStackTrace();
			}

			try {
				if (haveSensor(sensorUserflg, "Sensor7")) {
					System.out.println("有二氧化碳");
					holder.carbondioxideTextView
							.setOnClickListener(new getLineOnClickListener(
									"sensor7", map.get("parentID").toString(),
									RealtimeMoniterActivity.this, "日期", "二氧化碳",
									"二氧化碳浓度"));
					holder.carbondioxideTextView.getPaint().setFlags(
							Paint.UNDERLINE_TEXT_FLAG);
					if ("65535".equals(map.get("carbondioxide").toString())) {
						System.out.println("111");
						holder.carbondioxide.setText("异常");
						holder.carbondioxide.setTextColor(getResources()
								.getColor(R.color.red));
						System.out.println("222");
					} else {
						holder.carbondioxide.setText(map.get("carbondioxide")
								.toString());
						holder.carbondioxide.setTextColor(getResources()
								.getColor(R.color.black));
						System.out.println("333");
					}
					holder.carbondioxide_wide.setText(map.get(
							"carbondioxideWide").toString());

					holder.carbondioxideTextView.setVisibility(View.VISIBLE);
					holder.carbondioxide.setVisibility(View.VISIBLE);
					holder.carbondioxide_wide.setVisibility(View.VISIBLE);
					System.out.println("444");
				} else {
					System.out.println("无二氧化碳");
					holder.carbondioxideTextView.setVisibility(View.GONE);
					holder.carbondioxide.setVisibility(View.GONE);
					holder.carbondioxide_wide.setVisibility(View.GONE);
					System.out.println("555");
				}
			} catch (Exception e1) {
				System.out.println("判断有无sensor7异常");
				e1.printStackTrace();
			}
			System.out.println("holder==" + holder.lanName);
			holder.lanName.setText("111");
			String str = map.get("realData_device_name").toString();
			holder.lanName.setText(map.get("realData_device_name").toString());
			holder.time.setText(map.get("time").toString());
			holder.deviceBattery.setText(map.get("deviceBattery").toString()
					+ "%");
			String deviceState = map.get("deviceState").toString();
			holder.deviceState.setText(deviceState);
			if ("在线".equals(deviceState)) {
				holder.deviceState.setTextColor(getResources().getColor(
						R.color.green));
			} else {
				holder.deviceState.setTextColor(getResources().getColor(
						R.color.text));
			}
			/*
			 * holder.setTagButton = (Button) convertView
			 * .findViewById(R.id.btn_setTag);
			 */
			holder.setTagButton.setOnClickListener(new setTagOnClickListener());
			holder.setTagButton.setTextView(holder.tagTextView);
			holder.setTagButton.setId((Integer) map.get("areaID"));
			holder.setTagButton.setTagId((Integer) map.get("tagId"));
			if (!type.equals("farmer")) {
				holder.setTagButton.setVisibility(View.GONE);
			}
			holder.tagTextView.setText(map.get("tagName").toString());
			holder.tagTextView.setId((Integer) map.get("tagId"));

			if (displayNum < 1) {
				holder.controlBar1.setVisibility(View.GONE);
				holder.controlBar2.setVisibility(View.GONE);
			} else if (displayNum == 1) {
				holder.switchValue1.setRequestInfo(context, swithTags[0],
						states[0], map.get("parentID").toString(),
						swithName[0], usInfo, map, 0);
				holder.switch1.setText(swithName[0]);
				holder.switchValue1.setText(states[0]);
				holder.switchValueText1.setText(states[0]);
				holder.switch1.setVisibility(View.VISIBLE);
				if ("farmer".equals(type)) {
					holder.switchValue1.setVisibility(View.VISIBLE);
					holder.switchValueText1.setVisibility(View.GONE);
				} else {
					holder.switchValue1.setVisibility(View.GONE);
					holder.switchValueText1.setVisibility(View.VISIBLE);
				}
				holder.switch2.setVisibility(View.GONE);
				holder.switchValue2.setVisibility(View.GONE);
				holder.switchValueText2.setVisibility(View.GONE);
				holder.controlBar1.setVisibility(View.VISIBLE);
				holder.controlBar2.setVisibility(View.GONE);
			} else if (displayNum == 2) {
				holder.switchValue1.setRequestInfo(context, swithTags[0],
						states[0], map.get("parentID").toString(),
						swithName[0], usInfo, map, 0);
				holder.switchValue2.setRequestInfo(context, swithTags[1],
						states[1], map.get("parentID").toString(),
						swithName[1], usInfo, map, 1);
				holder.switch1.setText(swithName[0]);
				holder.switchValue1.setText(states[0]);
				holder.switchValueText1.setText(states[0]);
				holder.switch2.setText(swithName[1]);
				holder.switchValue2.setText(states[1]);
				holder.switchValueText2.setText(states[1]);
				holder.switch1.setVisibility(View.VISIBLE);
				holder.switch2.setVisibility(View.VISIBLE);
				if ("farmer".equals(type)) {
					holder.switchValue1.setVisibility(View.VISIBLE);
					holder.switchValueText1.setVisibility(View.GONE);
					holder.switchValue2.setVisibility(View.VISIBLE);
					holder.switchValueText2.setVisibility(View.GONE);
				} else {
					holder.switchValue1.setVisibility(View.GONE);
					holder.switchValueText1.setVisibility(View.VISIBLE);
					holder.switchValue2.setVisibility(View.GONE);
					holder.switchValueText2.setVisibility(View.VISIBLE);
				}
				holder.controlBar1.setVisibility(View.VISIBLE);
				holder.controlBar2.setVisibility(View.GONE);
			} else if (displayNum == 3) {
				holder.switchValue1.setRequestInfo(context, swithTags[0],
						states[0], map.get("parentID").toString(),
						swithName[0], usInfo, map, 0);
				holder.switchValue2.setRequestInfo(context, swithTags[1],
						states[1], map.get("parentID").toString(),
						swithName[1], usInfo, map, 1);
				holder.switchValue3.setRequestInfo(context, swithTags[2],
						states[2], map.get("parentID").toString(),
						swithName[2], usInfo, map, 2);
				holder.switch1.setText(swithName[0]);
				holder.switchValue1.setText(states[0]);
				holder.switchValueText1.setText(states[0]);
				holder.switch2.setText(swithName[1]);
				holder.switchValue2.setText(states[1]);
				holder.switchValueText2.setText(states[1]);
				holder.switch1.setVisibility(View.VISIBLE);
				holder.switch2.setVisibility(View.VISIBLE);
				holder.controlBar1.setVisibility(View.VISIBLE);
				holder.switch3.setText(swithName[2]);
				holder.switchValue3.setText(states[2]);
				holder.switchValueText3.setText(states[2]);
				holder.switch3.setVisibility(View.VISIBLE);
				holder.switch4.setVisibility(View.GONE);
				if ("farmer".equals(type)) {
					holder.switchValue1.setVisibility(View.VISIBLE);
					holder.switchValueText1.setVisibility(View.GONE);
					holder.switchValue2.setVisibility(View.VISIBLE);
					holder.switchValueText2.setVisibility(View.GONE);
					holder.switchValue3.setVisibility(View.VISIBLE);
					holder.switchValueText3.setVisibility(View.GONE);
					holder.switchValue4.setVisibility(View.GONE);
					holder.switchValueText4.setVisibility(View.GONE);
				} else {
					holder.switchValue1.setVisibility(View.GONE);
					holder.switchValueText1.setVisibility(View.VISIBLE);
					holder.switchValue2.setVisibility(View.GONE);
					holder.switchValueText2.setVisibility(View.VISIBLE);
					holder.switchValue3.setVisibility(View.GONE);
					holder.switchValueText3.setVisibility(View.VISIBLE);
					holder.switchValue4.setVisibility(View.GONE);
					holder.switchValueText4.setVisibility(View.GONE);
				}
				holder.controlBar1.setVisibility(View.VISIBLE);
				holder.controlBar2.setVisibility(View.VISIBLE);
			} else {
				holder.switchValue1.setRequestInfo(context, swithTags[0],
						states[0], map.get("parentID").toString(),
						swithName[0], usInfo, map, 0);
				holder.switchValue2.setRequestInfo(context, swithTags[1],
						states[1], map.get("parentID").toString(),
						swithName[1], usInfo, map, 1);
				holder.switchValue3.setRequestInfo(context, swithTags[2],
						states[2], map.get("parentID").toString(),
						swithName[2], usInfo, map, 2);
				holder.switchValue4.setRequestInfo(context, swithTags[3],
						states[3], map.get("parentID").toString(),
						swithName[3], usInfo, map, 3);
				holder.switch1.setText(swithName[0]);
				holder.switchValue1.setText(states[0]);
				holder.switchValueText1.setText(states[0]);
				holder.switch2.setText(swithName[1]);
				holder.switchValue2.setText(states[1]);
				holder.switchValueText2.setText(states[1]);
				holder.switch1.setVisibility(View.VISIBLE);
				holder.switch2.setVisibility(View.VISIBLE);
				holder.controlBar1.setVisibility(View.VISIBLE);
				holder.switch3.setText(swithName[2]);
				holder.switchValue3.setText(states[2]);
				holder.switchValueText3.setText(states[2]);
				holder.switch4.setText(swithName[3]);
				holder.switchValue4.setText(states[3]);
				holder.switchValueText4.setText(states[3]);
				holder.switch3.setVisibility(View.VISIBLE);
				holder.switch4.setVisibility(View.VISIBLE);
				if ("farmer".equals(type)) {
					holder.switchValue1.setVisibility(View.VISIBLE);
					holder.switchValueText1.setVisibility(View.GONE);
					holder.switchValue2.setVisibility(View.VISIBLE);
					holder.switchValueText2.setVisibility(View.GONE);
					holder.switchValue3.setVisibility(View.VISIBLE);
					holder.switchValueText3.setVisibility(View.GONE);
					holder.switchValue4.setVisibility(View.VISIBLE);
					holder.switchValueText4.setVisibility(View.GONE);
				} else {
					holder.switchValue1.setVisibility(View.GONE);
					holder.switchValueText1.setVisibility(View.VISIBLE);
					holder.switchValue2.setVisibility(View.GONE);
					holder.switchValueText2.setVisibility(View.VISIBLE);
					holder.switchValue3.setVisibility(View.GONE);
					holder.switchValueText3.setVisibility(View.VISIBLE);
					holder.switchValue4.setVisibility(View.GONE);
					holder.switchValueText4.setVisibility(View.VISIBLE);
				}
				holder.controlBar1.setVisibility(View.VISIBLE);
				holder.controlBar2.setVisibility(View.VISIBLE);
			}
			holder.switchValue1.setButtons(holder.switchValue2,
					holder.switchValue3, holder.switchValue4);
			holder.switchValue2.setButtons(holder.switchValue1,
					holder.switchValue3, holder.switchValue4);
			holder.switchValue3.setButtons(holder.switchValue1,
					holder.switchValue2, holder.switchValue4);
			holder.switchValue4.setButtons(holder.switchValue1,
					holder.switchValue2, holder.switchValue3);
			// 点击事件，还要判断开关状态：已打开、已关闭 未知
			final String buttonValue1 = holder.switchValue1.getText()
					.toString();
			final String buttonValue2 = holder.switchValue2.getText()
					.toString();
			final String buttonValue3 = holder.switchValue3.getText()
					.toString();
			final String buttonValue4 = holder.switchValue4.getText()
					.toString();
			if ("已打开".equals(buttonValue1)) {
				holder.switchValue1.setClickable(true);
				holder.switchValue1
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("已关闭".equals(buttonValue1)) {
				holder.switchValue1.setClickable(true);
				holder.switchValue1
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else if ("正在打开".equals(buttonValue1)
					|| "等待打开".equals(buttonValue1)) {
				holder.switchValue1.setClickable(false);
				holder.switchValue1
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("正在关闭".equals(buttonValue1)
					|| "等待关闭".equals(buttonValue1)) {
				holder.switchValue1.setClickable(false);
				holder.switchValue1
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else {
				holder.switchValue1.setClickable(false);
				holder.switchValue1
						.setBackgroundResource(R.drawable.toggle_bg_off);
			}
			if ("已打开".equals(buttonValue2)) {
				holder.switchValue2.setClickable(true);
				holder.switchValue2
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("已关闭".equals(buttonValue2)) {
				holder.switchValue2.setClickable(true);
				holder.switchValue2
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else if ("正在打开".equals(buttonValue2)
					|| "等待打开".equals(buttonValue2)) {
				holder.switchValue2.setClickable(false);
				holder.switchValue2
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("正在关闭".equals(buttonValue2)
					|| "等待关闭".equals(buttonValue2)) {
				holder.switchValue2.setClickable(false);
				holder.switchValue2
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else {
				holder.switchValue2.setClickable(false);
				holder.switchValue2
						.setBackgroundResource(R.drawable.toggle_bg_off);
			}
			if ("已打开".equals(buttonValue3)) {
				holder.switchValue3.setClickable(true);
				holder.switchValue3
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("已关闭".equals(buttonValue3)) {
				holder.switchValue3.setClickable(true);
				holder.switchValue3
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else if ("正在打开".equals(buttonValue3)
					|| "等待打开".equals(buttonValue3)) {
				holder.switchValue3.setClickable(false);
				holder.switchValue3
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("正在关闭".equals(buttonValue3)
					|| "等待关闭".equals(buttonValue3)) {
				holder.switchValue3.setClickable(false);
				holder.switchValue3
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else {
				holder.switchValue3.setClickable(false);
				holder.switchValue3
						.setBackgroundResource(R.drawable.toggle_bg_off);
			}
			if ("已打开".equals(buttonValue4)) {
				holder.switchValue4.setClickable(true);
				holder.switchValue4
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("已关闭".equals(buttonValue4)) {
				holder.switchValue4.setClickable(true);
				holder.switchValue4
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else if ("正在打开".equals(buttonValue4)
					|| "等待打开".equals(buttonValue4)) {
				holder.switchValue4.setClickable(false);
				holder.switchValue4
						.setBackgroundResource(R.drawable.toggle_bg_on);
			} else if ("正在关闭".equals(buttonValue4)
					|| "等待关闭".equals(buttonValue4)) {
				holder.switchValue4.setClickable(false);
				holder.switchValue4
						.setBackgroundResource(R.drawable.toggle_bg_off);
			} else {
				holder.switchValue4.setClickable(false);
				holder.switchValue4
						.setBackgroundResource(R.drawable.toggle_bg_off);
			}
			for (int i = 0; i < swithTags.length; i++) {
				System.out.println(swithTags[i]);
			}
			MyToggleClickListener listener1 = MyToggleClickListener.instance();
			listener1.setRequestInfo(RealtimeMoniterActivity.this);
			holder.switchValue1.setOnClickListener(listener1);

			MyToggleClickListener listener2 = MyToggleClickListener.instance();
			listener2.setRequestInfo(RealtimeMoniterActivity.this);
			holder.switchValue2.setOnClickListener(listener2);

			MyToggleClickListener listener3 = MyToggleClickListener.instance();
			listener3.setRequestInfo(RealtimeMoniterActivity.this);
			holder.switchValue3.setOnClickListener(listener3);

			MyToggleClickListener listener4 = MyToggleClickListener.instance();
			listener4.setRequestInfo(RealtimeMoniterActivity.this);
			holder.switchValue4.setOnClickListener(listener4);
			return convertView;
		}
	}

	private Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				if (msg.obj != null) {
					List<HashMap<String, Object>> tempList = (List<HashMap<String, Object>>) msg.obj;
					realItem = new ArrayList<HashMap<String, Object>>();
					if (!tempList.isEmpty()) {
						realItem.addAll(tempList);
						// realAdapter.notifyDataSetChanged();
					}

					String time = TimeUtil.getTimeUtilInstance()
							.TimeStamp2Date(System.currentTimeMillis() + "");
					currentTime.setText(time);

					for (int i = 0; i < realItem.size(); i++) {
						System.out.println(realItem.get(i));
					}
					realAdapter = new RealAdpter(RealtimeMoniterActivity.this,
							realItem, listView);
					listView.setAdapter(realAdapter);
					mPullDownView.enableAutoFetchMore(true, 1);
					// 显示并启用自动获取更多
					mPullDownView.setShowFooter();
					// 隐藏并且禁用头部刷新
					mPullDownView.setHideHeader();

				}
				// Toast.makeText(RealtimeMoniterActivity.this,
				// "" + listView.getAdapter().getCount(),
				// Toast.LENGTH_LONG).show();
				// 诉它数据加载完毕;
				break;
			}
			case WHAT_DID_REFRESH: {
				break;
			}

			case WHAT_DID_MORE: {
				HashMap<String, Object> body = (HashMap<String, Object>) msg.obj;
				realItem.add(0, body);
				realAdapter.notifyDataSetChanged();
				// adapter.notifyDataSetChanged();

				String time = TimeUtil.getTimeUtilInstance().TimeStamp2Date(
						System.currentTimeMillis() + "");
				currentTime.setText(time);
				break;
			}
			}

		}

	};

	private void loadData(final String type) {
		final List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				if (!"refresh".equals(type)) {
					progressDialog.setMessage("正在获取数据...");
					progressDialog.show();
				}
			}

			@Override
			public String requestUrl() {

				return "realData";

			}

			@Override
			public JSONObject requestData() {

				JSONObject object1 = new JSONObject();

				try {
					object1.put("account", houseOwner);
					object1.put("area_id", houseId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// }

				return object1;
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
				String tag_name = "";
				int tag_id = 0;
				String time = "";
				String realDataAreaName = "";
				String sensor1 = "";
				String sensor2 = "";
				String sensor3 = "";
				String sensor4 = "";
				String sensor5 = "";
				String sensor6 = "";
				String sensor7 = "";

				String sensor1Wide = "";
				String sensor2Wide = "";
				String sensor3Wide = "";
				String sensor4Wide = "";
				String sensor5Wide = "";
				String sensor6Wide = "";
				String sensor7Wide = "";

				String deviceBattery = "";

				int sluice = 0;
				int roller = 0;
				String id = "";
				String parentID = "";
				int areaID;
				String realData_device_name = "";
				String deviceState = "";// 设备状态
				int realDate_SensorUseFlag = 0;
				int realDate_CtrlFunction = 0;// 水闸卷帘状态
				String mapName = "";// 开关名字
				int relayState = 0;// 开关状态
				JSONObject data = new JSONObject();

				try {
					data = msg.getJSONObject("data");
					JSONArray array = new JSONArray();
					array = data.getJSONArray("list");
					itemLength = array.length();
					System.out.println("在commandBase里面的的itemlength->>"
							+ itemLength);

					for (int i = 0; i < array.length(); i++) {
						HashMap<String, Object> map1 = new HashMap<String, Object>();
						JSONObject object2 = new JSONObject();
						object2 = array.getJSONObject(i);
						tag_name = object2.getString("tag_name");
						tag_id = object2.getInt("tag_id");
						System.out.println("服务器返回的tag_name:" + tag_name);
						System.out.println("服务器返回的tag_id:" + tag_id);
						realDataAreaName = object2
								.getString("realData_area_name");
						time = object2.getString("addTime");
						System.out.println();
						if (time != null && !"".equals(time)) {
							time = object2.getJSONObject("addTime").getString(
									"time");
							time = TimeUtil.getTimeUtilInstance()
									.TimeStamp2Date(time);
						}
						System.out.println("time=" + time);
						realDate_SensorUseFlag = object2
								.getInt("realDate_SensorUseFlag");
						realDate_CtrlFunction = object2
								.getInt("realDate_CtrlFunction");
						mapName = object2.getString("mapName");
						relayState = object2.getInt("relayState");
						sensor1 = object2.getString("sensor1");
						sensor2 = object2.getString("sensor2");
						sensor3 = object2.getString("sensor3");
						sensor4 = object2.getString("sensor4");
						sensor5 = object2.getString("sensor5");
						sensor6 = object2.getString("sensor6");
						sensor7 = object2.getString("sensor7");
						// 各值的参考范围
						sensor1Wide = object2.getString("sensor1_limit_scope");
						sensor2Wide = object2.getString("sensor2_limit_scope");
						sensor3Wide = object2.getString("sensor3_limit_scope");
						sensor4Wide = object2.getString("sensor4_limit_scope");
						sensor5Wide = object2.getString("sensor5_limit_scope");
						sensor6Wide = object2.getString("sensor6_limit_scope");
						sensor7Wide = object2.getString("sensor7_limit_scope");
						// 设备电量
						deviceBattery = Integer.toString(object2
								.getInt("device_Battery"));
						realData_device_name = object2
								.getString("realData_device_name");
						deviceState = object2
								.getString("realData_Device_State");
						sluice = object2.getInt("sluice");
						roller = object2.getInt("roller");
						id = object2.getString("id");
						parentID = object2.getString("parentID");
						areaID = object2.getInt("realData_area_id");
						map1.put("id", id);
						map1.put("tagId", tag_id);
						map1.put("tagName", tag_name);
						map1.put("realDataAreaName", realDataAreaName);
						map1.put("time", time);
						map1.put("tempture", getCutStr(sensor1));
						map1.put("moist", getCutStr(sensor2));
						map1.put("soilTemperature", getCutStr(sensor5));
						map1.put("photosynthetic", getCutStr(sensor3));
						map1.put("soilhumidity", getCutStr(sensor6));
						map1.put("radiation", getCutStr(sensor4));
						map1.put("carbondioxide", getCutStr(sensor7));
						map1.put("sluice", sluice);
						map1.put("roller", roller);
						map1.put("parentID", parentID);
						map1.put("areaID", areaID);
						map1.put("temptureWide", sensor1Wide);
						map1.put("moistWide", sensor2Wide);
						map1.put("photosyntheticWide", sensor3Wide);
						map1.put("radiationWide", sensor4Wide);
						map1.put("soilTemperatureWide", sensor5Wide);
						map1.put("soilhumidityWide", sensor6Wide);
						map1.put("carbondioxideWide", sensor7Wide);
						map1.put("deviceBattery", deviceBattery);
						map1.put("realData_device_name", realData_device_name);
						map1.put("deviceState", deviceState);
						map1.put("realDate_SensorUseFlag",
								realDate_SensorUseFlag);

						// int realDate_CtrlFunction =
						// Integer.parseInt(map.get("realDate_CtrlFunction").toString());
						// String mapName = map.get("mapName").toString();
						// int relayState =
						// Integer.parseInt(map.get("relayState").toString());
						String str1 = Integer
								.toBinaryString(realDate_CtrlFunction);
						String str2 = Integer.toBinaryString(relayState);
						String[] maps = mapName.split("@");
						int j = str2.length();
						while (j < 8) {
							str2 = "0" + str2;
							j = str2.length();
						}
						String arr[] = new String[(str2.length()) / 2];
						for (int x = 0; x < arr.length; x++) {
							arr[x] = str2.substring(2 * x, 2 * x + 2);
						}
						String state = "";
						// for (int y = 0; y < arr.length; y++) {
						// System.out.println("arr[" + y + "]:" + arr[y]);
						// }
						int displayNum = 0;
						String swithName = "";
						String states = "";
						String swithTags = "";
						for (int i1 = 0; i1 < str1.length(); i1++) {
							int k = (realDate_CtrlFunction & (0x1 << i1)) >> i1;
							System.out.println(k);
							if (k == 1) {
								state = arr[3 - i1];
								System.out.println("state=" + state);
								if ("00".equals(state) || "11".equals(state)) {
									state = "未知";
								}
								if ("01".equals(state)) {
									state = "已关闭";
								}
								if ("10".equals(state)) {
									state = "已打开";
								}
								if (0 == displayNum) {
									swithName = maps[i1];
									states = state;
									swithTags = get(i1) + "";
								} else {
									swithName += "@" + maps[i1];
									states += "@" + state;
									swithTags += "@" + get(i1);
								}
								/*
								 * swithName[displayNum] = maps[i1];
								 * states[displayNum] = state;
								 * swithTags[displayNum] = get(i1);
								 */
								displayNum++;
							}
						}
						map1.put("swithName", swithName);
						map1.put("states", states);
						map1.put("swithTags", swithTags);
						map1.put("displayNum", displayNum);
						/*
						 * map1.put("realDate_CtrlFunction",
						 * realDate_CtrlFunction); map1.put("mapName", mapName);
						 * map1.put("relayState", relayState);
						 */
						list.add(map1);
					}
					for (int i = 0; i < list.size(); i++) {
						System.out.println(list.get(i));
					}
					Message message = mUIHandler
							.obtainMessage(WHAT_DID_LOAD_DATA);
					message.obj = list;
					message.sendToTarget();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finish() {
				refreshBtn.clearAnimation();
				if (!"refresh".equals(type)) {
					progressDialog.dismiss();
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

	private String getCutStr(String str) {

		String result = null;
		if (str.contains(".")) {
			if (str.length() - (str.indexOf(".") + 1) > 2) {
				result = str.substring(0, str.indexOf(".") + 3);
			} else {

				result = str;
			}

		} else {
			result = str;
		}

		return result;

	}

	public class MyAdapter extends BaseAdapter {
		private Context context;
		private List<HashMap<String, Object>> labels;

		public MyAdapter(Context context, List<HashMap<String, Object>> labels) {
			super();
			this.context = context;
			this.labels = labels;
		}

		@Override
		public int getCount() {
			return labels.size();
		}

		@Override
		public Object getItem(int arg0) {
			return labels.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = labels.get(arg0);
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			v = _LayoutInflater.inflate(R.layout.my_simple_spinner, null);
			if (v != null) {
				TextView label = (TextView) v
						.findViewById(R.id.simple_spinner_tx);
				label.setText(map.get("name").toString());
				String flag = labels.get(arg0).toString();
				System.out.println("flag=" + flag);
			}
			return v;
		}
	}

	// 判断是否含有该传感器
	public boolean haveSensor(Integer value, String sensortype)
			throws Exception {
		Integer comp = 0;
		if ("Sensor1".equals(sensortype)) {
			comp = (1 << 0);

		} else if ("Sensor2".equals(sensortype)) {
			comp = (1 << 1);
		} else if ("Sensor3".equals(sensortype)) {
			comp = (1 << 2);
		} else if ("Sensor4".equals(sensortype)) {
			comp = (1 << 3);
		} else if ("Sensor5".equals(sensortype)) {
			comp = (1 << 4);
		} else if ("Sensor6".equals(sensortype)) {
			comp = (1 << 5);
		} else if ("Sensor7".equals(sensortype)) {
			comp = (1 << 6);
		} else if ("Sensor8".equals(sensortype)) {
			comp = (1 << 7);
		} else if ("Sensor9".equals(sensortype)) {
			comp = (1 << 8);
		} else if ("Sensor10".equals(sensortype)) {
			comp = (1 << 9);
		} else if ("Sensor11".equals(sensortype)) {
			comp = (1 << 10);
		} else if ("Sensor12".equals(sensortype)) {
			comp = (1 << 11);
		} else if ("Sensor13".equals(sensortype)) {
			comp = (1 << 12);
		} else if ("Sensor14".equals(sensortype)) {
			comp = (1 << 13);
		} else if ("Sensor15".equals(sensortype)) {
			comp = (1 << 14);
		} else if ("Sensor16".equals(sensortype)) {
			comp = (1 << 15);
		} else if ("Sensor17".equals(sensortype)) {
			comp = (1 << 16);
		} else if ("Sensor18".equals(sensortype)) {
			comp = (1 << 17);
		} else if ("Sensor19".equals(sensortype)) {
			comp = (1 << 18);
		} else if ("Sensor20".equals(sensortype)) {
			comp = (1 << 19);
		}

		if ((value & comp) == comp) {
			return true;
		}

		return false;

	}

	private int get(int i) {
		int k = 1;
		if (i == 0)
			k = 1;
		while (i > 0) {
			k = k * 2;
			i--;
		}
		return k;
	}
}
