package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {
  private Prefser prefser;
  private Dialog loadingDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    prefser = new Prefser(this);
    loadingDialog = AppUtil.getDialogLoading(this);
  }
}

