package com.xiaoguo.wasp.mobile.model;

/*
 * 文章概要内容，访问文章第一级显示内容
 * @author eva
 * */
public class ArticleTitle {
	private Integer article_id;// 文章id
	private String article_title;// 文章标题
	private String article_publish_date;// 文章发表时间
	private String article_publish_name;// 文章发表者名称
	private String article_content;// 文章内容
	private Integer article_browser_count = 0;// 文章访问量
	private Integer article_type;// 文章类型
	private String save_userid;// 保存文章用户id
	private String article_tag;// 文章标签
	private Integer is_read; // 是否读了文章

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
