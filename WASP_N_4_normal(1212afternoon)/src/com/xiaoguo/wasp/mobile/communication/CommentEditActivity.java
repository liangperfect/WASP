package com.xiaoguo.wasp.mobile.communication;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.CommunicationDb;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;

public class CommentEditActivity extends Activity implements OnClickListener {
	private TextView title;
	private Button submit, exitbtn;
	private EditText commentEditText;
	private CommandBase commandBase;
	private int selectExchangeID;
	private Intent intent;
	private ProgressDialog mProgressDialog;
	private String commentStr;
	private CommunicationDb communicationDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_edit);
		initView();
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment_edit, menu);
		return true;
	}

	private void initView() {
		communicationDb = new CommunicationDb(CommentEditActivity.this);
		title = (TextView) findViewById(R.id.title);

		submit = (Button) findViewById(R.id.comment_publish_submit);

		submit.setOnClickListener(this);
		exitbtn = (Button) findViewById(R.id.bt_left);
		exitbtn.setVisibility(View.VISIBLE);
		exitbtn.setOnClickListener(this);
		commentEditText = (EditText) findViewById(R.id.comment_edit);
		mProgressDialog = new ProgressDialog(CommentEditActivity.this);
		mProgressDialog.setTitle("提交评论中...");
	}

	private void initData() {
		commandBase = CommandBase.instance();
		intent = getIntent();
		selectExchangeID = intent.getIntExtra("selectExchangeID", 0);
		title.setText("评论");
		/**
		 *  获取之前有的评论内容
		 */
//		commentStr = communicationDb.getCommentContent();
//		if (!commentStr.equals("")) {
//			commentEditText.setText(commentStr);
//		}

	}

	@Override
	public void onClick(View v) {
		// 获取评论内的内容

		switch (v.getId()) {
		case R.id.bt_left:
			CommentEditActivity.this.finish();
			overridePendingTransition(0, R.anim.to_left_out);
			String submitContent = commentEditText.getText().toString().trim();
			if (!submitContent.equals("")) {
				communicationDb.saveCommentContent(submitContent);

			}

			break;

		case R.id.comment_publish_submit:
			// 未将用户已经写了评论保存在SQLLite里面
			commentStr = commentEditText.getText().toString().trim();
			if (commentStr.equals("")) {
				commentEditText.setError("评论内容不能为空");

			} else {
				communicationDb.saveCommentContent(commentStr);
				commandBase.request(new TaskListener() {

					@Override
					public void updateCacheDate(
							List<HashMap<String, Object>> cacheData) {
					}

					@Override
					public void start() {
						mProgressDialog.show();
					}

					@Override
					public String requestUrl() {
						return "insertExchangeComment";
					}

					@Override
					public JSONObject requestData() {
						JSONObject data = new JSONObject();
						try {
							data.put("exchange_id", selectExchangeID);
							data.put("ec_publish_content", commentStr);
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

						System.out.println("提交评论返回的内容--->>" + msg.toString());
						Toast.makeText(CommentEditActivity.this, "评论成功",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("selectExchangeID", selectExchangeID);
						intent.setClass(CommentEditActivity.this,
								CommentListActivity.class);
						overridePendingTransition(R.anim.from_right_into,
								R.anim.to_left_out);
						CommentEditActivity.this.startActivity(intent);
					}

					@Override
					public void finish() {
						mProgressDialog.dismiss();

					}

					@Override
					public String filepath() {
						return null;
					}

					@Override
					public void failure(String str) {
						mProgressDialog.dismiss();
					}

					@Override
					public String contentype() {
						return null;
					}
				});
			}

			break;

		default:
			break;
		}
	}

}
