package com.xiaoguo.wasp.mobile.model;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * ��½���û���Ϣ����sp
 * @author eva
 * */
public class UserSettingInfo {
	/*
	 * sp������
	 */
	private static final String USER_INFO = "user_info";

	/*
	 * ��¼��Ϣ ACCOUNT--�˺� PASSWORD--���� TYPE--��½���
	 */
	private static final String ACCOUNT = "account";
	private static final String PASSWORD = "password";
	private static final String TYPE = "type";

	/*
	 * ��½�û�������Ϣ USER_NAME--�û��� USER_SEX--�û��Ա� USER_PHONE--�û��绰
	 * IS_SAVE_PSWD_CHECKED--����������� IS_AUTO_LOGIN_CHECKED--�Զ���¼��� ADDRESS--��ַ
	 * DEPARTMENT--���� EMAIL--���� BIRTHDAY--���� REGISTERDAY--ע��ʱ�� HOST--��¼������ip
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

	private static final String FIELDS = "��";
	private Context context = null;

	public UserSettingInfo() {
		super();
	}

	public UserSettingInfo(Context context) {
		super();
		this.context = context;
	}

	// ����ַ����͵�ֵ
	public void setUserInfo(String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putString(key, value);
		editor.commit();
	}

	// ������ε�ֵ
	public void setUserInfo(String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putInt(key, value);
		editor.commit();
	}

	// ��ų�����ֵ
	public void setUserInfo(String key, Long value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putLong(key, value);
		editor.commit();
	}

	// ��Ų�����ֵ
	public void setUserInfo(String key, Boolean value) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.putBoolean(key, value);
		editor.commit();
	}

	// ��ռ�¼
	public void clear() {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	// ע���û�ʱ����û���������
	public void logOut() {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(ACCOUNT);
		editor.remove(PASSWORD);
		editor.remove(WEATHERADDRESS);
		editor.commit();
	}

	// ����û���Ϣ��ĳ���ַ����͵�ֵ
	public String getStringInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	// ����û�Ϣ��ĳ�����β�����ֵ
	public int getIntInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}

	// ����û���Ϣ��ĳ����β�����ֵ
	public Long getLongInfo(String key) {
		SharedPreferences sp = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return sp.getLong(key, -1);
	}

	// ����û���Ϣ��ĳ����Ͳ�����ֵ
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
