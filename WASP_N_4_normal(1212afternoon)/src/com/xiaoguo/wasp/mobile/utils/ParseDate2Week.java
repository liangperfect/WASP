package com.xiaoguo.wasp.mobile.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ParseDate2Week {
	
	@SuppressLint("SimpleDateFormat")
	public static String getWeek(String pTime) {
		  String Week = "��";
		  SimpleDateFormat format = new SimpleDateFormat("yyyy��MM��dd��");
		  Calendar c = Calendar.getInstance();
		  try {
			  c.setTime(format.parse(pTime));
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 1) {
		   Week += "��";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 2) {
		   Week += "һ";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 3) {
		   Week += "��";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 4) {
		   Week += "��";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 5) {
		   Week += "��";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 6) {
		   Week += "��";
		  }
		  if (c.get(Calendar.DAY_OF_WEEK) == 7) {
		   Week += "��";
		  }
		  System.out.println("week="+Week);
		  return Week;
		 }
		
		@SuppressLint("SimpleDateFormat")
		public static String getWeek1(String pTime) {
			  String Week = "��";
			  SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			  Calendar c = Calendar.getInstance();
			  try {
				  c.setTime(format.parse(pTime));
			  } catch (ParseException e) {
			   e.printStackTrace();
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			   Week += "��";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			   Week += "һ";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			   Week += "��";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			   Week += "��";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			   Week += "��";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			   Week += "��";
			  }
			  if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			   Week += "��";
			  }
			  System.out.println("week="+Week);
			  return Week;
			 }

}
