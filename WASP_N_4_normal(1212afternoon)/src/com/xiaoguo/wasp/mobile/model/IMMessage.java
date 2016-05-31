package com.xiaoguo.wasp.mobile.model;


import android.os.Parcel;
import android.os.Parcelable;
/*
 * 即时聊天消息的格式
 * @author eva
 * */
public class IMMessage implements Parcelable {
	/**
     * 将消息保存在数据库中时的key
     */
	public static final String IMMESSAGE_KEY = "immessage.key";
	
	/*
	 * 
	 * */
	public static final String KEY_TIME = "immessage.time";
	
	/*
	 * 消息内容
	 * */
	private String content;
	
	/*
	 * 消息时间
	 * */
	private String time;
	
	/*
	 * 消息标题
	 * */
	private String title;
	
	/*
	 * 消息发送人ID
	 * */
	private String fromSubJid;
	
	/*
	 * 被发送人ID
	 * */
	private String toSubJid;
	
	/*
	 * 发送人名字
	 * */
	private String fromSubName;
	
	/*
	 * 被发送人名字
	 * */
	private String toSubName;
	
	/*
	 *进一步的查看地址(新闻)
	 * */
	private String infoUrl;	
	
	/*
	 * 该发送者未读消息数字
	 * */
	private int unReadCount;	
	
	/*
	 * 判断是发送还是接收数据
	 * */
	private int msgType = 0;
	
	/*
	 * */
	public static final int SUCCESS = 0;
	
	/*
	 * */
	public static final int ERROR = 1;
	
	/*
	 * 发送消息成功或失败的类型
	 * */
	private int type;
	
	/*
	 * 发送消息的类型（新闻还是消息）
	 * */
	private int acceptType = MessageType.message;
	
	/*
	 * 发送消息分为：文字（表情）消息，图片消息，语音消息，视频消息，分别对应0,1,2,3
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
		public static final int news = 1;	// 新闻（显示的界面有差别）
		public static final int message = 2;		// 消息
	}
	
	//消息内容
	public class MessageContent{
		@SuppressWarnings("unused")
		private String title;	// 标题
		@SuppressWarnings("unused")
		private String infoIconUrl;
		@SuppressWarnings("unused")
		private String infoUrl;	// 进一步 的查看地址
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
	//标题（新闻才有标题）
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	//内容
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	//时间
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	//发送人ID
	public String getFromSubJid() {
		return fromSubJid;
	}
	public void setFromSubJid(String fromSubJid) {
		this.fromSubJid = fromSubJid;
	}
	//发送人名字
	public String getFromSubName() {
		return fromSubName;
	}
	public void setFromSubName(String fromSubName) {
		this.fromSubName = fromSubName;
	}
	//被发送人ID
	public String getToSubJid() {
		return toSubJid;
	}
	public void setToSubJid(String toSubJid) {
		this.toSubJid = toSubJid;
	}
	//被发送人名字
	public String getToSubName() {
		return toSubName;
	}
	public void setToSubName(String toSubName) {
		this.toSubName = toSubName;
	}
	//进一步消息地址
	public String getInfoUrl() {
		return infoUrl;
	}
	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}
	//未读消息数
	public int getUnReadCount() {
		return unReadCount;
	}
	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}
	//消息是发送还是接收的
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	//是新闻还是消息
	public int getAcceptType() {
		return acceptType;
	}
	public void setAcceptType(int acceptType) {
		this.acceptType = acceptType;
	}
	//消息是新闻吗
	public boolean isNews(){	
		return this.getAcceptType()==MessageType.news;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	//是新闻还是消息
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
