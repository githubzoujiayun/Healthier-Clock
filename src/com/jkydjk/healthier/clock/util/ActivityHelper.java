package com.jkydjk.healthier.clock.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.Region;

/**
 * 一个Activity的帮助类，提供一些静态方法
 * 
 * @author miclle
 * 
 */
public class ActivityHelper {

  private static ArrayAdapter<Object> emptyArrayAdapter;

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
  public static boolean networkIsConnected(Context context) {
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

  /**
   * 给ViewGroup插入一个内容片段
   * 
   * @param group
   * @param title
   * @param content
   */
  public static void generateContentItem(ViewGroup group, String title, String content) {

    if (group == null || StringUtil.isEmpty(title) || StringUtil.isEmpty(content))
      return;

    LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View contentItemView = inflater.inflate(R.layout.content_item, null, false);

    TextView titleTextView = (TextView) contentItemView.findViewById(R.id.title);
    titleTextView.setText(title);

    TextView contentTextView = (TextView) contentItemView.findViewById(R.id.content);
    contentTextView.setText(content);

    group.addView(contentItemView);
  }

  /**
   * 返回一个空的ArrayAdapter实例
   * 
   * @param context
   * @return
   */
  public static ArrayAdapter<Object> getEmptyArrayAdapter(Context context) {
    if (emptyArrayAdapter == null)
      emptyArrayAdapter = new ArrayAdapter<Object>(context, R.layout.empty_simple_expandable_list_item, android.R.layout.simple_expandable_list_item_1);
    return emptyArrayAdapter;
  }

  /**
   * 根据给定Boolean值选中相应的RadioButton
   * 
   * @param value
   * @param trueRadio
   * @param falseRadio
   */
  public static void switchRadio(Boolean value, RadioButton trueRadio, RadioButton falseRadio) {
    if (value == null)
      return;
    if (value)
      trueRadio.setChecked(true);
    else
      falseRadio.setChecked(true);
  }

  /**
   * Geg welcome text
   * 
   * @param textView
   */
  public static String getWelcomeText(Context context) {
    SharedPreferences sharedPreferencesOnConfigure = context.getSharedPreferences("configure", Context.MODE_PRIVATE);

    Time t = new Time();
    t.setToNow();
    int hour = t.hour;

    String welcome;

    if (5 <= hour && hour < 11) {
      welcome = context.getString(R.string.welcome_good_morning);
    } else if (11 <= hour && hour < 13) {
      welcome = context.getString(R.string.welcome_good_noon);
    } else if (13 <= hour && hour < 19) {
      welcome = context.getString(R.string.welcome_good_afternoon);
    } else {
      welcome = context.getString(R.string.welcome_good_evening);
    }

    String realname = sharedPreferencesOnConfigure.getString("realname", null);
    String username = sharedPreferencesOnConfigure.getString("username", null);

    if (!StringUtil.isEmpty(realname)) {
      welcome += ", " + realname;
    } else if (!StringUtil.isEmpty(username)) {
      welcome += ", " + username;
    }
    return welcome;
  }

  /**
   * Get city
   * @param context
   * @return
   */
  public static String getCity(Context context) {
    return context.getSharedPreferences("configure", Context.MODE_PRIVATE).getString("city", Region.DEFAULT_REGION);
  }

}
