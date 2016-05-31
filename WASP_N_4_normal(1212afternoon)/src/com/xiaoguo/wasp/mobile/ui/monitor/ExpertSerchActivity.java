package com.xiaoguo.wasp.mobile.ui.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.viewbadger.BadgeView;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.areaDb;
import com.xiaoguo.wasp.mobile.model.condititon;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

public class ExpertSerchActivity extends Activity implements OnClickListener,
		OnItemSelectedListener/* ,OnItemClickListener */{

	private TextView tvTitle, tvSelectedProvince, tvSelectedTown,
			tvSelectedVillage, tvSelectedLabel, tvSelectedProvinceID,
			tvSelectedTownID, tvSelectedVillageID, tvSelectedLabelID,
			imgCancelTown, imgCancelVillage, imgCancelLabel;

	private areaDb areadb;

	private Button btnExit, btnSubmit;

	private Spinner spProvince, spCity, spxian, spTown, spShed, spLabel;

	private MyAdapter2 adapterProvince, adapterCity, adapterXian, adapterTown,
			adapterShed, adapterTag;

	private EditText edTown, edShed;

	private CommandBase commandBase = CommandBase.instance();

	private ProgressDialog progressdialog;

	private List<HashMap<String, Object>> areaList;

	private NetWorkDetect netWorkDetect;

	private HashMap<String, Object> areaMap;

	private Toast mToast;
	// String[] arrayProvince = {};
	// int[] arrayProvinceID = {};
	// String[] arrayCitys = {};
	// int[] arrayCitysID = {};
	// String[] arrayXian = {};
	// int[] arrayXianID = {};
	// String[] arrayTown = {};
	// int[] arrayTownID = {};
	// String[] arrayShed = {};
	// int[] arrayShedID = {};

	List<String> arrayProvince = null;
	List<String> arrayCitys = null;
	List<String> arrayXian = null;
	List<String> arrayTown = null;
	List<String> arrayShed = null;
	List<String> arrayTag = null;
	List<Integer> arrayProvinceID = null;
	List<Integer> arrayCitysID = null;
	List<Integer> arrayXianID = null;
	List<Integer> arrayTownID = null;
	List<Integer> arrayShedID = null;
	List<Integer> arrayTagID = null;

	HashMap<String, Object> hashVillage, hashTown, hashTag;
	String qu, town, shed;

	private boolean judgeNetWork;

	private BadgeView badgeProvince, badgeTown, badgeVillage, badgeLabel;
	MyBroadcastReceiver receiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expert_serch);
		initView();
		System.out.println(" 当前的网络状况是---->>" + judgeNetWork);
		if (judgeNetWork) {
			initServiceData();
		} else {
			toastShow("网络状况不好，请检查网络");
			initLocalData();
		}
	}

	@SuppressWarnings("unchecked")
	private void initLocalData() {
		arrayProvince.add("请选择");
		arrayProvince.add("四川");

		hashVillage = new HashMap<String, Object>();
		hashTown = new HashMap<String, Object>();
		hashTag = new HashMap<String, Object>();

		hashVillage = areadb.obtianVillage();
		arrayXian = (List<String>) hashVillage.get("villageName");
		arrayXianID = (List<Integer>) hashVillage.get("villageId");
		arrayXian.add(0, "-请选择-");
		arrayXianID.add(0, 0);

		hashTown = areadb.obtianTown();
		arrayTown = (List<String>) hashTown.get("townName");
		arrayTown.add(0, "-请选择-");
		arrayTownID = (List<Integer>) hashTown.get("townId");
		arrayTownID.add(0, 0);

		hashTag = areadb.obtianTag();
		arrayTag = (List<String>) hashTag.get("tagName");
		arrayTag.add(0, "-请选择-");
		arrayTagID = (List<Integer>) hashTag.get("tagId");
		arrayTagID.add(0, 0);

		setAllAdapter();

	}

	private void initServiceData() {

		btnSubmit.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		// 获取护数据
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
			}

			@Override
			public void start() {
				progressdialog.show();
			}

			@Override
			public String requestUrl() {
				return "selectAll";
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
				JSONObject data = new JSONObject();
				List<String> httpArrayProvince = new ArrayList<String>();
				httpArrayProvince.add("-请选择-");
				List<String> httpArrayCitys = new ArrayList<String>();
				httpArrayCitys.add("-请选择-");
				List<String> httpArrayXian = new ArrayList<String>();
				httpArrayXian.add("-请选择-");
				List<String> httpArrayTown = new ArrayList<String>();
				httpArrayTown.add("-请选择-");
				List<String> httpArrayShed = new ArrayList<String>();
				httpArrayShed.add("-请选择-");
				List<String> httpArrayTag = new ArrayList<String>();
				httpArrayTag.add("-请选择-");
				List<Integer> httpArrayProvinceID = new ArrayList<Integer>();
				httpArrayProvinceID.add(0);
				List<Integer> httpArrayCitysID = new ArrayList<Integer>();
				httpArrayCitysID.add(0);
				List<Integer> httpArrayXianID = new ArrayList<Integer>();
				httpArrayXianID.add(0);
				List<Integer> httpArrayTownID = new ArrayList<Integer>();
				httpArrayTownID.add(0);
				List<Integer> httpArrayShedID = new ArrayList<Integer>();
				httpArrayShedID.add(0);
				List<Integer> httpArrayTagID = new ArrayList<Integer>();
				httpArrayTagID.add(0);
				try {
					data = msg.getJSONObject("data");
					JSONArray jsonProvince = data.getJSONArray("province");
					for (int i1 = 0; i1 < jsonProvince.length(); i1++) {
						httpArrayProvince.add(jsonProvince.getJSONObject(i1)
								.getString("province_name"));

						httpArrayProvinceID.add(jsonProvince.getJSONObject(i1)
								.getInt("province_id"));
					}

					JSONArray jsonXian = data.getJSONArray("xian");
					for (int i2 = 0; i2 < jsonXian.length(); i2++) {

						httpArrayXian.add(jsonXian.getJSONObject(i2).getString(
								"xian_name"));

						httpArrayXianID.add(jsonXian.getJSONObject(i2).getInt(
								"xian_id"));

						boolean judge1 = areadb.saveTown(jsonXian
								.getJSONObject(i2).getInt("xian_id"), jsonXian
								.getJSONObject(i2).getString("xian_name"));
					}

					JSONArray jsonVillage = data.getJSONArray("village");

					for (int i3 = 0; i3 < jsonVillage.length(); i3++) {

						httpArrayTown.add(jsonVillage.getJSONObject(i3)
								.getString("village_name"));
						httpArrayTownID.add(jsonVillage.getJSONObject(i3)
								.getInt("village_xian_id"));
						boolean judge2 = areadb.saveVillage(
								jsonVillage.getJSONObject(i3).getInt(
										"village_xian_id"),
								jsonVillage.getJSONObject(i3).getString(
										"village_name"));
					}

					JSONArray jsonTag = data.getJSONArray("tag");

					for (int i4 = 0; i4 < jsonTag.length(); i4++) {
						httpArrayTag.add(jsonTag.getJSONObject(i4).getString(
								"tag_name"));
						httpArrayTagID.add(jsonTag.getJSONObject(i4).getInt(
								"tag_id"));
						// 将已有的标签添加到本地数据库
						boolean judge3 = areadb
								.saveTag(
										jsonTag.getJSONObject(i4).getInt(
												"tag_id"),
										jsonTag.getJSONObject(i4).getString(
												"tag_name"));

					}

					List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("arrayProvince", httpArrayProvince);
					map.put("arrayProvinceID", httpArrayProvinceID);
					map.put("arrayXian", httpArrayXian);
					map.put("arrayXianID", httpArrayXianID);
					map.put("arrayTown", httpArrayTown);
					map.put("arrayTownID", httpArrayTownID);
					map.put("arrayTag", httpArrayTag);
					map.put("arrayTagID", httpArrayTagID);
					list.add(map);
					Message message = myhandler.obtainMessage();
					message.obj = list;
					message.sendToTarget();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				progressdialog.dismiss();
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

	private void initView() {
		netWorkDetect = new NetWorkDetect();
		judgeNetWork = netWorkDetect.detect(ExpertSerchActivity.this);
		areadb = new areaDb(ExpertSerchActivity.this);
		arrayProvince = new ArrayList<String>();
		arrayCitys = new ArrayList<String>();
		arrayXian = new ArrayList<String>();
		arrayTown = new ArrayList<String>();
		arrayShed = new ArrayList<String>();
		arrayTag = new ArrayList<String>();
		arrayProvinceID = new ArrayList<Integer>();
		arrayXianID = new ArrayList<Integer>();
		arrayTownID = new ArrayList<Integer>();
		arrayShedID = new ArrayList<Integer>();
		arrayTagID = new ArrayList<Integer>();
		tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setText("查询");
		btnExit = (Button) findViewById(R.id.bt_left);
		btnExit.setVisibility(View.VISIBLE);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		spProvince = (Spinner) findViewById(R.id.sp_province);

		spProvince.setOnItemSelectedListener(this);
		spCity = (Spinner) findViewById(R.id.sp_city);
		spxian = (Spinner) findViewById(R.id.sp_hsien);

		spxian.setOnItemSelectedListener(this);

		spTown = (Spinner) findViewById(R.id.sp_town);
		spTown.setOnItemSelectedListener(this);

		spShed = (Spinner) findViewById(R.id.sp_shed);
		spLabel = (Spinner) findViewById(R.id.sp_label);

		spLabel.setOnItemSelectedListener(this);
		edTown = (EditText) findViewById(R.id.et_town);
		edShed = (EditText) findViewById(R.id.et_shed);
		tvSelectedProvince = (TextView) findViewById(R.id.tv_selectedProvince);
		tvSelectedProvince.setOnClickListener(this);
		tvSelectedTown = (TextView) findViewById(R.id.tv_selectedTown);
		tvSelectedTown.setOnClickListener(this);
		tvSelectedVillage = (TextView) findViewById(R.id.tv_selectedVillage);
		tvSelectedVillage.setOnClickListener(this);
		tvSelectedLabel = (TextView) findViewById(R.id.tv_selectedLabel);
		tvSelectedLabel.setOnClickListener(this);
		/**
		 * ID
		 */
		tvSelectedProvinceID = (TextView) findViewById(R.id.tv_selectedProvinceID);
		tvSelectedTownID = (TextView) findViewById(R.id.tv_selectedTownID);
		tvSelectedLabelID = (TextView) findViewById(R.id.tv_selectedTagID);
		tvSelectedProvinceID = (TextView) findViewById(R.id.tv_selectedProvinceID);
		tvSelectedVillageID = (TextView) findViewById(R.id.tv_selectedVillageID);
		imgCancelTown = (TextView) findViewById(R.id.img_cancelTown);
		imgCancelTown.setOnClickListener(this);
		imgCancelVillage = (TextView) findViewById(R.id.img_cancelVillage);
		imgCancelVillage.setOnClickListener(this);
		imgCancelLabel = (TextView) findViewById(R.id.img_cancelLabel);
		imgCancelLabel.setOnClickListener(this);
		progressdialog = new ProgressDialog(ExpertSerchActivity.this);

	}

	private Handler myhandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			areaList = new ArrayList<HashMap<String, Object>>();
			areaList = (List<HashMap<String, Object>>) msg.obj;

			arrayProvince = (List<String>) areaList.get(0).get("arrayProvince");
			arrayXian = (List<String>) areaList.get(0).get("arrayXian");
			arrayTown = (List<String>) areaList.get(0).get("arrayTown");
			arrayTag = (List<String>) areaList.get(0).get("arrayTag");
			arrayProvinceID = (List<Integer>) areaList.get(0).get(
					"arrayProvinceID");
			arrayCitysID = null;
			arrayXianID = (List<Integer>) areaList.get(0).get("arrayXianID");
			arrayTownID = (List<Integer>) areaList.get(0).get("arrayTownID");
			arrayTagID = (List<Integer>) areaList.get(0).get("arrayTagID");
			// System.out.println("arrayTagID里面的数量--->>" + arrayTagID.size()
			// + "第0个位置是" + arrayTagID.get(0));
			setAllAdapter();
		}

	};

	private void setAllAdapter() {
		adapterXian = new MyAdapter2(arrayXian, ExpertSerchActivity.this);

		spxian.setAdapter(adapterXian);
		adapterTown = new MyAdapter2(arrayTown, ExpertSerchActivity.this);

		spTown.setAdapter(adapterTown);
		adapterTag = new MyAdapter2(arrayTag, ExpertSerchActivity.this);

		spLabel.setAdapter(adapterTag);
		adapterProvince = new MyAdapter2(arrayProvince,
				ExpertSerchActivity.this);
		spProvince.setAdapter(adapterProvince);

	}

	private static class MyAdapter2 extends BaseAdapter {
		private Context context;
		private List<String> items1 = new ArrayList<String>();

		public MyAdapter2(List<String> items1, Context context) {
			super();
			this.items1 = items1;
			this.context = context;
		}

		@Override
		public int getCount() {
			return items1.size();
		}

		@Override
		public Object getItem(int position) {
			return items1.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			convertView = _LayoutInflater.inflate(R.layout.my_simple_spinner,
					null);
			if (convertView != null) {
				TextView label = (TextView) convertView
						.findViewById(R.id.simple_spinner_tx);
				label.setText(items1.get(position));
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_submit:
			Intent i = new Intent();
			Bundle bundle = new Bundle();
			condititon con = new condititon();
			int submitTownID,
			submitVillageID,
			submitTagID,
			submitProvinceID;

			if (tvSelectedProvinceID.getText().toString().equals("")) {
				submitProvinceID = 0;

			} else {
				submitProvinceID = Integer.parseInt(tvSelectedProvinceID
						.getText().toString());

			}

			if (tvSelectedTownID.getText().toString().equals("")) {
				submitTownID = 0;

			} else {
				submitTownID = Integer.parseInt(tvSelectedTownID.getText()
						.toString());
			}

			if (tvSelectedVillageID.getText().toString().equals("")) {
				submitVillageID = 0;

			} else {

				submitVillageID = Integer.parseInt(tvSelectedVillageID
						.getText().toString());
			}
			if (tvSelectedLabelID.getText().toString().equals("")) {
				submitTagID = 0;
			} else {
				submitTagID = Integer.parseInt(tvSelectedLabelID.getText()
						.toString());
			}

			con.setProvinceID(submitProvinceID);
			con.setTagID(submitTagID);
			con.setTownID(submitTownID);
			con.setVillageID(submitVillageID);
			bundle.putParcelable("condition", con);
			i.putExtra("data", bundle);
//			i.setClass(ExpertSerchActivity.this, RealtimeMoniterActivity.class);
			i.setClass(ExpertSerchActivity.this, AllGreenHouseActivity.class);
			startActivity(i);
			break;

		case R.id.bt_left:
			ExpertSerchActivity.this.finish();
			break;
		case R.id.tv_selectedProvince:

			break;

		case R.id.tv_selectedTown:
			new AlertDialog.Builder(ExpertSerchActivity.this)
					.setTitle("是否删除该条件?")
					.setPositiveButton("确定", new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							tvSelectedTown.setText("");
							tvSelectedTownID.setText("");
							imgCancelTown.setText("");
						}
					})
					.setNegativeButton("取消", new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
			break;

		case R.id.tv_selectedVillage:

			new AlertDialog.Builder(ExpertSerchActivity.this)
					.setTitle("是否删除该条件?")
					.setPositiveButton("确定", new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							tvSelectedVillage.setText("");
							tvSelectedVillageID.setText("");
							imgCancelVillage.setText("");
						}
					})
					.setNegativeButton("取消", new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();

			break;

		case R.id.tv_selectedLabel:
			new AlertDialog.Builder(ExpertSerchActivity.this)
					.setTitle("是否删除该条件?")
					.setPositiveButton("确定", new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							tvSelectedLabel.setText("");
							tvSelectedLabelID.setText("");
							imgCancelLabel.setText("");
						}
					})
					.setNegativeButton("取消", new AlertDialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
			break;

		case R.id.img_cancelTown:

			break;

		case R.id.img_cancelVillage:

			break;
		case R.id.img_cancelLabel:

			break;

		default:

			break;

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long arg3) {

		switch (adapterView.getId()) {

		case R.id.sp_province:
			if (0 == position) {
				tvSelectedProvince.setText("");
				tvSelectedProvinceID.setText("0");
			} else {

				tvSelectedProvince.setText((CharSequence) adapterView
						.getItemAtPosition(position));
				tvSelectedProvinceID.setText(arrayProvinceID.get(position)
						.toString());
			}
			break;

		case R.id.sp_hsien:
			if (0 == position) {
				tvSelectedTown.setText("");
				tvSelectedTownID.setText("");
				imgCancelTown.setText("");
			} else {
				tvSelectedTown.setText((CharSequence) adapterView
						.getItemAtPosition(position));
				tvSelectedTownID.setText(arrayXianID.get(position).toString());
				imgCancelTown.setText("×");

			}

			break;

		case R.id.sp_town:
			if (0 == position) {
				tvSelectedVillage.setText("");
				tvSelectedVillageID.setText("");
				imgCancelVillage.setText("");

			} else {
				tvSelectedVillage.setText((CharSequence) adapterView
						.getItemAtPosition(position));
				tvSelectedVillageID.setText(arrayTownID.get(position)
						.toString());
				imgCancelVillage.setText("×");

			}

			break;
		case R.id.sp_label:
			if (0 == position) {
				tvSelectedLabel.setText("");
				tvSelectedLabelID.setText("");
				imgCancelLabel.setText("");

			} else {
				tvSelectedLabel.setText((CharSequence) adapterView
						.getItemAtPosition(position));
				tvSelectedLabelID.setText(arrayTagID.get(position).toString());
				imgCancelLabel.setText("×");
			}

			break;
		default:
			break;

		}

	}

//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//		toastStop();
//	}
//
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(ExpertSerchActivity.this);
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
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void toastShow(String message) {
		if (null == mToast) {
			mToast = Toast.makeText(ExpertSerchActivity.this, message,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(message);
		}

		mToast.show();

	}

	private void toastStop() {

		if (null != mToast) {
			mToast.cancel();

		}

	}

}
