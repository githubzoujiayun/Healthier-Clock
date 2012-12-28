package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class SolutionStep {

  public static ContentValues jsonObjectToContentValues(JSONObject stepJSON) throws JSONException {
    ContentValues cvs = new ContentValues();

    cvs.put("steps_id", stepJSON.getString("id"));
    cvs.put("solution_id", stepJSON.getString("solution_id"));
    cvs.put("no", stepJSON.getString("no"));
    cvs.put("content", stepJSON.getString("content"));

    return cvs;
  }
}
