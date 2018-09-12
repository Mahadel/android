package com.github.bkhezry.learn2learn.util;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.github.bkhezry.learn2learn.BuildConfig;
import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.MyObjectBox;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class MyApplication extends Application {
  private Prefser prefser;
  private BoxStore boxStore;

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

    createBoxStore();

  }

  private void createBoxStore() {
    boxStore = MyObjectBox.builder().androidContext(MyApplication.this).build();
    if (BuildConfig.DEBUG) {
      new AndroidObjectBrowser(boxStore).start(this);
    }

    Log.d("App", "Using ObjectBox " + BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")");
  }

  public BoxStore getBoxStore() {
    return boxStore;
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
