package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.Toast;

public class SetAlarmCycle extends BaseActivity implements OnClickListener {

	private Alarm alarm;
	private int alarmId;

	private View backlAction;

	private CheckedTextView Monday;
	private CheckedTextView Tuesday;
	private CheckedTextView Wednesday;
	private CheckedTextView Thursday;
	private CheckedTextView Friday;
	private CheckedTextView Saturday;
	private CheckedTextView Sunday;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm_cycle);

		Intent i = getIntent();
		alarmId = i.getIntExtra(Alarms.ALARM_ID, -1);

		alarm = Alarms.getAlarm(getContentResolver(), alarmId);

		if (alarm == null) {
			finish();
			return;
		}

		backlAction = findViewById(R.id.back);
		backlAction.setOnClickListener(this);

		Monday = (CheckedTextView) findViewById(R.id.Monday);
		Monday.setOnClickListener(this);

		Tuesday = (CheckedTextView) findViewById(R.id.Tuesday);
		Tuesday.setOnClickListener(this);

		Wednesday = (CheckedTextView) findViewById(R.id.Wednesday);
		Wednesday.setOnClickListener(this);

		Thursday = (CheckedTextView) findViewById(R.id.Thursday);
		Thursday.setOnClickListener(this);

		Friday = (CheckedTextView) findViewById(R.id.Friday);
		Friday.setOnClickListener(this);

		Saturday = (CheckedTextView) findViewById(R.id.Saturday);
		Saturday.setOnClickListener(this);

		Sunday = (CheckedTextView) findViewById(R.id.Sunday);
		Sunday.setOnClickListener(this);

		updateCycle();
	}

	private void updateCycle() {
		boolean[] array = alarm.daysOfWeek.getBooleanArray();
		
//		alarm.daysOfWeek.getCoded();
		
		Toast.makeText(this, "" + array.toString(), 500).show();
		// alarmCycle.setText(alarm.daysOfWeek.toString(this, true));
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.Monday:
		case R.id.Tuesday:
		case R.id.Wednesday:
		case R.id.Thursday:
		case R.id.Friday:
		case R.id.Saturday:
		case R.id.Sunday:
			CheckedTextView ctv = (CheckedTextView) v;
			ctv.toggle();
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

}
