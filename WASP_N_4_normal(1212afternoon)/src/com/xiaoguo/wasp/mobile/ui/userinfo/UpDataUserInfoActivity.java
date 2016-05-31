package com.xiaoguo.wasp.mobile.ui.userinfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

public class UpDataUserInfoActivity extends Activity implements OnClickListener {

	private Button btnExit;
	private TextView title;
	private Button submit;
	private EditText updataNickName;
	private String updataNickNameText;
	private EditText updataTelNumber;
	private String updataTelNumberText;
	private EditText updataEmailAddress;
	private String updataEmailAddressText;
	private TextView birthdayText;
	private EditText updataAddress;
	private String updataAddressText;
	private RadioGroup radioSexGroup;
	private String selectSex = "";
	private RadioButton radioMale;
	private RadioButton radioFelmale;
	private CommandBase commandbase;
	private ProgressDialog progressdialog = null;
	private UserSettingInfo userInfo = null;
	private JSONObject itemObject;
	private TimeUtil timeutil;
	private Spinner typeView, botanyView;
	private EditText updataUserName, updataUserScale, updataUserQuXian,
			updataUserPlantYears, updataUserPlantLocation,
			updataUserPlantScales;

	private static final String items[] = { "种植", "养殖" };
	private List<String> tags;
	private List<Integer> tagIds;
	Calendar c = null;
	DatePickerDialog dialog = null;
	String birthStr;
	MyBroadcastReceiver receiver = null;
	private String type = "";
	private int botanyID = 0;
	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_up_data_user_info);

		commandbase = CommandBase.instance();
		commandbase.setCurrActivityContext(UpDataUserInfoActivity.this,
				UpDataUserInfoActivity.this);
		itemObject = new JSONObject();
		userInfo = new UserSettingInfo(this);
		WASPApplication.getInstance().addActivity(this);

		initView();
		initData();
		addListener();
	}

	private void addListener() {
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

			}
		});

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

	}

	private void initData() {
		// 暂时不用获取标签
		// commandbase.request(new TaskListener() {
		//
		// @Override
		// public void updateCacheDate(List<HashMap<String, Object>> cacheData)
		// {
		//
		// }
		//
		// @Override
		// public void start() {
		// progressdialog.show();
		// }
		//
		// @Override
		// public String requestUrl() {
		// return "SelectRegisterTag";
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
		// System.out.println("labelbName-->>" + labelName);
		// tags.add(labelName);
		// tagIds.add(labelId);
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void finish() {
		// progressdialog.dismiss();
		// MyAdapterTwo tagsAdapter = new MyAdapterTwo(tags,
		// UpDataUserInfoActivity.this);
		// botanyView.setAdapter(tagsAdapter);
		// MyAdapter spinner_adapter = new MyAdapter(items,
		// UpDataUserInfoActivity.this);
		// typeView.setAdapter(spinner_adapter);
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
		// progressdialog.dismiss();
		// }
		//
		// @Override
		// public String contentype() {
		// return null;
		// }
		// });

	}

	private void initView() {
		if (TextUtils.isEmpty(userInfo.getAddress())
				&& TextUtils.isEmpty(userInfo.getBirth())
				&& TextUtils.isEmpty(userInfo.getDepartment())
				&& TextUtils.isEmpty(userInfo.getEmail())
				&& TextUtils.isEmpty(userInfo.getRegister())
				&& TextUtils.isEmpty(userInfo.getUserName())
				&& TextUtils.isEmpty(userInfo.getUserPhone())
				&& TextUtils.isEmpty(userInfo.getUserSex())) {
			loadData();
		}
		tags = new ArrayList<String>();
		tagIds = new ArrayList<Integer>();
		// private Spinner typeView, botanyView;
		// private EditText updataUserName, updataUserScale;
		updataUserQuXian = (EditText) findViewById(R.id.updatequxian);
		typeView = (Spinner) findViewById(R.id.update_user_type);
		botanyView = (Spinner) findViewById(R.id.update_user_bontany);
		updataUserName = (EditText) findViewById(R.id.updataUserName);
		updataUserScale = (EditText) findViewById(R.id.updata_user_scale);
		updataUserPlantScales = (EditText) findViewById(R.id.updataUserScale);

		birthdayText = (TextView) findViewById(R.id.birthdayText);
		birthdayText.setOnClickListener(this);
		updataUserPlantYears = (EditText) findViewById(R.id.updataUserPlantYears);
		updataUserPlantLocation = (EditText) findViewById(R.id.updataUserPlantLocation);
		updataAddress = (EditText) findViewById(R.id.updataAddress);
		progressdialog = new ProgressDialog(UpDataUserInfoActivity.this);
		btnExit = (Button) findViewById(R.id.bt_left);
		btnExit.setVisibility(View.VISIBLE);
		btnExit.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("修改信息");
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		// EditText
		updataNickName = (EditText) findViewById(R.id.updataNickName);
		updataTelNumber = (EditText) findViewById(R.id.updataTelNumber);
		updataEmailAddress = (EditText) findViewById(R.id.updataEmailAddress);
		// RadioGroup
		radioSexGroup = (RadioGroup) findViewById(R.id.radioSexGroup);
		radioMale = (RadioButton) findViewById(R.id.sexMale);
		radioFelmale = (RadioButton) findViewById(R.id.sexFamale);
		radioSexGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						int radioButtonId = group.getCheckedRadioButtonId();

						RadioButton radiobutton = (RadioButton) findViewById(radioButtonId);
						selectSex = radiobutton.getText().toString();
					}
				});
		refresh();
	}

	protected void refresh() {
		birthdayText.setText(userInfo.getBirth().split(" ")[0]);
		updataAddress.setText(userInfo.getAddress());
		updataNickName.setText(userInfo.getUserName());
		updataTelNumber.setText(userInfo.getUserPhone());
		updataEmailAddress.setText(userInfo.getEmail());
		updataUserScale.setText(userInfo.getUserScale());
		updataUserPlantScales.setText(userInfo.getUserScale());
		updataUserPlantLocation.setText(userInfo.getUserPlantLocation());
		updataUserPlantYears.setText(userInfo.getUserPlantYear());
		selectSex = userInfo.getUserSex();

		if (userInfo.getUserSex().equals("男")) {
			radioMale.setChecked(true);
			radioFelmale.setChecked(false);
		} else if (userInfo.getUserSex().equals("女")) {
			radioMale.setChecked(false);
			radioFelmale.setChecked(true);
		} else {
			radioMale.setChecked(false);
			radioFelmale.setChecked(false);
		}
	}

	private void loadData() {
		commandbase.request(new TaskListener() {
			@Override
			public void start() {
			}

			@Override
			public String requestUrl() {
				return "userinfo";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object1 = new JSONObject();
				try {
					object1.put("action", "query");
					object1.put("account", userInfo.getAccount());
				} catch (JSONException e) {
					e.printStackTrace();
					object1 = null;
				}
				System.out.println("queryobj=" + object1);

				return object1;
			}

			// 对返回的msg进解析
			@Override
			public void messageUpdated(JSONObject msg) {
				try {

					// 获取json中的data的数据
					JSONObject data = msg.getJSONObject("data");

					JSONObject item = data.getJSONObject("item");
					System.out.println("item---->>" + item.toString());

					// 获取时间object
					JSONObject userCreateDate = item
							.getJSONObject("user_create_date");
					// 获取时间
					Long time = userCreateDate.getLong("time");
					// 获取北京时间
					timeutil = TimeUtil.getTimeUtilInstance();

					String timeStr = timeutil.TimeStamp2Date(time.toString());
					System.out.println("时间---->>" + timeStr);
					userInfo.setRegister(timeStr);

					// 电话号码
					String tel = item.getString("user_mobile_phone");
					System.out.println("电话号码---->>" + tel);
					userInfo.setUserPhone(tel);
					// 昵称
					String nick = item.getString("user_display_name");
					userInfo.setUserName(nick);
					System.out.println("昵称---->>" + nick);

					// 性别
					String userSex = item.getString("user_sex");
					if (userSex.equals("0")) {
						userSex = "女";
						System.out.println("userSex=" + userSex);
					}
					if (userSex.equals("1")) {
						userSex = "男";
						System.out.println("userSex=" + userSex);
					}
					userInfo.setUserSex(userSex);
					System.out.println("性别---->>" + userSex);

					// 邮箱
					String emailStr = item.getString("user_email");
					userInfo.setEmail(emailStr);

					// 部门
					String departmentStr = item.getString("user_department");
					userInfo.setDepartment(departmentStr);

					// 地址
					String addressStr = item.getString("user_address");
					userInfo.setAddress(addressStr);

					// 生日没有获取----------------------------
					String birth = item.getString("user_birthday");
					if (birth != null && !birth.equals("")) {
						System.out.println("生日时间是："
								+ item.getJSONObject("user_birthday").getLong(
										"time"));
						birth = TimeUtil.getTimeUtilInstance().TimeStamp2Date(
								item.getJSONObject("user_birthday").getLong(
										"time")
										+ "");
						birth = birth.split(" ")[0];
						userInfo.setBirth(birth);
						System.out.println("生日---->>" + birth);
					} else {
						userInfo.setBirth("");
					}
					Message msg1 = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					msg1.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			@Override
			public void finish() {

			}

			@Override
			public void failure(String str) {
			}

			@Override
			public String contentype() {
				return null;
			}

			@Override
			public String filepath() {
				return null;
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
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}
		});

	}

	// 各点击事件事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_left:

			UpDataUserInfoActivity.this.finish();
			break;
		case R.id.birthdayText:
			c = Calendar.getInstance();
			String tempStr = birthdayText.getText().toString();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (!"".equals(tempStr) && !"日期选择".equals(tempStr)) {
				String temp[] = tempStr.split("-");
				year = Integer.parseInt(temp[0]);
				if (temp[1].startsWith("0")) {
					month = Integer.parseInt(temp[1].substring(1));
				} else {
					month = Integer.parseInt(temp[1]);
				}
				month = month - 1;
				if (temp[2].startsWith("0")) {
					day = Integer.parseInt(temp[2].substring(1));
				} else {
					day = Integer.parseInt(temp[2]);
				}
			}
			dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,
								int month, int dayOfMonth) {
							if (month < 9 && dayOfMonth < 10) {
								birthStr = year + "-0" + (month + 1) + "-0"
										+ dayOfMonth;
							} else if (month < 9 && dayOfMonth >= 10) {
								birthStr = year + "-0" + (month + 1) + "-"
										+ dayOfMonth;
							} else if (month >= 9 && dayOfMonth < 10) {
								birthStr = year + "-" + (month + 1) + "-0"
										+ dayOfMonth;
							} else {
								birthStr = year + "-" + (month + 1) + "-"
										+ dayOfMonth;
							}
							System.out.println("生日=" + birthStr);
							String currentDay = TimeUtil
									.getTimeUtilInstance()
									.TimeStamp2Date(
											System.currentTimeMillis() + "")
									.split(" ")[0].replace("-", "");
							int current = Integer.parseInt(currentDay);
							int birth = Integer.parseInt(birthStr.replace("-",
									""));
							if (birth > current) {
								Toast.makeText(UpDataUserInfoActivity.this,
										"出生日期不能先于当前时间！", Toast.LENGTH_SHORT)
										.show();
							} else {
								birthdayText.setText(birthStr);
							}
						}
					}, year, // 传入年份
					month, // 传入月份
					day // 传入天数
			);
			dialog.show();
			break;
		case R.id.submit:
			// 各种数据
			updataNickNameText = updataNickName.getText().toString().trim();
			updataTelNumberText = updataTelNumber.getText().toString().trim();
			updataEmailAddressText = updataEmailAddress.getText().toString()
					.trim();
			updataAddressText = updataAddress.getText().toString().trim();

