package com.xiaoguo.wasp.mobile.database;

import com.xiaoguo.wasp.mobile.model.WeatherDbInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherInfoDb {
	private static final String db_name = "weatherDb.db";

	private SQLiteDatabase db;

	Context context;

	public WeatherInfoDb(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}

	/**
	 * 将天气相关信息存入
	 * 
	 * @param weatherData
	 *            天气信息实体
	 * @return
	 */
	public boolean saveWeatherInfo(WeatherDbInfo weatherData) {
		boolean result = false;
		try {

			db.execSQL("CREATE table IF NOT EXISTS _weatherinfo"
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT , currentTime TEXT,currentCity TEXT ,currentTemperature TEXT ,"
					+ "currentTemperatureAttrs TEXT, currentYuBao TEXT, currentQiXiang TEXT, currentCloths TEXT,"
					+ "secondTime TEXT,secondWeatherAttrs TEXT, secondTemperatureRange TEXT,secondWind TEXT ,thirdTime TEXT"
					+ ",thirdWeatherAttrs TEXT, thirdTemperatureRange TEXT,thirdWind TEXT,"
					+ "fourTime TEXT, fourWeatherAttrs TEXT,fourTemperature TEXT,fourWind Text)");
			System.out.println("创建表是否成功位置");
			if (weatherData != null) {
				db.execSQL(
						"INSERT INTO _weatherinfo"
								+ "(currentTime,currentCity,currentTemperature,currentTemperatureAttrs,currentYuBao,currentQiXiang,"
								+ "currentCloths,secondTime,secondWeatherAttrs,secondTemperatureRange,secondWind,"
								+ "thirdTime,thirdWeatherAttrs,thirdTemperatureRange,thirdWind,"
								+ "fourTime,fourWeatherAttrs,fourTemperature,fourWind) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { weatherData.getCurrentTime(),
								weatherData.getCurrentCity(),
								weatherData.getCurrentTemperature(),
								weatherData.getCurrentTemperatureAttrs(),
								weatherData.getCurrentYuBao(),
								weatherData.getCurrentQiXiang(),
								weatherData.getCurrentCloths(),
								weatherData.getSecondTime(),
								weatherData.getSecondWeatherAttrs(),
								weatherData.getSecondTemperatureRange(),
								weatherData.getSecondWind(),
								weatherData.getThirdTime(),
								weatherData.getThirdWeatherAttrs(),
								weatherData.getThirdTemperatureRange(),
								weatherData.getThirdWind(),
								weatherData.getFourTime(),
								weatherData.getFourWeatherAttrs(),
								weatherData.getFourTemperature(),
								weatherData.getFourWind() });
			}

			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		System.out.println("天气存入数据是否存入数据库--->>" + result);

		return result;

	}

	/**
	 * 删除保存天气信息的表
	 */

	public boolean DeleteWeatherDb() {
		boolean result = false;
		try {

			db.execSQL("DROP TABLE _weatherinfo");
			result = true;
		} catch (Exception e) {
			result = false;

		}
		return result;
	}

	/**
	 * 获取天气信息的数据
	 */

	public WeatherDbInfo getWeatherInfo() {
		WeatherDbInfo data = new WeatherDbInfo();
		db.execSQL("CREATE table IF NOT EXISTS _weatherinfo"
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT , currentTime TEXT,currentCity TEXT ,currentTemperature TEXT ,"
				+ "currentTemperatureAttrs TEXT, currentYuBao TEXT, currentQiXiang TEXT, currentCloths TEXT,"
				+ "secondTime TEXT,secondWeatherAttrs TEXT, secondTemperatureRange TEXT,secondWind TEXT ,thirdTime TEXT"
				+ ",thirdWeatherAttrs TEXT, thirdTemperatureRange TEXT,thirdWind TEXT,"
				+ "fourTime TEXT, fourWeatherAttrs TEXT,fourTemperature TEXT,fourWind Text)");
		Cursor c = db.rawQuery("SELECT currentTime,currentCity,currentTemperature,currentTemperatureAttrs,currentYuBao,currentQiXiang," +
				"currentCloths,secondTime,secondWeatherAttrs,secondTemperatureRange,secondWind,thirdTime,thirdWeatherAttrs,thirdTemperatureRange,thirdWind" +
				",fourTime,fourWeatherAttrs,fourTemperature,fourWind FROM _weatherinfo", null);

		while (c.moveToFirst()) {
			data.setCurrentTime(c.getString(c.getColumnIndex("currentTime")));
			data.setCurrentCity(c.getString(c.getColumnIndex("currentCity")));
			data.setCurrentTemperature(c.getString(c
					.getColumnIndex("currentTemperature")));
			data.setCurrentTemperatureAttrs(c.getString(c
					.getColumnIndex("currentTemperatureAttrs")));
			data.setCurrentYuBao(c.getString(c.getColumnIndex("currentYuBao")));
			data.setCurrentQiXiang(c.getString(c
					.getColumnIndex("currentQiXiang")));
			data.setCurrentCloths(c.getString(c.getColumnIndex("currentCloths")));
			data.setSecondTime(c.getString(c.getColumnIndex("secondTime")));
			data.setSecondWeatherAttrs(c.getString(c
					.getColumnIndex("secondWeatherAttrs")));
			data.setSecondTemperatureRange(c.getString(c
					.getColumnIndex("secondTemperatureRange")));
			data.setSecondWind(c.getString(c.getColumnIndex("secondWind")));
			data.setThirdTime(c.getString(c.getColumnIndex("thirdTime")));
			data.setThirdWeatherAttrs(c.getString(c
					.getColumnIndex("thirdWeatherAttrs")));
			data.setThirdTemperatureRange(c.getString(c
					.getColumnIndex("thirdTemperatureRange")));
			data.setThirdWind(c.getString(c.getColumnIndex("thirdWind")));
			data.setFourTime(c.getString(c.getColumnIndex("fourTime")));
			data.setFourWeatherAttrs(c.getString(c
					.getColumnIndex("fourWeatherAttrs")));
			data.setFourTemperature(c.getString(c
					.getColumnIndex("thirdTemperatureRange")));
			data.setFourWind(c.getString(c.getColumnIndex("fourWind")));

		}

		return data;

	}

}
