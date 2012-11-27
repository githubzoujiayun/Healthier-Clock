package com.jkydjk.healthier.clock.entity;

public class Question {

  private int id;
  private String title;
  private String type;
  private int label;
  private int score;
  
  public Question() {
    
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getLabel() {
    return label;
  }

  public void setLabel(int label) {
    this.label = label;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }
  
}
