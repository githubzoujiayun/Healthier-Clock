package com.jkydjk.healthier.clock.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

/**
 * 一个Activity的帮助类，提供一些静态方法
 * 
 * @author miclle
 * 
 */
public class ActivityHelper {

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
