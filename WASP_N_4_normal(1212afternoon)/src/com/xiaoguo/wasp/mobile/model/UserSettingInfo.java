package com.xiaoguo.wasp.mobile.model;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * 登陆的用户信息存入sp
 * @author eva
 * */
public class UserSettingInfo {
	/*
	 * sp的名字
	 */
	private static final String USER_INFO = "user_info";

	/*
	 * 登录信息 ACCOUNT--账号 PASSWORD--密码 TYPE--登陆身份
	 */
	private static final String ACCOUNT = "account";
	private static final String PASSWORD = "password";
	private static final String TYPE = "type";

	/*
	 * 登陆用户基本信息 USER_NAME--用户名 USER_SEX--用户性别 USER_PHONE--用户电话
	 * IS_SAVE_PSWD_CHECKED--保存密码与否 IS_AUTO_LOGIN_CHECKED--自动登录与否 ADDRESS--地址
	 * DEPARTMENT--部门 EMAIL--邮箱 BIRTHDAY--生日 REGISTERDAY--注册时间 HOST--登录服务器ip
	 */
	private static final String USER_NAME = "user_name";
	private static final String USER_SEX = "user_sex";
	private static final String USER_PHONE = "user_phone";
	private static final String IS_SAVE_PSWD_CHECKED = "is_save_pswd_checked";
	private static final String IS_AUTO_LOGIN_CHECKED = "is_auto_login_checked";
	private static final String ADDRESS = "address";
	private static final String DEPARTMENT = "department";
	private static final String EMAIL = "email";
	private static final String BIRTHDAY = "birthday";
	private static final String REGISTERDAY = "register_day";
	private static final String HOST = "host";
	private static final String WEATHERADDRESS = "weather_address";
	private static final String USER_XIAN_NAME = "user_xian_name";
	private static final String USER_ROLE_NAME = "user_role_name";
	private static final String USER_SERVE_TYPE = "user_serve_type";
	private static final String USER_SCALE = "user_scale";
	private static final String USER_PLANT_TYPE = "user_plant_type";
	private static final String USER_PLANT_LOCAITON = "user_plant_location";
	private static final String USER_PLANT_YEAR = "user_plant_year";
	private static final String USER_PALNT_SCALE = "user_plant_scale";

	private static final String FIELDS = "无";
	private Context context = null;

	public UserSettingInfo() {
		super();
	}

	public UserSettingInfo(Context context) {
		super();
		this.context = context;
	}

	// 存放字符串型的值
	public void setUserInfo(String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putString(key, value);
		editor.commit();
	}

	// 存放整形的值
	public void setUserInfo(String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putInt(key, value);
		editor.commit();
	}

	// 存放长整形值
	public void setUserInfo(String key, Long value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putLong(key, value);
		editor.commit();
	}

	// 存放布尔型值
	public void setUserInfo(String key, Boolean value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putBoolean(key, value);
		editor.commit();
	}

	// 清空记录
	public void clear() {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	// 注销用户时清空用户名和密码
	public void logOut() {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(ACCOUNT);
		editor.remove(PASSWORD);
		editor.remove(WEATHERADDRESS);
		editor.commit();
	}

	// 获得用户信息中某项字符串型的值
	public String getStringInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	// 获得用户息中某项整形参数的值
	public int getIntInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}

