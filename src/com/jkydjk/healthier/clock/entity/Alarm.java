package com.jkydjk.healthier.clock.entity;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * 
 * @author miclle
 * 
 */

@DatabaseTable(tableName = "alarms")
public final class Alarm implements Parcelable {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String label;// 标签

  @DatabaseField
  private boolean enabled;// 启用

  @DatabaseField
  private int hour;

  @DatabaseField
  private int minutes;

  @DatabaseField(columnName = "days_of_week")
  private int daysOfWeek;
  // public DaysOfWeek daysOfWeek;

  @DatabaseField
  private long time;

  @DatabaseField
  private boolean vibrate;

  @DatabaseField
  private String remark;

  @DatabaseField
  private String alert;
  // public Uri alert;

  @DatabaseField
  private boolean silent;

  public Alarm() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Alarm(Cursor c) {
    id = c.getInt(Columns.ALARM_ID_INDEX);
    label = c.getString(Columns.ALARM_LABEL_INDEX);
    enabled = c.getInt(Columns.ALARM_ENABLED_INDEX) == 1;
    hour = c.getInt(Columns.ALARM_HOUR_INDEX);
    minutes = c.getInt(Columns.ALARM_MINUTES_INDEX);
    daysOfWeek = new DaysOfWeek(c.getInt(Columns.ALARM_DAYS_OF_WEEK_INDEX));
    time = c.getLong(Columns.ALARM_TIME_INDEX);
    vibrate = c.getInt(Columns.ALARM_VIBRATE_INDEX) == 1;
    remark = c.getString(Columns.ALARM_REMARK_INDEX);
    String alertString = c.getString(Columns.ALARM_ALERT_INDEX);

    if (Alarms.ALARM_ALERT_SILENT.equals(alertString)) {
      if (Log.LOGV) {
        Log.v("报警被标记为无声");
      }
      silent = true;
    } else {
      if (alertString != null && alertString.length() != 0) {
        alert = Uri.parse(alertString);
      }
      // 如果数据库警报是空的，或未能解析，使用默认警报.
      if (alert == null) {
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
      }
    }
  }

  public Alarm(Parcel p) {
    id = p.readInt();
    enabled = p.readInt() == 1;
    hour = p.readInt();
    minutes = p.readInt();
    daysOfWeek = new DaysOfWeek(p.readInt());
    time = p.readLong();
    vibrate = p.readInt() == 1;
    remark = p.readString();
    alert = (Uri) p.readParcelable(null);
    silent = p.readInt() == 1;
  }

  public String getLabelOrDefault(Context context) {
    if (remark == null || remark.length() == 0) {
      return context.getString(R.string.default_label);
    }
    return remark;
  }

  /*
   * Days of week code as a single int. 0x00: no day 0x01: Monday 0x02: Tuesday
   * 0x04: Wednesday 0x08: Thursday 0x10: Friday 0x20: Saturday 0x40: Sunday
   */
  public static final class DaysOfWeek {

    private static int[] DAY_MAP = new int[] { Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY, };

    // Bitmask of all repeating days
    private int mDays;

    public DaysOfWeek(int days) {
      mDays = days;
    }

    public String toString(Context context, boolean showNever) {
      StringBuilder ret = new StringBuilder();

      // no days
      if (mDays == 0) {
        return showNever ? context.getText(R.string.never).toString() : "";
      }

      // every day
      if (mDays == 0x7f) {
        return context.getText(R.string.every_day).toString();
      }

      // count selected days
      int dayCount = 0, days = mDays;
      while (days > 0) {
        if ((days & 1) == 1)
          dayCount++;
        days >>= 1;
      }

      // short or long form?
      DateFormatSymbols dfs = new DateFormatSymbols();
      String[] dayList = (dayCount > 1) ? dfs.getShortWeekdays() : dfs.getWeekdays();

      // selected days
      for (int i = 0; i < 7; i++) {
        if ((mDays & (1 << i)) != 0) {
          ret.append(dayList[DAY_MAP[i]]);
          dayCount -= 1;
          if (dayCount > 0)
            ret.append(context.getText(R.string.day_concat));
        }
      }
      return ret.toString();
    }

