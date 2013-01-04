package com.jkydjk.healthier.clock.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "acupoints_solution_steps")
public class AcupointSolutionStep {

  public final static String ACUPOINT_ID_FIELD_NAME = "acupoint_id";
  public final static String SOLUTION_STEP_ID_FIELD_NAME = "solution_step_id";

  @DatabaseField(generatedId = true)
  int id;

  @DatabaseField(foreign = true, columnName = ACUPOINT_ID_FIELD_NAME)
  Acupoint acupoint;

  @DatabaseField(foreign = true, columnName = SOLUTION_STEP_ID_FIELD_NAME)
  SolutionStep solutionStep;

  public AcupointSolutionStep() {
    super();
  }

  public AcupointSolutionStep(Acupoint acupoint, SolutionStep solutionStep) {
    super();
    this.acupoint = acupoint;
    this.solutionStep = solutionStep;
  }

}
