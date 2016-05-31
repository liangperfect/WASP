package com.xiaoguo.wasp.mobile.model;


import android.os.Parcel;
import android.os.Parcelable;
/*
 * ��ʱ������Ϣ�ĸ�ʽ
 * @author eva
 * */
public class IMMessage implements Parcelable {
	/**
     * ����Ϣ���������ݿ���ʱ��key
     */
	public static final String IMMESSAGE_KEY = "immessage.key";
	
	/*
	 * 
	 * */
	public static final String KEY_TIME = "immessage.time";
	
	/*
	 * ��Ϣ����
	 * */
	private String content;
	
	/*
	 * ��Ϣʱ��
	 * */
	private String time;
	
	/*
	 * ��Ϣ����
	 * */
	private String title;
	
	/*
	 * ��Ϣ������ID
	 * */
	private String fromSubJid;
	
	/*
	 * ��������ID
	 * */
	private String toSubJid;
	
	/*
	 * ����������
	 * */
	private String fromSubName;
	
	/*
	 * ������������
	 * */
	private String toSubName;
	
	/*
	 *��һ���Ĳ鿴��ַ(����)
	 * */
	private String infoUrl;	
	
	/*
	 * �÷�����δ����Ϣ����
	 * */
	private int unReadCount;	
	
	/*
	 * �ж��Ƿ��ͻ��ǽ�������
	 * */
	private int msgType = 0;
	
	/*
	 * */
	public static final int SUCCESS = 0;
	
	/*
	 * */
	public static final int ERROR = 1;
	
	/*
	 * ������Ϣ�ɹ���ʧ�ܵ�����
	 * */
	private int type;
	
	/*
	 * ������Ϣ�����ͣ����Ż�����Ϣ��
	 * */
	private int acceptType = MessageType.message;
	
	/*
	 * ������Ϣ��Ϊ�����֣����飩��Ϣ��ͼƬ��Ϣ��������Ϣ����Ƶ��Ϣ���ֱ��Ӧ0,1,2,3
	 * */
	private int chatMode;
	
	public IMMessage(String content, String time, String title,
			String fromSubJid, String toSubJid, String fromSubName,
			String toSubName, String infoUrl, int unReadCount, int msgType,
			int type, int acceptType, int chatMode) {
		super();
		this.content = content;
		this.time = time;
		this.title = title;
		this.fromSubJid = fromSubJid;
		this.toSubJid = toSubJid;
		this.fromSubName = fromSubName;
		this.toSubName = toSubName;
		this.infoUrl = infoUrl;
		this.unReadCount = unReadCount;
		this.msgType = msgType;
		this.type = type;
		this.acceptType = acceptType;
		this.chatMode = chatMode;
	}

	public static class MessageType{
		public static final int news = 1;	// ���ţ���ʾ�Ľ����в��
		public static final int message = 2;		// ��Ϣ
	}
	
	//��Ϣ����
	public class MessageContent{
		@SuppressWarnings("unused")
		private String title;	// ����
		@SuppressWarnings("unused")
		private String infoIconUrl;
		@SuppressWarnings("unused")
		private String infoUrl;	// ��һ�� �Ĳ鿴��ַ
	}
	
	public IMMessage() {
		this.type = SUCCESS;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	//���⣨���Ų��б��⣩
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	//����
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	//ʱ��
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	//������ID
	public String getFromSubJid() {
		return fromSubJid;
	}
	public void setFromSubJid(String fromSubJid) {
		this.fromSubJid = fromSubJid;
	}
	//����������
	public String getFromSubName() {
		return fromSubName;
	}
	public void setFromSubName(String fromSubName) {
		this.fromSubName = fromSubName;
	}
	//��������ID
	public String getToSubJid() {
		return toSubJid;
	}
	public void setToSubJid(String toSubJid) {
		this.toSubJid = toSubJid;
	}
	//������������
	public String getToSubName() {
		return toSubName;
	}
	public void setToSubName(String toSubName) {
		this.toSubName = toSubName;
	}
	//��һ����Ϣ��ַ
	public String getInfoUrl() {
		return infoUrl;
	}
	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}
	//δ����Ϣ��
	public int getUnReadCount() {
		return unReadCount;
	}
	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}
	//��Ϣ�Ƿ��ͻ��ǽ��յ�
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	//�����Ż�����Ϣ
	public int getAcceptType() {
		return acceptType;
	}
	public void setAcceptType(int acceptType) {
		this.acceptType = acceptType;
	}
	//��Ϣ��������
	public boolean isNews(){	
		return this.getAcceptType()==MessageType.news;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	//�����Ż�����Ϣ
	public int getChatMode() {
		return chatMode;
	}
	public void setChatMode(int chatMode) {
		this.chatMode = chatMode;
	}

	@Override
	public String toString() {
		return "IMMessage [content=" + content + ", time=" + time + ", title="
				+ title + ", fromSubJid=" + fromSubJid + ", toSubJid="
				+ toSubJid + ", fromSubName=" + fromSubName + ", toSubName="
				+ toSubName + ", infoUrl=" + infoUrl + ", unReadCount="
				+ unReadCount + ", msgType=" + msgType + ", type=" + type
				+ ", acceptType=" + acceptType + ", chatMode=" + chatMode + "]";
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(title);
		dest.writeString(fromSubJid);
		dest.writeString(toSubJid);
		dest.writeString(fromSubName);
		dest.writeString(toSubName);
		dest.writeString(infoUrl);
		dest.writeInt(unReadCount);
		dest.writeInt(msgType);
		dest.writeInt(type);
		dest.writeInt(acceptType);
	}
	
	public static final Parcelable.Creator<IMMessage> CREATOR = new Parcelable.Creator<IMMessage>() {

		@Override
		public IMMessage createFromParcel(Parcel source) {
			IMMessage message = new IMMessage();
			message.setContent(source.readString());
			message.setTime(source.readString());
			message.setTitle(source.readString());
			message.setFromSubJid(source.readString());
			message.setFromSubName(source.readString());
			message.setInfoUrl(source.readString());
			message.setUnReadCount(source.readInt());
			message.setMsgType(source.readInt());
			message.setType(source.readInt());
			message.setAcceptType(source.readInt());
			return message;
		}

		@Override
		public IMMessage[] newArray(int size) {
			return new IMMessage[size];
		}
		
	};
	
	
}
