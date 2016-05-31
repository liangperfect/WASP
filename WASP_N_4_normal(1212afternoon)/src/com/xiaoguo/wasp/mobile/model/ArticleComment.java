package com.xiaoguo.wasp.mobile.model;
/***
 * 文章评论vo
 * @author eva
 *
 */
public class ArticleComment {
	private int article_id;//文章id
	private int comment_id;//评论id
	private String commenter_id;//评论者id
	private String comment_name;//评论者名称
	private String comment_content;//评论内容
	
	public int getArticle_id() {
		return article_id;
	}
	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public String getCommenter_id() {
		return commenter_id;
	}
	public void setCommenter_id(String commenter_id) {
		this.commenter_id = commenter_id;
	}
	public String getComment_name() {
		return comment_name;
	}
	public void setComment_name(String comment_name) {
		this.comment_name = comment_name;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	@Override
	public String toString() {
		return super.toString();
	}
	public ArticleComment(int article_id, int comment_id, String commenter_id,
			String comment_name, String comment_content) {
		super();
		this.article_id = article_id;
		this.comment_id = comment_id;
		this.commenter_id = commenter_id;
		this.comment_name = comment_name;
		this.comment_content = comment_content;
	}
	
	
}
