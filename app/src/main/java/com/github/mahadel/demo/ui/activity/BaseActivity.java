package com.github.mahadel.demo.ui.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.MyApplication;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * BaseActivity handle theme and custom font for all activity
 * Other activity should be extend it
 */
public class BaseActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    //Get type of theme from shared preferences
    Prefser prefser = new Prefser(this);
    if (prefser.get(Constant.IS_DARK_THEME, Boolean.class, true)) {
      setTheme(R.style.BaseAppTheme_Dark);
    } else {
      setTheme(R.style.BaseAppTheme);
    }
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void attachBaseContext(Context base) {
    Context newContext = MyApplication.localeManager.setLocale(base);
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newContext));
  }
}
