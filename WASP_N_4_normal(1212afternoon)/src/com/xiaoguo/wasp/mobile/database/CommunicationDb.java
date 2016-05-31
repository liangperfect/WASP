package com.xiaoguo.wasp.mobile.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommunicationDb {
	private static final String db_name = "communicationDb.db";
	private SQLiteDatabase db;
	Context context;

	public CommunicationDb(Context context) {
		this.context = context;
		// ���ݿ���г�ʼ��
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}

	/**
	 * ������һ�����۵�����
	 * 
	 * @param content
	 * @return
	 */
	public boolean saveCommentContent(String content) {

		db.execSQL("CREATE TABLE IF NOT EXISTS _commentcontent"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," + " content TEXT)");
		if (content.equals("")) {
			return false;
		} else {
			// ��������
			db.execSQL("INSERT INTO _commentcontent(content) VALUES(?)",
					new Object[] { content });

			return true;

		}

	}

	/**
	 * ��ձ༭���۵�����
	 * 
	 * @return
	 */
	public boolean clearCommentContent() {
		db.execSQL("CREATE TABLE IF NOT EXISTS _commentcontent"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," + " content TEXT)");

		db.execSQL("DELETE FORM _commentcontent");

		return true;
	}

	/**
	 * ���µ�ǰ��������
	 * 
	 * @param content
	 * @return
	 */
	public boolean updateCommentContent(String content) {
		db.execSQL("CREATE TABLE IF NOT EXISTS _commentcontent"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," + " content TEXT)");

		if (content.equals("")) {
			return false;
		} else {
			db.execSQL("UPDATE _commentcontent SET _id = " + 0
					+ " WHERE content = '" + content + "'");
			return true;
		}
	}

	public String getCommentContent() {
		String result = "";
		db.execSQL("CREATE TABLE IF NOT EXISTS _commentcontent"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," + " content TEXT)");

		Cursor c = db.rawQuery("SELECT content FROM _commentcontent", null);
		while (c.moveToNext()) {
			result = c.getString(c.getColumnIndex("content"));
		}
		
		return result;
		

	}

}
