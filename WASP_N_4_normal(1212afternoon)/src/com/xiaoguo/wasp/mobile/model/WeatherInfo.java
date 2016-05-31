package com.xiaoguo.wasp.mobile.model;

import android.content.Context;
import android.content.SharedPreferences;
/*
 * 天气信息存入sp
 * @author eva
 * */
public class WeatherInfo {
	/*
	 * sp名
	 * */
	private  String WEATHER_INFO = "weather_info";

	/*
	 * 天气信息更新时间
	 * */
	private static final String UPDATE_DATE = "update_date";
	
	/*
	 * 当前日期
	 * */
	private static final String CURRENT_DATE = "current_date";
	
	/*
	 * 当前城市
	 * */
	private static final String CURRENT_PROVIENCE = "current_provience";
	
	/*
	 * 当前城市
	 * */
	private static final String CURRENT_CITY = "current_city";
	
	/*
	 * 当前天的气象图片
	 * */
	private static final String CURRENT_PIC = "current_pic";
	
	/*
	 * 当前天的温度
	 * */
	private static final String CURRENT_TEMP = "current_temp";
	
	/*
	 * 当前天的风力风向
	 * */
	private static final String CURRENT_WIND = "current_wind";
	
	/*
	 * 当前天的总体描述
	 * */
	private static final String CURRENT_SUB = "current_sub";
	
	/*
	 * 未来一天日期
	 * */
	private static final String FIRST_DATE = "first_date";
	
	/*
	 * 未来一天最高温度
	 * */
	private static final String FIRST_TEMP_HIGH = "first_temp_high";
	
	/*
	 * 未来一天最低温度
	 * */
	private static final String FIRST_TEMP_LOW = "first_temp_low";
	
	/*
	 * 未来一天气象图片
	 * */
	private static final String FIRST_PIC = "first_pic";
	
	/*
	 * 未来二天日期
	 * */
	private static final String SECOND_DATE = "second_date";
	
	/*
	 * 未来二天最高温度
	 * */
	private static final String SECOND_TEMP_HIGH = "second_temp_high";
	
	/*
	 * 未来二天最低温度
	 * */
	private static final String SECOND_TEMP_LOW = "second_temp_low";
	
	/*
	 * 未来二天气象图片
	 * */
	private static final String SECOND_PIC = "second_pic";
	
	/*
	 * 未来三天日期
	 * */
	private static final String THREE_DATE = "three_date";
	
	/*
	 * 未来三天最高温度
	 * */
	private static final String THREE_TEMP_HIGH = "three_temp_high";
	
	/*
	 * 未来三天最低温度
	 * */
	private static final String THREE_TEMP_LOW = "three_temp_low";
	
	/*
	 * 未来三天气象图片
	 * */
	private static final String THREE_PIC = "three_pic";
	
	/*
	 * 未来四天日期
	 * */
	private static final String FOUR_DATE = "four_date";
	
	/*
	 * 未来四天最高温度
	 * */
	private static final String FOUR_TEMP_HIGH = "four_temp_high";
	
	/*
	 * 未来四天最低温度
	 * */
	private static final String FOUR_TEMP_LOW = "four_temp_low";
	
	/*
	 * 未来四天气象图片
	 * */
	private static final String FOUR_PIC = "four_pic";
	
	/*
	 * 未来五天日期
	 * */
	private static final String FIVE_DATE = "five_date";
	
	/*
	 * 未来五天最高温度
	 * */
	private static final String FIVE_TEMP_HIGH = "five_temp_high";
	
	/*
	 * 未来五天最低温度
	 * */
	private static final String FIVE_TEMP_LOW = "five_temp_low";
	
	/*
	 * 未来五天气象图片
	 * */
	private static final String FIVE_PIC = "five_pic";
	
	
	/*
	 * 气象灾害标题
	 * */
	private static final String DISASTER_WARNING_TITLE="disaster_warning_title";
	
	/*
	 * 指导文章标题1
	 * */
	private static final String GUID_TITLE_1="guid_title_1";
	
	/*
	 * 指导文章标题2
	 * */
	private static final String GUID_TITLE_2="guid_title_2";
	
	/*
	 * 指导文章标题3
	 * */
	private static final String GUID_TITLE_3="guid_title_3";
	
