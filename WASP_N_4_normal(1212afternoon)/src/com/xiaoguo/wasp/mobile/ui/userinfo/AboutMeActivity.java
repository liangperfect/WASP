package com.xiaoguo.wasp.mobile.ui.userinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.ImageTools;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutMeActivity extends Activity implements OnClickListener{
	private Button backView;
	private TextView titleView;
	private ImageView bgImgView;
	private ImageView accountImg;
	private TextView userAccountView;
	private TextView edtAccountView;
	private TextView briefAccountView;
	private TextView detailAccountView;
	private TextView concernFieldsView;
	private TextView concernNumsView;
	private TextView fieldsView;
	private LinearLayout moreFieldsView;
	private AlertDialog dialog;
	UserSettingInfo userInfo=null;
	MyBroadcastReceiver receiver = null;
	List<HashMap<String, Object>> list=null;
	 
	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;
	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH = 1;
	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE = 2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_me);
		
		userInfo = new UserSettingInfo(AboutMeActivity.this);
		WASPApplication.getInstance().addActivity(this);
		
		initView();
	}

	private void initView() {
		getfields();
		backView = (Button)findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);
		
		titleView = (TextView)findViewById(R.id.title);
		titleView.setText("个人信息");
		
		bgImgView = (ImageView)findViewById(R.id.my_info_bg);
		bgImgView.setOnClickListener(this);//修改背景
		
		accountImg = (ImageView)findViewById(R.id.account_img);
		//暂时屏蔽此功能
//		accountImg.setOnClickListener(this);//修改头像
		
		userAccountView = (TextView)findViewById(R.id.txt_account);
		if(userInfo.getUserName()!=null && !userInfo.getUserName().equals("")){
			userAccountView.setText(userInfo.getUserName());
		}else{
			userAccountView.setText(userInfo.getAccount());
		}
		
		edtAccountView = (TextView)findViewById(R.id.edt_account);
		edtAccountView.setOnClickListener(this);//编辑信息
		
		briefAccountView = (TextView)findViewById(R.id.brief_account);
		
		detailAccountView = (TextView)findViewById(R.id.detail_account);
		detailAccountView.setOnClickListener(this);//详情
		
		concernFieldsView = (TextView)findViewById(R.id.concern_account);
		concernFieldsView.setOnClickListener(this);//关注领域
		
		concernNumsView = (TextView)findViewById(R.id.concern_field_num);//关注领域数目
		
		fieldsView = (TextView)findViewById(R.id.fields);
		
		moreFieldsView = (LinearLayout)findViewById(R.id.fields_more);
		moreFieldsView.setOnClickListener(this);
		
	}

	private void getfields() {
		list = new ArrayList<HashMap<String,Object>>();
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
					for(int i=0;i<array.length();i++){
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
					concernNumsView.setText("("+list.size()+")");
					String tv = "";
					for(int i=0;i<list.size();i++){
						tv = tv + "		"+list.get(i).get("name").toString();
					}
					if(list.size()==0){
						fieldsView.setText("您还没有关注任何领域");
					}else{
						fieldsView.setText(tv);
					}
				}
				// 诉它数据加载完毕;
				break;
			}
			case WHAT_DID_REFRESH: {
				break;
			}

			case WHAT_DID_MORE: {
				break;
			}
		}
	}

	};

	@SuppressLint("WorldWriteableFiles")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.my_info_bg:
			Intent i = new Intent();
			i.setClass(AboutMeActivity.this, ResetBackgroundActivity.class);
			startActivityForResult(i, 1);
			break;
		case R.id.account_img:
			dialog = new AlertDialog.Builder(this).setItems(
					new String[] { "大图","相机", "相册" },
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(0 == which){
								//查看大图
								Toast.makeText(AboutMeActivity.this, "显示大图", Toast.LENGTH_SHORT).show();
							}else if (1 == which) {
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
								startActivityForResult(intentFromGallery, 2);
							}
						}
					}).create();
			dialog.show();
			break;
		case R.id.edt_account:
			Intent i2 = new Intent();
			i2.setClass(AboutMeActivity.this, UpDataUserInfoActivity.class);
			startActivityForResult(i2, 3);
			break;
		case R.id.detail_account:
			Intent i3 = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("userName", userInfo.getAccount());
			bundle.putString("where", "user");
			i3.putExtra("bd", bundle);
			i3.setClass(AboutMeActivity.this, UserBaseInfoActivity.class);
			startActivity(i3);
			break;
		case R.id.concern_account:
			Intent i4 = new Intent();
			i4.setClass(AboutMeActivity.this, FieldsActivity.class);
			startActivityForResult(i4, 4);
			break;
		case R.id.fields_more:
			Intent i5 = new Intent();
			i5.setClass(AboutMeActivity.this, FieldsActivity.class);
			startActivityForResult(i5, 4);
			break;
		default:
			break;
		}
	}
//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(AboutMeActivity.this);
//		IntentFilter filter = new IntentFilter();
//		
//		filter.addAction(Constant.ROSTER_ADDED);
//		filter.addAction(Constant.ROSTER_DELETED);
//		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
//		filter.addAction(Constant.ROSTER_UPDATED);
//		// 好友请求
//		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
//		filter.addAction(Constant.NEW_MESSAGE_ACTION);
//		registerReceiver(receiver, filter);
//		super.onResume();
//	}	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_CANCELED){
			switch (requestCode) {
			case 1://更改背景
				String i = data.getStringExtra("choose_color");
				System.out.println("AboutMe color:"+i);
				bgImgView.setBackgroundColor(android.graphics.Color.parseColor(i));
				break;
			case 2://更改头像
				break;
			case 3://修改资料
				if(userInfo.getUserName()!=null && !userInfo.getUserName().equals("")){
					userAccountView.setText(userInfo.getUserName());
				}else{
					userAccountView.setText(userInfo.getAccount());
				}
				break;
			case 4://修改领域
				getfields();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
