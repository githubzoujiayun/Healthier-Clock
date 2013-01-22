package com.jkydjk.healthier.clock.entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.jkydjk.healthier.clock.database.AlarmDatabaseHelper;
import com.jkydjk.healthier.clock.entity.columns.AlarmColumns;

@DatabaseTable(tableName = "solutions")
public class Solution implements BaseSolution {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String type;

  @DatabaseField
  private int category;

  @DatabaseField
  private String title;

  @DatabaseField
  private int consuming;

  @DatabaseField(columnName = "started_at")
  private int startedAt;

  @DatabaseField(columnName = "ended_at")
  private int endedAt;

  @DatabaseField
  private int frequency;

  @DatabaseField
  private int times;

  @DatabaseField
  private int cycle;

  @DatabaseField
  private String description;

  @DatabaseField
  private String effect;

  @DatabaseField
  private String principle;

  @DatabaseField
  private String note;

  @DatabaseField
  private boolean favorited;

  @DatabaseField
  private long version;

  @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "hour_ids")
  private Ids hourIds;

  @ForeignCollectionField(eager = false)
  private ForeignCollection<SolutionStep> steps;

  public Solution() {
    super();
  }

  /**
   * 从JSONObject中解析Solution
   * 
   * @param solutionJSON
   * @return
   * @throws JSONException
   */
  public static Solution parseJsonObject(JSONObject solutionJSON) throws JSONException {

    Solution solution = new Solution();

    solution.id = solutionJSON.getInt("id");

    solution.title = solutionJSON.getString("title");

    solution.type = solutionJSON.getString("type");

    if (!solutionJSON.isNull("category"))
      solution.category = solutionJSON.getInt("category");

    if (!solutionJSON.isNull("consuming"))
      solution.consuming = solutionJSON.getInt("consuming");

    if (!solutionJSON.isNull("started_at"))
      solution.startedAt = solutionJSON.getInt("started_at");

    if (!solutionJSON.isNull("ended_at"))
      solution.endedAt = solutionJSON.getInt("ended_at");

    if (!solutionJSON.isNull("frequency"))
      solution.frequency = solutionJSON.getInt("frequency");

    if (!solutionJSON.isNull("times"))
      solution.times = solutionJSON.getInt("times");

    if (!solutionJSON.isNull("cycle"))
      solution.cycle = solutionJSON.getInt("cycle");

    solution.description = solutionJSON.getString("description");
    solution.effect = solutionJSON.getString("effect");
    solution.principle = solutionJSON.getString("principle");
    solution.note = solutionJSON.getString("note");

    if (!solutionJSON.isNull("version"))
      solution.version = solutionJSON.getInt("version");

    JSONArray hoursArray = solutionJSON.getJSONArray("hours");

    Ids hourIds = new Ids();

    for (int i = 0; i < hoursArray.length(); i++) {
      hourIds.add((Integer) hoursArray.get(i));
    }

    solution.hourIds = hourIds;

    return solution;
  }

  /**
   * 返回为方案设置的闹铃提醒
   * 
   * @param context
   * @return
   */
  public Alarm getAlarm(Context context) {
    try {
      Dao<Alarm, Integer> alarmDao = new AlarmDatabaseHelper(context).getAlarmDao();
      QueryBuilder<Alarm, Integer> queryBuilder = alarmDao.queryBuilder();
      queryBuilder.where().eq(AlarmColumns.CATEGORY, Alarm.CATEGORY_SOLUTION).and().eq(AlarmColumns.CATEGORY_ABLE_ID, id);
      PreparedQuery<Alarm> preparedQuery = queryBuilder.prepare();
      return alarmDao.queryForFirst(preparedQuery);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 方案是否设置闹铃提醒
   * 
   * @param context
   * @return
   */
  public boolean isAlarm(Context context) {
    return getAlarm(context) == null ? false : true;
  }

  // get and set
  public int getSolutionId() {
    return id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
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

  public boolean isFavorited() {
    return favorited;
  }

  public void setFavorited(boolean favorited) {
    this.favorited = favorited;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public Ids getHourIds() {
    return hourIds;
  }

  public void setHourIds(Ids hourIds) {
    this.hourIds = hourIds;
  }

  public ForeignCollection<SolutionStep> getSteps() {
    return steps;
  }

  public void setSteps(ForeignCollection<SolutionStep> steps) {
    this.steps = steps;
  }

}
