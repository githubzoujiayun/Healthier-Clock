package com.jkydjk.healthier.clock.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageViewPager extends ViewPager {

  public ImageViewPager(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  public ImageViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent arg0) {
    // TODO Auto-generated method stub
    super.onInterceptTouchEvent(arg0);
    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent arg0) {
    // TODO Auto-generated method stub
    super.onTouchEvent(arg0);
    return false;
  }

}
