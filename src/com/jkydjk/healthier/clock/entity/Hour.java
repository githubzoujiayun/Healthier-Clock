package com.jkydjk.healthier.clock.entity;

import java.util.HashMap;
import java.util.Map;

import com.jkydjk.healthier.clock.ChineseHour;
import com.jkydjk.healthier.clock.database.DatabaseManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Hour {

  private long id;
  private String name;
  private String timeInterval;
  private String appropriate;
  private String taboo;

  /**
   * #根据给定时间(小时)返回对应时辰
   * 
   * @param hour
   * @return
   */
  public static int from_time_hour(int hour) {
    int a = hour % 2;

    if (a == 0) {
      return hour / 2;
    } else {
      return (hour + 1) / 2;
    }
  }

  public static Map<Long, Hour> all(Context context) {
    Map<Long, Hour> hours = null;
    SQLiteDatabase database = DatabaseManager.openDatabase(context);
    Cursor cursor = database.rawQuery("select * from hours", null);
    if (cursor != null && cursor.moveToFirst()) {
      hours = new HashMap<Long, Hour>();
      do {
        Hour hour = new Hour();
        hour.id = cursor.getLong(cursor.getColumnIndex("_id"));
        hour.name = cursor.getString(cursor.getColumnIndex("name"));
        hour.timeInterval = cursor.getString(cursor.getColumnIndex("interval"));
        hour.appropriate = cursor.getString(cursor.getColumnIndex("appropriate"));
        hour.taboo = cursor.getString(cursor.getColumnIndex("taboo"));
        hours.put(hour.id, hour);
      } while (cursor.moveToNext());
      cursor.close();
    }
    database.close();
    return hours;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTimeInterval() {
    return timeInterval;
  }

  public void setTimeInterval(String timeInterval) {
    this.timeInterval = timeInterval;
  }

  public String getAppropriate() {
    return appropriate;
  }

  public void setAppropriate(String appropriate) {
    this.appropriate = appropriate;
  }

  public String getTaboo() {
    return taboo;
  }

  public void setTaboo(String taboo) {
    this.taboo = taboo;
  }

}