//			if (updataTelNumberText.length() != 11) {
//				updataTelNumber.setError("电话号码为11位，请正确输入");
//			}

			// 给服务器发送数据

			// commandbase.request(new TaskListener() {
			//
			// @Override
			// public void start() {
			// progressdialog.setMessage("正在提交到服务器");
			// progressdialog.show();
			// }
			//
			// @Override
			// public String requestUrl() {
			// return "userupdate";
			// }
			//
			// @Override
			// public JSONObject requestData() {
			// JSONObject object1 = new JSONObject();
			// try {
			// object1.put("account", "a");
			//
			// itemObject.put("user_display_name", updataNickNameText);
			// itemObject.put("user_sex", selectSex);
			// itemObject
			// .put("user_mobile_phone", updataTelNumberText);
			// itemObject.put("user_email", updataEmailAddressText);
			// birthStr = birthdayText.getText().toString();
			// if (birthStr != null && !birthStr.equals("")) {
			// birthStr = TimeUtil.getTimeUtilInstance()
			// .getDate2Timescap(birthStr);
			// } else {
			// birthStr = "";
			// }
			// itemObject.put("user_birthday", birthStr);
			// itemObject.put("user_department", "");
			// itemObject.put("user_work_phone", "");
			// itemObject.put("user_address", updataAddressText);
			// itemObject.put("user_zipcode", "");
			// object1.put("item", itemObject);
			//
			// } catch (JSONException ex) {
			// ex.printStackTrace();
			// object1 = null;
			// }
			// return object1;
			// }
			//
			// @Override
			// public void messageUpdated(JSONObject msg) {
			// userInfo.setUserName(updataNickNameText);
			// userInfo.setUserSex(selectSex);
			// System.out.println("修改后的生日为："
			// + TimeUtil.getTimeUtilInstance().getDate2Timescap(
			// birthStr));
			// userInfo.setBirth(TimeUtil.getTimeUtilInstance()
			// .getDate2Timescap(birthStr).split(" ")[0]);
			// userInfo.setAddress(updataAddressText);
			// userInfo.setUserPhone(updataTelNumberText);
			// userInfo.setEmail(updataEmailAddressText);
			// Intent resultIntent = new Intent();
			// Bundle bd = new Bundle();
			// bd.putString("data", itemObject.toString());
			// resultIntent.putExtra("reultData", bd);
			// // 修改成功
			// UpDataUserInfoActivity.this.setResult(RESULT_OK,
			// resultIntent);
			// UpDataUserInfoActivity.this.finish();
			// }
			//
			// @Override
			// public void finish() {
			// progressdialog.dismiss();
			//
			// }
			//
			// @Override
			// public void failure(String str) {
			// Toast.makeText(UpDataUserInfoActivity.this, "连接网络失败",
			// Toast.LENGTH_SHORT).show();
			//
			// }
			//
			// @Override
			// public String contentype() {
			// return null;
			// }
			//
			// @Override
			// public String filepath() {
			// return null;
			// }
			//
			// @Override
			// public boolean needCacheTask() {
			// return false;
			// }
			//
			// @Override
			// public String readCache() {
			// return null;
			// }
			//
			// @Override
			// public void updateCacheDate(
			// List<HashMap<String, Object>> cacheData) {
			//
			// }
			// });

			// }
			// private Spinner typeView, botanyView;
			// private EditText updataUserName, updataUserScale,
			// updataUserQuXian;
			// System.out.println("提交的用户数据是-->>" + type + botanyID + "    "
			// + updataNickName.getText().toString().trim() + "    "
			// + updataTelNumber.getText().toString().trim() + "   "
			// + updataUserName.getText().toString().trim() + "   "
			// + updataUserScale.getText().toString().trim() + "    "
			// + updataUserQuXian.getText().toString().trim());

			final String nickNameStr = updataNickName.getText().toString()
					.trim();
			final String telNumberStr = updataTelNumber.getText().toString()
					.trim();
			final String userScaleStr = updataUserPlantScales.getText()
					.toString().trim();
			final String userPlantYearStr = updataUserPlantYears.getText()
					.toString().trim();
			final String userPlantLocationStr = updataUserPlantLocation
					.getText().toString().trim();

			System.out.println("修改界面提交的数据是->" + nickNameStr + " : "
					+ telNumberStr + " : " + userScaleStr + " : "
					+ userPlantYearStr + " : " + userPlantLocationStr);
			// final String userPlan

			// final String userPlantYear

			commandbase.request(new TaskListener() {

				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}

				@Override
				public void start() {
					progressdialog.setMessage("正在提交,请稍后...");
					progressdialog.show();
				}

				@Override
				public String requestUrl() {
					return "userupdate";
				}

				@Override
				public JSONObject requestData() {
					JSONObject data = new JSONObject();
					try {
						data.put("user_display_name", nickNameStr);
						data.put("user_mobile_phone", telNumberStr);
						data.put("user_service_type", type);
						// data.put("user_tag_id", botanyID);
						System.out.println("userScaleStr-->>" + userScaleStr);
						data.put("user_scale", userScaleStr);
						data.put("user_years", userPlantYearStr);
						data.put("user_location", userPlantLocationStr);

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

					System.out.println("修改成功后返回的数据是-->>" + msg.toString());
					JSONObject resultObject = new JSONObject();
					resultObject = msg;
					try {
						JSONObject data = resultObject.getJSONObject("data");
						JSONObject user = data.getJSONObject("user");

						String userDisplayName = user
								.getString("user_display_name");
						String userSex = user.getString("user_sex_name");
						String userEmail = user.getString("user_email");
						String userWorkPhone = user
								.getString("user_mobile_phone");
						String address = user.getString("user_address");
						String department = user.getString("user_department");
						String xianName = user.getString("user_xian_name");

						String userRoleName = user.getString("user_role_name");
						String userServeType = user
								.getString("user_service_type");
						String userScale = user.getString("user_scale");

						String userPlantLocationStr = user
								.getString("user_location");
						String userPlantYearStr1 = user.getString("user_years");
						userInfo.setType(userInfo.getType());
						userInfo.setAddress(address);
						userInfo.setBirth("");
						userInfo.setDepartment(department);
						userInfo.setEmail(userEmail);
						userInfo.setRegister("");
						userInfo.setUserName(userDisplayName);
						userInfo.setUserPhone(userWorkPhone);
						userInfo.setUserSex(userSex);
						userInfo.setUserPlantLocation(userPlantLocationStr);
						userInfo.setUserPlantYear(userPlantYearStr1);

						// userInfo.setUserXianName(xianName);
						// userInfo.setUserRoleName(userRoleName);
						// userInfo.setUserServeType(userServeType);
						userInfo.setUserScale(userScale);
						Intent resultIntent = new Intent();
						Bundle bd = new Bundle();
						bd.putString("data", resultObject.toString());
						resultIntent.putExtra("reultData", bd);
						// 修改成功
						UpDataUserInfoActivity.this.setResult(RESULT_OK,
								resultIntent);
						UpDataUserInfoActivity.this.finish();
						overridePendingTransition(R.anim.from_right_into,
								R.anim.to_left_out);

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
					progressdialog.dismiss();
				}

				@Override
				public String contentype() {
					return null;
				}
			});

			break;

		}

	}

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				refresh();
				break;
			}
			}
		}

	};

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
		// @Override
		// protected void onPause() {
		// unregisterReceiver(receiver);
		// super.onPause();
		// }
		//
		// @Override
		// protected void onResume() {
		// receiver = new MyBroadcastReceiver(UpDataUserInfoActivity.this);
		// IntentFilter filter = new IntentFilter();
		//
		// filter.addAction(Constant.ROSTER_ADDED);
		// filter.addAction(Constant.ROSTER_DELETED);
		// filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
		// filter.addAction(Constant.ROSTER_UPDATED);
		// // 好友请求
		// filter.addAction(Constant.ROSTER_SUBSCRIPTION);
		// filter.addAction(Constant.NEW_MESSAGE_ACTION);
		// registerReceiver(receiver, filter);
		// super.onResume();
		// }
	}
}
