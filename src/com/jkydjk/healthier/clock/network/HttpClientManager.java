package com.jkydjk.healthier.clock.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import com.jkydjk.healthier.clock.database.PhoneInfo;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.content.SharedPreferences;

public class HttpClientManager {
  private static final String TAG = "HttpClientManager";
  private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时3秒钟
  private static final int SO_TIMEOUT = 30 * 1000; // 设置等待数据超时时间20秒钟

  private ArrayList<NameValuePair> params;
  private ArrayList<NameValuePair> headers;
  private String url;
  private int responseCode;
  private String message;
  private String response;

  public String getResponse() {
    return response;
  }

  // 返回网络请求的错误信息
  public String getErrorMessage() {
    return message;
  }

  // 返回网络请求的结果字符串
  public int getResponseCode() {
    return responseCode;
  }

  public HttpClientManager(Context context, String url) {
    this.url = url;
    params = new ArrayList<NameValuePair>();
    headers = new ArrayList<NameValuePair>();

    addDefultParam(context);
  }

  // 添加请求时的默认参数
  private void addDefultParam(Context context) {
    PhoneInfo phoneInfo = new PhoneInfo(context);
    SharedPreferences sharedPreferences = context.getSharedPreferences("configure", Context.MODE_PRIVATE);
    params.add(new BasicNameValuePair("v", "1.0"));
    params.add(new BasicNameValuePair("phone", phoneInfo.phoneNumber()));
    // params.add(new BasicNameValuePair("phone", "13681989881"));
    // System.out.println("imei => " + phoneInfo.phoneIMEI());
    params.add(new BasicNameValuePair("imei", phoneInfo.phoneIMEI()));
    params.add(new BasicNameValuePair("token", sharedPreferences.getString("token", "")));
  }

  // 添加访问的参数
  public void addParam(String name, String value) {
    params.add(new BasicNameValuePair(name, value));
  }

  // 添加访问的头信息
  public void addHeader(String name, String value) {
    headers.add(new BasicNameValuePair(name, value));
  }

  // 根据请求的方式执行网络访问
  public void execute(ResuestMethod method) throws Exception {
    switch (method) {
    case GET: {
      StringBuffer sBuffer = new StringBuffer();
      if (!params.isEmpty()) {
        sBuffer.append("?");
        for (NameValuePair param : params) {
          if (sBuffer.length() > 1) {
            sBuffer.append("&");
          }
          sBuffer.append(param.getName()).append("=").append(URLEncoder.encode(param.getValue() == null ? "" : param.getValue(), HTTP.UTF_8));
        }
      }
      Log.v("params => " + sBuffer.toString());
      HttpGet request = new HttpGet(url + sBuffer.toString());
      for (NameValuePair head : headers) {
        request.addHeader(head.getName(), head.getValue());
      }

      executeRequest(request, url);
      break;
    }
    case POST: {
      HttpPost request = new HttpPost(url);
      for (NameValuePair head : headers) {
        request.addHeader(head.getName(), head.getValue());
      }

      if (!params.isEmpty()) {
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
      }

      executeRequest(request, url);
      break;
    }
    case PUT: {
      HttpPut request = new HttpPut(url);

      for (NameValuePair head : headers) {
        request.addHeader(head.getName(), head.getValue());
      }

      if (!params.isEmpty()) {
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
      }

      executeRequest(request, url);
      break;
    }
    case DELETE: {
      StringBuffer sBuffer = new StringBuffer();
      if (!params.isEmpty()) {
        sBuffer.append("?");
        for (NameValuePair param : params) {
          if (sBuffer.length() > 1) {
            sBuffer.append("&");
          }
          sBuffer.append(param.getName()).append("=").append(URLEncoder.encode(param.getValue(), HTTP.UTF_8));
        }
      }

      HttpDelete request = new HttpDelete(url + sBuffer.toString());

      for (NameValuePair head : headers) {
        request.addHeader(head.getName(), head.getValue());
      }

      executeRequest(request, url);
      break;
    }
    }
  }

  // 执行网络请求
  private void executeRequest(HttpUriRequest request, String url) {
    // 设置网络请求时间和数据等待时间
    BasicHttpParams httpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);

    HttpClient client = new DefaultHttpClient(httpParams);
    HttpResponse httpResponse = null;
    try {
      httpResponse = client.execute(request);
      responseCode = httpResponse.getStatusLine().getStatusCode();
      message = httpResponse.getStatusLine().getReasonPhrase();
      HttpEntity entity = httpResponse.getEntity();
      if (entity != null) {
        InputStream instream = entity.getContent();
        response = convertStreamToString(instream);
        // Closing the input stream will trigger connection release
        instream.close();
      }
    } catch (ClientProtocolException e) {
      client.getConnectionManager().shutdown();
      Log.e(TAG + e.getMessage());
    } catch (IOException e) {
      client.getConnectionManager().shutdown();
      e.printStackTrace();
      Log.e(TAG + e.getMessage());
    }
  }

  // 将网络流转换为字符串
  private static String convertStreamToString(InputStream is) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (IOException e) {
      Log.e(TAG + e.getMessage());
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        Log.e(TAG + e.getMessage());
      }
    }
    return sb.toString();
  }
}
