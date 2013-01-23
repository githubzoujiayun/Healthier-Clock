package com.jkydjk.healthier.clock.entity;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;

import com.jkydjk.healthier.clock.R;

/**
 * Days of week code as a single int. 0x00: no day 0x01: Monday 0x02: Tuesday
 * 0x04: Wednesday 0x08: Thursday 0x10: Friday 0x20: Saturday 0x40: Sunday
 * 
 * @author miclle
 */
public final class DaysOfWeek {

  private static int[] DAY_MAP = new int[] { Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY, };

  public static final int REPEATING_NEVER_DAYS = 0x00;
  public static final int REPEATING_EVERY_DAYS = 0x7f;

  public static final int WEEK_CODE_MONDAY = 0x01;
  public static final int WEEK_CODE_TUESDAY = 0x02;
  public static final int WEEK_CODE_WEDNESDAY = 0x04;
  public static final int WEEK_CODE_THURSDAY = 0x08;
  public static final int WEEK_CODE_FRIDAY = 0x10;
  public static final int WEEK_CODE_SATURDAY = 0x20;
  public static final int WEEK_CODE_SUNDAY = 0x40;

  // Bitmask of all repeating days
  private int mDays;

  public DaysOfWeek(int days) {
    mDays = days;
  }

  public DaysOfWeek(boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
    mDays = DaysOfWeek.getCycleCode(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
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

  /**
   * return week day cycle code
   * @param monday
   * @param tuesday
   * @param wednesday
   * @param thursday
   * @param friday
   * @param saturday
   * @param sunday
   * @return
   */
  public static int getCycleCode(boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
    int days = 0;

    if (monday)
      days += WEEK_CODE_MONDAY;
    if (tuesday)
      days += WEEK_CODE_TUESDAY;
    if (wednesday)
      days += WEEK_CODE_WEDNESDAY;
    if (thursday)
      days += WEEK_CODE_THURSDAY;
    if (friday)
      days += WEEK_CODE_FRIDAY;
    if (saturday)
      days += WEEK_CODE_SATURDAY;
    if (sunday)
      days += WEEK_CODE_SUNDAY;

    return days;
  }

}