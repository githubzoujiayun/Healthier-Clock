package com.jkydjk.healthier.clock.util;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.AlarmClock;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.SolutionActivity;
import com.jkydjk.healthier.clock.ToastMaster;
import com.jkydjk.healthier.clock.database.AlarmDatabaseHelper;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.DaysOfWeek;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.entity.Ids;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.columns.AlarmColumns;

import org.json.JSONObject;

/**
 * The Alarms provider supplies info about Alarm Clock settings
 */
public class Alarms {

  // This action triggers the AlarmReceiver as well as the AlarmKlaxon. It
  // is a public action used in the manifest for receiving Alarm broadcasts
  // from the alarm manager.
  public static final String ALARM_ALERT_ACTION = "com.jkydjk.healthier.clock.ALARM_ALERT";

  // This is a private action used by the AlarmKlaxon to update the UI to
  // show the alarm has been killed.
  public static final String ALARM_KILLED = "alarm_killed";

  // Extra in the ALARM_KILLED intent to indicate to the user how long the
  // alarm played before being killed.
  public static final String ALARM_KILLED_TIMEOUT = "alarm_killed_timeout";

  // This string is used to indicate a silent alarm in the db.
  public static final String ALARM_ALERT_SILENT = "silent";

  // This intent is sent from the notification when the user cancels the
  // snooze alert.
  public static final String CANCEL_SNOOZE = "cancel_snooze";

  // This string is used when passing an Alarm object through an intent.
  public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";

  // This extra is the raw Alarm object data. It is used in the
  // AlarmManagerService to avoid a ClassNotFoundException when filling in
  // the Intent extras.
  public static final String ALARM_RAW_DATA = "intent.extra.alarm_raw";

  // This string is used to identify the alarm id passed to SetAlarm from the
  // list of alarms.
  public static final String ALARM_ID = "alarm_id";

  final static String PREF_SNOOZE_ID = "snooze_id";
  final static String PREF_SNOOZE_TIME = "snooze_time";

  private final static String DM12 = "E h:mm aa";
  private final static String DM24 = "E k:mm";

  private final static String M12 = "h:mm aa";
  // Shared with DigitalClock
  public final static String M24 = "kk:mm";

  /**
   * Creates a new Alarm.
   */
  public static Uri addAlarm(ContentResolver contentResolver) {
    ContentValues values = new ContentValues();
    values.put(AlarmColumns.HOUR, 8);
    return contentResolver.insert(AlarmColumns.CONTENT_URI, values);
  }

  /**
   * Creates a new Alarm.
   */
  public static long addAlarm(Context context, String label, int hour, int minutes, DaysOfWeek daysOfWeek, boolean vibrate, String alert, String remark) {

    ContentValues values = new ContentValues(8);

    ContentResolver resolver = context.getContentResolver();

    long time = 0;

    if (!daysOfWeek.isRepeatSet()) {
      time = calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();
    }

    values.put(AlarmColumns.LABEL, label);
    values.put(AlarmColumns.ENABLED, 1);
    values.put(AlarmColumns.HOUR, hour);
    values.put(AlarmColumns.MINUTES, minutes);
    values.put(AlarmColumns.ALARM_TIME, time);
    values.put(AlarmColumns.DAYS_OF_WEEK, daysOfWeek.getCoded());
    values.put(AlarmColumns.VIBRATE, vibrate);
    values.put(AlarmColumns.REMARK, remark);
    values.put(AlarmColumns.ALERT, alert);

    resolver.insert(AlarmColumns.CONTENT_URI, values);

    long timeInMillis = calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();

    SharedPreferences prefs = context.getSharedPreferences(AlarmClock.PREFERENCES, 0);

    long snoozeTime = prefs.getLong(PREF_SNOOZE_TIME, 0);
    if (timeInMillis < snoozeTime) {
      clearSnoozePreference(context, prefs);
    }

    setNextAlert(context);

    return timeInMillis;
  }

  /**
   * Removes an existing Alarm. If this alarm is snoozing, disables snooze. Sets
   * next alert.
   */
  public static void deleteAlarm(Context context, int alarmId) {

    ContentResolver contentResolver = context.getContentResolver();
    /* If alarm is snoozing, lose it */
    disableSnoozeAlert(context, alarmId);

    Uri uri = ContentUris.withAppendedId(AlarmColumns.CONTENT_URI, alarmId);
    contentResolver.delete(uri, "", null);

    setNextAlert(context);
  }

