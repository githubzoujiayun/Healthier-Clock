package com.jkydjk.healthier.clock.entity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;

import com.jkydjk.healthier.clock.database.WeatherDatabaseHelper;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.Log;

/**
 * 
 * @author Miclle Zheng
 * 
 */
public class Weather {

  private Integer regionID;
  private Date date;
  private String flag;
  private String flagStart;
  private String flagEnd;
  private String temperature;
  private String wind;
  private String windPower;
  private String feel;
  private String proposal;
  private String uv;

  public Weather() {
    super();
  }

  public Weather(Integer regionID, Date date, String flag, String flagStart, String flagEnd, String temperature, String wind, String windPower, String feel, String proposal, String uv) {
    super();
    this.regionID = regionID;
    this.date = date;
    this.flag = flag;
    this.flagStart = flagStart;
    this.flagEnd = flagEnd;
    this.temperature = temperature;
    this.wind = wind;
    this.windPower = windPower;
    this.feel = feel;
    this.proposal = proposal;
    this.uv = uv;
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

  public String getFlagEnd() {
    return flagEnd;
  }

  public void setFlagEnd(String flagEnd) {
    this.flagEnd = flagEnd;
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

  public static class Task extends AsyncTask<String, Integer, String> {

    Context context;

    TaskCallback callback;

    public Task(Context context) {
      super();
      this.context = context;
    }

    public void setCallback(TaskCallback callback) {
      this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

      String regionID = params[0];

      HttpClientManager connect = new HttpClientManager(context, HttpClientManager.REQUEST_PATH + RequestRoute.WEATHER);

      connect.addParam("region_id", regionID);

      try {
        connect.execute(ResuestMethod.GET);

        String result = connect.getResponse();

        JSONObject json = new JSONObject(result);

        JSONArray weathers = json.getJSONArray("weathers");

        WeatherDatabaseHelper databaseHelp = new WeatherDatabaseHelper(context);
        SQLiteDatabase database = databaseHelp.getReadableDatabase();
        database.beginTransaction();

        database.delete("weathers", null, null);

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.DAY_OF_YEAR, -1);
        
        String insertMe = "INSERT INTO alarms " + "(label, hour, minutes, daysofweek, alarmtime, enabled, vibrate, alert, remark) " + "VALUES ";
        
        try {
          for (int i = 0; i < weathers.length(); i++) {

            // calendar.add(Calendar.DAY_OF_YEAR, 1);
            // String date = dateFormat.format(calendar.getTime());
            // Log.v("date: " + date);

            JSONObject weather = (JSONObject) weathers.get(i);

            int region_id      = weather.getInt("region_id");
            String date        = weather.getString("date");
            String flag        = weather.getString("flag");
            String flag_start  = weather.getString("flag_start");
            String flag_end    = weather.getString("flag_end");
            String temperature = weather.getString("temperature");
            String wind        = weather.getString("wind");
            String wind_power  = weather.getString("wind_power");
            String feel        = weather.getString("feel");
            String proposal    = weather.getString("proposal");
            String uv          = weather.getString("uv");
            
            database.execSQL(insertMe + "('闹铃', 7, 0, 127, 0, 0, 1, '', '');");
            
            // ContentValues cvs = new ContentValues();
            // cvs.put("region_id", regionID);

            // database.insert("weathers", null, cvs);

          }
          database.setTransactionSuccessful();

        } catch (Exception e) {
          Log.v("Database insert error!");
        } finally {
          database.endTransaction();
          database.close();
        }

      } catch (Exception e) {
        return "网络访问异常";
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      callback.onPostExecute(result);
    }
  }

  public interface TaskCallback {
    public void onPreExecute();

    public void onPostExecute(String result);
  }

}
