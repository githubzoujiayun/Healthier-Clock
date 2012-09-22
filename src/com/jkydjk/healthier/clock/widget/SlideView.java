package com.jkydjk.healthier.clock.widget;

import java.util.ArrayList;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class SlideView extends ViewPager {

  private ArrayList<View> pages;

  public SlideView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // LayoutInflater inflater = getLayoutInflater();
    // View snooze = inflater.inflate(R.layout.alarm_slider_snooze, null);
    // View dismiss = inflater.inflate(R.layout.alarm_slider_dismiss, null);
    // View transparent = inflater.inflate(R.layout.transparent, null);

//    setAdapter(new ViewPageAdapter());

//    getResources().getLayout(R.layout.alarm_slider_snooze);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return super.onInterceptTouchEvent(event);
  }

  // 指引页面数据适配器
  private class ViewPageAdapter extends PagerAdapter {

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

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
      // TODO Auto-generated method stub
    }

    @Override
    public Parcelable saveState() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void startUpdate(View container) {
      // TODO Auto-generated method stub
    }

    @Override
    public void finishUpdate(View container) {
      // TODO Auto-generated method stub
    }
  }

}
