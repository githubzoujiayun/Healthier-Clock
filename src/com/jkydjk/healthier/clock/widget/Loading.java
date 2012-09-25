package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.R;

import android.R.style;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 
 * <com.jkydjk.healthier.clock.widget.Loading
 * android:layout_width="wrap_content" android:layout_height="wrap_content"
 * automatic="false" />
 * 
 * @author miclle
 * 
 */

public class Loading extends ProgressBar {

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
    
//    style="@android:style/Widget.ProgressBar.Small"
    
    if (automatic) {
      startLoading();
    } else {
      stopLoading();
    }
  }

  public void startLoading() {
    setIndeterminateDrawable(getResources().getDrawable(R.drawable.animate_loading));
  }

  public void stopLoading() {
    setIndeterminateDrawable(getResources().getDrawable(R.drawable.icon_refresh));
  }

}