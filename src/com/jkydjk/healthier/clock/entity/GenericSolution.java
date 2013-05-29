package com.jkydjk.healthier.clock.entity;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.jkydjk.healthier.clock.database.AlarmDatabaseHelper;
import com.jkydjk.healthier.clock.entity.columns.AlarmColumns;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

/**
 * Created by miclle on 13-5-23.
 */
@DatabaseTable(tableName = "GenericSolution")
public class GenericSolution {

  public static class Type{
    public static final String MASSAGE_SOLUTION = "massage_solution";
    public static final String MOXIBUSTION_SOLUTION = "moxibustion_solution";
    public static final String CUPPING_SOLUTION = "cupping_solution";
    public static final String SKIN_SCRAPING_SOLUTION = "skin_scraping_solution";
    public static final String RECIPE = "recipe";
  }

  @DatabaseField(id = true)
  private String id;

  @DatabaseField
  private String type;

  @DatabaseField
  private int typeId;

  @DatabaseField
  private String title;

  @DatabaseField
  private String intro;

  @DatabaseField
  private String largeImage;

  @DatabaseField
  private String listImage;

  @DatabaseField
  private String data;

  @DatabaseField
  private boolean favorited;

  @DatabaseField
  private long version;

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIntro() {
    return intro;
  }

  public void setIntro(String intro) {
    this.intro = intro;
  }

  public String getType() {
    return type;
  }

  public int getTypeId() {
    return typeId;
  }

  public void setTypeId(int typeId) {
    this.typeId = typeId;
  }

  public String getLargeImage() {
    return largeImage;
  }

  public void setLargeImage(String largeImage) {
    this.largeImage = largeImage;
  }

  public String getListImage() {
    return listImage;
  }

  public void setListImage(String listImage) {
    this.listImage = listImage;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
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

  public static GenericSolution parseJsonObject(JSONObject solutionJSON) throws JSONException {
    GenericSolution solution = new GenericSolution();

    solution.id = solutionJSON.getString("type") + "-" + solutionJSON.getInt("id");
    solution.type = solutionJSON.getString("type");
    solution.typeId = solutionJSON.getInt("id");
    solution.title = solutionJSON.getString("title");
    solution.intro = solutionJSON.getString("intro");
    solution.listImage = solutionJSON.getString("list_image");
    solution.largeImage = solutionJSON.getString("large_image");
    solution.data = solutionJSON.toString();
    solution.version = solutionJSON.getInt("version");

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

}
