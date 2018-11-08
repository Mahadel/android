package com.github.bkhezry.learn2learn.model;

import com.google.gson.annotations.SerializedName;


public class ConnectionRequest {

  @SerializedName("email_to")
  private String emailTo;

  @SerializedName("updated_at")
  private String updatedAt;

  @SerializedName("teach_skill_uuid_from")
  private String teachSkillUuidFrom;

  @SerializedName("learn_skill_uuid_from")
  private String learnSkillUuidFrom;

  @SerializedName("description")
  private String description;

  @SerializedName("created_at")
  private String createdAt;

  @SerializedName("uuid")
  private String uuid;

  @SerializedName("user_uuid_to")
  private String userUuidTo;

  @SerializedName("email_from")
  private String emailFrom;

  @SerializedName("user_uuid_from")
  private String userUuidFrom;

  public String getEmailTo() {
    return emailTo;
  }

  public void setEmailTo(String emailTo) {
    this.emailTo = emailTo;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getTeachSkillUuidFrom() {
    return teachSkillUuidFrom;
  }

  public void setTeachSkillUuidFrom(String teachSkillUuidFrom) {
    this.teachSkillUuidFrom = teachSkillUuidFrom;
  }

  public String getLearnSkillUuidFrom() {
    return learnSkillUuidFrom;
  }

  public void setLearnSkillUuidFrom(String learnSkillUuidFrom) {
    this.learnSkillUuidFrom = learnSkillUuidFrom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUserUuidTo() {
    return userUuidTo;
  }

  public void setUserUuidTo(String userUuidTo) {
    this.userUuidTo = userUuidTo;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

  public String getUserUuidFrom() {
    return userUuidFrom;
  }

  public void setUserUuidFrom(String userUuidFrom) {
    this.userUuidFrom = userUuidFrom;
  }

}