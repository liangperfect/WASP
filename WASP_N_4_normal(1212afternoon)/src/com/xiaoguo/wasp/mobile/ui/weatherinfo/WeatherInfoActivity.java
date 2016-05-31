package com.xiaoguo.wasp.mobile.ui.weatherinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

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
import com.xiaoguo.wasp.mobile.widget.PullDownView;
import com.xiaoguo.wasp.mobile.widget.PullDownView.OnPullDownListener;
import com.xiaoguo.wasp.mobile.widget.Utility;
import com.xiaoguo.wasp.mobile.xmpphelper.MyBroadcastReceiver;

/**
 * @author hcq 气象信息
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class WeatherInfoActivity extends Activity implements OnClickListener {
	LinearLayout layout = null;

	private TextView titleView;
	private Button leftBtView;
	private Button rightView;
	com.xiaoguo.wasp.mobile.widget.My2TextButton warnView = null;
	TextView wtWarn = null;
	ListView listView = null;
	private static final String NAMESPACE = "http://WebXml.com.cn/";
	private static String URL = "http://www.webxml.com.cn/WebServices/WeatherWS.asmx";// 五日天气信息
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
	int unReadNums;
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

	private CommandBase commandBase;
	PullDownView mPullDownView;
	ListView mListView;
	WarnAdapter adapter2;
	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 2;
	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH_2 = 3;
	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE_2 = 4;
	/** Handler What更新数据完毕 **/
	TextView unreadNum1;
	private static final int WHAT_DID_MORE = 6;

	String tempTime = "";// 用来存放此时获取的最新一条数据的发布时间
	int tempLength = 0;// 保存服务器请求的数据的长度
	int localTempLength = 0;
	boolean hasNew = false;
