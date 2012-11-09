package com.jkydjk.healthier.clock.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 一个Activity的帮助类，提供一些静态方法
 * @author miclle
 *
 */
public class ActivityHelper {

  /**
   * 判断用户有没有登录
   * @param activity
   * @return
   */
  public static boolean isLogged(Activity activity) {
    SharedPreferences sharedPreference = activity.getSharedPreferences("configure", Context.MODE_PRIVATE);
    String token = sharedPreference.getString("token", null);
    return StringUtil.isEmpty(token) ? false : true;
  }
  
}
