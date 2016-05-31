package com.xiaoguo.wasp.mobile.database;

import java.util.List;

import com.xiaoguo.wasp.mobile.model.UploadItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApplicationDb {
	private static final String db_name = "data.db";
	private SQLiteDatabase db;
	Context context;
	
	public ApplicationDb(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
		
		if (!db.isOpen())
		{
		}

		db.execSQL("CREATE table IF NOT EXISTS tbl_uploadqueue (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"url TEXT, type INTEGER, content TEXT,filepath TEXT)");
		
	}
	
	public boolean isEmpty() 
	{
		Cursor c = db.rawQuery("SELECT COUNT(_id) FROM tbl_uploadqueue", null);
		
		return c.getCount() > 0 ? false : true;
	}

	@SuppressWarnings("null")
	public List<UploadItem> getUploadQueue() 
	{		
		List <UploadItem> uploadMap = null;
		Cursor c = db.rawQuery("SELECT * FROM tbl_uploadqueue LIMIT 0, 3 ORDER BY _id ASC", null);
		while (c.moveToNext()) 
		{
			uploadMap.add(new UploadItem(c.getInt(c.getColumnIndex("_id")),
					c.getString(c.getColumnIndex("url")),
					c.getString(c.getColumnIndex("content")),
					c.getInt(c.getColumnIndex("type")),
					c.getString(c.getColumnIndex("filepath"))));
		}
		
		return uploadMap;
	}
	
	public void removeUploadItem(int id)
	{
		db.execSQL("DELETE FROM tbl_uploadqueue WHERE _id="+id);
	}
	
	public void addUploadItem(UploadItem item)
	{
		db.execSQL("INSERT INTO tbl_uploadqueue(url, type, content, filepath) VALUES('"
				+ item.geturl() + "', " + item.gettype() + ", '" + item.getcontent() 
				+ "', '" + item.getfilepath() + "')");	
		
	}
	
	

}



