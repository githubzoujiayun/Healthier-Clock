package com.jkydjk.healthier.clock.entity;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.columns.AlarmColumns;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;

@DatabaseTable(tableName = "alarms")
public final class Alarm implements Parcelable {

  // Alarm category
  public static final int CATEGORY_SOLUTION = 1;

  // Public fields
  @DatabaseField(columnName = AlarmColumns.ID, generatedId = true)
  public int id;

  @DatabaseField(columnName = AlarmColumns.CATEGORY)
  public int category;

  @DatabaseField(columnName = AlarmColumns.CATEGORY_ABLE_ID)
  public int categoryAbleId;

  @DatabaseField(columnName = AlarmColumns.LABEL)
  public String label;

  @DatabaseField(columnName = AlarmColumns.ENABLED)
  public boolean enabled;

  @DatabaseField(columnName = AlarmColumns.HOUR)
  public int hour;

  @DatabaseField(columnName = AlarmColumns.MINUTES)
  public int minutes;

  /**
   * 周期<br />
   * Reference: daysOfWeek
   */
  @DatabaseField(columnName = AlarmColumns.DAYS_OF_WEEK)
  public int cycle;

  public DaysOfWeek daysOfWeek;

  @DatabaseField(columnName = AlarmColumns.ALARM_TIME)
  public long time;

  @DatabaseField(columnName = AlarmColumns.VIBRATE)
  public boolean vibrate;

  @DatabaseField(columnName = AlarmColumns.REMARK)
  public String remark;

  /**
   * 铃声<br />
   * Reference: alert
   */
  @DatabaseField(columnName = AlarmColumns.ALERT)
  public String ring;

  public Uri alert;

  public boolean silent;

  public Alarm() {
    super();
    // ORMLite need use
  }

  public Alarm(Cursor cursor) {

    Log.v("new Alarm(Cursor cursor)");

    id = cursor.getInt(AlarmColumns.ALARM_ID_INDEX);

    // id = cursor.getInt(cursor.getColumnIndex(AlarmColumns.ID));

    label = cursor.getString(AlarmColumns.ALARM_LABEL_INDEX);

    enabled = cursor.getInt(AlarmColumns.ALARM_ENABLED_INDEX) == 1;

    hour = cursor.getInt(AlarmColumns.ALARM_HOUR_INDEX);
    minutes = cursor.getInt(AlarmColumns.ALARM_MINUTES_INDEX);

    daysOfWeek = new DaysOfWeek(cursor.getInt(AlarmColumns.ALARM_DAYS_OF_WEEK_INDEX));

    time = cursor.getLong(AlarmColumns.ALARM_TIME_INDEX);
    vibrate = cursor.getInt(AlarmColumns.ALARM_VIBRATE_INDEX) == 1;
    remark = cursor.getString(AlarmColumns.ALARM_REMARK_INDEX);

    String alertString = cursor.getString(AlarmColumns.ALARM_ALERT_INDEX);

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
    label = p.readString();
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
    p.writeString(label);
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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public int getCategoryAbleId() {
    return categoryAbleId;
  }

  public void setCategoryAbleId(int categoryAbleId) {
    this.categoryAbleId = categoryAbleId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public int getMinutes() {
    return minutes;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public int getCycle() {
    return cycle;
  }

  public void setCycle(int cycle) {
    this.cycle = cycle;
  }

  public void setCycle(DaysOfWeek daysOfWeek) {
    this.cycle = daysOfWeek.getCoded();
  }

  public DaysOfWeek getDaysOfWeek() {
    return daysOfWeek;
  }

  public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
    this.daysOfWeek = daysOfWeek;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public boolean isVibrate() {
    return vibrate;
  }

  public void setVibrate(boolean vibrate) {
    this.vibrate = vibrate;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getRing() {
    return ring;
  }

  public void setRing(String ring) {
    this.ring = ring;
  }

  public Uri getAlert() {
    return alert;
  }

  public void setAlert(Uri alert) {
    this.alert = alert;
  }

  public boolean isSilent() {
    return silent;
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

}
