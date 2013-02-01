package com.jkydjk.healthier.clock.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.Toast;

import com.jkydjk.healthier.clock.AlarmClock;
import com.jkydjk.healthier.clock.BaseActivity;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.database.WeatherDatabaseHelper;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;

/**
 * 
 * @author Miclle Zheng
 * 
 */
@SuppressLint("SimpleDateFormat")
public class Weather {

  private Integer regionID;
  private Date date;
  private String flag;
  private String flagStart;
  private String flagCodeStart;
  private String flagEnd;
  private String flagCodeEnd;
  private String temperature;
  private String wind;
  private String windPower;
  private String feel;
  private String proposal;
  private String uv;

  public Weather() {
    super();
  }

  public Weather(Integer regionID, Date date, String flag, String flagStart, String flagCodeStart, String flagEnd, String flagCodeEnd, String temperature, String wind, String windPower, String feel,
      String proposal, String uv) {
    super();
    this.regionID = regionID;
    this.date = date;
    this.flag = flag;
    this.flagStart = flagStart;
    this.flagCodeStart = flagCodeStart;
    this.flagEnd = flagEnd;
    this.flagCodeEnd = flagCodeEnd;
    this.temperature = temperature;
    this.wind = wind;
    this.windPower = windPower;
    this.feel = feel;
    this.proposal = proposal;
    this.uv = uv;
  }

  public int getIcon(Context context) {
    return ActivityHelper.getStringResourceID(context, "wealther_icon_" + getFlagCodeStart());
  }

