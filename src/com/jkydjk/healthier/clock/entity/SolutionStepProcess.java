package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "solution_step_processes")
public class SolutionStepProcess {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField(columnName = "solution_id", foreign = true, canBeNull = false, foreignAutoCreate = true, foreignAutoRefresh = true)
  private Solution solution;

  @DatabaseField(columnName = "comply")
  private Boolean comply;

  public SolutionStepProcess() {
    super();
  }

  public SolutionStepProcess(int solutionStepId) {
    id = solutionStepId;
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

  public Boolean isComply() {
    return comply;
  }

  public void setComply(Boolean comply) {
    this.comply = comply;
  }

}
