package com.xiaoguo.wasp.mobile.model;

import java.util.List;

public class MenuItem {
	private String item;//���˵�
	private List<String> detail;//�Ӳ˵�

	public MenuItem(String item, List<String> detail) {
		this.item = item;
		this.detail = detail;
	}

	public int getCount() {
		if (detail != null)
			return detail.size();
		return 0;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public List<String> getSubItem() {
		return detail;
	}

	public void setSubItem(List<String> detail) {
		this.detail = detail;
	}

}
