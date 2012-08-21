package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;

public class SetAlarmCycle extends BaseActivity implements OnClickListener {

	private boolean[] daysOfWeekBooleanArray;

	private View backlAction;

	private CheckedTextView Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm_cycle);

		Intent i = getIntent();
		
		daysOfWeekBooleanArray = i.getBooleanArrayExtra("cycle"); 
			
		if (daysOfWeekBooleanArray == null) {
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
		Monday.setChecked(daysOfWeekBooleanArray[0]);
		Tuesday.setChecked(daysOfWeekBooleanArray[1]);
		Wednesday.setChecked(daysOfWeekBooleanArray[2]);
		Thursday.setChecked(daysOfWeekBooleanArray[3]);
		Friday.setChecked(daysOfWeekBooleanArray[4]);
		Saturday.setChecked(daysOfWeekBooleanArray[5]);
		Sunday.setChecked(daysOfWeekBooleanArray[6]);
	}

	public void onClick(View v) {
		
		if(v.getId() == R.id.back){
			finish();
			return;
		}
		
		CheckedTextView ctv = (CheckedTextView) v;
		ctv.toggle();
		
		switch (v.getId()) {
		case R.id.Monday:
			daysOfWeekBooleanArray[0] = ctv.isChecked();
			break;
			
		case R.id.Tuesday:
			daysOfWeekBooleanArray[1] = ctv.isChecked();
			break;
			
		case R.id.Wednesday:
			daysOfWeekBooleanArray[2] = ctv.isChecked();
			break;
			
		case R.id.Thursday:
			daysOfWeekBooleanArray[3] = ctv.isChecked();
			break;
			
		case R.id.Friday:
			daysOfWeekBooleanArray[4] = ctv.isChecked();
			break;
			
		case R.id.Saturday:
			daysOfWeekBooleanArray[5] = ctv.isChecked();
			break;
			
		case R.id.Sunday:
			daysOfWeekBooleanArray[6] = ctv.isChecked();
			break;
		}
	}

	@Override
	public void finish() {
		Intent intent = getIntent();
		intent.putExtra("cycle", daysOfWeekBooleanArray);
		setResult(RESULT_CANCELED, intent);
		super.finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

}
