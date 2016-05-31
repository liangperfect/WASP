package com.xiaoguo.wasp.mobile.model;

/*
 * 专家实体
 * @author eva
 * */
public class Expert {
	private String expertAccount;//专家账号
	private String expertName;//专家名字
	private String expertDescription;//专家描述
	private String expertImg;//专家头像
	
	public Expert() {
		super();
	}
	
	public Expert(String expertAccount, String expertName,
			String expertDescription, String expertImg) {
		super();
		this.expertAccount = expertAccount;
		this.expertName = expertName;
		this.expertDescription = expertDescription;
		this.expertImg = expertImg;
	}
	
	public String getExpertAccount() {
		return expertAccount;
	}
	public void setExpertAccount(String expertAccount) {
		this.expertAccount = expertAccount;
	}
	public String getExpertName() {
		return expertName;
	}
	public void setExpertName(String expertName) {
		this.expertName = expertName;
	}
	public String getExpertDescription() {
		return expertDescription;
	}
	public void setExpertDescription(String expertDescription) {
		this.expertDescription = expertDescription;
	}
	public String getExpertImg() {
		return expertImg;
	}
	public void setExpertImg(String expertImg) {
		this.expertImg = expertImg;
	}
	
	@Override
	public String toString() {
		return "Expert [expertAccount=" + expertAccount + ", expertName="
				+ expertName + ", expertDescription=" + expertDescription
				+ ", expertImg=" + expertImg + "]";
	}
	
}
