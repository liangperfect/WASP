package com.xiaoguo.wasp.mobile.ui.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.model.GreenHouse;
import com.xiaoguo.wasp.mobile.model.MonitorGroup;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.widget.NPullToFreshContainer;
import com.xiaoguo.wasp.mobile.widget.NPullToFreshContainer.OnContainerRefreshListener;

public class ExpertRealtimeMonitorActivity extends Activity implements OnClickListener{
	private Button backView;
	private TextView titlevView;
	private NPullToFreshContainer iPulltoRefresh;
	private EditText searchInputView;
	private ImageView searchView;
	private ExpandableListView monitorList = null;
	private MonitorExpandAdapter expandAdapter = null;
	private ListView monitorSearchLisview=null;
	private searchAdapter searchAdapter;
	List<MonitorGroup> groupLists=null;
	
	UserSettingInfo userSettingInfo = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expert_monitor);
		
		userSettingInfo = new UserSettingInfo(ExpertRealtimeMonitorActivity.this);
		WASPApplication.getInstance().addActivity(this);
		
		initView();
		
	}

	private void initView() {
		backView = (Button)findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);
		
		titlevView = (TextView)findViewById(R.id.title);
		titlevView.setText("实时监控");
		
		iPulltoRefresh =(NPullToFreshContainer)findViewById(R.id.pulltofresh);
		iPulltoRefresh.setOnRefreshListener(new OnContainerRefreshListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onContainerRefresh() {
				monitorList = (ExpandableListView)findViewById(R.id.main_expand_list);
				//取消图标
				monitorList.setGroupIndicator(null);
					if(!NetWorkDetect.detect(ExpertRealtimeMonitorActivity.this)){
						Toast.makeText(ExpertRealtimeMonitorActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
					}else{
						groupLists = getMonitorLists();
						expandAdapter = new MonitorExpandAdapter(groupLists);
						monitorList.setAdapter(expandAdapter);
					}
				Date vdate = new Date();
				iPulltoRefresh.onComplete(vdate.toLocaleString());
			}

		});
		
		monitorList = (ExpandableListView) findViewById(R.id.main_expand_list);
		monitorList.setGroupIndicator(this.getResources().getDrawable(R.drawable.indocator));
		groupLists = getMonitorLists();
		expandAdapter = new MonitorExpandAdapter(groupLists);
		monitorList.setAdapter(expandAdapter);

		monitorList.setVisibility(View.VISIBLE);
		monitorSearchLisview = (ListView)findViewById(R.id.search_list_2);
		monitorSearchLisview.setVisibility(View.GONE);
		monitorSearchLisview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v,
					int arg2, long arg3) {
				
			}
		});
		
		searchInputView = (EditText)findViewById(R.id.search_input);
		searchInputView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String str = searchInputView.getText().toString();
				if (str.equals("")) {
					monitorList.setVisibility(View.VISIBLE);
					monitorSearchLisview.setVisibility(View.GONE);
				}
				
			}
		});
		searchView = (ImageView)findViewById(R.id.search_img);
		searchView.setOnClickListener(this);
	}
	private class MonitorExpandAdapter extends BaseExpandableListAdapter {

		private List<MonitorGroup> groups = null;

		public MonitorExpandAdapter(List<MonitorGroup> groups) {
			this.groups = groups;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return groups.get(groupPosition).getHouse().get(childPosition);
		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			 convertView= ExpertRealtimeMonitorActivity.this.getLayoutInflater().inflate(R.layout.monitor_group_item, null);
			TextView textView1 = (TextView)convertView.findViewById(R.id.houseName);
			TextView textView2 = (TextView)convertView.findViewById(R.id.houseOwner);
			GreenHouse house = groups.get(groupPosition).getHouse().get(childPosition);
			textView1.setText(house.getHouseName());
			textView2.setText(house.getHouseOwner());
			convertView.setOnClickListener(contacterOnClick);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groups.get(groupPosition).getCount();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition).getHouse();
		}

		@Override
		public int getGroupCount() {
			return groups.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView view = new TextView(ExpertRealtimeMonitorActivity.this);
			view.setText(groups.get(groupPosition).getName() + " ("
					+ groups.get(groupPosition).getCount() + ")");
			view.setPadding(80, 8, 8, 0);
			view.setTextColor(Color.DKGRAY);
			view.setTextSize(20);
			view.setTag(groups.get(groupPosition).getName());
			return view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}
	
	private OnClickListener contacterOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
		}
	};
	
	private List<MonitorGroup> getMonitorLists() {
		List<MonitorGroup> list = new ArrayList<MonitorGroup>();
		return list;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.search_img:
			String house = searchInputView.getText().toString();
			if(house!=null&&!house.equals("")){
				List<GreenHouse> list = new ArrayList<GreenHouse>();
				for(int j=0;j<groupLists.size();j++){
					new HashMap<String, Object>();
					MonitorGroup tempGroup = groupLists.get(j);
					List<GreenHouse> tempHouses = tempGroup.getHouse();
					GreenHouse greenHouse = null;
					for(int k=0;k<tempHouses.size();k++){
						greenHouse = tempHouses.get(k);
						String houseName = greenHouse.getHouseName();
						if(houseName.contains(house)){
							list.add(greenHouse);
						}
					}
				}
				for(int i=0;i<list.size();i++){
					String temp1 = (String) list.get(i).getHouseName();
					for(int j=i+1;j<list.size();j++){
						String temp2 = (String) list.get(j).getHouseName();
						if(temp1.equals(temp2)){
							list.remove(j);
						}
					}
				}
				System.out.println("list的长度:"+list.size());
				searchAdapter = new searchAdapter(list);
				monitorSearchLisview.setAdapter(searchAdapter);
				monitorSearchLisview.setVisibility(View.VISIBLE);
				monitorList.setVisibility(View.GONE);
				if(list.size()>0){
				}else{
					String text = "没有搜索到相关信息";
					Toast.makeText(ExpertRealtimeMonitorActivity.this, text, Toast.LENGTH_SHORT).show();
				}
			}
		default:
			break;
		}
	}
	public class searchAdapter extends BaseAdapter {
		List<GreenHouse> houseList=null;
		
		public searchAdapter(List<GreenHouse> houseList) {
			super();
			this.houseList = houseList;
		}
		@Override
		public int getCount() {
			return houseList.size();
		}
		@Override
		public Object getItem(int arg0) {
			return houseList.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			 convertView= ExpertRealtimeMonitorActivity.this.getLayoutInflater().inflate(R.layout.monitor_group_item, null);
				TextView textView1 = (TextView)convertView.findViewById(R.id.houseName);
				TextView textView2 = (TextView)convertView.findViewById(R.id.houseOwner);
				GreenHouse house = houseList.get(arg0);
				textView1.setText(house.getHouseName());
				textView2.setText(house.getHouseOwner());
				return convertView;
		}
		
	}
}
