package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class SetAlarmAlert extends BaseActivity implements OnClickListener {

	private MediaPlayer mediaPlayer;

	private Uri alert;
	private Uri currentAlert;
	private Uri defaultAlert;

	private static Uri currentPlay;

	private View backlAction;

	private View silentLayout;
	private RadioButton silentRadio;

	private View defaultRingtoneLayout;
	private TextView defaultRingtoneTextView;
	private RadioButton defaultRingtoneRadio;

	private View currentRingtoneLayout;
	private TextView currentRingtoneTextView;
	private RadioButton currentRingtoneRadio;
	
	private View systemRingtones;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm_alert);

		Intent intent = getIntent();
		currentAlert = (Uri) intent.getParcelableExtra("alert");
		alert = currentAlert;

		defaultAlert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);

		backlAction = findViewById(R.id.back);
		backlAction.setOnClickListener(this);

		silentLayout = findViewById(R.id.silent_layout);
		silentLayout.setOnClickListener(this);

		silentRadio = (RadioButton) findViewById(R.id.silent_radio);

		defaultRingtoneLayout = findViewById(R.id.default_ringtone_layout);
		defaultRingtoneLayout.setOnClickListener(this);

		defaultRingtoneTextView = (TextView) findViewById(R.id.default_ringtone);

		defaultRingtoneRadio = (RadioButton) findViewById(R.id.default_ringtone_radio);

		currentRingtoneLayout = findViewById(R.id.current_ringtone_layout);
		currentRingtoneLayout.setOnClickListener(this);

		currentRingtoneTextView = (TextView) findViewById(R.id.current_ringtone);

		currentRingtoneRadio = (RadioButton) findViewById(R.id.current_ringtone_radio);

		systemRingtones = findViewById(R.id.system_ringtones_layout);
		systemRingtones.setOnClickListener(this);
		
		updateRingtones();
	}

	private void updateRingtones() {
		Ringtone defaultRingtone = RingtoneManager.getRingtone(this, defaultAlert);

		if (defaultRingtone != null) {
			defaultRingtoneTextView.setText(defaultRingtone.getTitle(this));
		}

		if (currentAlert == null) {
			silentRadio.setChecked(true);
			currentRingtoneLayout.setVisibility(View.GONE);
		} else {
			if (currentAlert.equals(defaultAlert)) {
				currentRingtoneLayout.setVisibility(View.GONE);
				defaultRingtoneRadio.setChecked(true);
			} else {
				Ringtone currentRingtone = RingtoneManager.getRingtone(this, currentAlert);
				if (currentRingtone != null) {
					currentRingtoneTextView.setText(currentRingtone.getTitle(this));
					currentRingtoneRadio.setChecked(true);
				}
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.silent_layout:
			silentRadio.setChecked(true);
			defaultRingtoneRadio.setChecked(false);
			currentRingtoneRadio.setChecked(false);
			stopPlayMedia();
			break;

		case R.id.default_ringtone_layout:
			silentRadio.setChecked(false);
			defaultRingtoneRadio.setChecked(true);
			currentRingtoneRadio.setChecked(false);
			autoPlayMedia(defaultAlert);
			break;

		case R.id.current_ringtone_layout:
			silentRadio.setChecked(false);
			defaultRingtoneRadio.setChecked(false);
			currentRingtoneRadio.setChecked(true);
			autoPlayMedia(currentAlert);
			break;
			
		case R.id.system_ringtones_layout:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.RINGTONE_PICKER");
			startActivityForResult(intent, 3);
			break;

		default:
			break;
		}
	}

	private void autoPlayMedia(Uri uri) {
		alert = uri;
		if (currentPlay != uri) {
			if (mediaPlayer != null) {
				stopPlayMedia();
			}
			mediaPlayer = MediaPlayer.create(this, uri);
			mediaPlayer.start();
			currentPlay = uri;
		} else {
			stopPlayMedia();
		}
	}

	private void stopPlayMedia() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
			currentPlay = null;
		}
		alert = null;
	}

	@Override
	public void finish() {
		 Intent intent = getIntent();
		 intent.putExtra("alert", alert);
		 setResult(RESULT_CANCELED, intent);
		super.finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}

	}

}
