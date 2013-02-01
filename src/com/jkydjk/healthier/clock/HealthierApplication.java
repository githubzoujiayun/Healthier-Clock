package com.jkydjk.healthier.clock;

import android.app.Application;
import android.content.pm.PackageManager;

import com.jkydjk.healthier.clock.util.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HealthierApplication extends Application {

  public static String PACKAGE_NAME;

  public static PackageManager packageManager;

  @Override
  public void onCreate() {
    super.onCreate();

    packageManager = getPackageManager();

    PACKAGE_NAME = getApplicationContext().getPackageName();

    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(ImageLoaderUtil.getImageLoaderConfiguration(getApplicationContext()));
  }

  /**
   * Application version code
   * 
   * @return
   */
  public static int getVersionCode() {
    try {
      return packageManager.getPackageInfo(PACKAGE_NAME, 0).versionCode;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Application version name
   * 
   * @return
   */
  public static String getVersionName() {
    try {
      return packageManager.getPackageInfo(PACKAGE_NAME, 0).versionName;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
