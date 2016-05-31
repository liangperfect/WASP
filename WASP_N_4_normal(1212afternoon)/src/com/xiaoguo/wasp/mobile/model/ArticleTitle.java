package com.xiaoguo.wasp.mobile.model;

/*
 * ���¸�Ҫ���ݣ��������µ�һ����ʾ����
 * @author eva
 * */
public class ArticleTitle {
	private Integer article_id;// ����id
	private String article_title;// ���±���
	private String article_publish_date;// ���·���ʱ��
	private String article_publish_name;// ���·���������
	private String article_content;// ��������
	private Integer article_browser_count = 0;// ���·�����
	private Integer article_type;// ��������
	private String save_userid;// ���������û�id
	private String article_tag;// ���±�ǩ
	private Integer is_read; // �Ƿ��������

	public ArticleTitle() {
		super();
	}

	public ArticleTitle(Integer article_id, String article_title,
			String article_publish_date, String article_publish_name,
			String article_content, Integer article_browser_count,
			Integer article_type, String save_userid, String article_tag,
			Integer is_read) {
		super();
		this.article_id = article_id;
		this.article_title = article_title;
		this.article_publish_date = article_publish_date;
		this.article_publish_name = article_publish_name;
		this.article_content = article_content;
		this.article_browser_count = article_browser_count;
		this.article_type = article_type;
		this.save_userid = save_userid;
		this.article_tag = article_tag;
		this.is_read = is_read;
	}

	public Integer getIs_read() {
		return is_read;
	}

	public void setIs_read(Integer is_read) {
		this.is_read = is_read;
	}

	public String getArticle_tag() {
		return article_tag;
	}

	public void setArticle_tag(String article_tag) {
		this.article_tag = article_tag;
	}

	public Integer getArticle_type() {
		return article_type;
	}

	public void setArticle_type(Integer article_type) {
		this.article_type = article_type;
	}

	public Integer getArticle_id() {
		return article_id;
	}

	public void setArticle_id(Integer article_id) {
		this.article_id = article_id;
	}

	public String getArticle_title() {
		return article_title;
	}

	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}

	public String getArticle_publish_date() {
		return article_publish_date;
	}

	public void setArticle_publish_date(String article_publish_date) {
		this.article_publish_date = article_publish_date;
	}

	public String getArticle_publish_name() {
		return article_publish_name;
	}

	public void setArticle_publish_name(String article_publish_name) {
		this.article_publish_name = article_publish_name;
	}

	public String getArticle_content() {
		return article_content;
	}

	public void setArticle_content(String article_content) {
		this.article_content = article_content;
	}

	public Integer getArticle_browser_count() {
		return article_browser_count;
	}

	public void setArticle_browser_count(Integer article_browser_count) {
		this.article_browser_count = article_browser_count;
	}

	public String getSave_userid() {
		return save_userid;
	}

	public void setSave_userid(String save_userid) {
		this.save_userid = save_userid;
	}

	@Override
	public String toString() {
		return "ArticleTitle [article_id=" + article_id + ", article_title="
				+ article_title + ", article_publish_date="
				+ article_publish_date + ", article_publish_name="
				+ article_publish_name + ", article_content=" + article_content
				+ ", article_browser_count=" + article_browser_count
				+ ", article_type=" + article_type + ", save_userid="
				+ save_userid + ",is_read = " + is_read + "]";
	}

}
