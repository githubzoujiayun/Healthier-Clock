package com.jkydjk.healthier.clock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.*;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.Lunar;
import com.jkydjk.healthier.clock.util.TimerThread;
import com.jkydjk.healthier.clock.widget.TextViewWeather;
import com.jkydjk.healthier.clock.entity.HealthTip;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by miclle on 13-5-30.
 */
public class PullService extends Service {

  final static int MESSAGE_WEATHER = 1;
  final static int MESSAGE_HEALTH_TIP = 2;
  final static int MESSAGE_INFORMATION = 3;

  final static int NOTIFICATION_ID = 0x9999;

  static int NOTIFICATION_HAS_BEEN_SEND_ON_TIME = -1;

  NotificationManager notificationManager;

  SharedPreferences sharedPreferences;

  DatabaseHelper helper;

  Handler handler;

  TimerThread weatherUpdateTimer;
  TimerThread informationPullTimer;

  @Override
  public void onCreate() {
    super.onCreate();

    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    sharedPreferences = getSharedPreferences("chinese_hour", Context.MODE_PRIVATE);

    helper = new DatabaseHelper(getApplicationContext());

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
          case MESSAGE_WEATHER:
            sendWeatherNotification();
            break;
          case MESSAGE_HEALTH_TIP:
            sendHealthTipNotification(msg.getData());
            break;
          case MESSAGE_INFORMATION:
            sendInformationNotification(msg.getData());
            break;
        }
      }
    };

    // 每两小时更新天气
    weatherUpdateTimer = new TimerThread(new TimerThread.Callback() {
      @Override
      public void run() {
        Time time = new Time();
        time.setToNow();
        Log.v("Update weather: " + time.hour);
        if (ActivityHelper.networkIsConnected(getApplicationContext())) {
          SharedPreferences sharedPreferences = getSharedPreferences("configure", Context.MODE_PRIVATE);
          long regionID = sharedPreferences.getLong("city_id", Region.DEFAULT_REGION_ID);
          Weather.update(getApplicationContext(), String.valueOf(regionID));
        }
      }
    }, 1000 * 60 * 60 * 2);
    weatherUpdateTimer.start();

    informationPullTimer = new TimerThread(new TimerThread.Callback() {
      @Override
      public void run() {
        Time time = new Time();
        time.setToNow();

        Log.v("Now time hour: " + time.hour);
        Log.v("NOTIFICATION_HAS_BEEN_SEND_ON_TIME: " + NOTIFICATION_HAS_BEEN_SEND_ON_TIME);

        switch (time.hour){
          case 7:
            if(NOTIFICATION_HAS_BEEN_SEND_ON_TIME != 7){
              Message msg = new Message();
              msg.what = MESSAGE_WEATHER;
              handler.sendMessage(msg);
            }
            break;

          case 11:
          case 15:
          case 19:
            if(NOTIFICATION_HAS_BEEN_SEND_ON_TIME != time.hour){
              pullInformationMessage();
            }
            break;

          default:
            if(time.hour > 21){
              NOTIFICATION_HAS_BEEN_SEND_ON_TIME = -1;
            }
            break;
        }
      }
    }, 1000 * 60);
    informationPullTimer.start();

  }

  /**
   * 发送天气通知
   */
  private void sendWeatherNotification(){
    cancelNotification(NOTIFICATION_ID);

    SharedPreferences sharedPreferences = getSharedPreferences("configure", Context.MODE_PRIVATE);
    long regionID = sharedPreferences.getLong("city_id", Region.DEFAULT_REGION_ID);
    List<Weather> weathers = Weather.getWeathers(getApplicationContext(), String.valueOf(regionID));
    LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_weather);

    if (weathers.size() >= 1) {
      Weather today = weathers.get(0);
      LinearLayout weatherIconView = (LinearLayout)layoutInflater.inflate(R.layout.weather_icon, null, false);
      TextViewWeather weatherIcon = (TextViewWeather)weatherIconView.findViewById(R.id.weather_icon);
      weatherIcon.setText(today.getIcon(getApplicationContext()));
      weatherIconView.setDrawingCacheEnabled(true);
      weatherIconView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      weatherIconView.layout(0, 0, weatherIconView.getMeasuredWidth(), weatherIconView.getMeasuredHeight());
      weatherIconView.buildDrawingCache();
      contentView.setImageViewBitmap(R.id.weather_icon_today, weatherIconView.getDrawingCache());
      contentView.setTextViewText(R.id.weather_text_today, "今: " + today.getFlag() + "\n" + today.getTemperature());
    }

    if (weathers.size() >= 2) {
      Weather tomorrow = weathers.get(1);
      LinearLayout weatherIconView = (LinearLayout)layoutInflater.inflate(R.layout.weather_icon, null, false);
      TextViewWeather weatherIcon = (TextViewWeather)weatherIconView.findViewById(R.id.weather_icon);
      weatherIcon.setText(tomorrow.getIcon(getApplicationContext()));
      weatherIconView.setDrawingCacheEnabled(true);
      weatherIconView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      weatherIconView.layout(0, 0, weatherIconView.getMeasuredWidth(), weatherIconView.getMeasuredHeight());
      weatherIconView.buildDrawingCache();
      contentView.setImageViewBitmap(R.id.weather_icon_tomorrow, weatherIconView.getDrawingCache());
      contentView.setViewVisibility(R.id.weather_icon_tomorrow, View.VISIBLE);
      contentView.setTextViewText(R.id.weather_text_tomorrow, "明: " + tomorrow.getFlag() + "\n" + tomorrow.getTemperature());
      contentView.setViewVisibility(R.id.weather_text_tomorrow, View.VISIBLE);
      contentView.setViewVisibility(R.id.dividing_line, View.VISIBLE);
    }

    if (weathers.size() >= 1) {

      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
        .setContent(contentView)
        .setSmallIcon(R.drawable.ic_launcher_alarmclock)
        .setAutoCancel(true);

      notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

      Time time = new Time();
      time.setToNow();
      NOTIFICATION_HAS_BEEN_SEND_ON_TIME = time.hour;
    }
  }

  /**
   * 获取资讯信息
   */
  private void pullInformationMessage() {
    if (ActivityHelper.networkIsConnected(getApplicationContext())) {
      try {

        Time time = new Time();
        time.setToNow();

        HttpClientManager connect = new HttpClientManager(getApplicationContext(), RequestRoute.PULL_SERVICE);
        connect.addParam("hour", String.valueOf(Hour.from_time_hour(time.hour)));
        connect.addParam("solar_term", String.valueOf(Lunar.getCurrentSolarTermIntervalIndex()));
        connect.execute(ResuestMethod.GET);

        JSONObject json = new JSONObject(connect.getResponse());

        String type = json.getString("type");
        int id = json.getInt("id");

        if("health_tip".equals(type)) {
          Dao<HealthTip, Integer> healthTipIntegerDao = helper.getHealthTipIntegerDao();
          HealthTip tip = new HealthTip();
          tip.id = id;
          tip.content = json.getString("content");

          healthTipIntegerDao.createOrUpdate(tip);

          Message msg = new Message();
          msg.what = MESSAGE_HEALTH_TIP;
          Bundle bundle = new Bundle();
          bundle.putInt("id", tip.id);
          bundle.putString("content", tip.content);
          msg.setData(bundle);
          handler.sendMessage(msg);

        }else{

          Dao<GenericSolution, String> genericSolutionStringDao = helper.getGenericSolutionStringDao();
          GenericSolution genericSolution = genericSolutionStringDao.queryForId(type + "-" + id);

          boolean isFavorited = (genericSolution != null && genericSolution.isFavorited()) ? true : false;

          genericSolution = GenericSolution.parseJsonObject(json);
          genericSolution.setFavorited(isFavorited);

          genericSolutionStringDao.createOrUpdate(genericSolution);

          Message msg = new Message();
          msg.what = MESSAGE_INFORMATION;
          Bundle bundle = new Bundle();
          bundle.putString("id", genericSolution.getId());
          bundle.putString("type", genericSolution.getType());
          bundle.putString("title", genericSolution.getTitle());
          bundle.putString("content", genericSolution.getIntro());
          msg.setData(bundle);
          handler.sendMessage(msg);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 健康小贴士通知
   * @param data
   */
  private void sendHealthTipNotification(Bundle data){
    cancelNotification(NOTIFICATION_ID);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_information);
    contentView.setTextViewText(R.id.title, "健康小贴士");
    contentView.setTextViewText(R.id.intro, data.getString("content"));

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
      .setContent(contentView)
      .setSmallIcon(R.drawable.ic_launcher_alarmclock)
      .setAutoCancel(true);

    Intent resultIntent = new Intent(getApplicationContext(), HealthTipActivity.class);

    resultIntent.putExtra("id", data.getInt("id"));

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
    stackBuilder.addParentStack(HealthTipActivity.class);
    stackBuilder.addNextIntent(resultIntent);

    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    mBuilder.setContentIntent(resultPendingIntent);

    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    Time time = new Time();
    time.setToNow();

    NOTIFICATION_HAS_BEEN_SEND_ON_TIME = time.hour;
  }
  /**
   * 发送资讯通知
   */
  private void sendInformationNotification(Bundle data){
    cancelNotification(NOTIFICATION_ID);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_information);
    contentView.setTextViewText(R.id.title, data.getString("title"));
    contentView.setTextViewText(R.id.intro, data.getString("content"));

    String solutionType = data.getString("type");

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
      .setContent(contentView)
      .setSmallIcon(R.drawable.ic_launcher_alarmclock)
      .setAutoCancel(true);

    Class activityClass = null;

    if (GenericSolution.Type.RECIPE.equals(solutionType)){
      activityClass = RecipeActivity.class;
    }

    if (GenericSolution.Type.MASSAGE_SOLUTION.equals(solutionType) || GenericSolution.Type.MOXIBUSTION_SOLUTION.equals(solutionType)
      || GenericSolution.Type.CUPPING_SOLUTION.equals(solutionType) || GenericSolution.Type.SKIN_SCRAPING_SOLUTION.equals(solutionType)){
      activityClass = SolutionActivity.class;
    }

    Intent resultIntent = new Intent(getApplicationContext(), activityClass);

    resultIntent.putExtra("generic_solution_id", data.getString("id"));
    resultIntent.putExtra("notification_to_enter", true);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
    stackBuilder.addParentStack(activityClass);
    stackBuilder.addNextIntent(resultIntent);

    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    mBuilder.setContentIntent(resultPendingIntent);

    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    Time time = new Time();
    time.setToNow();

    NOTIFICATION_HAS_BEEN_SEND_ON_TIME = time.hour;
  }

  /**
   * 移除通知
   * @param id
   */
  private void cancelNotification(int id){
    if(notificationManager != null){
      notificationManager.cancel(id);
      NOTIFICATION_HAS_BEEN_SEND_ON_TIME = -1;
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    weatherUpdateTimer.destroy();
    informationPullTimer.destroy();
    super.onDestroy();
  }

  @Override
  public void onRebind(Intent intent) {
    super.onRebind(intent);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}