package com.jkydjk.healthier.clock.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;

/**
 * 一个Activity的帮助类，提供一些静态方法
 * 
 * @author miclle
 * 
 */
public class ActivityHelper {
  
  /**
   * SD卡目录
   */
  public static final File SDCARD = Environment.getExternalStorageDirectory().getAbsoluteFile();
  
  /**
   * Android动态获取图片资源
   * 
   * @param context
   * @param name
   * @return Resource ID: R.drawable.xxxxx
   */
  public static int getImageResourceID(Context context, String name) {
    return context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + name, null, null);
  }

  /**
   * Android动态获取文本资源
   * 
   * @param context
   * @param name
   * @return Resource ID: R.string.xxxxx
   */
  public static int getStringResourceID(Context context, String name) {
    return context.getResources().getIdentifier(context.getPackageName() + ":string/" + name, null, null);
  }

  /**
   * 判断用户有没有登录
   * 
   * @param activity
   * @return
   */
  public static boolean isLogged(Activity activity) {
    SharedPreferences sharedPreference = activity.getSharedPreferences("configure", Context.MODE_PRIVATE);
    String token = sharedPreference.getString("token", null);
    return StringUtil.isEmpty(token) ? false : true;
  }

  /**
   * 判断网络连接状态
   * 
   * @param context
   * @return
   */
  public static boolean networkConnected(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.CONNECTED) {
      // Toast.makeText(ChineseHour.this, "WIFI已经连接",
      // Toast.LENGTH_SHORT).show();
      return true;
    }

    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == android.net.NetworkInfo.State.CONNECTED) {
      // Toast.makeText(ChineseHour.this, "GPRS已经连接",
      // Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }

}
