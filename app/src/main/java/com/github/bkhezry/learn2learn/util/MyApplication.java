package com.github.bkhezry.learn2learn.util;

import android.app.Application;
import android.content.res.Configuration;

import com.github.pwittchen.prefser.library.rx2.Prefser;

public class MyApplication extends Application {
  private Prefser prefser;

  @Override
  public void onCreate() {
    super.onCreate();

  }

  private void changeLanguage() {
    prefser = new Prefser(this);
    if (prefser.contains(Constant.LANGUAGE)) {
      AppUtil.updateResources(this);
    } else {
      prefser.put(Constant.LANGUAGE, "fa");
      AppUtil.updateResources(this);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    changeLanguage();
  }
}
