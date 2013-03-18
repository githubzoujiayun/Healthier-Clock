package com.jkydjk.healthier.clock.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期帮助类
 * 
 * @author miclle
 * 
 */
@SuppressLint("SimpleDateFormat") 
public class DateHelper {

  final static SimpleDateFormat SIMPLE_DATE_FORMAT_yyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
  
  /**
   * 求两个日期相隔天数
   * 
   * @param cal1
   * @param cal2
   * @return
   */
  public static long daysAway(Calendar cal1, Calendar cal2) {

    long distance = Math.abs(cal1.getTimeInMillis() - cal2.getTimeInMillis());

    Log.v("distanceL: " + distance);

    return distance / (1000 * 60 * 60 * 24);
  }

  /**
   * 距离今天多少天
   * 
   * @param cal
   * @return
   * @throws ParseException
   */
  public static int daysAway(Calendar cal) throws ParseException {

    
    Date todayDate = SIMPLE_DATE_FORMAT_yyy_MM_dd.parse(SIMPLE_DATE_FORMAT_yyy_MM_dd.format(new Date()));
    
    Calendar today = Calendar.getInstance();
    today.setTime(todayDate);
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(cal.getTime());

    long distance = Math.abs(calendar.getTimeInMillis() - today.getTimeInMillis());

    return (int) (distance / (1000 * 60 * 60 * 24));
  }

}
