package com.xiaoguo.wasp.mobile.widget;

import java.util.ArrayList;
import java.util.HashMap;

import com.xiaoguo.wasp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdpater extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, Object>> items = null;
	private HashMap<String, Object> map = null;
	private View childView;

	public MainAdpater(Context context, ArrayList<HashMap<String, Object>> items) {
		super();
		this.context = context;
		this.items = items;
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		arg1 = mInflater.inflate(R.layout.simple_grid_item, null);
		ImageView imageView = (ImageView) arg1.findViewById(R.id.item);
		TextView numView = (TextView) arg1.findViewById(R.id.unreadMessageNum);
		TextView textView = (TextView) arg1.findViewById(R.id.text);
		map = new HashMap<String, Object>();
		map = items.get(arg0);
		int drawableId = Integer.parseInt(map.get("img").toString());
		String name = map.get("str").toString();
		int unreadNum = Integer.parseInt(map.get("num").toString());
		if (unreadNum == 0) {
			numView.setVisibility(View.GONE);
		} else {
			numView.setText(unreadNum + "");
		}
		imageView.setImageResource(drawableId);
		textView.setText(name);
		if (arg0 == 1) {
			childView = new View(context);
			childView = arg1;

		}

		return arg1;
	}

	public View getView() {

		return childView;
	}

}
