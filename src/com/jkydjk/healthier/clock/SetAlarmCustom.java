package com.jkydjk.healthier.clock;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.Alarm.DaysOfWeek;
import com.jkydjk.healthier.clock.util.AlarmUtil;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.ToggleSwitch;
import com.jkydjk.healthier.clock.widget.ToggleSwitch.OnChangeAttemptListener;

public class SetAlarmCustom extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnChangeAttemptListener, TimePickerDialog.OnTimeSetListener {

  private static final int ALARM_CYCLE = 2;
  private static final int ALARM_ALERT = 3;

  private int alarmId;
  private boolean alarmEnabled = true;
  private int alarmHour;
  private int alarmMinutes;
  private DaysOfWeek alarmDaysOfWeek;
  private Uri alarmAlert;
  private String alarmLabel;
  private String alarmRemark;
  private boolean alarmVibrate = true;

  private View cancelAction;
  private View saveAction;

  private View alarmTimeSetting;
  private View alarmCycleSetting;
  private View alarmAlertSetting;

  private TextView alarmTimeTextView;
  private TextView alarmAlertTextView;
  private TextView alarmCycleTextView;
  private EditText alarmLabelEditText;
  private EditText alarmRemarkEditText;
  private ToggleSwitch alarmVibrateToggleSwitch;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.set_alarm_custom);

    Intent intent = getIntent();

    alarmId = intent.getIntExtra(Alarms.ALARM_ID, -1);

    if (alarmId != -1) {
      /* load alarm details from database */
      Alarm alarm = Alarms.getAlarm(getContentResolver(), alarmId);
      if (alarm != null) {
        alarmEnabled = alarm.enabled;
        alarmHour = alarm.hour;
        alarmMinutes = alarm.minutes;
        alarmDaysOfWeek = alarm.daysOfWeek;
        alarmLabel = alarm.label;
        alarmAlert = alarm.alert;
        alarmRemark = alarm.remark;
        alarmVibrate = alarm.vibrate;
      }
    } else {
      Time t = new Time();
      t.setToNow();

      alarmHour = t.hour;
      alarmMinutes = t.minute;
      alarmDaysOfWeek = new DaysOfWeek(0);
      alarmAlert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    }

    cancelAction = findViewById(R.id.cancel);
    cancelAction.setOnClickListener(this);

    saveAction = findViewById(R.id.save);
    saveAction.setOnClickListener(this);

    alarmTimeSetting = findViewById(R.id.alarm_time_setting);
    alarmTimeSetting.setOnClickListener(this);

    alarmCycleSetting = findViewById(R.id.alarm_cycle_setting);
    alarmCycleSetting.setOnClickListener(this);

    alarmAlertSetting = findViewById(R.id.alarm_alert_setting);
    alarmAlertSetting.setOnClickListener(this);

    alarmTimeTextView = (TextView) findViewById(R.id.alarm_time);
    alarmAlertTextView = (TextView) findViewById(R.id.alarm_alert);
    alarmCycleTextView = (TextView) findViewById(R.id.alarm_cycle);
    alarmLabelEditText = (EditText) findViewById(R.id.alarm_label);

    alarmRemarkEditText = (EditText) findViewById(R.id.alarm_remark);
    alarmRemarkEditText.setHintTextColor(0xFF666666);

    alarmVibrateToggleSwitch = (ToggleSwitch) findViewById(R.id.alarm_vibrate);
    alarmVibrateToggleSwitch.setChecked(alarmVibrate);
    alarmVibrateToggleSwitch.setOnCheckedChangeListener(this);

    updateTime();
    updateAlert();
    updateCycle();
    updateLabel();
    updateRemark();

    alarmLabelEditText.clearFocus();
    alarmRemarkEditText.clearFocus();
  }

  private void updateTime() {
    alarmTimeTextView.setText(Alarms.formatTime(this, alarmHour, alarmMinutes, alarmDaysOfWeek));
  }

  private void updateAlert() {
    if (alarmAlert != null) {
      final Ringtone r = RingtoneManager.getRingtone(this, alarmAlert);
      if (r != null) {
        alarmAlertTextView.setText(r.getTitle(this));
      }
    } else {
      alarmAlertTextView.setText(R.string.silent_alarm_summary);
    }
  }

  private void updateCycle() {
    alarmCycleTextView.setText(alarmDaysOfWeek.toString(this, true));
  }

  private void updateLabel() {
    if (!StringUtil.isEmpty(alarmLabel))
      alarmLabelEditText.setText(alarmLabel);
  }

  private void updateRemark() {
    if (!StringUtil.isEmpty(alarmRemark))
      alarmRemarkEditText.setText(alarmRemark);
  }

  private void saveAlarm() {
    alarmLabel = alarmLabelEditText.getText().toString();
    alarmRemark = alarmRemarkEditText.getText().toString();
    long time;
    if (alarmId == -1) {
      time = Alarms.addAlarm(this, alarmLabel, alarmHour, alarmMinutes, alarmDaysOfWeek, alarmVibrate, alarmAlert.toString(), alarmRemark);
    } else {
      time = Alarms.setAlarm(this, alarmId, alarmLabel, alarmEnabled, alarmHour, alarmMinutes, alarmDaysOfWeek, alarmVibrate, alarmAlert.toString(), alarmRemark);
    }
    if (alarmEnabled) {
      AlarmUtil.popAlarmSetToast(this, time);
    }
  }

  /**
   * 点击处理
   */
  public void onClick(View v) {
    Intent intent;
    switch (v.getId()) {
    case R.id.cancel:
      finish();
      break;

    case R.id.save:
      saveAlarm();
      finish();
      break;

    case R.id.alarm_time_setting:
      new TimePickerDialog(this, this, alarmHour, alarmMinutes, DateFormat.is24HourFormat(this)).show();
      break;

    case R.id.alarm_cycle_setting:
      intent = new Intent(this, SetAlarmCycle.class);
      intent.putExtra("cycle", alarmDaysOfWeek.getBooleanArray());
      startActivityForResult(intent, ALARM_CYCLE);
      overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
      break;

    case R.id.alarm_alert_setting:
      intent = new Intent(this, SetAlarmAlert.class);
      intent.putExtra("alert", alarmAlert);
      startActivityForResult(intent, ALARM_ALERT);
      overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
      break;
    }
  }

  /**
   * Activity返回结果处理
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case ALARM_CYCLE:
      boolean[] array = data.getBooleanArrayExtra("cycle");
      for (int i = 0; i < array.length; i++) {
        alarmDaysOfWeek.set(i, array[i]);
      }
      updateCycle();
      break;

    case ALARM_ALERT:
      alarmAlert = (Uri) data.getParcelableExtra("alert");
      updateAlert();
      break;

    default:
      break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    switch (buttonView.getId()) {
    case R.id.alarm_vibrate:
      alarmVibrate = isChecked;
      break;
    }
  }

  public void onChangeAttempted(boolean isChecked) {
    // TODO Auto-generated method stub

  }

  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    alarmHour = hourOfDay;
    alarmMinutes = minute;
    updateTime();
    // If the time has been changed, enable the alarm.
    alarmEnabled = true;
  }

}
