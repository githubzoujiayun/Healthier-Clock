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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.HealthierApplication;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.ui.dialog.CustomProgressDialog;
import com.jkydjk.healthier.clock.util.NetDialog;

/**
 * apk 文件的下载与安装
 * 
 * @author echowang_2011@163.com
 * 
 */
public class APKManager {

  private Context mContext;

  // 提示语
  private String updateMsg = "该软件有的新版本，请您更新！";
  private String currentVersion = "";

  // 返回的安装包url
  private String apkNumber = "";
  private String description = "";
  private String apkUrl = "";

  private Dialog noticeDialog;

  private Dialog downloadDialog;
  /* 下载包安装路径 */
  private static final String savePath = "/sdcard/healthier/";

  private String saveFileName = savePath + "healthier" + currentVersion + ".apk";

  /* 进度条与通知ui刷新的handler和msg常量 */
  private ProgressBar mProgress;
  private TextView txtContext;
  private boolean interceptFlag = false;

  private static final int DOWN_UPDATE = 1;
  private static final int DOWN_OVER = 2;
  private LayoutInflater inflater = null;
  private int progress;

  private CustomProgressDialog progressDialog;

  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case DOWN_UPDATE:
        mProgress.setProgress(progress);
        txtContext.setText(progress + "%");
        break;
      case DOWN_OVER:
        installApk();
        downloadDialog.dismiss();
        break;
      default:
        break;
      }
    };
  };

  public APKManager(Context context) {
    this.mContext = context;
    currentVersion = HealthierApplication.VERSION_NAME;
    inflater = LayoutInflater.from(context);
    Log.i("currentVersion=", currentVersion);

  }

  // 外部接口让主Activity调用
  public void checkUpdateInfo() {
    // 判断当前版本
    // 检测网络
    if (NetUtil.checkNetWork(mContext)) {

      progressDialog = new CustomProgressDialog(mContext);

      // proDialog.show(mContext, "提示", "数据正在加载...", true);
      // proDialog = ProgressDialog.show(mContext, "提示", "数据正在加载...",
      // true);
      progressDialog.setTitle("提示");
      progressDialog.setMessage("数据正在加载...");
      progressDialog.setCanceledOnTouchOutside(false);

      new getWebVersion(progressDialog).execute();

    } else {
      NetDialog.getNetDialog(mContext).show();
    }
  }

  // 首页显示的时候执行
  public void CheckUpdateTask() {
    new NetCheckUpdateTask().execute();
  }

  private class NetCheckUpdateTask extends AsyncTask<Void, ProgressDialog, Map<String, String>> {

    protected Map<String, String> doInBackground(Void... params) {
      return getWebVersion();
    }

    protected void onPostExecute(Map<String, String> result) {
      super.onPostExecute(result);
      if (result != null) {
        apkNumber = result.get("apkNumber");
        description = result.get("description");
        apkUrl = result.get("apkUrl");
        Log.i("result===>", apkNumber + description + apkUrl);
        if (!currentVersion.equals(apkNumber)) {
          showNoticeDialog(description);
        }
      }
    }
  }

  private class getWebVersion extends AsyncTask<Void, CustomProgressDialog, Map<String, String>> {
    private CustomProgressDialog dialog = null;

    public getWebVersion(CustomProgressDialog dialog) {
      this.dialog = dialog;
    }

    protected Map<String, String> doInBackground(Void... params) {
      return getWebVersion();
    }

    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog.show();
    }

    protected void onPostExecute(Map<String, String> result) {

      super.onPostExecute(result);

      if (result != null) {
        apkNumber = result.get("apkNumber");
        description = result.get("description");
        apkUrl = result.get("apkUrl");
        // apkUrl="http://192.168.0.104:3000/apps/healthier-clock.apk_1.0.3_20120413234451.apk";
        Log.i("result===>", apkNumber + description + apkUrl);

        if (!currentVersion.equals(apkNumber)) {
          showNoticeDialog(description);
        } else {
          Toast.makeText(mContext, "当前为最新版本", Toast.LENGTH_SHORT).show();
        }
      }
      publishProgress();
    }

    protected void onProgressUpdate(CustomProgressDialog... values) {

      super.onProgressUpdate(values);

      dialog.dismiss();
    }
  }

  // 获取网络版本
  private Map<String, String> getWebVersion() {
    Map<String, String> apkVsersion = null;
    String path = RequestRoute.REQUEST_PATH + "version/android";
    Log.i("path=", path);
    HttpClientManager httpClientManager = new HttpClientManager(mContext, path);
    try {
      httpClientManager.execute(ResuestMethod.GET);
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
    }

    String result = httpClientManager.getResponse();
    if (result != null || !"".equals(result)) {
      Log.i("result => ", result + "");
      JSONObject json = null;
      int status = 0;
      try {
        json = new JSONObject(result);
        status = json.getInt("status");
        if (status == 1) {
          JSONObject version = json.getJSONObject("version");
          apkVsersion = new HashMap<String, String>();
          apkVsersion.put("apkNumber", version.getString("number"));
          apkVsersion.put("description", version.getString("description"));
          apkVsersion.put("apkUrl", version.getString("url"));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return apkVsersion;
  }

  public void downloadAPK(String apkUrl) {
    this.apkUrl = apkUrl;
    showDownloadDialog("下载新软件");
  }

  // 提示下载dialog
  private void showNoticeDialog(String strDis) {
    Builder builder = new Builder(mContext);
    builder.setTitle("软件版本更新");
    builder.setMessage(strDis);
    builder.setPositiveButton("下载", new OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        showDownloadDialog("软件版本更新");
      }
    });
    builder.setNegativeButton("以后再说", new OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    noticeDialog = builder.create();
    noticeDialog.setCanceledOnTouchOutside(false);
    noticeDialog.show();
  }

  private void showDownloadDialog(String dialogTitle) {
    // 创建下载异步
    final DownApk downApk = new DownApk(apkUrl);
    Builder builder = new Builder(mContext);
    builder.setTitle(dialogTitle);

    View v = inflater.inflate(R.layout.progress, null);

    mProgress = (ProgressBar) v.findViewById(R.id.progress);

    txtContext = (TextView) v.findViewById(R.id.txt);

    builder.setView(v);
    builder.setNegativeButton("取消", new OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        interceptFlag = true;
        downApk.cancel(true);
      }
    });
    downloadDialog = builder.create();

    downloadDialog.setCanceledOnTouchOutside(false);

    downloadDialog.show();
    
    // 下载apk
    downApk.execute();
  }

  private class DownApk extends AsyncTask<Void, Void, File> {
    private String dowmUrl = "";

    public DownApk(String dowmUrl) {
      this.dowmUrl = dowmUrl;
    }

    protected File doInBackground(Void... params) {
      return downLoadFile(dowmUrl);
    }

    protected void onPostExecute(File result) {
      super.onPostExecute(result);
      mHandler.sendEmptyMessage(DOWN_OVER);
    }
  }

  protected File downLoadFile(String httpUrl) {
    File tmpFile = new File(savePath);
    if (!tmpFile.exists()) {
      tmpFile.mkdir();
    }
    final File file = new File(saveFileName);
    try {
      URL url = new URL(httpUrl);
      try {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        Log.i("data_length", "conn=" + conn.getContentLength());
        double length = conn.getContentLength();
        InputStream is = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[256];
        conn.connect();
        double count = 0;
        if (length == -1 || conn.getResponseCode() >= 400) {
          Toast.makeText(mContext, "网络问题连接超时", Toast.LENGTH_SHORT).show();
        } else {
          while (!interceptFlag) {
            if (is != null) {
              int numRead = is.read(buf);
              count += numRead;
              progress = (int) (((float) count / length) * 100);
              mHandler.sendEmptyMessage(DOWN_UPDATE);
              if (numRead <= 0) {
                // mHandler.sendEmptyMessage(DOWN_OVER);
                break;
              } else {
                fos.write(buf, 0, numRead);
              }
            }
          }
        }
        conn.disconnect();
        fos.close();
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return file;
  }

  /**
   * 安装apk
   * 
   * 打开文件就会自动调os中自带的安装软件,从而进行安装
   * 
   * @param url
   */
  private void installApk() {
    File file = new File(saveFileName);
    Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setAction(android.content.Intent.ACTION_VIEW);
    /* get the MimeType */
    String type = getMIMEType(file);
    intent.setDataAndType(Uri.fromFile(file), type);
    mContext.startActivity(intent);
  }

  /* 获取要打开文件的类型 */
  private String getMIMEType(File f) {
    String type = "";
    String fName = f.getName();
    String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
    if (end.equals("apk")) {
      type = "application/vnd.android.package-archive";
    } else {
      type = "*";
    }
    if (end.equals("apk")) {
    } else {
      type += "/*";
    }
    Log.i("getMIMEType type=", type);
    return type;
  }
  
}