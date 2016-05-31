package com.xiaoguo.wasp.mobile.model;

import android.content.Context;
import android.content.SharedPreferences;
/*
 * 更新时间缓存
 * */
public class UpdateTimeInfo {
	
	private String UPDATE_TIME = "update_time_info";
	
	/*
	 * 灾害预警更新时间
	 * */
	private static final String WEATHER_WARNING_TIME = "weather_warning_time";
	
	/*
	 * 指导文章更新时间
	 */
	private static final String GUID_ARTICLE_TIME = "guid_article_time";
	
	/*
	 * 气象信息更新时间
	 * */
	private static final String WEATHER_INFO_TIME = "weather_info_time";
	/*
	 * 产线更新时间
	 * */
	private static final String PRODUCTLINE_TIME = "productline_time";
	/*
	 * 生产步骤更新时间
	 * */
	private static final String PRODUCT_STEP_TIME = "product_step_time";
	/*
	 * 生产操作更新时间
	 * */
	private static final String PRODUCT_OPERATION_TIME = "product_operation_time";
	
	private Context context = null;

	public UpdateTimeInfo() {
		super();
	}

	public UpdateTimeInfo(Context context,String account) {
		super();
		this.context = context;
		UPDATE_TIME = account+UPDATE_TIME;
	}
	
	//存放字符串型的值
	public void setUpdateTimeInfo(String key, String value){
		SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putString(key, value);
		editor.commit();
		}
		//存放整形的值
		public void setUpdateTimeInfo(String key, int value){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.putInt(key, value);
			editor.commit();
		}
		//存放长整形值
		public void setUpdateTimeInfo(String key, Long value){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.putLong(key, value);
			editor.commit();
		}
		//存放布尔型值
		public void setUpdateTimeInfo(String key, Boolean value){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.putBoolean(key, value);
			editor.commit();
		}
		//清空记录
		public void clear(){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.clear();
			editor.commit();
		}
		
		//获得用户信息中某项字符串型的值
		public String getStringInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getString(key, "");
		}
		//获得用户息中某项整形参数的值
		public int getIntInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getInt(key, -1);
		}
		//获得用户信息中某项长整形参数的值
		public Long getLongInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getLong(key, -1);
		}
		//获得用户信息中某项布尔型参数的值
		public boolean getBooleanInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getBoolean(key, false);
		}
		public void setWeatherWarningTime(String weather_warning_time){
			setUpdateTimeInfo(WEATHER_WARNING_TIME, weather_warning_time);
		}
		public void setGuidArticleTime(String guidArticleTime){
			setUpdateTimeInfo(GUID_ARTICLE_TIME, guidArticleTime);
		}
		public void setWeatherInfoTime(String weather_info_time){
			setUpdateTimeInfo(WEATHER_INFO_TIME, weather_info_time);
		}
		public void setProductLineTime(String product_line_time){
			setUpdateTimeInfo(PRODUCTLINE_TIME, product_line_time);
		}
		public void setProductStepTime(String product_step_time){
			setUpdateTimeInfo(PRODUCT_STEP_TIME, product_step_time);
		}
		public void setProductOperationTime(String product_operation_time){
			setUpdateTimeInfo(PRODUCT_OPERATION_TIME, product_operation_time);
		}
		public String getWeatherWarningTime(){
			return getStringInfo(WEATHER_WARNING_TIME);
		}
		public String getGuidArticleTime(){
			return getStringInfo(GUID_ARTICLE_TIME);
		}
		public String getWeatherInfoTime(){
			return getStringInfo(WEATHER_INFO_TIME);
		}
		public String getProductLineTime(){
			return getStringInfo(PRODUCTLINE_TIME);
		}
		public String getProductStepTime(){
			return getStringInfo(PRODUCT_STEP_TIME);
		}
		public String getProductOperationTime(){
			return getStringInfo(PRODUCT_OPERATION_TIME);
		}
}

