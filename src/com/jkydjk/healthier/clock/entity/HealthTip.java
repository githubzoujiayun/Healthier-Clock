package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by miclle on 13-6-6.
 */
@DatabaseTable(tableName = "HealthTip")
public class HealthTip{

  @DatabaseField(id = true)
  public int id;

  @DatabaseField
  public String content;

}
