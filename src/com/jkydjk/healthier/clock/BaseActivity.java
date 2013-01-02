package com.jkydjk.healthier.clock;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.widget.Toast;

public class BaseActivity extends Activity {

  public static final int COMPLETE = 0x000005;

  public static final File SDCARD = Environment.getExternalStorageDirectory().getAbsoluteFile();

  /**
   * Android动态获取图片资源
   * 
   * @param context
   * @param name
   * @return Resource ID: R.drawable.xxxxx
   */
  public static int getImageResourceID(Context context, String name) {
    return context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + name, null, null);
  }

  /**
   * Android动态获取文本资源
   * 
   * @param context
   * @param name
   * @return Resource ID: R.string.xxxxx
   */
  public static int getStringResourceID(Context context, String name) {
    return context.getResources().getIdentifier(context.getPackageName() + ":string/" + name, null, null);
  }
  
}
