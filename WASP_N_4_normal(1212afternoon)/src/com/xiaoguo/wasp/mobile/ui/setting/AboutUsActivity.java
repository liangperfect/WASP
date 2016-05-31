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
	private int progress;// 获取新apk的下载数据量,更新下载滚动条
	private static final int DOWN = 1;// 用于区分正在下载
	private static final int DOWN_FINISH = 0;// 用于区分下载完成
	private boolean cancelUpdate = false;// 是否取消下载
	String currentVerName = "";
	int currentVerCode = -1;
	String newVerName = "";// 新版本名称
	int newVerCode = -1;// 新版本号
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
		titleView.setText("关于平台");

		appIconView = (ImageView) findViewById(R.id.app_img);
		appIconView.setImageResource(R.drawable.wasp_new_logo);
		appVersionView = (TextView) findViewById(R.id.app_version);

		currentVerName = getVerName(getApplicationContext());
		currentVerCode = getVerCode(getApplicationContext());
		appVersionView.setText(currentVerName);

		updateAppVesionView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.app_version_1);
		updateAppVesionView.setTextView1Text("版本更新");
		updateAppVesionView.setTextView2Text(">");
		updateAppVesionView.setOnClickListener(this);

		aboutAppView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.about_app);
		aboutAppView.setTextView1Text("功能介绍");
		aboutAppView.setTextView2Text(">");
		aboutAppView.setOnClickListener(this);

		shareAppView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.share_app);
		shareAppView.setTextView1Text("应用分享");
		shareAppView.setTextView2Text(">");
		shareAppView.setOnClickListener(this);

		AppUsView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.about_us);
		AppUsView.setTextView1Text("联系我们");
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
			System.out.println("当前版本：" + currentVerName);
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
									System.out.println("子："
											+ newVerStr.substring(newVerStr
													.lastIndexOf(".")));
									newVerCode = Integer.parseInt(newVerStr
											.substring(newVerStr
													.lastIndexOf(".") + 1));
									doNewVersionUpdate();// 更新版本
								} else {
									notNewVersionUpdate();// 提示已是最新版本
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
			// 生成二维码图片
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
				// canvas.drawText("江西烟草手机平台", 150, 150, p);
				// Bitmap bitLogo = BitmapFactory.decodeResource(getResources(),
				// R.drawable.wasp_new_logo);

				// canvas.drawBitmap(bitLogo, 0, 0, null);
				// Toast.makeText(
				// AboutUsActivity.this,
				// "图片的大小都是-->>" + erweimaBitmap.getHeight() + "  : "
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
				// "我正在使用智慧农业平台，你也赶快加入吧！！点击或扫描二维码体验吧！");
				i3.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				i3.putExtra(Intent.EXTRA_SUBJECT, "分享");
				i3.putExtra(Intent.EXTRA_TEXT,
						"我正在使用智慧农业平台，你也赶快加入吧！！点击或扫描二维码体验吧！");
				i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// i3.setType("image/jpeg");
				AboutUsActivity.this.startActivity(Intent.createChooser(i3,
						"分享江西省烟草平台"));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("二维码生成出错");
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
	 * 不更新版本
	 */
	public void notNewVersionUpdate() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本：");
		sb.append(currentVerName);
		sb.append("\n已是最新版本，无需更新");
		Dialog dialog = new AlertDialog.Builder(this).setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.show();
	}

	/**
	 * 更新版本
	 */
	public void doNewVersionUpdate() {
		StringBuffer sb = new StringBuffer();
		sb.append("发现有最新版本：");
		sb.append(newVerName);
		sb.append(",是否马上更新?");
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pd.setTitle("正在下载");
						pd.setMessage("正在下载,请稍后...");
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.show();
						new downloadApkThread().start();
					}
				})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();
		// 显示更新框
		dialog.show();
	}

	/**
	 * 下载apk
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
	 * 下载完成，通过handler将下载对话框取消
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
	 * 安装应用
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
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					fileSavePath = sdpath + "download";
					URL url = new URL("http://"
							+ CommandBase.instance().getHost()
							+ "/wasp_jiangxi/ClientVersionDownload?version_no="
							+ newVerName);

					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setReadTimeout(5 * 1000);// 设置超时时间
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Charser",
							"GBK,utf-8;q=0.7,*;q=0.3");
					// 获取文件大小
					int length = conn.getContentLength();
					System.out.println("获取的文件大小为：" + length);
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(fileSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					System.out.println("filePath=" + fileSavePath + "WASP"
							+ newVerName + ".apk");
					File apkFile = new File(fileSavePath + "/WASP" + newVerName
							+ ".apk");
					FileOutputStream fos = new FileOutputStream(apkFile);
					System.out.println("文件路径：" + apkFile.getPath());
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						Message message = new Message();
						message.obj = DOWN;
						handler1.sendMessage(message);
						if (numread <= 0) {
							// 下载完成
							// 取消下载对话框显示
							pd.dismiss();
							Message message2 = new Message();
							message2.obj = DOWN_FINISH;
							handler1.sendMessage(message2);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
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
	private Handler handler1 = new Handler() {// 跟心ui

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
		// 通过Intent安装APK文件
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
			System.out.println("版本号获取异常" + e.getMessage());
		}
		return verCode;
	}

	/**
	 * 获得版本名称
	 */
	public String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.xiaoguo.wasp", 0).versionName;
		} catch (NameNotFoundException e) {
			System.out.println("版本名称获取异常" + e.getMessage());
		}
		return verName;
	}

	/**
	 * 从服务器端获得版本号与版本名称
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
	// // 好友请求
	// filter.addAction(Constant.ROSTER_SUBSCRIPTION);
	// filter.addAction(Constant.NEW_MESSAGE_ACTION);
	// registerReceiver(receiver, filter);
	// super.onResume();
	// }
}
