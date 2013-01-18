package com.jkydjk.healthier.clock.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.Acupoint;
import com.jkydjk.healthier.clock.entity.AcupointSolutionStep;
import com.jkydjk.healthier.clock.entity.SolarTermSolution;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionComment;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

  public static final String PACKAGE_NAME = "com.jkydjk.healthier.clock";

  // 在手机里存放数据库的位置
  public static final String DATABASE_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/databases";

  // 保存的数据库文件名
  public static final String DATABASE_NAME = "healthier.db";

  private static final int BUFFER_SIZE = 400000;

  private static final int DATABASE_VERSION = 1;

  // Dao
  private Dao<Solution, Integer> solutionDao = null;
  private Dao<SolarTermSolution, Integer> solarTermSolutionDao = null;
  private Dao<SolutionStep, Integer> solutionStepDao = null;
  private Dao<SolutionComment, Integer> solutionCommentDao = null;
  private Dao<Acupoint, Integer> acupointDao = null;
  private Dao<AcupointSolutionStep, Integer> acupointSolutionStepDao = null;

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);

    if (!new File(DATABASE_PATH + "/" + DATABASE_NAME).exists()) {
      try {
        Log.v("导入初始化数据");
        InputStream is = context.getResources().openRawResource(R.raw.initdatabase); // 欲导入的数据库
        FileOutputStream fos = new FileOutputStream(DATABASE_PATH + "/" + DATABASE_NAME);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
          fos.write(buffer, 0, count);
        }
        fos.close();
        is.close();

      } catch (Exception e) {
        new File(DATABASE_PATH + "/" + DATABASE_NAME).delete();
        Log.v(DATABASE_NAME + "创建数据库失败: \n" + e);
        e.printStackTrace();
      }
    }

  }

  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, Solution.class);
      TableUtils.createTable(connectionSource, SolarTermSolution.class);
      TableUtils.createTable(connectionSource, SolutionStep.class);
      TableUtils.createTable(connectionSource, SolutionComment.class);
      TableUtils.createTable(connectionSource, AcupointSolutionStep.class);
    } catch (SQLException e) {
      Log.v(DATABASE_NAME + "创建数据库失败: \n" + e);
      e.printStackTrace();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    try {
      TableUtils.dropTable(connectionSource, Solution.class, true);
      TableUtils.dropTable(connectionSource, SolarTermSolution.class, true);
      TableUtils.dropTable(connectionSource, SolutionStep.class, true);
      TableUtils.dropTable(connectionSource, SolutionComment.class, true);
      TableUtils.dropTable(connectionSource, AcupointSolutionStep.class, true);
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
    solarTermSolutionDao = null;
    solutionStepDao = null;
    solutionCommentDao = null;
    acupointDao = null;
    acupointSolutionStepDao = null;
  }

  /**
   * 方案
   * 
   * @return
   * @throws SQLException
   */
  public Dao<Solution, Integer> getSolutionDao() throws SQLException {
    if (solutionDao == null) {
      solutionDao = getDao(Solution.class);
    }
    return solutionDao;
  }

  /**
   * 节气方案
   * 
   * @return
   * @throws SQLException
   */
  public Dao<SolarTermSolution, Integer> getSolarTermSolutionDao() throws SQLException {
    if (solarTermSolutionDao == null) {
      solarTermSolutionDao = getDao(SolarTermSolution.class);
    }
    return solarTermSolutionDao;
  }

  /**
   * 方案操作步骤
   * 
   * @return
   * @throws SQLException
   */
  public Dao<SolutionStep, Integer> getSolutionStepDao() throws SQLException {
    if (solutionStepDao == null) {
      solutionStepDao = getDao(SolutionStep.class);
    }
    return solutionStepDao;
  }

  /**
   * 方案评论
   * 
   * @return
   * @throws SQLException
   */
  public Dao<SolutionComment, Integer> getSolutionCommentDao() throws SQLException {
    if (solutionCommentDao == null) {
      solutionCommentDao = getDao(SolutionComment.class);
    }
    return solutionCommentDao;
  }

  /**
   * 穴位
   * 
   * @return
   * @throws SQLException
   */
  public Dao<Acupoint, Integer> getAcupointDao() throws SQLException {
    if (acupointDao == null) {
      acupointDao = getDao(Acupoint.class);
    }
    return acupointDao;
  }

  public Dao<AcupointSolutionStep, Integer> getAcupointSolutionStepDao() throws SQLException {
    if (acupointSolutionStepDao == null) {
      acupointSolutionStepDao = getDao(AcupointSolutionStep.class);
    }
    return acupointSolutionStepDao;
  }

}