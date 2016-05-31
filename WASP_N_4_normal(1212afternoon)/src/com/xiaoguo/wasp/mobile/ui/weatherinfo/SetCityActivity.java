package com.xiaoguo.wasp.mobile.ui.weatherinfo;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.model.WeatherInfo;
import com.xiaoguo.wasp.mobile.ui.weatherinfo.SetCityActivity.MyAdapter1.MyAdapter2;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SetCityActivity extends Activity implements OnClickListener{
	private TextView titleView;
	private Button backButton;
	private Button commitButton;
	
	TextView textView ;
	Spinner sp1;
	Spinner sp2;
	Button bt_1;
	Button bt_2;
	ListView listView_1;
	ListView listView_2;
	TableRow tableRow_1;
	TableRow tableRow_2;
	
	MyAdapter1 adapter1;
	MyAdapter2 adapter2;
	CharSequence[] tempItems1=null;
	CharSequence[] tempItems2=null;
	private Button btOk;
	
	WeatherInfo weatherInfo=null;
	UserSettingInfo userSettingInfo=null;
	private static String city="";
	MyBroadcastReceiver receiver = null;
	String currentProvence="";
	String currentCity="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_city);
		
		userSettingInfo = new UserSettingInfo(this);
		weatherInfo = new WeatherInfo(this, userSettingInfo.getAccount());
		currentProvence = weatherInfo.getCurrentProvience();
		currentCity = weatherInfo.getCurrentCity();
		
		WASPApplication.getInstance().addActivity(this);
		
		init();
	}
	private void init() {
		titleView  = (TextView)findViewById(R.id.title);
		titleView.setText("城市设置");
		
		backButton = (Button)findViewById(R.id.bt_left);
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);
		
		commitButton = (Button)findViewById(R.id.bt_right);
		commitButton.setVisibility(View.VISIBLE);
		commitButton.setOnClickListener(this);
		
		textView = (TextView)findViewById(R.id.set_tx);
		textView.setVisibility(View.GONE);
		bt_1 = (Button)findViewById(R.id.set_sp1);
		bt_1.setText(currentProvence);
		bt_2 = (Button)findViewById(R.id.set_sp2);
		bt_2.setText(currentCity);
		tableRow_1 = (TableRow)findViewById(R.id.table_1);
		tableRow_2 = (TableRow)findViewById(R.id.table_2);
		listView_1 = (ListView)findViewById(R.id.list_1);
		listView_2 = (ListView)findViewById(R.id.list_2);
		btOk = (Button)findViewById(R.id.set_city_ok);
		btOk.setOnClickListener(this);
		
		tempItems1 = this.getResources().getStringArray(R.array.province);
		adapter1 = new MyAdapter1(tempItems1, SetCityActivity.this);
		
		bt_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(tableRow_1.getVisibility()!=0){
					tableRow_1.setVisibility(View.VISIBLE);
					listView_1.setAdapter(adapter1);
					listView_1.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							 TextView label = (TextView) arg1.findViewById(R.id.simple_spinner_tx); 
							 String flag = label.getText().toString();
							if(flag.equals("北京")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.beijing);
							 }else if(flag.equals("天津")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.tianjjing);
							 }else if(flag.equals("重庆")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.chongqing);
							 }else if(flag.equals("上海")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.shanghai);
							 }else if(flag.equals("河北")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.hebei);
							 }else if(flag.equals("山西")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.shangxi);
							 }else if(flag.equals("内蒙古")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.neimenggu);
							 }else if(flag.equals("辽宁")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.liaoning);
							 }else if(flag.equals("吉林")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.jining);
							 }else if(flag.equals("黑龙江")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.heilongjiang);
							 }else if(flag.equals("江苏")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.jiangsu);
							 }else if(flag.equals("浙江")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.zhejiang);
							 }else if(flag.equals("安徽")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.anhui);
							 }else if(flag.equals("福建")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.fujian);
							 }else if(flag.equals("江西")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.jiangxi);
							 }else if(flag.equals("山东")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.shandong);
							 }else if(flag.equals("河南")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.henan);
							 }else if(flag.equals("湖北")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.hubei);
							 }else if(flag.equals("湖南")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.hunan);
							 }else if(flag.equals("广东")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.guangdong);
							 }else if(flag.equals("广西")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.guangxi);
							 }else if(flag.equals("海南")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.hainan);
							 }else if(flag.equals("四川")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.sichuan);
							 }else if(flag.equals("贵州")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.guizhou);
							 }else if(flag.equals("云南")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.yunnan);
							 }else if(flag.equals("西藏")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.xizang);
							 }else if(flag.equals("陕西")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.shanxi);
							 }else if(flag.equals("甘肃")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.ganshu);
							 }else if(flag.equals("青海")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.qinghai);
							 }else if(flag.equals("宁夏")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.ningxia);
							 }else if(flag.equals("新疆")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.xinjiang);
							 }else if(flag.equals("香港")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.xianggang);
							 }else if(flag.equals("澳门")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.aomen);
							 }else if(flag.equals("台湾")){
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.taiwan);
							 }else{
								 tempItems2 = SetCityActivity.this.getResources().getStringArray(R.array.default_city);
							 }
							bt_1.setText(flag);
							tableRow_1.setVisibility(View.GONE);
							adapter2 = new MyAdapter2(tempItems2, SetCityActivity.this);
							bt_2.setText(tempItems2[0]);
							city = tempItems2[0].toString();
	//						sp2.setAdapter(adapter2);
							bt_2.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if(tableRow_2.getVisibility() != 0){
										tableRow_2.setVisibility(View.VISIBLE);
										listView_2.setAdapter(adapter2);
									}else{
										tableRow_2.setVisibility(View.VISIBLE);
									}
								}
							});
							listView_2.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									city = tempItems2[arg2].toString();
									System.out.println("city="+city);
									bt_2.setText(city);
									tableRow_2.setVisibility(View.GONE);
								}
							});
						}
					});
				}else{
					tableRow_1.setVisibility(View.GONE);
				}
			}
		});
	}
	public static class MyAdapter1 extends BaseAdapter{
		private Context context;
		private CharSequence[] items1;
		public MyAdapter1(CharSequence[] items1,Context context) {
			super();
			this.items1 = items1;
			this.context = context;
		}
		@Override
		public int getCount() {
			return items1.length;
		}
		@Override
		public Object getItem(int arg0) {
			return items1[arg0];
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			 LayoutInflater _LayoutInflater=LayoutInflater.from(context);  
			 v =_LayoutInflater.inflate(R.layout.my_simple_spinner, null);  
			 if(v!=null){
				 TextView label = (TextView) v.findViewById(R.id.simple_spinner_tx); 
				 label.setText(items1[arg0]);
				 String flag = items1[arg0].toString();
				 System.out.println("flag="+flag);
			 }
			 return v;
		}
		
	public static class MyAdapter2 extends BaseAdapter{
		private Context context;
		private CharSequence[] items1;
		public MyAdapter2(CharSequence[] items1,Context context) {
			super();
			this.items1 = items1;
			this.context = context;
		}
		@Override
		public int getCount() {
			return items1.length;
		}
		@Override
		public Object getItem(int position) {
			return items1[position];
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater _LayoutInflater=LayoutInflater.from(context);  
			 convertView =_LayoutInflater.inflate(R.layout.my_simple_spinner, null); 
			 if(convertView!=null){
				 TextView label = (TextView) convertView.findViewById(R.id.simple_spinner_tx); 
				 label.setText(items1[position]);
			 }
			 return convertView;
		}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_right:
//			weatherInfo.setCurrentProvience(bt_1.getText().toString());
//			weatherInfo.setCurrentCity(bt_2.getText().toString());
			if(city.equals("-城市-") || city.equals("")){
				Toast.makeText(SetCityActivity.this, "您没有更改城市！", Toast.LENGTH_SHORT).show();
			}else{
				 textView.setText("您选择的城市是："+city);
				 Intent i2 = new Intent();
				 Bundle bundle = new Bundle();
				 bundle.putString("city", city);
				 bundle.putString("province", bt_1.getText().toString());
				 i2.putExtra("bd", bundle);
				 i2.setClass(SetCityActivity.this, WeatherActivity.class);
				 this.setResult(RESULT_OK, i2);
				 this.finish();
			}
			break;
		case R.id.set_city_ok:
			 weatherInfo.setCurrentCity(city);
			 textView.setText("您选择的城市是："+city);
			 Intent i1 = new Intent();
			 i1.setClass(SetCityActivity.this, WeatherInfoActivity.class);
			 startActivity(i1);
			 this.finish();
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
//		receiver = new MyBroadcastReceiver(SetCityActivity.this);
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
