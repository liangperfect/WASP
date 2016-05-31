package com.xiaoguo.wasp.mobile.model;

/***
 * 文章vo
 * 
 * @author eva
 * 
 */
public class Article {
	private Integer article_id;// 文章id
	private String article_title;// 文章标题
	private Long article_publisher_id;// 发表者id
	private String article_publish_date;// 发表时间
	private String article_summary;// 文章摘要
	private String article_content;// 文章内容
	private String article_tag_id;// 文章标签id
	private Integer article_browser_count = 0;// 文章访问量
	private Integer article_like;// 赞
	private Integer article_reject;// 反对
	private Integer article_type;// 文章类型
	private Integer article_enabled = 1;// 文章状态

	private String article_user_name;// 发表者名字
	private String article_tag_name;// 文章标签名
	
	/**
	 * @return the article_id
	 */
	public Integer getArticle_id() {
		return article_id;
	}

	/**
	 * @param article_id
	 *            the article_id to set
	 */
	public void setArticle_id(Integer article_id) {
		this.article_id = article_id;
	}

	/**
	 * @return the article_title
	 */
	public String getArticle_title() {
		return article_title;
	}

	/**
	 * @param article_title
	 *            the article_title to set
	 */
	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}

	/**
	 * @return the article_publisher_id
	 */
	public Long getArticle_publisher_id() {
		return article_publisher_id;
	}

	/**
	 * @param article_publisher_id
	 *            the article_publisher_id to set
	 */
	public void setArticle_publisher_id(Long article_publisher_id) {
		this.article_publisher_id = article_publisher_id;
	}

	/**
	 * @return the article_publish_date
	 */
	public String getArticle_publish_date() {
		return article_publish_date;
	}

	/**
	 * @param article_publish_date
	 *            the article_publish_date to set
	 */
	public void setArticle_publish_date(String article_publish_date) {
		this.article_publish_date = article_publish_date;
	}

	/**
	 * @return the article_summary
	 */
	public String getArticle_summary() {
		return article_summary;
	}

	/**
	 * @param article_summary
	 *            the article_summary to set
	 */
	public void setArticle_summary(String article_summary) {
		this.article_summary = article_summary;
	}

	/**
	 * @return the article_content
	 */
	public String getArticle_content() {
		return article_content;
	}

	/**
	 * @param article_content
	 *            the article_content to set
	 */
	public void setArticle_content(String article_content) {
		this.article_content = article_content;
	}

	/**
	 * @return the article_tag_id
	 */
	public String getArticle_tag_id() {
		return article_tag_id;
	}

	/**
	 * @param article_tag_id
	 *            the article_tag_id to set
	 */
	public void setArticle_tag_id(String article_tag_id) {
		this.article_tag_id = article_tag_id;
	}

	/**
	 * @return the article_browser_count
	 */
	public Integer getArticle_browser_count() {
		return article_browser_count;
	}

	/**
	 * @param article_browser_count
	 *            the article_browser_count to set
	 */
	public void setArticle_browser_count(Integer article_browser_count) {
		this.article_browser_count = article_browser_count;
	}

	/**
	 * @return the article_like
	 */
	public Integer getArticle_like() {
		return article_like;
	}

	/**
	 * @param article_like
	 *            the article_like to set
	 */
	public void setArticle_like(Integer article_like) {
		this.article_like = article_like;
	}

	/**
	 * @return the article_reject
	 */
	public Integer getArticle_reject() {
		return article_reject;
	}

	/**
	 * @param article_reject
	 *            the article_reject to set
	 */
	public void setArticle_reject(Integer article_reject) {
		this.article_reject = article_reject;
	}

	/**
	 * @return the article_type
	 */
	public Integer getArticle_type() {
		return article_type;
	}

	/**
	 * @param article_type
	 *            the article_type to set
	 */
	public void setArticle_type(Integer article_type) {
		this.article_type = article_type;
	}

	/**
	 * @return the article_enabled
	 */
	public Integer getArticle_enabled() {
		return article_enabled;
	}

	/**
	 * @param article_enabled
	 *            the article_enabled to set
	 */
	public void setArticle_enabled(Integer article_enabled) {
		this.article_enabled = article_enabled;
	}

	/**
	 * @return the article_user_name
	 */
	public String getArticle_user_name() {
		return article_user_name;
	}

	/**
	 * @param article_user_name
	 *            the article_user_name to set
	 */
	public void setArticle_user_name(String article_user_name) {
		this.article_user_name = article_user_name;
	}

	/**
	 * @return the article_tag_name
	 */
	public String getArticle_tag_name() {
		return article_tag_name;
	}

	/**
	 * @param article_tag_name
	 *            the article_tag_name to set
	 */
	public void setArticle_tag_name(String article_tag_name) {
		this.article_tag_name = article_tag_name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Article [article_id=");
		builder.append(article_id);
		builder.append(", article_title=");
		builder.append(article_title);
		builder.append(", article_publisher_id=");
		builder.append(article_publisher_id);
		builder.append(", article_publish_date=");
		builder.append(article_publish_date);
		builder.append(", article_summary=");
		builder.append(article_summary);
		builder.append(", article_content=");
		builder.append(article_content);
		builder.append(", article_tag_id=");
		builder.append(article_tag_id);
		builder.append(", article_browser_count=");
		builder.append(article_browser_count);
		builder.append(", article_like=");
		builder.append(article_like);
		builder.append(", article_reject=");
		builder.append(article_reject);
		builder.append(", article_type=");
		builder.append(article_type);
		builder.append(", article_enabled=");
		builder.append(article_enabled);
		builder.append(", article_user_name=");
		builder.append(article_user_name);
		builder.append(", article_tag_name=");
		builder.append(article_tag_name);
//		builder.append(", is_read=");
//		builder.append(is_read);
		builder.append("]");
		return builder.toString();
	}

	public Article(Integer article_id, String article_title,
			Long article_publisher_id, String article_publish_date,
			String article_summary, String article_content,
			String article_tag_id, Integer article_browser_count,
			Integer article_like, Integer article_reject, Integer article_type,
			Integer article_enabled, String article_user_name,
			String article_tag_name) {
		super();
		this.article_id = article_id;
		this.article_title = article_title;
		this.article_publisher_id = article_publisher_id;
		this.article_publish_date = article_publish_date;
		this.article_summary = article_summary;
		this.article_content = article_content;
		this.article_tag_id = article_tag_id;
		this.article_browser_count = article_browser_count;
		this.article_like = article_like;
		this.article_reject = article_reject;
		this.article_type = article_type;
		this.article_enabled = article_enabled;
		this.article_user_name = article_user_name;
		this.article_tag_name = article_tag_name;
//		this.is_read = is_read;
	}

	public Article() {
		super();
	}

}
