package com.jkydjk.healthier.clock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.entity.Region;
import com.jkydjk.healthier.clock.entity.Weather;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.widget.TextViewWeather;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by miclle on 13-5-30.
 */
public class PullService extends Service {

  final static int MESSAGE_WEATHER = 1;
  final static int MESSAGE_INFORMATION = 2;

  final static int NOTIFICATION_ID = 0x9999;

  SharedPreferences sharedPreferences;

  DatabaseHelper helper;
  Dao<GenericSolution, String> genericSolutionStringDao;
  GenericSolution genericSolution;

  Timer timer;
  Handler handler;

  @Override
  public void onCreate() {
    super.onCreate();

    sharedPreferences = getSharedPreferences("chinese_hour", Context.MODE_PRIVATE);

    helper = new DatabaseHelper(getApplicationContext());

    try {
      genericSolutionStringDao = helper.getGenericSolutionStringDao();
//      genericSolution = genericSolutionStringDao.queryForId(genericSolutionId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what){
          case MESSAGE_WEATHER:
            sendWeatherNotification();
            break;

          case MESSAGE_INFORMATION:
//            sendInformationNotification();
            break;
        }
      }
    };

    // 定义计时器
    timer = new Timer();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (ActivityHelper.networkIsConnected(getApplicationContext())) {
          SharedPreferences sharedPreferences = getSharedPreferences("configure", Context.MODE_PRIVATE);
          long regionID = sharedPreferences.getLong("city_id", Region.DEFAULT_REGION_ID);
          Weather.update(getApplicationContext(), String.valueOf(regionID));
        }
      }
    }, 0, 1000 * 60 * 60 * 4);

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Time time = new Time();
        time.setToNow();
        if (time.hour == 7){
          Message msg = new Message();
          msg.what = MESSAGE_WEATHER;
          handler.sendMessage(msg);
        }
      }
    }, 0, 1000 * 60);

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
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

  /**
   * 发送天气通知
   */
  private void sendWeatherNotification(){
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
      sendNotification(contentView, Healthier.class);
    }
  }

  /**
   * 发送资讯通知
   */
  private void sendInformationNotification(){

    if (!ActivityHelper.networkIsConnected(getApplicationContext())) {
      return;
    }

    try {
      HttpClientManager connect = new HttpClientManager(getApplicationContext(), RequestRoute.REQUEST_PATH + RequestRoute.SOLUTION_HOUR);
//      connect.addParam("hour", hourID + "");
    }catch (Exception e){
      e.printStackTrace();
      return;
    }

    Log.v("==========================sendNotification()==========================");

    Time time = new Time();
    time.setToNow();

    int hourID = Hour.from_time_hour(time.hour);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_information);

    contentView.setTextViewText(R.id.title, time.hour + ":" + time.minute + ":" + time.second);

    contentView.setTextViewText(R.id.intro, "健康投资：聪明的人，投资健康，健康增值，一百二十；明白的人，关注健康，健康保值，平安九十；无知的人，漠视健康，健康贬值，带病活到七十；糊涂的人，透支健康，健康贬值，五十六十。");

    sendNotification(contentView, Healthier.class);
  }

  /**
   * 发送通知
   * @param contentView
   * @param activity
   */
  private void sendNotification(RemoteViews contentView, Class activity){
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
      .setContent(contentView)
      .setSmallIcon(R.drawable.ic_launcher_alarmclock);

    // Creates an explicit intent for an Activity in your app
    Intent resultIntent = new Intent(getApplicationContext(), activity);

    // The stack builder object will contain an artificial back stack for the started Activity.
    // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

    // Adds the back stack for the Intent (but not the Intent itself)
    stackBuilder.addParentStack(activity);

    // Adds the Intent that starts the Activity to the top of the stack
    stackBuilder.addNextIntent(resultIntent);

    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    mBuilder.setContentIntent(resultPendingIntent);

    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // mId allows you to update the notification later on.
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
  }

}