package com.jkydjk.healthier.clock;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.annotation.SuppressLint;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * AlarmClock application.
 */
@SuppressLint("NewApi")
public class AlarmClock extends BaseActivity implements OnClickListener {

	protected static final int MSG_CLOCK = 0x1234;

	static final String PREFERENCES = "AlarmClock";
	static final String PREF_CLOCK_FACE = "face";
	static final String PREF_SHOW_CLOCK = "show_clock";

	/** Cap alarm count at this number */
	static final int MAX_ALARM_COUNT = 12;

	/**
	 * This must be false for production. If true, turns on logging, test code,
	 * etc.
	 */
	static final boolean DEBUG = true;

	private SharedPreferences mPrefs;
	private LayoutInflater mFactory;

	private Button settingsButton;
	private Button addAlarmButton;

	private TextView welcomeTextView;
	private RelativeLayout noAlarmLayout;

	// private View mClock = null;
	private ListView mAlarmsList;
	private Cursor alarmsCursor;

	private String mAm, mPm;

	private final Handler mHandler = new Handler();

	private Handler timer;
	private Thread timerThread;

	private View currentAlarmLayout;

	/*
	 * TODO: it would be nice for this to live in an xml config file.
	 */
	static final int[] CLOCKS = { R.layout.clock_basic_bw, R.layout.clock_googly, R.layout.clock_droid2, R.layout.clock_droids, R.layout.digital_clock };

	private class AlarmTimeAdapter extends CursorAdapter {

		public AlarmTimeAdapter(Context context, Cursor cursor) {
			super(context, cursor);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View ret = mFactory.inflate(R.layout.alarm_time, parent, false);

			((TextView) ret.findViewById(R.id.am)).setText(mAm);
			((TextView) ret.findViewById(R.id.pm)).setText(mPm);

			DigitalClock digitalClock = (DigitalClock) ret.findViewById(R.id.digitalClock);
			digitalClock.setLive(false);
			return ret;
		}

		public void actionToggle(View v) {
			View parentLayout = (View) v.getParent();
			View alarmActionsLayout, arrow;

			if (currentAlarmLayout != null && currentAlarmLayout != parentLayout) {
				alarmActionsLayout = currentAlarmLayout.findViewById(R.id.alarm_actions_layout);
				arrow = currentAlarmLayout.findViewById(R.id.arrow);
				alarmActionsLayout.setVisibility(View.GONE);
				arrow.setVisibility(View.GONE);
				alarmActionsLayout.setEnabled(true);
				currentAlarmLayout = null;
			}

			alarmActionsLayout = parentLayout.findViewById(R.id.alarm_actions_layout);
			arrow = parentLayout.findViewById(R.id.arrow);

			if (alarmActionsLayout.isEnabled() == true) {
				currentAlarmLayout = parentLayout;
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
			
			TextView alarmName = (TextView)view.findViewById(R.id.alarm_name);
			alarmName.setText(alarm.name);

			// CheckBox onButton = (CheckBox)
			// view.findViewById(R.id.alarmButton);
			// onButton.setChecked(alarm.enabled);

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
				}
			});

			DigitalClock digitalClock = (DigitalClock) view.findViewById(R.id.digitalClock);

			// 设置闹钟文字
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

			// 显示标签
			TextView labelView = (TextView) digitalClock.findViewById(R.id.label);
			if (alarm.label != null && alarm.label.length() != 0) {
				labelView.setText(alarm.label);
				labelView.setTextColor(0xff669900);
				labelView.setVisibility(View.VISIBLE);
			} else {
				labelView.setVisibility(View.GONE);
			}

