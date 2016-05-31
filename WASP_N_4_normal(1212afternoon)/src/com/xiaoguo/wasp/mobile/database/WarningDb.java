package com.xiaoguo.wasp.mobile.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WarningDb {

	private static final String db_name = "WarningDb.db";

	private SQLiteDatabase db;

	Context context;

	public WarningDb(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}

	/**
	 * 保存债还预警消息
	 * 
	 * @param time
	 *            接收的时间
	 * @param Content
	 *            接收到的内容
	 * @return
	 */
	public String savaWarningMessage(String time, String Content) {
		String result = null;
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS _WarningMessage"
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT ,"
					+ "warning_time TEXT,warning_message TEXT)");
			db.execSQL("INSERT INTO _WarningMessage "
					+ "(warning_time , warning_message) VALUES (?,?)",
					new Object[] { time, Content });
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}
		return result;
	}

	public List<HashMap<String, Object>> getWarningMessage() {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		db.execSQL("CREATE TABLE IF NOT EXISTS _WarningMessage"
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "warning_time TEXT,warning_message TEXT)");
		Cursor c = db.rawQuery("SELECT * FROM _WarningMessage", null);
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("time", c.getString(c.getColumnIndex("warning_time")));
				map.put("content",
						c.getString(c.getColumnIndex("warning_message")));
				list.add(map);
			}

		} else {
			list = null;
		}
		return list;
	}
}
