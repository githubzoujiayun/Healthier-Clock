package com.jkydjk.healthier.clock.animation;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.jkydjk.healthier.clock.R;

/**
 * 
 * @author miclle
 * 
 */
public class Cycling {

  /**
   * 开始加载动画
   * 
   * @param view
   */
  public static void start(View view) {
    AnimationDrawable animationDrawable = (AnimationDrawable) view.getResources().getDrawable(R.drawable.animate_loading);
    view.setBackgroundDrawable(animationDrawable);
    animationDrawable.start();
  }

  /**
   * 停止加载动画
   * 
   * @param view
   */
  public static void stop(View view) {
    Drawable drawable = view.getBackground();
    if (drawable instanceof AnimationDrawable) {
      ((AnimationDrawable) drawable).stop();
    }
    view.setBackgroundResource(R.drawable.icon_refresh);
  }
}
