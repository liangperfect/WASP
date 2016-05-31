package com.xiaoguo.wasp.mobile.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoguo.wasp.mobile.model.Article;
import com.xiaoguo.wasp.mobile.model.ArticleComment;
import com.xiaoguo.wasp.mobile.model.ArticleTitle;
import com.xiaoguo.wasp.mobile.model.Expert;
import com.xiaoguo.wasp.mobile.model.Friends;
import com.xiaoguo.wasp.mobile.model.Notification;

public class ProductDb {
	private static final String db_name = "product.db";
	private SQLiteDatabase db;
	Context context;

	public ProductDb(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
	}

	// ����������
	public String saveArticleName(ArticleTitle article) {
		String results = null;
		try {
			// db.execSQL("CREATE table IF NOT EXISTS _articlenames"+" (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			// +
			// "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
			// +
			// "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
			// +
			// ",article_type INTEGER,tag_name TEXT,save_userid TEXT)");
			// if(article != null){
			// db.execSQL("insert into _articlenames"
			// + " (article_id, article_title,article_publish_date," +
			// "article_publish_name,article_content,article_browser_count,article_type,tag_name,save_userid) values(?,?,?,?,?,?,?,?,?)",
			// new
			// Object[]{article.getArticle_id(),article.getArticle_title(),article.getArticle_publish_date(),
			// article.getArticle_publish_name(),article.getArticle_content(),article.getArticle_browser_count(),
			// article.getArticle_type(),article.getArticle_tag(),article.getSave_userid()});
			// }
			db.execSQL("CREATE table IF NOT EXISTS _articlenames"
					+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
					+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
					+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER )");
			if (article != null) {
				db.execSQL(
						"insert into _articlenames"
								+ " (article_id, article_title,article_publish_date,"
								+ "article_publish_name,article_content,article_browser_count,article_type,tag_name,save_userid,is_read) values(?,?,?,?,?,?,?,?,?,?)",
						new Object[] { article.getArticle_id(),
								article.getArticle_title(),
								article.getArticle_publish_date(),
								article.getArticle_publish_name(),
								article.getArticle_content(),
								article.getArticle_browser_count(),
								article.getArticle_type(),
								article.getArticle_tag(),
								article.getSave_userid(), article.getIs_read() });
			}

			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			results = "failed";
		}
		System.out.println("�������ݿ�" + results);
		return results;
	}

	// ��ȡĳһ�û�ĳЩ����
	public List<ArticleTitle> getSBArticleNames(int types, String userJid,
			int begin, int num) {
		ArticleTitle article = null;
		List<ArticleTitle> list = new ArrayList<ArticleTitle>();
		db.execSQL("CREATE table IF NOT EXISTS _articlenames"
				+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
				+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
				+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER)");
		Cursor c = db.rawQuery("SELECT * from _articlenames"
				+ " WHERE article_publish_name='" + userJid + "'"
				+ " AND article_type=" + types
				+ " ORDER BY article_publish_date DESC" + " LIMIT " + begin
				+ "," + num, null);
		System.out.println(types + "������cursor=" + c.getCount());
		while (c.moveToNext()) {
			article = new ArticleTitle(
					c.getInt(c.getColumnIndex("article_id")), c.getString(c
							.getColumnIndex("article_title")), c.getString(c
							.getColumnIndex("article_publish_date")),
					c.getString(c.getColumnIndex("article_publish_name")),
					c.getString(c.getColumnIndex("article_content")),
					c.getInt(c.getColumnIndex("article_browser_count")),
					c.getInt(c.getColumnIndex("article_type")), c.getString(c
							.getColumnIndex("save_userid")), c.getString(c
							.getColumnIndex("tag_name")), c.getInt(c
							.getColumnIndex("is_read")));
			System.out.println("article=" + article.toString());
			list.add(article);
		}
		c.close();
		return list;
	}

