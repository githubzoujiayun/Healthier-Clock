package com.jkydjk.healthier.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;

/**
 * Displays the time
 */
public class DigitalClock extends LinearLayout {

	private final static String M12 = "h:mm";

	private Calendar mCalendar;
	private String mFormat;
	private TextView mTimeDisplay;
	private AmPm mAmPm;
	private boolean mAnimate;
	private ContentObserver mFormatChangeObserver;
	private boolean mLive = true;
	private boolean mAttached;

	/* called by system on minute ticks */
	private final Handler mHandler = new Handler();
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (mLive && intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				mCalendar = Calendar.getInstance();
			}
			updateTime();
		}
	};

	static class AmPm {
		private int mColorOn, mColorOff;

		private LinearLayout mAmPmLayout;
		private TextView mAm, mPm;

		AmPm(View parent) {
			mAmPmLayout = (LinearLayout) parent.findViewById(R.id.am_pm);
			mAm = (TextView) mAmPmLayout.findViewById(R.id.am);
			mPm = (TextView) mAmPmLayout.findViewById(R.id.pm);

			Resources r = parent.getResources();
			mColorOn = r.getColor(R.color.ampm_on);
			mColorOff = r.getColor(R.color.ampm_off);
		}

		void setShowAmPm(boolean show) {
			mAmPmLayout.setVisibility(show ? View.VISIBLE : View.GONE);
		}

		void setIsMorning(boolean isMorning) {
			mAm.setTextColor(isMorning ? mColorOn : mColorOff);
			mPm.setTextColor(isMorning ? mColorOff : mColorOn);
		}
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			setDateFormat();
			updateTime();
		}
	}

	public DigitalClock(Context context) {
		this(context, null);
	}

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
		mAmPm = new AmPm(this);
		mCalendar = Calendar.getInstance();

		setDateFormat();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (Log.LOGV)
			Log.v("onAttachedToWindow " + this);

		if (mAttached)
			return;
		mAttached = true;

		if (mAnimate) {
			setBackgroundResource(R.drawable.animate_circle);
			/* Start the animation (looped playback by default). */
			((AnimationDrawable) getBackground()).start();
		}

		if (mLive) {
			/* monitor time ticks, time changed, timezone */
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
		}

		/* monitor 12/24-hour display preference */
		mFormatChangeObserver = new FormatChangeObserver();
		getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mFormatChangeObserver);

		updateTime();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		if (!mAttached)
			return;
		mAttached = false;

		Drawable background = getBackground();
		if (background instanceof AnimationDrawable) {
			((AnimationDrawable) background).stop();
		}

		if (mLive) {
			getContext().unregisterReceiver(mIntentReceiver);
		}
		getContext().getContentResolver().unregisterContentObserver(mFormatChangeObserver);
	}

	public void updateTime(Calendar c) {
		mCalendar = c;
		updateTime();
	}

	private void updateTime() {
		if (mLive) {
			mCalendar.setTimeInMillis(System.currentTimeMillis());
		}

		CharSequence newTime = DateFormat.format(mFormat, mCalendar);
		mTimeDisplay.setText(newTime);
		mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
	}

	private void setDateFormat() {
		mFormat = Alarms.get24HourMode(getContext()) ? Alarms.M24 : M12;
		mAmPm.setShowAmPm(mFormat == M12);
	}

	void setAnimate() {
		mAnimate = true;
	}

	public void setLive(boolean live) {
		mLive = live;
	}
}