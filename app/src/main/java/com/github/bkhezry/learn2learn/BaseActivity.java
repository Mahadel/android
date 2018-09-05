package com.github.bkhezry.learn2learn;

import android.content.Context;

import com.github.bkhezry.learn2learn.util.AppUtil;

import androidx.appcompat.app.AppCompatActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {

  @Override
  protected void attachBaseContext(Context base) {
    Context newContext = AppUtil.updateResources(base);
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newContext));
  }
}
