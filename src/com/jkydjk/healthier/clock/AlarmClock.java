package com.jkydjk.healthier.clock;

import java.text.DateFormatSymbols;
import java.text.ParseException;
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

import com.google.analytics.tracking.android.EasyTracker;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.Region;
import com.jkydjk.healthier.clock.entity.Weather;
import com.jkydjk.healthier.clock.entity.Weather.Callback;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.DateHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.TextViewWeather;

/**
 * AlarmClock application.
 */
public class AlarmClock extends BaseActivity implements OnClickListener {

  protected static final int MSG_CLOCK = 0x1234;

  public static final String PREFERENCES = "AlarmClock";
  static final String PREF_CLOCK_FACE = "face";
  static final String PREF_SHOW_CLOCK = "show_clock";

  /** Cap alarm count at this number */
  static final int MAX_ALARM_COUNT = 12;

  private LayoutAnimationController controller;
  private AnimationSet set;
  private Animation animation;

  private SharedPreferences sharedPreferences;
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

  private View weatherDividingLine;

  private TextViewWeather weatherLogoTomorrow;
  private TextView weatherTextTomorrow;

  // private View mClock = null;
  private ListView mAlarmsList;
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

    sharedPreferences = getSharedPreferences(PREFERENCES, 0);

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

    weatherDividingLine = findViewById(R.id.weather_dividing_line);

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

