package com.xiaoguo.wasp.mobile.ui.weatherinfo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xiaoguo.wasp.R;
import com.xiaoguo.wasp.mobile.database.WeatherInfoDb;
import com.xiaoguo.wasp.mobile.model.UserSettingInfo;
import com.xiaoguo.wasp.mobile.model.WeatherDbInfo;
import com.xiaoguo.wasp.mobile.model.WeatherUnit;
import com.xiaoguo.wasp.mobile.network.CommandBase;
import com.xiaoguo.wasp.mobile.utils.SharedPreferencesUtils;
import com.xiaoguo.wasp.mobile.utils.TimeUtil;
import com.xiaoguo.wasp.mobile.widget.WeatherItem;

public class WeatherActivityBaiDu extends Activity implements OnClickListener {
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	private ImageView imageWeatherTitle, secondweatherImageItem,
			thirdweatherImageItem, fourweatherImageItem;
	private TextView currentCityTitle, currentCity, temperatureTitle,
			temperature, weatherAttrs, temperatureRange, atmosphere, clothes,
			secondDayItem, thirdDayItem, fourDayItem, secondweatherAttrsItem,
			fouthWeatherAttrsItem, thirdweatherAttrsItem,
			secondTemperatureRangeItem, thirdTemperatureRangeItem,
			fourTemperatureRangeItem, secondWindItem, thirdWindItem,
			fourWindItem, title, localTime;
	private ListView weatherListView;
	private WeatherItem secondItem, ThirdItem, fourItem;
	private AsyncHttpClient client;
	private String city = "宜春";
	private String baiduInterface = "http://api.map.baidu.com/telematics/v3/weather?location="
			+ city + "&output=json&ak=hDzDLLo2wOlBV9UUDfvvPVkw";
	private String currentDate, currentTemperatureStr;
	private ArrayList<WeatherUnit> weatherList;
	private WeatherAdapter mWeatherAdapter;
	private Toast mToast;
	private WeatherDbInfo weatherInfo;
	private WeatherInfoDb weatherDb;
	private SharedPreferencesUtils mSharePreferencesUtils;
	private CommandBase commandBase;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	private TimeUtil mTimeUtil;
	private String localTimeStr;
	private UserSettingInfo userSettingInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		initView();
	}

	private void initView() {
		// 保存有用户相关信息
		userSettingInfo = new UserSettingInfo(WeatherActivityBaiDu.this);
		// 默认是临汾
		String refreshCity = "宜春";
		if (!userSettingInfo.getWeatherAddress().equals("")) {
			refreshCity = userSettingInfo.getWeatherAddress();
		}

		// 获取本地时间
		localTime = (TextView) findViewById(R.id.local_time);
		mTimeUtil = TimeUtil.getTimeUtilInstance();
		localTimeStr = mTimeUtil.getLocalTime();
		localTime.setText(localTimeStr);

		// 图片加载的设置
		initImageLoader(WeatherActivityBaiDu.this);
		mImageLoader = ImageLoader.getInstance();
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ico_yixin_gray)
				.showImageOnFail(R.drawable.ico_yixin_gray)
				.showImageOnLoading(R.drawable.ico_yixin_gray)
				.cacheOnDisc(true).cacheInMemory(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();

		commandBase = CommandBase.instance();
		commandBase.setCurrActivityContext(WeatherActivityBaiDu.this,
				WeatherActivityBaiDu.this);
		mSharePreferencesUtils = new SharedPreferencesUtils();
		weatherInfo = new WeatherDbInfo();
		weatherDb = new WeatherInfoDb(WeatherActivityBaiDu.this);
		mToast = new Toast(WeatherActivityBaiDu.this);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.weather_info);
		secondItem = (WeatherItem) findViewById(R.id.secondDay);
		ThirdItem = (WeatherItem) findViewById(R.id.thiredDay);
		fourItem = (WeatherItem) findViewById(R.id.fourthDay);
		// 第二天item
		secondDayItem = (TextView) secondItem.findViewById(R.id.day_item);
		secondweatherImageItem = (ImageView) secondItem
				.findViewById(R.id.weather_image_item);
		secondweatherAttrsItem = (TextView) secondItem
				.findViewById(R.id.weather_attrs_item);
		secondTemperatureRangeItem = (TextView) secondItem
				.findViewById(R.id.temperature_range_item);
		secondWindItem = (TextView) secondItem.findViewById(R.id.wind_item);
		// 第三天item
		thirdDayItem = (TextView) ThirdItem.findViewById(R.id.day_item);
		thirdweatherImageItem = (ImageView) ThirdItem
				.findViewById(R.id.weather_image_item);
		thirdweatherAttrsItem = (TextView) ThirdItem
				.findViewById(R.id.weather_attrs_item);
		thirdTemperatureRangeItem = (TextView) ThirdItem
				.findViewById(R.id.temperature_range_item);
		thirdWindItem = (TextView) ThirdItem.findViewById(R.id.wind_item);
		// 第四天的item
		fourDayItem = (TextView) fourItem.findViewById(R.id.day_item);
		fourweatherImageItem = (ImageView) fourItem
				.findViewById(R.id.weather_image_item);
		fouthWeatherAttrsItem = (TextView) fourItem
				.findViewById(R.id.weather_attrs_item);
		fourTemperatureRangeItem = (TextView) fourItem
				.findViewById(R.id.temperature_range_item);
		fourWindItem = (TextView) fourItem.findViewById(R.id.wind_item);

		weatherList = new ArrayList<WeatherUnit>();
		imageWeatherTitle = (ImageView) findViewById(R.id.image_weather_title);
		currentCityTitle = (TextView) findViewById(R.id.current_city_title);
		currentCity = (TextView) findViewById(R.id.current_city);
		currentCity.setOnClickListener(this);
		temperatureTitle = (TextView) findViewById(R.id.temperature_title);
		temperature = (TextView) findViewById(R.id.temperature);
		weatherAttrs = (TextView) findViewById(R.id.weather_attrs);
		temperatureRange = (TextView) findViewById(R.id.temperature_range);
		atmosphere = (TextView) findViewById(R.id.atmosphere);
		clothes = (TextView) findViewById(R.id.clothes);
		weatherListView = (ListView) findViewById(R.id.weather_list);
		// 获取数据库保存的天气数据
		// WeatherDbInfo mWeatherDbInfo = new WeatherDbInfo();
		// mWeatherDbInfo = weatherDb.getWeatherInfo();

		// 获取用户之前刷新所获得的城市 默认城市是临汾
		city = refreshCity;
		currentCity.setText(city);
		getWeatherData();

		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getWeatherData();

					}
				});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
	}

	public void getWeatherData() {
		client = new AsyncHttpClient();
		System.out.println("BAIDUiNTERFA---->>" + baiduInterface);
		client.get(baiduInterface, null, new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(int arg0, JSONObject data) {
				super.onSuccess(arg0, data);
				System.out.println("返回的天气数据是-->>" + data.toString());
				try {
					currentDate = data.getString("date");
					JSONArray result = data.getJSONArray("results");
					JSONObject object1 = result.getJSONObject(0);
					// System.out.println("currentCity--->>"
					// + object1.getString("currentCity"));
					JSONArray weatherDataArray = object1
							.getJSONArray("weather_data");
					// 获取当天的天气温度
					JSONObject currentObject = weatherDataArray
							.getJSONObject(0);
					WeatherUnit currentWeatherUnit = new WeatherUnit();
					currentWeatherUnit.setDate(currentObject.getString("date"));
					String currentDate = currentObject.getString("date");
					// 将具体温度给截取到
					int leftIndex = currentDate.lastIndexOf("(");
					int rightIndex = currentDate.indexOf("℃");
					String temp = currentDate.substring(leftIndex + 4,
							rightIndex);

					// ----
					currentWeatherUnit.setDayPictureUrl(currentObject
							.getString("dayPictureUrl"));
					currentWeatherUnit.setNightPicture(currentObject
							.getString("nightPictureUrl"));
					currentWeatherUnit.setWeather(currentObject
							.getString("weather"));
					currentWeatherUnit.setWind(currentObject.getString("wind"));
					currentWeatherUnit.setTemperature(currentObject
							.getString("temperature"));

					String dateStr = currentObject.getString("date");
					String date = dateStr.substring(0, dateStr.indexOf("(") - 1);

					currentCityTitle.setText(date);
					weatherInfo.setCurrentTime(date);

					temperature.setText(temp);
					weatherInfo.setCurrentTemperature(temp);
					System.out.println("temperaturetemperature-->>"
							+ temperature);
					weatherAttrs.setText(currentObject.getString("weather"));
					weatherInfo.setCurrentTemperatureAttrs(currentObject
							.getString("weather"));
					temperatureRange.setText(currentObject
							.getString("temperature"));
					weatherInfo.setCurrentYuBao(currentObject
							.getString("temperature"));
					atmosphere.setText(currentObject.getString("wind"));
					weatherInfo.setCurrentQiXiang(currentObject
							.getString("wind"));
					// 获取穿衣指数
					JSONArray indexObject = object1.getJSONArray("index");
					JSONObject indexObject1 = indexObject.getJSONObject(0);
					currentWeatherUnit.setClothes(indexObject1.getString("zs"));
					clothes.setText(indexObject1.getString("zs"));
					weatherInfo.setCurrentCloths(indexObject1.getString("zs"));
					weatherInfo.setCurrentCity(city);
					// 获取明天的天气温度
					JSONObject nextObject = weatherDataArray.getJSONObject(1);
					WeatherUnit nextWeatherUnit = new WeatherUnit();
					nextWeatherUnit.setDate(nextObject.getString("date"));
					nextWeatherUnit.setDayPictureUrl(nextObject
							.getString("dayPictureUrl"));
					nextWeatherUnit.setNightPicture(nextObject
							.getString("nightPictureUrl"));
					nextWeatherUnit.setWeather(nextObject.getString("weather"));
					nextWeatherUnit.setWind(nextObject.getString("wind"));
					nextWeatherUnit.setTemperature(nextObject
							.getString("temperature"));

					weatherInfo.setSecondTime(nextObject.getString("date"));
					weatherInfo.setSecondWeatherAttrs(nextObject
							.getString("weather"));
					weatherInfo.setSecondTemperatureRange(nextObject
							.getString("temperature"));
					weatherInfo.setSecondWind(nextObject.getString("wind"));

					// 获取两天后的天气温度
					JSONObject afterTomorrowObject = weatherDataArray
							.getJSONObject(2);
					WeatherUnit afterTomorrowWeatherUnit = new WeatherUnit();
					afterTomorrowWeatherUnit.setDate(afterTomorrowObject
							.getString("date"));
					afterTomorrowWeatherUnit
							.setDayPictureUrl(afterTomorrowObject
									.getString("dayPictureUrl"));
					afterTomorrowWeatherUnit
							.setNightPicture(afterTomorrowObject
									.getString("nightPictureUrl"));
					afterTomorrowWeatherUnit.setWeather(afterTomorrowObject
							.getString("weather"));
					afterTomorrowWeatherUnit.setWind(afterTomorrowObject
							.getString("wind"));
					afterTomorrowWeatherUnit.setTemperature(afterTomorrowObject
							.getString("temperature"));

					weatherInfo.setThirdTime(afterTomorrowObject
							.getString("date"));
					weatherInfo.setThirdTemperatureRange(afterTomorrowObject
							.getString("temperature"));
					weatherInfo.setThirdWeatherAttrs(afterTomorrowObject
							.getString("weather"));
					weatherInfo.setThirdWind(afterTomorrowObject
							.getString("wind"));

					// 获取三天后的温度
					JSONObject threeAfterDayObject = weatherDataArray
							.getJSONObject(3);
					WeatherUnit threeAfterDayWeatherUnit = new WeatherUnit();
					threeAfterDayWeatherUnit.setDate(threeAfterDayObject
							.getString("date"));
					threeAfterDayWeatherUnit
							.setDayPictureUrl(threeAfterDayObject
									.getString("dayPictureUrl"));
					threeAfterDayWeatherUnit
							.setNightPicture(threeAfterDayObject
									.getString("nightPictureUrl"));
					threeAfterDayWeatherUnit.setWeather(threeAfterDayObject
							.getString("weather"));
					threeAfterDayWeatherUnit.setWind(threeAfterDayObject
							.getString("wind"));
					threeAfterDayWeatherUnit.setTemperature(threeAfterDayObject
							.getString("temperature"));
					weatherInfo.setFourTime(threeAfterDayObject
							.getString("date"));
					weatherInfo.setFourTemperature(threeAfterDayObject
							.getString("temperature"));
					weatherInfo.setFourWeatherAttrs(threeAfterDayObject
							.getString("weather"));
					weatherInfo.setFourWind(threeAfterDayObject
							.getString("wind"));

					// 保存到数据库
					// // boolean judge =
					// weatherDb.saveWeatherInfo(weatherInfo);
					// if (judge) {
					// System.out.println("保存天气数据陈宫");
					// } else {
					// System.out.println("保存出错");
					// }

					weatherList.add(currentWeatherUnit);
					weatherList.add(nextWeatherUnit);
					weatherList.add(afterTomorrowWeatherUnit);
					weatherList.add(threeAfterDayWeatherUnit);
					// mWeatherAdapter = new WeatherAdapter(weatherList,
					// WeatherActivityBaiDu.this);
					//
					// weatherListView.setAdapter(mWeatherAdapter);
					// 读取第二天的天气数据
					secondDayItem.setText(nextWeatherUnit.getDate());
					secondweatherAttrsItem.setText(nextWeatherUnit.getWeather());
					secondTemperatureRangeItem.setText(nextWeatherUnit
							.getTemperature());
					secondWindItem.setText(nextWeatherUnit.getWind());
					// 获取第三天的天气数据
					thirdDayItem.setText(afterTomorrowWeatherUnit.getDate());
					thirdweatherAttrsItem.setText(afterTomorrowWeatherUnit
							.getWeather());
					thirdTemperatureRangeItem.setText(afterTomorrowWeatherUnit
							.getTemperature());
					thirdWindItem.setText(afterTomorrowWeatherUnit.getWind());
					// 获取第四天的天气数据
					fourDayItem.setText(threeAfterDayWeatherUnit.getDate());
					fouthWeatherAttrsItem.setText(threeAfterDayWeatherUnit
							.getWeather());
					fourTemperatureRangeItem.setText(threeAfterDayWeatherUnit
							.getTemperature());
					fourWindItem.setText(threeAfterDayWeatherUnit.getWind());
					// mSharePreferencesUtils.setParam(WeatherActivityBaiDu.this,
					// "weatherCity", city);
					// System.out.println("city---->>" + city);

					// imageWeatherTitle, secondweatherImageItem,
					// thirdweatherImageItem, fourweatherImageItem;

					// 获取气象图片
					mImageLoader.displayImage(
							currentWeatherUnit.getDayPictureUrl(),
							imageWeatherTitle);
					mImageLoader.displayImage(
							nextWeatherUnit.getDayPictureUrl(),
							secondweatherImageItem);
					mImageLoader.displayImage(
							afterTomorrowWeatherUnit.getDayPictureUrl(),
							thirdweatherImageItem);
					mImageLoader.displayImage(
							threeAfterDayWeatherUnit.getDayPictureUrl(),
							fourweatherImageItem);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFinish() {
				super.onFinish();
				mPullRefreshScrollView.onRefreshComplete();
			}

			@Override
			public void onFailure(Throwable arg0) {
				super.onFailure(arg0);
				mPullRefreshScrollView.onRefreshComplete();

			}

		});

	}

	private class WeatherAdapter extends BaseAdapter {
		private List<WeatherUnit> list;
		private Context mContext;

		public WeatherAdapter(List<WeatherUnit> list, Context context) {
			this.list = list;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			System.out.println("list-->>" + list.size());
			System.out.println("positon-->>" + position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.weather_item, null);
				holder = new ViewHolder();
				holder.dayItem = (TextView) convertView
						.findViewById(R.id.day_item);
				holder.weatherImageItem = (ImageView) convertView
						.findViewById(R.id.weather_image_item);
				holder.weatherAttrsItem = (TextView) convertView
						.findViewById(R.id.weather_attrs_item);
				holder.temperatureRangeItem = (TextView) convertView
						.findViewById(R.id.temperature_range_item);
				holder.windItem = (TextView) convertView
						.findViewById(R.id.wind_item);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			WeatherUnit unit = list.get(position);
			holder.dayItem.setText(unit.getDate());
			holder.weatherImageItem.setBackgroundResource(R.drawable.a13);
			holder.weatherAttrsItem.setText(unit.getWeather());
			holder.temperatureRangeItem.setText(unit.getTemperature());
			holder.windItem.setText(unit.getWind());

			return convertView;
		}

	}

	public class ViewHolder {

		TextView dayItem;
		ImageView weatherImageItem;
		TextView weatherAttrsItem;
		TextView temperatureRangeItem;
		TextView windItem;

	}

	// 对ImageLoaderConfiguration进行初始化, 没有设置成Application的全局变量
	public static void initImageLoader(Context context) {

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();

		ImageLoader.getInstance().init(config);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.current_city:

			AlertDialog.Builder bulider = new AlertDialog.Builder(
					WeatherActivityBaiDu.this);
			bulider.setTitle(R.string.select_city);
			final String[] cities = { "宜春", "赣州", "吉安", "抚州" };

			bulider.setItems(cities, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					// mToast.cancel();
					System.out.println("cities[which]-->>" + cities[which]);
					city = cities[which];
					currentCity.setText(city);
					userSettingInfo.setWeatherAddress(city);
					baiduInterface = "http://api.map.baidu.com/telematics/v3/weather?location="
							+ city + "&output=json&ak=hDzDLLo2wOlBV9UUDfvvPVkw";
					getWeatherData();

					Toast.makeText(WeatherActivityBaiDu.this, "更新完毕",
							Toast.LENGTH_SHORT).show();
				}
			});
			bulider.show();
			break;
		}

	}

}
