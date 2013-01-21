package com.jkydjk.healthier.clock.entity.columns;

import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmColumns implements BaseColumns {

  /**
   * The content:// style URL for this table
   */
  public static final Uri CONTENT_URI = Uri.parse("content://com.jkydjk.healthier.clock/alarm");

  /**
   * 闹铃分类
   * <p>
   * Type: STRING
   * </p>
   */
  public static final String CATEABLE_TYPE = "cateableType";
  
  /**
   * 闹铃分类关联对象ID
   * <p>
   * Type: INTEGER
   * </p>
   */
  public static final String CATEABLE_ID = "cateableId";
  
  /**
   * 闹铃的标签
   * <p>
   * Type: STRING
   * </p>
   */
  public static final String LABEL = "label";

  /**
   * Hour in 24-hour localtime 0 - 23.
   * <p>
   * Type: INTEGER
   * </p>
   */
  public static final String HOUR = "hour";

  /**
   * Minutes in localtime 0 - 59
   * <p>
   * Type: INTEGER
   * </p>
   */
  public static final String MINUTES = "minutes";

  /**
   * Days of week coded as integer
   * <p>
   * Type: INTEGER
   * </p>
   */
  public static final String DAYS_OF_WEEK = "days_of_week";

  /**
   * Alarm time in UTC milliseconds from the epoch.
   * <p>
   * Type: INTEGER
   * </p>
   */
  public static final String ALARM_TIME = "alarm_time";

  /**
   * True if alarm is active
   * <p>
   * Type: BOOLEAN
   * </p>
   */
  public static final String ENABLED = "enabled";

  /**
   * True if alarm should vibrate
   * <p>
   * Type: BOOLEAN
   * </p>
   */
  public static final String VIBRATE = "vibrate";

  /**
   * 响铃时的声音
   * <p>
   * Type: STRING
   * </p>
   */
  public static final String ALERT = "alert";

  /**
   * Remark to show when alarm triggers Note: not currently used
   * <p>
   * Type: STRING
   * </p>
   */
  public static final String REMARK = "remark";

  /**
   * 默认排序顺序
   */
  public static final String DEFAULT_SORT_ORDER = HOUR + ", " + MINUTES + " ASC";

  // Used when filtering enabled alarms.
  public static final String WHERE_ENABLED = ENABLED + "=1";

  static final String[] ALARM_QUERY_COLUMNS = { _ID, LABEL, HOUR, MINUTES, DAYS_OF_WEEK, ALARM_TIME, ENABLED, VIBRATE, ALERT, REMARK };

  /**
   * These save calls to cursor.getColumnIndexOrThrow() THEY MUST BE KEPT IN
   * SYNC WITH ABOVE QUERY COLUMNS
   */
  public static final int ALARM_ID_INDEX = 0;
  public static final int ALARM_LABEL_INDEX = 1;
  public static final int ALARM_HOUR_INDEX = 2;
  public static final int ALARM_MINUTES_INDEX = 3;
  public static final int ALARM_DAYS_OF_WEEK_INDEX = 4;
  public static final int ALARM_TIME_INDEX = 5;
  public static final int ALARM_ENABLED_INDEX = 6;
  public static final int ALARM_VIBRATE_INDEX = 7;
  public static final int ALARM_ALERT_INDEX = 8;
  public static final int ALARM_REMARK_INDEX = 9;

}
