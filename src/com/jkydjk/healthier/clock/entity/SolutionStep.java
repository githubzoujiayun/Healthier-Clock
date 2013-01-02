package com.jkydjk.healthier.clock.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class SolutionStep {

  private long id;
  private long solutionID;
  private int no;
  private String content;

  public static ArrayList<SolutionStep> steps(long solutionID) {

    return null;
  }

  public static ContentValues jsonObjectToContentValues(JSONObject stepJSON) throws JSONException {

    ContentValues cvs = new ContentValues();

    cvs.put("step_id", stepJSON.getString("id"));
    cvs.put("solution_id", stepJSON.getString("solution_id"));
    cvs.put("no", stepJSON.getInt("no"));
    cvs.put("content", stepJSON.getString("content"));

    return cvs;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getSolutionID() {
    return solutionID;
  }

  public void setSolutionID(long solutionID) {
    this.solutionID = solutionID;
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
