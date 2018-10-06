package com.github.bkhezry.learn2learn.model;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

  @SerializedName("gender")
  private int gender;

  @SerializedName("last_name")
  private String lastName;

  @SerializedName("first_name")
  private String firstName;

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
}