package com.jkydjk.healthier.clock.network;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class NetUtil {
  private static Location tempLocation = null;

  public static boolean checkNetWork(Context context) {
    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
    try {
      ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivity != null) {
        // 获取网络连接管理的对象
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
          // 判断当前网络是否已经连接
          if (info.getState() == NetworkInfo.State.CONNECTED) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
      Log.v("error", e.toString());
    }
    return false;
  }

  public static Location getLocation(Context context) {
    Location location = null;
    // 获取位置管理服务
    LocationManager locationManager;
    String serviceName = Context.LOCATION_SERVICE;
    locationManager = (LocationManager) context.getSystemService(serviceName);
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      // 查找到服务信息
      Criteria criteria = new Criteria();
      criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

      String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
      location = locationManager.getLastKnownLocation(provider);

      if (location == null) {
        LocationListener listener = new NetUtil().new MyLocationListener();
        locationManager.requestLocationUpdates(provider, 500, 100, listener);

        while (location == null) {
          location = tempLocation;
        }
        locationManager.removeUpdates(listener);
      }
    }
    return location;
  }

  class MyLocationListener implements LocationListener {

    public void onLocationChanged(Location location) {
      // TODO Auto-generated method stub
      tempLocation = location;
    }

    public void onProviderDisabled(String provider) {
      // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
      // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub

    }

  }
}
