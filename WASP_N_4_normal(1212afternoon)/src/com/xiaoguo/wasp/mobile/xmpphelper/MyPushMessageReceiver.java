package com.xiaoguo.wasp.mobile.xmpphelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.database.pushDb;
import com.xiaoguo.wasp.mobile.model.ArticleTitle;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.ui.monitor.MessageMointerActivity;
import com.xiaoguo.wasp.mobile.ui.monitor.PushMonitorActivity;
import com.xiaoguo.wasp.mobile.ui.warning.DisterWarmingDetailedActivity;
import com.xiaoguo.wasp.mobile.ui.warning.ProductGuideDetailedActivity;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;

public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {

	private CommandBase commandBase = CommandBase.instance();

	private pushDb pushdb;

	private UserSettingInfo userInfo;

	private ProductDb productDb;

	private TimeUtil timeUtil;

	public MyPushMessageReceiver() {
	}

	@Override
	public void onBind(Context context, int errorCode, String appid,
			final String userId, final String channelId, String requestId) {
		System.out
				.println("百度绑定成功了的百度绑定成功了的百度绑定成功了的百度绑定成功了的百度绑定成功了的百度绑定成功了的百度绑定成功了的百度绑定成功了的");
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		// pushdb = new pushDb(context);
		// // 将等到的channelId和userId传给服务器
		// String judge = pushdb.savePushId(userId, channelId);
		// // if (judge.equals("success")) {
		// // System.out.println("数据添加成功");
		// // } else {
		// // System.out.println("数据添加失败");
		// // }
		//
		// commandBase.request(new TaskListener() {
		//
		// @Override
		// public void updateCacheDate(List<HashMap<String, Object>> cacheData)
		// {
		//
		// }
		//
		// @Override
		// public void start() {
		//
		// }
		//
		// @Override
		// public String requestUrl() {
		// return "Push";
		// }
		//
		// @Override
		// public JSONObject requestData() {
		// JSONObject object = new JSONObject();
		// try {
		// object.put("push_user_id", userId);
		// object.put("channel_id", channelId);
		// object.put("login_flg", true);
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// return object;
		// }
		//
		// @Override
		// public String readCache() {
		// return null;
		// }
		//
		// @Override
		// public boolean needCacheTask() {
		// return false;
		// }
		//
		// @Override
		// public void messageUpdated(JSONObject msg) {
		//
		// }
		//
		// @Override
		// public void finish() {
		//
		// }
		//
		// @Override
		// public String filepath() {
		// return null;
		// }
		//
		// @Override
		// public void failure(String str) {
		//
		// }
		//
		// @Override
		// public String contentype() {
		// return null;
		// }
		// });
		// 保存在本地数据库
	}

	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		System.out.println("删除了标签啊啊啊啊啊啊啊啊啊啊啊");
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
	}

	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;

	}

	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		userInfo = new UserSettingInfo(context);
		productDb = new ProductDb(context);
		timeUtil = TimeUtil.getTimeUtilInstance();

		String messageString = "返回的生产提示信息==>>" + message;
		Log.d(TAG, messageString);
		try {

			/**
			 * articlenames_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			 * "article_id INTEGER, article_title TEXT, article_publish_date
			 * TEXT," + "article_publish_name TEXT, article_content TEXT,
			 * article_browser_count INTEGER" + ",article_type INTEGER, tag_name
			 * TEXT, save_userid TEXT
			 */
			JSONObject object = new JSONObject(message);
			int articleId = object.getInt("article_id");
			String artilceTitle = object.getString("article_title");
			JSONObject articlePublishDateObject = object
					.getJSONObject("article_publish_date");
			String articlePublishDate = timeUtil
					.TimeStamp2Date(articlePublishDateObject.getString("time"));
			String articlePublishName = object.getString("article_user_name");
			String artilceContent = object.getString("article_content");
			int articleBrowserCount = object.getInt("article_browser_count");
			int articleType = object.getInt("article_type");
			String tagName = object.getString("article_tag_name");
			String saveUserId = object.getString(userInfo.getAccount());

			ArticleTitle articleTitle = new ArticleTitle();
			articleTitle.setArticle_id(articleId);
			articleTitle.setArticle_title(artilceTitle);
			articleTitle.setArticle_browser_count(articleBrowserCount);
			articleTitle.setArticle_content(artilceContent);
			articleTitle.setArticle_publish_date(articlePublishDate);
			articleTitle.setArticle_type(articleType);
			articleTitle.setSave_userid(saveUserId);
			articleTitle.setArticle_publish_name(articlePublishName);
			articleTitle.setArticle_tag(tagName);
			articleTitle.setIs_read(0);
			productDb.saveArticleName(articleTitle);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "通知点击 title=" + title + " description="
				+ description + " customContent=" + customContentString;
		System.out.println("notifyStringnotifyStringnotifyString-->>"
				+ notifyString);
		// Log.d(TAG, notifyString);
		// 自定义内容获取方式，mykey 和 myvalue 对应通知推送时自定义内容中设置的
		// 键和值
		// if (customContentString != null & customContentString != "") {
		// JSONObject customJson = null;
		// try {
		// customJson = new JSONObject(customContentString);
		// String myvalue = null;
		// if (customJson.isNull("mykey")) {
		// myvalue = customJson.getString("mykey");
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// String judgeStr = title.substring(0, 2);
		//
		// if (judgeStr.equals("大鹏")) {
		// String contentStr = title + description + customContentString;
		// System.out.println("大棚的报警信息--->>" + contentStr);
		// Intent intent2 = new Intent();
		// String[] str1 = title.split(" ");
		// String time1 = str1[str1.length - 2] + " " + str1[str1.length - 1];
		// intent2.putExtra("condition", contentStr);
		// intent2.putExtra("description", description);
		// intent2.putExtra("time", time1);
		// intent2.setClass(context.getApplicationContext(),
		// PushMonitorActivity.class);
		// intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intent2);
		//
		// } else {
		// Intent intent1 = new Intent();
		// intent1.setClass(context.getApplicationContext(),
		// PushMonitorActivity.class);
		// String[] str = title.split(" ");
		// String time = str[str.length - 2] + " " + str[str.length - 1];
		// String titleMessage = str[0];
		// System.out.println("截取到的时间是--->>" + time + "标题是--->>"
		// + titleMessage);
		// String result = titleMessage + " " + time;
		// intent1.putExtra("condition", result);
		// intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// // 转向详细数据页面
		// context.startActivity(intent1);
		// }

		// showMonitorActivity(context, description);
		Intent intent1 = new Intent();
		intent1.setClass(context.getApplicationContext(),
				ProductGuideDetailedActivity.class);
		int alarmID = 0;
		try {
			JSONObject data = new JSONObject(customContentString);
			alarmID = Integer.parseInt(data.getString("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		intent1.putExtra("productGuideId", alarmID);
		intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent1);

	}

	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		System.out.println("设置了标签123123123123123123123123");
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {

	}

	public void showMonitorActivity(Context context, String content) {
		Intent intent = new Intent();
		List<String> list = new ArrayList<String>();
		intent.setClass(context.getApplicationContext(),
				PushMonitorActivity.class);
		intent.putStringArrayListExtra("content", (ArrayList<String>) list);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
