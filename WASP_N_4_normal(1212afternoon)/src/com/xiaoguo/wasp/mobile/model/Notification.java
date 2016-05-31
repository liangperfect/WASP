package com.xiaoguo.wasp.mobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * 通知实体
 * @author eva
 *
 */
public class Notification implements Parcelable {
	/**
     * 通知id
     */
	private Long notification_id;
	
	/**
     * 通知标题
     */
	private String notification_title;
	
	/**
     * 通知内容
     */
	private String notification_content;
	
	/**
     * 发表通知者id
     */
	private Long notification_create_user_id;
	
	/**
     * 发表统治者账号
     */
	private String notification_create_user_name;
	
	/**
     * 发表通知时间
     */
	private String notification_create_date;
	
	/**
     * 通知状态
     */
	private Long notification_is_enabled = 1L;
	
	/**
     * 更新通知者id
     */
	private Long notification_update_user_id;
	
	/**
     * 更新通知者账号
     */
	private String notification_update_user_name;
	
	/**
     * 通知更新时间
     */
	private String notification_update_date;
	
	/**
     * 数据库通知状态
     * 0--未读,1--已读
     */
	private int enable=0;

	
	public Notification() {
		super();
	}
	
	public Notification(Long notification_id, String notification_title,
			String notification_content, Long notification_create_user_id,
			String notification_create_user_name,
			String notification_create_date, Long notification_is_enabled,
			Long notification_update_user_id,
			String notification_update_user_name,
			String notification_update_date,
			int enable) {
		super();
		this.notification_id = notification_id;
		this.notification_title = notification_title;
		this.notification_content = notification_content;
		this.notification_create_user_id = notification_create_user_id;
		this.notification_create_user_name = notification_create_user_name;
		this.notification_create_date = notification_create_date;
		this.notification_is_enabled = notification_is_enabled;
		this.notification_update_user_id = notification_update_user_id;
		this.notification_update_user_name = notification_update_user_name;
		this.notification_update_date = notification_update_date;
		this.enable = enable;
	}

	/**
	 * @return the notification_id
	 */
	public Long getNotification_id() {
		return notification_id;
	}

	/**
	 * @param notification_id the notification_id to set
	 */
	public void setNotification_id(Long notification_id) {
		this.notification_id = notification_id;
	}

	/**
	 * @return the notification_title
	 */
	public String getNotification_title() {
		return notification_title;
	}

	/**
	 * @param notification_title the notification_title to set
	 */
	public void setNotification_title(String notification_title) {
		this.notification_title = notification_title;
	}

	/**
	 * @return the notification_content
	 */
	public String getNotification_content() {
		return notification_content;
	}

	/**
	 * @param notification_content the notification_content to set
	 */
	public void setNotification_content(String notification_content) {
		this.notification_content = notification_content;
	}

	/**
	 * @return the notification_create_user_id
	 */
	public Long getNotification_create_user_id() {
		return notification_create_user_id;
	}

	/**
	 * @param notification_create_user_id the notification_create_user_id to set
	 */
	public void setNotification_create_user_id(Long notification_create_user_id) {
		this.notification_create_user_id = notification_create_user_id;
	}

	/**
	 * @return the notification_create_user_name
	 */
	public String getNotification_create_user_name() {
		return notification_create_user_name;
	}

	/**
	 * @param notification_create_user_name the notification_create_user_name to set
	 */
	public void setNotification_create_user_name(
			String notification_create_user_name) {
		this.notification_create_user_name = notification_create_user_name;
	}

	/**
	 * @return the notification_create_date
	 */
	public String getNotification_create_date() {
		return notification_create_date;
	}

	/**
	 * @param notification_create_date the notification_create_date to set
	 */
	public void setNotification_create_date(String notification_create_date) {
		this.notification_create_date = notification_create_date;
	}

	/**
	 * @return the notification_is_enabled
	 */
	public Long getNotification_is_enabled() {
		return notification_is_enabled;
	}

	/**
	 * @param notification_is_enabled the notification_is_enabled to set
	 */
	public void setNotification_is_enabled(Long notification_is_enabled) {
		this.notification_is_enabled = notification_is_enabled;
	}

	/**
	 * @return the notification_update_user_id
	 */
	public Long getNotification_update_user_id() {
		return notification_update_user_id;
	}

	/**
	 * @param notification_update_user_id the notification_update_user_id to set
	 */
	public void setNotification_update_user_id(Long notification_update_user_id) {
		this.notification_update_user_id = notification_update_user_id;
	}

	/**
	 * @return the notification_update_user_name
	 */
	public String getNotification_update_user_name() {
		return notification_update_user_name;
	}

	/**
	 * @param notification_update_user_name the notification_update_user_name to set
	 */
	public void setNotification_update_user_name(
			String notification_update_user_name) {
		this.notification_update_user_name = notification_update_user_name;
	}

	/**
	 * @return the notification_update_date
	 */
	public String getNotification_update_date() {
		return notification_update_date;
	}

	/**
	 * @param notification_update_date the notification_update_date to set
	 */
	public void setNotification_update_date(String notification_update_date) {
		this.notification_update_date = notification_update_date;
	}

	/**
	 * @return the enable
	 */
	public int getEnable() {
		return enable;
	}

	/**
	 * @param enable the enable to set
	 */
	public void setEnable(int enable) {
		this.enable = enable;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(notification_id);
		dest.writeString(notification_title);
		dest.writeString(notification_content);
		dest.writeLong(notification_create_user_id);
		dest.writeString(notification_create_user_name);
		dest.writeString(notification_create_date);
		dest.writeLong(notification_is_enabled);
		dest.writeLong(notification_update_user_id);
		dest.writeString(notification_update_user_name);
		dest.writeString(notification_update_date);
		dest.writeInt(enable);
	}
	
	public static final Parcelable.Creator<Notification> CREATOR = new Creator<Notification>() {
		@Override
		public Notification createFromParcel(Parcel source) {
			Notification notification = new Notification();
			notification.notification_id = source.readLong();
			notification.notification_title = source.readString();
			notification.notification_content = source.readString();
			notification.notification_create_user_id = source.readLong();
			notification.notification_create_user_name = source.readString();
			notification.notification_create_date = source.readString();
			notification.notification_is_enabled = source.readLong();
			notification.notification_update_user_id = source.readLong();
			notification.notification_update_user_name = source.readString();
			notification.notification_update_date = source.readString();
			notification.enable = source.readInt();
			return notification;
		}

		@Override
		public Notification[] newArray(int size) {
			return new Notification[size];
		}
		
	};


	@Override
	public String toString() {
		return "Notification [notification_id=" + notification_id
				+ ", notification_title=" + notification_title
				+ ", notification_content=" + notification_content
				+ ", notification_create_user_id="
				+ notification_create_user_id
				+ ", notification_create_user_name="
				+ notification_create_user_name + ", notification_create_date="
				+ notification_create_date + ", notification_is_enabled="
				+ notification_is_enabled + ", notification_update_user_id="
				+ notification_update_user_id
				+ ", notification_update_user_name="
				+ notification_update_user_name + ", notification_update_date="
				+ notification_update_date + ", enable=" + enable + "]";
	}
	
	
}
