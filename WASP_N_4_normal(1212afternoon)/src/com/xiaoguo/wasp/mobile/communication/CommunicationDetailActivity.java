package com.xiaoguo.wasp.mobile.communication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

public class CommunicationDetailActivity extends Activity implements
		OnClickListener {
	private TextView showCommunicationContent, title, contentTitle,
			contentTime, contentResponseNums, exchangePublisherName;
	private CommandBase commandBase;
	private ProgressDialog mProgressDialog;
	private Button exitBtn, publishComment, commentBtn;
	/**
	 * 交流Item的ID
	 */
	private int selectExchangeID;
	/**
	 * 对交流的回复数量
	 */
	private int commentCountInt;
	private String publishTimeStr;
	private String contentTitleStr;
	// 文章详细信息
	private String exchangeContent;
	private String exchangePublisherNameStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_detail);
		overridePendingTransition(R.anim.from_right_into, R.anim.to_left_out);
		initView();
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		selectExchangeID = intent.getIntExtra("exchange_id", 0);
		commentCountInt = intent.getIntExtra("commentCount", 0);
		publishTimeStr = intent.getStringExtra("exchange_time");
		contentTitleStr = intent.getStringExtra("content_title");
		exchangePublisherNameStr = intent
				.getStringExtra("exchange_publisher_name");
		exchangePublisherName.setText(exchangePublisherNameStr);
		contentTitle.setText(contentTitleStr);
		contentTime.setText(publishTimeStr);
		contentResponseNums.setText(commentCountInt + "");
		commandBase = CommandBase.instance();
		mProgressDialog = new ProgressDialog(CommunicationDetailActivity.this);
		// mProgressDialog.setTitle("获取数据中,请稍后");
		mProgressDialog.setTitle("获取数据中,请稍后");
		commandBase.request(new TaskListener() {

			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				mProgressDialog.show();
			}

			@Override
			public String requestUrl() {
				return "selectExchangeById";
			}

			@Override
			public JSONObject requestData() {
				JSONObject data = new JSONObject();
				try {
					data.put("exchange_id", selectExchangeID);
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
				System.out.println("返回的数据--->>" + msg.toString());
				// showView.setText(msg.toString());
				JSONObject data = new JSONObject();
				try {
					data = msg.getJSONObject("data");
					JSONObject exchange = new JSONObject();
					exchange = data.getJSONObject("exchange");
					exchangeContent = exchange.getString("exchange_content");
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void finish() {
				mProgressDialog.dismiss();
				URLImageParser p = new URLImageParser(showCommunicationContent,
						CommunicationDetailActivity.this);
				Spanned htmlSpan = Html.fromHtml(exchangeContent, p, null);
				showCommunicationContent.setText(htmlSpan);

			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
				mProgressDialog.dismiss();
				Toast.makeText(CommunicationDetailActivity.this, str,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public String contentype() {
				return null;
			}
		});
	}

	private void initView() {

		commentBtn = (Button) findViewById(R.id.bt_right);
		commentBtn.setOnClickListener(this);
		commentBtn.setVisibility(View.VISIBLE);
		commentBtn.setBackgroundDrawable(null);
		commentBtn.setText(R.string.check_comment);
		commentBtn.setTextColor(getResources().getColor(R.color.white));
		commentBtn.setTextSize(16);
		showCommunicationContent = (TextView) findViewById(R.id.showID);
		exchangePublisherName = (TextView) findViewById(R.id.communication_detail_publisher_name);
		exitBtn = (Button) findViewById(R.id.bt_left);
		exitBtn.setVisibility(View.VISIBLE);
		exitBtn.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.communication_detail);
		publishComment = (Button) findViewById(R.id.publish_comment_submit);
		publishComment.setOnClickListener(this);
		// contentTitle,
		// contentTime, contentResponseNums
		contentTitle = (TextView) findViewById(R.id.content_title);
		contentTime = (TextView) findViewById(R.id.content_time);
		contentResponseNums = (TextView) findViewById(R.id.content_response_nums);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.communication_detail, menu);
		return true;
	}

	public static byte[] getBytes(InputStream is) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		is.close();
		bos.flush();
		byte[] result = bos.toByteArray();
		System.out.println(new String(result));
		return result;
	}

	public static Bitmap getImage(String address) throws Exception {
		// 通过代码 模拟器浏览器访问图片的流程
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		// 获取服务器返回回来的流
		InputStream is = conn.getInputStream();
		byte[] imagebytes = getBytes(is);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imagebytes, 0,
				imagebytes.length);
		return bitmap;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			CommunicationDetailActivity.this.finish();
			overridePendingTransition(0, R.anim.to_left_out);
			break;
		case R.id.publish_comment_submit:
			Intent i = new Intent();
			i.setClass(CommunicationDetailActivity.this,
					CommentEditActivity.class);
			System.out.println("要对发表评论ID是什么---->>" + selectExchangeID);
			i.putExtra("selectExchangeID", selectExchangeID);
			startActivity(i);
			overridePendingTransition(R.anim.from_right_into,
					R.anim.to_left_out);
			break;
		case R.id.bt_right:
			Intent intent1 = new Intent();
			intent1.setClass(CommunicationDetailActivity.this,
					CommentListActivity.class);
			intent1.putExtra("selectExchangeID", selectExchangeID);
			startActivity(intent1);
			overridePendingTransition(R.anim.from_right_into,
					R.anim.to_left_out);
			break;

		}

	}
}
