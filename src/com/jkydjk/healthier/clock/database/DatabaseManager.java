package com.jkydjk.healthier.clock.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jkydjk.healthier.clock.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DatabaseManager {

  private static final int BUFFER_SIZE = 400000;

  // 保存的数据库文件名
  public static final String DATABASE_NAME = "healthier.db";

  public static final String PACKAGE_NAME = "com.jkydjk.healthier.clock";

  // 在手机里存放数据库的位置
  public static final String DATABASE_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME;

  /**
   * 打开数据库
   * 
   * @return 返回已经打开后数据库
   */
  public static SQLiteDatabase openDatabase(Context context) {
    return openDatabase(context, DATABASE_PATH + "/" + DATABASE_NAME);
  }

  /**
   * 按照数据库路径打开数据库
   * 
   * @param dbfile
   *          数据库路径
   * @return 返回已经打开后数据库
   */
  private static SQLiteDatabase openDatabase(Context context, String dbfile) {
    try {
      if (!(new File(dbfile).exists())) {// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
        InputStream is = context.getResources().openRawResource(R.raw.initdatabase); // 欲导入的数据库
        FileOutputStream fos = new FileOutputStream(dbfile);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
          fos.write(buffer, 0, count);
        }
        fos.close();
        is.close();
      }
      SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
      return db;
    } catch (FileNotFoundException e) {
      Log.e("Database", "File not found");
      e.printStackTrace();
    } catch (IOException e) {
      Log.e("Database", "IO exception");
      e.printStackTrace();
    }
    return null;
  }

}
