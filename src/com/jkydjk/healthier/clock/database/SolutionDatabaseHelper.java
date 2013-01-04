package com.jkydjk.healthier.clock.database;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SolutionDatabaseHelper extends OrmLiteSqliteOpenHelper {

  private static final String DATABASE_NAME = "solutions.db";

  private static final int DATABASE_VERSION = 1;

  private Dao<Solution, Integer> solutionDao = null;

  public SolutionDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, Solution.class);
      TableUtils.createTable(connectionSource, SolutionStep.class);
    } catch (SQLException e) {
      Log.v(DATABASE_NAME + "创建数据库失败: \n" + e);
      e.printStackTrace();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    try {
      TableUtils.dropTable(connectionSource, Solution.class, true);
      TableUtils.dropTable(connectionSource, SolutionStep.class, true);
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.v(DATABASE_NAME + "更新数据库失败: \n" + e);
      e.printStackTrace();
    }
  }

  @Override
  public void close() {
    super.close();
    solutionDao = null;
  }

  public Dao<Solution, Integer> getSolutionDataDao() throws SQLException {
    if (solutionDao == null) {
      solutionDao = getDao(Solution.class);
    }
    return solutionDao;
  }

}