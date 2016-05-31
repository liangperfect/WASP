package com.xiaoguo.wasp.mobile.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ParseDate2Week {
	
	@SuppressLint("SimpleDateFormat")
	public static String getWeek(String pTime) {
		  String Week = "周";
		  SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		  Calendar c = Calendar.getInstance();
		  try {
			  c.setTime(format.parse(pTime));
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 1) {
		   Week += "日";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 2) {
		   Week += "一";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 3) {
		   Week += "二";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 4) {
		   Week += "三";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 5) {
		   Week += "四";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 6) {
		   Week += "五";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 7) {
		   Week += "六";
		  }
		  System.out.println("week="+Week);
		  return Week;
		 }
		
		@SuppressLint("SimpleDateFormat")
		public static String getWeek1(String pTime) {
			  String Week = "周";
			  SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			  Calendar c = Calendar.getInstance();
			  try {
				  c.setTime(format.parse(pTime));
			  } catch (ParseException e) {
			   e.printStackTrace();
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			   Week += "日";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			   Week += "一";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			   Week += "二";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			   Week += "三";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			   Week += "四";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			   Week += "五";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			   Week += "六";
			  }
			  System.out.println("week="+Week);
			  return Week;
			 }

}
