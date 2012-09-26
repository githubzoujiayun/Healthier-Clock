package com.jkydjk.healthier.clock.util;

import com.jkydjk.healthier.clock.BaseActivity;
import com.jkydjk.healthier.clock.R;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class TaskHandler extends Handler {

  private Activity activity;
  private ThreadTask task;

  public TaskHandler(Activity activity, ThreadTask task) {
    this.activity = activity;
    this.task = task;

    Runnable runnable = new Runnable() {
      public void run() {
        Message msg = new Message();
        msg.what = BaseActivity.COMPLETE;
        TaskHandler.this.sendMessage(msg);
      }
    };
    
    new Thread(runnable).start();
  }

  public TaskHandler(Looper L) {
    super(L);
  }

  @Override
  public void handleMessage(Message msg) {
    super.handleMessage(msg);
    switch (msg.what) {
    case BaseActivity.COMPLETE:
      task.run();
      break;
    }
  }
  
  public interface ThreadTask{
    void run();
  }
  
}