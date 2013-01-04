package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;

public class Acupoint {

  @DatabaseField(id = true)
  private int id;

//  @DatabaseField(foreign = true, canBeNull = false)
//  private Solution solution;

  @DatabaseField
  private String name;
  
  @DatabaseField
  private String alias;
  
  
}
