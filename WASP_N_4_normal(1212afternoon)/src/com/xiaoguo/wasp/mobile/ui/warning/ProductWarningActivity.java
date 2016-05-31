package com.xiaoguo.wasp.mobile.ui.warning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.viewbadger.BadgeView;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.ArticleTitle;
import com.xiaoguo.wasp.mobile.model.UpdateTimeInfo;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.model.WeatherInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.weatherinfo.ArticleActivity;
import com.xiaoguo.wasp.mobile.ui.weatherinfo.WriteDisterActivity;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

public class ProductWarningActivity extends Activity implements OnClickListener {
	PullDownView mPullDownView;
	ListView mListView;
	private List<ArticleTitle> list2 = null;
	UserSettingInfo userInfo = null;
	ProductDb productDb = null;
	UpdateTimeInfo timeInfo = null;
	ProgressDialog dialog = null;
	WeatherInfo weatherInfo = null;
	WarnAdapter adapter2;
	BadgeView badeView;
	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_REFRESH_3 = 4;
	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE_3 = 5;

	private static final int WHAT_DID_MORE = 6;
	private Button backView;
	private TextView titleView;
	private Button writeView;
	String userType = "";
	MyBroadcastReceiver receiver = null;

	String tempTime = "";// 用来存放此时获取的最新一条数据的发布时间
	int tempLength = 0;// 保存服务器请求的数据的长度
	int localTempLength = 0;
	boolean hasNew = false;
	CommandBase commandBase = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.produce_cue);

		WASPApplication.getInstance().addActivity(this);

		userInfo = new UserSettingInfo(this);
		productDb = new ProductDb(this);
		timeInfo = new UpdateTimeInfo(ProductWarningActivity.this,
				userInfo.getAccount());
		dialog = new ProgressDialog(ProductWarningActivity.this);
		weatherInfo = new WeatherInfo(ProductWarningActivity.this,
				userInfo.getAccount());
		userType = userInfo.getType();
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(ProductWarningActivity.this,
				ProductWarningActivity.this);

		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			list2 = new ArrayList<ArticleTitle>();
			list2 = productDb.getArticleNames(4, userInfo.getAccount(), 0, 10);
			localTempLength = list2.size();
			if (list2.size() > 0) {
				if (list2.size() <= 0) {
					Toast.makeText(ProductWarningActivity.this, "没有生产提示信息!",
							Toast.LENGTH_SHORT).show();
				}
			}
			adapter2 = new WarnAdapter(ProductWarningActivity.this, list2, 3);
			mListView.setAdapter(adapter2);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		backView = (Button) findViewById(R.id.bt_left);
