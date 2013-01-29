package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
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
  private int id;

  // 时间
  @DatabaseField(columnName = "time_is_comply")
  private Boolean timeIsComply;

  // 工具
  @DatabaseField(columnName = "tool_is_comply")
  private Boolean toolIsComply;

  // 选材
  @DatabaseField(columnName = "material_is_comply")
  private Boolean materialIsComply;

  // 场合
  @DatabaseField(columnName = "occasion_is_comply")
  private Boolean occasionIsComply;

  public SolutionProcess() {
    super();
    // NOTE ORMLite need use
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Boolean getTimeIsComply() {
    return timeIsComply;
  }

  public void setTimeIsComply(Boolean timeIsComply) {
    this.timeIsComply = timeIsComply;
  }

  public Boolean getToolIsComply() {
    return toolIsComply;
  }

  public void setToolIsComply(Boolean toolIsComply) {
    this.toolIsComply = toolIsComply;
  }

  public Boolean getMaterialIsComply() {
    return materialIsComply;
  }

  public void setMaterialIsComply(Boolean materialIsComply) {
    this.materialIsComply = materialIsComply;
  }

  public Boolean getOccasionIsComply() {
    return occasionIsComply;
  }

  public void setOccasionIsComply(Boolean occasionIsComply) {
    this.occasionIsComply = occasionIsComply;
  }

}
