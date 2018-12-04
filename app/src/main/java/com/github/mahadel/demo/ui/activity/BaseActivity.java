package com.github.mahadel.demo.ui.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.MyApplication;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Prefser prefser = new Prefser(this);
    if (prefser.get(Constant.IS_DARK_THEME, Boolean.class, false)) {
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
