package com.jkydjk.healthier.clock;

import android.app.Application;
import android.content.pm.PackageManager;

import com.jkydjk.healthier.clock.util.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HealthierApplication extends Application {

  public static final int VERSION = 1;

  public static final String VERSION_NAME = "1.0BETA";

  public static PackageManager packageManager;

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(ImageLoaderUtil.getImageLoaderConfiguration(getApplicationContext()));

    packageManager = getPackageManager();

  }

  // public static int version() {
  // try {
  //
  // PackageInfo packageInfo = packageManager.getPackageInfo("com.testSocket",
  // 0);
  //
  // return packageInfo.versionCode;
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  //
  // return -1;
  // }

}