  /**
   * 获取今天天气
   * 
   * @param context
   * @param regionID
   * @return
   */
  public static Weather getToday(Context context, String regionID) {
    Weather weather = null;

    SQLiteDatabase database = new WeatherDatabaseHelper(context).getWritableDatabase();

    try {

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      Calendar calendar = Calendar.getInstance();
      String today = dateFormat.format(calendar.getTime());
      String fields = "region_id, date(date,'localtime'), flag, flag_start, flag_code_start, flag_end, flag_code_end, temperature, wind, wind_power, feel, proposal, uv";
      Cursor cursor = database.rawQuery("select " + fields + " from weathers where region_id = ? and date = ? order by date", new String[] { regionID, today });

      if (cursor != null && cursor.moveToFirst()) {

        weather = new Weather();
        weather.regionID = cursor.getInt(cursor.getColumnIndex("region_id"));
        weather.date = dateFormat.parse(cursor.getString(cursor.getColumnIndex("date(date,'localtime')")));
        weather.flag = cursor.getString(cursor.getColumnIndex("flag"));
        weather.flagStart = cursor.getString(cursor.getColumnIndex("flag_start"));
        weather.flagCodeStart = cursor.getString(cursor.getColumnIndex("flag_code_start"));
        weather.flagEnd = cursor.getString(cursor.getColumnIndex("flag_end"));
        weather.flagCodeEnd = cursor.getString(cursor.getColumnIndex("flag_code_end"));
        weather.temperature = cursor.getString(cursor.getColumnIndex("temperature"));
        weather.wind = cursor.getString(cursor.getColumnIndex("wind"));
        weather.windPower = cursor.getString(cursor.getColumnIndex("wind_power"));
        weather.feel = cursor.getString(cursor.getColumnIndex("feel"));
        weather.proposal = cursor.getString(cursor.getColumnIndex("proposal"));
        weather.uv = cursor.getString(cursor.getColumnIndex("uv"));

        cursor.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    database.close();

    return weather;
  }

  /**
   * 获取从今天开始的天气
   * 
   * @param context
   * @param regionID
   * @return
   */
  public static List<Weather> getWeathers(Context context, String regionID) {
    List<Weather> weathers = new ArrayList<Weather>();
    SQLiteDatabase database = new WeatherDatabaseHelper(context).getWritableDatabase();

    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Calendar calendar = Calendar.getInstance();
      String today = dateFormat.format(calendar.getTime());
      String fields = "region_id, date(date,'localtime'), flag, flag_start, flag_code_start, flag_end, flag_code_end, temperature, wind, wind_power, feel, proposal, uv";
      Cursor cursor = database.rawQuery("select " + fields + " from weathers where region_id = ? and date >= ? order by date", new String[] { regionID, today });

      if (cursor != null && cursor.moveToFirst()) {
        do {
          Weather weather = new Weather();
          weather.regionID = cursor.getInt(cursor.getColumnIndex("region_id"));
          weather.date = dateFormat.parse(cursor.getString(cursor.getColumnIndex("date(date,'localtime')")));
          weather.flag = cursor.getString(cursor.getColumnIndex("flag"));
          weather.flagStart = cursor.getString(cursor.getColumnIndex("flag_start"));
          weather.flagCodeStart = cursor.getString(cursor.getColumnIndex("flag_code_start"));
          weather.flagEnd = cursor.getString(cursor.getColumnIndex("flag_end"));
          weather.flagCodeEnd = cursor.getString(cursor.getColumnIndex("flag_code_end"));
          weather.temperature = cursor.getString(cursor.getColumnIndex("temperature"));
          weather.wind = cursor.getString(cursor.getColumnIndex("wind"));
          weather.windPower = cursor.getString(cursor.getColumnIndex("wind_power"));
          weather.feel = cursor.getString(cursor.getColumnIndex("feel"));
          weather.proposal = cursor.getString(cursor.getColumnIndex("proposal"));
          weather.uv = cursor.getString(cursor.getColumnIndex("uv"));

          weathers.add(weather);
        } while (cursor.moveToNext());
        cursor.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    database.close();
    return weathers;

  }

  /**
   * 更新天气信息
   * 
   * @param context
   * @param regionID
   * @return
   */
  public static boolean update(Context context, String regionID) {

    SQLiteDatabase database = new WeatherDatabaseHelper(context).getReadableDatabase();

    HttpClientManager connect = new HttpClientManager(context, RequestRoute.REQUEST_PATH + RequestRoute.WEATHER);

    connect.addParam("region_id", regionID);

    try {
      database.beginTransaction();
      connect.execute(ResuestMethod.GET);
      JSONArray weathers = new JSONObject(connect.getResponse()).getJSONArray("weathers");

      database.delete("weathers", null, null);

      for (int i = 0; i < weathers.length(); i++) {
        JSONObject weather = (JSONObject) weathers.get(i);
        ContentValues cvs = new ContentValues();
        cvs.put("region_id", weather.getInt("region_id"));
        cvs.put("date", weather.getString("date"));
        cvs.put("flag", weather.getString("flag"));
        cvs.put("flag_start", weather.getString("flag_start"));
        cvs.put("flag_code_start", weather.getString("flag_code_start"));
        cvs.put("flag_end", weather.getString("flag_end"));
        cvs.put("flag_code_end", weather.getString("flag_code_end"));
        cvs.put("temperature", weather.getString("temperature"));
        cvs.put("wind", weather.getString("wind"));
        cvs.put("wind_power", weather.getString("wind_power"));

        if (!weather.isNull("feel"))
          cvs.put("feel", weather.getString("feel"));

        if (!weather.isNull("proposal"))
          cvs.put("proposal", weather.getString("proposal"));

        if (!weather.isNull("uv"))
          cvs.put("uv", weather.getString("uv"));

        database.insert("weathers", null, cvs);
      }
      database.setTransactionSuccessful();
      database.endTransaction();
      database.close();
      return true;

    } catch (Exception e) {
      Log.v("Update failed!");
      database.endTransaction();
      database.close();
      return false;
    }
  }

  /**
   * 今天是否更新过天气数据
   * 
   * @param context
   * @return
   */
  public static boolean todayIsUpdated(Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences("configure", Context.MODE_PRIVATE);
    Time time = new Time();
    time.setToNow();
    return sharedPreferences.getInt("weather_update_day", -1) == time.yearDay ? true : false;
  }

  /**
   * 
   * @author miclle
   * 
   */
  public static class Task extends AsyncTask<String, Integer, String> {

    Context context;

    SharedPreferences sharedPreferences;

    boolean force = false;

    boolean updateSuccess = false;

    Callback callback;

    public List<Weather> weathers = null;

    public Task(Context context) {
      super();
      this.context = context;
      sharedPreferences = context.getSharedPreferences("configure", Context.MODE_PRIVATE);
    }

    public void setForceUpdate(boolean force) {
      this.force = force;
    }

    public boolean getForceUpdate() {
      return force;
    }

    public boolean getUpdateSuccess() {
      return updateSuccess;
    }

    public void setCallback(Callback callback) {
      this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      callback.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

      String regionID = params[0];

      if (force) {
        updateSuccess = Weather.update(context, regionID);
        setWeatherUpdateVersion();
      }

      weathers = Weather.getWeathers(context, regionID);

      if (weathers.size() == 0 && Weather.update(context, regionID)) {
        weathers = Weather.getWeathers(context, regionID);
        updateSuccess = true;
        setWeatherUpdateVersion();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      callback.onPostExecute(this, result);
    }

    private void setWeatherUpdateVersion() {
      Editor editor = sharedPreferences.edit();
      Time time = new Time();
      time.setToNow();
      editor.putInt("weather_update_day", time.yearDay);
      editor.commit();
    }
  }

  /**
   * Task Callback
   * 
   * @author miclle
   * 
   */
  public interface Callback {
    public void onPreExecute();

    public void onPostExecute(Task task, String result);
  }

  public Integer getRegionID() {
    return regionID;
  }

  public void setRegionID(Integer regionID) {
    this.regionID = regionID;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public String getFlagStart() {
    return flagStart;
  }

  public void setFlagStart(String flagStart) {
    this.flagStart = flagStart;
  }

  public String getFlagCodeStart() {
    return flagCodeStart;
  }

  public void setFlagCodeStart(String flagCodeStart) {
    this.flagCodeStart = flagCodeStart;
  }

  public String getFlagEnd() {
    return flagEnd;
  }

  public void setFlagEnd(String flagEnd) {
    this.flagEnd = flagEnd;
  }

  public String getFlagCodeEnd() {
    return flagCodeEnd;
  }

  public void setFlagCodeEnd(String flagCodeEnd) {
    this.flagCodeEnd = flagCodeEnd;
  }

  public String getTemperature() {
    return temperature;
  }

  public void setTemperature(String temperature) {
    this.temperature = temperature;
  }

  public String getWind() {
    return wind;
  }

  public void setWind(String wind) {
    this.wind = wind;
  }

  public String getWindPower() {
    return windPower;
  }

  public void setWindPower(String windPower) {
    this.windPower = windPower;
  }

  public String getFeel() {
    return feel;
  }

  public void setFeel(String feel) {
    this.feel = feel;
  }

  public String getProposal() {
    return proposal;
  }

  public void setProposal(String proposal) {
    this.proposal = proposal;
  }

  public String getUv() {
    return uv;
  }

  public void setUv(String uv) {
    this.uv = uv;
  }

}
