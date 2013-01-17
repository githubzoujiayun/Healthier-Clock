package com.jkydjk.healthier.clock.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "solution_steps")
public class SolutionStep {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField(columnName = "solution_id", foreign = true, canBeNull = false, foreignAutoCreate = true, foreignAutoRefresh = true)
  private Solution solution;

  @DatabaseField
  private int no;

  @DatabaseField
  private String content;

  @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "acupoint_ids")
  private Ids acupointIds;

  public SolutionStep() {
    super();
  }

  /**
   * 从JSONObject中解析一个SolutionStep对象
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

    JSONArray acupoints = stepJSON.getJSONArray("acupoints");

    step.acupointIds = new Ids();

    for (int i = 0; i < acupoints.length(); i++) {
      Integer acupointId = (Integer) acupoints.get(i);
      step.acupointIds.add(i, acupointId);
    }

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

  public Ids getAcupointIds() {
    return acupointIds;
  }

  public void setAcupointIds(Ids acupointIds) {
    this.acupointIds = acupointIds;
  }

}
