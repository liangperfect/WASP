package com.xiaoguo.wasp.mobile;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.IMMessageDb;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.database.pushDb;
import com.xiaoguo.wasp.mobile.model.IMMessage;
import com.xiaoguo.wasp.mobile.model.PushInfo;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.BasicActivity;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.register.RegisterActivity;
import com.xiaoguo.wasp.mobile.utils.JsonUtils;
import com.xiaoguo.wasp.mobile.utils.SHA1Util;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.IMChatService;
import com.xiaoguo.wasp.mobile.xmpphelper.IMContactService;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;
import com.xiaoguo.wasp.mobile.xmpphelper.MyConnectionListener;
import com.xiaoguo.wasp.mobile.xmpphelper.fileListenerService;

/*
 * 登陆模块
 * 功能：登录并获取离线消息
 * @author eva
 * */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity extends BasicActivity implements OnClickListener {
	// title
	// private TextView titleView;
	// 临时注册
	private Button registerView;
	private static final int WHAT_DID_LOAD_DATA = 0;
	// UI references.
	private EditText accountView;
	private EditText passwordView;
	private CheckBox savePassword;
	private CheckBox autoLogin;
	private Button loginView;
	private Button newAccountView;
	private Button searchPasswordView;
	private UserSettingInfo userSettingInfo;
	private String account;
	private String password;

	UserSettingInfo userInfo = null;
	JSONObject object = null;
	CommandBase commandBase = null;
	ProgressDialog dialog = null;

	MyBroadcastReceiver receiver = null;

	IMMessageDb messageDb = null;

	AlertDialog.Builder builder = null;
	OfflineMessageManager offlineManager = null;
	String userType = "";
	ProductDb productDb = null;
	int flag = 0;
	JSONObject object2 = null;
	private PushInfo pushInfo;
	private pushDb pushdb;
	private TimeUtil mTimeUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		userSettingInfo = new UserSettingInfo(LoginActivity.this);
		mTimeUtil = TimeUtil.getTimeUtilInstance();
		commandBase = CommandBase.instance();
		dialog = getProgressDialog();
		commandBase.setMainActivityContext(LoginActivity.this);
		Activity activity = new LoginActivity();
		commandBase.setCurrActivityContext(LoginActivity.this, activity);
		pushInfo = new PushInfo();
		pushdb = new pushDb(LoginActivity.this);

		userInfo = new UserSettingInfo(this);
		messageDb = new IMMessageDb(LoginActivity.this);
		String host = userInfo.getHost();
		if (!host.equals("") && host != null) {
			CommandBase.setHost(host);
		}

		userType = userInfo.getType();
		productDb = new ProductDb(this);
		setupView();
	}

	private void setupView() {

		registerView = (Button) this.findViewById(R.id.register);
		registerView.setOnClickListener(this);

		accountView = (EditText) findViewById(R.id.telephone);
		passwordView = (EditText) findViewById(R.id.password);

		savePassword = (CheckBox) findViewById(R.id.save_password);
		autoLogin = (CheckBox) findViewById(R.id.auto_login);

		loginView = (Button) findViewById(R.id.login);
		loginView.setOnClickListener(this);

		// 长按登陆键可以输入服务器IP地址和端口号
		loginView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final EditText host_input = new EditText(LoginActivity.this);
				host_input.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				host_input.setText(CommandBase.instance().getHost());
				host_input
						.setBackgroundResource(android.R.drawable.editbox_background);
				new AlertDialog.Builder(LoginActivity.this)
						.setTitle("请输入服务器IP地址:")
						.setView(host_input)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String host = host_input.getText()
												.toString();
										System.out.println("host=" + host);
										CommandBase.setHost(host);
										ConnectionUtils.setHost(host);
										userInfo.setHost(host);
									}
								}).setNegativeButton("取消", null).show();
				return true;
			}
		});

		newAccountView = (Button) findViewById(R.id.register_new_user);
		newAccountView.setOnClickListener(this);

		searchPasswordView = (Button) findViewById(R.id.search_password);
		searchPasswordView.setOnClickListener(this);

		accountView.setText(userInfo.getAccount());
		if (userInfo.getSavePswd()) {
			passwordView.setText(userInfo.getPassword());
			savePassword.setChecked(true);
			if (userInfo.getAutoLogin()) {
				autoLogin.setChecked(true);
				attemptLogin();
			}
		}
		passwordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		// 保存用户信息，下次自动获取用户信息
		savePassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (savePassword.isChecked()) {
					Toast.makeText(LoginActivity.this, "选中记住密码！",
							Toast.LENGTH_SHORT).show();
				} else {
				}
			}
		});
		// 自动登录，点击此选项后每次将自动登录到Logo界面
		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (autoLogin.isChecked()) {
					Toast.makeText(LoginActivity.this, "选中自动登录！",
							Toast.LENGTH_SHORT).show();
				} else {
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			attemptLogin();
			break;
		case R.id.bt_right:
			Intent i2 = new Intent();
			i2.setClass(LoginActivity.this, RegisterActivity.class);
			startActivityForResult(i2, 1);
			// this.finish();
			break;
		case R.id.register:
			Intent i1 = new Intent();
			i1.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(i1);
			break;
		case R.id.search_password:
			Toast.makeText(LoginActivity.this, "开发中...", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
	}

	private void attemptLogin() {
		passwordView.setError(null);
		password = passwordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			passwordView.setError("此项不能为空");
			focusView = passwordView;
			cancel = true;
		} else if (password.length() < 3) {
			passwordView.setError("密码太短");
			focusView = passwordView;
			cancel = true;
		}

		// Reset errors.
		accountView.setError(null);
		// Store values at the time of the login attempt.
		account = accountView.getText().toString();
		// Check for a valid studentId.
		if (TextUtils.isEmpty(account)) {
			accountView.setError("此项不能为空");
			focusView = accountView;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			// 联网登陆
			commandBase.setUserInfo(account, "");
			commandBase.request(new TaskListener() {
				@Override
				public void start() {
					dialog.setMessage("正在登录，请稍后...");
					dialog.show();
				}

				@Override
				public String requestUrl() {
					return "userlogin";
				}

				@Override
				public JSONObject requestData() {
					JSONObject object = JsonUtils.getLoginJson(account,
							SHA1Util.getSHA1EncString(password));
					// System.out.println("object=" + object);
					return object;
				}

				@Override
				public void messageUpdated(final JSONObject msg) {
					flag = 1;
					object2 = msg;

					// new AsyncTask<Void, Void, Boolean>() {
					//
					// @Override
					// protected Boolean doInBackground(Void... params) {
					// System.out.println("执行openfire");
					// if (ConnectionUtils.getConnection(
					// LoginActivity.this).isAuthenticated()) {
					// return true;
					// } else {
					// try {
					// XMPPConnection connection = ConnectionUtils
					// .getConnection(LoginActivity.this);
					// if (connection.isConnected()) {
					// connection.login(
					// account,// password);
					// SHA1Util.getSHA1EncString(password));
					//
					// System.out.println("加密后的密码为:"
					// + SHA1Util
					// .getSHA1EncString(password));
					// return ConnectionUtils.getConnection(
					// LoginActivity.this)
					// .isAuthenticated();
					// } else {
					// return false;
					// }
					// } catch (XMPPException e) {
					// System.out.println("登陆出错");
					// return false;
					// }
					// }
					// }
					//
					// @Override
					// protected void onPostExecute(Boolean result) {
					// // System.out.println("openfire连接情况是--->>" + result);
					// dialog.dismiss();
					// System.out.println("执行结果");
					// if (result) {
					// System.out.println("xmpp成功");
					// initServer();
					// MyConnectionListener connectionListener = new
					// MyConnectionListener(
					// LoginActivity.this, account, password);
					// ConnectionUtils.getConnection(
					// LoginActivity.this)
					// .addConnectionListener(
					// connectionListener);
					// getOfflineMessages();
					// // System.out.println("login_reply data=" + msg);
					// try {
					// object = msg.getJSONObject("data");
					// String session = object
					// .getString("session");
					// String type = object.getString("type");
					// // System.out.println("session=" + session);
					// // System.out.println("type=" + type);
					// // userInfo.setType(type);
					// // userInfo.setAddress("");
					// // userInfo.setBirth("");
					// // userInfo.setDepartment("");
					// // userInfo.setEmail("");
					// // userInfo.setRegister("");
					// // userInfo.setUserName("");
					// // userInfo.setUserPhone("");
					// // userInfo.setUserSex("");
					// userInfo.setUserAccount(account);
					// commandBase.setUserInfo(account, session);
					// if (savePassword.isChecked()) {
					// userInfo.setUserPswd(password);
					// userInfo.setSavePswd(true);
					// if (autoLogin.isChecked()) {
					// userInfo.setAutoLogin(true);
					// // System.out.println("userInfo="
					// // + userInfo.getAccount()
					// // + ","
					// // + userInfo.getPassword()
					// // + ","
					// // + userInfo.getSavePswd()
					// // + ","
					// // + userInfo.getAutoLogin());
					// } else {
					// userInfo.setAutoLogin(false);
					// }
					// }
					// Toast.makeText(LoginActivity.this, "登录成功",
					// Toast.LENGTH_SHORT).show();
					// Intent i = new Intent();
					// if ((userType.equals("farmer") && productDb
					// .getAllExperts(account).size() <= 0)
					// || (!userType.equals("farmer") && productDb
					// .getAllFarmers(account)
					// .size() <= 0)) {
					// // i.setClass(LoginActivity.this,
					// // BeforeMainActivity.class);
					// } else {
					// i.setClass(LoginActivity.this,
					// MainActivity.class);
					// }
					// startActivity(i);
					// LoginActivity.this.finish();
					// } catch (JSONException e) {
					// e.printStackTrace();
					// Toast.makeText(LoginActivity.this, "解析异常！",
					// Toast.LENGTH_SHORT).show();
					// }
					// } else {
					// Toast.makeText(MContext, "openfire登陆失败",
					// Toast.LENGTH_SHORT).show();
					// System.out.println("xmpp失败");
					// }
					// super.onPostExecute(result);
					// }
					//
					// @Override
					// protected void onPreExecute() {
					// System.out.println("openfire开始了");
					// super.onPreExecute();
					// }
					//
					// }.execute();

				}

				@Override
				public void finish() {
					if (flag == 0) {
						dialog.dismiss();
					} else {
						new AsyncTask<Void, Void, Boolean>() {
							@Override
							protected Boolean doInBackground(Void... params) {
								System.out.println("执行openfire");
								// 禁Openfire从这开始
								if (ConnectionUtils.getConnection(
										LoginActivity.this).isAuthenticated()) {
									return true;
								} else {
									try {
										XMPPConnection connection = ConnectionUtils
												.getConnection(LoginActivity.this);
										if (connection.isConnected()) {
											connection
													.login(account,
															// password);
															SHA1Util.getSHA1EncString(password));
											System.out.println("加密后的密码为:"
													+ SHA1Util
															.getSHA1EncString(password));
											return ConnectionUtils
													.getConnection(
															LoginActivity.this)
													.isAuthenticated();
										} else {
											return false;
										}
									} catch (XMPPException e) {
										System.out.println("登陆出错");
										return false;
									}
								}
								// 禁Openfire从这结束
							}

							@Override
							protected void onPostExecute(Boolean result) {
								dialog.dismiss();
								System.out.println("从openfire返回的结果是--->>"
										+ result);
								System.out.println("执行结果");
								if (result) {
									System.out.println("xmpp成功");
									initServer();
									MyConnectionListener connectionListener = new MyConnectionListener(
											LoginActivity.this, account,
											password);
									ConnectionUtils.getConnection(
											LoginActivity.this)
											.addConnectionListener(
													connectionListener);
									XMPPConnection connection = ConnectionUtils
											.getConnection(LoginActivity.this);
									if (connection.isConnected()) {
										getOfflineMessages();
									}
									System.out.println("login_reply data="
											+ object2);
									try {

										System.out
												.println("这里的object2是有什么东西的--->>"
														+ object2.toString());

										object = object2.getJSONObject("data");
										String session = object
												.getString("session");
										String type = object.getString("type");
										JSONObject user = object
												.getJSONObject("user");

										// JSONObject birthdayJsonObject = user
										// .getJSONObject("user_birthday");
										// String userBirthday = "";
										// if (birthdayJsonObject != null) {
										// long userBirthdayLong =
										// birthdayJsonObject
										// .getLong("time");
										// userBirthday = mTimeUtil
										// .getDateToStringType2(userBirthdayLong);
										// }

										String plantTypeStr = user
												.getString("user_tag_name");
										String userDisplayName = user
												.getString("user_display_name");
										String userSex = user
												.getString("user_sex_name");
										String userEmail = user
												.getString("user_email");
										String userWorkPhone = user
												.getString("user_mobile_phone");
										String address = user
												.getString("user_address");
										String department = user
												.getString("user_department");
										String xianName = user
												.getString("user_xian_name");

										String userRoleName = user
												.getString("user_role_name");
										String userServeType = user
												.getString("user_service_type");
										String userScale = user
												.getString("user_scale");
										String userPlantLocation = user
												.getString("user_location");
										String userPlantYear = user
												.getString("user_years");

										System.out.println("用户的一些基本信息-->>"
												+ userDisplayName + ";"
												+ userSex + ";" + userEmail
												+ ";" + userWorkPhone + ";"
												+ address + ";" + department);

										userInfo.setType(type);
										userInfo.setAddress(address);
										userInfo.setBirth("");
										userInfo.setDepartment(department);
										userInfo.setEmail(userEmail);
										userInfo.setRegister("");
										userInfo.setUserName(userDisplayName);
										userInfo.setUserPhone(userWorkPhone);
										userInfo.setUserSex(userSex);
										userInfo.setUserAccount(account);
										userInfo.setUserPswd(password);
										userInfo.setUserXianName(xianName);
										userInfo.setUserRoleName(userRoleName);
										userInfo.setUserServeType(userServeType);
										userInfo.setUserScale(userScale);
										userInfo.setUserPlantType(plantTypeStr);
										userInfo.setUserPlantLocation(userPlantLocation);
										userInfo.setUserPlantYear(userPlantYear);
										commandBase.setUserInfo(account,
												session);
										if (savePassword.isChecked()) {
											userInfo.setSavePswd(true);
											if (autoLogin.isChecked()) {
												userInfo.setAutoLogin(true);
											} else {
												userInfo.setAutoLogin(false);
											}
										}
										Toast.makeText(LoginActivity.this,
												"登录成功", Toast.LENGTH_SHORT)
												.show();
										Intent i = new Intent();
										if ((userType.equals("user_admin") && productDb
												.getAllExperts(account).size() <= 0)
												|| (!userType
														.equals("user_admin") && productDb
														.getAllFarmers(account)
														.size() <= 0)) {
											i.setClass(LoginActivity.this,
													BeforeMainActivity.class);

										} else {

											bindBaiDuCloud();
											i.setClass(LoginActivity.this,
													MainActivityTab.class);
										}
										startActivity(i);
										LoginActivity.this.finish();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								} else {
									Toast.makeText(MContext, "登陆失败",
											Toast.LENGTH_SHORT).show();
									System.out.println("xmpp失败");
								}
								super.onPostExecute(result);
							}

							@Override
							protected void onPreExecute() {
								System.out.println("openfire开始了");
								super.onPreExecute();
							}

						}.execute();
					}
				}

				@Override
				public void failure(String str) {
					dialog.dismiss();
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
	}

	/**
	 * 初始化各项服务
	 */
	private void initServer() {
		Intent server = new Intent(MContext, IMContactService.class);
		startService(server);
		Intent chatServer = new Intent(MContext, IMChatService.class);
		startService(chatServer);
		Intent fileServer = new Intent(MContext, fileListenerService.class);
		startService(fileServer);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void getOfflineMessages() {
		offlineManager = new OfflineMessageManager(
				ConnectionUtils.getConnection(LoginActivity.this));
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				Boolean boolean1 = false;
				try {
					Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager
							.getMessages();
					IMMessage tempMessage = null;
					// System.out.println("离线消息数："
					// + offlineManager.getMessageCount());
					while (it.hasNext()) {
						org.jivesoftware.smack.packet.Message message = it
								.next();
						tempMessage = new IMMessage();
						// tempMessage.setMsgType(1);
						tempMessage.setToSubJid(message.getTo());
						tempMessage.setFromSubJid(message.getFrom().substring(
								0, message.getFrom().indexOf("/")));
						String content = message.getBody();
						try {
							content = new String(content.getBytes(), "utf_8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						tempMessage.setTime(message.getProperty(
								IMMessage.KEY_TIME).toString());
						System.out.println("time="
								+ message.getProperty(IMMessage.KEY_TIME)
										.toString());
						tempMessage.setContent(message.getBody());
						String from = message.getFrom();
						System.out.println("from=" + from);
						from = from.substring(0, from.indexOf("@"));
						System.out.println("处理后的：" + from);
						System.out.println("tempMessage=" + tempMessage);
						messageDb.saveMessage(tempMessage, from,
								userInfo.getAccount());
						Log.e("LoginActivity",
								"收到离线消息, Received from 【" + message.getFrom()
										+ "】 message: " + message.getBody());
					}
					boolean1 = true;
				} catch (XMPPException e1) {
					boolean1 = false;
					System.out.println("获取离线消息失败");
					e1.printStackTrace();
				}
				return boolean1;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					try {
						if (ConnectionUtils.getConnection(MContext)
								.isConnected()) {
							offlineManager.deleteMessages();
							Presence presence = new Presence(
									Presence.Type.available);
							presence.setMode(org.jivesoftware.smack.packet.Presence.Mode.available);
							ConnectionUtils.getConnection(LoginActivity.this)
									.sendPacket(presence);
							Intent intent = new Intent(
									Constant.OFFLINE_MESSAGE_ACTION);
							intent.putExtra("offline", "success");
							sendBroadcast(intent);
						}
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}

		}.execute();

	}

	/**
	 * 百度云推送相关代码
	 * 
	 * 
	 * private void BindBaiDu() { // 每次登陆的时候都请求一次
	 * PushManager.startWork(getApplicationContext(),
	 * PushConstants.LOGIN_TYPE_API_KEY, "eBYfpEwZZw5q2sSCDhjMUhSo"); }
	 * 
	 * private void onLogin() {
	 * 
	 * pushInfo = pushdb.getPushInfo(); commandBase.request(new TaskListener() {
	 * 
	 * @Override public void updateCacheDate(List<HashMap<String, Object>>
	 *           cacheData) {
	 * 
	 *           }
	 * @Override public void start() {
	 * 
	 *           }
	 * @Override public String requestUrl() { return "Push"; }
	 * @Override public JSONObject requestData() { JSONObject object = new
	 *           JSONObject(); try { object.put("push_user_id",
	 *           pushInfo.getUserID()); object.put("channel_id",
	 *           pushInfo.getChannelID()); object.put("login_flg", true); }
	 *           catch (JSONException e) { e.printStackTrace(); }
	 * 
	 *           return object; }
	 * @Override public String readCache() { return null; }
	 * @Override public boolean needCacheTask() { return false; }
	 * @Override public void messageUpdated(JSONObject msg) {
	 *           System.out.println("返回的数据是" + msg.toString()); }
	 * @Override public void finish() {
	 * 
	 *           }
	 * @Override public String filepath() { return null; }
	 * @Override public void failure(String str) {
	 * 
	 *           }
	 * @Override public String contentype() { return null; } });
	 * 
	 *           }
	 */

	/**
	 * 百度云推送绑定
	 */
	public void bindBaiDuCloud() {
		System.out.println("百度绑定这里执行了的");
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, "E5qqVsQxeql9q1bWlMeGNXtC");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				Bundle bundle = data.getBundleExtra("bundle");
				String account = bundle.getString("username");
				String password = bundle.getString("password");
				System.out.println("account=" + account + ";password="
						+ password);
				accountView.setText(account);
				passwordView.setText(password);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
