package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * <ProgressBar style="@android:style/Widget.ProgressBar.Small.Inverse"
 * android:layout_width="wrap_content" android:layout_height="wrap_content"
 * android:indeterminateDrawable="@drawable/animate_loading" />
 * 
 * <com.jkydjk.healthier.clock.widget.Loading
 * android:layout_width="wrap_content" android:layout_height="wrap_content"
 * automatic="false" android:background="@drawable/animate_loading" />
 * 
 * @author miclle
 * 
 */

public class Loading extends Button {

  private AnimationDrawable animateLoading;
  private boolean automatic;

  public Loading(Context context) {
    super(context);
  }

  public Loading(Context context, AttributeSet attrs) {
    super(context, attrs);
    automatic = attrs.getAttributeBooleanValue(null, "automatic", false);
    init();

  }

  public Loading(Context context, AttributeSet attrs, int paramInt) {
    super(context, attrs, paramInt);
    automatic = attrs.getAttributeBooleanValue(null, "automatic", false);
    init();
  }

  private void init() {

    setBackgroundResource(R.drawable.animate_loading);
    animateLoading = (AnimationDrawable) getBackground();
    if (automatic) {
      animateLoading.start();
    } else {
      animateLoading.stop();
    }
  }

  public void startLoading() {
    animateLoading.start();
  }

  public void stopLoading() {
    animateLoading.stop();
  }

}