package com.xiaoguo.wasp.mobile.widget;

import com.xiaoguo.wasp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherItem extends LinearLayout {

	private TextView dayItem;

	private ImageView weatherIamgeItem;

	private TextView weatherAttrsItem;

	private TextView temperatureRangeItem;

	public WeatherItem(Context context) {
		super(context,null);

	}

	public WeatherItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.weather_item, this, true);

		dayItem = (TextView) findViewById(R.id.day_item);

		weatherIamgeItem = (ImageView) findViewById(R.id.weather_image_item);

		weatherAttrsItem = (TextView) findViewById(R.id.weather_attrs_item);

		temperatureRangeItem = (TextView) findViewById(R.id.temperature_range_item);

	}

	public TextView getDayItem() {
		return dayItem;
	}

	public void setDayItem(TextView dayItem) {
		this.dayItem = dayItem;
	}

	public ImageView getWeatherIamgeItem() {
		return weatherIamgeItem;
	}

	public void setWeatherIamgeItem(ImageView weatherIamgeItem) {
		this.weatherIamgeItem = weatherIamgeItem;
	}

	public TextView getWeatherAttrsItem() {
		return weatherAttrsItem;
	}

	public void setWeatherAttrsItem(TextView weatherAttrsItem) {
		this.weatherAttrsItem = weatherAttrsItem;
	}

	public TextView getTemperatureRangeItem() {
		return temperatureRangeItem;
	}

	public void setTemperatureRangeItem(TextView temperatureRangeItem) {
		this.temperatureRangeItem = temperatureRangeItem;
	}

}
