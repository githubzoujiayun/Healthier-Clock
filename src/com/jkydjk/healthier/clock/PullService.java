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
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jkydjk.healthier.clock.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by miclle on 13-5-30.
 */
public class PullService extends Service {

  Timer timer;

  @Override
  public void onCreate() {
    super.onCreate();
    Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
    Log.v("onCreate");

    // 定义Handler
    final Handler handler = new Handler() {

      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if(msg.what == 0x1234){

          RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);

          contentView.setTextViewText(R.id.title, "健康时钟");
          contentView.setTextViewText(R.id.intro, "健康投资：聪明的人，投资健康，健康增值，一百二十；明白的人，关注健康，健康保值，平安九十；无知的人，漠视健康，健康贬值，带病活到七十；糊涂的人，透支健康，健康贬值，五十六十。");

          NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
            .setContent(contentView)
            .setSmallIcon(R.drawable.ic_launcher_alarmclock);
//            .setContentTitle("健康时钟")
//            .setContentText("健康投资：聪明的人，投资健康，健康增值，一百二十；明白的人，关注健康，健康保值，平安九十；无知的人，漠视健康，健康贬值，带病活到七十；糊涂的人，透支健康，健康贬值，五十六十。");

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
    };

    // 定义计时器
    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
      Log.v("yao: " + Thread.currentThread().getName());

      Message msg = new Message();
      msg.what = 0x1234;
      handler.sendMessage(msg);

//      SharedPreferences sharedPreferences = getSharedPreferences("chinese_hour", Context.MODE_PRIVATE);
      }
    }, 0, 1000 * 10);
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

}