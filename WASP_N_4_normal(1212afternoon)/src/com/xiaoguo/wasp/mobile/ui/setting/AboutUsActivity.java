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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.utils.MyAsyncHttpClient;
import com.xiaoguo.wasp.mobile.utils.QRcodeUtils;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsActivity extends Activity implements OnClickListener {
	private Button backBtView;
	private TextView titleView;
	private ImageView appIconView;
	private TextView appVersionView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton updateAppVesionView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton aboutAppView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton shareAppView;
	private com.xiaoguo.wasp.mobile.widget.My2TextButton AppUsView;

	String fileSavePath = "";
	private int progress;// ��ȡ��apk������������,�������ع�����
	private static final int DOWN = 1;// ����������������
	private static final int DOWN_FINISH = 0;// ���������������
	private boolean cancelUpdate = false;// �Ƿ�ȡ������
	String currentVerName = "";
	int currentVerCode = -1;
	String newVerName = "";// �°汾����
	int newVerCode = -1;// �°汾��
	ProgressDialog pd = null;
	String UPDATE_SERVERAPK = "WASP.apk";
	String apkPath = "";

	MyBroadcastReceiver receiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		pd = new ProgressDialog(this);
		WASPApplication.getInstance().addActivity(this);
		init();
	}

	private void init() {
		backBtView = (Button) findViewById(R.id.bt_left);
		backBtView.setVisibility(View.VISIBLE);
		backBtView.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("����ƽ̨");

		appIconView = (ImageView) findViewById(R.id.app_img);
		appIconView.setImageResource(R.drawable.wasp_new_logo);
		appVersionView = (TextView) findViewById(R.id.app_version);

		currentVerName = getVerName(getApplicationContext());
		currentVerCode = getVerCode(getApplicationContext());
		appVersionView.setText(currentVerName);

		updateAppVesionView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.app_version_1);
		updateAppVesionView.setTextView1Text("�汾����");
		updateAppVesionView.setTextView2Text(">");
		updateAppVesionView.setOnClickListener(this);

		aboutAppView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.about_app);
		aboutAppView.setTextView1Text("���ܽ���");
		aboutAppView.setTextView2Text(">");
		aboutAppView.setOnClickListener(this);

		shareAppView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.share_app);
		shareAppView.setTextView1Text("Ӧ�÷���");
		shareAppView.setTextView2Text(">");
		shareAppView.setOnClickListener(this);

		AppUsView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.about_us);
		AppUsView.setTextView1Text("��ϵ����");
		AppUsView.setTextView2Text(">");
		AppUsView.setOnClickListener(this);
	}

	@SuppressLint("SdCardPath")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.app_version_1:
			RequestParams params = new RequestParams();
			System.out.println("��ǰ�汾��" + currentVerName);
			params.put("version_no", currentVerName);
			MyAsyncHttpClient.post("updateclient", params,
					new JsonHttpResponseHandler() {

						@Override
						public void onFailure(Throwable arg0, JSONObject arg1) {
							super.onFailure(arg0, arg1);
						}

						@Override
						public void onSuccess(int arg0, JSONObject arg1) {
							System.out.println("settingMsg=" + arg1);
							try {
								String newVerStr = arg1.getString("apkVersion");
								newVerName = arg1.getString("apkVersion");
								if (newVerName != null
										&& !newVerName.equals("")) {
									System.out.println("�ӣ�"
											+ newVerStr.substring(newVerStr
													.lastIndexOf(".")));
									newVerCode = Integer.parseInt(newVerStr
											.substring(newVerStr
													.lastIndexOf(".") + 1));
									doNewVersionUpdate();// ���°汾
								} else {
									notNewVersionUpdate();// ��ʾ�������°汾
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							super.onSuccess(arg0, arg1);
						}

						@Override
						public void onFinish() {
							pd.dismiss();
							super.onFinish();
						}

						@Override
						public void onStart() {
							pd.show();
							super.onStart();
						}
					});
			break;
		case R.id.about_app:
			Intent i1 = new Intent();
			i1.setClass(AboutUsActivity.this, AboutAppActivity.class);
			startActivity(i1);
			break;
		case R.id.share_app:
			// ���ɶ�ά��ͼƬ
			// String qrcodeStr = "http://" + "182.254.167.232:8080"
			// + "/wasp_jiangxi/ClientVersionDownload?version_no="
			// + currentVerName;
			try {
				// QRcodeUtils.saveEWM(QRcodeUtils.androidQRCode("http://"
				// + "182.254.167.232:8080"
				// + "/wasp_jiangxi/ClientVersionDownload?version_no="
				// + "2.0.18"), "2.0.18", 0);
				Bitmap erweimaBitmap = QRcodeUtils.androidQRCode("http://"
						+ "182.254.167.232:8080"
						+ "/wasp_jiangxi/ClientVersionDownload?version_no="
						+ "2.0.18");
				// Canvas canvas = new Canvas(erweimaBitmap);
				// Paint p = new Paint();
				// p.setAntiAlias(true);
				// p.setColor(Color.BLACK);
				// p.setTextSize(50);
				// canvas.drawColor(Color.RED);
				// canvas.drawText("�����̲��ֻ�ƽ̨", 150, 150, p);
				// Bitmap bitLogo = BitmapFactory.decodeResource(getResources(),
				// R.drawable.wasp_new_logo);

				// canvas.drawBitmap(bitLogo, 0, 0, null);
				// Toast.makeText(
				// AboutUsActivity.this,
				// "ͼƬ�Ĵ�С����-->>" + erweimaBitmap.getHeight() + "  : "
				// + erweimaBitmap.getWidth(), Toast.LENGTH_SHORT)
				// .show();
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.wasp_qr);
				QRcodeUtils.saveEWM(bitmap, "2.0.18", 0);
				Intent i3 = new Intent(Intent.ACTION_SEND);
				i3.setType("text/plain");
				i3.setType("image/png");
				File file = new File("/sdcard/WASP/qrcode/" + "2.0.18" + ".png");
				// i3.putExtra(Intent.EXTRA_TEXT,
				// "������ʹ���ǻ�ũҵƽ̨����Ҳ�Ͽ����ɣ��������ɨ���ά������ɣ�");
				i3.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				i3.putExtra(Intent.EXTRA_SUBJECT, "����");
				i3.putExtra(Intent.EXTRA_TEXT,
						"������ʹ���ǻ�ũҵƽ̨����Ҳ�Ͽ����ɣ��������ɨ���ά������ɣ�");
				i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// i3.setType("image/jpeg");
				AboutUsActivity.this.startActivity(Intent.createChooser(i3,
						"������ʡ�̲�ƽ̨"));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("��ά�����ɳ���");
			}
			break;
		case R.id.about_us:
			Intent i = new Intent();
			i.setClass(AboutUsActivity.this, AuthorActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}

	}

	/**
	 * �����°汾
	 */
	public void notNewVersionUpdate() {
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾��");
		sb.append(currentVerName);
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
						pd.setMessage("��������,���Ժ�...");
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
					byte buf[] = new byte[1024];
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
		AboutUsActivity.this.startActivity(i);

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
					"com.xiaoguo.wasp", 0).versionName;
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
					"http://218.65.88.29:10080/wasp_jiangxi/webservice/ap/updateclient");
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
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	// @Override
	// protected void onPause() {
	// unregisterReceiver(receiver);
	// super.onPause();
	// }
	// @Override
	// protected void onResume() {
	// receiver = new MyBroadcastReceiver(AboutUsActivity.this);
	// IntentFilter filter = new IntentFilter();
	//
	// filter.addAction(Constant.ROSTER_ADDED);
	// filter.addAction(Constant.ROSTER_DELETED);
	// filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
	// filter.addAction(Constant.ROSTER_UPDATED);
	// // ��������
	// filter.addAction(Constant.ROSTER_SUBSCRIPTION);
	// filter.addAction(Constant.NEW_MESSAGE_ACTION);
	// registerReceiver(receiver, filter);
	// super.onResume();
	// }
}
