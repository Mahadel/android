package com.github.bkhezry.learn2learn.util;

import android.app.Application;

public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    AppUtil.updateResources(getBaseContext(), "fa");
  }
}
