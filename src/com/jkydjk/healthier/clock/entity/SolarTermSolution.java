package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 节气方案
 * 
 * @author miclle
 * 
 */
@DatabaseTable(tableName = "solar_term_solutions")
public class SolarTermSolution implements BaseSolution {

  @DatabaseField(generatedId = true)
  private int id;

  @DatabaseField(columnName = "solution_id", index = true)
  private int solutionId;

  @DatabaseField(columnName = "solar_term_index")
  private int solarTermIndex;

  @DatabaseField
  private String title;

  @DatabaseField
  private String effect;

  public SolarTermSolution() {
    super();
  }

  public static SolarTermSolution parseJsonObject(JSONObject solutionJSON) throws JSONException {
    SolarTermSolution solution = new SolarTermSolution();

    if (!solutionJSON.isNull("id"))
      solution.solutionId = solutionJSON.getInt("id");

    solution.title = solutionJSON.getString("title");
    solution.effect = solutionJSON.getString("effect");

    return solution;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSolutionId() {
    return solutionId;
  }

  public void setSolutionId(int solutionId) {
    this.solutionId = solutionId;
  }

  public int getSolarTermIndex() {
    return solarTermIndex;
  }

  public void setSolarTermIndex(int solarTermIndex) {
    this.solarTermIndex = solarTermIndex;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getEffect() {
    return effect;
  }

  public void setEffect(String effect) {
    this.effect = effect;
  }

}
