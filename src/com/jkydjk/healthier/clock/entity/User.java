package com.jkydjk.healthier.clock.entity;

import java.io.Serializable;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class User implements Serializable {

  private static final long serialVersionUID = 4918606834770319507L;
  
  public int id;
  public String token;
  public String username;
  public String email;
  public int gender;
  public int birthYear;
  public int birthMonth;
  public int birthDay;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
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

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public int getBirthYear() {
    return birthYear;
  }

  public void setBirthYear(int birthYear) {
    this.birthYear = birthYear;
  }

  public int getBirthMonth() {
    return birthMonth;
  }

  public void setBirthMonth(int birthMonth) {
    this.birthMonth = birthMonth;
  }

  public int getBirthDay() {
    return birthDay;
  }

  public void setBirthDay(int birthDay) {
    this.birthDay = birthDay;
  }

  public User() {

  }

  public User(String username, String email, int gender, int birthYear, int birthMonth, int birthDay) {
    super();
    this.username = username;
    this.email = email;
    this.gender = gender;
    this.birthYear = birthYear;
    this.birthMonth = birthMonth;
    this.birthDay = birthDay;
  }

  public static void serializable(SharedPreferences sharedPreference, User user) {
    Editor edit = sharedPreference.edit();
    edit.putString("token", user.getToken());
    edit.putString("username", user.getUsername());
    edit.putString("email", user.getEmail());
    edit.putInt("gender", user.getGender());
    edit.putInt("birthYear", user.getBirthYear());
    edit.putInt("birthMonth", user.getBirthMonth());
    edit.putInt("birthDay", user.getBirthDay());
    edit.commit();
  }

}
