package com.pxene.odin.cloud.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import static com.pxene.odin.cloud.common.constant.CodeTableConstant.*;

/**
 * 时间工具类
 * @author lizhuoling
 *
 */
public class DateUtils {
	
	/**
	 * 获取当前小时
	 * @return
	 */
	public static String getCurrentHour() {
		DateTime time = new DateTime();
		String strTime = time.toString("HH");
		return strTime;
	}
	
	/**
	 * 获取当前天
	 * @return
	 */
	public static Date getCurrenDay() {
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String strDay = sdf.format(time);
		Date day = null;
		// 转成Date类型
		try {
			day = sdf.parse(strDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}
	
	/**
	 * 获取当前天
	 * @return
	 */
	public static String getCurrentDay() {
		DateTime time = new DateTime();
		String strDay = time.toString("dd");
		return strDay;
	}
	
	/**
	 * 获取当前日期（年-月-日）
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		DateTime time = new DateTime();
		String string = time.toString("yyyy-MM-dd");
		return string;
	}
	
	
    public static Date strToDate(String dateStr, String formatStr) {
    	SimpleDateFormat format = new SimpleDateFormat(formatStr);
    	Date date = null;
    	try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}    	
    	return date;
    }
	
    public static Date changeDate(Date date, int field, int value) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(field, value);    	
    	return cal.getTime();
    }
		
}
