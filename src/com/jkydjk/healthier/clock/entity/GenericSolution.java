package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miclle on 13-5-23.
 */
@DatabaseTable(tableName = "GenericSolution")
public class GenericSolution {

  public static class Type{
    public static final String RECIPE = "recipe";
  }

  @DatabaseField(id = true)
  private String id;

  @DatabaseField
  private String type;

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
    solution.title = solutionJSON.getString("title");
    solution.intro = solutionJSON.getString("intro");
    solution.listImage = solutionJSON.getString("list_image");
    solution.largeImage = solutionJSON.getString("large_image");
    solution.data = solutionJSON.toString();
    solution.version = solutionJSON.getInt("version");

    return solution;
  }
}
