package com.xiaoguo.wasp.mobile.database;

import com.xiaoguo.wasp.mobile.model.PushInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class pushDb {

	private static final String db_name = "pushDb.db";

	private SQLiteDatabase db;

	Context context;

	public pushDb(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}

	// 保存推送的user id和channel id
	public String savePushId(String userId, String channelId) {
		String result = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _pushId"
					+ "(push_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "push_user_id TEXT,push_channel_id TEXT)");
			db.execSQL("INSERT INTO _pushId"
					+ "(push_user_id , push_channel_id) VALUES(?,?)",
					new Object[] { userId, channelId });
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";

		}
		return result;
	}

	// 获取本机的user id 和channel id
	public PushInfo getPushInfo() {
		PushInfo info = new PushInfo();
		db.execSQL("CREATE table IF NOT EXISTS _pushId"
				+ "(push_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "push_user_id TEXT,push_channel_id TEXT)");

		Cursor c = db.rawQuery("SELECT * FROM _pushId", null);
		if (c.getCount() == 0) {
			info = null;
		} else {
			while (c.moveToNext()) {
				info.setUserID(c.getString(c.getColumnIndex("push_user_id")));
				info.setChannelID(c.getString(c
						.getColumnIndexOrThrow("push_channel_id")));
			}

		}
		c.close();

		return info;
	}

}
