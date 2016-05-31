package com.xiaoguo.wasp.mobile.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.widget.MainAdpater;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.SimpleAdapter;


public class GridviewUtils {
	//聊天界面更多操作
	public static GridView getMoreGridView(Context context) {
			final GridView view = new GridView(context);
			ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> map = null;
			map=new HashMap<String, Object>();
			map.put("img", R.drawable.pic_4);
			map.put("str", "图片");
			map.put("num", 0);
			items.add(map);
			map=new HashMap<String, Object>();
			map.put("img", R.drawable.camera_4);
			map.put("str", "拍照");
			map.put("num", 0);
			items.add(map);
			/*map=new HashMap<String, Object>();
			map.put("img", R.drawable.vedio);
			map.put("str", "视频通话");
			items.add(map);
			map=new HashMap<String, Object>();
			map.put("img", R.drawable.record_animate_02);
			map.put("str", "语音通话");
			items.add(map);*/
			/*map=new HashMap<String, Object>();
			map.put("img", R.drawable.folder);
			map.put("str", "文件");
			items.add(map);*/
			MainAdpater adapter = new MainAdpater(context, items);
//			SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.simple_grid_item, new String[]{"img","str"}, new int[]{R.id.item,R.id.text});
			view.setAdapter(adapter);
			view.setNumColumns(4);
			view.setBackgroundColor(Color.rgb(214, 211, 214));
			view.setHorizontalSpacing(2);
			view.setVerticalSpacing(2);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			return view;
		}
//表情对话框中的网格
	public static GridView createGridView(Context context,int[] imageIds) {
		final GridView view = new GridView(context);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		//生成107个表情的id，封装
		for(int i = 0; i < 105; i++){
			try {
				if(i<10){
					Field field = R.drawable.class.getDeclaredField("f00" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else if(i<100){
					Field field = R.drawable.class.getDeclaredField("f0" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else{
					Field field = R.drawable.class.getDeclaredField("f" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	        Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("image", imageIds[i]);
			listItems.add(listItem);
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
		view.setAdapter(simpleAdapter);
		view.setNumColumns(6);
		view.setBackgroundColor(Color.rgb(214, 211, 214));
		view.setHorizontalSpacing(1);
		view.setVerticalSpacing(1);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		view.setGravity(Gravity.CENTER);
		return view;
	}
	public static GridView createIconGridView(Context context,int[] imageIds) {
		final GridView view = new GridView(context);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		//生成14个icon的id，封装
		for(int i = 0; i < 16; i++){
			try {
			
					Field field = R.drawable.class.getDeclaredField("j_" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	        Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("image", imageIds[i]);
			listItems.add(listItem);
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
		view.setAdapter(simpleAdapter);
		view.setNumColumns(4);
		view.setBackgroundColor(Color.rgb(214, 211, 214));
		view.setHorizontalSpacing(1);
		view.setVerticalSpacing(1);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		view.setGravity(Gravity.CENTER);
		return view;
	}
}
