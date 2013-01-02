package com.jkydjk.healthier.clock.database;

import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SolutionDatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "solutions.db";

  private static final int DATABASE_VERSION = 1;

  public SolutionDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE solutions (_id INTEGER PRIMARY KEY, ableon_type TEXT, ableon_id INTEGER, solution_id INTEGER, type TEXT, category INTEGER, title TEXT, consuming INTEGER, started_at INTEGER, ended_at INTEGER, frequency INTEGER, times INTEGER, cycle INTEGER, description TEXT, effect TEXT, principle TEXT, note TEXT, favorited INTEGER, alarm INTEGER, version INTEGER);");

    db.execSQL("CREATE TABLE steps (_id INTEGER PRIMARY KEY, step_id INTEGER, solution_id INTEGER, no INTEGER, content TEXT);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
    Log.v("Upgrading solutions database from version " + oldVersion + " to " + currentVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS solutions");
    db.execSQL("DROP TABLE IF EXISTS steps");
    onCreate(db);
  }
}