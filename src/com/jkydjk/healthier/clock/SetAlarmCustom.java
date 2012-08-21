package com.jkydjk.healthier.clock;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jkydjk.healthier.clock.widget.ToggleSwitch;
import com.jkydjk.healthier.clock.widget.ToggleSwitch.OnChangeAttemptListener;

public class SetAlarmCustom extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnChangeAttemptListener, TimePickerDialog.OnTimeSetListener {

	private static final int ALARM_CYCLE = 2;
	private static final int ALARM_ALERT = 3;

	private Alarm alarm;
	private int alarmId;

	private boolean mEnabled;
	private int mHour;
	private int mMinutes;

	private View cancelAction;
	private View saveAction;

	private View alarmTimeSetting;
	private View alarmCycleSetting;
	private View alarmAlertSetting;

	private TextView alarmTime;
	private TextView alarmAlert;
	private TextView alarmCycle;
	private TextView alarmLabel;
	private ToggleSwitch alarmVibrateSwitch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm_custom);

		Intent i = getIntent();
		alarmId = i.getIntExtra(Alarms.ALARM_ID, -1);

		/* load alarm details from database */
		alarm = Alarms.getAlarm(getContentResolver(), alarmId);
		// Bad alarm, bail to avoid a NPE.
		if (alarm == null) {
			finish();
			return;
		}

		mEnabled = alarm.enabled;
		mHour = alarm.hour;
		mMinutes = alarm.minutes;

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

		alarmTime = (TextView) findViewById(R.id.alarm_time);
		alarmAlert = (TextView) findViewById(R.id.alarm_alert);
		alarmCycle = (TextView) findViewById(R.id.alarm_cycle);
		alarmLabel = (TextView) findViewById(R.id.alarm_label);

		alarmVibrateSwitch = (ToggleSwitch) findViewById(R.id.alarm_vibrate);
		alarmVibrateSwitch.setChecked(alarm.vibrate);
		alarmVibrateSwitch.setOnCheckedChangeListener(this);

		updateTime();
		updateAlert();
		updateCycle();
		updateLabel();
		// Toast.makeText(this, "" + array.toString(), 500).show();
	}

	private void updateTime() {
		alarmTime.setText(Alarms.formatTime(this, mHour, mMinutes, alarm.daysOfWeek));
	}

	private void updateAlert() {
		if (alarm.alert != null) {
			final Ringtone r = RingtoneManager.getRingtone(this, alarm.alert);
			if (r != null) {
				alarmAlert.setText(r.getTitle(this));
			}
		} else {
			alarmAlert.setText(R.string.silent_alarm_summary);
		}
	}

	private void updateCycle() {
		alarmCycle.setText(alarm.daysOfWeek.toString(this, true));
	}

	private void updateLabel() {
		alarmLabel.setText(alarm.label);
	}

	private void saveAlarm() {
		long time = Alarms.setAlarm(this, alarmId, mEnabled, mHour, mMinutes, alarm.daysOfWeek, alarm.vibrate, alarm.label, alarm.alert.toString());

		if (mEnabled) {
			popAlarmSetToast(this, time);
		}
	}

	private static void popAlarmSetToast(Context context, long timeInMillis) {
		String toastText = formatToast(context, timeInMillis);
		Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
		ToastMaster.setToast(toast);
		toast.show();
	}

	/**
	 * format "Alarm set for 2 days 7 hours and 53 minutes from now"
	 */
	static String formatToast(Context context, long timeInMillis) {
		long delta = timeInMillis - System.currentTimeMillis();
		long hours = delta / (1000 * 60 * 60);
		long minutes = delta / (1000 * 60) % 60;
		long days = hours / 24;
		hours = hours % 24;

		String daySeq = (days == 0) ? "" : (days == 1) ? context.getString(R.string.day) : context.getString(R.string.days, Long.toString(days));
		String minSeq = (minutes == 0) ? "" : (minutes == 1) ? context.getString(R.string.minute) : context.getString(R.string.minutes, Long.toString(minutes));
		String hourSeq = (hours == 0) ? "" : (hours == 1) ? context.getString(R.string.hour) : context.getString(R.string.hours, Long.toString(hours));

		boolean dispDays = days > 0;
		boolean dispHour = hours > 0;
		boolean dispMinute = minutes > 0;

		int index = (dispDays ? 1 : 0) | (dispHour ? 2 : 0) | (dispMinute ? 4 : 0);

		String[] formats = context.getResources().getStringArray(R.array.alarm_set);
		return String.format(formats[index], daySeq, hourSeq, minSeq);
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
			new TimePickerDialog(this, this, mHour, mMinutes, DateFormat.is24HourFormat(this)).show();
			break;

		case R.id.alarm_cycle_setting:
			intent = new Intent(this, SetAlarmCycle.class);
			intent.putExtra("cycle", alarm.daysOfWeek.getBooleanArray());
			startActivityForResult(intent, ALARM_CYCLE);
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
			break;

		case R.id.alarm_alert_setting:
			intent = new Intent(this, SetAlarmAlert.class);
			intent.putExtra("alert", alarm.alert);
			startActivityForResult(intent, ALARM_ALERT);
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ALARM_CYCLE:
			boolean[] array = data.getBooleanArrayExtra("cycle");
			for (int i = 0; i < array.length; i++) {
				alarm.daysOfWeek.set(i, array[i]);
			}
			updateCycle();
			break;

		case ALARM_ALERT:
			alarm.alert = (Uri) data.getParcelableExtra("alert");
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
			alarm.vibrate = isChecked;
			break;
		}
	}

	public void onChangeAttempted(boolean isChecked) {
		// TODO Auto-generated method stub

	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMinutes = minute;
		updateTime();
		// If the time has been changed, enable the alarm.
		mEnabled = true;
	}

}
