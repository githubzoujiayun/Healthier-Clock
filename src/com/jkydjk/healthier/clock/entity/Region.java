package com.jkydjk.healthier.clock.entity;

import java.util.ArrayList;

public class Region {
  
  public final static long DEFAULT_REGION_ID = 310100;
  public final static String DEFAULT_REGION = "上海";

  private Integer id;
  private String type;
  private String name;
  private String zipcode;
  private Integer parentId;
  
  private Region parent;
  private ArrayList<Region> children;
  
  public Region() {
    super();
  }

  public Region(Integer id, String type, String name, String zipcode, Integer parentId) {
    super();
    this.id = id;
    this.type = type;
    this.name = name;
    this.zipcode = zipcode;
    this.parentId = parentId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }
  
}
