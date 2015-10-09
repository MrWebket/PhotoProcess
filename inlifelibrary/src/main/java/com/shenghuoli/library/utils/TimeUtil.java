package com.shenghuoli.library.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间转换
 * 
 * @author vendor
 */
public class TimeUtil {

    private static final String TAG = "TimeUtil";
    
	/**
	 * 时间格式：yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final String TIMEFORMAT1 = "yyyy-MM-dd HH:mm:ss.SSS";
	/**
	 * 时间格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String TIMEFORMAT2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 格式化 Date 日期
	 * 
	 * @param format
	 * @param date
	 * @return
	 */
	public static String getFormattingDate(String format, Date date) {
		String dateFormat = format;
		if (TextUtils.isEmpty(format)) {
			dateFormat = TIMEFORMAT2;
		}
		if (date == null) {
			date = new Date();
		}
		return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(date);
	}

	/**
	 * string型时间转换
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String convertTime(String timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date timeDate = null;
		try {
			timeDate = sdf.parse(timestamp);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (ParseException e) {
		    Log.e(TAG, e.getMessage());
		} catch (NullPointerException e) {
		    Log.e(TAG, e.getMessage());
		}

		if (timeDate == null) {
			return timestamp;
		}

		long interval = (System.currentTimeMillis() - timeDate.getTime()) / 1000;// 与现在时间相差秒数

		String timeStr = null;

		 if(interval <= 60){ //1分钟内 服务端的时间 可能和本地的有区别 所以小于0的 对于这个情况全部都显示刚刚
		 timeStr = "1分钟前";
		 }else if (interval < 60 * 60) { // 1小时内
			timeStr = (interval / 60 == 0 ? 1 : interval / 60 )+ "分钟前";
		} else if (interval < 24 * 60 * 60) { // 一天内
			timeStr = timestamp.substring(11, 16);
		} else if (interval < 2 * 24 * 60 * 60) { // 昨天
			timeStr = timestamp.substring(5, 10);
		} else if (interval < 3 * 24 * 60 * 60) { // 前天
			timeStr = timestamp.substring(5, 10);
		} else if (interval < 365 * 24 * 60 * 60) { // 一年
			timeStr = timestamp.substring(5, 10);
		} else {
			timeStr = timestamp.substring(0, timestamp.length() - 8);
		}

		return timeStr;
	}
	
	/**
	 * long型时间转换
	 * 
	 * @param longTime
	 * @return
	 */
	public static String convertTime(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String timestamp = sdf.format(new Date(longTime));

		return convertTime(timestamp);
	}

	/**
	 * 将long型时间转为固定格式的时间字符串
	 * 
	 * @param timeformat
	 * @param longTime
	 * @return
	 */
	public static String convertTimeToString(String timeformat, long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeformat, Locale.getDefault());
		Date date = new Date(longTime);
		String timestamp = sdf.format(date);

		return timestamp;
	}
	
	/**
	 * 将String类型时间转为long类型时间
	 * 
	 * @param timestamp
	 * @return
	 */
	public static long covertTimeToLong(String timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT2, Locale.getDefault());
		try {
			Date date = sdf.parse(timestamp);
			return date.getTime();
		} catch (ParseException e) {
		    Log.e(TAG, e.getMessage());
			return -1;
		}
	}

	/**
	 * long型时间转换
	 * 
	 * @param longTime
	 * @return 2013年7月3日 18:05(星期三)
	 */
	public static String convertTimeAndWeek(long longTime, String format) {
		if(TextUtils.isEmpty(format)) {
			format = "%d年%d月%d日 %s:%s(%s)";
		}

		Calendar c = Calendar.getInstance(); // 日历实例
		c.setTime(new Date(longTime));

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		String h = hour > 9 ? String.valueOf(hour) : "0" + hour;
		int minute = c.get(Calendar.MINUTE);
		String m = minute > 9 ? String.valueOf(minute) : "0" + minute;
		return String.format(Locale.getDefault(), format, year, month+1, date, h, m, converWeek(c.get(Calendar.DAY_OF_WEEK)));
	}

	/**
	 * long型时间转换
	 *
	 * @param longTime
	 * @return 2013年7月3日 星期三
	 */
	public static String convertTimeAndWeek(long longTime) {
		String format = "%d年%d月%d日 %s";

		Calendar c = Calendar.getInstance(); // 日历实例
		c.setTime(new Date(longTime));

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);

		return String.format(Locale.getDefault(), format, year, month+1, date, converWeek(c.get(Calendar.DAY_OF_WEEK)));
	}

	/**
	 * 转换数字的星期为字符串的
	 * 
	 * @param w
	 * @return
	 */
	private static String converWeek(int w) {
		String week = null;

		switch (w) {
		case 1:
			week = "星期日";
			break;
		case 2:
			week = "星期一";
			break;
		case 3:
			week = "星期二";
			break;
		case 4:
			week = "星期三";
			break;
		case 5:
			week = "星期四";
			break;
		case 6:
			week = "星期五";
			break;
		case 7:
			week = "星期六";
			break;
		}

		return week;
	}
}