    mAlarmsList = (ListView) findViewById(R.id.alarms_list);

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
    alarmsCursor = Alarms.getAlarmsCursor(getContentResolver());
    noAlarmLayout.setVisibility(alarmsCursor.getCount() > 0 ? View.GONE : View.VISIBLE);
    mAlarmsList.setAdapter(new AlarmTimeAdapter(this, alarmsCursor));
    mAlarmsList.setVerticalScrollBarEnabled(true);
    // mAlarmsList.setOnItemClickListener(this);
    mAlarmsList.setOnCreateContextMenuListener(this);
  }

  /**
   * 设置欢迎文字
   */
  private void setWelcomeText() {
    welcomeTextView.setText(ActivityHelper.getWelcomeText(this));
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
          weatherLogoToday.setVisibility(View.VISIBLE);
          weatherTextToday.setText("今天 " + today.getFlag() + "\n" + today.getTemperature());
          weatherTextToday.setVisibility(View.VISIBLE);
        }

        if (weathers.size() >= 2) {
          weatherDividingLine.setVisibility(View.VISIBLE);
          Weather tomorrow = weathers.get(1);
          weatherLogoTomorrow.setText(tomorrow.getIcon(AlarmClock.this));
          weatherLogoTomorrow.setVisibility(View.VISIBLE);
          weatherTextTomorrow.setText("明天 " + tomorrow.getFlag() + "\n" + tomorrow.getTemperature());
          weatherTextTomorrow.setVisibility(View.VISIBLE);
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

  private class AlarmTimeAdapter extends CursorAdapter {

    public AlarmTimeAdapter(Context context, Cursor cursor) {
      super(context, cursor);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      View ret = layoutInflater.inflate(R.layout.alarm_time, parent, false);

      ((TextView) ret.findViewById(R.id.am)).setText(mAm);
      ((TextView) ret.findViewById(R.id.pm)).setText(mPm);

      DigitalClock digitalClock = (DigitalClock) ret.findViewById(R.id.digitalClock);
      digitalClock.setLive(false);
      return ret;
    }

    public void actionToggle(View v) {
      View parentLayout = (View) v.getParent();

      LinearLayout alarmActionsLayout;
      ImageView arrow;

      if (currentAlarmLayout != null && currentAlarmLayout != parentLayout) {
        alarmActionsLayout = (LinearLayout) currentAlarmLayout.findViewById(R.id.alarm_actions_layout);
        arrow = (ImageView) currentAlarmLayout.findViewById(R.id.arrow);
        alarmActionsLayout.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        alarmActionsLayout.setEnabled(true);
        currentAlarmLayout = null;
      }

      alarmActionsLayout = (LinearLayout) parentLayout.findViewById(R.id.alarm_actions_layout);
      arrow = (ImageView) parentLayout.findViewById(R.id.arrow);

      if (alarmActionsLayout.isEnabled() == true) {
        currentAlarmLayout = parentLayout;
        alarmActionsLayout.setLayoutAnimation(controller);

        alarmActionsLayout.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);
        alarmActionsLayout.setEnabled(false);
      } else {
        alarmActionsLayout.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        alarmActionsLayout.setEnabled(true);
        currentAlarmLayout = null;
      }

    }

    public void bindView(final View view, Context context, Cursor cursor) {
      final Alarm alarm = new Alarm(cursor);

      View alarmLnfoLayout = view.findViewById(R.id.alarm_info_layout);
      alarmLnfoLayout.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          actionToggle(v);
        }
      });

      final View closed = view.findViewById(R.id.closed);
      closed.setVisibility(!alarm.enabled ? View.VISIBLE : View.GONE);

      TextView alarmName = (TextView) view.findViewById(R.id.alarm_name);
      if (!StringUtil.isEmpty(alarm.label)) {
        alarmName.setText(alarm.label);
      }

      // 设置闹钟文字
      DigitalClock digitalClock = (DigitalClock) view.findViewById(R.id.digitalClock);
      final Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
      calendar.set(Calendar.MINUTE, alarm.minutes);
      digitalClock.updateTime(calendar);

      // 设置重复的文字或，如果它不重复留空
      TextView daysOfWeekView = (TextView) digitalClock.findViewById(R.id.daysOfWeek);
      final String daysOfWeekStr = alarm.daysOfWeek.toString(AlarmClock.this, false);
      if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
        daysOfWeekView.setText(daysOfWeekStr);
        daysOfWeekView.setVisibility(View.VISIBLE);
      } else {
        daysOfWeekView.setVisibility(View.GONE);
      }

      TextView dateTextView = (TextView) view.findViewById(R.id.date_text);

      Calendar alarmCalendar = Alarms.calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek);
      
      try {
        switch (DateHelper.daysAway(alarmCalendar)) {
        case 0:
          dateTextView.setText(R.string.today);
          break;
        case 1:
          dateTextView.setText(R.string.tomorrow);
          break;
        case 2:
          dateTextView.setText(R.string.the_day_after_tomorrow);
          break;

        default:
          dateTextView.setText(ActivityHelper.getStringResourceID(AlarmClock.this, "day_of_week_" + alarmCalendar.get(Calendar.DAY_OF_WEEK)));
          break;
        }
      } catch (ParseException e) {
        dateTextView.setText(ActivityHelper.getStringResourceID(AlarmClock.this, "day_of_week_" + alarmCalendar.get(Calendar.DAY_OF_WEEK)));
        e.printStackTrace();
      }

      // 编辑按钮
      Button editButton = (Button) view.findViewById(R.id.edit);
      editButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Intent intent = new Intent(AlarmClock.this, SetAlarmCustom.class);
          intent.putExtra(Alarms.ALARM_ID, alarm.id);
          startActivity(intent);
        }
      });

      // 关闭/开启 切换按钮
      Button toggle = (Button) view.findViewById(R.id.toggle);
      toggle.setText(alarm.enabled ? R.string.close : R.string.open);
      toggle.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Button button = (Button) v;
          if (alarm.enabled == true) {
            alarm.enabled = false;
            Alarms.enableAlarm(AlarmClock.this, alarm.id, false);
            button.setText(R.string.open);
          } else {
            alarm.enabled = true;
            Alarms.enableAlarm(AlarmClock.this, alarm.id, true);
            SetAlarm.popAlarmSetToast(AlarmClock.this, alarm.hour, alarm.minutes, alarm.daysOfWeek);
            button.setText(R.string.close);
          }
          closed.setVisibility(!alarm.enabled ? View.VISIBLE : View.GONE);
        }
      });

      // 删除按钮
      Button deleteButton = (Button) view.findViewById(R.id.delete);
      deleteButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          // Confirm that the alarm will be deleted.
          new AlertDialog.Builder(AlarmClock.this).setTitle(getString(R.string.delete_alarm)).setMessage(getString(R.string.delete_alarm_confirm))
              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int w) {
                  Alarms.deleteAlarm(AlarmClock.this, alarm.id);
                  updateAlarmList();
                }
              }).setNegativeButton(android.R.string.cancel, null).show();
        }
      });

      // // 跳过按钮
      // Button skipButton = (Button) view.findViewById(R.id.skip);
      // skipButton.setOnClickListener(new OnClickListener() {
      // public void onClick(View v) {
      // Intent intent = new Intent(AlarmClock.this, AlarmAlertTest.class);
      // // intent.putExtra(Alarms.ALARM_ID, alarm.id);
      // startActivity(intent);
      //
      // }
      // });

    }
  };

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
    EasyTracker.getInstance().activityStop(this); // Add this method.
  }

}