package com.jkydjk.healthier.clock;

import android.app.Application;

import com.jkydjk.healthier.clock.util.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HealthierApplication extends Application {
  
  @Override
  public void onCreate() {
    super.onCreate();
    
    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(ImageLoaderUtil.getImageLoaderConfiguration(getApplicationContext()));
  }
  
}
