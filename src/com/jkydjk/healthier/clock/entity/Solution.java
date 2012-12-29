package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.jkydjk.healthier.clock.database.SolutionDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Solution {

  private long id;
  private String type;
  private int category;
  private String title;
  private int consuming;
  private int startedAt;
  private int endedAt;
  private int frequency;
  private int times;
  private int cycle;
  private String description;
  private String effect;
  private String principle;
  private String note;
  private int favorited;
  private int alarm;
  private long version;

  public Solution() {
    super();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getConsuming() {
    return consuming;
  }

  public void setConsuming(int consuming) {
    this.consuming = consuming;
  }

  public int getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(int startedAt) {
    this.startedAt = startedAt;
  }

  public int getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(int endedAt) {
    this.endedAt = endedAt;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public int getCycle() {
    return cycle;
  }

  public void setCycle(int cycle) {
    this.cycle = cycle;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEffect() {
    return effect;
  }

  public void setEffect(String effect) {
    this.effect = effect;
  }

  public String getPrinciple() {
    return principle;
  }

  public void setPrinciple(String principle) {
    this.principle = principle;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public int getFavorited() {
    return favorited;
  }

  public void setFavorited(int favorited) {
    this.favorited = favorited;
  }

  public int getAlarm() {
    return alarm;
  }

  public void setAlarm(int alarm) {
    this.alarm = alarm;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public static Solution getSolution(Context context, String ableOnType, long ableOnID) {

    SQLiteDatabase database = new SolutionDatabaseHelper(context).getWritableDatabase();

    Cursor cursor = database.rawQuery("select * from solutions where ableon_type = ? and ableon_id = ?", new String[] { ableOnType, ableOnID + "" });

    if (cursor != null && cursor.moveToFirst()) {

      Solution solution = new Solution();

      solution.id = cursor.getLong(cursor.getColumnIndex("solution_id"));
      solution.title = cursor.getString(cursor.getColumnIndex("title"));
      solution.type = cursor.getString(cursor.getColumnIndex("type"));
      solution.category = cursor.getInt(cursor.getColumnIndex("category"));
      solution.consuming = cursor.getInt(cursor.getColumnIndex("consuming"));
      solution.startedAt = cursor.getInt(cursor.getColumnIndex("started_at"));
      solution.endedAt = cursor.getInt(cursor.getColumnIndex("ended_at"));
      solution.frequency = cursor.getInt(cursor.getColumnIndex("frequency"));
      solution.times = cursor.getInt(cursor.getColumnIndex("times"));
      solution.cycle = cursor.getInt(cursor.getColumnIndex("cycle"));
      solution.description = cursor.getString(cursor.getColumnIndex("description"));
      solution.effect = cursor.getString(cursor.getColumnIndex("effect"));
      solution.principle = cursor.getString(cursor.getColumnIndex("principle"));
      solution.note = cursor.getString(cursor.getColumnIndex("note"));
      solution.favorited = cursor.getInt(cursor.getColumnIndex("favorited"));
      solution.alarm = cursor.getInt(cursor.getColumnIndex("alarm"));
      solution.version = cursor.getLong(cursor.getColumnIndex("version"));
      
      database.close();
      return solution;
    }
    
    database.close();

    return null;
  }

  /**
   * 
   * @param ableOnType
   * @param ableOnID
   * @param solutionJSON
   * @return
   * @throws JSONException
   */
  public static ContentValues jsonObjectToContentValues(String ableOnType, long ableOnID, JSONObject solutionJSON) throws JSONException {

    ContentValues cvs = new ContentValues();

    cvs.put("ableon_type", ableOnType);
    cvs.put("ableon_id", ableOnID);

    cvs.put("solution_id", solutionJSON.getInt("id"));

    cvs.put("title", solutionJSON.getString("title"));

    cvs.put("type", solutionJSON.getString("type"));

    if (!solutionJSON.isNull("category"))
      cvs.put("category", solutionJSON.getInt("category"));

    if (!solutionJSON.isNull("consuming"))
      cvs.put("consuming", solutionJSON.getInt("consuming"));

    if (!solutionJSON.isNull("started_at"))
      cvs.put("started_at", solutionJSON.getInt("started_at"));

    if (!solutionJSON.isNull("ended_at"))
      cvs.put("ended_at", solutionJSON.getInt("ended_at"));

    if (!solutionJSON.isNull("frequency"))
      cvs.put("frequency", solutionJSON.getInt("frequency"));

    if (!solutionJSON.isNull("times"))
      cvs.put("times", solutionJSON.getInt("times"));

    if (!solutionJSON.isNull("cycle"))
      cvs.put("cycle", solutionJSON.getInt("cycle"));

    if (!solutionJSON.isNull("description"))
      cvs.put("description", solutionJSON.getString("description"));

    if (!solutionJSON.isNull("effect"))
      cvs.put("effect", solutionJSON.getString("effect"));

    if (!solutionJSON.isNull("principle"))
      cvs.put("principle", solutionJSON.getString("principle"));

    if (!solutionJSON.isNull("note"))
      cvs.put("note", solutionJSON.getString("note"));

    cvs.put("version", solutionJSON.getInt("version"));

    return cvs;
  }

}
