package com.jkydjk.healthier.clock.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AnimationUtil {

  /**
   * 设置图片以中心旋转动画
   * 
   * @param image
   * @return
   */
  public static Animation setRotationCenter(ImageView image) {
    int w = image.getDrawable().getIntrinsicWidth();
    int h = image.getDrawable().getIntrinsicHeight();
    RotateAnimation anim = new RotateAnimation(0f, 360f, w / 2f, h / 2f);
    anim.setInterpolator(new LinearInterpolator());
    anim.setRepeatCount(Animation.INFINITE);
    anim.setDuration(972);
    // image.startAnimation(anim);
    image.setAnimation(anim);
    // image.refreshDrawableState();
    return anim;
  }

  /**
   * 移动View上的动画
   * 
   * @param view
   */
  public static void stopAnimation(View view) {
    view.setAnimation(null);
  }

}
