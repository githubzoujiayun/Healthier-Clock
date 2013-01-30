package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "solution_step_processes")
public class SolutionStepProcess {

  @DatabaseField(id = true)
  public int id;

  @DatabaseField(columnName = "solution_id", foreign = true, canBeNull = false, foreignAutoCreate = true, foreignAutoRefresh = true)
  public Solution solution;

  @DatabaseField(columnName = "solution_process_id", foreign = true, canBeNull = false, foreignAutoCreate = true, foreignAutoRefresh = true)
  public SolutionProcess solutionProcess;

  @DatabaseField(columnName = "comply")
  public Boolean comply;

  public SolutionStepProcess() {
    super();
  }

  public SolutionStepProcess(int solutionStepId) {
    id = solutionStepId;
  }

  public JSONObject toJSON() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("id", id);
    json.put("solution_id", solution.getId());
    json.put("solution_process_id", solutionProcess.id);
    json.put("comply", comply);
    return json;
  }

}