	// �ж�ĳһ�û������Ƿ��Ѷ� 0--δ�� 1--�Ѷ�
	public Integer judgeArticleIsRead(Integer articleId, String userJid) {
		Integer result = 0;
		db.execSQL("CREATE table IF NOT EXISTS _articlenames"
				+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
				+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
				+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER)");
		if (articleId > 0) {
			Cursor c = db.rawQuery(
					"SELECT is_read  FROM _articlenames WHERE article_id="
							+ articleId + " AND save_userid = '" + userJid
							+ "'", null);

			if (c.getCount() > 0) {

				while (c.moveToNext()) {
					result = c.getInt(c.getColumnIndex("is_read"));
				}
				c.close();
			} else {

				result = 0;
			}
		} else {
			result = 0;
		}

		return result;

	}

	// ĳһ�û����¶�ȡ֮��Ҫ�޸��Ѷ�״̬
	public String upDataIsRead(Integer article_id, String userJid) {
		String result = "false";
		db.execSQL("CREATE table IF NOT EXISTS _articlenames"
				+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
				+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
				+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER)");
		// if (cursor.getCount() > 0) {
		// db.execSQL("UPDATE _notification"/* +userAccount */+ " SET "
		// + "enable=" + enable + " WHERE notification_id="
		// + notification.getNotification_id());
		// Cursor c = db.rawQuery("SELECT * FROM _notification"
		// + /* userAccount+ */" WHERE notification_id="
		// + notification.getNotification_id(), null);
		// if (c.getCount() == 0) {
		// result = saveNotification(notification, userAccount, enable);
		// }
		// result = "success";
		// } else {
		// result = saveNotification(notification, userAccount, enable);
		// }
		if (article_id > 0) {

			db.execSQL("UPDATE _articlenames SET is_read = " + 1
					+ " WHERE article_id = " + article_id
					+ " AND save_userid = '" + userJid + "'");
			result = "success";
		} else {
			result = "falut";

		}

		return result;
	}

	// ��ȡĳ���û����ػ��ж�����ĳһ������û���Ķ�
	public Integer getUnReadArticleCount(String userJid, Integer articleyType) {
		Integer count = 0;
		db.execSQL("CREATE table IF NOT EXISTS _articlenames"
				+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
				+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
				+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER)");

		Cursor c = db.rawQuery("SELECT * FROM _articlenames WHERE is_read = "
				+ 0 + " AND save_userid = '" + userJid + "'"
				+ " AND article_type=" + articleyType, null);
		if (c.getCount() > 0) {
			count = c.getCount();
		} else {
			count = 0;

		}
		c.close();
		return count;
	}

	// ��ѯָ������ĳһ��������
	public List<ArticleTitle> getArticleNames(int types, String userJid,
			int begin, int num) {
		ArticleTitle article = null;
		List<ArticleTitle> list = new ArrayList<ArticleTitle>();
		db.execSQL("CREATE table IF NOT EXISTS _articlenames"
				+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
				+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
				+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER)");

		Cursor c2 = db.rawQuery("SELECT * from _articlenames", null);
		System.out.println("���ݿ����±�������" + c2.getCount());
		while (c2.moveToNext()) {
			String res = c2.getInt(c2.getColumnIndex("articlenames_id")) + ","
					+ c2.getInt(c2.getColumnIndex("article_id")) + ","
					+ c2.getString(c2.getColumnIndex("article_title")) + ","
					+ c2.getString(c2.getColumnIndex("article_publish_date"))
					+ ","
					+ c2.getString(c2.getColumnIndex("article_publish_name"))
					+ "," + c2.getString(c2.getColumnIndex("article_content"))
					+ ","
					+ c2.getInt(c2.getColumnIndex("article_browser_count"))
					+ "," + c2.getInt(c2.getColumnIndex("article_type")) + ","
					+ c2.getString(c2.getColumnIndex("save_userid")) + ","
					+ c2.getInt(c2.getColumnIndex("is_read"));
			System.out.println(res);
		}
		/*
		 * Cursor c =
		 * db.rawQuery("SELECT * from _articlenames"+" WHERE save_userid='"
		 * +userJid+"'"+
		 * " AND article_type="+types+" ORDER BY article_publish_date DESC"
		 * +" LIMIT "+begin+","+num,null);
		 */
		Cursor c = db.rawQuery("SELECT * from _articlenames"
				+ " WHERE article_type=" + types + " AND save_userid='"
				+ userJid + "' ORDER BY article_publish_date DESC" + " LIMIT "
				+ begin + "," + num, null);
		System.out.println(types + "������cursor=" + c.getCount());
		while (c.moveToNext()) {
			System.out.println("c.getInt(c.getColumnIndex(is_read)--->>"
					+ c.getInt(c.getColumnIndex("is_read")));

			article = new ArticleTitle(
					c.getInt(c.getColumnIndex("article_id")), c.getString(c
							.getColumnIndex("article_title")), c.getString(c
							.getColumnIndex("article_publish_date")),
					c.getString(c.getColumnIndex("article_publish_name")),
					c.getString(c.getColumnIndex("article_content")),
					c.getInt(c.getColumnIndex("article_browser_count")),
					c.getInt(c.getColumnIndex("article_type")), c.getString(c
							.getColumnIndex("save_userid")), c.getString(c
							.getColumnIndex("tag_name")), c.getInt(c2
							.getColumnIndex("is_read")));
			System.out.println("article=" + article.toString());
			list.add(article);
		}
		c.close();
		return list;
	}

