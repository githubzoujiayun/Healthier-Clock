package com.jkydjk.healthier.clock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
//          NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
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

        SharedPreferences sharedPreferences = getSharedPreferences("chinese_hour", Context.MODE_PRIVATE);
      }
    }, 0, 1000 * 5);
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