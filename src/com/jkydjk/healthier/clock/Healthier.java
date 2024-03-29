package com.jkydjk.healthier.clock;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.jkydjk.healthier.clock.entity.Version;
import com.jkydjk.healthier.clock.network.APKManager;
import com.jkydjk.healthier.clock.network.NetUtil;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.AnimationTabHost;
import com.jkydjk.healthier.clock.widget.CustomDialog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class Healthier extends TabActivity implements OnTabChangeListener, OnClickListener {

  private TabWidget channelTabWidget;
  private TabHost mTabHost;

  private Intent intentAlarmClock;
  private Intent intentChineseHour;
  private Intent intentSolarTerms;
  private Intent intentFavorites;

  private View more;
  private View account;

  private View titlebar;
  private View tabs;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 检查更新
    UmengUpdateAgent.update(this);

    setContentView(R.layout.main);

    mTabHost = (TabHost) findViewById(android.R.id.tabhost);
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

    mTabHost.setCurrentTab(0);

    more = findViewById(R.id.more);
    more.setOnClickListener(this);

    account = findViewById(R.id.account);
    account.setOnClickListener(this);

    titlebar = findViewById(R.id.titlebar);
    titlebar.setVisibility(View.VISIBLE);
    tabs = findViewById(android.R.id.tabs);
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

  public void onTabChanged(String tabId) {
    channelTabWidget.setBackgroundResource(ActivityHelper.getImageResourceID(this, "channel_buttons_on_" + tabId));
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

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.more:
      startActivity(new Intent(this, More.class));
      break;

    case R.id.account:

      if (ActivityHelper.isLogged(this)) {
        startActivity(new Intent(Healthier.this, Resume.class));
      } else {
        final CustomDialog dialog = new CustomDialog(this);

        dialog.setTitle(R.string.my_account);
        dialog.setContentText(R.string.account_dialog_tip);

        dialog.setPositiveButton(R.string.signup, new OnClickListener() {
          public void onClick(View v) {
            dialog.dismiss();
            startActivity(new Intent(Healthier.this, Signup.class));
          }
        });

        dialog.setNegativeButton(R.string.signin, new OnClickListener() {
          public void onClick(View v) {
            dialog.dismiss();
            startActivity(new Intent(Healthier.this, Signin.class));
          }
        });
        dialog.show();
      }
      break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    startActivity(new Intent(this, More.class));
    return false;
  }

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