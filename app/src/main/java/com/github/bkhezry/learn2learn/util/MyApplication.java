package com.github.bkhezry.learn2learn.util;

import android.app.Application;
import android.content.res.Configuration;

import com.github.bkhezry.learn2learn.R;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {
  private Prefser prefser;

  @Override
  public void onCreate() {
    super.onCreate();
    ViewPump.init(ViewPump.builder()
      .addInterceptor(new CalligraphyInterceptor(
        new CalligraphyConfig.Builder()
          .setDefaultFontPath("fonts/DroidNaskh-Regular.ttf")
          .setFontAttrId(R.attr.fontPath)
          .build()))
      .build());
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
