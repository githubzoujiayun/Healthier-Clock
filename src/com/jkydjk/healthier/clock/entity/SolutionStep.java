package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "steps")
public class SolutionStep {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField(foreign = true, canBeNull = false)
  private Solution solution;

  @DatabaseField
  private int no;

  @DatabaseField
  private String content;

  public SolutionStep() {
    super();
  }

  /**
   * 
   * @param stepJSON
   * @return
   * @throws JSONException
   */
  public static SolutionStep parseJsonObject(JSONObject stepJSON) throws JSONException {

    SolutionStep step = new SolutionStep();

    step.id = stepJSON.getInt("id");

    if (!stepJSON.isNull("no"))
      step.no = stepJSON.getInt("no");

    step.content = stepJSON.getString("content");

    return step;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Solution getSolution() {
    return solution;
  }

  public void setSolution(Solution solution) {
    this.solution = solution;
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