//		backView.setVisibility(View.VISIBLE);
//		backView.setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("生产提示");

		writeView = (Button) findViewById(R.id.bt_right);
		writeView.setBackgroundResource(R.drawable.btn_add);
		if (userType != null && !userType.equals("")
				&& !userType.equals("farmer")) {
			writeView.setVisibility(View.VISIBLE);
		} else {
			writeView.setVisibility(View.INVISIBLE);
		}
		writeView.setOnClickListener(this);

		mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);
		mListView = mPullDownView.getListView();
		mListView.setDividerHeight(25);
		mListView.setDivider(getResources().getDrawable(R.drawable.white));
		mListView.setBackgroundColor(getResources().getColor(R.color.white));
		mListView.setVerticalScrollBarEnabled(false);

		// 先从本地数据库读取10条数据
		list2 = new ArrayList<ArticleTitle>();
		list2 = productDb.getArticleNames(4, userInfo.getAccount(), 0, 10);
		localTempLength = list2.size();
		if (list2.size() > 0) {
			if (list2.size() <= 0) {
				Toast.makeText(ProductWarningActivity.this, "没有生产提示信息!",
						Toast.LENGTH_SHORT).show();
			}
			adapter2 = new WarnAdapter(ProductWarningActivity.this, list2, 3);
			mListView.setAdapter(adapter2);
			mPullDownView.notifyDidMore();
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					ArticleTitle article = list2.get(arg2 - 1);
					productDb.upDataIsRead(article.getArticle_id(),
							userInfo.getAccount());
					Intent i = new Intent();
					i.setClass(ProductWarningActivity.this,
							ArticleActivity.class);
					Bundle bd = new Bundle();
					bd.putString("title", article.getArticle_title());
					bd.putString("fromwhere", "tip");
					bd.putInt("id", article.getArticle_id());
					i.putExtra("bd", bd);
					// startActivity(i);
					startActivityForResult(i, 1);
				}
			});

			mPullDownView.setOnPullDownListener(new OnPullDownListener() {
				@Override
				public void onRefresh() {
					// 下拉的时候获取服务器最先消息
					getArtices(3, "refresh");
				}

				@Override
				public void onMore() {
					System.out.println("hasNew=" + hasNew);
					if (hasNew) {
						getArtices(3, "more");
					} else {
						// 从本地获取5条数据
						List<ArticleTitle> tempTitles = productDb
								.getArticleNames(4, userInfo.getAccount(),
										localTempLength, 5);
						localTempLength = localTempLength + tempTitles.size();
						mPullDownView.notifyDidMore();
						Message message = mUIHandler
								.obtainMessage(WHAT_DID_MORE_3);
						message.obj = tempTitles;
						message.sendToTarget();
					}
				}
			});

			/* mListView.setAdapter(mAdapter); */
			// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
			mPullDownView.enableAutoFetchMore(true, 1);
			// 隐藏 并禁用尾部
			mPullDownView.setHideFooter();
			// 显示并启用自动获取更多
			mPullDownView.setShowFooter();
			// 隐藏并且禁用头部刷新
			mPullDownView.setHideHeader();
			// 显示并且可以使用头部刷新
			mPullDownView.setShowHeader();
			// 加载数据 本类使用
		} else {
			getArtices(3, "loaddata");
		}

	}

	// 自定义Adapter
	private class WarnAdapter extends BaseAdapter {
		private List<ArticleTitle> articles;
		private Context context;
		private int i = 0;

		public WarnAdapter(Context context, List<ArticleTitle> articles, int i) {
			super();
			this.articles = articles;
			this.context = context;
			this.i = i;
		}

		@Override
		public int getCount() {
			return articles.size();
		}

		@Override
		public Object getItem(int arg0) {
			return articles.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			ArticleTitle article = articles.get(arg0);
			System.out.println(article);
			LayoutInflater _LayoutInflater = LayoutInflater.from(context);
			if (i == 2) {
				v = _LayoutInflater.inflate(R.layout.warning_item, null);
				if (v != null) {
					TextView time = (TextView) v.findViewById(R.id.time);
					time.setText(TimeUtil.getTimeUtilInstance().TimeStamp2Date(
							article.getArticle_publish_date()));
					TextView title = (TextView) v.findViewById(R.id.title);
					title.setText(article.getArticle_title());
					TextView content = (TextView) v.findViewById(R.id.content);
					content.setText(article.getArticle_content());
				}
			} else {
				v = _LayoutInflater.inflate(R.layout.expert_page_title, null);
				if (v != null) {
					ImageView img = (ImageView) v.findViewById(R.id.expert_img);

					img.setImageResource(R.drawable.head_default_yixin);
					TextView name = (TextView) v.findViewById(R.id.expert_name);
					name.setText(article.getArticle_publish_name() + "");
					TextView time = (TextView) v
							.findViewById(R.id.expert_page_time);
					time.setText(TimeUtil.getTimeUtilInstance().TimeStamp2Date(
							article.getArticle_publish_date()));
					TextView title = (TextView) v
							.findViewById(R.id.expert_page_title);
					title.setText(article.getArticle_title());
					TextView tagName = (TextView) v
							.findViewById(R.id.title_tag);
					System.out.println("文章标签：" + article.getArticle_tag());
					tagName.setText(article.getArticle_tag());
					TextView content = (TextView) v
							.findViewById(R.id.expert_page_content);
					content.setText(article.getArticle_content());
					TextView isRead = (TextView) v
							.findViewById(R.id.tv_is_read);

					if (0 == article.getIs_read()) {

						isRead.setVisibility(View.VISIBLE);

					} else {

						isRead.setVisibility(View.GONE);
					}

				}
			}
			return v;
		}

	}

	private List<ArticleTitle> getArtices(final int i5, final String style) {
		System.out.println("当前进入的view为：" + i5);
		System.out.println("list2联网前的长度：" + list2.size());
		final List<ArticleTitle> articles = new ArrayList<ArticleTitle>();
		commandBase.request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
			}

			@Override
			public void start() {
				if (style.equals("loaddata")) {
					dialog.setMessage("正在请求数据,请稍后...");
					dialog.show();
				}
			}

			@Override
			public String requestUrl() {
				return "disasterwarning";
			}

			@Override
			public JSONObject requestData() {
				int limit = 0;
				int offset = 10;
				int tipLimit = 0;
				int tipOffset = 10;
				if (style.equals("more")) {
					limit = 0;
					offset = 0;
					if (tempTime == null || tempTime.equals("")) {
						tipLimit = list2.size();
					} else {
						tipLimit = tempLength;
						System.out.println("tempLength=" + tempLength);
					}
					tipOffset = 5;
				} else if (style.equals("refresh")) {
					limit = 0;
					offset = 0;
					tipLimit = 0;
					tipOffset = 5;
				} else {
					limit = 0;
					offset = 0;
					tipLimit = 0;
					tipOffset = 10;
				}
				JSONObject object = new JSONObject();
				JSONObject tempJsonObject = new JSONObject();
				try {
					object.put("limit", limit);
					object.put("offset", offset);
					object.put("tipLimit", tipLimit);
					object.put("tipOffset", tipOffset);
					if (style.equals("refresh")) {
						tempJsonObject.put("direction", "asc");
						object.put("updatedate", timeInfo.getGuidArticleTime());
					} else {
						tempJsonObject.put("direction", "desc");
						object.put("updatedate", "");
					}
					object.put("sort", tempJsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
					object = null;
				}
				System.out.println("object=" + object);
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
				System.out.println("list2 联网成功后的长度：" + list2.size());
				ArticleTitle article = null;
				List<ArticleTitle> tipList = new ArrayList<ArticleTitle>();
				ArticleTitle tip = null;
				try {
					JSONObject object1 = msg.getJSONObject("data");
					JSONArray array = (JSONArray) object1.get("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = new JSONObject();
						object = (JSONObject) array.get(i);
						article = new ArticleTitle();
						article.setArticle_id(object.getInt("article_id"));
						article.setArticle_title(object
								.getString("article_title"));
						String publish = object
								.getString("article_publish_date");
						if (publish != null && !publish.equals("")) {
							article.setArticle_publish_date(object
									.getJSONObject("article_publish_date")
									.getString("time"));
						}
						article.setArticle_publish_name(object
								.getString("article_user_name"));
						article.setArticle_content(object
								.getString("article_content"));
						article.setArticle_browser_count(object
								.getInt("article_browser_count"));
						int type = object.getInt("article_type");
						article.setArticle_type(type);
						article.setSave_userid(userInfo.getAccount());
						article.setIs_read(0);
						System.out.println("article=" + article.toString());
						if (!productDb.isArticleSaved(
								object.getInt("article_id"),
								userInfo.getAccount())) {
							productDb.saveArticleName(article);
							articles.add(article);
						}
					}
					JSONArray tips = (JSONArray) object1.get("tip");
					System.out.println("tip长度：" + tips.length());
					for (int j = 0; j < tips.length(); j++) {
						object1 = tips.getJSONObject(j);
						tip = new ArticleTitle();
						tip.setArticle_id(object1.getInt("article_id"));
						tip.setArticle_title(object1.getString("article_title"));
						System.out.println("服务器返回标签："
								+ object1.getString("article_tag_name"));
						tip.setArticle_tag(object1
								.getString("article_tag_name"));
						String publish = object1
								.getString("article_publish_date");
						if (publish != null && !publish.equals("")) {
							tip.setArticle_publish_date(object1.getJSONObject(
									"article_publish_date").getString("time"));
						}
						tip.setArticle_publish_name(object1
								.getString("article_user_name"));
						tip.setArticle_content(object1
								.getString("article_content"));
						tip.setArticle_browser_count(object1
								.getInt("article_browser_count"));
						tip.setIs_read(0);
						int type1 = object1.getInt("article_type");
						tip.setArticle_type(type1);
						tip.setSave_userid(userInfo.getAccount());
						System.out.println("tip=" + tip.toString());
						if (!productDb.isArticleSaved(
								object1.getInt("article_id"),
								userInfo.getAccount())) {
							String result = productDb.saveArticleName(tip);

							System.out.println("生产提示的数据是否保存成功了--->>" + result);

							tipList.add(tip);
						}
						System.out.println("11");

						if (!style.equals("refresh")) {
							Long publishDate = 0L;
							if (tipList.size() > 0) {
								publishDate = Long.parseLong(tipList.get(
										tipList.size() - 1)
										.getArticle_publish_date());
							}
							Long tempDate;
							if (tempTime == null || tempTime.equals("")) {
								tempDate = 0L;
							} else {
								tempDate = Long.parseLong(tempTime);
							}
							if (publishDate > tempDate) {
								hasNew = true;
							}
						}
					}
					System.out.println("list2 联网后的长度：" + list2.size());
					int tipSize = tipList.size();
					if (tipSize >= 1) {
						weatherInfo.setGuidId1(tipList.get(0).getArticle_id());
						weatherInfo.setGuidTitle1(tipList.get(0)
								.getArticle_title());
						if (tipSize >= 2) {
							weatherInfo.setGuidId2(tipList.get(1)
									.getArticle_id());
							weatherInfo.setGuidTitle2(tipList.get(1)
									.getArticle_title());
							if (tipSize >= 3) {
								weatherInfo.setGuidId3(tipList.get(2)
										.getArticle_id());
								weatherInfo.setGuidTitle3(tipList.get(2)
										.getArticle_title());
								if (tipSize >= 4) {
									weatherInfo.setGuidId4(tipList.get(3)
											.getArticle_id());
									weatherInfo.setGuidTitle4(tipList.get(3)
											.getArticle_title());
									if (tipSize >= 5) {
										weatherInfo.setGuidId5(tipList.get(4)
												.getArticle_id());
										weatherInfo.setGuidTitle5(tipList
												.get(4).getArticle_title());
									}
								}
							}
						}
					} else {
					}
					Message message = null;
					if (style.equals("refresh")) {
						if (tipList.size() > 0) {
							timeInfo.setGuidArticleTime(tipList.get(
									tipList.size() - 1)
									.getArticle_publish_date());
						}
						mPullDownView.RefreshComplete();
						message = mUIHandler.obtainMessage(WHAT_DID_REFRESH_3);
						Collections.reverse(tipList);
						message.obj = tipList;
						message.sendToTarget();
					}
					if (style.equals("more")) {
						mPullDownView.notifyDidMore();
						message = mUIHandler.obtainMessage(WHAT_DID_MORE_3);
						message.obj = tipList;
						message.sendToTarget();
					}
					if (style.equals("loaddata")) {
						if (tipList.size() > 0) {
							timeInfo.setGuidArticleTime(tipList.get(0)
									.getArticle_publish_date());
						}
						System.out.println("View 3 tipList长度：" + tipList.size());
						list2 = tipList;
						for (int i = 0; i < list2.size(); i++) {
							System.out.println(list2.get(i)
									.getArticle_publish_date());
						}
						System.out.println(timeInfo.getGuidArticleTime());
						System.out
								.println("---------------------------------------------");
						for (int i = 0; i < list2.size(); i++) {
							System.out.println(list2.get(i)
									.getArticle_publish_date());
						}
						System.out.println("View 3 List2长度：" + tipList.size());
						adapter2 = new WarnAdapter(ProductWarningActivity.this,
								list2, 3);
						mListView.setAdapter(adapter2);
						mListView.setSelection(list2.size() - 1);
						mPullDownView.notifyDidMore();
						mPullDownView.RefreshComplete();
						if (list2.size() <= 0) {
							Toast.makeText(ProductWarningActivity.this,
									"没有生产提示信息!", Toast.LENGTH_SHORT).show();
						}
						adapter2 = new WarnAdapter(ProductWarningActivity.this,
								list2, 3);
						mListView.setAdapter(adapter2);
						mListView
								.setOnItemClickListener(new OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										ArticleTitle article = list2
												.get(arg2 - 1);
										Intent i = new Intent();
										i.setClass(ProductWarningActivity.this,
												ArticleActivity.class);
										Bundle bd = new Bundle();
										bd.putString("title",
												article.getArticle_title());
										bd.putString("fromwhere", "tip");
										bd.putInt("id", article.getArticle_id());
										i.putExtra("bd", bd);
										startActivity(i);
									}
								});
						mPullDownView
								.setOnPullDownListener(new OnPullDownListener() {
									@Override
									public void onRefresh() {
										getArtices(3, "refresh");
									}

									@Override
									public void onMore() {
										if (hasNew) {
											getArtices(3, "more");
										} else {
											// 从本地获取5条数据
											List<ArticleTitle> tempTitles = productDb
													.getArticleNames(
															4,
															userInfo.getAccount(),
															localTempLength, 5);
											localTempLength = localTempLength
													+ tempTitles.size();
											mPullDownView.notifyDidMore();
											Message message = mUIHandler
													.obtainMessage(WHAT_DID_MORE_3);
											message.obj = tempTitles;
											message.sendToTarget();
										}
									}
								});

						// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
						mPullDownView.enableAutoFetchMore(true, 1);
						// 隐藏 并禁用尾部
						mPullDownView.setHideFooter();
						// 显示并启用自动获取更多
						mPullDownView.setShowFooter();
						// 隐藏并且禁用头部刷新
						mPullDownView.setHideHeader();
						// 显示并且可以使用头部刷新
						mPullDownView.setShowHeader();
						// 加载数据 本类使用
					}
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("解析错误");
				}
			}

			@Override
			public void finish() {
				if (style.equals("loaddata")) {
					dialog.dismiss();
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
		return articles;
	}

	// 更新UI线程处理器
	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_REFRESH_3: {
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles = (List<ArticleTitle>) msg.obj;
				tempLength = tempLength + titles.size();
				System.out.println("tempLength=" + tempLength);
				System.out.println("tempTime:" + tempTime);
				System.out.println("最新发布时间：" + timeInfo.getGuidArticleTime());
				if (titles != null) {
					for (int i = 0; i < titles.size(); i++) {
						list2.add(i, titles.get(titles.size() - 1 - i));
					}
				}
				adapter2.notifyDataSetChanged();
				break;
			}
			case WHAT_DID_MORE_3: {
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles = (List<ArticleTitle>) msg.obj;
				tempLength = tempLength + titles.size();
				System.out.println("tempLength=" + tempLength);
				System.out.println("tempTime:" + tempTime);
				System.out.println("最新发布时间：" + timeInfo.getGuidArticleTime());
				if (titles != null) {
					list2.addAll(titles);
				}
				adapter2.notifyDataSetChanged();
				break;
			}
			case WHAT_DID_MORE: {
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles = (List<ArticleTitle>) msg.obj;
				System.out.println("tempTime:" + tempTime);
				System.out.println("最新发布时间：" + timeInfo.getGuidArticleTime());
				if (titles != null) {
					list2.addAll(titles);
				}
				adapter2.notifyDataSetChanged();
				break;
			}
			}

		}

	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_right:
			Intent i = new Intent();
			i.setClass(ProductWarningActivity.this, WriteDisterActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("where", "productWarning");
			i.putExtra("bd", bundle);
			startActivity(i);
			break;
		default:
			break;
		}

	}
}
