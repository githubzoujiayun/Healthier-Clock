package com.jkydjk.healthier.clock.entity;

import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "acupoints")
public class Acupoint {

  @DatabaseField(id = true)
  private int id;

  @DatabaseField
  private String name; // 名称

  @DatabaseField
  private String subtitle; // 副标题

  @DatabaseField
  private String numbering; // 国际编号

  @DatabaseField
  private String alias; // 别名

  @DatabaseField
  private String pinyin; // 拼音

  @DatabaseField(columnName = "meridian_id", foreign = true, canBeNull = false, foreignAutoCreate = true, foreignAutoRefresh = true)
  private Meridian meridian;// 对应的经络

  @DatabaseField
  private int sort; // 在经络上的排序

  @DatabaseField(columnName = "on_ear")
  private boolean onEar; // 是否为耳穴

  @DatabaseField
  private String nomenclature; // 命名来源;

  @DatabaseField(columnName = "blood_feature")
  private String bloodFeature; // 气血特征;

  @DatabaseField(columnName = "run_law")
  private String runLaw; // 运行规律;

  @DatabaseField
  private String dissection; // 解剖;

  @DatabaseField
  private String position; // 准确定位;

  @DatabaseField(columnName = "locate_skill")
  private String locateSkill; // 取穴技巧;

  @DatabaseField
  private String effect; // 功能作用;

  @DatabaseField
  private String theory; // 治法;

  @DatabaseField
  private String acupucture; // 针刺疗法;

  @DatabaseField
  private String moxibustion; // 艾灸疗法;

  @DatabaseField
  private String massage; // 推拿疗法;

  @DatabaseField
  private String caution; // 注意事项;

  @DatabaseField
  private String literature; // 文学描述;

  public Acupoint() {
    super();
  }
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public String getNumbering() {
    return numbering;
  }

  public void setNumbering(String numbering) {
    this.numbering = numbering;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getPinyin() {
    return pinyin;
  }

  public void setPinyin(String pinyin) {
    this.pinyin = pinyin;
  }

  public Meridian getMeridian() {
    return meridian;
  }

  public void setMeridian(Meridian meridian) {
    this.meridian = meridian;
  }

  public int getSort() {
    return sort;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public boolean isOnEar() {
    return onEar;
  }

  public void setOnEar(boolean onEar) {
    this.onEar = onEar;
  }

  public String getNomenclature() {
    return nomenclature;
  }

  public void setNomenclature(String nomenclature) {
    this.nomenclature = nomenclature;
  }

  public String getBloodFeature() {
    return bloodFeature;
  }

  public void setBloodFeature(String bloodFeature) {
    this.bloodFeature = bloodFeature;
  }

  public String getRunLaw() {
    return runLaw;
  }

  public void setRunLaw(String runLaw) {
    this.runLaw = runLaw;
  }

  public String getDissection() {
    return dissection;
  }

  public void setDissection(String dissection) {
    this.dissection = dissection;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getLocateSkill() {
    return locateSkill;
  }

  public void setLocateSkill(String locateSkill) {
    this.locateSkill = locateSkill;
  }

  public String getEffect() {
    return effect;
  }

  public void setEffect(String effect) {
    this.effect = effect;
  }

  public String getTheory() {
    return theory;
  }

  public void setTheory(String theory) {
    this.theory = theory;
  }

  public String getAcupucture() {
    return acupucture;
  }

  public void setAcupucture(String acupucture) {
    this.acupucture = acupucture;
  }

  public String getMoxibustion() {
    return moxibustion;
  }

  public void setMoxibustion(String moxibustion) {
    this.moxibustion = moxibustion;
  }

  public String getMassage() {
    return massage;
  }

  public void setMassage(String massage) {
    this.massage = massage;
  }

  public String getCaution() {
    return caution;
  }

  public void setCaution(String caution) {
    this.caution = caution;
  }

  public String getLiterature() {
    return literature;
  }

  public void setLiterature(String literature) {
    this.literature = literature;
  }

}
