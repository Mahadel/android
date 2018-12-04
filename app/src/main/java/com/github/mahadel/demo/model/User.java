package com.github.mahadel.demo.model;

import com.google.gson.annotations.SerializedName;

public class User extends UserInfo {

  @SerializedName("uuid")
  private String uuid;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
}