	/*
	 * 指导文章标题4
	 * */
	private static final String GUID_TITLE_4="guid_title_4";
	
	/*
	 * 指导文章标题5
	 * */
	private static final String GUID_TITLE_5="guid_title_5";
	
	/*
	 * 灾害预警id
	 * */
	private static final String DISASTER_WARNING_ID="disaster_warning_id";
	
	/*
	 * 指导文章id1
	 * */
	private static final String GUID_ID_1="guid_id_1";
	
	/*
	 * 指导文章id2
	 * */
	private static final String GUID_ID_2="guid_id_2";
	
	/*
	 * 指导文章id3
	 * */
	private static final String GUID_ID_3="guid_id_3";
	
	/*
	 * 指导文章id4
	 * */
	private static final String GUID_ID_4="guid_id_4";
	
	/*
	 * 指导文章id5
	 * */
	private static final String GUID_ID_5="guid_id_5";

	private static final String MESSAGE_TIME="message_time";
	
	private Context context = null;
	
	public WeatherInfo() {
		super();
	}

	public WeatherInfo(Context context,String userName) {
		super();
		this.context = context;
		this.WEATHER_INFO = WEATHER_INFO+userName;
	}
	
	//存放字符串型的值
	public void setWeatherInfo(String key, String value){
//		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putString(key, value);
		editor.commit();
		}
	//存放整形的值
	public void setWeatherInfo(String key, int value){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putInt(key, value);
		editor.commit();
	}
	//存放长整形值
	public void setWeatherInfo(String key, Long value){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putLong(key, value);
		editor.commit();
	}
	//存放布尔型值
	public void setWeatherInfo(String key, Boolean value){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putBoolean(key, value);
		editor.commit();
	}
	//清空记录
	public void clear(){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
	//获得用户信息中某项字符串型的值
	public String getStringInfo(String key){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}
	//获得用户息中某项整形参数的值
	public int getIntInfo(String key){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}
	//获得用户信息中某项长整形参数的值
	public Long getLongInfo(String key){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		return sp.getLong(key, -1);
	}
	//获得用户信息中某项布尔型参数的值
	public boolean getBooleanInfo(String key){
		SharedPreferences sp = context.getSharedPreferences(WEATHER_INFO, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}
	
	public void setUpdateTime(String time){
		setWeatherInfo(UPDATE_DATE, time);
	}
	
	public void setCurrentDate(String currentDate){
		setWeatherInfo(CURRENT_DATE, currentDate);
	}
	
	public void setCurrentProvience(String currentProvience){
		setWeatherInfo(CURRENT_PROVIENCE, currentProvience);
	}
	
	public void setCurrentCity(String currentCity){
		setWeatherInfo(CURRENT_CITY, currentCity);
	}
	
	public void setCurrentTem(String currentPic){
		setWeatherInfo(CURRENT_PIC, currentPic);
	}
	
	public void setCurrentPic(String currentTemp){
		setWeatherInfo(CURRENT_TEMP, currentTemp);
	}
	
	public void setCurrentWind(String currentWind){
		setWeatherInfo(CURRENT_WIND, currentWind);
	}
	
	public void setCurrentSub(String currentSub){
		setWeatherInfo(CURRENT_SUB, currentSub);
	}
	
	public void setFirstDate(String firstDate){
		setWeatherInfo(FIRST_DATE, firstDate);
	}
	public void setFirstTemHigh(String firstTemHigh){
		setWeatherInfo(FIRST_TEMP_HIGH, firstTemHigh);
	}
	public void setFirstTemLow(String firstTemLow){
		setWeatherInfo(FIRST_TEMP_LOW, firstTemLow);
	}
	public void setFirstPic(String firstPic){
		setWeatherInfo(FIRST_PIC, firstPic);
	}
	
	public void setSecondDate(String secondDate){
		setWeatherInfo(SECOND_DATE, secondDate);
	}
	public void setSecondTemHigh(String secondTemHigh){
		setWeatherInfo(SECOND_TEMP_HIGH, secondTemHigh);
	}
	public void setSecondTemLow(String secondTemLow){
		setWeatherInfo(SECOND_TEMP_LOW, secondTemLow);
	}
	public void setSecondPic(String secondPic){
		setWeatherInfo(SECOND_PIC, secondPic);
	}
	
	public void setThreeDate(String threeDate){
		setWeatherInfo(THREE_DATE, threeDate);
	}
	public void setThreeTemHigh(String threeTemHigh){
		setWeatherInfo(THREE_TEMP_HIGH, threeTemHigh);
	}
	public void setThreeTemLow(String threeTemLow){
		setWeatherInfo(THREE_TEMP_LOW, threeTemLow);
	}
	public void setThreePic(String threePic){
		setWeatherInfo(THREE_PIC, threePic);
	}
	
	public void setFourDate(String fourDate){
		setWeatherInfo(FOUR_DATE, fourDate);
	}
	public void setFourTemHigh(String fourTemHigh){
		setWeatherInfo(FOUR_TEMP_HIGH, fourTemHigh);
	}
	public void setFourTemLow(String fourTemLow){
		setWeatherInfo(FOUR_TEMP_LOW, fourTemLow);
	}
	public void setFourPic(String fourPic){
		setWeatherInfo(FOUR_PIC, fourPic);
	}
	
	public void setFiveDate(String fiveDate){
		setWeatherInfo(FIVE_DATE, fiveDate);
	}
	public void setFiveTemHigh(String fiveTemHigh){
		setWeatherInfo(FIVE_TEMP_HIGH, fiveTemHigh);
	}
	public void setFiveTemLow(String fiveTemLow){
		setWeatherInfo(FIVE_TEMP_LOW, fiveTemLow);
	}
	public void setFivePic(String fivePic){
		setWeatherInfo(FIVE_PIC, fivePic);
	}
	
	
	public String getUpdateTime(){
		return getStringInfo(UPDATE_DATE);
	}
	
	public String getCurrentDate(){
		return getStringInfo(CURRENT_DATE);
	}
	
	public String getCurrentProvience(){
		return getStringInfo(CURRENT_PROVIENCE);
	}
	
	public String getCurrentCity(){
		return getStringInfo(CURRENT_CITY);
	}
	
	public String getCurrentTem(){
		return getStringInfo(CURRENT_PIC);
	}
	
	public String getCurrentPic(){
		return getStringInfo(CURRENT_TEMP);
	}
	
	public String getCurrentWind(){
		return getStringInfo(CURRENT_WIND);
	}
	
	public String getCurrentSub(){
		return getStringInfo(CURRENT_SUB);
	}
	
	public String getFirstDate(){
		return getStringInfo(FIRST_DATE);
	}
	public String getFirstTemHigh(){
		return getStringInfo(FIRST_TEMP_HIGH);
	}
	public String getFirstTemLow(){
		return getStringInfo(FIRST_TEMP_LOW);
	}
	public String getFirstPic(){
		return getStringInfo(FIRST_PIC);
	}
	
	public String getSecondDate(){
		return getStringInfo(SECOND_DATE);
	}
	public String getSecondTemHigh(){
		return getStringInfo(SECOND_TEMP_HIGH);
	}
	public String getSecondTemLow(){
		return getStringInfo(SECOND_TEMP_LOW);
	}
	public String getSecondPic(){
		return getStringInfo(SECOND_PIC);
	}
	
	public String getThreeDate(){
		return getStringInfo(THREE_DATE);
	}
	public String getThreeTemHigh(){
		return getStringInfo(THREE_TEMP_HIGH);
	}
	public String getThreeTemLow(){
		return getStringInfo(THREE_TEMP_LOW);
	}
	public String getThreePic(){
		return getStringInfo(THREE_PIC);
	}
	
	public String getFourDate(){
		return getStringInfo(FOUR_DATE);
	}
	public String getFourTemHigh(){
		return getStringInfo(FOUR_TEMP_HIGH);
	}
	public String getFourTemLow(){
		return getStringInfo(FOUR_TEMP_LOW);
	}
	public String getFourPic(){
		return getStringInfo(FOUR_PIC);
	}
	
	public String getFiveDate(){
		return getStringInfo(FIVE_DATE);
	}
	public String getFiveTemHigh(){
		return getStringInfo(FIVE_TEMP_HIGH);
	}
	public String getFiveTemLow(){
		return getStringInfo(FIVE_TEMP_LOW);
	}
	public String getFivePic(){
		return getStringInfo(FIVE_PIC);
	}
	
	public String toString(){
		return "weatherInfo=[update_date="+getUpdateTime()+
				",currentcity="+getCurrentCity()+
				",currentday="+getCurrentDate()+
				",currentpic="+getCurrentPic()+
				",currentsub="+getCurrentSub()+
				",currenttemp="+getCurrentTem()+
				",currentwind="+getCurrentWind()+
				",firstday="+getFirstDate()+
				",firstpic="+getFirstPic()+
				",firsthigh="+getFirstTemHigh()+
				",firstlow="+getFirstTemLow()+
				",secondday="+getSecondDate()+
				",secondpic="+getSecondPic()+
				",secondhigh="+getSecondTemHigh()+
				",secondlow="+getSecondTemLow()+
				",threeday="+getThreeDate()+
				",threepic="+getThreePic()+
				",threehigh="+getThreeTemHigh()+
				",threelow="+getThreeTemLow()+
				",fourday="+getFourDate()+
				",fourpic="+getFourPic()+
				",fourhigh="+getFourTemHigh()+
				",fourlow="+getFourTemLow()+
				",fiveday="+getFiveDate()+
				",fivepic="+getFivePic()+
				",fivehigh="+getFiveTemHigh()+
				",fivelow="+getFiveTemLow()+"]";
	}
	
	public void setDisasterWarningTitle(String disaster_warning_title){
		setWeatherInfo(DISASTER_WARNING_TITLE, disaster_warning_title);
	}
	
	public String getDisasterWarningTitle(){
		return getStringInfo(DISASTER_WARNING_TITLE);
	}
	public void setGuidTitle1(String guidTitle1){
		setWeatherInfo(GUID_TITLE_1, guidTitle1);
	}
	public String getGuidTitle1(){
		return getStringInfo(GUID_TITLE_1);
	}
	public void setGuidTitle2(String guidTitle2){
		setWeatherInfo(GUID_TITLE_2, guidTitle2);
	}
	public String getGuidTitle2(){
		return getStringInfo(GUID_TITLE_2);
	}
	public void setGuidTitle3(String guidTitle3){
		setWeatherInfo(GUID_TITLE_3, guidTitle3);
	}
	public String getGuidTitle3(){
		return getStringInfo(GUID_TITLE_3);
	}
	public void setGuidTitle4(String guidTitle4){
		setWeatherInfo(GUID_TITLE_4, guidTitle4);
	}
	public String getGuidTitle4(){
		return getStringInfo(GUID_TITLE_4);
	}
	public void setGuidTitle5(String guidTitle5){
		setWeatherInfo(GUID_TITLE_5, guidTitle5);
	}
	public String getGuidTitle5(){
		return getStringInfo(GUID_TITLE_5);
	}
	public void setDisaterId(int disasterId){
		setWeatherInfo(DISASTER_WARNING_ID,disasterId);
	}
	public void setGuidId1(int guidId1){
		setWeatherInfo(GUID_ID_1, guidId1);
	}
	public int getDisaterId(){
		return getIntInfo(DISASTER_WARNING_ID);
	}
	public int getGuidId1(){
		return getIntInfo(GUID_ID_1);
	}
	public void setGuidId2(int guidId2){
		setWeatherInfo(GUID_ID_2, guidId2);
	}
	public int getGuidId2(){
		return getIntInfo(GUID_ID_2);
	}
	public void setGuidId3(int guidId3){
		setWeatherInfo(GUID_ID_3, guidId3);
	}
	public int getGuidId3(){
		return getIntInfo(GUID_ID_3);
	}
	public void setGuidId4(int guidId4){
		setWeatherInfo(GUID_ID_4, guidId4);
	}
	public int getGuidId4(){
		return getIntInfo(GUID_ID_4);
	}
	public void setGuidId5(int guidId5){
		setWeatherInfo(GUID_ID_5, guidId5);
	}
	public int getGuidId5(){
		return getIntInfo(GUID_ID_5);
	}
	
	public void setMessageTime(String messageTime){
		setWeatherInfo(MESSAGE_TIME, messageTime);
	}
	public String getMessageTime(){
		return getStringInfo(MESSAGE_TIME);
	}
}
