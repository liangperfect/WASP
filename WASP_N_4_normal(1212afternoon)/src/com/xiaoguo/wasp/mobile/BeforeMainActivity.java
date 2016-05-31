package com.xiaoguo.wasp.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.Expert;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.ConnectionUtils;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.xmpphelper.ContacterManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ���ܣ����ε�¼����ȡũ����ר���б��������Ӻ���
 * 
 * */
public class BeforeMainActivity extends Activity implements OnClickListener {
	private TextView titTextView;

	private TextView titleView;
	private ProgressBar progressBar;
	private TextView noticeView;
	private Button commitView;

	private UserSettingInfo userSettingInfo;
	String userType = "";
	private ProductDb productDb;
	CommandBase commandBase = null;

	/** Handler What����������� **/
	private static final int WHAT_DID_LOAD_DATA = 0;
	// AlertDialog.Builder builder = null;
	private Dialog builder;
	View v = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);

		userSettingInfo = new UserSettingInfo(this);
		productDb = new ProductDb(this);
		commandBase = CommandBase.instance();
		userType = userSettingInfo.getType();

		LayoutInflater mInflater = LayoutInflater.from(this);
		v = mInflater.inflate(R.layout.beforemain, null);
		initView(v);
	}

	private void initView(View v) {
		titTextView = (TextView) findViewById(R.id.title);
		titTextView.setText("�������");

		// titleView = (TextView)v.findViewById(R.id.before_main_title);

		progressBar = (ProgressBar) v.findViewById(R.id.load_info_progress);
		progressBar.setVisibility(View.GONE);

		noticeView = (TextView) v.findViewById(R.id.notice_message);
		noticeView.setText("���ε�¼����Ҫ��������");

		commitView = (Button) v.findViewById(R.id.commit_button);
		commitView.setText("����");
		commitView.setOnClickListener(this);

		builder = new Dialog(BeforeMainActivity.this);
		builder.setTitle("��ʾ");
		builder.setContentView(v);
		builder.show();

		commitView.performClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commit_button:
			if (commitView.getText().toString().equals("����")) {
				noticeView.setText("���ڼ���,���Ժ�...");
				progressBar.setVisibility(View.VISIBLE);
				commitView.setVisibility(View.INVISIBLE);
				initData();
			} else {
				Intent i = new Intent();
				i.setClass(BeforeMainActivity.this, MainActivityTab.class);
				startActivity(i);
				this.finish();
			}
			break;
		default:
			break;
		}
	}

	private void initData() {
		final List<Expert> list = new ArrayList<Expert>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
			}

			@Override
			public String requestUrl() {
				return "userList";
			}

			@Override
			public JSONObject requestData() {
				System.out.println("�б�");
				JSONObject object = new JSONObject();
				try {
					if (userType.equals("user_admin")) {
						object.put("role_id", 1);// ר���б�
					} else {
						object.put("role_id", 3);// ũ���б�
					}
				} catch (JSONException e) {
					object = null;
					e.printStackTrace();
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
				System.out.println("���μ�����ɺ󷵻ص�������--->>" + msg.toString());
				Expert expert = null;
				try {
					JSONObject object = msg.getJSONObject("data");
					JSONArray array = object.getJSONArray("userlist");
					HashMap<String, Object> map = null;
					Friends friends = null;
					for (int i = 0; i < array.length(); i++) {
						object = array.getJSONObject(i);
						String userId = object.getString("user_name");
						String userRemark = object.getString("user_remark");
						String userName = object.getString("user_display_name");
						// String userImg = object.getString("user_img");
						String host = CommandBase.instance().getHost();
						friends = new Friends();
						if (userName.equals("")) {
							userName = userId;
						}
						expert = new Expert(userId, userName, userRemark, "");
						String jid = CommandBase.instance().getHost();
						jid = jid.substring(0, jid.indexOf(":"));
						jid = expert.getExpertAccount() + "@" + jid;

						if (expert.getExpertName() == null) {
							friends.setName(expert.getExpertAccount());
						} else {
							friends.setName(expert.getExpertName());
						}
						friends.setJID(jid);
						Roster roster = ConnectionUtils.getConnection(
								BeforeMainActivity.this).getRoster();
						Presence presence = roster.getPresence(jid);
						friends.setFrom(presence.getFrom());
						String state = "����";
						org.jivesoftware.smack.packet.Presence.Mode usMode = presence
								.getMode();
						if (usMode == org.jivesoftware.smack.packet.Presence.Mode.dnd) {
							state = "æµ";
						} else if (usMode == org.jivesoftware.smack.packet.Presence.Mode.away
								|| usMode == org.jivesoftware.smack.packet.Presence.Mode.xa) {
							state = "�뿪";
						} else if (presence.isAvailable()) {
							state = "����";
						} else {
							state = "����";
						}
						friends.setStatus(state);
						friends.setAvailable(presence.isAvailable());
						if (userType.equals("user_admin")
								&& !productDb.isExpertSaved(expert,
										userSettingInfo.getAccount())) {
							try {
								createSubscriber(jid, expert.getExpertName(),
										new String[] { "ר��" });
								ContacterManager.contacters.put(jid, friends);
								productDb.saveExpert(expert,
										userSettingInfo.getAccount());
								list.add(expert);
							} catch (XMPPException e) {
								e.printStackTrace();
								Toast.makeText(BeforeMainActivity.this,
										"���ר�ҳ�����", Toast.LENGTH_SHORT).show();
							}
						}
						if (!userType.equals("user_admin")
								&& !productDb.isFarmerSaved(expert,
										userSettingInfo.getAccount())) {
							try {
								createSubscriber(jid, expert.getExpertName(),
										new String[] { "ũ��" });
								ContacterManager.contacters.put(jid, friends);
								productDb.saveFarmer(expert,
										userSettingInfo.getAccount());
								list.add(expert);
							} catch (XMPPException e) {
								Toast.makeText(BeforeMainActivity.this,
										"���ũ��������", Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							}
						}
					}
					Message message = null;
					message = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					message.obj = list;
					message.sendToTarget();
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("ר���б�����쳣");
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

	private Handler mUIHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				progressBar.setVisibility(View.GONE);
				commitView.setVisibility(View.VISIBLE);
				noticeView.setText("�������");
				commitView.setText("ȷ��");
				commitView.performClick();
				break;
			}
			}

		}
	};

	protected void createSubscriber(String userJid, String nickname,
			String[] groups) throws XMPPException {

		ConnectionUtils.getConnection(BeforeMainActivity.this).getRoster()
				.createEntry(userJid, nickname, groups);
	}
}
