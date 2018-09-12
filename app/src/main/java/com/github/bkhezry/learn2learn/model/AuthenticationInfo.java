package com.github.bkhezry.learn2learn.model;


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
}