  /**
   * Queries all alarms
   *
   * @return cursor over all alarms
   */
  public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
    return contentResolver.query(AlarmColumns.CONTENT_URI, AlarmColumns.ALARM_QUERY_COLUMNS, null, null, AlarmColumns.DEFAULT_SORT_ORDER);
  }

  // Private method to get a more limited set of alarms from the database.
  private static Cursor getFilteredAlarmsCursor(ContentResolver contentResolver) {
    return contentResolver.query(AlarmColumns.CONTENT_URI, AlarmColumns.ALARM_QUERY_COLUMNS, AlarmColumns.WHERE_ENABLED, null, null);
  }

  /**
   * Return an Alarm object representing the alarm id in the database. Returns
   * null if no alarm exists.
   */
  public static Alarm getAlarm(ContentResolver contentResolver, int alarmId) {
    Cursor cursor = contentResolver.query(ContentUris.withAppendedId(AlarmColumns.CONTENT_URI, alarmId), AlarmColumns.ALARM_QUERY_COLUMNS, null, null, null);
    Alarm alarm = null;
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        alarm = new Alarm(cursor);
      }
      cursor.close();
    }
    return alarm;
  }

  /**
   * A convenience method to set an alarm in the Alarms content provider.
   * @param context
   * @param id
   * @param label
   * @param enabled
   * @param hour
   * @param minutes
   * @param daysOfWeek
   * @param vibrate
   * @param alert
   * @param remark
   * @return
   */
  public static long setAlarm(Context context, int id, String label, boolean enabled, int hour, int minutes, DaysOfWeek daysOfWeek, boolean vibrate, String alert, String remark) {

    ContentValues values = new ContentValues(8);
    ContentResolver resolver = context.getContentResolver();
    // Set the alarm_time value if this alarm does not repeat. This will be
    // used later to disable expired alarms.
    long time = 0;
    if (!daysOfWeek.isRepeatSet()) {
      time = calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();
    }

    if (Log.LOGV)
      Log.v("**  setAlarm * idx " + id + " hour " + hour + " minutes " + minutes + " enabled " + enabled + " time " + time);

    values.put(AlarmColumns.LABEL, label);
    values.put(AlarmColumns.ENABLED, enabled ? 1 : 0);
    values.put(AlarmColumns.HOUR, hour);
    values.put(AlarmColumns.MINUTES, minutes);
    values.put(AlarmColumns.ALARM_TIME, time);
    values.put(AlarmColumns.DAYS_OF_WEEK, daysOfWeek.getCoded());
    values.put(AlarmColumns.VIBRATE, vibrate);
    values.put(AlarmColumns.REMARK, remark);
    values.put(AlarmColumns.ALERT, alert);

    resolver.update(ContentUris.withAppendedId(AlarmColumns.CONTENT_URI, id), values, null, null);

    long timeInMillis = calculateAlarm(hour, minutes, daysOfWeek).getTimeInMillis();

    if (enabled) {
      // If this alarm fires before the next snooze, clear the snooze to
      // enable this alarm.
      SharedPreferences prefs = context.getSharedPreferences(AlarmClock.PREFERENCES, 0);
      long snoozeTime = prefs.getLong(PREF_SNOOZE_TIME, 0);
      if (timeInMillis < snoozeTime) {
        clearSnoozePreference(context, prefs);
      }
    }

    setNextAlert(context);

    return timeInMillis;
  }

  /**
   * A convenience method to enable or disable an alarm.
   *
   * @param id
   *          corresponds to the _id column
   * @param enabled
   *          corresponds to the ENABLED column
   */

  public static void enableAlarm(final Context context, final int id, boolean enabled) {
    enableAlarmInternal(context, id, enabled);
    setNextAlert(context);
  }

  private static void enableAlarmInternal(final Context context, final int id, boolean enabled) {
    enableAlarmInternal(context, getAlarm(context.getContentResolver(), id), enabled);
  }

  private static void enableAlarmInternal(final Context context, final Alarm alarm, boolean enabled) {
    ContentResolver resolver = context.getContentResolver();

    ContentValues values = new ContentValues(2);
    values.put(AlarmColumns.ENABLED, enabled ? 1 : 0);

    // If we are enabling the alarm, calculate alarm time since the time
    // value in Alarm may be old.
    if (enabled) {
      long time = 0;
      if (!alarm.daysOfWeek.isRepeatSet()) {
        time = calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis();
      }
      values.put(AlarmColumns.ALARM_TIME, time);
    }

    resolver.update(ContentUris.withAppendedId(AlarmColumns.CONTENT_URI, alarm.id), values, null, null);
  }

  public static Alarm calculateNextAlert(final Context context) {
    Alarm alarm = null;
    long minTime = Long.MAX_VALUE;
    long now = System.currentTimeMillis();
    Cursor cursor = getFilteredAlarmsCursor(context.getContentResolver());
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        do {
          Alarm a = new Alarm(cursor);
          // A time of 0 indicates this is a repeating alarm, so
          // calculate the time to get the next alert.
          if (a.time == 0) {
            a.time = calculateAlarm(a.hour, a.minutes, a.daysOfWeek).getTimeInMillis();
          } else if (a.time < now) {
            // Expired alarm, disable it and move along.
            enableAlarmInternal(context, a, false);
            continue;
          }
          if (a.time < minTime) {
            minTime = a.time;
            alarm = a;
          }
        } while (cursor.moveToNext());
      }
      cursor.close();
    }
    return alarm;
  }

  /**
   * Disables non-repeating alarms that have passed. Called at boot.
   */
  public static void disableExpiredAlarms(final Context context) {
    Cursor cur = getFilteredAlarmsCursor(context.getContentResolver());
    long now = System.currentTimeMillis();

    if (cur.moveToFirst()) {
      do {
        Alarm alarm = new Alarm(cur);
        // A time of 0 means this alarm repeats. If the time is
        // non-zero, check if the time is before now.
        if (alarm.time != 0 && alarm.time < now) {
          if (Log.LOGV) {
            Log.v("** DISABLE " + alarm.id + " now " + now + " set " + alarm.time);
          }
          enableAlarmInternal(context, alarm, false);
        }
      } while (cur.moveToNext());
    }
    cur.close();
  }

  /**
   * Called at system startup, on time/timezone change, and whenever the user
   * changes alarm settings. Activates snooze if set, otherwise loads all
   * alarms, activates next alert.
   */
  public static void setNextAlert(final Context context) {
    if (!enableSnoozeAlert(context)) {
      Alarm alarm = calculateNextAlert(context);
      if (alarm != null) {
        enableAlert(context, alarm, alarm.time);
      } else {
        disableAlert(context);
      }
    }
  }

  /**
   * Sets alert in AlarmManger and StatusBar. This is what will actually launch
   * the alert when the alarm triggers.
   *
   * @param alarm
   *          Alarm.
   * @param atTimeInMillis
   *          milliseconds since epoch
   */
  private static void enableAlert(Context context, final Alarm alarm, final long atTimeInMillis) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    if (Log.LOGV) {
      Log.v("** setAlert id " + alarm.id + " atTime " + atTimeInMillis);
    }

    Intent intent = new Intent(ALARM_ALERT_ACTION);

    // XXX: This is a slight hack to avoid an exception in the remote
    // AlarmManagerService process. The AlarmManager adds extra data to
    // this Intent which causes it to inflate. Since the remote process
    // does not know about the Alarm class, it throws a
    // ClassNotFoundException.
    //
    // To avoid this, we marshall the data ourselves and then parcel a plain
    // byte[] array. The AlarmReceiver class knows to build the Alarm
    // object from the byte[] array.
    Parcel out = Parcel.obtain();
    alarm.writeToParcel(out, 0);
    out.setDataPosition(0);
    intent.putExtra(ALARM_RAW_DATA, out.marshall());

    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    alarmManager.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);

    setStatusBarIcon(context, true);

    Calendar c = Calendar.getInstance();
    c.setTime(new java.util.Date(atTimeInMillis));
    String timeString = formatDayAndTime(context, c);
    saveNextAlarm(context, timeString);
  }

  /**
   * Disables alert in AlarmManger and StatusBar.
   * @param context
   */
  static void disableAlert(Context context) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent sender = PendingIntent.getBroadcast(context, 0, new Intent(ALARM_ALERT_ACTION), PendingIntent.FLAG_CANCEL_CURRENT);
    alarmManager.cancel(sender);
    setStatusBarIcon(context, false);
    saveNextAlarm(context, "");
  }

  public static void saveSnoozeAlert(final Context context, final int id, final long time) {
    SharedPreferences prefs = context.getSharedPreferences(AlarmClock.PREFERENCES, 0);
    if (id == -1) {
      clearSnoozePreference(context, prefs);
    } else {
      SharedPreferences.Editor ed = prefs.edit();
      ed.putInt(PREF_SNOOZE_ID, id);
      ed.putLong(PREF_SNOOZE_TIME, time);
      ed.commit();
    }
    // Set the next alert after updating the snooze.
    setNextAlert(context);
  }

  /**
   * Disable the snooze alert if the given id matches the snooze id.
   */
  public static void disableSnoozeAlert(final Context context, final int id) {
    SharedPreferences prefs = context.getSharedPreferences(AlarmClock.PREFERENCES, 0);
    int snoozeId = prefs.getInt(PREF_SNOOZE_ID, -1);
    if (snoozeId == -1) {
      // No snooze set, do nothing.
      return;
    } else if (snoozeId == id) {
      // This is the same id so clear the shared prefs.
      clearSnoozePreference(context, prefs);
    }
  }

  // Helper to remove the snooze preference. Do not use clear because that
  // will erase the clock preferences. Also clear the snooze notification in
  // the window shade.
  private static void clearSnoozePreference(final Context context, final SharedPreferences prefs) {
    final int alarmId = prefs.getInt(PREF_SNOOZE_ID, -1);
    if (alarmId != -1) {
      NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      nm.cancel(alarmId);
    }

    final SharedPreferences.Editor ed = prefs.edit();
    ed.remove(PREF_SNOOZE_ID);
    ed.remove(PREF_SNOOZE_TIME);
    ed.commit();
  };

  /**
   * If there is a snooze set, enable it in AlarmManager
   *
   * @return true if snooze is set
   */
  private static boolean enableSnoozeAlert(final Context context) {
    SharedPreferences prefs = context.getSharedPreferences(AlarmClock.PREFERENCES, 0);

    int id = prefs.getInt(PREF_SNOOZE_ID, -1);
    if (id == -1) {
      return false;
    }
    long time = prefs.getLong(PREF_SNOOZE_TIME, -1);

    // Get the alarm from the db.
    final Alarm alarm = getAlarm(context.getContentResolver(), id);
    // The time in the database is either 0 (repeating) or a specific time
    // for a non-repeating alarm. Update this value so the AlarmReceiver
    // has the right time to compare.
    alarm.time = time;

    enableAlert(context, alarm, time);
    return true;
  }

  /**
   * Tells the StatusBar whether the alarm is enabled or disabled
   */
  private static void setStatusBarIcon(Context context, boolean enabled) {
    // Intent alarmChanged = new Intent(Intent.ACTION_ALARM_CHANGED);
    Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
    alarmChanged.putExtra("alarmSet", enabled);
    context.sendBroadcast(alarmChanged);
  }

  /**
   * Given an alarm in hours and minutes, return a time suitable for setting in
   * AlarmManager.
   *
   * @param hour
   *          Always in 24 hour 0-23
   * @param minute
   *          0-59
   * @param daysOfWeek
   *          0-59
   */
  public static Calendar calculateAlarm(int hour, int minute, DaysOfWeek daysOfWeek) {

    // start with now
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());

    int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
    int nowMinute = calendar.get(Calendar.MINUTE);

    // if alarm is behind current time, advance one day
    if (hour < nowHour || hour == nowHour && minute <= nowMinute) {
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    int addDays = daysOfWeek.getNextAlarm(calendar);
    /*
     * Log.v("** TIMES * " + c.getTimeInMillis() + " hour " + hour + " minute "
     * + minute + " dow " + c.get(Calendar.DAY_OF_WEEK) + " from now " +
     * addDays);
     */
    if (addDays > 0)
      calendar.add(Calendar.DAY_OF_WEEK, addDays);
    return calendar;
  }

  public static String formatTime(final Context context, int hour, int minute, DaysOfWeek daysOfWeek) {
    Calendar c = calculateAlarm(hour, minute, daysOfWeek);
    return formatTime(context, c);
  }

  /* used by AlarmAlert */
  public static String formatTime(final Context context, Calendar c) {
    String format = get24HourMode(context) ? M24 : M12;
    return (c == null) ? "" : (String) DateFormat.format(format, c);
  }

  /**
   * Shows day and time -- used for lock screen
   */
  private static String formatDayAndTime(final Context context, Calendar c) {
    String format = get24HourMode(context) ? DM24 : DM12;
    return (c == null) ? "" : (String) DateFormat.format(format, c);
  }

  /**
   * Save time of the next alarm, as a formatted string, into the system
   * settings so those who care can make use of it.
   */
  static void saveNextAlarm(final Context context, String timeString) {
    Settings.System.putString(context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED, timeString);
  }

  /**
   * @return true if clock is set to 24-hour mode
   */
  public static boolean get24HourMode(final Context context) {
    return android.text.format.DateFormat.is24HourFormat(context);
  }

  /**
   * 显示Toast提示
   *
   * @param context
   * @param timeInMillis
   */
  public static void popAlarmSetToast(Context context, long timeInMillis) {
    String toastText = formatToast(context, timeInMillis);
    Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
    ToastMaster.setToast(toast);
    toast.show();
  }

  /**
   * format "Alarm set for 2 days 7 hours and 53 minutes from now"
   */
  static String formatToast(Context context, long timeInMillis) {
    long delta = timeInMillis - System.currentTimeMillis();
    long hours = delta / (1000 * 60 * 60);
    long minutes = delta / (1000 * 60) % 60;
    long days = hours / 24;
    hours = hours % 24;

    String daySeq = (days == 0) ? "" : (days == 1) ? context.getString(R.string.day) : context.getString(R.string.days, Long.toString(days));
    String minSeq = (minutes == 0) ? "" : (minutes == 1) ? context.getString(R.string.minute) : context.getString(R.string.minutes, Long.toString(minutes));
    String hourSeq = (hours == 0) ? "" : (hours == 1) ? context.getString(R.string.hour) : context.getString(R.string.hours, Long.toString(hours));

    boolean dispDays = days > 0;
    boolean dispHour = hours > 0;
    boolean dispMinute = minutes > 0;

    int index = (dispDays ? 1 : 0) | (dispHour ? 2 : 0) | (dispMinute ? 4 : 0);

    String[] formats = context.getResources().getStringArray(R.array.alarm_set);
    return String.format(formats[index], daySeq, hourSeq, minSeq);
  }

  /**
   * 增加方案闹钟
   */
  public static long addSolutionAlarm(Context context, GenericSolution genericSolution) {

    String solutionType = genericSolution.getType();

    if (GenericSolution.Type.MASSAGE_SOLUTION.equals(solutionType) || GenericSolution.Type.MOXIBUSTION_SOLUTION.equals(solutionType)
      || GenericSolution.Type.CUPPING_SOLUTION.equals(solutionType) || GenericSolution.Type.SKIN_SCRAPING_SOLUTION.equals(solutionType)){

      long timeInMillis = 0;

      AlarmDatabaseHelper help = new AlarmDatabaseHelper(context);

      try {
        Dao<Alarm, Integer> alarmDao = help.getAlarmDao();

        Alarm alarm = new Alarm();

        alarm.setCategory(Alarm.CATEGORY_SOLUTION);

        alarm.setCategoryAbleId(genericSolution.getId());

        alarm.setLabel(genericSolution.getTitle());

        JSONObject solutionJSON = new JSONObject(genericSolution.getData());

        int startedAt = solutionJSON.getInt("started_at");

        alarm.setHour(startedAt);
//      alarm.setMinutes(0);
        alarm.setEnabled(true);
        alarm.setCycle(DaysOfWeek.REPEATING_EVERY_DAYS);
        alarm.setVibrate(true);
        // alarm.setRing(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
        alarm.setRemark(genericSolution.getIntro());

        alarmDao.create(alarm);

        timeInMillis = calculateAlarm(alarm.getHour(), alarm.getMinutes(), new DaysOfWeek(alarm.getCycle())).getTimeInMillis();

        SharedPreferences prefs = context.getSharedPreferences(AlarmClock.PREFERENCES, 0);

        long snoozeTime = prefs.getLong(PREF_SNOOZE_TIME, 0);

        if (timeInMillis < snoozeTime) {
          clearSnoozePreference(context, prefs);
        }

        setNextAlert(context);

      } catch (Exception e) {
        e.printStackTrace();
      }

      return timeInMillis;

    } else {
      return 0;
    }
  }

}
