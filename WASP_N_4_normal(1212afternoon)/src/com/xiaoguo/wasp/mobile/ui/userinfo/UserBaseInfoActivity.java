package com.xiaoguo.wasp.mobile.ui.userinfo;

import java.io.File;
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
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.ImageTools;
import com.xiaoguo.wasp.mobile.utils.MyAsyncHttpClient;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

public class UserBaseInfoActivity extends Activity implements OnClickListener {

	// 内容textView
	private Button btnExit;
	private TextView userID;
	private TextView nickName;
	private TextView registerTime;
	private TextView emailAddress;
	private TextView userTelNumber;
	private ImageView userImage;
	private TextView saveAndEdit;

	private TextView sex;
	private TextView birthday;
	private TextView department;
	private TextView address;
	private TextView fields;
	private TextView fieldButton;
	private TextView quxian;
	private TextView userServiceType;
	private TextView userRoleName;
	private TextView userScale;
	private TextView userPlantType;
	private TextView userPlantLocation;
	private TextView userPlantYear;

	private File sdcardTempFile;
	private AlertDialog dialog;
	private CommandBase commandbase;

	private Integer UpDataUserActivity = 3;

	private TimeUtil timeutil;

	ProgressDialog progressDialog = null;
	UserSettingInfo userInfo = null;

	Bundle bundle = null;
	String fromWhere = "";
	String userAccount = "";
	List<HashMap<String, Object>> list = null;

