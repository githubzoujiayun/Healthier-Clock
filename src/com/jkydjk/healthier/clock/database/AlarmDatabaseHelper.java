package com.jkydjk.healthier.clock.database;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jkydjk.healthier.clock.entity.AcupointSolutionStep;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.SolarTermSolution;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionComment;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AlarmDatabaseHelper extends OrmLiteSqliteOpenHelper {

  private static final String DATABASE_NAME = "alarms.db";

  private static final int DATABASE_VERSION = 5;

  private Dao<Alarm, Integer> alarmDao = null;

  public AlarmDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, Alarm.class);
    } catch (SQLException e) {
      Log.v(DATABASE_NAME + "创建数据库失败: \n" + e);
      e.printStackTrace();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    try {
      Log.v("Upgrading alarms database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

      TableUtils.dropTable(connectionSource, Alarm.class, true);
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.v(DATABASE_NAME + "更新数据库失败: \n" + e);
      e.printStackTrace();
    }
  }

  @Override
  public void close() {
    super.close();
    alarmDao = null;
  }

  public Dao<Alarm, Integer> getAlarmDao() throws SQLException {
    if (alarmDao == null)
      alarmDao = getDao(Alarm.class);
    return alarmDao;
  }

}