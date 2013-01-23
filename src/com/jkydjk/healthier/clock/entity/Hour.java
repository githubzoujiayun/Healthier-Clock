package com.jkydjk.healthier.clock.entity;

import com.jkydjk.healthier.clock.database.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Hour {

  public static final int[] startHours = { 23, 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21 };

  private int id;
  private String name;
  private String timeInterval;
  private String appropriate;
  private String taboo;

  /**
   * #根据给定时间(小时)返回对应时辰
   * 
   * @param hour
   * @return 1~12
   */
  public static int from_time_hour(int hour) {
    if (hour == 0 || hour == 23) {
      return 1;
    }

    if (hour % 2 == 0) {
      return hour / 2 + 1;
    } else {
      return (hour + 1) / 2 + 1;
    }
  }

  /**
   * return start hour
   * 
   * @return
   */
  public int getStartHour() {
    return startHours[id - 1];
  }

  /**
   * 根据ID查找
   * 
   * @param context
   * @param hourID
   * @return
   */
  public static Hour find(Context context, long hourID) {
    Hour hour = null;

    SQLiteDatabase database = new DatabaseHelper(context).getWritableDatabase();

    Cursor cursor = database.rawQuery("select * from hours where _id = ?", new String[] { hourID + "" });
    if (cursor != null && cursor.moveToFirst()) {
      hour = new Hour();
      hour.id = cursor.getInt(cursor.getColumnIndex("_id"));
      hour.name = cursor.getString(cursor.getColumnIndex("name"));
      hour.timeInterval = cursor.getString(cursor.getColumnIndex("interval"));
      hour.appropriate = cursor.getString(cursor.getColumnIndex("appropriate"));
      hour.taboo = cursor.getString(cursor.getColumnIndex("taboo"));
      cursor.close();
    }
    database.close();

    return hour;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
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