	String state = "";
	String type = "";
	MyBroadcastReceiver receiver = null;
	private UserSettingInfo userSettingInfo;

	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_base_info);

		WASPApplication.getInstance().addActivity(this);

		progressDialog = new ProgressDialog(this);
		userInfo = new UserSettingInfo(this);
		userSettingInfo = new UserSettingInfo(UserBaseInfoActivity.this);
		bundle = getIntent().getBundleExtra("bd");
		fromWhere = bundle.getString("where");
		userAccount = bundle.getString("userName");
		System.out.println("userAccount=" + userAccount);
		System.out.println("fromwhere=" + fromWhere);
		if (!fromWhere.equals("user")) {
			state = bundle.getString("state");
			System.out.println("关注状态：" + state);
		}
		initView();
		// 联网查询基本信息
		getfields();

		initUserData();

	}

	private void initUserData() {

		String userIDStr = userSettingInfo.getAccount();
		String nickNameStr = userSettingInfo.getUserName();
		String emailAddressStr = userSettingInfo.getEmail();
		String userTelNumberStr = userSettingInfo.getUserPhone();
		String sexStr = userSettingInfo.getUserSex();
		String departmentStr = userSettingInfo.getDepartment();
		String quxianStr = userSettingInfo.getUserXianName();
		String addressStr = userSettingInfo.getAddress();
		String birthdayStr = userSettingInfo.getBirth();
		String userRoleNameStr = userSettingInfo.getUserRoleName();
		String userServiceTypeStr = userSettingInfo.getUserServeType();
		String userScaleStr = userSettingInfo.getUserScale();
		String userPlantTypeStr = userSettingInfo.getUserPlantType();
		String userPlantLocationStr = userSettingInfo.getUserPlantLocation();
		String userPlantYearStr = userSettingInfo.getUserPlantYear();
		userID.setText(userIDStr);
		nickName.setText(nickNameStr);
		emailAddress.setText(emailAddressStr);
		userTelNumber.setText(userTelNumberStr);
		sex.setText(sexStr);
		department.setText(departmentStr);
		// quxian.setText(quxianStr);
		address.setText(addressStr);
		birthday.setText(birthdayStr);
		userScale.setText(userScaleStr);
		userPlantLocation.setText(userPlantLocationStr);
		userPlantYear.setText(userPlantYearStr);
		// userRoleName.setText(userRoleNameStr);
		// userScale.setText(userScaleStr);
		// userServiceType.setText(userServiceTypeStr);
		// userPlantType.setText(userPlantTypeStr);
	}

	private void getData() {
		RequestParams postParam = new RequestParams();
		JSONObject object = new JSONObject();
		JSONObject object1 = new JSONObject();
		try {
			object1.put("action", "query");
			object1.put("account", userAccount);
			object.put("data", object1);
			object1 = new JSONObject();
			object1.put("session", CommandBase.instance().getSession());
			object1.put("account", userAccount);
			object.put("user", object1);
			postParam.put("req", object.toString());
			System.out.println("请求的数据为:" + object);
			MyAsyncHttpClient.post("userinfo", postParam,
					new JsonHttpResponseHandler() {
						@Override
						public void onFailure(Throwable arg0, String arg1) {
							super.onFailure(arg0, arg1);
							Toast.makeText(UserBaseInfoActivity.this,
									"网络连接失败！", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onFinish() {
							progressDialog.dismiss();
							super.onFinish();
						}

						@Override
						public void onStart() {
							progressDialog.setMessage("加载中...");
							progressDialog.show();
							super.onStart();
						}

						@Override
						public void onSuccess(int arg0, JSONObject arg1) {
							JSONObject object;
							try {
								object = arg1.getJSONObject("error");
								String code = object.getString("code");
								String errorTag = object.getString("string");
								System.out.println("code=" + code);
								System.out.println("error=" + errorTag);
								if (code.equals("0")) {
									object = arg1.getJSONObject("data");
								} else if (code.equals("-99")) {
									Toast.makeText(UserBaseInfoActivity.this,
											"用户信息已过期,请重新登陆", Toast.LENGTH_SHORT)
											.show();
								} else {
									Toast.makeText(UserBaseInfoActivity.this,
											errorTag, Toast.LENGTH_SHORT)
											.show();
								}

								JSONObject item = object.getJSONObject("item");
								System.out.println("item---->>"
										+ item.toString());

								// 获取时间object
								JSONObject userCreateDate = item
										.getJSONObject("user_create_date");
								// 获取时间
								Long time = userCreateDate.getLong("time");
								// 获取北京时间
								timeutil = TimeUtil.getTimeUtilInstance();

								String timeStr = timeutil.TimeStamp2Date(time
										.toString());
								System.out.println("时间---->>" + timeStr);
								registerTime.setText(timeStr.split(" ")[0]);

								userID.setText(userAccount);
								// 电话号码
								String tel = item
										.getString("user_mobile_phone");
								System.out.println("电话号码---->>" + tel);
								userTelNumber.setText(tel);
								// 昵称
								String nick = item
										.getString("user_display_name");
								nickName.setText(nick);
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
								sex.setText(userSex);
								System.out.println("性别---->>" + userSex);

								// 邮箱
								String emailStr = item.getString("user_email");
								emailAddress.setText(emailStr);
								userInfo.setEmail(emailStr);

								// 部门
								String departmentStr = item
										.getString("user_department");
								department.setText(departmentStr);

								// 地址
								String addressStr = item
										.getString("user_address");
								address.setText(addressStr);

								// 生日没有获取----------------------------
								String birth = item.getString("user_birthday");
								if (birth != null && !birth.equals("")) {
									System.out.println("生日时间是："
											+ item.getJSONObject(
													"user_birthday").getLong(
													"time"));
									birth = TimeUtil.getTimeUtilInstance()
											.TimeStamp2Date(
													item.getJSONObject(
															"user_birthday")
															.getLong("time")
															+ "");
									System.out.println("birth=" + birth);
									birthday.setText(birth.split(" ")[0]);
									System.out.println("生日---->>"
											+ birth.split(" ")[0]);
								} else {
									birthday.setText("");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							super.onSuccess(arg0, arg1);
						}

					});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initData() {
		commandbase = CommandBase.instance();
		commandbase.request(new TaskListener() {

			@Override
			public void start() {
				progressDialog.setMessage("加载中...");
				progressDialog.show();
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
					object1.put("account", userAccount);
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
					registerTime.setText(timeStr);
					userInfo.setRegister(timeStr);

					userID.setText(userInfo.getAccount());
					// 电话号码
					String tel = item.getString("user_mobile_phone");
					System.out.println("电话号码---->>" + tel);
					userTelNumber.setText(tel);
					userInfo.setUserPhone(tel);
					// 昵称
					String nick = item.getString("user_display_name");
					nickName.setText(nick);
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
					sex.setText(userSex);
					userInfo.setUserSex(userSex);
					System.out.println("性别---->>" + userSex);

					// 邮箱
					String emailStr = item.getString("user_email");
					emailAddress.setText(emailStr);
					userInfo.setEmail(emailStr);

					// 部门
					String departmentStr = item.getString("user_department");
					department.setText(departmentStr);
					userInfo.setDepartment(departmentStr);

					// 地址
					String addressStr = item.getString("user_address");
					address.setText(addressStr);
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
						birthday.setText(birth);
						userInfo.setBirth(birth);
						System.out.println("生日---->>" + birth);
					} else {
						birthday.setText("");
						userInfo.setBirth("");
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

			@Override
			public void finish() {
				progressDialog.dismiss();
			}

			@Override
			public void failure(String str) {
				Toast.makeText(UserBaseInfoActivity.this, "网络连接失败",
						Toast.LENGTH_SHORT).show();
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

	@SuppressLint("SdCardPath")
	private void initView() {
		sdcardTempFile = new File("/mnt/sdcard/", "tmp_pic_"
				+ SystemClock.currentThreadTimeMillis() + ".jpg");
		// userPlantType = (TextView) findViewById(R.id.userPlantTypeText);
		// userServiceType = (TextView) findViewById(R.id.userServeTypeText);
		// userRoleName = (TextView) findViewById(R.id.userRoleNameText);
		// userScale = (TextView) findViewById(R.id.userScaleText);
		//
		// quxian = (TextView) findViewById(R.id.quxianText);
		userScale = (TextView) findViewById(R.id.user_scale_text);
		userPlantLocation = (TextView) findViewById(R.id.user_plant_location_text);
		userPlantYear = (TextView) findViewById(R.id.user_plant_year_text);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnExit.setOnClickListener(this);
		userID = (TextView) findViewById(R.id.userIDText);
		nickName = (TextView) findViewById(R.id.nickNameText);
		registerTime = (TextView) findViewById(R.id.registerTimeText);
		emailAddress = (TextView) findViewById(R.id.mailAddressText);
		userTelNumber = (TextView) findViewById(R.id.userTelNumberText);
		sex = (TextView) findViewById(R.id.sexText);
		birthday = (TextView) findViewById(R.id.birthdayText);
		department = (TextView) findViewById(R.id.departmentText);
		address = (TextView) findViewById(R.id.addressText);

		fields = (TextView) findViewById(R.id.attention_field_text);
		fieldButton = (TextView) findViewById(R.id.attention_field_detai);
		fieldButton.setOnClickListener(this);

		userImage = (ImageView) findViewById(R.id.userImage);

		saveAndEdit = (TextView) findViewById(R.id.saveAndEdit);
		if (fromWhere.equals("user")) {
			saveAndEdit.setText("编辑");
		} else {
			saveAndEdit.setText(state);
		}
		saveAndEdit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userImage:
			dialog = new AlertDialog.Builder(this).setItems(
					new String[] { "相机", "相册" },
					new DialogInterface.OnClickListener() {

						@SuppressLint("WorldWriteableFiles")
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (0 == which) {
								Uri imageUri = null;
								String fileName = null;
								Intent intentFromCapture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								@SuppressWarnings("deprecation")
								SharedPreferences sharedPreferences = getSharedPreferences(
										"temp", Context.MODE_WORLD_WRITEABLE);
								ImageTools.deletePhotoAtPathAndName(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath(), sharedPreferences
										.getString("tempName", ""));

								fileName = String.valueOf(System
										.currentTimeMillis()) + ".jpg";
								Editor editor = sharedPreferences.edit();
								editor.putString("tempName", fileName);
								editor.commit();
								imageUri = Uri.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										fileName));
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT, imageUri);
								startActivityForResult(intentFromCapture, 0);
							} else {
								Intent intentFromGallery = new Intent();
								intentFromGallery.setType("image/*"); // 璁剧疆鏂囦欢绫诲瀷
								intentFromGallery
										.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intentFromGallery, 1);
							}
						}
					}).create();
			dialog.show();

			break;
		case R.id.saveAndEdit:
			Intent intent1 = new Intent();
			if (fromWhere.equals("user")) {
				intent1.setClass(UserBaseInfoActivity.this,
						UpDataUserInfoActivity.class);
				UserBaseInfoActivity.this.startActivityForResult(intent1,
						UpDataUserActivity);
			} else {
				if (state.equals("添加关注") || state.equals("+关注")) {
					// 添加关注
					addAttention();
				} else {
					// 取消关注
					cancelAttention();
				}
			}
			break;

		case R.id.btnExit:
			if (type.equals("userinfosuccess")) {
				Intent resultIntent = new Intent();
				Bundle bd = new Bundle();
				bd.putString("data", userInfo.getUserName());
				resultIntent.putExtra("reultData", bd);
				// 修改成功
				UserBaseInfoActivity.this.setResult(RESULT_OK, resultIntent);
				UserBaseInfoActivity.this.finish();
				overridePendingTransition(0, R.anim.to_left_out);

			} else {
				UserBaseInfoActivity.this.finish();
			}
			break;
		case R.id.attention_field_detai:
			Intent intent = new Intent();
			intent.setClass(UserBaseInfoActivity.this, FieldsActivity.class);
			// startActivity(intent);
			startActivityForResult(intent, 4);
			break;
		default:
			break;
		}
	}

	private void cancelAttention() {
		commandbase.request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				progressDialog.setMessage("取消中");
				progressDialog.show();
			}

			@Override
			public String requestUrl() {
				return "followerdelete";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("username", userInfo.getAccount());
					object.put("followedusername", userAccount);
				} catch (Exception e) {
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
				System.out.println("添加成功后返回的数据----->>" + msg.toString());
				saveAndEdit.setText("添加关注");
			}

			@Override
			public void finish() {
				progressDialog.dismiss();
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

	private void addAttention() {
		commandbase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				progressDialog.setMessage("添加关注中");
				progressDialog.show();
			}

			@Override
			public String requestUrl() {
				return "followerinsert";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("username", userInfo.getAccount());
					object.put("followedusername", userAccount);

				} catch (Exception e) {
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
				saveAndEdit.setText("取消关注");
			}

			@Override
			public void finish() {
				progressDialog.dismiss();
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

	private void getfields() {
		list = new ArrayList<HashMap<String, Object>>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
			}

			@Override
			public String requestUrl() {
				return "selectTagByUser";
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
				// json解析
				JSONObject data = new JSONObject();
				try {
					data = msg.getJSONObject("data");
					JSONArray array = data.getJSONArray("list");
					String tagName = "";
					int tagId = 0;
					HashMap<String, Object> map = null;
					for (int i = 0; i < array.length(); i++) {
						map = new HashMap<String, Object>();
						data = array.getJSONObject(i);
						tagName = data.getString("tag_name");
						tagId = data.getInt("tag_id");
						map.put("name", tagName);
						map.put("id", tagId);
						list.add(map);
					}
					Message msg1 = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					msg1.obj = list;
					msg1.sendToTarget();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
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

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				if (msg.obj != null) {
					String tv = "";
					for (int i = 0; i < list.size(); i++) {
						tv = tv + "		" + list.get(i).get("name").toString();
					}
					if (list.size() == 0) {
						fields.setText("您还没有关注任何领域");
						userInfo.setFields("您还没有关注任何领域");
					} else {
						fields.setText(tv);
						userInfo.setFields(tv);
					}
				}
				break;
			}
			}
		}

	};

	// 图片上传服务器
	@SuppressLint("WorldWriteableFiles")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 0:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
					System.out.println("Data");
				} else {
					System.out.println("File");
					@SuppressWarnings("deprecation")
					String fileName = getSharedPreferences("temp",
							Context.MODE_WORLD_WRITEABLE).getString("tempName",
							"");
					uri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), fileName));
				}
				startPhotoZoom(uri);
				break;
			case 1:
				startPhotoZoom(data.getData());
				break;
			case 2:
				Bitmap photo = null;
				Uri photoUri = data.getData();
				if (photoUri != null) {
					photo = BitmapFactory.decodeFile(photoUri.getPath());
				}
				if (photo == null) {
					Bundle extra = data.getExtras();
					if (extra != null) {
						photo = (Bitmap) extra.get("data");
						if (photo == null) {
							Bitmap bmp1 = BitmapFactory.decodeResource(
									getResources(),
									R.drawable.head_default_yixin);
							userImage.setImageBitmap(bmp1);
						} else {
							userImage.setImageBitmap(photo);
						}
					}
				}
				break;

			case 3:
				Bundle bd = data.getBundleExtra("reultData");
				String str = bd.getString("data");
				// System.out.println("str=" + str);
				try {

					JSONObject resultObject = new JSONObject(str);
					JSONObject datafanhui = resultObject.getJSONObject("data");
					JSONObject user = datafanhui.getJSONObject("user");
					UserSettingInfo usf = new UserSettingInfo(
							UserBaseInfoActivity.this);
					// System.out.println("返回的数据是--->>" +
					// resultObject.toString());
					// // 移动电话
					// String telnumber = resultObject
					// .getString("user_mobile_phone");
					// userTelNumber.setText(telnumber);
					// userInfo.setUserPhone(telnumber);
					//
					// // 昵称
					// String nickname = resultObject
					// .getString("user_display_name");
					// nickName.setText(nickname);
					// userInfo.setUserName(nickname);
					// // 性别
					// String usersex = resultObject.getString("user_sex");
					// sex.setText(usersex);
					// userInfo.setUserSex(usersex);
					//
					// String birth = resultObject.getString("user_birthday");
					// if (birth != null && !birth.equals("")) {
					// birthday.setText(TimeUtil.getTimeUtilInstance()
					// .TimeStamp2Date(birth).split(" ")[0]);
					// userInfo.setBirth(TimeUtil.getTimeUtilInstance()
					// .TimeStamp2Date(birth));
					// }
					// // 电子邮件
					// String emailAddressText = resultObject
					// .getString("user_email");
					// emailAddress.setText(emailAddressText);
					// // 家庭地址
					// String homeAddressText = resultObject
					// .getString("user_address");
					// address.setText(homeAddressText);
					// userInfo.setAddress(homeAddressText);
					// Toast.makeText(UserBaseInfoActivity.this, "修改信息成功",
					// Toast.LENGTH_LONG).show();

					// String nickNameStr = resultObject
					String userDisplayName = user
							.getString("user_display_name");
					String userSex = user.getString("user_sex_name");
					String userEmail = user.getString("user_email");
					String userWorkPhone = user.getString("user_mobile_phone");
					String address = user.getString("user_address");
					String department = user.getString("user_department");
					String xianName = user.getString("user_xian_name");

					String userRoleNameStr = user.getString("user_role_name");
					String userServeType = user.getString("user_service_type");
					String userScaleStr = user.getString("user_scale");
					String userPlantLocationStr = user
							.getString("user_location");
					String userPlantYears = user.getString("user_years");

					nickName.setText(userDisplayName);
					// private TextView userID;
					// private TextView nickName;
					// private TextView registerTime;
					// private TextView emailAddress;
					// private TextView userTelNumber;
					// private ImageView userImage;
					// private TextView saveAndEdit;
					//
					// private TextView sex;
					// private TextView birthday;
					// private TextView department;
					// private TextView address;
					// private TextView fields;
					// private TextView fieldButton;
					// private TextView quxian;
					// private TextView userServiceType;
					// private TextView userRoleName;
					// private TextView UserScale;
					userScale.setText(userScaleStr);
					userPlantLocation.setText(userPlantLocationStr);
					userPlantYear.setText(userPlantYears);

					userTelNumber.setText(userWorkPhone);
					// quxian.setText(userSettingInfo.getUserXianName());
					// userRoleName.setText(userSettingInfo.getUserRoleName());
					// userServiceType.setText(userServeType);
					// userScale.setText(userScaleStr);

					type = "userinfosuccess";
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			case 4:
				getfields();
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 图片的放大处理
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	// @Override
	// protected void onPause() {
	// unregisterReceiver(receiver);
	// super.onPause();
	// }
	// @Override
	// protected void onResume() {
	// receiver = new MyBroadcastReceiver(UserBaseInfoActivity.this);
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
