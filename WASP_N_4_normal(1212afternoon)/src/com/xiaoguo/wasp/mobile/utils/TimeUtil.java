package com.xiaoguo.wasp.mobile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.provider.ContactsContract.Contacts.Data;

public class TimeUtil {

	private static TimeUtil timeutil;

	public static TimeUtil getTimeUtilInstance() {

		if (timeutil == null) {
			timeutil = new TimeUtil();

		}
		return timeutil;

	}

	private TimeUtil() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 获取本地时间
	 * @return
	 */
	public String getLocalTime() {

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String result = format.format(date);
		return result;
	}

	/**
	 * 将long型的时间戳转换成时间字符串 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param time
	 * @return
	 */
	public static String getDateToString(long time) {
		Date d = new Date(time);
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日hh时");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}
	
	/**
	 * 将long型的时间戳转换成时间字符串 yyyy-MM-dd
	 * 
	 * @param time
	 * @return
	 */
	public static String getDateToStringType2(long time) {
		Date d = new Date(time);
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日hh时");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(d);
	}
	
	

	public String TimeStamp2Date(String timestampString) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date(Long.parseLong(timestampString)));
		return date;
	}

	public int getDate2Int(String dateStr) {

		String date = TimeStamp2Date(dateStr).split(" ")[0].replace("-", "");
		int resultDate = Integer.parseInt(date);
		return resultDate;
	}

	public String getDate(String dateStr) {
		String date = TimeStamp2Date(dateStr).split(" ")[0];
		return date;
	}

	public String getDateTime(String dateStr) {
		if (dateStr.split(" ").length > 1) {
			return dateStr.split(" ")[1];
		}
		return null;
	}

	public String getDate2Timescap(String time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1;
		String time1 = null;
		try {
			date1 = df.parse(time);
			Long l = date1.getTime();
			time1 = String.valueOf(l);

		} catch (ParseException e) {
			e.printStackTrace();
			time1 = "";
		}
		System.out.println("转换后的值=" + time1);
		return time1;
	}

	public int getTotalDate(String startDate, String currentDate) {
		int totalDay = 0;
		int startTotalDay = 0;
		int currentTotalDay = 0;
		String[] startStrs = startDate.split("-");
		int startYear = Integer.parseInt(startStrs[0]);
		if (startStrs[1].startsWith("0")) {
			startStrs[1] = startStrs[1].substring(1);
		}
		int startMonth = Integer.parseInt(startStrs[1]);

		if (startStrs[2].startsWith("0")) {
			startStrs[2] = startStrs[2].substring(1);
		}
		int startDay = Integer.parseInt(startStrs[2]);

		String[] currentStrs = currentDate.split("-");
		System.out.println("currentStrs=" + currentStrs.length);
		for (int i = 0; i < currentStrs.length; i++) {
			System.out.println("currentStrs=" + currentStrs[i]);
		}
		int currentYear = Integer.parseInt(currentStrs[0]);
		if (currentStrs[1].startsWith("0")) {
			currentStrs[1] = currentStrs[1].substring(1);
		}
		int currentMonth = Integer.parseInt(currentStrs[1]);
		if (currentStrs[2].startsWith("0")) {
			currentStrs[2] = currentStrs[2].substring(1);
		}
		int currentDay = Integer.parseInt(currentStrs[2]);

		startTotalDay = oneYearDay(startYear, startMonth, startDay);
		currentTotalDay = oneYearDay(currentYear, currentMonth, currentDay);
		int years = currentYear - startYear;
		int x = years % 4;
		int y = years / 4;
		if (x == 0) {
			totalDay = y * 1461 + currentTotalDay - startTotalDay + 1;
		} else if (x == 1) {
			totalDay = y * 1461 + (365 + currentTotalDay) - startTotalDay + 1;
			if ((currentYear - 1) % 4 == 0 && (currentYear - 1) % 100 != 0) {
				totalDay = totalDay + 1;
			}
		} else if (x == 2) {
			totalDay = y * 1461 + (365 * 2 + currentTotalDay) - startTotalDay
					+ 1;
			if (((currentYear - 1) % 4 == 0 && (currentYear - 1) % 100 != 0)
					|| ((currentYear - 2) % 4 == 0 && (currentYear - 2) % 100 != 0)) {
				totalDay = totalDay + 1;
			}
		} else {
			totalDay = y * 1461 + (365 * 3 + currentTotalDay) - startTotalDay
					+ 1;
			if (((currentYear - 1) % 4 == 0 && (currentYear - 1) % 100 != 0)
					|| ((currentYear - 2) % 4 == 0 && (currentYear - 2) % 100 != 0)
					|| ((currentYear - 3) % 4 == 0 && (currentYear - 3) % 100 != 0)) {
				totalDay = totalDay + 1;
			}
		}
		return totalDay;
	}

	public int oneYearDay(int year, int month, int day) {
		int days = 0;
		switch (month) {
		case 1:
			days = day;
			break;
		case 2:
			days = 31 + day;
			break;
		case 3:
			days = 31 + 28 + day;
			break;
		case 4:
			days = 31 + 28 + 31 + day;
			break;
		case 5:
			days = 31 + 28 + 31 + 30 + day;
			break;
		case 6:
			days = 31 + 28 + 31 + 30 + 31 + day;
			break;
		case 7:
			days = 31 + 28 + 31 + 30 + 31 + +30 + day;
			break;
		case 8:
			days = 31 + 28 + 31 + 30 + 31 + +30 + 31 + day;
			break;
		case 9:
			days = 31 + 28 + 31 + 30 + 31 + +30 + 31 + 30 + day;
			break;
		case 10:
			days = 31 + 28 + 31 + 30 + 31 + +30 + 31 + 30 + 31 + day;
			break;
		case 11:
			days = 31 + 28 + 31 + 30 + 31 + +30 + 31 + 30 + 31 + 30 + day;
			break;
		case 12:
			days = 31 + 28 + 31 + 30 + 31 + +30 + 31 + 30 + 31 + 30 + 31 + day;
			break;
		default:
			break;
		}
		if (year % 4 == 0 && year % 100 != 0) {// 闰年
			days = days + 1;
		}
		return days;
	}
}
