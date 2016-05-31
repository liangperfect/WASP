package com.xiaoguo.wasp.mobile.ui.setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.LoginActivity;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.pushDb;
import com.xiaoguo.wasp.mobile.model.PushInfo;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.userinfo.UserBaseInfoActivity;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

public class SettingActivity extends Activity implements OnClickListener {
	private Button backView;
	private TextView titleView;
	private com.xiaoguo.wasp.mobile.widget.ImageTxSpinner myStateView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton AppVesionView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton voiceView;// ������Ϣ������
	private com.xiaoguo.wasp.mobile.widget.My2TextButton resetPswdView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton logoutView;
	private Button exitView;
	private Spinner spinner;

	private PushInfo pushInfo;
	private pushDb pushdb;
	private ProgressDialog progressDialog;
	private CommandBase commandBase;
	private String[] items2 = new String[] { "����", "��", "����" };
	private static final int states[] = { R.drawable.online, R.drawable.kx,
			R.drawable.offline, R.drawable.busy, R.drawable.dnd };// ״̬ͼ
	private String[] stateStrs = { "����", "����", "�뿪", "æµ", "�������" };

	UserSettingInfo userInfo = null;

	String newVerName = "";// �°汾����
	int newVerCode = -1;// �°汾��
	ProgressDialog pd = null;
	String UPDATE_SERVERAPK = "WASP.apk";
	String apkPath = "";

	int selectedIndex = 0;
	LinearLayout titleImgLayout = null;

	View oldView = null;

	String fileSavePath = "";
	private int progress;// ��ȡ��apk������������,�������ع�����
	private static final int DOWN = 1;// ����������������
	private static final int DOWN_FINISH = 0;// ���������������
	private boolean cancelUpdate = false;// �Ƿ�ȡ������

	MyBroadcastReceiver receiver = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		pushInfo = new PushInfo();
		pushdb = new pushDb(SettingActivity.this);
		progressDialog = new ProgressDialog(SettingActivity.this);
		userInfo = new UserSettingInfo(SettingActivity.this);
		commandBase = CommandBase.instance();
		WASPApplication.getInstance().addActivity(this);
		init();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void init() {
		backView = (Button) findViewById(R.id.bt_left);
		// backView.setVisibility(View.VISIBLE);
		// backView.setOnClickListener(this);
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("����");
		myStateView = (com.xiaoguo.wasp.mobile.widget.ImageTxSpinner) findViewById(R.id.my_account_info);
		myStateView.setImageViewImg(R.drawable.head_default_yixin);
		myStateView.setTextViewTx(userInfo.getAccount());
		String nickName = userInfo.getUserName();
		if (nickName == null || "".equals(nickName)) {
			nickName = "";
		}
		myStateView.setTextView3(userInfo.getUserName());
		myStateView.setTextView2(">");
		myStateView.setOnClickListener(new MyOnClickListener());

		spinner = myStateView.getSpinner();
		MyAdapter adapter = new MyAdapter(states, stateStrs,
				SettingActivity.this);
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v,
					int position, long arg3) {
				Presence presence = new Presence(Presence.Type.available);
				switch (position) {
				case 0:// ����
					presence.setMode(org.jivesoftware.smack.packet.Presence.Mode.available);
					break;
				case 1:// ����
					presence.setMode(org.jivesoftware.smack.packet.Presence.Mode.chat);
					break;
				case 2:// �뿪
					presence.setMode(org.jivesoftware.smack.packet.Presence.Mode.away);
					break;
				case 3:// æµ
					presence.setMode(org.jivesoftware.smack.packet.Presence.Mode.xa);
					break;
				case 4:// �������
					presence.setMode(org.jivesoftware.smack.packet.Presence.Mode.dnd);
					break;
				default:
					break;
				}
				ConnectionUtils.getConnection(SettingActivity.this).sendPacket(
						presence);
				TextView label = (TextView) v.findViewById(R.id.label);
				label.setVisibility(View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		AppVesionView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.app_version);
		AppVesionView.setTextView1Text("����ƽ̨");
		AppVesionView.setTextView2Text(">");
		AppVesionView.setOnClickListener(new MyOnClickListener());

		voiceView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.setting_voice);
		voiceView.setTextView1Text("��������");
		voiceView.setTextView2Text(">");
		voiceView.setOnClickListener(new MyOnClickListener());

