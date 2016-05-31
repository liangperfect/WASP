package com.xiaoguo.wasp.mobile.widget;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.ui.userinfo.FieldsActivity;

public class MyAdapter extends BaseAdapter {
	Context context;
	UserSettingInfo userSettingInfo;
	List<HashMap<String, Object>> list = null;
	MyAdapter adapter;
	ListView listView;
	public MyAdapter(Context context,List<HashMap<String, Object>> list,UserSettingInfo userSettingInfo,
			MyAdapter adapter,ListView listView) {
		this.context = context;
		this.list = list;
		this.userSettingInfo = userSettingInfo;
		this.adapter = adapter;
		this.listView = listView;
	}

	@Override
	public int getCount() {
//		System.out.println("list的长度为:"+list.size());
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.field_item,null);
		TextView tv1 = (TextView) view.findViewById(R.id.tv1);
		tv1.setText(list.get(position).get("name").toString());
		JSONObject object = new JSONObject();
		try {
			object.put("tag_idList", list.get(position).get("id").toString());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MyCanceAttentionBtn btn = (MyCanceAttentionBtn) view.findViewById(R.id.cancelAttention);
		String tag = list.get(position).get("tag").toString();
		if(tag.equals("no")){
			btn.setVisibility(View.INVISIBLE);
		}else{
			btn.setUserName(userSettingInfo.getAccount());
			btn.setRequestInfo("FollowTag",object,"RemoveFollowTag",object,list,adapter,listView);
			btn.setOnClickListener(MyOnclClikcListener.instance());
			MyOnclClikcListener.instance().setContext(context);
			MyOnclClikcListener.instance().setUserset(userSettingInfo);
		}

		return view;
	}
}
