package com.xiaoguo.wasp.mobile.model;

import java.util.List;

public class MonitorGroup {
	private String name;
	private List<GreenHouse> houses;

	public MonitorGroup(String name, List<GreenHouse> houses) {
		this.name = name;
		this.houses = houses;
	}

	public int getCount() {
		if (houses != null)
			return houses.size();
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GreenHouse> getHouse() {
		return houses;
	}

	public void setHouse(List<GreenHouse> houses) {
		this.houses = houses;
	}

}
