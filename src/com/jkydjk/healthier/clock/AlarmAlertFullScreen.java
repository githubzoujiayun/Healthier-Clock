package com.jkydjk.healthier.clock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.Region;
import com.jkydjk.healthier.clock.entity.Weather;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm tone. This
 * activity is the full screen version which shows over the lock screen with the
 * wallpaper as the background.
 */
@SuppressLint("SimpleDateFormat")
public class AlarmAlertFullScreen extends BaseActivity implements OnPageChangeListener {

  // These defaults must match the values in res/xml/settings.xml

  private static final String DEFAULT_SNOOZE = "5"; // 间隔时间为1分钟，方便测试
  private static final String DEFAULT_VOLUME_BEHAVIOR = "2";
  protected static final String SCREEN_OFF = "screen_off";

  protected Alarm mAlarm;
  private int mVolumeBehavior;

  ArrayList<View> pages = new ArrayList<View>();

  TextView weatherLogoTextView;
  TextView locationTextView;
  TextView weatherInfoTextView;
  TextView alarmIntroTextView;
  TextView timeTextView;

  private Button snooze;
  private Button dismiss;

  // Receives the ALARM_KILLED action from the AlarmKlaxon.
  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Alarm alarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
      if (alarm != null && mAlarm.id == alarm.id) {
        dismiss(true);
      }
    }
  };

  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    mAlarm = getIntent().getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);

    // Get the volume/camera button behavior setting
    final String vol = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_VOLUME_BEHAVIOR, DEFAULT_VOLUME_BEHAVIOR);
    mVolumeBehavior = Integer.parseInt(vol);

    requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);

    final Window win = getWindow();
    win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    // Turn on the screen unless we are being launched from the AlarmAlert
    // subclass.
    if (!getIntent().getBooleanExtra(SCREEN_OFF, false)) {
      win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    updateLayout();

    // Register to get the alarm killed intent.
    registerReceiver(mReceiver, new IntentFilter(Alarms.ALARM_KILLED));
  }

  /**
   * 更新界面
   */
  private void updateLayout() {
    LayoutInflater inflater = LayoutInflater.from(this);

    setContentView(inflater.inflate(R.layout.alarm_alert, null));

    ViewPager slider = (ViewPager) findViewById(R.id.slider);

    // 响铃界面
    View alarmAlertPage = inflater.inflate(R.layout.alarm_alert_page, null);

    pages.add(inflater.inflate(R.layout.transparent, null));
    pages.add(alarmAlertPage);
    pages.add(inflater.inflate(R.layout.transparent, null));

    slider.setAdapter(new SlidePageAdapter());
    slider.setCurrentItem(1);

    slider.setOnPageChangeListener(this);

    snooze = (Button) alarmAlertPage.findViewById(R.id.snooze);
    snooze.setBackgroundResource(R.drawable.animate_arrow_left);
    ((AnimationDrawable) snooze.getBackground()).start();

    dismiss = (Button) alarmAlertPage.findViewById(R.id.dismiss);
    dismiss.setBackgroundResource(R.drawable.animate_arrow_right);
    ((AnimationDrawable) dismiss.getBackground()).start();

    long regionID = getSharedPreferences("configure", Context.MODE_PRIVATE).getLong("city_id", Region.DEFAULT_REGION_ID);

    Weather weather = Weather.getToday(this, regionID + "");

    weatherInfoTextView = (TextView) alarmAlertPage.findViewById(R.id.text_view_weather_info);

    weatherInfoTextView.setText("今天 " + weather.getFlag() + "\n" + weather.getTemperature());

    weatherLogoTextView = (TextView) alarmAlertPage.findViewById(R.id.text_view_weather_logo);

    weatherLogoTextView.setText(weather.getIcon(this));

    locationTextView = (TextView) alarmAlertPage.findViewById(R.id.text_view_location);
    locationTextView.setText(ActivityHelper.getCity(this));

    alarmIntroTextView = (TextView) alarmAlertPage.findViewById(R.id.text_view_alarm_intro);

    if (StringUtil.isEmpty(mAlarm.getLabel())) {
      alarmIntroTextView.setText(ActivityHelper.getWelcomeText(this));
    } else {
      alarmIntroTextView.setText(mAlarm.getLabel());
    }

    timeTextView = (TextView) alarmAlertPage.findViewById(R.id.text_view_time);
    timeTextView.setText(new SimpleDateFormat("HH:mm").format(new java.util.Date()));

  }

  // 指引页面更改事件监听器
  public void onPageScrollStateChanged(int state) {
  }

  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  public void onPageSelected(int position) {
    if (position == 0) {
      dismiss(false);
    }
    if (position == pages.size() - 1) {
      snooze();
    }
  }

  // Attempt to snooze this alert.
  private void snooze() {
    final String snooze = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_ALARM_SNOOZE, DEFAULT_SNOOZE);

    int snoozeMinutes = Integer.parseInt(snooze);

    final long snoozeTime = System.currentTimeMillis() + (1000 * 60 * snoozeMinutes);

    Alarms.saveSnoozeAlert(AlarmAlertFullScreen.this, mAlarm.id, snoozeTime);

    // Get the display time for the snooze and update the notification.
    final Calendar c = Calendar.getInstance();
    c.setTimeInMillis(snoozeTime);

    // Append (snoozed) to the label.
    String label = mAlarm.getLabelOrDefault(this);
    label = getString(R.string.alarm_notify_snooze_label, label);

    // Notify the user that the alarm has been snoozed.
    Intent cancelSnooze = new Intent(this, AlarmReceiver.class);
    cancelSnooze.setAction(Alarms.CANCEL_SNOOZE);
    cancelSnooze.putExtra(Alarms.ALARM_ID, mAlarm.id);

    PendingIntent broadcast = PendingIntent.getBroadcast(this, mAlarm.id, cancelSnooze, 0);

    NotificationManager nm = getNotificationManager();
    Notification n = new Notification(R.drawable.stat_notify_alarm, label, 0);
    n.setLatestEventInfo(this, label, getString(R.string.alarm_notify_snooze_text, Alarms.formatTime(this, c)), broadcast);
    n.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
    nm.notify(mAlarm.id, n);

    String displayTime = getString(R.string.alarm_alert_snooze_set, snoozeMinutes);
    // Intentionally log the snooze time for debugging.
    Log.v(displayTime);

    // Display the snooze minutes in a toast.
    Toast.makeText(AlarmAlertFullScreen.this, displayTime, Toast.LENGTH_LONG).show();

    stopService(new Intent(Alarms.ALARM_ALERT_ACTION));

    finish();
  }

  private NotificationManager getNotificationManager() {
    return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
  }

  // Dismiss the alarm.
  private void dismiss(boolean killed) {
    // The service told us that the alarm has been killed, do not modify
    // the notification or stop the service.
    if (!killed) {
      // Cancel the notification and stop playing the alarm
      NotificationManager nm = getNotificationManager();
      nm.cancel(mAlarm.id);
      stopService(new Intent(Alarms.ALARM_ALERT_ACTION));
    }
    finish();
  }

  /**
   * this is called when a second alarm is triggered while a previous alert
   * window is still active.
   */
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (Log.LOGV)
      Log.v("AlarmAlert.OnNewIntent()");
    mAlarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (Log.LOGV)
      Log.v("AlarmAlert.onDestroy()");
    // No longer care about the alarm being killed.
    unregisterReceiver(mReceiver);
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    // Do this on key down to handle a few of the system keys.
    boolean up = event.getAction() == KeyEvent.ACTION_UP;
    switch (event.getKeyCode()) {
    // Volume keys and camera keys dismiss the alarm
    case KeyEvent.KEYCODE_VOLUME_UP:
    case KeyEvent.KEYCODE_VOLUME_DOWN:
    case KeyEvent.KEYCODE_CAMERA:
    case KeyEvent.KEYCODE_FOCUS:
      if (up) {
        switch (mVolumeBehavior) {
        case 1:
          snooze();
          break;

        case 2:
          dismiss(false);
          break;

        default:
          break;
        }
      }
      return true;
    default:
      break;
    }
    return super.dispatchKeyEvent(event);
  }

  @Override
  public void onBackPressed() {
    // Don't allow back to dismiss. This method is overriden by AlarmAlert
    // so that the dialog is dismissed.
    return;
  }

  // 指引页面数据适配器
  private class SlidePageAdapter extends PagerAdapter {

    public SlidePageAdapter() {
      super();
    }

    @Override
    public int getCount() {
      return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
      return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
      ((ViewPager) container).removeView(pages.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
      ((ViewPager) container).addView(pages.get(position));
      return pages.get(position);
    }

  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    EasyTracker.getInstance().activityStart(this);
  }

  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();

    if (!isFinishing()) {
      // Don't hang around.
      finish();
    }

    EasyTracker.getInstance().activityStop(this); // Add this method.
  }

}