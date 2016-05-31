package com.xiaoguo.wasp.mobile.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class User {
	String account;
	String password;
	String userName;
	String avater;
	String recentMessage;
	
	public User() {
		super();
	}

	
	public User(String account, String password,String userName,String avater,String recentMessage) {
		super();
		this.account = account;
		this.password = password;
		this.userName = userName;
		this.avater = avater;
		this.recentMessage = recentMessage;
	}
	public String getPassword() {
		return password;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccount() {
		return account;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserAvater(String avater) {
		this.avater = avater;
	}
	public String getAvater() {
		return avater;
	}
	public void setRecentMessage(String recentMessage) {
		this.recentMessage = recentMessage;
	}
	public String getRecentMessage() {
		return recentMessage;
	}
	@Override
	public String toString() {
		return "User [account=" + account + ", Password=" + password
				+ ",UserName="+userName+",avater="+avater+"]";
	}
	
	
	public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			User user = new User();
			user.account = source.readString();
			user.password = source.readString();
			return user;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
	public int describeContents() {
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(account);
		dest.writeString(password);

	}	
}
