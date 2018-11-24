package com.github.bkhezry.learn2learn.model;

import com.google.gson.annotations.SerializedName;


public class About {

  @SerializedName("sponsor_description")
  private String sponsorDescription;

  @SerializedName("app_url")
  private String appUrl;

  @SerializedName("app_version")
  private String appVersion;

  @SerializedName("updated_at")
  private String updatedAt;

  @SerializedName("created_at")
  private String createdAt;

  @SerializedName("id")
  private int id;

  @SerializedName("changelog_url")
  private String changelogUrl;

  @SerializedName("sponsor_name")
  private String sponsorName;

  @SerializedName("license_url")
  private String licenseUrl;

  @SerializedName("sponsor_url")
  private String sponsorUrl;

  public String getSponsorDescription() {
    return sponsorDescription;
  }

  public void setSponsorDescription(String sponsorDescription) {
    this.sponsorDescription = sponsorDescription;
  }

  public String getAppUrl() {
    return appUrl;
  }

  public void setAppUrl(String appUrl) {
    this.appUrl = appUrl;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getChangelogUrl() {
    return changelogUrl;
  }

  public void setChangelogUrl(String changelogUrl) {
    this.changelogUrl = changelogUrl;
  }

  public String getSponsorName() {
    return sponsorName;
  }

  public void setSponsorName(String sponsorName) {
    this.sponsorName = sponsorName;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

  public String getSponsorUrl() {
    return sponsorUrl;
  }

  public void setSponsorUrl(String sponsorUrl) {
    this.sponsorUrl = sponsorUrl;
  }
}