//	TabHost tabHost ;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);
		WASPApplication.getInstance().addActivity(this);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(WeatherInfoActivity.this,
				WeatherInfoActivity.this);
		commandBase.setMainActivityContext(WeatherInfoActivity.this);
		userInfo = new UserSettingInfo(this);
		productDb = new ProductDb(this);
		timeInfo = new UpdateTimeInfo(WeatherInfoActivity.this,
				userInfo.getAccount());
		weatherInfo = new WeatherInfo(WeatherInfoActivity.this, userInfo.getAccount());
		dialog = new ProgressDialog(WeatherInfoActivity.this);
		userType = userInfo.getType();
		WASPApplication.getInstance().addActivity(this);

		setUpView();
	}

	private void setUpView() {

		View view = (View) LayoutInflater.from(this).inflate(
				R.layout.mytabhost, null);
		TextView unreadNum = (TextView) view
				.findViewById(R.id.unreadMessageNum);
		TextView text = (TextView) view.findViewById(R.id.text);
		ImageView img = (ImageView) view.findViewById(R.id.item);
		unreadNum.setVisibility(View.GONE);
		text.setText("气象信息");
		text.setTextSize(14);
		text.setTextColor(getResources().getColor(R.color.text));
		img.setImageResource(R.drawable.weather_selector);

		View view1 = (View) LayoutInflater.from(this).inflate(
				R.layout.mytabhost, null);
		unreadNum1 = (TextView) view1.findViewById(R.id.unreadMessageNum);
		TextView text1 = (TextView) view1.findViewById(R.id.text);
		ImageView img1 = (ImageView) view1.findViewById(R.id.item);
		unReadNums = productDb.getUnReadArticleCount(userInfo.getAccount(), 3);
		if (unReadNums > 0) {
			unreadNum1.setVisibility(View.VISIBLE);
			unreadNum1.setText(unReadNums + "");

		} else {

			unreadNum1.setVisibility(View.GONE);
		}

		text1.setText("灾害预警");
		text1.setTextSize(14);
		text1.setTextColor(getResources().getColor(R.color.text));
		img1.setImageResource(R.drawable.disater_selector);

//		tabHost = getTabHost();
		// 3个及3个以上的写法
//		tabHost.addTab(tabHost.newTabSpec("气象信息").setIndicator(view)
//				.setContent(R.id.weather_info));
//		tabHost.addTab(tabHost.newTabSpec("灾害预警").setIndicator(view1)
//				.setContent(R.id.disaster));
//		layout = (LinearLayout) tabHost.getChildAt(0);
		titleView = (TextView) findViewById(R.id.title);
		leftBtView = (Button) findViewById(R.id.bt_left);
		leftBtView.setVisibility(View.VISIBLE);
		leftBtView.setOnClickListener(this);
		rightView = (Button) findViewById(R.id.bt_right);
		init1(layout);
//		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
//			@Override
//			public void onTabChanged(String tabId) {
//				if ("气象信息".equals(tabId)) {
//					init1(layout);
//				}
//				if ("灾害预警".equals(tabId)) {
//					init2(layout);
//				}
//			}
//		});
	}

	private void init1(LinearLayout layout) {
		utility = new Utility();
		titleView.setText("气象信息");
		rightView.setBackgroundResource(R.drawable.btn_setting);
		rightView.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(WeatherInfoActivity.this, SetCityActivity.class);
				startActivityForResult(i, 1);
			}
		});

		warnView = (com.xiaoguo.wasp.mobile.widget.My2TextButton) layout
				.findViewById(R.id.warning);
		wtWarn = (TextView) layout.findViewById(R.id.wt_notice);
		imgRefresh = warnView.getRefreshImage();
		imgRefresh.setVisibility(View.GONE);
		refreshAnimation = rotateAnimation();
		imgRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// aa.setAnimation(refreshAnimation);
				imgRefresh.startAnimation(refreshAnimation);
				// imgRefresh.setEnabled(false);
				Toast.makeText(WeatherInfoActivity.this, "开始更新",
						Toast.LENGTH_SHORT).show();
			}
		});
		currentTimeView = (TextView) layout.findViewById(R.id.current_time);
		currentCityView = (TextView) layout.findViewById(R.id.currentCity);
		currentCityView.setOnClickListener(this);
		currentCityView.setTextColor(Color.BLUE);
		currentCityView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		currentWtPicView = (ImageView) layout.findViewById(R.id.current_wt_pic);
		currentWtDetailView = (TextView) layout.findViewById(R.id.wt_details);
		// 获取最新一条预警信息

		// warnView.setOnClickListener(this);

		firstDayView = (TextView) layout.findViewById(R.id.weekday1);
		firstImgView = (ImageView) layout.findViewById(R.id.wt_pic_1);
		firstDaytimeView = (TextView) layout.findViewById(R.id.daytime1);
		firstDayNightView = (TextView) layout.findViewById(R.id.night1);

		secondDayView = (TextView) layout.findViewById(R.id.weekday2);
		secondImgView = (ImageView) layout.findViewById(R.id.wt_pic_2);
		secondDaytimeView = (TextView) layout.findViewById(R.id.daytime2);
		secondDayNightView = (TextView) layout.findViewById(R.id.night2);

		threeDayView = (TextView) layout.findViewById(R.id.weekday3);
		threeImgView = (ImageView) layout.findViewById(R.id.wt_pic_3);
		threeDaytimeView = (TextView) layout.findViewById(R.id.daytime3);
		threeDayNightView = (TextView) layout.findViewById(R.id.night3);

		fourDayView = (TextView) layout.findViewById(R.id.weekday4);
		fourtImgView = (ImageView) layout.findViewById(R.id.wt_pic_4);
		fourDaytimeView = (TextView) layout.findViewById(R.id.daytime4);
		fourDayNightView = (TextView) layout.findViewById(R.id.night4);

		fiveDayView = (TextView) layout.findViewById(R.id.weekday5);
		fiveImgView = (ImageView) layout.findViewById(R.id.wt_pic_5);
		fiveDaytimeView = (TextView) layout.findViewById(R.id.daytime5);
		fiveDayNightView = (TextView) layout.findViewById(R.id.night5);
		listView = (ListView) layout.findViewById(R.id.wt_words);

		city = weatherInfo.getCurrentCity();
		if (city.equals("") || city == null) {
			city = "临汾";
			weatherInfo.setCurrentProvience("山西");
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
				System.out.println("天气11");
				refresh();
			} else {
				System.out.println("天气22");
				new Thread(new Runnable() {
					@Override
					public void run() {
						getWeather(city);
					}
				}).start();
			}
			if (current == update) {// 如果已经获取过当天的最新信息，则直接从数据库显示信息
				weatherInfo.setDisasterWarningTitle(weatherInfo
						.getDisasterWarningTitle());

				list2 = new ArrayList<ArticleTitle>();
				tempTitle = new ArticleTitle();
				tempTitle.setArticle_title(weatherInfo
						.getDisasterWarningTitle());
				tempTitle.setArticle_id(weatherInfo.getDisaterId());
				list2.add(tempTitle);
				list3 = new ArrayList<ArticleTitle>();
				if (weatherInfo.getGuidId1() != -1) {
					tempTitle = new ArticleTitle();
					tempTitle.setArticle_title(weatherInfo.getGuidTitle1());
					tempTitle.setArticle_id(weatherInfo.getGuidId1());
					list3.add(tempTitle);
				}
				if (weatherInfo.getGuidId2() != -1) {
					tempTitle = new ArticleTitle();
					tempTitle.setArticle_title(weatherInfo.getGuidTitle2());
					tempTitle.setArticle_id(weatherInfo.getGuidId2());
					list3.add(tempTitle);
				}
				if (weatherInfo.getGuidId3() != -1) {
					tempTitle = new ArticleTitle();
					tempTitle.setArticle_title(weatherInfo.getGuidTitle3());
					tempTitle.setArticle_id(weatherInfo.getGuidId3());
					list3.add(tempTitle);
				}
				if (weatherInfo.getGuidId4() != -1) {
					tempTitle = new ArticleTitle();
					tempTitle.setArticle_title(weatherInfo.getGuidTitle4());
					tempTitle.setArticle_id(weatherInfo.getGuidId4());
					list3.add(tempTitle);
				}
				if (weatherInfo.getGuidId5() != -1) {
					tempTitle = new ArticleTitle();
					tempTitle.setArticle_title(weatherInfo.getGuidTitle5());
					tempTitle.setArticle_id(weatherInfo.getGuidId5());
					list3.add(tempTitle);
				}

				refreshArticle();
			} else {// 每天获取一次最新消息
				list2 = new ArrayList<ArticleTitle>();
				list3 = new ArrayList<ArticleTitle>();
				getArtices(1, "loaddata");
			}
		} else {
			System.out.println("天气33");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("天气44");
					getWeather(city);
				}
			}).start();
			// 第一次则直接从服务器获取信息
			list2 = new ArrayList<ArticleTitle>();
			list3 = new ArrayList<ArticleTitle>();
			getArtices(1, "loaddata");
		}
	}

	// 获取天气信息
	private SoapObject getWeather(String city) {
		System.out.println("天气55");
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
			System.out.println("天气66");
			parseWeather(detail);
			return detail;
		} catch (Exception e) {
			System.out.println("天气77");
			e.printStackTrace();
			return null;
		}

	}

	// 解析天气信息
	private void parseWeather(SoapObject detail) {
		System.out.println("天气88");
		String[] temp = null;
		String updateTime = detail.getProperty(3).toString();
		System.out.println("最近更新时间:" + updateTime);// 最近更新时间
		String currentCity = detail.getProperty(1).toString();
		System.out.println("请求城市:" + currentCity);
		temp = detail.getProperty(4).toString().split(" ");
		String currentWind = temp[0].split("：")[3]
				+ temp[1].split("：")[0].split("；")[0];
		String currentTem = temp[0].split("：")[2].split("；")[0];
		System.out.println("风向风力:" + currentWind);
		System.out.println("当前温度:" + currentTem);
		temp = detail.getProperty(6).toString().split(" ");
		String currentDate = updateTime.split(" ")[0];
		String year = currentDate.substring(0, currentDate.indexOf("/"));
		System.out.println("当前时间:" + currentDate);
		weatherInfo.setUpdateTime(updateTime);
		weatherInfo.setCurrentCity(currentCity);
		weatherInfo.setCurrentDate(currentDate);
		weatherInfo.setCurrentTem(currentTem);
		weatherInfo.setCurrentWind(currentWind);
		weatherInfo.setCurrentPic("");
		weatherInfo.setCurrentSub("");

		temp = detail.getProperty(7).toString().split(" ");
		String firstDate = year + "年" + temp[0];
		temp = detail.getProperty(8).toString().split("/");
		String firstTemHigh = temp[1];
		String firstTemLow = temp[0];
		String firstPic = detail.getProperty(10).toString();
		System.out.println("一天日期:" + firstDate);
		System.out.println("一天最高温度:" + firstTemHigh);
		System.out.println("一天最低温度:" + firstTemLow);
		System.out.println("一天图标:" + firstPic);
		weatherInfo.setFirstDate(firstDate);
		weatherInfo.setFirstPic(firstPic);
		weatherInfo.setFirstTemHigh(firstTemHigh);
		weatherInfo.setFirstTemLow(firstTemLow);

		String secondDate = year + "年"
				+ detail.getProperty(12).toString().split(" ")[0];
		temp = detail.getProperty(13).toString().split("/");
		String secondTempHigh = temp[1];
		String secondTempLow = temp[0];
		String secondPic = detail.getProperty(15).toString();
		System.out.println("二天日期:" + secondDate);
		System.out.println("二天最高温度:" + secondTempHigh);
		System.out.println("二天最低温度:" + secondTempLow);
		System.out.println("二天图标:" + secondPic);
		weatherInfo.setSecondDate(secondDate);
		weatherInfo.setSecondPic(secondPic);
		weatherInfo.setSecondTemHigh(secondTempHigh);
		weatherInfo.setSecondTemLow(secondTempLow);

		String threeDate = year + "年"
				+ detail.getProperty(17).toString().split(" ")[0];
		temp = detail.getProperty(18).toString().split("/");
		String threeTempHigh = temp[1];
		String threeTempLow = temp[0];
		String threePic = detail.getProperty(20).toString();
		System.out.println("三天日期:" + threeDate);
		System.out.println("三天最高温度:" + threeTempHigh);
		System.out.println("三天最低温度:" + threeTempLow);
		System.out.println("三天图标:" + threePic);
		weatherInfo.setThreeDate(threeDate);
		weatherInfo.setThreePic(threePic);
		weatherInfo.setThreeTemHigh(threeTempHigh);
		weatherInfo.setThreeTemLow(threeTempLow);

		String fourDate = year + "年"
				+ detail.getProperty(22).toString().split(" ")[0];
		temp = detail.getProperty(23).toString().split("/");
		String fourTempHigh = temp[1];
		String fourTempLow = temp[0];
		String fourPic = detail.getProperty(25).toString();
		System.out.println("四天日期:" + fourDate);
		System.out.println("四天最高温度:" + fourTempHigh);
		System.out.println("四天最低温度:" + fourTempLow);
		System.out.println("四天图标:" + fourPic);
		weatherInfo.setFourDate(fourDate);
		weatherInfo.setFourPic(fourPic);
		weatherInfo.setFourTemHigh(fourTempHigh);
		weatherInfo.setFourTemLow(fourTempLow);

		String fiveDate = year + "年"
				+ detail.getProperty(27).toString().split(" ")[0];
		temp = detail.getProperty(28).toString().split("/");
		String fiveTempHigh = temp[1];
		String fiveTempLow = temp[0];
		String fivePic = detail.getProperty(30).toString();
		System.out.println("五天日期:" + fiveDate);
		System.out.println("五天最高温度:" + fiveTempHigh);
		System.out.println("五天最低温度:" + fiveTempLow);
		System.out.println("五天图标:" + fivePic);
		weatherInfo.setFiveDate(fiveDate);
		weatherInfo.setFivePic(fivePic);
		weatherInfo.setFiveTemHigh(fiveTempHigh);
		weatherInfo.setFiveTemLow(fiveTempLow);

		Message message = mUIHandler.obtainMessage(GET_WEATHER);
		message.sendToTarget();
	}

	// 更新界面
	private void refresh() {
		System.out.println("进来木有");
		System.out.println("气象信息为");
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
					WeatherInfoActivity.this, weatherInfo.getFirstPic()));
			firstImgView.setImageBitmap(WeatherImgUtils.getPicBmp(
					WeatherInfoActivity.this, weatherInfo.getFirstPic()));
		}
		if (weatherInfo.getCurrentTem() != null
				&& !weatherInfo.getCurrentTem().equals("")) {
			currentWtDetailView.setText("气温：" + weatherInfo.getCurrentTem()
					+ "\n风向风力：" + weatherInfo.getCurrentWind());
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
					WeatherInfoActivity.this, weatherInfo.getSecondPic()));
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
					WeatherInfoActivity.this, weatherInfo.getThreePic()));
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
					WeatherInfoActivity.this, weatherInfo.getFourPic()));
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
					WeatherInfoActivity.this, weatherInfo.getFivePic()));
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

	// 获取文章标题列表
	private List<ArticleTitle> getArtices(final int i5, final String style) {
		System.out.println("当前进入的view为：" + i5);
		System.out.println("list2联网前的长度：" + list2.size());
		System.out.println("list3联网前的长度：" + list3.size());
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
				System.out.println("list2 联网成功后的长度：" + list2.size());
				System.out.println("list2联网成功后的长度：" + list3.size());
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
						articles.add(article);
					}
					JSONArray tips = (JSONArray) object1.get("tip");
					System.out.println("tip长度：" + tips.length());
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
					System.out.println("list2 联网后的长度：" + list2.size());
					String time = System.currentTimeMillis() + "";
					System.out.println("time=" + time);
					timeInfo.setWeatherInfoTime(time);

					System.out.println("weatherinfotime="
							+ timeInfo.getWeatherInfoTime());
					System.out.println("view1 list2未操作的长度：" + list2.size());
					if (articles.size() > 0) {
						list2.add(0, articles.get(0));
					}
					System.out.println("view1  List2加入article【0】长度为："
							+ list2.size());
					System.out.println("view1 tipsList:" + tipList.size());
					if (tipList.size() > 0) {
						// list2.addAll(tipList);
						list3.addAll(tipList);
					}
					System.out.println("view1  List2的长度为：" + list2.size());
					System.out.println("view1  List3的长度为：" + list3.size());
					Message msg1 = mUIHandler.obtainMessage(GET_WEATHER_DONE);
					msg1.sendToTarget();
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

	private void refreshArticle() {
		final Bundle bd = new Bundle();
		list4 = new ArrayList<Integer>();
		list1 = new ArrayList<String>();
		System.out.println("气象信息长度：" + list2.size());
		if (list2.size() <= 0) {

			warnView.setTextView1Text("没有预警信息");
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
					Intent i = new Intent();
					i.setClass(WeatherInfoActivity.this, ArticleActivity.class);
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
			for (int i = 0; i < list3.size(); i++) {
				tempTitle = list3.get(i);
				list4.add(tempTitle.getArticle_id());
				list1.add(tempTitle.getArticle_title());
				System.out.println("标题：" + tempTitle.getArticle_title());
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
				WeatherInfoActivity.this, android.R.layout.simple_list_item_1,
				list1);
		listView.setAdapter(adapter);
		utility.setListViewHeightBasedOnChildren(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent();
				i.setClass(WeatherInfoActivity.this, ArticleActivity.class);
				Bundle bd = new Bundle();
				bd.putString("title", list1.get(arg2));
				bd.putString("fromwhere", "tip");
				bd.putInt("id", list4.get(arg2));
				i.putExtra("bd", bd);
				startActivity(i);
			}

		});
	}

	// 更新UI线程处理器
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
			case WHAT_DID_LOAD_DATA:
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles = (List<ArticleTitle>) msg.obj;
				tempLength = tempLength + titles.size();
				System.out.println("tempLength=" + tempLength);
				System.out.println("tempTime:" + tempTime);
				System.out
						.println("最新发布时间：" + timeInfo.getWeatherWarningTime());
				adapter2.notifyDataSetChanged();
				break;
			case WHAT_DID_REFRESH_2:
				System.out.println("刷新的数据：");
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles3 = (List<ArticleTitle>) msg.obj;
				tempLength = tempLength + titles3.size();
				System.out.println("tempLength=" + tempLength);
				System.out.println("tempTime:" + tempTime);
				System.out
						.println("最新发布时间：" + timeInfo.getWeatherWarningTime());
				if (titles3 != null) {
					for (int i = 0; i < titles3.size(); i++) {
						System.out.println(titles3.get(titles3.size() - 1 - i));
						list2.add(i, titles3.get(titles3.size() - 1 - i));
					}
				}
				adapter2.notifyDataSetChanged();
				break;
			case WHAT_DID_MORE_2:
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles2 = (List<ArticleTitle>) msg.obj;
				System.out.println("tempTime:" + tempTime);
				System.out
						.println("最新发布时间：" + timeInfo.getWeatherWarningTime());
				if (titles2 != null) {
					list2.addAll(titles2);
				}
				adapter2.notifyDataSetChanged();
				break;
			case WHAT_DID_MORE:
				System.out.println("tempTime:" + tempTime);
				System.out
						.println("最新发布时间：" + timeInfo.getWeatherWarningTime());
				@SuppressWarnings("unchecked")
				List<ArticleTitle> titles1 = (List<ArticleTitle>) msg.obj;
				if (titles1 != null) {
					list2.addAll(titles1);
				}
				adapter2.notifyDataSetChanged();
				break;
			default:
				break;
			}

		}

	};

