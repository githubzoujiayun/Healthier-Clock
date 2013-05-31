package com.jkydjk.healthier.clock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by miclle on 13-5-30.
 */
public class PullService extends Service {

  SharedPreferences sharedPreferences;

  DatabaseHelper helper;
  Dao<GenericSolution, String> genericSolutionStringDao;
  GenericSolution genericSolution;

  Timer timer;

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

    // 定义Handler
    final Handler handler = new Handler() {

      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if(msg.what == 0x1234){
          sendNotification();
        }
      }
    };

    // 定义计时器
    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Message msg = new Message();
        msg.what = 0x1234;
        handler.sendMessage(msg);
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

  public void sendNotification(){

    Time time = new Time();
    time.setToNow();

    int hourID = Hour.from_time_hour(time.hour);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);

    contentView.setTextViewText(R.id.title, hourID + "");

    contentView.setTextViewText(R.id.intro, "健康投资：聪明的人，投资健康，健康增值，一百二十；明白的人，关注健康，健康保值，平安九十；无知的人，漠视健康，健康贬值，带病活到七十；糊涂的人，透支健康，健康贬值，五十六十。");

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
      .setContent(contentView)
      .setSmallIcon(R.drawable.ic_launcher_alarmclock);

    // Creates an explicit intent for an Activity in your app
    Intent resultIntent = new Intent(getApplicationContext(), Healthier.class);

    // The stack builder object will contain an artificial back stack for the started Activity.
    // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

    // Adds the back stack for the Intent (but not the Intent itself)
    stackBuilder.addParentStack(Healthier.class);

    // Adds the Intent that starts the Activity to the top of the stack
    stackBuilder.addNextIntent(resultIntent);

    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    mBuilder.setContentIntent(resultPendingIntent);

    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // mId allows you to update the notification later on.
    mNotificationManager.notify(1234, mBuilder.build());
  }

}