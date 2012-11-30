package com.jkydjk.healthier.clock.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.jkydjk.healthier.clock.util.Log;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class User implements Serializable {

  private static final long serialVersionUID = 4918606834770319507L;

  public String token;
  public String username;
  public String email;

  public String realname;
  public String constitution;
  public String birthday;
  public String gender;

  public Integer province;
  public Integer city;
  public Integer district;

  public User() {
    super();
  }

  public User(String token, String username, String email, String realname, String constitution, String birthday, String gender, Integer province, Integer city, Integer district) {
    super();
    this.token = token;
    this.username = username;
    this.email = email;
    this.realname = realname;
    this.constitution = constitution;
    this.birthday = birthday;
    this.gender = gender;
    this.province = province;
    this.city = city;
    this.district = district;
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

  public String getRealname() {
    return realname;
  }

  public void setRealname(String realname) {
    this.realname = realname;
  }

  public String getConstitution() {
    return constitution;
  }

  public void setConstitution(String constitution) {
    this.constitution = constitution;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public Integer getProvince() {
    return province;
  }

  public void setProvince(Integer province) {
    this.province = province;
  }

  public Integer getCity() {
    return city;
  }

  public void setCity(Integer city) {
    this.city = city;
  }

  public Integer getDistrict() {
    return district;
  }

  public void setDistrict(Integer district) {
    this.district = district;
  }

  public static User parse(JSONObject json) {
    User user = new User();
    user.token = json.isNull("token") ? null : json.optString("token");
    user.email = json.isNull("email") ? null : json.optString("email");
    user.username = json.isNull("username") ? null : json.optString("username");
    user.realname = json.isNull("realname") ? null : json.optString("realname");

    user.constitution = json.isNull("constitution") ? null : json.optString("constitution");

    user.birthday = json.isNull("birthday") ? null : json.optString("birthday");
    user.gender = json.isNull("gender") ? null : json.optString("gender");

    user.province = json.isNull("province") ? null : json.optInt("province");
    user.city = json.isNull("city") ? null : json.optInt("city");
    user.district = json.isNull("district") ? null : json.optInt("district");

    return user;
  }

  public static void serializable(SharedPreferences sharedPreference, User user) {
    Editor edit = sharedPreference.edit();

    edit.putString("token", user.token);
    edit.putString("username", user.username);
    edit.putString("email", user.email);
    edit.putString("realname", user.realname);
    edit.putString("constitution", user.constitution);
    edit.putString("birthday", user.birthday);
    edit.putString("gender", user.gender);

    if (user.province != null)
      edit.putInt("province", user.province);

    if (user.city != null)
      edit.putInt("city", user.city);

    if (user.district != null)
      edit.putInt("district", user.district);

    edit.commit();
  }

}
