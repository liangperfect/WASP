package com.xiaoguo.wasp.mobile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class charData implements Parcelable {

	private static float[] data;

	private String[] time;

	private String xTitle;

	private String yTitle;

	private String title;

	public charData() {
	}

	public charData(float[] data, String[] time, String xTitle, String yTitle,
			String title) {
		this.data = data;
		this.time = time;
		this.xTitle = xTitle;
		this.yTitle = yTitle;
		this.title = title;

	}

	/**
	 * @return the data
	 */
	public float[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(float[] data) {
		this.data = data;
	}

	/**
	 * @return the time
	 */
	public String[] getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String[] time) {
		this.time = time;
	}

	/**
	 * @return the xTitle
	 */
	public String getxTitle() {
		return xTitle;
	}

	/**
	 * @param xTitle
	 *            the xTitle to set
	 */
	public void setxTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	/**
	 * @return the yTitle
	 */
	public String getyTitle() {
		return yTitle;
	}

	/**
	 * @param yTitle
	 *            the yTitle to set
	 */
	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		/**
		 * this.data = data; this.time = time; this.xTitle = xTitle; this.yTitle
		 * = yTitle; this.title = title;
		 */
		parcel.writeFloatArray(data);
		parcel.writeStringArray(time);
		parcel.writeString(xTitle);
		parcel.writeString(yTitle);
		parcel.writeString(title);
	}

	public static final Parcelable.Creator<charData> CREATOR = new Creator<charData>() {

		@Override
		public charData createFromParcel(Parcel parcel) {
			charData cd = new charData();
			// cd.setData(parcel.readFloatArray(data));
			cd.data = parcel.createFloatArray();
			cd.time = parcel.createStringArray();
			cd.xTitle = parcel.readString();
			cd.yTitle = parcel.readString();
			cd.title = parcel.readString();
			return cd;
		}

		@Override
		public charData[] newArray(int size) {
			return new charData[size];
		}

	};

}