//	@Override
//	protected void onPause() {
//		unregisterReceiver(receiver);
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		receiver = new MyBroadcastReceiver(WeatherInfoActivity.this);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case 1:
				Bundle bundle2 = data.getBundleExtra("bd");
				final String city = bundle2.getString("city");
				final String provience = bundle2.getString("province");
				System.out.println("返回的城市是：" + city);
				System.out.println("当前城市是：" + weatherInfo.getCurrentCity());
				if (!city.equals(weatherInfo.getCurrentCity())) {
					if (NetWorkDetect.detect(WeatherInfoActivity.this)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// 如果是当天非第一次查询，且城市一样则不再查询
								getWeather(city);
								weatherInfo.setCurrentProvience(provience);
								weatherInfo.setCurrentCity(city);
							}
						}).start();
					} else {
						Toast.makeText(WeatherInfoActivity.this, "网络连接失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case 2:
				Toast.makeText(WeatherInfoActivity.this, "灾害预警发布成功！",
						Toast.LENGTH_SHORT).show();
				break;

			case 3:
				list2 = productDb.getArticleNames(3, userInfo.getAccount(), 0,
						10);
				adapter2 = new WarnAdapter(WeatherInfoActivity.this, list2, 3);
				adapter2.notifyDataSetChanged();
				mListView.setAdapter(adapter2);

				unReadNums = productDb.getUnReadArticleCount(
						userInfo.getAccount(), 3);
				if (unReadNums > 0) {
					unreadNum1.setVisibility(View.VISIBLE);
					unreadNum1.setText(unReadNums + "");

				} else {

					unreadNum1.setVisibility(View.GONE);
				}

				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void init2(LinearLayout layout) {
		titleView.setText("灾害预警");
		rightView.setBackgroundResource(R.drawable.btn_add);
		if (userType.equals("farmer")) {
			rightView.setVisibility(View.INVISIBLE);
		} else {
			rightView.setVisibility(View.VISIBLE);
		}
		rightView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(WeatherInfoActivity.this, WriteDisterActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("where", "disaster");
				i.putExtra("bd", bundle);
				startActivityForResult(i, 2);
			}
		});

		mPullDownView = (PullDownView) layout.findViewById(R.id.pull_down_view);
		mListView = mPullDownView.getListView();
		mListView.setDividerHeight(25);
		mListView.setDivider(this.getResources().getDrawable(R.drawable.white));
		mListView.setBackgroundColor(this.getResources()
				.getColor(R.color.white));
		mListView.setVerticalScrollBarEnabled(false);

		// 先获取本地数据,从本地获取10条数据
		list2 = new ArrayList<ArticleTitle>();
		list2 = productDb.getArticleNames(3, userInfo.getAccount(), 0, 10);
		localTempLength = list2.size();
		if (list2.size() > 0) {
			if (list2.size() <= 0) {
				Toast.makeText(WeatherInfoActivity.this, "没有灾害预警信息！",
						Toast.LENGTH_SHORT).show();
			}
			adapter2 = new WarnAdapter(WeatherInfoActivity.this, list2, 3);
			adapter2.notifyDataSetChanged();
			mListView.setAdapter(adapter2);
			mPullDownView.notifyDidMore();
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					System.out.println("list2=" + list2.size());
					System.out.println("arg2=" + arg2);
					ArticleTitle article = list2.get(arg2 - 1);
					Intent i = new Intent();
					i.setClass(WeatherInfoActivity.this, ArticleActivity.class);
					Bundle bd = new Bundle();
					bd.putString("title", article.getArticle_title());
					bd.putString("fromwhere", "disater");
					bd.putInt("id", article.getArticle_id());
					i.putExtra("bd", bd);

					startActivityForResult(i, 3);
					// startActivity(i);
				}
			});
			mPullDownView.setOnPullDownListener(new OnPullDownListener() {
				@Override
				public void onRefresh() {
					getArtices1(2, "refresh");
				}

				@Override
				public void onMore() {
					System.out.println("hasNew=" + hasNew);
					if (hasNew) {
						getArtices1(2, "more");
					} else {
						// 从本地获取5条数据
						List<ArticleTitle> tempTitles = productDb
								.getArticleNames(3, userInfo.getAccount(),
										localTempLength, 5);
						localTempLength = localTempLength + tempTitles.size();
						mPullDownView.notifyDidMore();
						Message message = mUIHandler
								.obtainMessage(WHAT_DID_MORE_2);
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
			// loadData();
		} else {
			getArtices1(2, "loaddata");
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

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			ArticleTitle article = articles.get(arg0);
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
					tagName.setVisibility(View.GONE);
					TextView content = (TextView) v
							.findViewById(R.id.expert_page_content);
					content.setText(article.getArticle_content());
					TextView isRead = (TextView) v
							.findViewById(R.id.tv_is_read);
					System.out.println("ARTICLE==?" + article);
					if (0 == article.getIs_read()) {

						// isRead.setText("未读");
						// isRead.setTextColor(Color.parseColor("#FF0000"));
						isRead.setVisibility(View.VISIBLE);

					} else {

						isRead.setVisibility(View.GONE);
						// isRead.setText("已读");
						// isRead.setTextColor(Color.parseColor("#0000FF"));
					}

				}
			}
			return v;
		}

	}

	// 获取文章标题列表
	private List<ArticleTitle> getArtices1(final int i5, final String style) {
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
				// if(style.equals("refresh")){
				if (style.equals("more")) {
					if (tempTime == null || tempTime.equals("")) {
						limit = list2.size();
					} else {
						limit = tempLength;
						System.out.println("tempLength=" + tempLength);
					}
					offset = 5;
					tipLimit = 0;
					tipOffset = 0;
				} else if (style.equals("refresh")) {
					limit = 0;
					offset = 5;
					tipLimit = 0;
					tipOffset = 0;
				} else {
					limit = 0;
					offset = 10;
					tipLimit = 0;
					tipOffset = 0;
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
						object.put("updatedate",timeInfo.getWeatherWarningTime());
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
						article.setArticle_title(object.getString("article_title"));
						String publish = object.getString("article_publish_date");
						if (publish != null && !publish.equals("")) {
							article.setArticle_publish_date(object.getJSONObject("article_publish_date").getString("time"));
						}
						article.setArticle_publish_name(object.getString("article_user_name"));
						article.setArticle_content(object.getString("article_content"));
						article.setArticle_browser_count(object.getInt("article_browser_count"));
						article.setIs_read(0);
						int type = object.getInt("article_type");
						article.setArticle_type(type);
						article.setSave_userid(userInfo.getAccount());
						System.out.println("article=" + article.toString());
						if (!productDb.isArticleSaved(object.getInt("article_id"),userInfo.getAccount())) {
							productDb.saveArticleName(article);
							articles.add(article);
						}

						if (!style.equals("refresh")) {
							Long publishDate = 0L;
							if (articles.size() > 0) {
								publishDate = Long.parseLong(articles.get(
										articles.size() - 1)
										.getArticle_publish_date());
							}
							Long tempDate;
							if (tempTime.equals("") || tempTime == null) {
								tempDate = 0L;
							} else {
								tempDate = Long.parseLong(tempTime);
							}
							System.out.println("tempDate=" + tempDate);
							if (publishDate > tempDate) {
								hasNew = true;
							}
						}
					}
					//未读灾害预警信息条数
					unReadNums = productDb.getUnReadArticleCount(userInfo.getAccount(), 3);
					if(unReadNums>0){
						unreadNum1.setText(unReadNums+"");
						unreadNum1.setVisibility(View.VISIBLE);
					}else{
						unreadNum1.setVisibility(View.GONE);
					}
					JSONArray tips = (JSONArray) object1.get("tip");
					System.out.println("tip长度：" + tips.length());
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
						if (!productDb.isArticleSaved(object1.getInt("article_id"),userInfo.getAccount())) {
							productDb.saveArticleName(tip);
							tipList.add(tip);
						}
						System.out.println("11");
					}

					System.out.println("list2 联网后的长度：" + list2.size());
					if (articles.size() > 0) {
						// 保存最新一条灾害预警的发布时间
						weatherInfo.setDisasterWarningTitle(articles.get(0)
								.getArticle_title());
						weatherInfo.setDisaterId(articles.get(0)
								.getArticle_id());
					} else {
					}
					Message message = null;
					if (style.equals("refresh")) {
						if (articles.size() > 0) {
							timeInfo.setWeatherWarningTime(articles.get(
									articles.size() - 1)
									.getArticle_publish_date());
						}
						mPullDownView.RefreshComplete();
						message = mUIHandler.obtainMessage(WHAT_DID_REFRESH_2);
						Collections.reverse(articles);
						message.obj = articles;
						message.sendToTarget();
					}
					if (style.equals("more")) {
						mPullDownView.notifyDidMore();
						message = mUIHandler.obtainMessage(WHAT_DID_MORE_2);
						message.obj = articles;
						message.sendToTarget();
					}
					if (style.equals("loaddata")) {
						if (articles.size() > 0) {
							timeInfo.setWeatherWarningTime(articles.get(0)
									.getArticle_publish_date());
						}
						System.out.println("view 2 articles长度：" + list2.size());
						list2 = articles;
						for (int i = 0; i < list2.size(); i++) {
							System.out.println(list2.get(i)
									.getArticle_publish_date());
						}
						for (int i = 0; i < list2.size(); i++) {
							System.out.println(list2.get(i)
									.getArticle_publish_date());
						}
						System.out.println("view 2 list2长度：" + list2.size());
						adapter2 = new WarnAdapter(WeatherInfoActivity.this,
								list2, 2);
						adapter2.notifyDataSetChanged();
						mListView.setAdapter(adapter2);
						mListView.setSelection(list2.size() - 1);
						mPullDownView.notifyDidMore();
						mPullDownView.RefreshComplete();
						if (list2.size() <= 0) {
							Toast.makeText(WeatherInfoActivity.this,
									"没有灾害预警信息！", Toast.LENGTH_SHORT).show();
						}
						adapter2 = new WarnAdapter(WeatherInfoActivity.this,
								list2, 3);
						adapter2.notifyDataSetChanged();
						mListView.setAdapter(adapter2);
						mListView
								.setOnItemClickListener(new OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										System.out.println("list2="
												+ list2.size());
										System.out.println("arg2=" + arg2);
										ArticleTitle article = list2
												.get(arg2 - 1);
										Intent i = new Intent();
										i.setClass(WeatherInfoActivity.this,
												ArticleActivity.class);
										Bundle bd = new Bundle();
										bd.putString("title",
												article.getArticle_title());
										bd.putString("fromwhere", "disater");
										bd.putInt("id", article.getArticle_id());
										i.putExtra("bd", bd);
										startActivity(i);
									}
								});
						mPullDownView
								.setOnPullDownListener(new OnPullDownListener() {
									@Override
									public void onRefresh() {
										getArtices1(2, "refresh");
										// 下拉的时候获取服务器最先消息
									}

									@Override
									public void onMore() {
										if (hasNew) {
											getArtices1(2, "more");
										} else {
											// 从本地获取5条数据
											List<ArticleTitle> tempTitles = productDb
													.getArticleNames(
															3,
															userInfo.getAccount(),
															localTempLength, 5);
											mPullDownView.notifyDidMore();
											Message message = mUIHandler
													.obtainMessage(WHAT_DID_REFRESH_2);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.currentCity:
			Intent i = new Intent();
			i.setClass(WeatherInfoActivity.this, SetCityActivity.class);
			startActivityForResult(i, 1);
			break;
		default:
			break;
		}

	}
}
