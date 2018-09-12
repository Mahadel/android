package com.github.bkhezry.learn2learn.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Category {
  @SerializedName("uuid")
  private String uuid;

  @SerializedName("fa_name")
  private String faName;

  @SerializedName("skills")
  private List<SkillsItem> skills;

  @SerializedName("en_name")
  private String enName;

  @Id
  private Long id;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getFaName() {
    return faName;
  }

  public void setFaName(String faName) {
    this.faName = faName;
  }

  public List<SkillsItem> getSkills() {
    return skills;
  }

  public void setSkills(List<SkillsItem> skills) {
    this.skills = skills;
  }

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}