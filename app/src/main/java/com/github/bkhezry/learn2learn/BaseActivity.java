package com.github.bkhezry.learn2learn;

import android.content.Context;
import android.os.Bundle;

import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import androidx.appcompat.app.AppCompatActivity;
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
    Context newContext = AppUtil.updateResources(base);
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newContext));
  }
}
