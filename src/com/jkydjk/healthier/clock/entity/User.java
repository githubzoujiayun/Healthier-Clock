package com.jkydjk.healthier.clock.entity;

import java.io.Serializable;

public class User implements Serializable {

  public int id;
  public String username;
  public String email;
  public String password;
  public int gender;
  public int birth_year;
  public int birth_month;
  public int birth_day;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public int getBirth_year() {
    return birth_year;
  }

  public void setBirth_year(int birth_year) {
    this.birth_year = birth_year;
  }

  public int getBirth_month() {
    return birth_month;
  }

  public void setBirth_month(int birth_month) {
    this.birth_month = birth_month;
  }

  public int getBirth_day() {
    return birth_day;
  }

  public void setBirth_day(int birth_day) {
    this.birth_day = birth_day;
  }

  public User() {

  }

  public User(String username, String email, String password, int gender, int birth_year, int birth_month, int birth_day) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.gender = gender;
    this.birth_year = birth_year;
    this.birth_month = birth_month;
    this.birth_day = birth_day;
  }

  public User(String username, String email, String password) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
