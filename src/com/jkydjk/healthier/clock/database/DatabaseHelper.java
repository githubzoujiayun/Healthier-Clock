package com.jkydjk.healthier.clock.database;

import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "alarms.db";

  private static final int DATABASE_VERSION = 5;

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE alarms (" + "_id INTEGER PRIMARY KEY," + "label TEXT, " + "hour INTEGER, " + "minutes INTEGER, " + "daysofweek INTEGER, " + "alarmtime INTEGER, " + "enabled INTEGER, "
        + "vibrate INTEGER, " + "alert TEXT, " + "remark TEXT);");

    // insert default alarms
    String insertMe = "INSERT INTO alarms " + "(label, hour, minutes, daysofweek, alarmtime, enabled, vibrate, alert, remark) " + "VALUES ";
    db.execSQL(insertMe + "('闹铃', 7, 0, 127, 0, 0, 1, '', '');");
    db.execSQL(insertMe + "('闹铃', 8, 30, 31, 0, 0, 1, '', '');");
    db.execSQL(insertMe + "('闹铃', 9, 00, 0, 0, 0, 1, '', '');");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    if (Log.LOGV)
      Log.v("Upgrading alarms database from version " + oldVersion + " to " + currentVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS alarms");
    onCreate(db);
  }
}