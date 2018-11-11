package com.github.bkhezry.learn2learn.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ConnectionRequest {

  @SerializedName("connection_receive")
  private List<ConnectionReceiveItem> connectionReceive;

  @SerializedName("connection_send")
  private List<ConnectionSendItem> connectionSend;

  public void setConnectionReceive(List<ConnectionReceiveItem> connectionReceive) {
    this.connectionReceive = connectionReceive;
  }

  public List<ConnectionReceiveItem> getConnectionReceive() {
    return connectionReceive;
  }

  public void setConnectionSend(List<ConnectionSendItem> connectionSend) {
    this.connectionSend = connectionSend;
  }

  public List<ConnectionSendItem> getConnectionSend() {
    return connectionSend;
  }

}