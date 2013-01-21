package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.adapter.AlarmAdapter;
import com.jkydjk.healthier.clock.database.AlarmDatabaseHelper;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.Region;
import com.jkydjk.healthier.clock.entity.Weather;
import com.jkydjk.healthier.clock.entity.Weather.Callback;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.TextViewWeather;

/**
 * AlarmClock application.
 */
public class AlarmClock extends OrmLiteBaseActivity<AlarmDatabaseHelper> implements OnClickListener {

  protected static final int MSG_CLOCK = 0x1234;

  public static final String PREFERENCES = "AlarmClock";
  public static final String PREF_CLOCK_FACE = "face";
  public static final String PREF_SHOW_CLOCK = "show_clock";

  Dao<Alarm, Integer> alarmDao;

  /** Cap alarm count at this number */
  static final int MAX_ALARM_COUNT = 12;

  private LayoutAnimationController controller;
  private AnimationSet set;
  private Animation animation;

  private SharedPreferences sharedPreferencesOnConfigure;

  private LayoutInflater layoutInflater;

  private ImageView weatherPicture;

  private View addAlarmButton;

  private TextView welcomeTextView;

  private TextView locationTextView;

  private RelativeLayout noAlarmLayout;

  private TextView weatherInfoTip;
  private View weatherInfo;

  private TextViewWeather weatherLogoToday;
  private TextView weatherTextToday;

  private TextViewWeather weatherLogoTomorrow;
  private TextView weatherTextTomorrow;

  // private View mClock = null;
  private ListView alarmsList;
  private Cursor alarmsCursor;

  private String mAm, mPm;

  private final Handler mHandler = new Handler();

  private Handler timer;
  private Thread timerThread;

  private View currentAlarmLayout;

  private long regionID;
  private String city;

  /*
   * TODO: it would be nice for this to live in an xml config file.
   */
  static final int[] CLOCKS = { R.layout.clock_basic_bw, R.layout.clock_googly, R.layout.clock_droid2, R.layout.clock_droids, R.layout.digital_clock };

  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setContentView(R.layout.alarm_clock);

    layoutInflater = LayoutInflater.from(this);

    sharedPreferencesOnConfigure = getSharedPreferences("configure", Context.MODE_PRIVATE);

    city = sharedPreferencesOnConfigure.getString("city", Region.DEFAULT_REGION);
    regionID = sharedPreferencesOnConfigure.getLong("city_id", Region.DEFAULT_REGION_ID);

    addAlarmButton = findViewById(R.id.add_alarm);
    addAlarmButton.setOnClickListener(this);

    weatherPicture = (ImageView) findViewById(R.id.weather_picture);

    welcomeTextView = (TextView) findViewById(R.id.welcome);

    locationTextView = (TextView) findViewById(R.id.location);

    noAlarmLayout = (RelativeLayout) findViewById(R.id.no_alarm);

    weatherInfoTip = (TextView) findViewById(R.id.weather_info_tip);

    weatherInfo = findViewById(R.id.weather_info);
    weatherInfo.setOnClickListener(this);

    weatherLogoToday = (TextViewWeather) findViewById(R.id.weather_logo_today);

    weatherTextToday = (TextView) findViewById(R.id.weather_text_today);

    weatherLogoTomorrow = (TextViewWeather) findViewById(R.id.weather_logo_tomorrow);

    weatherTextTomorrow = (TextView) findViewById(R.id.weather_text_tomorrow);

    String[] ampm = new DateFormatSymbols().getAmPmStrings();
    mAm = ampm[0];
    mPm = ampm[1];

    set = new AnimationSet(true);

    animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setDuration(100);
    set.addAnimation(animation);

    animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    animation.setDuration(100);
    set.addAnimation(animation);

    controller = new LayoutAnimationController(set, 0.5f);

    alarmsList = (ListView) findViewById(R.id.alarms_list);

    setWelcomeText();

    locationTextView.setText(city);

    updateWeatherInfo(Weather.todayIsUpdated(this) ? false : true);

    updateAlarmList();

