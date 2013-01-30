package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 方案过程管理
 * 
 * @author miclle
 * 
 */
@DatabaseTable(tableName = "solution_processes")
public class SolutionProcess {

  @DatabaseField(id = true)
  public int id;

  // 工具
  @DatabaseField(columnName = "tool_is_comply")
  public Boolean toolIsComply;

  // 选材
  @DatabaseField(columnName = "material_is_comply")
  public Boolean materialIsComply;
  
  // 时间
  @DatabaseField(columnName = "time_is_comply")
  public Boolean timeIsComply;

  // 场合
  @DatabaseField(columnName = "occasion_is_comply")
  public Boolean occasionIsComply;

  @ForeignCollectionField(eager = false)
  public ForeignCollection<SolutionStepProcess> stepProcesses;

  public SolutionProcess() {
    super();
    // NOTE ORMLite need use
  }
  
  public JSONObject toJSON() throws JSONException{
    JSONObject json = new JSONObject();
    
    json.put("id", id);
    json.put("tool_is_comply", toolIsComply);
    json.put("material_is_comply", materialIsComply);
    json.put("time_is_comply", timeIsComply);
    json.put("occasion_is_comply", occasionIsComply);
    
    return json;
  }

}
