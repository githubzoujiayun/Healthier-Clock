package com.jkydjk.healthier.clock.entity;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jkydjk.healthier.clock.BaseActivity;

public class User implements Serializable {

  private static final long serialVersionUID = 4918606834770319507L;

  public String token;
  public String username;
  public String email;

  public String realname;
  public String constitution;
  public String birthday;
  public String gender;

  public String province;
  public Long provinceID;

  public String city;
  public Long cityID;

  public String district;
  public Long districtID;

  public User() {
    super();
  }

  public User(String token, String username, String email, String realname, String constitution, String birthday, String gender, String province, Long provinceID, String city, Long cityID,
      String district, Long districtID) {
    super();
    this.token = token;
    this.username = username;
    this.email = email;
    this.realname = realname;
    this.constitution = constitution;
    this.birthday = birthday;
    this.gender = gender;
    this.province = province;
    this.provinceID = provinceID;
    this.city = city;
    this.cityID = cityID;
    this.district = district;
    this.districtID = districtID;
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

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public Long getProvinceID() {
    return provinceID;
  }

  public void setProvinceID(Long provinceID) {
    this.provinceID = provinceID;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Long getCityID() {
    return cityID;
  }

  public void setCityID(Long cityID) {
    this.cityID = cityID;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public Long getDistrictID() {
    return districtID;
  }

  public void setDistrictID(Long districtID) {
    this.districtID = districtID;
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

    user.province = json.isNull("province") ? null : json.optString("province");
    user.provinceID = json.isNull("province_id") ? null : json.optLong("province_id");

    user.city = json.isNull("city") ? null : json.optString("city");
    user.cityID = json.isNull("city_id") ? null : json.optLong("city_id");

    user.district = json.isNull("district") ? null : json.optString("district");
    user.districtID = json.isNull("district_id") ? null : json.optLong("district_id");

    return user;
  }

  public static void serializable(Context context, SharedPreferences sharedPreference, User user) {
    Editor editor = sharedPreference.edit();

    editor.putString("token", user.token);
    editor.putString("username", user.username);
    editor.putString("email", user.email);
    editor.putString("realname", user.realname);
    editor.putString("constitution", user.constitution);

    editor.putString("constitution_flag", context.getString(BaseActivity.getStringResourceID(context, "constitution_" + user.constitution)));

    editor.putString("birthday", user.birthday);
    editor.putString("gender", user.gender);

    if (user.province != null)
      editor.putString("province", user.province);

    if (user.provinceID != null)
      editor.putLong("province", user.provinceID);

    if (user.city != null)
      editor.putString("city", user.city);

    if (user.cityID != null)
      editor.putLong("city_id", user.cityID);

    if (user.district != null)
      editor.putString("district", user.district);

    if (user.districtID != null)
      editor.putLong("district", user.districtID);

    editor.commit();
  }

}
