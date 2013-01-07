package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "meridians")
public class Meridian {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String name;

  @DatabaseField
  private String subtitle;

  @DatabaseField
  private int category; // 经络分类

  @DatabaseField(columnName = "category_tradition")
  private int categoryTradition; // 经络分类 阴阳

  @DatabaseField(columnName = "acupoints_description")
  private String acupointsDescription; // 本经穴描述

  @DatabaseField
  private String definition; // 经脉循行

  @DatabaseField
  private String indications; // 主治病候

  @DatabaseField
  private String law; // 气血运行规律

  @DatabaseField
  private String proverb; // 歌诀

  @DatabaseField(columnName = "health_tips")
  private String healthTips; // 养生要诀

  @DatabaseField
  private String description;

  public Meridian() {
    super();
    // TODO Auto-generated constructor stub
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public int getCategoryTradition() {
    return categoryTradition;
  }

  public void setCategoryTradition(int categoryTradition) {
    this.categoryTradition = categoryTradition;
  }

  public String getAcupointsDescription() {
    return acupointsDescription;
  }

  public void setAcupointsDescription(String acupointsDescription) {
    this.acupointsDescription = acupointsDescription;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public String getIndications() {
    return indications;
  }

  public void setIndications(String indications) {
    this.indications = indications;
  }

  public String getLaw() {
    return law;
  }

  public void setLaw(String law) {
    this.law = law;
  }

  public String getProverb() {
    return proverb;
  }

  public void setProverb(String proverb) {
    this.proverb = proverb;
  }

  public String getHealthTips() {
    return healthTips;
  }

  public void setHealthTips(String healthTips) {
    this.healthTips = healthTips;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
