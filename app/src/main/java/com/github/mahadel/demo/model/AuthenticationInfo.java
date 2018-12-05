package com.github.mahadel.demo.model;


import com.google.gson.annotations.SerializedName;


public class AuthenticationInfo {

  @SerializedName("message")
  private String message;

  @SerializedName("type")
  private String type;

  @SerializedName("uuid")
  private String uuid;

  @SerializedName("token")
  private String token;

  private String email;

  @SerializedName("fill_info")
  private boolean fillInfo;

  private String firebaseId;

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean getFillInfo() {
    return fillInfo;
  }

  public void setFillInfo(boolean fillInfo) {
    this.fillInfo = fillInfo;
  }

  public String getFirebaseId() {
    return firebaseId;
  }

  public void setFirebaseId(String firebaseId) {
    this.firebaseId = firebaseId;
  }
}