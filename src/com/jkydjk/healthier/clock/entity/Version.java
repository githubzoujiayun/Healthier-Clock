package com.jkydjk.healthier.clock.entity;

/**
 * 
 * @author miclle
 *
 */
public class Version {

  public int code;
  public String name;
  public String description;
  public String url;

  public Version() {
    super();
  }

  public Version(int code, String name, String description, String url) {
    super();
    this.code = code;
    this.name = name;
    this.description = description;
    this.url = url;
  }

}