	// 获得用户信息中某项长整形参数的值
	public Long getLongInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getLong(key, -1);
	}

	// 获得用户信息中某项布尔型参数的值
	public boolean getBooleanInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	public void setUserAccount(String account) {
		setUserInfo(ACCOUNT, account);
	}

	public void setUserPswd(String password) {
		setUserInfo(PASSWORD, password);
	}

	public void setUserName(String username) {
		setUserInfo(USER_NAME, username);
	}

	public void setUserSex(String sex) {
		setUserInfo(USER_SEX, sex);
	}

	public void setUserPhone(String phone) {
		setUserInfo(USER_PHONE, phone);
	}

	public void setSavePswd(boolean savePswd) {
		setUserInfo(IS_SAVE_PSWD_CHECKED, savePswd);
	}

	public void setAutoLogin(boolean autoLogin) {
		setUserInfo(IS_AUTO_LOGIN_CHECKED, autoLogin);
	}

	public String getAccount() {
		return getStringInfo(ACCOUNT);
	}

	public String getPassword() {
		return getStringInfo(PASSWORD);
	}

	public String getUserName() {
		return getStringInfo(USER_NAME);
	}

	public String getUserSex() {
		return getStringInfo(USER_SEX);
	}

	public String getBirth() {
		return getStringInfo(BIRTHDAY);
	}

	public String getRegister() {
		return getStringInfo(REGISTERDAY);
	}

	public String getUserPhone() {
		return getStringInfo(USER_PHONE);
	}

	public boolean getSavePswd() {
		return getBooleanInfo(IS_SAVE_PSWD_CHECKED);
	}

	public boolean getAutoLogin() {
		return getBooleanInfo(IS_AUTO_LOGIN_CHECKED);
	}

	public String getAddress() {
		return getStringInfo(ADDRESS);
	}

	public String getDepartment() {
		return getStringInfo(DEPARTMENT);
	}

	public String getEmail() {
		return getStringInfo(EMAIL);
	}

	public String getHost() {
		return getStringInfo(HOST);
	}

	public void setAddress(String address) {
		setUserInfo(ADDRESS, address);
	}

	public String getType() {
		return getStringInfo(TYPE);
	}

	public void setType(String type) {
		setUserInfo(TYPE, type);
	}

	public void setBirth(String birth) {
		setUserInfo(BIRTHDAY, birth);
	}

	public void setRegister(String register) {
		setUserInfo(REGISTERDAY, register);
	}

	public void setEmail(String email) {
		setUserInfo(EMAIL, email);
	}

	public void setDepartment(String department) {
		setUserInfo(DEPARTMENT, department);
	}

	public void setHost(String host) {
		setUserInfo(HOST, host);
	}

	public void setFields(String fields) {
		setUserInfo(FIELDS, fields);
	}

	public String getFields() {
		return getStringInfo(FIELDS);
	}

	public void setWeatherAddress(String address) {
		setUserInfo(WEATHERADDRESS, address);
	}

	public String getWeatherAddress() {

		return getStringInfo(WEATHERADDRESS);
	}

	public void setUserXianName(String xian) {
		setUserInfo(USER_XIAN_NAME, xian);
	}

	public String getUserXianName() {
		return getStringInfo(USER_XIAN_NAME);
	}

	// //private static final String USER_ROLE_NAME = "user_role_name";
	// private static final String USER_SERVE_TYPE = "user_serve_type";
	// private static final String USER_SCALE = "user_scale";

	public void setUserRoleName(String name) {
		setUserInfo(USER_ROLE_NAME, name);
	}

	public String getUserRoleName() {
		return getStringInfo(USER_ROLE_NAME);
	}

	public void setUserServeType(String type) {
		setUserInfo(USER_SERVE_TYPE, type);
	}

	public String getUserServeType() {
		return getStringInfo(USER_SERVE_TYPE);
	}

	public void setUserScale(String scale) {
		setUserInfo(USER_SCALE, scale);
	}

	public String getUserScale() {
		return getStringInfo(USER_SCALE);
	}

	public void setUserPlantType(String type) {
		setUserInfo(USER_PLANT_TYPE, type);
	}

	public String getUserPlantType() {
		return getStringInfo(USER_PLANT_TYPE);
	}

	// private static final String USER_PLANT_LOCAITON = "user_plant_location";
	// private static final String USER_PLANT_YEAR = "user_plant_year";
	// private static final String USER_PALNT_SCALE = "user_plant_scale";

	public void setUserPlantLocation(String location) {
		setUserInfo(USER_PLANT_LOCAITON, location);
	}

	public String getUserPlantLocation() {
		return getStringInfo(USER_PLANT_LOCAITON);
	}

	public void setUserPlantYear(String year) {
		setUserInfo(USER_PLANT_YEAR, year);
	}

	public String getUserPlantYear() {
		return getStringInfo(USER_PLANT_YEAR);
	}

	public void setUserPlantScale(String scale) {
		setUserInfo(USER_PALNT_SCALE, scale);
	}

	public String getUserPlantScale() {
		return getStringInfo(USER_PALNT_SCALE);
	}

}
