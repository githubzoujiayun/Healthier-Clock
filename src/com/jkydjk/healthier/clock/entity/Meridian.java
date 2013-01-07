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

}
