package com.xiaoguo.wasp.mobile.ui.weatherinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.Article;
import com.xiaoguo.wasp.mobile.model.ArticleComment;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.widget.Utility;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ArticleActivity extends Activity implements OnClickListener {
	private TextView titleView;
	private Button backView;
	private Button attentionView;
	private ImageView authorImgView;
	private TextView authorName;
	private TextView authorIntruduce;
	private TextView articleDate;
	private TextView articleLabel;
	private TextView articleBrief;
	private LinearLayout articleContent;

	private LinearLayout attachmentView;
	private ListView attachListView;

	private LinearLayout commentLayout;
	private ListView commentListView;

	SimpleAdapter attachAdapter;
	CommentAdapter commentAdapter;

	List<HashMap<String, Object>> attachList;
	List<HashMap<String, Object>> commentList;

	HashMap<String, Object> attachMap;
	HashMap<String, Object> commentMap;

	private LinearLayout good;
	private LinearLayout bad;
	private LinearLayout comment;
	private LinearLayout collect;
	private TextView goodNumView;
	private TextView badNumView;
	private TextView commentNumView;
	private TextView collectNumView;
	private LinearLayout LabelLayout;
	private LinearLayout briefLayout;

	EditText input;

	int badNum = 0;
	int goodNum = 0;
	int commentNum = 0;
	int collectNum = 0;
	ProgressDialog dialog = null;
	UserSettingInfo usInfo = null;

	boolean aggree = false;
	boolean disaggree = false;

	Bundle bundle = null;
	int article_id;
	Article article = null;
	String author_name = null;
	ProductDb productDb = null;
	String fromWhere = "";

	EditText myCommentView;
	Button submitView;
	TextView contentView;
	String publish_name = "";

	Utility utility = null;
	String text = "";
	MyBroadcastReceiver receiver = null;
	private Animation refreshAnimation;
	CommandBase commandBase=null;
	private Button refreshComment;
	int localCommentNum=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);

		WASPApplication.getInstance().addActivity(this);
		
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(ArticleActivity.this, ArticleActivity.this);
		
		utility = new Utility();
		productDb = new ProductDb(this);
		dialog = new ProgressDialog(this);
		usInfo = new UserSettingInfo(this);
		bundle = getIntent().getBundleExtra("bd");
		article_id = bundle.getInt("id");
		productDb.upDataIsRead(article_id, usInfo.getAccount());
		localCommentNum = productDb.getArticleComments(article_id).size();
		refreshAnimation = rotateAnimation();
		fromWhere = bundle.getString("fromwhere");
		if (!fromWhere.equals("disater") && !fromWhere.equals("tip")
				&& !fromWhere.equals("weather")) {
			publish_name = bundle.getString("user_name");
			System.out.println("作者" + publish_name);
		}
		System.out.println("来自" + fromWhere + "的文章详情");

		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		attentionView = (Button) findViewById(R.id.bt_right);
		attentionView.setBackground(ArticleActivity.this.getResources()
				.getDrawable(R.drawable.toolbar_right));
		attentionView.setVisibility(View.VISIBLE);
		// attentionView.setText("添加关注");
		// attentionView.setTextColor(getResources().getColor(R.color.white));
		attentionView.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText(bundle.getString("title"));
		backView = (Button) findViewById(R.id.bt_left);
		backView.setVisibility(View.VISIBLE);
		backView.setOnClickListener(this);

		authorImgView = (ImageView) findViewById(R.id.author_img);
		authorName = (TextView) findViewById(R.id.author_name);
		authorIntruduce = (TextView) findViewById(R.id.author_intruduce);
		articleDate = (TextView) findViewById(R.id.article_time);
		articleLabel = (TextView) findViewById(R.id.article_label);
		articleBrief = (TextView) findViewById(R.id.article_brief);
		articleContent = (LinearLayout) findViewById(R.id.article_content);
		contentView = (TextView) findViewById(R.id.contents);

		LabelLayout = (LinearLayout) findViewById(R.id.label_layout);
		briefLayout = (LinearLayout) findViewById(R.id.brief_layout);
		briefLayout.setVisibility(View.GONE);

		if (fromWhere.equals("disater")) {
			LabelLayout.setVisibility(View.GONE);
		}

		if (fromWhere.equals("users")) {
//			attentionView.setVisibility(View.INVISIBLE);
			LabelLayout.setVisibility(View.GONE);
			briefLayout.setVisibility(View.GONE);
		} else if (!publish_name.equals("")
				&& publish_name.equals(usInfo.getAccount())) {
//			attentionView.setVisibility(View.INVISIBLE);
		} else {
//			attentionView.setVisibility(View.INVISIBLE);
		}

		// 有附件的时候才显示
		attachmentView = (LinearLayout) findViewById(R.id.attachment);
		attachListView = (ListView) findViewById(R.id.floder_list);

		good = (LinearLayout) findViewById(R.id.good);
		good.setOnClickListener(this);
		good.setVisibility(View.GONE);
		goodNumView = (TextView) findViewById(R.id.good_num);

		bad = (LinearLayout) findViewById(R.id.bad);
		bad.setVisibility(View.GONE);
		bad.setOnClickListener(this);
		badNumView = (TextView) findViewById(R.id.bad_num);

		comment = (LinearLayout) findViewById(R.id.comment);
		comment.setVisibility(View.GONE);
		comment.setOnClickListener(this);
		commentNumView = (TextView) findViewById(R.id.comment_num);
		commentNumView.setVisibility(View.GONE);

		collect = (LinearLayout) findViewById(R.id.collection);
		collect.setOnClickListener(this);
		collectNumView = (TextView) findViewById(R.id.collection_num);

		// 获取评论列表
		commentLayout = (LinearLayout) findViewById(R.id.comment_layout);
		commentLayout.setVisibility(View.VISIBLE);
		refreshComment = (Button)findViewById(R.id.comment_more);
		refreshComment.setOnClickListener(this);
		commentListView = (ListView) findViewById(R.id.comment_list);

		myCommentView = (EditText) findViewById(R.id.my_comment);
		submitView = (Button) findViewById(R.id.sub_comment);
		submitView.setOnClickListener(this);

		commentList = new ArrayList<HashMap<String, Object>>();

		Article dispalyArticle = new Article();
		if (article_id == 0) {
			String title = bundle.getString("name");
			String content = bundle.getString("content");
			String author = bundle.getString("author");
			String time = bundle.getString("time");
			dispalyArticle.setArticle_content(content);
			dispalyArticle.setArticle_publish_date(time);
			dispalyArticle.setArticle_user_name(author);
			dispalyArticle.setArticle_title(title);
		} else {
			dispalyArticle = productDb.getOneArticle(article_id);
		}
		System.out.println("displayArticle=" + dispalyArticle);
		if (dispalyArticle != null) {
			List<ArticleComment> comments = new ArrayList<ArticleComment>();
			if (article_id == 0) {

			} else {
				comments = productDb.getArticleComments(article_id);
			}
			/*if (commentList.size() > 0) {
				HashMap<String, Object> last = commentList.get(commentList
						.size() - 1);
				String comm = last.get("comment").toString();
				if (comm.equals("更多评论...")) {
					commentList.remove(commentList.size() - 1);
				}
			}*/
			ArticleComment comment = null;
			for (int i = 0; i < comments.size(); i++) {
				comment = comments.get(i);
				commentMap = new HashMap<String, Object>();
				commentMap.put("comment", comment.getComment_name() + ":"
						+ comment.getComment_content());
				commentList.add(commentMap);
			}
			commentAdapter = new CommentAdapter(commentList,
					ArticleActivity.this);
			commentListView.setAdapter(commentAdapter);
			utility.setListViewHeightBasedOnChildren(commentListView);

			titleView.setText(dispalyArticle.getArticle_title());
			authorName.setText(dispalyArticle.getArticle_user_name());
			articleDate.setText(TimeUtil.getTimeUtilInstance().TimeStamp2Date(
					dispalyArticle.getArticle_publish_date()));
			articleLabel.setText(dispalyArticle.getArticle_tag_name());
			articleBrief.setText(dispalyArticle.getArticle_summary());
			contentView.setText(dispalyArticle.getArticle_content());
		} else {
			commandBase.readCache(new TaskListener() {
				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {

				}

				@Override
				public void start() {
					dialog.setMessage("正在请求数据，请稍后...");
					dialog.show();
				}

				@Override
				public String requestUrl() {
					if (fromWhere.equals("disater")) {
						return "warningdetail";
					} else if (fromWhere.equals("tip")) {
						return "tipDetails";
					} else {
						return "articleselect";
					}
				}

				@Override
				public JSONObject requestData() {
					JSONObject object = new JSONObject();
					try {
						if (fromWhere.equals("disater")) {
							object.put("article_id", article_id);
						} else if (fromWhere.equals("tip")) {
							object.put("article_id", article_id);
						} else {
							object.put("articleid", article_id);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						object = null;
					}
					return object;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					System.out.println("msg=" + msg);
					try {
						JSONObject object = msg.getJSONObject("data");
						if (fromWhere.equals("disater")
								|| fromWhere.equals("tip")) {
							object = object.getJSONObject("article");
						} else {
							object = object.getJSONObject("item");
						}
						article = new Article();
						article.setArticle_id(object.getInt("article_id"));
						article.setArticle_content(object
								.getString("article_content"));
						article.setArticle_like(object.getInt("article_like"));
						if (fromWhere.equals("tip")) {
							article.setArticle_tag_name(object
									.getString("article_tag_name"));
						}
						String publish = object
								.getString("article_publish_date");
						if (!publish.equals("") && publish != null) {
							article.setArticle_publish_date(object
									.getJSONObject("article_publish_date")
									.getString("time"));
						}
						article.setArticle_reject(object
								.getInt("article_reject"));
						article.setArticle_summary(object
								.getString("article_summary"));
						article.setArticle_tag_name(object
								.getString("article_tag_name"));
						article.setArticle_title(object
								.getString("article_title"));
						article.setArticle_type(object.getInt("article_type"));
						article.setArticle_user_name(object
								.getString("article_user_name"));

						productDb.saveArticle(article, usInfo.getAccount());

						titleView.setText(article.getArticle_title());
						if (fromWhere.equals("weather")
								|| fromWhere.equals("tip")
								|| fromWhere.equals("disater")) {
							authorName.setText(article.getArticle_user_name()
									+ "");
						} else {
							authorName.setText(publish_name);
						}
						articleDate.setText(TimeUtil.getTimeUtilInstance()
								.TimeStamp2Date(
										article.getArticle_publish_date()));
						articleLabel.setText(article.getArticle_tag_name());
						articleBrief.setText(article.getArticle_summary());
						contentView.setText(article.getArticle_content());
						goodNumView.setText(article.getArticle_like() + "");
						badNumView.setText(article.getArticle_reject() + "");
						goodNum = article.getArticle_like();
						badNum = article.getArticle_reject();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void finish() {
					dialog.dismiss();
					if (!fromWhere.equals("weather")) {
						getComment("more",0, 5);
					}
				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {
				}

				@Override
				public String contentype() {
					return null;
				}
			});
		}
		/*commentListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView textView = (TextView) arg1
						.findViewById(R.id.simple_spinner_tx);
				String str = textView.getText().toString();
				if (str.equals("更多评论...")) {
					// 每次获取5条
					getComment("more",commentList.size() - 1, 5);
				}
			}
		});*/
	}

	private void getComment(final String type, final int offset, final int rowcount) {
		commandBase.readCache(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
			}
			@Override
			public void start() {
			}
			@Override
			public String requestUrl() {
				return "articlecommentlistselect";
			}
			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("articlecomment_article_id", article_id);
					object.put("offset", offset);
					object.put("rowcount", rowcount);
				} catch (JSONException e) {
					e.printStackTrace();
					object = null;
				}
				return object;
			}
			@Override
			public String readCache() {
				return null;
			}
			@Override
			public boolean needCacheTask() {
				return false;
			}
			@Override
			public void messageUpdated(JSONObject msg) {
				System.out.println("评论结果=" + msg);
				ArticleComment comment = null;
				try {
					JSONObject object = msg.getJSONObject("data");
					JSONArray array = object.getJSONArray("item");
					System.out.println("评论条数：" + array.length());
					if(array.length()<1 && "refesh".equals(type)){
						Toast.makeText(ArticleActivity.this, "没有更多评论了", Toast.LENGTH_SHORT).show();
					}
					/*if (commentList.size() > 0) {
						HashMap<String, Object> last = commentList
								.get(commentList.size() - 1);
						String comm = last.get("comment").toString();
						if (comm.equals("更多评论...")) {
							commentList.remove(commentList.size() - 1);
						}
					} else {
					}
*/
					for (int i = 0; i < array.length(); i++) {
						object = array.getJSONObject(i);
						commentMap = new HashMap<String, Object>();
						commentMap.put(
								"comment",
								object.getString("articlecomment_user_name")
										+ ":"
										+ object.getString("articlecomment_content"));
						comment = new ArticleComment(article_id, 0, "", object
								.getString("articlecomment_user_name"), object
								.getString("articlecomment_content"));
						System.out.println("xxxxxxxxxx");
						commentList.add(commentMap);
						productDb.saveArticleComment(comment,usInfo.getAccount());
					}
					System.out.println("列表长度=" + commentList.size());
					commentAdapter = new CommentAdapter(commentList,
							ArticleActivity.this);
					commentListView.setAdapter(commentAdapter);
					utility.setListViewHeightBasedOnChildren(commentListView);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void finish() {
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
			}

			@Override
			public String contentype() {
				return null;
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_right:
			attentionView.startAnimation(refreshAnimation);
			commandBase.readCache(new TaskListener() {
				@Override
				public void updateCacheDate(
						List<HashMap<String, Object>> cacheData) {
				}
				@Override
				public void start() {
				}

				@Override
				public String requestUrl() {
					if (fromWhere.equals("disater")) {
						return "warningdetail";
					} else if (fromWhere.equals("tip")) {
						return "tipDetails";
					} else {
						return "articleselect";
					}
				}

				@Override
				public JSONObject requestData() {
					JSONObject object = new JSONObject();
					try {
						if (fromWhere.equals("disater")) {
							object.put("article_id", article_id);
						} else if (fromWhere.equals("tip")) {
							object.put("article_id", article_id);
						} else {
							object.put("articleid", article_id);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						object = null;
					}
					return object;
				}

				@Override
				public String readCache() {
					return null;
				}

				@Override
				public boolean needCacheTask() {
					return false;
				}

				@Override
				public void messageUpdated(JSONObject msg) {
					System.out.println("msg=" + msg);
					try {
						JSONObject object = msg.getJSONObject("data");
						if (fromWhere.equals("disater")
								|| fromWhere.equals("tip")) {
							object = object.getJSONObject("article");
						} else {
							object = object.getJSONObject("item");
						}
						article = new Article();
						article.setArticle_id(object.getInt("article_id"));
						article.setArticle_content(object
								.getString("article_content"));
						article.setArticle_like(object.getInt("article_like"));
						if (fromWhere.equals("tip")) {
							article.setArticle_tag_name(object
									.getString("article_tag_name"));
						}
						String publish = object
								.getString("article_publish_date");
						if (!publish.equals("") && publish != null) {
							article.setArticle_publish_date(object
									.getJSONObject("article_publish_date")
									.getString("time"));
						}
						article.setArticle_reject(object
								.getInt("article_reject"));
						article.setArticle_summary(object
								.getString("article_summary"));
						article.setArticle_tag_name(object
								.getString("article_tag_name"));
						article.setArticle_title(object
								.getString("article_title"));
						article.setArticle_type(object.getInt("article_type"));
						article.setArticle_user_name(object
								.getString("article_user_name"));

						productDb.saveArticle(article, usInfo.getAccount());

						titleView.setText(article.getArticle_title());
						if (fromWhere.equals("weather")
								|| fromWhere.equals("tip")
								|| fromWhere.equals("disater")) {
							authorName.setText(article.getArticle_user_name()
									+ "");
						} else {
							authorName.setText(publish_name);
						}
						articleDate.setText(TimeUtil.getTimeUtilInstance()
								.TimeStamp2Date(
										article.getArticle_publish_date()));
						articleLabel.setText(article.getArticle_tag_name());
						articleBrief.setText(article.getArticle_summary());
						contentView.setText(article.getArticle_content());
						goodNumView.setText(article.getArticle_like() + "");
						badNumView.setText(article.getArticle_reject() + "");
						goodNum = article.getArticle_like();
						badNum = article.getArticle_reject();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void finish() {
					attentionView.clearAnimation();
					if (!fromWhere.equals("weather")) {
//						getComment("refresh",0, 5);
						List<ArticleComment> comments = new ArrayList<ArticleComment>();
						commentList = new ArrayList<HashMap<String,Object>>();
						if (article_id == 0) {

						} else {
							comments = productDb.getArticleComments(article_id);
							System.out.println("本地评论："+comments.size());
						}
						if(comments.size()>0){
							ArticleComment comment = null;
							for (int i = 0; i < comments.size(); i++) {
								comment = comments.get(i);
								commentMap = new HashMap<String, Object>();
								commentMap.put("comment", comment.getComment_name() + ":"
										+ comment.getComment_content());
								commentList.add(commentMap);
							}
							commentAdapter = new CommentAdapter(commentList,
									ArticleActivity.this);
							commentListView.setAdapter(commentAdapter);
							utility.setListViewHeightBasedOnChildren(commentListView);
						}else{
							getComment("more", 0, 5);
						}
					}
				}

				@Override
				public String filepath() {
					return null;
				}

				@Override
				public void failure(String str) {
				}

				@Override
				public String contentype() {
					return null;
				}
			});
			break;
		case R.id.comment_more:
			getComment("refesh",commentList.size(), 5);
			break;
		case R.id.bt_left:
			System.out.println("点击了返回按钮的");
			ArticleActivity.this.setResult(1);
			this.finish();

			break;
		case R.id.sub_comment:
			String conmmentStr = myCommentView.getText().toString();
			if (conmmentStr.equals("") || conmmentStr == null) {
				Toast.makeText(ArticleActivity.this, "评论信息不能为空！",
						Toast.LENGTH_SHORT).show();
			} else {
				comment(conmmentStr);
			}
			break;
		case R.id.good:
			String goodStr = goodNumView.getText().toString().replace("(", "");
			goodStr = goodStr.replace(")", "");
			goodNum = Integer.parseInt(goodStr);
			System.out.println("good=" + goodStr);
			if (aggree) {
				// 取消赞
				aggree = false;
				goodNum--;
				System.out.println("good-->" + 111);
			} else {
				System.out.println("good-->" + 222);
				aggree = true;
				goodNum++;
			}
			goodNumView.setText("(" + goodNum + ")");
			break;
		case R.id.bad:
			String badStr = badNumView.getText().toString().replace("(", "");
			badStr = badStr.replace(")", "");
			badNum = Integer.parseInt(badStr);
			System.out.println("bad=" + badStr);
			if (disaggree) {
				disaggree = false;
				badNum--;
				System.out.println("bad-->" + 111);
			} else {
				System.out.println("bad-->" + 222);
				disaggree = true;
				badNum++;
			}
			badNumView.setText("(" + badNum + ")");
			break;
		case R.id.comment:
			Toast.makeText(ArticleActivity.this, "开发中...", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.collection:
			collectNum++;
			collectNumView.setText("(" + collectNum + ")");
			break;
		default:
			break;
		}
	}

	private void comment(final String conmmentStr) {
		commandBase.readCache(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {

			}

			@Override
			public void start() {
				dialog.setMessage("正在提交数据，请稍后...");
				dialog.show();
			}

			@Override
			public String requestUrl() {
				return "articlecommentinsert";
			}

			@Override
			public JSONObject requestData() {
				JSONObject object = new JSONObject();
				try {
					object.put("articlecomment_article_id", article_id);
					object.put("articlecomment_content", conmmentStr);
					object.put("user_name", usInfo.getAccount());
					object.put("offset", 1);
					object.put("rowcount", 4);
				} catch (JSONException e) {
					e.printStackTrace();
					object = null;
				}
				return object;
			}

			@Override
			public String readCache() {
				return null;
			}

			@Override
			public boolean needCacheTask() {
				return false;
			}

			@Override
			public void messageUpdated(JSONObject msg) {
				System.out.println("发表评论结果=" + msg);
				String result = "";
				try {
					JSONObject object = msg.getJSONObject("error");
					result = object.getString("string");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Toast.makeText(ArticleActivity.this, result, Toast.LENGTH_SHORT)
						.show();
				myCommentView.setText("");
			}

			@Override
			public void finish() {
				dialog.dismiss();
				getComment("more",commentList.size(), 1);
			}

			@Override
			public String filepath() {
				return null;
			}

			@Override
			public void failure(String str) {
			}

			@Override
			public String contentype() {
				return null;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				Bundle b = data.getBundleExtra("bd");
				int flag = b.getInt("flag");
				if (flag > 0) {
					commentNum += commentNum;
					commentNumView.setText("(" + commentNum + ")");
				}
				break;
			default:
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	private class CommentAdapter extends BaseAdapter {
		private List<HashMap<String, Object>> items = null;
		private HashMap<String, Object> map = null;
		private Context context;

		public CommentAdapter(List<HashMap<String, Object>> items,
				Context context) {
			super();
			this.items = items;
			System.out.println("评论的长度是：" + items.size());
			for (int i = 0; i < items.size(); i++) {
				System.out.println(items.get(i));
			}
			/*if ((items.size() != 0) && (items.size() % 5 == 0)) {
				// if((items.size()!=0) && (items.size()>=5)){
				map = new HashMap<String, Object>();
				map.put("comment", "更多评论...");
				items.add(map);
			}*/
			this.context = context;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			HashMap<String, Object> tempMap = items.get(arg0);
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			v = _LayoutInflater.inflate(R.layout.my_simple_spinner_2, null);
			if (v != null) {
				TextView label = (TextView) v
						.findViewById(R.id.simple_spinner_tx);
				label.setText("" + tempMap.get("comment"));
			}
			return v;
		}

	}

//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(ArticleActivity.this);
//		IntentFilter filter = new IntentFilter();
//
//		filter.addAction(Constant.ROSTER_ADDED);
//		filter.addAction(Constant.ROSTER_DELETED);
//		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
//		filter.addAction(Constant.ROSTER_UPDATED);
//		// 好友请求
//		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
//		filter.addAction(Constant.NEW_MESSAGE_ACTION);
//		registerReceiver(receiver, filter);
//		super.onResume();
//	}

	public Animation rotateAnimation() {
		Animation aa = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		aa.setInterpolator(new LinearInterpolator());
		aa.setRepeatCount(1000);
		aa.setFillBefore(true);
		aa.setDuration(2000);
		return aa;
	}

}
