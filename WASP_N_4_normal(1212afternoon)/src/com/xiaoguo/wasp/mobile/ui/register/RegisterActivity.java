package com.xiaoguo.wasp.mobile.ui.register;

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
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.LoginActivity;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.adapter.ArrayWheelAdapter;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.JsonUtils;
import com.xiaoguo.wasp.mobile.utils.SHA1Util;
import com.xiaoguo.wasp.mobile.widget.OnWheelChangedListener;
import com.xiaoguo.wasp.mobile.widget.WheelView;

/**
 * 
 * @author hcq
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener,
		OnWheelChangedListener {

	private String tag = "RegisterActivity";

	// title
	private TextView titleTextView;
	// left button
	private Button btLeft;
	// right button
	private Button btRight;

	// screen
	private Button selectCity;
	private EditText studentNumberView;
	private EditText phoneNumberView;
	private EditText passwordAgainView;
	private Button submitView;
	private Spinner typeView, botanyView;
	private MyAdapter spinner_adapter = null;

	// Values
	private String studentNumber;
	private String phoneNumber;
	private String passwordAgain;
	private String selectCityStr;

	private String type = "farmer";
	JSONObject object = null;
	UserSettingInfo userInfo = null;
	CommandBase commandBase = null;
	// 标签名字
	private List<String> tags;
	private List<Integer> tagIds;

	private ProgressDialog dialog;
	private static final String items[] = { "种植", "养殖" };
	private int botanyID = 0;

	// 滚轮显示
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(tag, "onDestroy");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "onCreate");
		setContentView(R.layout.register);

		commandBase = CommandBase.instance();
		dialog = new ProgressDialog(RegisterActivity.this);
		userInfo = new UserSettingInfo(this);
		WASPApplication.getInstance().addActivity(this);

		setupView();
	}

	private void setupView() {
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);
		mViewDistrict = (WheelView) findViewById(R.id.id_district);
		setUpListener();
		setUpData();
		botanyView = (Spinner) findViewById(R.id.botany);
		getTagsData();

		botanyView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				botanyID = tagIds.get(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

				botanyID = 0;

			}
		});

		selectCity = (Button) findViewById(R.id.select_city);
		selectCity.setOnClickListener(this);
		titleTextView = (TextView) this.findViewById(R.id.title);
		titleTextView.setText("注册新用户");

		btLeft = (Button) this.findViewById(R.id.bt_left);
		btLeft.setVisibility(View.VISIBLE);
		btLeft.setBackgroundResource(R.drawable.btn_back);
		btLeft.setOnClickListener(this);

		studentNumberView = (EditText) findViewById(R.id.input_student_number);
		phoneNumberView = (EditText) findViewById(R.id.input_phone_number);
		passwordAgainView = (EditText) findViewById(R.id.input_verification_code);

		submitView = (Button) findViewById(R.id.get_verification_code);
		submitView.setOnClickListener(this);

		typeView = (Spinner) findViewById(R.id.type);
		spinner_adapter = new MyAdapter(items, RegisterActivity.this);
		typeView.setAdapter(spinner_adapter);
		typeView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0:
					type = "种植";
					break;
				case 1:
					type = "养殖";
					break;
				default:
					type = null;
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				type = "种植";
			}
		});

	}

	// // 获取标签
	// private void getTags() {
	// final List<HashMap<String, Object>> templist = new
	// ArrayList<HashMap<String, Object>>();
	// CommandBase.instance().request(new TaskListener() {
	// @Override
	// public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
	//
	// }
	//
	// @Override
	// public void start() {
	//
	// }
	//
	// @Override
	// public String requestUrl() {
	// return "TagSelList";
	// }
	//
	// @Override
	// public JSONObject requestData() {
	// return null;
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
	// System.out.println("标签msg=" + msg);
	// JSONObject object;
	// HashMap<String, Object> map = null;
	// try {
	// object = msg.getJSONObject("data");
	// JSONArray array = object.getJSONArray("list");
	// for (int i = 0; i < array.length(); i++) {
	// map = new HashMap<String, Object>();
	// object = array.getJSONObject(i);
	// String labelName = object.getString("tag_name");
	// int labelId = object.getInt("tag_id");
	// int id = 0;
	// int k = 0;
	// for (; k < list.size(); k++) {
	// id = Integer.parseInt(list.get(k).get("id")
	// .toString());
	// if (id == labelId) {
	// break;
	// }
	// }
	// if (k >= list.size()) {
	// map.put("name", labelName);
	// map.put("id", labelId);
	// templist.add(map);
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void finish() {
	// System.out.println("标签个数：" + list.size());
	// // for (int j = 0; j < templist.size(); j++) {
	// // System.out.println(templist.get(j));
	// // }
	// if (templist.size() == 0) {
	// Toast.makeText(FieldsActivity.this, "没有新的领域",
	// Toast.LENGTH_SHORT).show();
	// } else {
	// items = new String[templist.size()];
	// bools = new boolean[templist.size()];
	// if (templist.size() > 0) {
	// for (int i = 0; i < templist.size(); i++) {
	// items[i] = templist.get(i).get("name").toString();
	// bools[i] = false;
	// }
	// final List<String> chooseResultID = new ArrayList<String>();
	// final List<String> chooserReultName = new ArrayList<String>();
	// new AlertDialog.Builder(FieldsActivity.this)
	// .setCancelable(false)
	// .setTitle("更多关注领域")
	// .setMultiChoiceItems(
	// items,
	// bools,
	// new DialogInterface.OnMultiChoiceClickListener() {
	// @Override
	// public void onClick(
	// DialogInterface dialog,
	// int which, boolean isChecked) {
	// if (isChecked) {
	// chooseResultID.add(templist
	// .get(which).get(
	// "id")
	// + "");
	//
	// chooserReultName
	// .add(templist.get(
	// which).get(
	// "name")
	// + "");
	// }
	// }
	// })
	// .setPositiveButton("确认",
	// new DialogInterface.OnClickListener() {
	// // 提交给服务器的标签
	// String ss = "";
	// public void onClick(
	// DialogInterface dialoginterface,
	// int i) {
	// if (chooseResultID.size() > 0) {
	//
	// // 添加区县
	// String xian = userSettingInfo
	// .getUserXianName()
	// + "_";
	//
	// ss = chooseResultID.get(0);
	// List<String> tagBaiDu = new ArrayList<String>();
	// tagBaiDu.add(xian
	// + chooserReultName
	// .get(0));
	// for (int j = 1; j < chooseResultID
	// .size(); j++) {
	// ss = ss
	// + ","
	// + chooseResultID
	// .get(j);
	// System.out.println("添加的标签是--->>"
	// + xian
	// + chooserReultName
	// .get(j));
	// tagBaiDu.add(xian
	// + chooserReultName
	// .get(j));
	//
	// }
	//
	// // 往百度云提交数据
	// PushManager
	// .setTags(
	// FieldsActivity.this,
	// tagBaiDu);
	// // 往服务器提交数据
	// submitMyTag(ss);
	// }
	// dialoginterface.dismiss();
	// }
	// }).setNegativeButton("取消", null).show();// 显示对话框
	// }
	// }
	//
	// }
	//
	// @Override
	// public String filepath() {
	// return null;
	// }
	//
	// @Override
	// public void failure(String str) {
	//
	// }
	//
	// @Override
	// public String contentype() {
	// return null;
	// }
	// });
	// }

	private void getTagsData() {
		tags = new ArrayList<String>();
		tagIds = new ArrayList<Integer>();
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				dialog.show();
			}

			@Override
			public String requestUrl() {
				return "SelectRegisterTag";
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
						tags.add(labelName);
						tagIds.add(labelId);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finish() {
				dialog.dismiss();
				// 将内容添加到下拉列表当中去
				// ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(
				// RegisterActivity.this,
				// android.R.layout.simple_dropdown_item_1line, tags);
				MyAdapterTwo tagsAdapter = new MyAdapterTwo(tags,
						RegisterActivity.this);
				botanyView.setAdapter(tagsAdapter);
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

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left: {
			Intent i = new Intent();
			i.setClass(RegisterActivity.this, LoginActivity.class);
			this.finish();
			break;
		}
		case R.id.get_verification_code:
			attemptNext();
			break;

		case R.id.select_city:

			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle(R.string.select_city);
			final String[] cities = { "尧都区", "曲沃", "翼城", "襄汾", "洪洞", "古县",
					"安泽", "浮山", "吉县", "乡宁", "大宁", "隰县", "永和", "蒲县", "汾西", "侯马",
					"霍州" };
			builder.setItems(cities, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectCity.setText(cities[which]);
					selectCity.setTextColor(R.color.black);
				}
			});
			builder.show();
			break;

		default:
			break;
		}
	}

	// 娉ㄥ唽涓嬩竴姝?
	private void attemptNext() {
		// Reset errors.
		studentNumberView.setError(null);
		phoneNumberView.setError(null);
		passwordAgainView.setError(null);
		selectCity.setError(null);
		// Store values at the time of the next attempt.
		studentNumber = studentNumberView.getText().toString();
		phoneNumber = phoneNumberView.getText().toString();
		passwordAgain = passwordAgainView.getText().toString();
		selectCityStr = selectCity.getText().toString().trim();
		boolean cancel = false;
		View focusView = null;

		// check for a valid verify code
		if (TextUtils.isEmpty(passwordAgain)) {
			passwordAgainView
					.setError(getString(R.string.error_field_required));
			focusView = phoneNumberView;
			cancel = true;
		} else if (!passwordAgain.equals(phoneNumber)) {
			passwordAgainView
					.setError(getString(R.string.error_invalid_password_again));
			focusView = phoneNumberView;
			cancel = true;
		}
		// Check for a valid phone number.
		if (TextUtils.isEmpty(phoneNumber)) {
			phoneNumberView.setError(getString(R.string.error_field_required));
			focusView = phoneNumberView;
			cancel = true;
		} else if (phoneNumber.length() < 6) {
			phoneNumberView
					.setError(getString(R.string.error_invalid_password));
			focusView = phoneNumberView;
			cancel = true;
		}
		// Check for a valid student number.
		/*
		 * Pattern pt = Pattern.compile("^[0-9a-zA-Z_]+$"); Matcher mt =
		 * pt.matcher(studentNumber);
		 */
		if (TextUtils.isEmpty(studentNumber)) {
			studentNumberView
					.setError(getString(R.string.error_field_required));
			focusView = studentNumberView;
			cancel = true;
		}/*
		 * else if(!mt.matches()){
		 * studentNumberView.setError(getString(R.string.error_account));
		 * focusView = studentNumberView; cancel = true; }
		 */

		// if (selectCityStr.equals("")) {
		// Toast.makeText(RegisterActivity.this, "请选择您所在区县...",
		// Toast.LENGTH_SHORT).show();
		// cancel = true;
		// }

		if (cancel) {
		} else {
			Activity activity = new RegisterActivity();
			commandBase.setCurrActivityContext(RegisterActivity.this, activity);
			commandBase.setUserInfo(studentNumber, "");
			commandBase.request(new TaskListener() {
				@Override
				public void start() {
					commandBase.setUserInfo(studentNumber, "");
					dialog.setMessage("正在提交，请稍后...");
					dialog.show();
				}

				@Override
				public String requestUrl() {
					return "userregister";
				}

				@Override
				public JSONObject requestData() {
					JSONObject object = JsonUtils.getLoginJson(studentNumber,
							SHA1Util.getSHA1EncString(phoneNumber));
					System.out.println("用户提交的城市信息是->" + mCurrentProviceName
							+ " : " + mCurrentCityName + " : "
							+ mCurrentDistrictName);

					// try {
					// object.put("type", "烟农");
					// object.put("xian_name", selectCityStr);
					//
					// System.out.println("传的服务类型是-->>" + type
					// + "  种植类型是--->>" + botanyID);
					// object.put("user_service_type", type);
					// object.put("user_tag_id", botanyID);
					//
					// } catch (JSONException e) {
					// e.printStackTrace();
					// object = null;
					// }
					// System.out.println("object=" + object);
					try {
						object.put("type", "烟农");
						object.put("city_name", mCurrentCityName);
						object.put("xian_name", mCurrentDistrictName);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					return object;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					try {
						object = msg.getJSONObject("data");
						System.out.println("object=" + object);
						String session = object.getString("session");
						String type = object.getString("type");
						userInfo.setType(type);
						commandBase.setUserInfo(studentNumber, session);
						Toast.makeText(RegisterActivity.this, "注册成功",
								Toast.LENGTH_SHORT).show();
						Intent i = new Intent();
						i.setClass(RegisterActivity.this, LoginActivity.class);
						// startActivity(i);
						Bundle bundle = new Bundle();
						bundle.putString("username", studentNumber);
						bundle.putString("password", phoneNumber);
						i.putExtra("bundle", bundle);
						setResult(RESULT_OK, i);
						RegisterActivity.this.finish();
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(RegisterActivity.this, "解析异常！",
								Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void finish() {
					dialog.dismiss();
				}

				@Override
				public void failure(String str) {
				}

				@Override
				public String contentype() {
					return "text";
				}

				@Override
				public String filepath() {
					return "";
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}
			});
		}

		// }

	}

	private class MyAdapter extends BaseAdapter {
		private String[] items = null;
		private Context context;

		public MyAdapter(String[] items, Context context) {
			super();
			this.items = items;
			this.context = context;
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int arg0) {
			return items[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			v = _LayoutInflater.inflate(R.layout.my_simple_spinner, null);
			if (v != null) {
				TextView label = (TextView) v
						.findViewById(R.id.simple_spinner_tx);
				label.setText(items[arg0]);
			}
			return v;
		}

	}

	private class MyAdapterTwo extends BaseAdapter {
		private List<String> items = null;
		private Context context;

		public MyAdapterTwo(List<String> items, Context context) {
			super();
			this.items = items;
			this.context = context;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			v = _LayoutInflater.inflate(R.layout.my_simple_spinner, null);
			if (v != null) {
				TextView label = (TextView) v
						.findViewById(R.id.simple_spinner_tx);
				label.setText(items.get(arg0));
			}
			return v;
		}

	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				RegisterActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}

	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict
				.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
	}
}
