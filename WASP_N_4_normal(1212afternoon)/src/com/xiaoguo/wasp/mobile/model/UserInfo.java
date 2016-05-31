package com.xiaoguo.wasp.mobile.model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
/*
 * 用户信息实体
 * @author eva
 * */
public class UserInfo implements Parcelable{
	/*
	 * 用户ID
	 * */
	private String userId;
	
	/*
	 * 用户名
	 * */
	private String userName;
	
	/*
	 * 用户昵称
	 * */
	private String nickName;
	
	/*
	 * 密码
	 * */
	private String password;
	
	/*
	 * 头像地址
	 * */
	private String avaterUrl;
	
	/*
	 * 移动电话
	 * */
	private String mobilhone;
	
	/*
	 * 座机
	 * */
	private String telephone;
	
	/*
	 * 地址
	 * */
	private String address;
	
	/*
	 * 角色
	 * */
	private String type;
	
	Context  context;
	
	
	public UserInfo(Context context) {
		super();
		this.context = context;
	}

	
	public UserInfo() {
		super();
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the avaterUrl
	 */
	public String getAvaterUrl() {
		return avaterUrl;
	}

	/**
	 * @param avaterUrl the avaterUrl to set
	 */
	public void setAvaterUrl(String avaterUrl) {
		this.avaterUrl = avaterUrl;
	}

	/**
	 * @return the mobilhone
	 */
	public String getMobilhone() {
		return mobilhone;
	}

	/**
	 * @param mobilhone the mobilhone to set
	 */
	public void setMobilhone(String mobilhone) {
		this.mobilhone = mobilhone;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(userId);
		dest.writeString(userName);
		dest.writeString(nickName);
		dest.writeString(password);
		dest.writeString(avaterUrl);
		dest.writeString(mobilhone);
		dest.writeString(telephone);
		dest.writeString(address);
		dest.writeString(type);
	}

	public static final Parcelable.Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
		@Override
		public UserInfo createFromParcel(Parcel source) {
			UserInfo userInfo = new UserInfo();
			userInfo.userId = source.readString();
			userInfo.userName = source.readString();
			userInfo.nickName = source.readString();
			userInfo.password = source.readString();
			userInfo.avaterUrl = source.readString();
			userInfo.mobilhone = source.readString();
			userInfo.telephone = source.readString();
			userInfo.address = source.readString();
			userInfo.type = source.readString();
			return userInfo;
		}

		@Override
		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
		
	};
	
}