			Button editButton = (Button) view.findViewById(R.id.edit);
			editButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(AlarmClock.this, SetAlarm.class);
					intent.putExtra(Alarms.ALARM_ID, alarm.id);
					startActivity(intent);
				}
			});

			Button deleteButton = (Button) view.findViewById(R.id.delete);
			deleteButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Confirm that the alarm will be deleted.
					new AlertDialog.Builder(AlarmClock.this).setTitle(getString(R.string.delete_alarm)).setMessage(getString(R.string.delete_alarm_confirm))
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int w) {
									Alarms.deleteAlarm(AlarmClock.this, alarm.id);
									updateLayout();
								}
							}).setNegativeButton(android.R.string.cancel, null).show();
				}
			});

		}
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.alarm_clock);

		settingsButton = (Button) findViewById(R.id.settings);
		settingsButton.setOnClickListener(this);

		addAlarmButton = (Button) findViewById(R.id.add_alarm);
		addAlarmButton.setOnClickListener(this);

		welcomeTextView = (TextView) findViewById(R.id.welcome);
		setWelcomeText();

		noAlarmLayout = (RelativeLayout) findViewById(R.id.no_alarm);

		String[] ampm = new DateFormatSymbols().getAmPmStrings();
		mAm = ampm[0];
		mPm = ampm[1];

		mFactory = LayoutInflater.from(this);
		mPrefs = getSharedPreferences(PREFERENCES, 0);

		updateLayout();

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
				updateLayout();
			}
		});
	}

	private void updateLayout() {

		alarmsCursor = Alarms.getAlarmsCursor(getContentResolver());

		mAlarmsList = (ListView) findViewById(R.id.alarms_list);
		mAlarmsList.setAdapter(new AlarmTimeAdapter(this, alarmsCursor));
		mAlarmsList.setVerticalScrollBarEnabled(true);
		// mAlarmsList.setOnItemClickListener(this);
		mAlarmsList.setOnCreateContextMenuListener(this);

		noAlarmLayout.setVisibility(alarmsCursor.getCount() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setWelcomeText();
		updateLayout();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ToastMaster.cancelToast();
		alarmsCursor.deactivate();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		// Inflate the menu from xml.
		getMenuInflater().inflate(R.menu.context_menu, menu);

		// Use the current item to create a custom view for the header.
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		final Cursor c = (Cursor) mAlarmsList.getAdapter().getItem((int) info.position);
		final Alarm alarm = new Alarm(c);

		// Construct the Calendar to compute the time.
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, alarm.hour);
		cal.set(Calendar.MINUTE, alarm.minutes);
		final String time = Alarms.formatTime(this, cal);

		// Inflate the custom view and set each TextView's text.
		final View v = mFactory.inflate(R.layout.context_menu_header, null);
		TextView textView = (TextView) v.findViewById(R.id.header_time);
		textView.setText(time);
		textView = (TextView) v.findViewById(R.id.header_label);
		textView.setText(alarm.label);

		// 设置菜单上的自定义视图
		menu.setHeaderView(v);
		// Change the text to "disable" if the alarm is already enabled.
		if (alarm.enabled) {
			menu.findItem(R.id.enable_alarm).setTitle(R.string.disable_alarm);
		}
	}

	/**
	 * 点击处理
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;

		case R.id.add_alarm:
			startActivity(new Intent(this, AddAlarm.class));
			// overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			break;
		}
	}

	/**
	 * 设置欢迎文字
	 */
	private void setWelcomeText() {
		Time t = new Time();
		t.setToNow();
		int hour = t.hour;

		if (5 <= hour && hour < 11) {
			welcomeTextView.setText(getStringResourceID(getApplicationContext(), "welcome_good_morning"));
		} else if (11 <= hour && hour < 13) {
			welcomeTextView.setText(getStringResourceID(getApplicationContext(), "welcome_good_noon"));
		} else if (13 <= hour && hour < 19) {
			welcomeTextView.setText(getStringResourceID(getApplicationContext(), "welcome_good_afternoon"));
		} else {
			welcomeTextView.setText(getStringResourceID(getApplicationContext(), "welcome_good_evening"));
		}

	}

}