package com.github.mahadel.demo.util;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.github.mahadel.demo.BuildConfig;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.MyObjectBox;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class MyApplication extends Application {
  private static BoxStore boxStore;
  public static LocaleManager localeManager;

  @Override
  public void onCreate() {
    super.onCreate();
    ViewPump.init(ViewPump.builder()
        .addInterceptor(new CalligraphyInterceptor(
            new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()))
        .build());

    createBoxStore();

  }

  /**
   * Return instance of box store
   *
   * @return Instance of {@link BoxStore}
   */

  public static BoxStore getBoxStore() {
    return boxStore;
  }

  /**
   * Create static {@link BoxStore} instance
   */
  private void createBoxStore() {
    boxStore = MyObjectBox.builder().androidContext(MyApplication.this).build();
    if (BuildConfig.DEBUG) {
      new AndroidObjectBrowser(boxStore).start(this);
    }
  }

  @Override
  protected void attachBaseContext(Context base) {
    localeManager = new LocaleManager(base);
    super.attachBaseContext(localeManager.setLocale(base));
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    localeManager.setLocale(this);
  }
}
