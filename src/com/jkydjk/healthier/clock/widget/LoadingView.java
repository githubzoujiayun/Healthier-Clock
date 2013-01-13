package com.jkydjk.healthier.clock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.jkydjk.healthier.clock.R;

/**
 * 
 * <com.jkydjk.healthier.clock.widget.Loading
 * android:layout_width="wrap_content" android:layout_height="wrap_content"
 * automatic="false" />
 * 
 * @author miclle
 * 
 */

public class LoadingView extends ProgressBar {

  private boolean automatic;

  public LoadingView(Context context) {
    super(context);
  }

  public LoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    automatic = attrs.getAttributeBooleanValue(null, "automatic", false);
    init();
  }

  public LoadingView(Context context, AttributeSet attrs, int paramInt) {
    super(context, attrs, paramInt);
    automatic = attrs.getAttributeBooleanValue(null, "automatic", false);
    init();
  }

  private void init() {
    // style="@android:style/Widget.ProgressBar.Small"
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
    setIndeterminate(false);
    setIndeterminateDrawable(getResources().getDrawable(R.drawable.icon_refresh));
    setBackgroundResource(R.drawable.icon_refresh);
  }

}