package com.xiaoguo.wasp.mobile.widget;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ListView;

public class MyCanceAttentionBtn extends Button {

	private String userName;
	
	private String addUrl;
	private JSONObject addObject;
	private String cancelUrl;
	private JSONObject cancelObject;
	private List<HashMap<String, Object>> list;
	private MyAdapter adapter;
	private ListView listView;
	
	public void setRequestInfo(String addUrl,JSONObject addObject,String cancelUrl,JSONObject cancelObject,
			List<HashMap<String, Object>> list,MyAdapter adapter,ListView listView){
		this.addUrl = addUrl;
		this.addObject = addObject;
		this.cancelUrl = cancelUrl;
		this.cancelObject = cancelObject;
		this.list = list;
		this.adapter = adapter;
		this.listView = listView;
	}

	public String getAddUrl() {
		return addUrl;
	}

	public void setAddUrl(String addUrl) {
		this.addUrl = addUrl;
	}

	public JSONObject getAddObject() {
		return addObject;
	}

	public void setAddObject(JSONObject addObject) {
		this.addObject = addObject;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public JSONObject getCancelObject() {
		return cancelObject;
	}

	public void setCancelObject(JSONObject cancelObject) {
		this.cancelObject = cancelObject;
	}

	public MyCanceAttentionBtn(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyCanceAttentionBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyCanceAttentionBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<HashMap<String, Object>> getList() {
		return list;
	}

	public void setList(List<HashMap<String, Object>> list) {
		this.list = list;
	}

	public MyAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(MyAdapter adapter) {
		this.adapter = adapter;
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

}
