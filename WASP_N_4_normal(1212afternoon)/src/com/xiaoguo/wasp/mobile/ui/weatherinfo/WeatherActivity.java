package com.xiaoguo.wasp.mobile.ui.weatherinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.WASPApplication;
import com.xiaoguo.wasp.mobile.database.ProductDb;
import com.xiaoguo.wasp.mobile.model.ArticleTitle;
import com.xiaoguo.wasp.mobile.model.UpdateTimeInfo;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.model.WeatherInfo;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.network.TaskListener;
import com.xiaoguo.wasp.mobile.utils.NetWorkDetect;
import com.xiaoguo.wasp.mobile.utils.ParseDate2Week;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.utils.WeatherImgUtils;
import com.xiaoguo.wasp.mobile.widget.Utility;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WeatherActivity extends Activity implements OnClickListener {
	private TextView titleView;
	private Button leftBtView;
	private Button rightView;
	com.xiaoguo.wasp.mobile.widget.My2TextButton warnView = null;
	TextView wtWarn = null;
	ListView listView = null;
	private static final String NAMESPACE = "http://WebXml.com.cn/";
	private static String URL = "http://www.webxml.com.cn/WebServices/WeatherWS.asmx";// ����������Ϣ
	private static final String METHOD_NAME = "getWeather";
	private static String SOAP_ACTION = "http://WebXml.com.cn/getWeather";
	SoapObject detail;
	TextView currentTimeView;
	TextView currentCityView;
	ImageView currentWtPicView;
	TextView currentWtDetailView;
	TextView firstDayView;
	ImageView firstImgView;
	TextView firstDaytimeView;
	TextView firstDayNightView;
	TextView secondDayView;
	ImageView secondImgView;
	TextView secondDaytimeView;
	TextView secondDayNightView;
	TextView threeDayView;
	ImageView threeImgView;
	TextView threeDaytimeView;
	TextView threeDayNightView;
	TextView fourDayView;
	ImageView fourtImgView;
	TextView fourDaytimeView;
	TextView fourDayNightView;
	TextView fiveDayView;
	ImageView fiveImgView;
	TextView fiveDaytimeView;
	TextView fiveDayNightView;
	WeatherInfo weatherInfo = null;
	String city = "";
	private List<ArticleTitle> list2 = null;
	private List<ArticleTitle> list3 = null;
	UserSettingInfo userInfo = null;
	ProductDb productDb = null;
	UpdateTimeInfo timeInfo = null;
	private static final int GET_WEATHER_DONE = 1;
	private static final int GET_WEATHER = 0;
	ProgressDialog dialog = null;
	List<Integer> list4 = new ArrayList<Integer>();
	List<String> list1 = new ArrayList<String>();
	ArticleTitle tempTitle = null;
	String userType = "";
	MyBroadcastReceiver receiver = null;
	Animation refreshAnimation;
	ImageView imgRefresh;
	Utility utility = null;
	CommandBase commandBase=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_infomation);

		userInfo = new UserSettingInfo(this);
		productDb = new ProductDb(this);
		timeInfo = new UpdateTimeInfo(WeatherActivity.this,
				userInfo.getAccount());
		weatherInfo = new WeatherInfo(WeatherActivity.this, userInfo.getAccount());
		dialog = new ProgressDialog(WeatherActivity.this);
		userType = userInfo.getType();
		WASPApplication.getInstance().addActivity(this);
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(WeatherActivity.this, WeatherActivity.this);
		utility = new Utility();
		initView();
	}

	private void initView() {
		titleView = (TextView) findViewById(R.id.title);
		titleView.setText("������Ϣ");
		leftBtView = (Button) findViewById(R.id.bt_left);
		leftBtView.setVisibility(View.VISIBLE);
		leftBtView.setOnClickListener(this);
		rightView = (Button) findViewById(R.id.bt_right);
		rightView.setBackgroundResource(R.drawable.btn_setting);
		rightView.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(this);

		warnView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) findViewById(R.id.warning);
		wtWarn = (TextView) findViewById(R.id.wt_notice);
		imgRefresh = warnView.getRefreshImage();
		imgRefresh.setVisibility(View.GONE);
		refreshAnimation = rotateAnimation();
		imgRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// aa.setAnimation(refreshAnimation);
				imgRefresh.startAnimation(refreshAnimation);
				// imgRefresh.setEnabled(false);
				Toast.makeText(WeatherActivity.this, "��ʼ����", Toast.LENGTH_SHORT)
						.show();
			}
		});
		currentTimeView = (TextView) findViewById(R.id.current_time);
		currentCityView = (TextView) findViewById(R.id.currentCity);
		currentWtPicView = (ImageView) findViewById(R.id.current_wt_pic);
		currentWtDetailView = (TextView) findViewById(R.id.wt_details);
		// ��ȡ����һ��Ԥ����Ϣ

		// warnView.setOnClickListener(this);

		firstDayView = (TextView) findViewById(R.id.weekday1);
		firstImgView = (ImageView) findViewById(R.id.wt_pic_1);
		firstDaytimeView = (TextView) findViewById(R.id.daytime1);
		firstDayNightView = (TextView) findViewById(R.id.night1);

		secondDayView = (TextView) findViewById(R.id.weekday2);
		secondImgView = (ImageView) findViewById(R.id.wt_pic_2);
		secondDaytimeView = (TextView) findViewById(R.id.daytime2);
		secondDayNightView = (TextView) findViewById(R.id.night2);

		threeDayView = (TextView) findViewById(R.id.weekday3);
		threeImgView = (ImageView) findViewById(R.id.wt_pic_3);
		threeDaytimeView = (TextView) findViewById(R.id.daytime3);
		threeDayNightView = (TextView) findViewById(R.id.night3);

		fourDayView = (TextView) findViewById(R.id.weekday4);
		fourtImgView = (ImageView) findViewById(R.id.wt_pic_4);
		fourDaytimeView = (TextView) findViewById(R.id.daytime4);
		fourDayNightView = (TextView) findViewById(R.id.night4);

		fiveDayView = (TextView) findViewById(R.id.weekday5);
		fiveImgView = (ImageView) findViewById(R.id.wt_pic_5);
		fiveDaytimeView = (TextView) findViewById(R.id.daytime5);
		fiveDayNightView = (TextView) findViewById(R.id.night5);
		listView = (ListView) findViewById(R.id.wt_words);

		city = weatherInfo.getCurrentCity();
		if (city.equals("") || city == null) {
			city = "�ٷ�";
			weatherInfo.setCurrentProvience("ɽ��");
			weatherInfo.setCurrentCity(city);
		}

		if (weatherInfo.getUpdateTime() != null
				&& !weatherInfo.getUpdateTime().equals("")) {
			int update = Integer.parseInt(weatherInfo.getUpdateTime()
					.split(" ")[0].replace("/", ""));
			String currentDate = TimeUtil.getTimeUtilInstance().TimeStamp2Date(
					System.currentTimeMillis() + "");
			int current = Integer.parseInt(currentDate.split(" ")[0].replace(
					"-", ""));
			System.out.println("current=" + current);
			System.out.println("update=" + update);
			if (city.equals(weatherInfo.getCurrentCity())
					&& (current == update)) {
				System.out.println("����11");
				refresh();
			} else {
				System.out.println("����22");
				new Thread(new Runnable() {
					@Override
					public void run() {
						getWeather(city);
					}
				}).start();
			}
			if (current == update) {// ����Ѿ���ȡ�������������Ϣ����ֱ�Ӵ����ݿ���ʾ��Ϣ
				list2 = new ArrayList<ArticleTitle>();
				list2 = productDb.getArticleNames(3, userInfo.getAccount(), 0,
						1);
				list3 = new ArrayList<ArticleTitle>();
				list3 = productDb.getArticleNames(4, userInfo.getAccount(), 0,
						5);
				// list2.addAll(list3);
				refreshArticle();
			} else {// ÿ���ȡһ��������Ϣ
				list2 = new ArrayList<ArticleTitle>();
				list3 = new ArrayList<ArticleTitle>();
				getArtices(1, "loaddata");
			}
		} else {
			System.out.println("����33");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("����44");
					getWeather(city);
				}
			}).start();
			// ��һ����ֱ�Ӵӷ�������ȡ��Ϣ
			list2 = new ArrayList<ArticleTitle>();
			list3 = new ArrayList<ArticleTitle>();
			getArtices(1, "loaddata");
		}
	}

	// ��ȡ������Ϣ
	private SoapObject getWeather(String city) {
		System.out.println("����55");
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			System.out.println("aa");
			rpc.addProperty("theCityCode", city);
			System.out.println("bb");
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			System.out.println("cc");
			envelope.bodyOut = rpc;
			System.out.println("dd");
			envelope.dotNet = true;
			System.out.println("ee");
			envelope.setOutputSoapObject(rpc);
			System.out.println("ff");
			HttpTransportSE ht = new HttpTransportSE(URL);
			System.out.println("gg");
			ht.debug = true;
			System.out.println("hh");
			ht.call(SOAP_ACTION, envelope);
			System.out.println("ii");
			detail = (SoapObject) envelope.getResponse();
			System.out.println("detail=" + detail);
			System.out.println("����66");
			parseWeather(detail);
			return detail;
		} catch (Exception e) {
			System.out.println("����77");
			e.printStackTrace();
			return null;
		}

	}

	// ����������Ϣ
	private void parseWeather(SoapObject detail) {
		System.out.println("����88");
		String[] temp = null;
		String updateTime = detail.getProperty(3).toString();
		System.out.println("�������ʱ��:" + updateTime);// �������ʱ��
		String currentCity = detail.getProperty(1).toString();
		System.out.println("�������:" + currentCity);
		temp = detail.getProperty(4).toString().split(" ");
		String currentWind = temp[0].split("��")[3]
				+ temp[1].split("��")[0].split("��")[0];
		String currentTem = temp[0].split("��")[2].split("��")[0];
		System.out.println("�������:" + currentWind);
		System.out.println("��ǰ�¶�:" + currentTem);
		temp = detail.getProperty(6).toString().split(" ");
		String currentDate = updateTime.split(" ")[0];
		String year = currentDate.substring(0, currentDate.indexOf("/"));
		System.out.println("��ǰʱ��:" + currentDate);
		weatherInfo.setUpdateTime(updateTime);
		weatherInfo.setCurrentCity(currentCity);
		weatherInfo.setCurrentDate(currentDate);
		weatherInfo.setCurrentTem(currentTem);
		weatherInfo.setCurrentWind(currentWind);
		weatherInfo.setCurrentPic("");
		weatherInfo.setCurrentSub("");

		temp = detail.getProperty(7).toString().split(" ");
		String firstDate = year + "��" + temp[0];
		temp = detail.getProperty(8).toString().split("/");
		String firstTemHigh = temp[1];
		String firstTemLow = temp[0];
		String firstPic = detail.getProperty(10).toString();
		System.out.println("һ������:" + firstDate);
		System.out.println("һ������¶�:" + firstTemHigh);
		System.out.println("һ������¶�:" + firstTemLow);
		System.out.println("һ��ͼ��:" + firstPic);
		weatherInfo.setFirstDate(firstDate);
		weatherInfo.setFirstPic(firstPic);
		weatherInfo.setFirstTemHigh(firstTemHigh);
		weatherInfo.setFirstTemLow(firstTemLow);

		String secondDate = year + "��"
				+ detail.getProperty(12).toString().split(" ")[0];
		temp = detail.getProperty(13).toString().split("/");
		String secondTempHigh = temp[1];
		String secondTempLow = temp[0];
		String secondPic = detail.getProperty(15).toString();
		System.out.println("��������:" + secondDate);
		System.out.println("��������¶�:" + secondTempHigh);
		System.out.println("��������¶�:" + secondTempLow);
		System.out.println("����ͼ��:" + secondPic);
		weatherInfo.setSecondDate(secondDate);
		weatherInfo.setSecondPic(secondPic);
		weatherInfo.setSecondTemHigh(secondTempHigh);
		weatherInfo.setSecondTemLow(secondTempLow);

		String threeDate = year + "��"
				+ detail.getProperty(17).toString().split(" ")[0];
		temp = detail.getProperty(18).toString().split("/");
		String threeTempHigh = temp[1];
		String threeTempLow = temp[0];
		String threePic = detail.getProperty(20).toString();
		System.out.println("��������:" + threeDate);
		System.out.println("��������¶�:" + threeTempHigh);
		System.out.println("��������¶�:" + threeTempLow);
		System.out.println("����ͼ��:" + threePic);
		weatherInfo.setThreeDate(threeDate);
		weatherInfo.setThreePic(threePic);
		weatherInfo.setThreeTemHigh(threeTempHigh);
		weatherInfo.setThreeTemLow(threeTempLow);

		String fourDate = year + "��"
				+ detail.getProperty(22).toString().split(" ")[0];
		temp = detail.getProperty(23).toString().split("/");
		String fourTempHigh = temp[1];
		String fourTempLow = temp[0];
		String fourPic = detail.getProperty(25).toString();
		System.out.println("��������:" + fourDate);
		System.out.println("��������¶�:" + fourTempHigh);
		System.out.println("��������¶�:" + fourTempLow);
		System.out.println("����ͼ��:" + fourPic);
		weatherInfo.setFourDate(fourDate);
		weatherInfo.setFourPic(fourPic);
		weatherInfo.setFourTemHigh(fourTempHigh);
		weatherInfo.setFourTemLow(fourTempLow);

		String fiveDate = year + "��"
				+ detail.getProperty(27).toString().split(" ")[0];
		temp = detail.getProperty(28).toString().split("/");
		String fiveTempHigh = temp[1];
		String fiveTempLow = temp[0];
		String fivePic = detail.getProperty(30).toString();
		System.out.println("��������:" + fiveDate);
		System.out.println("��������¶�:" + fiveTempHigh);
		System.out.println("��������¶�:" + fiveTempLow);
		System.out.println("����ͼ��:" + fivePic);
		weatherInfo.setFiveDate(fiveDate);
		weatherInfo.setFivePic(fivePic);
		weatherInfo.setFiveTemHigh(fiveTempHigh);
		weatherInfo.setFiveTemLow(fiveTempLow);

		Message message = mUIHandler.obtainMessage(GET_WEATHER);
		message.sendToTarget();
	}

	// ���½���
	private void refresh() {
		System.out.println("����ľ��");
		System.out.println("������ϢΪ");
		if (weatherInfo.getCurrentDate() != null
				&& !weatherInfo.getCurrentDate().equals("")) {
			currentTimeView.setText(weatherInfo.getCurrentDate() + " "
					+ ParseDate2Week.getWeek1(weatherInfo.getCurrentDate()));
		}
		if (weatherInfo.getCurrentCity() != null
				&& !weatherInfo.getCurrentCity().equals("")) {
			currentCityView.setText(weatherInfo.getCurrentCity());
		}
		if (weatherInfo.getFirstPic() != null
				&& !weatherInfo.getFirstPic().equals("")) {
			currentWtPicView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherActivity.this, weatherInfo.getFirstPic()));
			firstImgView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherActivity.this, weatherInfo.getFirstPic()));
		}
		if (weatherInfo.getCurrentTem() != null
				&& !weatherInfo.getCurrentTem().equals("")) {
			currentWtDetailView.setText("���£�" + weatherInfo.getCurrentTem()
					+ "\n���������" + weatherInfo.getCurrentWind());
		}
		if (weatherInfo.getFirstDate() != null
				&& !weatherInfo.getFirstDate().equals("")) {
			firstDayView.setText(ParseDate2Week.getWeek(weatherInfo
					.getFirstDate()));
		}
		if (weatherInfo.getFirstTemHigh() != null
				&& !weatherInfo.getFirstTemHigh().equals("")) {
			firstDaytimeView.setText(weatherInfo.getFirstTemHigh());
		}
		if (weatherInfo.getFirstTemLow() != null
				&& !weatherInfo.getFirstTemLow().equals("")) {
			firstDayNightView.setText(weatherInfo.getFirstTemLow());
		}
		if (weatherInfo.getSecondDate() != null
				&& !weatherInfo.getSecondDate().equals("")) {
			secondDayView.setText(ParseDate2Week.getWeek(weatherInfo
					.getSecondDate()));
		}
		if (weatherInfo.getSecondPic() != null
				&& !weatherInfo.getSecondPic().equals("")) {
			secondImgView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherActivity.this, weatherInfo.getSecondPic()));
		}
		if (weatherInfo.getSecondTemHigh() != null
				&& !weatherInfo.getSecondTemHigh().equals("")) {
			secondDaytimeView.setText(weatherInfo.getSecondTemHigh());
		}
		if (weatherInfo.getSecondTemLow() != null
				&& !weatherInfo.getSecondTemLow().equals("")) {
			secondDayNightView.setText(weatherInfo.getSecondTemLow());
		}
		if (weatherInfo.getThreeDate() != null
				&& !weatherInfo.getThreeDate().equals("")) {
			threeDayView.setText(ParseDate2Week.getWeek(weatherInfo
					.getThreeDate()));
		}
		if (weatherInfo.getThreePic() != null
				&& !weatherInfo.getThreePic().equals("")) {
			threeImgView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherActivity.this, weatherInfo.getThreePic()));
		}
		if (weatherInfo.getThreeTemHigh() != null
				&& !weatherInfo.getThreeTemHigh().equals("")) {
			threeDaytimeView.setText(weatherInfo.getThreeTemHigh());
		}
		if (weatherInfo.getThreeTemLow() != null
				&& !weatherInfo.getThreeTemLow().equals("")) {
			threeDayNightView.setText(weatherInfo.getThreeTemLow());
		}
		if (weatherInfo.getFourDate() != null
				&& !weatherInfo.getFourDate().equals("")) {
			fourDayView.setText(ParseDate2Week.getWeek(weatherInfo
					.getFourDate()));
		}
		if (weatherInfo.getFourPic() != null
				&& !weatherInfo.getFourPic().equals("")) {
			fourtImgView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherActivity.this, weatherInfo.getFourPic()));
		}
		if (weatherInfo.getFourTemHigh() != null
				&& !weatherInfo.getFourTemHigh().equals("")) {
			fourDaytimeView.setText(weatherInfo.getFourTemHigh());
		}
		if (weatherInfo.getFourTemLow() != null
				&& !weatherInfo.getFourTemLow().equals("")) {
			fourDayNightView.setText(weatherInfo.getFourTemLow());
		}
		if (weatherInfo.getFiveDate() != null
				&& !weatherInfo.getFiveDate().equals("")) {
			fiveDayView.setText(ParseDate2Week.getWeek(weatherInfo
					.getFiveDate()));
		}
		if (weatherInfo.getFivePic() != null
				&& !weatherInfo.getFivePic().equals("")) {
			fiveImgView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherActivity.this, weatherInfo.getFivePic()));
		}
		if (weatherInfo.getFiveTemHigh() != null
				&& !weatherInfo.getFiveTemHigh().equals("")) {
			fiveDaytimeView.setText(weatherInfo.getFiveTemHigh());
		}
		if (weatherInfo.getFiveTemLow() != null
				&& !weatherInfo.getFiveTemLow().equals("")) {
			fiveDayNightView.setText(weatherInfo.getFiveTemLow());
		}

	}

	// ��ȡ���±����б�
	private List<ArticleTitle> getArtices(final int i5, final String style) {
		System.out.println("��ǰ�����viewΪ��" + i5);
		System.out.println("list2����ǰ�ĳ��ȣ�" + list2.size());
		System.out.println("list3����ǰ�ĳ��ȣ�" + list3.size());
		final List<ArticleTitle> articles = new ArrayList<ArticleTitle>();
		commandBase.request(new TaskListener() {
			@Override
			public void updateCacheDate(List<HashMap<String, Object>> cacheData) {
			}

			@Override
			public void start() {
				if (style.equals("loaddata")) {
					dialog.setMessage("������������,���Ժ�...");
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
				int offset = 1;
				int tipLimit = 0;
				int tipOffset = 5;
				JSONObject object = new JSONObject();
				JSONObject tempJsonObject = new JSONObject();
				try {
					object.put("limit", limit);
					object.put("offset", offset);
					object.put("tipLimit", tipLimit);
					object.put("tipOffset", tipOffset);
					tempJsonObject.put("direction", "desc");//
					object.put("sort", tempJsonObject);
					object.put("updatedate", "");
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
				System.out.println("list2 �����ɹ���ĳ��ȣ�" + list2.size());
				System.out.println("list2�����ɹ���ĳ��ȣ�" + list3.size());
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
						System.out.println("article=" + article.toString());
						articles.add(article);
					}
					JSONArray tips = (JSONArray) object1.get("tip");
					System.out.println("tip���ȣ�" + tips.length());
					for (int j = 0; j < tips.length(); j++) {
						object1 = tips.getJSONObject(j);
						tip = new ArticleTitle();
						tip.setArticle_id(object1.getInt("article_id"));
						tip.setArticle_title(object1.getString("article_title"));
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
						int type1 = object1.getInt("article_type");
						tip.setArticle_type(type1);
						tip.setSave_userid(userInfo.getAccount());
						System.out.println("tip=" + tip.toString());
						tipList.add(tip);
						System.out.println("11");
					}
					System.out.println("list2 ������ĳ��ȣ�" + list2.size());
					String time = System.currentTimeMillis() + "";
					System.out.println("time=" + time);
					timeInfo.setWeatherInfoTime(time);

					System.out.println("weatherinfotime="
							+ timeInfo.getWeatherInfoTime());
					System.out.println("view1 list2δ�����ĳ��ȣ�" + list2.size());
					if (articles.size() > 0) {
						list2.add(0, articles.get(0));
					}
					System.out.println("view1  List2����article��0������Ϊ��"
							+ list2.size());
					System.out.println("view1 tipsList:" + tipList.size());
					if (tipList.size() > 0) {
						// list2.addAll(tipList);
						list3.addAll(tipList);
					}
					System.out.println("view1  List2�ĳ���Ϊ��" + list2.size());
					System.out.println("view1  List3�ĳ���Ϊ��" + list3.size());
					Message msg1 = mUIHandler.obtainMessage(GET_WEATHER_DONE);
					msg1.sendToTarget();
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("��������");
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

	private void refreshArticle() {
		final Bundle bd = new Bundle();
		list4 = new ArrayList<Integer>();
		list1 = new ArrayList<String>();
		System.out.println("������Ϣ���ȣ�" + list2.size());
		if (list2.size() <= 0) {

			warnView.setTextView1Text("û��Ԥ����Ϣ");
			warnView.setTextView2Text("");
		}
		if (list2.size() == 1) {
			tempTitle = list2.get(0);
			warnView.setTextView1Text(tempTitle.getArticle_title());

			weatherInfo.setDisasterWarningTitle(tempTitle.getArticle_title());
			weatherInfo.setDisaterId(tempTitle.getArticle_id());

			warnView.setTextView2Text(">");
			wtWarn.setVisibility(View.VISIBLE);
			warnView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// Toast.makeText(WeatherActivity.this,
					// "�����"+list2.get(0).getArticle_title(),
					// Toast.LENGTH_SHORT).show();
					Intent i = new Intent();
					i.setClass(WeatherActivity.this, ArticleActivity.class);
					bd.putInt("id", list2.get(0).getArticle_id());
					bd.putString("fromwhere", "disater");
					i.putExtra("bd", bd);
					startActivity(i);
				}
			});

		}
		if (list3.size() <= 0) {
			wtWarn.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
		if (list3.size() > 0) {
			wtWarn.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			/*
			 * tempTitle = list2.get(0); System.out.println("tempTitle=" +
			 * tempTitle);
			 * warnView.setTextView1Text(tempTitle.getArticle_title());
			 * warnView.setTextView2Text(">"); warnView.setOnClickListener(new
			 * OnClickListener() {
			 * 
			 * @Override public void onClick(View arg0) { tempTitle =
			 * list2.get(0); Intent i = new Intent();
			 * i.setClass(WeatherActivity.this, ArticleActivity.class);
			 * bd.putInt("id", tempTitle.getArticle_id());
			 * bd.putString("fromwhere", "disater"); i.putExtra("bd", bd);
			 * startActivity(i); } });
			 */
			for (int i = 0; i < list3.size(); i++) {
				tempTitle = list3.get(i);
				list4.add(tempTitle.getArticle_id());
				list1.add(tempTitle.getArticle_title());
				System.out.println("���⣺" + tempTitle.getArticle_title());
			}

			if (list3.size() >= 1) {
				weatherInfo.setGuidId1(list3.get(0).getArticle_id());
				weatherInfo.setGuidTitle1(list3.get(0).getArticle_title());
				if (list3.size() >= 2) {
					weatherInfo.setGuidId2(list3.get(1).getArticle_id());
					weatherInfo.setGuidTitle2(list3.get(1).getArticle_title());
					if (list3.size() >= 3) {
						weatherInfo.setGuidId3(list3.get(2).getArticle_id());
						weatherInfo.setGuidTitle3(list3.get(2)
								.getArticle_title());
						if (list3.size() >= 4) {
							weatherInfo
									.setGuidId4(list3.get(3).getArticle_id());
							weatherInfo.setGuidTitle4(list3.get(3)
									.getArticle_title());
							if (list3.size() >= 5) {
								weatherInfo.setGuidId5(list3.get(4)
										.getArticle_id());
								weatherInfo.setGuidTitle5(list3.get(4)
										.getArticle_title());
							}
						}
					}
				}
			}

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				WeatherActivity.this, android.R.layout.simple_list_item_1,
				list1);
		listView.setAdapter(adapter);
		utility.setListViewHeightBasedOnChildren(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent();
				i.setClass(WeatherActivity.this, ArticleActivity.class);
				Bundle bd = new Bundle();
				bd.putString("title", list1.get(arg2));
				bd.putString("fromwhere", "tip");
				bd.putInt("id", list4.get(arg2));
				i.putExtra("bd", bd);
				startActivity(i);
			}
		});
	}

	// ����UI�̴߳�����
	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_WEATHER:
				refresh();
				break;
			case GET_WEATHER_DONE:
				refreshArticle();
				break;
			default:
				break;
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
			i.setClass(WeatherActivity.this, SetCityActivity.class);
			startActivityForResult(i, 1);
			break;

		case R.id.warning:

			break;

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
//		receiver = new MyBroadcastReceiver(WeatherActivity.this);
//		IntentFilter filter = new IntentFilter();
//
//		filter.addAction(Constant.ROSTER_ADDED);
//		filter.addAction(Constant.ROSTER_DELETED);
//		filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
//		filter.addAction(Constant.ROSTER_UPDATED);
//		// ��������
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				Bundle bundle2 = data.getBundleExtra("bd");
				final String city = bundle2.getString("city");
				final String province = bundle2.getString("province");
				System.out.println("���صĳ����ǣ�" + city);
				System.out.println("��ǰ�����ǣ�" + weatherInfo.getCurrentCity());
				if (!city.equals(weatherInfo.getCurrentCity())) {
					if (NetWorkDetect.detect(WeatherActivity.this)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// ����ǵ���ǵ�һ�β�ѯ���ҳ���һ�����ٲ�ѯ
								weatherInfo.setCurrentProvience(province);
								weatherInfo.setCurrentCity(city);
								getWeather(city);
							}
						}).start();
					} else {
						Toast.makeText(WeatherActivity.this, "��������ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
