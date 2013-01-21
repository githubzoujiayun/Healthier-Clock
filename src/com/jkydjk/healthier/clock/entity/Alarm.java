package com.jkydjk.healthier.clock.entity;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.columns.AlarmColumns;
import com.jkydjk.healthier.clock.util.Alarms;

/**
 * 
 * @author miclle
 * 
 */
@DatabaseTable(tableName = "alarms")
public final class Alarm implements Parcelable {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField(columnName = AlarmColumns.CATEABLE_TYPE)
  private String cateableType;

  @DatabaseField(columnName = AlarmColumns.CATEABLE_ID)
  private int cateableId;

  @DatabaseField(columnName = AlarmColumns.LABEL)
  private String label;// 标签

  @DatabaseField(columnName = AlarmColumns.HOUR)
  private int hour;

  @DatabaseField(columnName = AlarmColumns.MINUTES)
  private int minutes;

  @DatabaseField(columnName = AlarmColumns.DAYS_OF_WEEK)
  private int daysOfWeek;
  // public DaysOfWeek daysOfWeek;

  @DatabaseField(columnName = AlarmColumns.ALARM_TIME)
  private long time;

  @DatabaseField(columnName = AlarmColumns.ENABLED)
  private boolean enabled;// 启用

  @DatabaseField(columnName = AlarmColumns.VIBRATE)
  private boolean vibrate;

  @DatabaseField(columnName = AlarmColumns.ALERT)
  private String alert;
  // public Uri alert;

  @DatabaseField(columnName = AlarmColumns.REMARK)
  private String remark;

  // @DatabaseField(columnName = AlarmColumns.xxx)
  private boolean silent;

  public Alarm() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Alarm(Parcel p) {
    id = p.readInt();
    enabled = p.readInt() == 1;
    hour = p.readInt();
    minutes = p.readInt();

    daysOfWeek = new DaysOfWeek(p.readInt()).getCoded();

    time = p.readLong();
    vibrate = p.readInt() == 1;
    remark = p.readString();

    alert = ((Uri) p.readParcelable(null)).toString();

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
    p.writeInt(enabled ? 1 : 0);
    p.writeInt(hour);
    p.writeInt(minutes);

    p.writeInt(daysOfWeek);

    p.writeLong(time);
    p.writeInt(vibrate ? 1 : 0);
    p.writeString(remark);

    p.writeString(alert);

    p.writeInt(silent ? 1 : 0);
  }

  // ////////////////////////////
  // end Parcelable apis
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

  // //////////////////////////
  // get and set //
  // //////////////////////////

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCateableType() {
    return cateableType;
  }

  public void setCateableType(String cateableType) {
    this.cateableType = cateableType;
  }

  public int getCateableId() {
    return cateableId;
  }

  public void setCateableId(int cateableId) {
    this.cateableId = cateableId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
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

  public DaysOfWeek getDaysOfWeek() {
    return new DaysOfWeek(daysOfWeek);
  }

  public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
    
    this.daysOfWeek = daysOfWeek.getCoded();
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isVibrate() {
    return vibrate;
  }

  public void setVibrate(boolean vibrate) {
    this.vibrate = vibrate;
  }

  public Uri getAlert() {
    return Uri.parse(alert);
  }

  public void setAlert(String alert) {
    this.alert = alert;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public boolean isSilent() {
    return silent;
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

}