    private boolean isSet(int day) {
      return ((mDays & (1 << day)) > 0);
    }

    public void set(int day, boolean set) {
      if (set) {
        mDays |= (1 << day);
      } else {
        mDays &= ~(1 << day);
      }
    }

    public void set(DaysOfWeek dow) {
      mDays = dow.mDays;
    }

    public int getCoded() {
      return mDays;
    }

    // Returns days of week encoded in an array of booleans.
    public boolean[] getBooleanArray() {
      boolean[] ret = new boolean[7];
      for (int i = 0; i < 7; i++) {
        ret[i] = isSet(i);
      }
      return ret;
    }

    public boolean isRepeatSet() {
      return mDays != 0;
    }

    /**
     * returns number of days from today until next alarm
     * 
     * @param c
     *          must be set to today
     */
    public int getNextAlarm(Calendar c) {
      if (mDays == 0) {
        return -1;
      }

      int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;

      int day = 0;
      int dayCount = 0;
      for (; dayCount < 7; dayCount++) {
        day = (today + dayCount) % 7;
        if (isSet(day)) {
          break;
        }
      }
      return dayCount;
    }
  }

  // ////////////////////////////
  // Parcelable apis
  // ////////////////////////////
  public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {

    public Alarm createFromParcel(Parcel p) {
      return new Alarm(p);
    }

    public Alarm[] newArray(int size) {
      return new Alarm[size];
    }
  };

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel p, int flags) {
    p.writeInt(id);
    p.writeInt(enabled ? 1 : 0);
    p.writeInt(hour);
    p.writeInt(minutes);
    p.writeInt(daysOfWeek.getCoded());
    p.writeLong(time);
    p.writeInt(vibrate ? 1 : 0);
    p.writeString(remark);
    p.writeParcelable(alert, flags);
    p.writeInt(silent ? 1 : 0);
  }

  // ////////////////////////////
  // end Parcelable apis
  // ////////////////////////////

  // ////////////////////////////
  // Column definitions
  // ////////////////////////////
  public static class Columns implements BaseColumns {
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://com.jkydjk.healthier.clock/alarm");

    /**
     * 闹铃的标签
     * <p>
     * Type: STRING
     * </p>
     */
    public static final String LABEL = "label";

    /**
     * 闹铃的分类
     * <p>
     * Type: STRING
     * </p>
     */
    public static final String CATEGORY = "category";

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
    public static final String DAYS_OF_WEEK = "daysofweek";

    /**
     * Alarm time in UTC milliseconds from the epoch.
     * <p>
     * Type: INTEGER
     * </p>
     */
    public static final String ALARM_TIME = "alarmtime";

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

    public static final String[] ALARM_QUERY_COLUMNS = { _ID, LABEL, HOUR, MINUTES, DAYS_OF_WEEK, ALARM_TIME, ENABLED, VIBRATE, ALERT, REMARK };

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

  // ////////////////////////////
  // End column definitions
  // ////////////////////////////

  /**
   * 增加方案闹钟
   */
  public static long addSolutionAlarm(Context context, Solution solution) {
    String alarmLabel = solution.getTitle();
    int alarmHour;
    int alarmMinutes;
    DaysOfWeek alarmDaysOfWeek;
    boolean alarmVibrate = true;
    Uri alarmAlert;
    String alarmRemark = solution.getEffect();

    Time t = new Time();
    t.setToNow();

    alarmHour = t.hour;
    alarmMinutes = t.minute;
    alarmDaysOfWeek = new DaysOfWeek(0);
    alarmAlert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

    long time = Alarms.addAlarm(context, alarmLabel, alarmHour, alarmMinutes, alarmDaysOfWeek, alarmVibrate, alarmAlert.toString(), alarmRemark);
    return time;
  }

}
