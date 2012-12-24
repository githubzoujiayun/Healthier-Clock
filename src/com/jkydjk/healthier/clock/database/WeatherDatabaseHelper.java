package com.jkydjk.healthier.clock.database;

import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "weathers.db";

  private static final int DATABASE_VERSION = 1;

  public WeatherDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE weathers (_id INTEGER PRIMARY KEY, region_id INTEGER, date DATE, flag TEXT, flag_start TEXT, flag_code_start TEXT, flag_end TEXT, flag_code_end TEXT, temperature TEXT, wind TEXT, wind_power TEXT, feel TEXT, proposal TEXT, uv TEXT);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    if (Log.LOGV)
      Log.v("Upgrading weathers database from version " + oldVersion + " to " + currentVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS weathers");
    onCreate(db);
  }
}