package com.xiaoguo.wasp.mobile.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.raw;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.Keyboard.Row;

public class areaDb {

	private static final String db_name = "areaDb.db";

	private SQLiteDatabase db;

	Context context;

	public areaDb(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}

	// 获取tag

	// 获取town
	public HashMap<String, Object> obtianTag() {
		HashMap<String, Object> mapTag = new HashMap<String, Object>();
		List<String> TagName = new ArrayList<String>();
		List<Integer> TagId = new ArrayList<Integer>();
		db.execSQL("CREATE table IF NOT EXISTS _tag"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "tag_id INTEGER ," + "tag_name INTEGER)");
		Cursor c = db.rawQuery("SELECT * FROM _tag", null);
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				TagName.add(c.getString(c.getColumnIndex("tag_name")));
				TagId.add(c.getInt(c.getColumnIndex("tag_id")));
			}

			mapTag.put("tagName", TagName);
			mapTag.put("tagId", TagId);

		} else {
			mapTag = null;
		}
		return mapTag;
		
	}

	// 获取town
	public HashMap<String, Object> obtianTown() {
		HashMap<String, Object> mapTown = new HashMap<String, Object>();
		List<String> TownName = new ArrayList<String>();
		List<Integer> TownId = new ArrayList<Integer>();
		db.execSQL("CREATE table IF NOT EXISTS _town"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ " town_id INTEGER , " + "town_name TEXT)");
		Cursor c = db.rawQuery("SELECT * FROM _town", null);
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				TownName.add(c.getString(c.getColumnIndex("town_name")));
				TownId.add(c.getInt(c.getColumnIndex("town_id")));
			}

			mapTown.put("townName", TownName);
			mapTown.put("townId", TownId);

		} else {

			mapTown = null;
		}
		return mapTown;

	}

	// 获取village
	public HashMap<String, Object> obtianVillage() {
		HashMap<String, Object> mapVillage = new HashMap<String, Object>();
		List<String> villageName = new ArrayList<String>();
		List<Integer> villageId = new ArrayList<Integer>();
		db.execSQL("CREATE table IF NOT EXISTS _village(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "village_id INTEGER ," + "village_name TEXT )");
		Cursor c = db.rawQuery("SELECT * FROM _village", null);
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				villageName.add(c.getString(c.getColumnIndex("village_name")));
				villageId.add(c.getInt(c.getColumnIndex("village_id")));
			}

			mapVillage.put("villageName", villageName);
			mapVillage.put("villageId", villageId);

		} else {

			mapVillage = null;
		}
		return mapVillage;

	}

	// 保存村的ID和村名
	public boolean saveVillage(int villageId, String villageName) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _village(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "village_id INTEGER ," + "village_name TEXT )");
		if (judgeVillage(villageId)) {

			result = false;

		} else {
			db.execSQL(
					"INSERT INTO _village(village_id , village_name) VALUES(?,?)",
					new Object[] { villageId, villageName });
			result = true;
		}

		return result;

	}

	// 判断村的ID是否存在
	public boolean judgeVillage(int villageId) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _village(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "village_id INTEGER ," + "village_name TEXT )");
		Cursor c = db.rawQuery("SELECT * FROM _village WHERE village_id = "
				+ villageId, null);
		if (c.getCount() > 0) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	// 保存城镇
	public boolean saveTown(int townId, String townName) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _town"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ " town_id INTEGER , " + "town_name TEXT)");

		if (judgeTownSave(townId)) {

			result = false;
		} else {

			db.execSQL("INSERT INTO _town(town_id , town_name) VALUES (?,?)",
					new Object[] { townId, townName });
			result = true;

		}

		return result;
	}

	// 判断城镇是否存在
	public boolean judgeTownSave(int townId) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _town"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ " town_id INTEGER ," + "town_name TEXT)");
		Cursor c = db.rawQuery("SELECT * FROM _town WHERE town_id=" + townId,
				null);
		if (c.getCount() > 0) {
			result = true;

		} else {
			result = false;

		}

		return result;
	}

	// 保存标签
	public boolean saveTag(int tagId, String tagName) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _tag"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "tag_id INTEGER ," + "tag_name INTEGER)");
		if (judgeTagSave(tagId)) {
			result = false;
		} else {
			db.execSQL("INSERT INTO _tag(tag_id , tag_name) VALUES(?,?)",
					new Object[] { tagId, tagName });
			result = true;
		}

		return result;
	}

	// 判断标签是否存在
	public boolean judgeTagSave(int tagId) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _tag"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "tag_id INTEGER ," + "tag_name INTEGER)");
		Cursor c = db.rawQuery("SELECT * FROM _tag where tag_Id = " + tagId,
				null);

		if (c.getCount() > 0) {
			result = true;
		} else {

			result = false;
		}

		return result;
	}
}
