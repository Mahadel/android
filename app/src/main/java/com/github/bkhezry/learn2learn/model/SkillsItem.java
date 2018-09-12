package com.github.bkhezry.learn2learn.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class SkillsItem {

  @SerializedName("fa_name")
  private String faName;

  @SerializedName("category_uuid")
  private String categoryUuid;

  @SerializedName("en_name")
  private String enName;

  @Id
  @SerializedName("id")
  private Long id;

  @SerializedName("uuid")
  private String uuid;

  public void setFaName(String faName) {
    this.faName = faName;
  }

  public String getFaName() {
    return faName;
  }

  public String getCategoryUuid() {
    return categoryUuid;
  }

  public void setCategoryUuid(String categoryUuid) {
    this.categoryUuid = categoryUuid;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public String getEnName() {
    return enName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }
}