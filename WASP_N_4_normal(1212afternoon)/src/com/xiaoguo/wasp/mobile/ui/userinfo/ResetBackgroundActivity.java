package com.xiaoguo.wasp.mobile.ui.userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.xmpphelper.Constant;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ResetBackgroundActivity extends Activity implements OnClickListener{
	private Button backView;
	private TextView titleView;
	private GridView gridView;
	private ArrayList<HashMap<String, Object>> items =null;
	HashMap<String, Object> map = null;
	MyGridViewAdapter adapter=null;
	MyBroadcastReceiver receiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.background);
		WASPApplication.getInstance().addActivity(this);
		
		init();
	}
	private void init() {
		backView = (Button)findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);
		
		titleView = (TextView)findViewById(R.id.title);
		titleView.setText("���ñ���");
		
		gridView = (GridView)findViewById(R.id.background_color);
		items = new ArrayList<HashMap<String,Object>>();
		
		map = new HashMap<String, Object>();
		map.put("color", "#ffffff");//������
		map.put("name", "��ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#ffffe0");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#ffea33");//������
		map.put("name", "��ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#ffe4e1");//������
		map.put("name", "ǳõ��ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#ffc0c5");//������
		map.put("name", "�ۺ�ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#e31700");//������
		map.put("name", "��ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#f0ffff");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#e0ffff");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#40e0d0");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#bdfcc9");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#7cfc00");//������
		map.put("name", "�ݵ���");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#00ff7f");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#a39480");//������
		map.put("name", "��ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#d2b48c");//������
		map.put("name", "�غ�ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#deb8b1");//������
		map.put("name", "ʵľɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#dda0dd");//������
		map.put("name", "÷��ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#da70d6");//������
		map.put("name", "����ɫ");
		items.add(map);
		
		map = new HashMap<String, Object>();
		map.put("color", "#a020f0");//������
		map.put("name", "��ɫ");
		items.add(map);
		
		adapter = new MyGridViewAdapter(items, ResetBackgroundActivity.this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//��������ɫ���沢�������½���
				HashMap<String, Object> map = items.get(arg2);
				String i = map.get("color").toString();
				System.out.println("����ȥ��ֵ�ǣ�"+i);
				Intent intent = new Intent(ResetBackgroundActivity.this, AboutMeActivity.class);
				intent.putExtra("choose_color", i);
				ResetBackgroundActivity.this.setResult(RESULT_OK, intent);
				ResetBackgroundActivity.this.finish();
			}
		});
	}
	class MyGridViewAdapter extends BaseAdapter{
		private List<HashMap<String, Object>> list=null;
		private Context context=null;
		public MyGridViewAdapter(List<HashMap<String, Object>> list,
				Context context) {
			super();
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = list.get(arg0);
			System.out.println("map="+map);
			LayoutInflater mInflater = LayoutInflater.from(context);  
			arg1 = mInflater.inflate(R.layout.background_item, null);  
			View view = (View)arg1.findViewById(R.id.color_view);
			TextView textView = (TextView)arg1.findViewById(R.id.color_name);
			
			textView.setText(map.get("name").toString());
//			int i = Integer.parseInt(map.get("color").toString());
			String i = map.get("color").toString();
			System.out.println("��ɫ��"+i);
			view.setBackgroundColor(android.graphics.Color.parseColor(i));
			return arg1;
		}
		
	}
//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(ResetBackgroundActivity.this);
//		IntentFilter filter = new IntentFilter();
//		
//		filter.addAction(Constant.ROSTER_ADDED);
//		filter.addAction(Constant.ROSTER_DELETED);
//		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
//		filter.addAction(Constant.ROSTER_UPDATED);
//		// ��������
//		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
//		filter.addAction(Constant.NEW_MESSAGE_ACTION);
//		registerReceiver(receiver, filter);
//		super.onResume();
//	}	
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

}
