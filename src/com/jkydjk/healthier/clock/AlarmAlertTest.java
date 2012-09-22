package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import com.jkydjk.healthier.clock.util.Log;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

/**
 * AlarmClock application.
 */
public class AlarmAlertTest extends BaseActivity {

  ArrayList<View> pages = new ArrayList<View>();

  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setContentView(R.layout.alarm_alert);

    LayoutInflater inflater = getLayoutInflater();
    
    Log.v("inflater" + inflater);

    View snooze = inflater.inflate(R.layout.alarm_slider_snooze, null);
    View dismiss = inflater.inflate(R.layout.alarm_slider_dismiss, null);

    View transparent = inflater.inflate(R.layout.transparent, null);

    pages.add(snooze);
    pages.add(transparent);

    ViewPager snoozeSlider = (ViewPager) findViewById(R.id.snooze_slider);
    snoozeSlider.setAdapter(new SlidePageAdapter());

    // snoozeSlider.addView(snooze);
    // ViewPager dismissSlider = (ViewPager) findViewById(R.id.dismiss_slider);
    // dismissSlider.addView(dismiss);

  }

  // 指引页面数据适配器
  private class SlidePageAdapter extends PagerAdapter {

    public SlidePageAdapter() {
      super();
    }

    @Override
    public int getCount() {
      return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
      return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
      ((ViewPager) container).removeView(pages.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
      ((ViewPager) container).addView(pages.get(position));
      return pages.get(position);
    }

  }

}