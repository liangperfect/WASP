package com.xiaoguo.wasp.mobile.model;

import org.jivesoftware.smack.packet.RosterPacket;

import android.os.Parcel;
import android.os.Parcelable;
/*
 * ����ʵ��
 * @author eva
 * */
public class Friends implements Parcelable {
	public static final String userKey = "lovesong_user";//��user������intent��ʱ��key
	private String name;//��������
	private String JID;//����id
	private static RosterPacket.ItemType type;//�Ժ��ַ�ʽ���ܶԷ���½״̬
	private String status;//���ѵ�¼״̬
	private String from;//��������
	private String groupName;//������������
	private int imgId;//�û�״̬��Ӧ��ͼƬ
	private int size;//group��size
	private boolean available;//�Ƿ�����
	
	public Friends() {
		super();
	}
	
	public Friends(String name, String jID, String status, String from,
			String groupName, int imgId, int size, boolean available) {
		super();
		this.name = name;
		JID = jID;
		this.status = status;
		this.from = from;
		this.groupName = groupName;
		this.imgId = imgId;
		this.size = size;
		this.available = available;
	}
	
	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJID() {
		return JID;
	}

	public void setJID(String jID) {
		JID = jID;
	}

	public RosterPacket.ItemType getType() {
		return type;
	}

	@SuppressWarnings("static-access")
	public void setType(RosterPacket.ItemType type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() {
		return "Friends [name=" + name + ", JID=" + JID + ", status=" + status
				+ ", from=" + from + ", groupName=" + groupName + ", imgId="
				+ imgId + ", size=" + size + ", available=" + available + "]";
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(JID);
		dest.writeString(name);
		dest.writeString(from);
		dest.writeString(status);
		dest.writeInt(available ? 1 : 0);
	}

	public static final Parcelable.Creator<Friends> CREATOR = new Parcelable.Creator<Friends>() {

		@Override
		public Friends createFromParcel(Parcel source) {
			Friends f = new Friends();
			f.JID = source.readString();
			f.name = source.readString();
			f.from = source.readString();
			f.status = source.readString();
			f.available = source.readInt() == 1 ? true : false;
			return f;
		}

		@Override
		public Friends[] newArray(int size) {
			return new Friends[size];
		}

	};

	public Friends clone() {
		Friends friends = new Friends();
		friends.setAvailable(Friends.this.available);
		friends.setFrom(Friends.this.from);
		friends.setGroupName(Friends.this.groupName);
		friends.setImgId(Friends.this.imgId);
		friends.setJID(Friends.this.JID);
		friends.setName(Friends.this.name);
		friends.setSize(Friends.this.size);
		friends.setStatus(Friends.this.status);
		return friends;
	}

}
