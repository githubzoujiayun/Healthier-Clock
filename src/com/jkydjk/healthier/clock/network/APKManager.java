package com.jkydjk.healthier.clock.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.HealthierApplication;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.Version;
import com.jkydjk.healthier.clock.ui.dialog.CustomProgressDialog;
import com.jkydjk.healthier.clock.util.NetDialog;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.CustomDialog;

/**
 * APK 文件的下载与安装
 * 
 * @author miclle
 * 
 */
public class APKManager {

  private static final String savePath = "/sdcard/healthier/";

  private Context context;

  private Version version;

  public APKManager(Context context) {
    this.context = context;
  }

  /**
   * 检查应用版本
   * 
   * @param context
   */
  public void checkApplicationVersion() {
    new CheckUpdateTask().execute();
  }

  /**
   * 查检应用版本异步任务
   * 
   * @author miclle
   * 
   */
  class CheckUpdateTask extends AsyncTask<String, ProgressDialog, Version> {

    @Override
    protected Version doInBackground(String... params) {
      try {

        version = new Version();

        HttpClientManager httpClientManager = new HttpClientManager(context, RequestRoute.VERSION);

        httpClientManager.execute(ResuestMethod.GET);

        JSONObject json = new JSONObject(httpClientManager.getResponse());

        version.code = json.getInt("code");
        version.name = json.getString("name");
        version.description = json.getString("description");
        version.url = json.getString("url");

        return version;

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Version version) {
      if (HealthierApplication.getVersionCode() < version.code) {
        final CustomDialog dialog = new CustomDialog(context);

        dialog.setTitle(R.string.version_update);
        dialog.setContentText(R.string.version_update_tip);

        dialog.setPositiveButton(R.string.a_later_date, new OnClickListener() {
          public void onClick(View v) {
            dialog.dismiss();
            // startActivity(new Intent(Healthier.this, Signup.class));
          }
        });

        dialog.setNegativeButton(R.string.download_the_update, new OnClickListener() {
          public void onClick(View v) {
            dialog.dismiss();
            openBrowserDownload();
          }
        });
        dialog.show();
      }

      super.onPostExecute(version);
    }
  }

  /**
   * TODO: 打开浏览器下载APK
   */
  private void openBrowserDownload() {
    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(version.url));
    context.startActivity(viewIntent);
  }

}