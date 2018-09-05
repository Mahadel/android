package com.github.bkhezry.learn2learn;

import android.content.Context;

import com.github.bkhezry.learn2learn.util.AppUtil;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(AppUtil.updateResources(base));
  }
}
