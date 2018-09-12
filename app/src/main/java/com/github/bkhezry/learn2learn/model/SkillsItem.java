package com.github.bkhezry.learn2learn.model;

import com.google.gson.annotations.SerializedName;


public class SkillsItem {

  @SerializedName("fa_name")
  private String faName;

  @SerializedName("category_id")
  private int categoryId;

  @SerializedName("en_name")
  private String enName;

  @SerializedName("id")
  private int id;

  @SerializedName("uuid")
  private String uuid;

  public void setFaName(String faName) {
    this.faName = faName;
  }

  public String getFaName() {
    return faName;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public String getEnName() {
    return enName;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }
}