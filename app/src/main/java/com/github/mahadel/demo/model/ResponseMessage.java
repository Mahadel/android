package com.github.mahadel.demo.model;

import com.google.gson.annotations.SerializedName;


public class ResponseMessage {

  @SerializedName("message")
  private String message;

  @SerializedName("type")
  private String type;

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

}