package com.xiaoguo.wasp.mobile.model;

import android.content.Context;
import android.content.SharedPreferences;
/*
 * ����ʱ�仺��
 * */
public class UpdateTimeInfo {
	
	private String UPDATE_TIME = "update_time_info";
	
	/*
	 * �ֺ�Ԥ������ʱ��
	 * */
	private static final String WEATHER_WARNING_TIME = "weather_warning_time";
	
	/*
	 * ָ�����¸���ʱ��
	 */
	private static final String GUID_ARTICLE_TIME = "guid_article_time";
	
	/*
	 * ������Ϣ����ʱ��
	 * */
	private static final String WEATHER_INFO_TIME = "weather_info_time";
	/*
	 * ���߸���ʱ��
	 * */
	private static final String PRODUCTLINE_TIME = "productline_time";
	/*
	 * �����������ʱ��
	 * */
	private static final String PRODUCT_STEP_TIME = "product_step_time";
	/*
	 * ������������ʱ��
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
	
	//����ַ����͵�ֵ
	public void setUpdateTimeInfo(String key, String value){
		SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putString(key, value);
		editor.commit();
		}
		//������ε�ֵ
		public void setUpdateTimeInfo(String key, int value){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.putInt(key, value);
			editor.commit();
		}
		//��ų�����ֵ
		public void setUpdateTimeInfo(String key, Long value){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.putLong(key, value);
			editor.commit();
		}
		//��Ų�����ֵ
		public void setUpdateTimeInfo(String key, Boolean value){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.putBoolean(key, value);
			editor.commit();
		}
		//��ռ�¼
		public void clear(){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.clear();
			editor.commit();
		}
		
		//����û���Ϣ��ĳ���ַ����͵�ֵ
		public String getStringInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getString(key, "");
		}
		//����û�Ϣ��ĳ�����β�����ֵ
		public int getIntInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getInt(key, -1);
		}
		//����û���Ϣ��ĳ����β�����ֵ
		public Long getLongInfo(String key){
			SharedPreferences sp = context.getSharedPreferences(UPDATE_TIME, Context.MODE_PRIVATE);
			return sp.getLong(key, -1);
		}
		//����û���Ϣ��ĳ����Ͳ�����ֵ
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

