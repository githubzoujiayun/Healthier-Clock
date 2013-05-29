package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "solution_comments")
public class SolutionComment {

  @DatabaseField(id = true)
  private String id;

  @DatabaseField(columnName = "effect_feel")
  private int effectFeel;

  @DatabaseField
  private int cost;

  @DatabaseField
  private int convenience;

  @DatabaseField
  private int overall;

  @DatabaseField
  private String content;

  public SolutionComment() {
    super();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getEffectFeel() {
    return effectFeel;
  }

  public void setEffectFeel(int effectFeel) {
    this.effectFeel = effectFeel;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public int getConvenience() {
    return convenience;
  }

  public void setConvenience(int convenience) {
    this.convenience = convenience;
  }

  public int getOverall() {
    return overall;
  }

  public void setOverall(int overall) {
    this.overall = overall;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
