package com.jkydjk.healthier.clock;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.jkydjk.healthier.clock.widget.AnimationTabHost;

@SuppressLint("NewApi")
public class Healthier extends TabActivity implements OnTabChangeListener {

	private TabWidget channelTabWidget;
	private AnimationTabHost mTabHost;

	private Intent intentAlarmClock;
	private Intent intentChineseHour;
	private Intent intentSolarTerms;
	private Intent intentFavorites;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTabHost = (AnimationTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setOnTabChangedListener(this);

		channelTabWidget = (TabWidget) findViewById(android.R.id.tabs);

		intentAlarmClock = new Intent().setClass(this, AlarmClock.class);
		intentChineseHour = new Intent().setClass(this, ChineseHour.class);
		intentSolarTerms = new Intent().setClass(this, SolarTerms.class);
		intentFavorites = new Intent().setClass(this, Favorites.class);

		mTabHost.addTab(mTabHost.newTabSpec("alarm").setIndicator(getIndicator(R.string.default_label)).setContent(intentAlarmClock));
		mTabHost.addTab(mTabHost.newTabSpec("chinese_hour").setIndicator(getIndicator(R.string.chinese_hour)).setContent(intentChineseHour));
		mTabHost.addTab(mTabHost.newTabSpec("solar_terms").setIndicator(getIndicator(R.string.solar_terms)).setContent(intentSolarTerms));
		mTabHost.addTab(mTabHost.newTabSpec("favorites").setIndicator(getIndicator(R.string.favorites)).setContent(intentFavorites));

		mTabHost.setOpenAnimation(true);
		mTabHost.setCurrentTab(0);
	}

	/**
	 * set indicator layout
	 * 
	 * @param id
	 * @return
	 */
	private View getIndicator(int id) {
		View indicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);
		((TextView) indicator.findViewById(R.id.title)).setText(id);
		return indicator;
	}

	@SuppressLint("ResourceAsColor")
	public void onTabChanged(String tabId) {
		channelTabWidget.setBackgroundResource(BaseActivity.getImageResourceID(this, "channel_buttons_on_" + tabId));
		int tabs = channelTabWidget.getChildCount();
		for (int i = 0; i < tabs; i++) {
			TextView title = (TextView) channelTabWidget.getChildAt(i).findViewById(R.id.title);
			if (i == mTabHost.getCurrentTab()) {
				title.setPadding(0, 0, 0, 6);
				title.setShadowLayer(1f, 1.5f, 1.5f, 0xffffffff);
			} else {
				title.setPadding(0, 0, 0, 0);
				title.setShadowLayer(1f, 1.5f, 1.5f, 0x00ffffff);
			}
		}
	}
	
}