	// ɾ��ArticleName
	public String deleteArticleName() {
		String result = "false";
		try {
			db.execSQL("DROP TABLE _articlenames");
			result = "success";
		} catch (Exception e) {
			result = "false";
		}

		return result;

	}

	// �ж����±��Ᵽ��û��
	public boolean isArticleSaved(int articleId, String username) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _articlenames"
				+ " (articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publish_date TEXT,"
				+ "article_publish_name TEXT,article_content TEXT,article_browser_count INTEGER"
				+ ",article_type INTEGER,tag_name TEXT,save_userid TEXT,is_read INTEGER)");
		Cursor c = db.rawQuery("SELECT * from _articlenames WHERE article_id="+ articleId + " AND save_userid='"+username+"'", null);
		if (c.getCount() > 0) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// ��������
	public String saveArticle(Article article, String userid) {
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _article"
					+ " ("
					+ "articlesave_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "article_id INTEGER,article_title TEXT,article_publisher_id TEXT,"
					+ "article_publish_date TEXT,article_summary TEXT,article_content TEXT,"
					+ "article_tag_id TEXT,article_browser_count INTEGER,article_like INTEGER,"
					+ "article_reject INTEGER,article_type INTEGER,"
					+ "article_enabled INTEGER,article_user_name TEXT,article_tag_name TEXT,"
					+ "save_userid TEXT)");
			if (article != null) {
				db.execSQL(
						"insert into _article"
								+ " (article_id, article_title,article_publisher_id,"
								+ "article_publish_date,article_summary,article_content,"
								+ "article_tag_id,article_browser_count,article_like,"
								+ "article_reject,article_type,article_enabled,article_user_name,"
								+ "article_tag_name,save_userid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { article.getArticle_id(),
								article.getArticle_title(),
								article.getArticle_publisher_id(),
								article.getArticle_publish_date(),
								article.getArticle_summary(),
								article.getArticle_content(),
								article.getArticle_tag_id(),
								article.getArticle_browser_count(),
								article.getArticle_like(),
								article.getArticle_reject(),
								article.getArticle_type(),
								article.getArticle_enabled(),
								article.getArticle_user_name(),
								article.getArticle_tag_name(), userid});
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			results = "failed";
		}
		System.out.println("�������£�" + results);
		return results;
	}

	// ��ȡĳһƪ����
	public Article getOneArticle(int article_id) {
		Article article = new Article();
		db.execSQL("CREATE table IF NOT EXISTS _article"
				+ " (articlesave_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER,article_title TEXT,article_publisher_id LONG,"
				+ "article_publish_date TEXT,article_summary TEXT,article_content TEXT,"
				+ "article_tag_id TEXT,article_browser_count INTEGER,article_like INTEGER,article_reject INTEGER,"
				+ "article_type INTEGER,article_enabled INTEGER,article_user_name TEXT,article_tag_name TEXT,"
				+ "save_userid TEXT)");
		Cursor c = db.rawQuery("SELECT * from _article" + " WHERE article_id="
				+ article_id, null);
		if (c.getCount() == 0) {
			article = null;
		} else {
			while (c.moveToNext()) {

				article = new Article(c.getInt(c.getColumnIndex("article_id")),
						c.getString(c.getColumnIndex("article_title")),
						c.getLong(c.getColumnIndex("article_publisher_id")),
						c.getString(c.getColumnIndex("article_publish_date")),
						c.getString(c.getColumnIndex("article_summary")),
						c.getString(c.getColumnIndex("article_content")),
						c.getString(c.getColumnIndex("article_tag_id")),
						c.getInt(c.getColumnIndex("article_browser_count")),
						c.getInt(c.getColumnIndex("article_like")), c.getInt(c
								.getColumnIndex("article_reject")), c.getInt(c
								.getColumnIndex("article_type")), c.getInt(c
								.getColumnIndex("article_enabled")),
						c.getString(c.getColumnIndex("article_user_name")),
						c.getString(c.getColumnIndex("article_tag_name")));
			}
		}
		return article;
	}

	// ������������
	public String saveArticleComment(ArticleComment comment, String userid) {
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _articlecomment"
					+ " ("
					+ "articlecommentsave_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "article_id INTEGER," + "article_comment_name TEXT,"
					+ "article_comment_content TEXT," + "save_userid TEXT)");
			if (comment != null) {
				db.execSQL("insert into _articlecomment" + " (" + "article_id,"
						+ "article_comment_name," + "article_comment_content,"
						+ "save_userid) values(?,?,?,?)", new Object[] {
						comment.getArticle_id(), comment.getComment_name(),
						comment.getComment_content(), userid });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			results = "failed";
		}
		System.out.println("�����������ۣ�" + results);
		return results;
	}

	// ��ȡ��������
	public List<ArticleComment> getArticleComments(int article_id) {
		List<ArticleComment> comments = new ArrayList<ArticleComment>();
		ArticleComment comment = null;
		db.execSQL("CREATE table IF NOT EXISTS _articlecomment" + " ("
				+ "articlecommentsave_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "article_id INTEGER," + "article_comment_name TEXT,"
				+ "article_comment_content TEXT," + "save_userid TEXT)");
		Cursor c = db.rawQuery("SELECT * from _articlecomment"
				+ " WHERE article_id=" + article_id, null);
		while (c.moveToNext()) {
			comment = new ArticleComment(article_id, 0, "", c.getString(c
					.getColumnIndex("article_comment_name")), c.getString(c
					.getColumnIndex("article_comment_content")));
			comments.add(comment);
		}
		return comments;
	}

	// ����֪ͨ
	public String saveNotification(Notification notification,
			String userAccount, int enable) {
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _notification"/* +userAccount */
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "notification_id INTEGER,notification_title TEXT,notification_content TEXT,"
					+ "notification_create_user_id INTEGER,notification_create_user_name TEXT,notification_create_date TEXT,"
					+ "notification_is_enabled INTEGER,notification_update_user_id INTEGER,notification_update_user_name TEXT,"
					+ "notification_update_date TEXT,enable INTEGER)");
			if (notification != null) {
				db.execSQL(
						"insert into _notification"/* +userAccount */
								+ " (notification_id, notification_title,notification_content,"
								+ "notification_create_user_id,notification_create_user_name,notification_create_date,"
								+ "notification_is_enabled,notification_update_user_id,notification_update_user_name,"
								+ "notification_update_date,enable) values(?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] {
								notification.getNotification_id(),
								notification.getNotification_title(),
								notification.getNotification_content(),
								notification.getNotification_create_user_id(),
								notification.getNotification_create_user_name(),
								notification.getNotification_create_date(),
								notification.getNotification_is_enabled(),
								notification.getNotification_update_user_id(),
								notification.getNotification_update_user_name(),
								notification.getNotification_update_date(),
								enable });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		return results;
	}

	// ����֪ͨ
	public String updateNotification(Notification notification,
			String userAccount, Long notification_id, int enable) {
		String result = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _notification"/* +userAccount */
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "notification_id INTEGER,notification_title TEXT,notification_content TEXT,"
					+ "notification_create_user_id INTEGER,notification_create_user_name TEXT,notification_create_date TEXT,"
					+ "notification_is_enabled INTEGER,notification_update_user_id INTEGER,notification_update_user_name TEXT,"
					+ "notification_update_date TEXT,enable INTEGER)");
			Cursor cursor = db.rawQuery("SELECT * FROM _notification"/*
																	 * +userAccount
																	 */, null);
			System.out.println("count22=" + cursor.getCount());
			if (cursor.getCount() > 0) {
				db.execSQL("UPDATE _notification"/* +userAccount */+ " SET "
						+ "enable=" + enable + " WHERE notification_id="
						+ notification.getNotification_id());
				Cursor c = db.rawQuery("SELECT * FROM _notification"
						+ /* userAccount+ */" WHERE notification_id="
						+ notification.getNotification_id(), null);
				if (c.getCount() == 0) {
					result = saveNotification(notification, userAccount, enable);
				}
				result = "success";
			} else {
				result = saveNotification(notification, userAccount, enable);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����֪ͨ������");
			result = "fialed";
		}
		return result;
	}

	// ��ȡ���е�֪ͨ
	public List<Notification> getAllNotification(String userAccount, int enable) {
		Notification notification = null;
		List<Notification> notifications = new ArrayList<Notification>();
		db.execSQL("CREATE table IF NOT EXISTS _notification"/* +userAccount */
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "notification_id INTEGER,notification_title TEXT,notification_content TEXT,"
				+ "notification_create_user_id INTEGER,notification_create_user_name TEXT,notification_create_date TEXT,"
				+ "notification_is_enabled INTEGER,notification_update_user_id INTEGER,notification_update_user_name TEXT,"
				+ "notification_update_date TEXT,enable INTEGER)");
		Cursor c = null;
		if (enable == -1) {// ���ȫ��
			c = db.rawQuery("SELECT * from _notification" /* + userAccount */,
					null);
		} else {
			c = db.rawQuery("SELECT * from _notification" /* + userAccount */
					+ " WHERE enable=" + enable, null);
		}
		System.out.println("���ݿ�Ľ��Ϊ=" + c.getCount());
		while (c.moveToNext()) {
			notification = new Notification(c.getLong(c
					.getColumnIndex("notification_id")), c.getString(c
					.getColumnIndex("notification_title")), c.getString(c
					.getColumnIndex("notification_content")), c.getLong(c
					.getColumnIndex("notification_create_user_id")),
					c.getString(c
							.getColumnIndex("notification_create_user_name")),
					c.getString(c.getColumnIndex("notification_create_date")),
					c.getLong(c.getColumnIndex("notification_is_enabled")),
					c.getLong(c.getColumnIndex("notification_update_user_id")),
					c.getString(c
							.getColumnIndex("notification_update_user_name")),
					c.getString(c.getColumnIndex("notification_update_date")),
					c.getInt(c.getColumnIndex("enable")));
			notifications.add(notification);
		}
		c.close();
		return notifications;
	}

	// ����ר��
	public String saveExpert(Expert expert, String userAccount) {
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _expert" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			if (expert != null) {
				db.execSQL(
						"insert into _expert" + " (expertAccount,"
								+ "expertName," + "expertDescription,"
								+ "expertImg," + "saveuser) values(?,?,?,?,?)",
						new Object[] { expert.getExpertAccount(),
								expert.getExpertName(),
								expert.getExpertDescription(),
								expert.getExpertImg(), userAccount });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		System.out.println("ר�ұ���" + results);
		return results;
	}

	// ��ѯר���ǳ�
	public String getExpertNickName(String expertAccount, String userAccpunt) {
		String nickName = expertAccount;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _expert" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			Cursor c = db.rawQuery("SELECT * from _expert"
					+ " WHERE expertAccount='" + expertAccount
					+ "' AND saveuser='" + userAccpunt + "'", null);
			while (c.moveToNext()) {
				nickName = c.getString(c.getColumnIndex("expertName"));
				System.out.println("nick=" + nickName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			nickName = expertAccount;
		}
		System.out.println("ר���ǳƣ�" + nickName);
		return nickName;
	}

	// �ж�ר���Ƿ��Ѿ�����
	public Boolean isExpertSaved(Expert expert, String userAccount) {
		Boolean results = false;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _expert" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			Cursor c = db.rawQuery("SELECT * from _expert"
					+ " WHERE expertAccount='" + expert.getExpertAccount()
					+ "' AND saveuser='" + userAccount + "'", null);
			if (c.getCount() > 0) {
				results = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = false;
		}
		return results;
	}

	// ����ר��
	public String updateExperts(Expert expert, String userAccount) {
		String result = "";
		try {
			db.execSQL("CREATE table IF NOT EXISTS _expert" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			Cursor c = db.rawQuery("SELECT * from _expert"
					+ " WHERE expertAccount='" + expert.getExpertAccount()
					+ "' AND saveuser='" + userAccount + "'", null);
			if (c.getCount() > 0) {
				db.execSQL("UPDATE _expert SET expertName='"
						+ expert.getExpertName() + "' WHERE expertAccount='"
						+ expert.getExpertAccount() + "' AND saveuser='"
						+ userAccount + "'");
			}
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			result = "failed";
		}
		return result;
	}

	//���������ݵ��û��洢
	public String saveLoadInfoUsers(String userAccount){
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _loadinfouser" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "userAccount TEXT)");
			if (userAccount != null) {
				db.execSQL(
						"insert into _loadinfouser" + " (userAccount) values(?)",
						new Object[] { userAccount });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		System.out.println("�����������û�" + results);
		return results;
	}
	//�ж��û��Ƿ�����������
	public boolean isUserLoadInfo(String userAccount){
		Boolean results = false;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _loadinfouser" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "userAccount TEXT)");
			Cursor c = db.rawQuery("SELECT * from _loadinfouser"
					+ " WHERE userAccount='" + userAccount + "'", null);
			if (c.getCount() > 0) {
				results = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = false;
		}
		return results;
	}
	// ��ȡר���б�
	public List<Expert> getAllExperts(String userAccount) {
		Expert expert = null;
		List<Expert> experts = new ArrayList<Expert>();
		db.execSQL("CREATE table IF NOT EXISTS _expert" + " "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "expertAccount TEXT," + "expertName TEXT,"
				+ "expertDescription TEXT," + "expertImg TEXT,"
				+ "saveuser TEXT)");
		Cursor c = db.rawQuery("SELECT * from _expert WHERE saveuser='"
				+ userAccount + "'", null);
		System.out.println("ר����Ϊ=" + c.getCount());
		while (c.moveToNext()) {
			expert = new Expert(c.getString(c.getColumnIndex("expertAccount")),
					c.getString(c.getColumnIndex("expertName")), c.getString(c
							.getColumnIndex("expertDescription")),
					c.getString(c.getColumnIndex("expertImg")));
			experts.add(expert);
		}
		c.close();
		return experts;
	}

	public void deleteExpert(String userAccount) {
		db.execSQL("CREATE table IF NOT EXISTS _expert" + " "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "expertAccount TEXT," + "expertName TEXT,"
				+ "expertDescription TEXT," + "expertImg TEXT,"
				+ "saveuser TEXT)");
		db.execSQL("DELETE FROM _expert", new Object[] {});
	}

	// ����ũ��
	public String saveFarmer(Expert expert, String userAccount) {
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _farmer" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			if (expert != null) {
				db.execSQL(
						"insert into _farmer" + " (expertAccount,"
								+ "expertName," + "expertDescription,"
								+ "expertImg," + "saveuser) values(?,?,?,?,?)",
						new Object[] { expert.getExpertAccount(),
								expert.getExpertName(),
								expert.getExpertDescription(),
								expert.getExpertImg(), userAccount });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		System.out.println("ũ�񱣴�" + results);
		return results;
	}

	// ��ѯר���ǳ�
	public String getFarmerNickName(String farmerAccount, String userAccpunt) {
		String nickName = farmerAccount;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _farmer" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			Cursor c = db.rawQuery("SELECT * from _farmer"
					+ " WHERE expertAccount='" + farmerAccount
					+ "' AND saveuser='" + userAccpunt + "'", null);
			while (c.moveToNext()) {
				nickName = c.getString(c.getColumnIndex("expertName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			nickName = farmerAccount;
			System.out.println("ũ���ǳƳ�����");
		}
		System.out.println("ũ���ǳƣ�" + nickName);
		return nickName;
	}

	// �ж�ר���Ƿ��Ѿ�����
	public Boolean isFarmerSaved(Expert expert, String userAccount) {
		Boolean results = false;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _farmer" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			Cursor c = db.rawQuery("SELECT * from _farmer"
					+ " WHERE expertAccount='" + expert.getExpertAccount()
					+ "' AND saveuser='" + userAccount + "'", null);
			if (c.getCount() > 0) {
				results = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = false;
		}
		return results;
	}

	// ����ר��
	public String updateFarmers(Expert expert, String userAccount) {
		String result = "";
		try {
			db.execSQL("CREATE table IF NOT EXISTS _farmer" + " "
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "expertAccount TEXT," + "expertName TEXT,"
					+ "expertDescription TEXT," + "expertImg TEXT,"
					+ "saveuser TEXT)");
			Cursor c = db.rawQuery("SELECT * from _farmer"
					+ " WHERE expertAccount='" + expert.getExpertAccount()
					+ "' AND saveuser='" + userAccount + "'", null);
			if (c.getCount() > 0) {
				db.execSQL("UPDATE _farmer SET expertName='"
						+ expert.getExpertName() + "' WHERE expertAccount='"
						+ expert.getExpertAccount() + "' AND saveuser='"
						+ userAccount + "'");
			}
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			result = "failed";
		}
		return result;
	}

	public List<Expert> getAllFarmers(String userAccount) {
		Expert expert = null;
		List<Expert> experts = new ArrayList<Expert>();
		db.execSQL("CREATE table IF NOT EXISTS _farmer" + " "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "expertAccount TEXT," + "expertName TEXT,"
				+ "expertDescription TEXT," + "expertImg TEXT,"
				+ "saveuser TEXT)");
		Cursor c = db.rawQuery("SELECT * from _farmer WHERE saveuser='"
				+ userAccount + "'", null);
		System.out.println("ũ����Ϊ=" + c.getCount());
		while (c.moveToNext()) {
			expert = new Expert(c.getString(c.getColumnIndex("expertAccount")),
					c.getString(c.getColumnIndex("expertName")), c.getString(c
							.getColumnIndex("expertDescription")),
					c.getString(c.getColumnIndex("expertImg")));
			experts.add(expert);
		}
		c.close();
		return experts;
	}

	// �������
	public String saveFriends(Friends friends, String groupName,
			String userAccount) {
		String results = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _friends" + userAccount
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "groupName TEXT," + "friendsName TEXT,"
					+ "friendsJID TEXT," + "friendImg TEXT)");
			if (friends != null) {
				db.execSQL(
						"insert into _friends" + userAccount + " (groupName,"
								+ "friendsName," + "friendsJID,"
								+ "friendImg) values(?,?,?,?)",
						new Object[] { friends.getGroupName(),
								friends.getName(), friends.getJID(), "", });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		System.out.println("���ѱ���" + results);
		return results;
	}

	// ��ѯĳһ��������к���
	public List<Friends> getAllFriends(String groupName, String userAccount) {
		Friends friends = null;
		List<Friends> fList = new ArrayList<Friends>();
		db.execSQL("CREATE table IF NOT EXISTS _friends" + userAccount
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "groupName TEXT," + "friendsName TEXT," + "friendsJID TEXT,"
				+ "friendImg TEXT)");
		Cursor c = db.rawQuery("SELECT * from _friends" + userAccount
				+ " WHERE groupName='" + groupName + "'", null);
		System.out.println(groupName + "������Ϊ=" + c.getCount());
		while (c.moveToNext()) {
			friends = new Friends(c.getString(c.getColumnIndex("friendsName")),
					c.getString(c.getColumnIndex("friendsJID")), "", "",
					groupName, 0, 0, false);
			fList.add(friends);
		}
		c.close();
		return fList;
	}

	// ��ѯ�����б����Ƿ���ĳһ����
	public boolean friendIsInMyContact(String userAccount, String friendJID) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _friends" + userAccount
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "groupName TEXT," + "friendsName TEXT," + "friendsJID TEXT,"
				+ "friendImg TEXT)");
		Cursor c = db.rawQuery("SELECT * from _friends" + userAccount
				+ " WHERE friendsJID='" + friendJID + "'", null);
		if (c.getCount() > 0) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// �����û����ѷ���
	public String saveGroupName(String groupName, String userAccount) {
		String results = "";
		try {
			db.execSQL("CREATE table IF NOT EXISTS _friendsgroupName"
					+ userAccount + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "groupName TEXT)");
			if (groupName != null) {
				db.execSQL("insert into _friendsgroupName" + userAccount
						+ " (groupName) values(?)", new Object[] { groupName });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		System.out.println("�����û����飺" + results);
		return results;
	}

	// �����û����ѷ���
	public List<String> getAllGroupName(String userAccount) {
		String name = "";
		List<String> fList = new ArrayList<String>();
		db.execSQL("CREATE table IF NOT EXISTS _friendsgroupName" + userAccount
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "groupName TEXT)");
		Cursor c = db.rawQuery("SELECT * from _friendsgroupName" + userAccount,
				null);
		System.out.println("������Ϊ����" + c.getCount());
		while (c.moveToNext()) {
			name = c.getString(c.getColumnIndex("groupName"));
			fList.add(name);
		}
		c.close();
		return fList;
	}

	// ��ѯ���ѷ������Ƿ��Ѿ�����
	public boolean groupNameIsInMyContact(String userAccount, String groupName) {
		boolean result = false;
		db.execSQL("CREATE table IF NOT EXISTS _friendsgroupName" + userAccount
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "groupName TEXT)");
		Cursor c = db.rawQuery("SELECT * from _friendsgroupName" + userAccount
				+ " WHERE groupName='" + groupName + "'", null);
		if (c.getCount() > 0) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// ������ǩ�������ǩ
	public String saveTag(String tagName, int tagId) {
		String results = "";
		try {
			db.execSQL("CREATE table IF NOT EXISTS _tag"
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "tagId INTERGER, tagName TEXT)");
			if (tagName != null) {
				db.execSQL(
						"insert into _tag" + " (tagId, tagName) values(?,?)",
						new Object[] { tagId, tagName });
			}
			results = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			results = "failed";
		}
		System.out.println("�����ǩ�ɹ���" + results);
		return results;
	}

	// ��ȡ���б��ر�ǩ
	public List<HashMap<String, Object>> getTags() {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;
		try {
			db.execSQL("CREATE table IF NOT EXISTS _tag"
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "tagId INTERGER, tagName TEXT)");
			Cursor c = db.rawQuery("SELECT * FROM _tag", null);
			while (c.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("name", c.getString(c.getColumnIndex("tagName")));
				map.put("id", c.getInt(c.getColumnIndex("tagId")));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		for (int i = 0; i < list.size(); i++) {
			System.out.println("���ݿ��ǩ��" + list.get(i));
		}
		return list;
	}

	// ���±�ǩ��
	public String updateTags(List<HashMap<String, Object>> list) {
		String result = "";
		HashMap<String, Object> map = null;
		String tagName = "";
		int tagId = 0;
		try {
			db.execSQL("DROP table IF EXISTS _tag");
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				tagName = map.get("name").toString();
				tagId = Integer.parseInt(map.get("id").toString());
				System.out.println("tagName=" + tagName);
				System.out.println("tagId=" + tagId);
				saveTag(tagName, tagId);
			}
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			result = "false";
		}
		return result;
	}

}
