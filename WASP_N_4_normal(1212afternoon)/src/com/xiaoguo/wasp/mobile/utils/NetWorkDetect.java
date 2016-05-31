package com.xiaoguo.wasp.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkDetect {
	static ConnectivityManager manager;
	static NetworkInfo networkinfo;

	private static void init(Activity act) {

		manager = (ConnectivityManager) act.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		networkinfo = manager.getActiveNetworkInfo();
	}

	public static boolean detect(Activity act) {
		init(act);
		if (manager == null) {
			return false;
		}

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}

	public static String get(Activity act) {
		init(act);

		if (manager == null) {
			return "";
		}

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return "";
		}
		int i = networkinfo.getType();
		return i == 0 ? "GPRS" : "WIFT";
	}

}