    timer = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_CLOCK:
          setWelcomeText();
          break;
        }
        super.handleMessage(msg);
      }
    };
    timerThread = new LooperThread();
    timerThread.start();
  }

  class LooperThread extends Thread {
    @Override
    public void run() {
      super.run();
      try {
        do {
          Message msg = new Message();
          msg.what = AlarmClock.MSG_CLOCK;
          AlarmClock.this.timer.sendMessage(msg);
          Thread.sleep(1000 * 60);
        } while (Thread.interrupted() == false);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // 发送消息，以避免可能的ANR
    mHandler.post(new Runnable() {
      public void run() {
        updateAlarmList();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    setWelcomeText();
    updateLocation();
    updateAlarmList();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ToastMaster.cancelToast();
    alarmsCursor.deactivate();
  }

  /**
   * 闹钟列表
   */
  private void updateAlarmList() {

    try {
      alarmDao = getHelper().getAlarmDao();

      List<Alarm> alarms = alarmDao.queryForAll();

      noAlarmLayout.setVisibility(alarms.size() > 0 ? View.GONE : View.VISIBLE);

      alarmsList.setAdapter(new AlarmAdapter(this, alarms));
      
      // mAlarmsList.setVerticalScrollBarEnabled(true);
      // mAlarmsList.setOnItemClickListener(this);
      // mAlarmsList.setOnCreateContextMenuListener(this);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * 设置欢迎文字
   */
  private void setWelcomeText() {
    Time t = new Time();
    t.setToNow();
    int hour = t.hour;

    String welcome;

    if (5 <= hour && hour < 11) {
      welcome = getString(R.string.welcome_good_morning);
    } else if (11 <= hour && hour < 13) {
      welcome = getString(R.string.welcome_good_noon);
    } else if (13 <= hour && hour < 19) {
      welcome = getString(R.string.welcome_good_afternoon);
    } else {
      welcome = getString(R.string.welcome_good_evening);
    }

    String realname = sharedPreferencesOnConfigure.getString("realname", null);
    String username = sharedPreferencesOnConfigure.getString("username", null);

    if (!StringUtil.isEmpty(realname)) {
      welcome += ", " + realname;
    } else if (!StringUtil.isEmpty(username)) {
      welcome += ", " + username;
    }

    welcomeTextView.setText(welcome);
  }

  /**
   * 更新所在城市
   */
  private void updateLocation() {
    long newRegionID = sharedPreferencesOnConfigure.getLong("city_id", Region.DEFAULT_REGION_ID);
    if (regionID != newRegionID) {
      regionID = newRegionID;
      city = sharedPreferencesOnConfigure.getString("city", Region.DEFAULT_REGION);
      locationTextView.setText(city);
      updateWeatherInfo(true);
    }
  }

  /**
   * 设置天气信息
   */
  private void updateWeatherInfo(boolean force) {

    Weather.Task task = new Weather.Task(this);

    task.setForceUpdate(force);

    task.setCallback(new Callback() {
      public void onPreExecute() {
        weatherInfoTip.setVisibility(View.VISIBLE);
        weatherInfo.setVisibility(View.GONE);
      }

      public void onPostExecute(Weather.Task task, String result) {

        if (task.getForceUpdate()) {
          Toast.makeText(AlarmClock.this, (task.getUpdateSuccess() ? R.string.wealther_info_updated : R.string.unable_to_get_weather_information), Toast.LENGTH_SHORT).show();
        }

        List<Weather> weathers = task.weathers;

        if (weathers.size() == 0) {
          weatherInfoTip.setText(R.string.unable_to_get_weather_information);
          return;
        }

        if (weathers.size() >= 1) {
          Weather today = weathers.get(0);

          weatherPicture.setImageResource(ActivityHelper.getImageResourceID(AlarmClock.this, "weather_picture_" + today.getFlagCodeStart()));

          weatherLogoToday.setText(today.getIcon(AlarmClock.this));
          weatherTextToday.setText("今天 " + today.getFlag() + "\n" + today.getTemperature());
        }

        if (weathers.size() >= 2) {
          Weather tomorrow = weathers.get(1);
          weatherLogoTomorrow.setText(tomorrow.getIcon(AlarmClock.this));
          weatherTextTomorrow.setText("明天 " + tomorrow.getFlag() + "\n" + tomorrow.getTemperature());
        }

        weatherInfoTip.setVisibility(View.GONE);
        weatherInfo.setVisibility(View.VISIBLE);

      }
    });

    task.execute(String.valueOf(regionID));
  }

  /**
   * 点击处理
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.add_alarm:
      // startActivity(new Intent(this, AddAlarm.class));
      startActivity(new Intent(this, SetAlarmCustom.class));
      break;
    case R.id.weather_info:
      updateWeatherInfo(true);
      break;
    }
  }

  @Override
  protected void onPause() {
    if (currentAlarmLayout != null) {
      View alarmActionsLayout = currentAlarmLayout.findViewById(R.id.alarm_actions_layout);
      View arrow = currentAlarmLayout.findViewById(R.id.arrow);
      alarmActionsLayout.setVisibility(View.GONE);
      arrow.setVisibility(View.GONE);
      alarmActionsLayout.setEnabled(true);
      currentAlarmLayout = null;
    }
    super.onPause();
  }

  // @Override
  // protected void onStop() {
  // Log.v("OnStop");
  // super.onStop();
  // }

}