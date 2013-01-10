package com.jkydjk.healthier.clock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "solutions")
public class Solution implements BaseSolution{

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String type;

  @DatabaseField
  private int category;

  @DatabaseField
  private String title;

  @DatabaseField
  private int consuming;

  @DatabaseField(columnName = "started_at")
  private int startedAt;

  @DatabaseField(columnName = "ended_at")
  private int endedAt;

  @DatabaseField
  private int frequency;

  @DatabaseField
  private int times;

  @DatabaseField
  private int cycle;

  @DatabaseField
  private String description;

  @DatabaseField
  private String effect;

  @DatabaseField
  private String principle;

  @DatabaseField
  private String note;

  @DatabaseField
  private boolean favorited;

  @DatabaseField
  private boolean alarm;

  @DatabaseField
  private long version;

  @ForeignCollectionField(eager = false)
  private ForeignCollection<SolutionStep> steps;

  public Solution() {
    super();
  }

  /**
   * 从JSONObject中解析Solution
   * 
   * @param solutionJSON
   * @return
   * @throws JSONException
   */
  public static Solution parseJsonObject(JSONObject solutionJSON) throws JSONException {

    Solution solution = new Solution();

    solution.id = solutionJSON.getInt("id");

    solution.title = solutionJSON.getString("title");

    solution.type = solutionJSON.getString("type");

    if (!solutionJSON.isNull("category"))
      solution.category = solutionJSON.getInt("category");

    if (!solutionJSON.isNull("consuming"))
      solution.consuming = solutionJSON.getInt("consuming");

    if (!solutionJSON.isNull("started_at"))
      solution.startedAt = solutionJSON.getInt("started_at");

    if (!solutionJSON.isNull("ended_at"))
      solution.endedAt = solutionJSON.getInt("ended_at");

    if (!solutionJSON.isNull("frequency"))
      solution.frequency = solutionJSON.getInt("frequency");

    if (!solutionJSON.isNull("times"))
      solution.times = solutionJSON.getInt("times");

    if (!solutionJSON.isNull("cycle"))
      solution.cycle = solutionJSON.getInt("cycle");

    solution.description = solutionJSON.getString("description");
    solution.effect = solutionJSON.getString("effect");
    solution.principle = solutionJSON.getString("principle");
    solution.note = solutionJSON.getString("note");

    if (!solutionJSON.isNull("version"))
      solution.version = solutionJSON.getInt("version");

    return solution;
  }
  
  public int getSolutionId(){
    return id;
  }
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getConsuming() {
    return consuming;
  }

  public void setConsuming(int consuming) {
    this.consuming = consuming;
  }

  public int getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(int startedAt) {
    this.startedAt = startedAt;
  }

  public int getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(int endedAt) {
    this.endedAt = endedAt;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public int getCycle() {
    return cycle;
  }

  public void setCycle(int cycle) {
    this.cycle = cycle;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEffect() {
    return effect;
  }

  public void setEffect(String effect) {
    this.effect = effect;
  }

  public String getPrinciple() {
    return principle;
  }

  public void setPrinciple(String principle) {
    this.principle = principle;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public boolean isFavorited() {
    return favorited;
  }

  public void setFavorited(boolean favorited) {
    this.favorited = favorited;
  }

  public boolean isAlarm() {
    return alarm;
  }

  public void setAlarm(boolean alarm) {
    this.alarm = alarm;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public ForeignCollection<SolutionStep> getSteps() {
    return steps;
  }

  public void setSteps(ForeignCollection<SolutionStep> steps) {
    this.steps = steps;
  }

}
