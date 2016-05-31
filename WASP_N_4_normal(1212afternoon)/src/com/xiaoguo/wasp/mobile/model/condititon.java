package com.xiaoguo.wasp.mobile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class condititon implements Parcelable {
	/**
	 * tvSelectedProvinceID = (TextView)
	 * findViewById(R.id.tv_selectedProvinceID); tvSelectedTownID = (TextView)
	 * findViewById(R.id.tv_selectedTownID); tvSelectedLabelID = (TextView)
	 * findViewById(R.id.tv_selectedTagID); tvSelectedProvinceID = (TextView)
	 * findViewById(R.id.tv_selectedProvinceID); tvSelectedVillageID =
	 * (TextView) findViewById(R.id.tv_selectedVillageID);
	 */
	private Integer provinceID;

	private Integer townID;

	private Integer tagID;

	private Integer villageID;

	public condititon() {
	}

	public condititon(Integer province, Integer townID, Integer tagID,
			Integer villageID) {

		this.provinceID = province;

		this.townID = townID;

		this.tagID = tagID;

		this.villageID = villageID;
	}

	/**
	 * @return the provinceID
	 */
	public Integer getProvinceID() {
		return provinceID;
	}

	/**
	 * @param provinceID
	 *            the provinceID to set
	 */
	public void setProvinceID(Integer provinceID) {
		this.provinceID = provinceID;
	}

	/**
	 * @return the townID
	 */
	public Integer getTownID() {
		return townID;
	}

	/**
	 * @param townID
	 *            the townID to set
	 */
	public void setTownID(Integer townID) {
		this.townID = townID;
	}

	/**
	 * @return the tagID
	 */
	public Integer getTagID() {
		return tagID;
	}

	/**
	 * @param tagID
	 *            the tagID to set
	 */
	public void setTagID(Integer tagID) {
		this.tagID = tagID;
	}

	/**
	 * @return the villageID
	 */
	public Integer getVillageID() {
		return villageID;
	}

	/**
	 * @param villageID
	 *            the villageID to set
	 */
	public void setVillageID(Integer villageID) {
		this.villageID = villageID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(provinceID);
		parcel.writeInt(townID);
		parcel.writeInt(tagID);
		parcel.writeInt(villageID);

	}

	public static final Parcelable.Creator<condititon> CREATOR = new Creator<condititon>() {
		@Override
		public condititon createFromParcel(Parcel source) {
			condititon con = new condititon();
			con.setProvinceID(source.readInt());
			con.setTownID(source.readInt());
			con.setTagID(source.readInt());
			con.setVillageID(source.readInt());
			return con;
		}

		@Override
		public condititon[] newArray(int size) {
			return new condititon[size];
		}

	};

}