		resetPswdView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.reset_pswd);
		resetPswdView.setTextView1Text("�����޸�");
		resetPswdView.setTextView2Text(">");
		resetPswdView.setOnClickListener(new MyOnClickListener());

		logoutView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.logout);
		// logoutView.setTextView1Text("�˺��л�");
		logoutView.setTextView1Text("�û�ע��");
		logoutView.setTextView2Text(">");
		logoutView.setOnClickListener(new MyOnClickListener());

		exitView = (Button) findViewById(R.id.exit_account);
		exitView.setOnClickListener(new MyOnClickListener());

	}

	class MyOnClickListener implements TextView.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.my_account_info:
				Intent i = new Intent();
				// i.setClass(SettingActivity.this, AboutMeActivity.class);
				i.setClass(SettingActivity.this, UserBaseInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("where", "user");
				i.putExtra("bd", bundle);
				// startActivity(i);
				startActivityForResult(i, 1);
				break;
			case R.id.app_version:
				Intent i3 = new Intent();
				i3.setClass(SettingActivity.this, AboutUsActivity.class);
				startActivity(i3);
				break;
			case R.id.setting_voice:
				final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				selectedIndex = audioManager.getRingerMode();
				System.out.println("��ǰ����ģʽ��" + selectedIndex);
				new AlertDialog.Builder(SettingActivity.this)
						.setTitle("����ģʽ")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (selectedIndex) {
										case 2:// ��ͨģʽΪ��2
											audioManager
													.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
											break;
										case 1:// ��ģʽΪ��1
											audioManager
													.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
											break;
										case 0:// ����ģʽΪ��0
											audioManager
													.setRingerMode(AudioManager.RINGER_MODE_SILENT);
											break;
										default:
											break;
										}
									}
								})
						.setSingleChoiceItems(items2, selectedIndex,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int which) {
										selectedIndex = which;
									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
				break;
			case R.id.reset_pswd:
				Intent i2 = new Intent();
				i2.setClass(SettingActivity.this, ResetPswdActivity.class);
				startActivity(i2);
				break;
			case R.id.logout:
				new AlertDialog.Builder(SettingActivity.this)
						.setTitle("��ʾ")
						.setMessage("ȷ��Ҫע���û���?")
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										userInfo.setAutoLogin(false);
										userInfo.setSavePswd(false);
										userInfo.setUserAccount("");
										userInfo.setUserPswd("");
										off("logoff");
									}
								}).create().show();
				break;
			case R.id.exit_account:
				new AlertDialog.Builder(SettingActivity.this)
						.setTitle("��ʾ")
						.setMessage("ȷ��Ҫ�˳�Ӧ����?")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// ConnectionUtils.getConnection(SettingActivity.this).disconnect();

										/**
										 * ȡ����ٶȷ�����Ϣ
										 */
										// off("exit");
										WASPApplication.getInstance().exit();

									}
								})
						.setNegativeButton("ȡ��",
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

	}

	// �ٶ���������ش���

	private void off(final String type) {

		if (type.equals("logoff")) {
			Intent i = new Intent();
			i.setClass(SettingActivity.this, LoginActivity.class);
			startActivity(i);
			WASPApplication.getInstance().exit();
		} else {

			WASPApplication.getInstance().exit();
		}

		// pushInfo = pushdb.getPushInfo();
		// commandBase.request(new TaskListener() {
		// @Override
		// public void updateCacheDate(List<HashMap<String, Object>> cacheData)
		// {
		//
		// }
		//
		// @Override
		// public void start() {
		// progressDialog.setTitle("�˳���...");
		// progressDialog.show();
		// }
		//
		// @Override
		// public String requestUrl() {
		// return "Push";
		// }
		//
		// @Override
		// public JSONObject requestData() {
		// JSONObject object = new JSONObject();
		// try {
		// object.put("push_user_id", pushInfo.getUserID());
		// object.put("channel_id", pushInfo.getChannelID());
		// object.put("login_flg", false);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// return object;
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
		// System.out.println("���ص�������" + msg.toString());
		//
		// }
		//
		// @Override
		// public void finish() {
		// progressDialog.dismiss();
		//
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

	}

	public int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.xiaoguo.wasp.mobile", 0).versionCode;
		} catch (NameNotFoundException e) {
			System.out.println("�汾�Ż�ȡ�쳣" + e.getMessage());
		}
		return verCode;
	}

	/**
	 * ��ð汾����
	 */
	public String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.xiaoguo.wasp.mobile", 0).versionName;
		} catch (NameNotFoundException e) {
			System.out.println("�汾���ƻ�ȡ�쳣" + e.getMessage());
		}
		return verName;
	}

	/**
	 * �ӷ������˻�ð汾����汾����
	 * 
	 * @return
	 */
	public boolean getServerVer() {
		try {
			URL url = new URL(
					"http://http://182.254.167.232:8080/wasp_jiangxi/webservice/ap/updateclient");
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();
			InputStreamReader reader = new InputStreamReader(
					httpConnection.getInputStream());
			BufferedReader bReader = new BufferedReader(reader);
			String json = bReader.readLine();
			JSONObject object = new JSONObject(json);
			newVerCode = object.getInt("apkVersion");
			apkPath = object.getString("apkPath");

			System.out.println("��ȡ���İ汾����--->>" + newVerCode + "   apkPth-->>"
					+ apkPath);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * �����°汾
	 */
	public void notNewVersionUpdate() {
		String verName = this.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾��");
		sb.append(verName);
		sb.append("\n�������°汾���������");
		Dialog dialog = new AlertDialog.Builder(this).setTitle("�������")
				.setMessage(sb.toString())
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.show();
	}

	/**
	 * ���°汾
	 */
	public void doNewVersionUpdate() {
		StringBuffer sb = new StringBuffer();
		sb.append("���������°汾��");
		sb.append(newVerName);
		sb.append(",�Ƿ����ϸ���?");
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("�������")
				.setMessage(sb.toString())
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pd.setTitle("��������");
						pd.setMessage("���Ժ�...");
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.show();
						new downloadApkThread().start();
					}
				})
				.setNegativeButton("�ݲ�����",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();
		// ��ʾ���¿�
		dialog.show();
	}

	/**
	 * ����apk
	 */
	public void downFile(final String url) {
		pd.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost get = new HttpPost(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					System.out.println("length=" + length);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								UPDATE_SERVERAPK);
						fileOutputStream = new FileOutputStream(file);
						byte[] b = new byte[16];
						int charb = -1;
						int count = 0;
						while ((charb = is.read(b)) != -1) {
							fileOutputStream.write(b, 0, charb);
							count += charb;
						}
						System.out.println("count=" + count);
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			pd.cancel();
			update();
		}
	};

	/**
	 * ������ɣ�ͨ��handler�����ضԻ���ȡ��
	 */
	public void down() {
		new Thread() {
			public void run() {
				Message message = handler.obtainMessage();
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * ��װӦ��
	 */
	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), UPDATE_SERVERAPK)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	public class downloadApkThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					fileSavePath = sdpath + "download";
					URL url = new URL("http://"
							+ CommandBase.instance().getHost()
							+ "/wasp_jiangxi/ClientVersionDownload?version_no="
							+ newVerName);
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setReadTimeout(5 * 1000);// ���ó�ʱʱ��
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Charser",
							"GBK,utf-8;q=0.7,*;q=0.3");
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					System.out.println("��ȡ���ļ���СΪ��" + length);
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(fileSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					System.out.println("filePath=" + fileSavePath + "WASP"
							+ newVerName + ".apk");
					File apkFile = new File(fileSavePath + "/WASP" + newVerName
							+ ".apk");
					FileOutputStream fos = new FileOutputStream(apkFile);
					System.out.println("�ļ�·����" + apkFile.getPath());
					int count = 0;
					// ����
					byte buf[] = new byte[128];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						Message message = new Message();
						message.obj = DOWN;
						handler1.sendMessage(message);
						if (numread <= 0) {
							// �������
							// ȡ�����ضԻ�����ʾ
							pd.dismiss();
							Message message2 = new Message();
							message2.obj = DOWN_FINISH;
							handler1.sendMessage(message2);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler1 = new Handler() {// ����ui

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch ((Integer) msg.obj) {
			case DOWN:
				pd.setProgress(progress);
				break;
			case DOWN_FINISH:
				installAPK();
				break;

			default:
				break;
			}
		}

	};

	private void installAPK() {
		File apkfile = new File(fileSavePath, "WASP" + newVerName + ".apk");
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		System.out.println("filepath=" + apkfile.toString() + "  "
				+ apkfile.getPath());
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		SettingActivity.this.startActivity(i);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				Bundle bundle1 = data.getBundleExtra("reultData");
				String nickName = bundle1.getString("data");
				System.out.println("���صĽ���ǣ�" + nickName);
				myStateView.setTextView3(nickName);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MyAdapter extends BaseAdapter {
		private int[] states;
		private String[] stateStrs;
		private Context mContext;

		public MyAdapter(int[] states, String[] stateStrs, Context mContext) {
			super();
			this.states = states;
			this.stateStrs = stateStrs;
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return states.length;
		}

		@Override
		public Object getItem(int arg0) {
			return states[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
			v = _LayoutInflater.inflate(R.layout.my_pic_spinner, null);
			if (v != null) {
				ImageView img = (ImageView) v.findViewById(R.id.icon);
				TextView label = (TextView) v.findViewById(R.id.label);
				img.setImageResource(states[position]);
				label.setText(stateStrs[position]);
			}
			return v;
		}

	}

	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				userInfo.setAutoLogin(false);
				userInfo.setSavePswd(false);
				userInfo.setUserAccount("");
				userInfo.setUserPswd("");
				Intent intent4 = new Intent();
				intent4.setClass(SettingActivity.this, LoginActivity.class);
				startActivity(intent4);
				SettingActivity.this.finish();
				break;
			default:
				break;
			}

		}
	};
}
