package com.xiaoguo.wasp.mobile.ui.weatherinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.Article;
import com.xiaoguo.wasp.mobile.model.ArticleTitle;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.warning.ProductWarningActivity;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WriteDisterActivity extends Activity implements OnClickListener{
	private TextView titleView;
	private Button backView;
	private Button okView;
	
	private TextView articleNameView;
	private EditText articleTitleView;
	
	private TextView articleLabelTextview;
	private Button articleLabelView;
	private ListView articleLabelListView;
	
	MyAdapter adapter=null;
	String [] items=null;
	List<HashMap<String, Object>> list=null;
	private TextView articleContent;
	private EditText articleContentView;
	
	private CommandBase commandBase = null;
	private UserSettingInfo userInfo=null;
	
	String title = "";
	String type="";
	String contnet="";
	ProgressDialog dialog = null;
	int typeId=0;
	String label="";
	MyBroadcastReceiver receiver = null;
	String where = "";
	ProductDb productDb=null;
	int labelId=0;
	private static final int WHAT_DID_LOAD = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_article);
		
		commandBase = CommandBase.instance();
		productDb = new ProductDb(WriteDisterActivity.this);
		userInfo = new UserSettingInfo(this);
		dialog = new ProgressDialog(this);
		WASPApplication.getInstance().addActivity(this);
		
		list = new ArrayList<HashMap<String,Object>>();
		
		
		Bundle bundle = getIntent().getBundleExtra("bd");
		where = bundle.getString("where");
		
		initView();
	}
	private void initView() {
		titleView = (TextView)findViewById(R.id.title);
		if(where.equals("disaster")){
			titleView.setText("发布灾害预警");
		}
		if(where.equals("productWarning")){
			titleView.setText("发布生产提示");
		}
		
		backView = (Button)findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);
		
		okView = (Button)findViewById(R.id.bt_right);
		okView.setVisibility(View.VISIBLE);
		okView.setOnClickListener(this);
		
		articleNameView = (TextView)findViewById(R.id.article_name);
		if(where.equals("disaster")){
			articleNameView.setText("灾害预警标题：");
		}
		if(where.equals("productWarning")){
			articleNameView.setText("生产提示标题：");
		}
		
		articleTitleView = (EditText)findViewById(R.id.edt_article_name);
		
		articleLabelTextview = (TextView)findViewById(R.id.type);
		articleLabelView = (Button)findViewById(R.id.edt_type);
		if(where.equals("disaster")){
			articleLabelTextview.setVisibility(View.GONE);
			articleLabelView.setVisibility(View.GONE);
		}
		if(where.equals("productWarning")){
			articleLabelView.setText("点击选中生产提示标签");
			articleLabelView.setVisibility(View.VISIBLE);
		}
		articleLabelView.setOnClickListener(this);
		
		articleContent = (TextView)findViewById(R.id.article_content);
		if(where.equals("disaster")){
			articleContent.setText("灾害预警内容：");
		}
		if(where.equals("productWarning")){
			articleContent.setText("生产提示内容：");
		}
		articleContentView = (EditText)findViewById(R.id.edt_content);
		articleLabelListView = (ListView)findViewById(R.id.write_article_list);
		
		
		if(where.equals("disaster")){
			typeId = 3;
		}
		if(where.equals("productWarning")){
			typeId = 4;
		}
		
		list = productDb.getTags();
		getLabel();
		System.out.println("标签个数："+list.size());
		for(int j=0;j<list.size();j++){
			System.out.println(list.get(j));
		}
		adapter = new MyAdapter(list, WriteDisterActivity.this);
		articleLabelListView.setAdapter(adapter);
		articleLabelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HashMap<String, Object> map = list.get(arg2);
				label = map.get("name").toString();
				labelId =  Integer.parseInt(map.get("id").toString());
				articleLabelView.setText(label);
				articleLabelListView.setVisibility(View.GONE);
				articleContentView.setVisibility(View.VISIBLE);
				articleContent.setVisibility(View.VISIBLE);
			}
		});
	}

	private List<HashMap<String, Object>> getLabel() {
		final List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		CommandBase.instance().request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
				
			}
			@Override
			public void start() {
				
			}
			@Override
			public String requestUrl() {
				return "TagSelList";
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
				System.out.println("标签msg="+msg);
				JSONObject object;
				HashMap<String, Object> map = null;
				try {
					object = msg.getJSONObject("data");
					JSONArray array = object.getJSONArray("list");
					for(int i=0;i<array.length();i++){
						map = new HashMap<String, Object>();
						object = array.getJSONObject(i);
						String labelName = object.getString("tag_name");
						int labelId = object.getInt("tag_id");
						map.put("name", labelName);
						map.put("id", labelId);
						list.add(map);
					}
					productDb.updateTags(list);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void finish() {
				Message message = mUIHandler.obtainMessage(WHAT_DID_LOAD);
				message.obj = list;
				message.sendToTarget();
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
		return list;
	}

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD: {
				@SuppressWarnings("unchecked")
				List<HashMap<String, Object>> list1 = (List<HashMap<String,Object>>)msg.obj;
				if(list1!=null && list1.size()>0){
					list = new ArrayList<HashMap<String,Object>>();
					list.addAll(list1);
				}
//				adapter.notifyDataSetChanged();
				adapter = new MyAdapter(list, WriteDisterActivity.this);
				articleLabelListView.setAdapter(adapter);
				articleLabelListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						HashMap<String, Object> map = list.get(arg2);
						label = map.get("name").toString();
						labelId =  Integer.parseInt(map.get("id").toString());
						articleLabelView.setText(label);
						articleLabelListView.setVisibility(View.GONE);
						articleContentView.setVisibility(View.VISIBLE);
						articleContent.setVisibility(View.VISIBLE);
					}
				});
				break;
			}
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.edt_type:
			if(list.size()<=0){
				Toast.makeText(WriteDisterActivity.this, "获取标签失败", Toast.LENGTH_SHORT).show();
				articleLabelListView.setVisibility(View.GONE);
				articleContentView.setVisibility(View.VISIBLE);
				articleContent.setVisibility(View.VISIBLE);
			}else{
				articleLabelListView.setVisibility(View.VISIBLE);
				articleContentView.setVisibility(View.GONE);
				articleContent.setVisibility(View.GONE);
			}
			break;
		case R.id.bt_right:
			title = articleTitleView.getText().toString();
			contnet = articleContentView.getText().toString();
			if(title==null || title.equals("")){
				Toast.makeText(WriteDisterActivity.this, "标题不能空！", Toast.LENGTH_SHORT).show();
			}else if(where.equals("productWarning") && (label.equals("") || label==null)){
				Toast.makeText(WriteDisterActivity.this, "标签不能空！", Toast.LENGTH_SHORT).show();
			}else if(contnet == null || contnet.equals("")){
				Toast.makeText(WriteDisterActivity.this, "内容不能空！", Toast.LENGTH_SHORT).show();
			}else{
				final ArticleTitle articleTitle = new ArticleTitle();
				final Article article = new Article();
				commandBase.readCache(new TaskListener() {
					@Override
					public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
						
					}
					@Override
					public void start() {
						dialog.setMessage("正在提交数据，请稍后...");
						dialog.show();
					}
					@Override
					public String requestUrl() {
						if(where.equals("disaster")){
							return "WaringAdd";
						}else if(where.equals("productWarning")){
							return "WaringTipAdd";
						}else{
							return "articleinsert";
						}
					}
					@Override
					public JSONObject requestData() {
						JSONObject object = new JSONObject();
						try {
							object.put("article_title", title);
							object.put("article_content", contnet);
							if(where.equals("productWarning")){
								object.put("article_tag_id", labelId);
							}
							articleTitle.setArticle_title(title);
							articleTitle.setArticle_publish_name(userInfo.getAccount());
							if(!where.equals("disaster")){
								articleTitle.setArticle_type(typeId);
							}
							articleTitle.setArticle_content(contnet);
							articleTitle.setArticle_publish_date(System.currentTimeMillis()+"");
							
							article.setArticle_title(title);
							article.setArticle_content(contnet);
							if(!where.equals("disaster")){
								article.setArticle_type(typeId);
							}
							article.setArticle_user_name(userInfo.getAccount());
							article.setArticle_publish_date(System.currentTimeMillis()+"");
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
						System.out.println("222");
						Intent resultIntent = new Intent();
						Bundle bd = new Bundle();
						bd.putInt("type", typeId);
						resultIntent.putExtra("reultData", bd);
						if(where.equals("disaster")){
							resultIntent.setClass(WriteDisterActivity.this, DisasterActivity.class);
						}
						if(where.equals("productWarning")){
							resultIntent.setClass(WriteDisterActivity.this, ProductWarningActivity.class);
						}
						WriteDisterActivity.this.setResult(RESULT_OK,resultIntent);
						WriteDisterActivity.this.finish();
						//让利利返回id，再存数据库
						productDb.saveArticle(article, userInfo.getAccount());
						productDb.saveArticleName(articleTitle);
					}
					@Override
					public void finish() {
						dialog.dismiss();
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
			break;
		default:
			break;
		}
	}
	private class MyAdapter extends BaseAdapter{
		private List<HashMap<String, Object>>items=null;
		private Context context;
		HashMap<String, Object> map=null;
		public MyAdapter(List<HashMap<String, Object>> items, Context context) {
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
			map = new HashMap<String, Object>();
			map = items.get(arg0);
			System.out.println("map="+map);
			String labelName = map.get("name").toString();
			 LayoutInflater _LayoutInflater=LayoutInflater.from(context);  
			 v =_LayoutInflater.inflate(R.layout.my_simple_spinner, null);  
			 if(v!=null){
				 TextView label = (TextView) v.findViewById(R.id.simple_spinner_tx); 
				 label.setText(labelName);
			 }
			 return v;
		}
		
	}
	
//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(WriteDisterActivity.this);
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
}
