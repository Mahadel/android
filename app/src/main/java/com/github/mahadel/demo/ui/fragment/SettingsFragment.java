package com.github.mahadel.demo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.ui.activity.MainActivity;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.LocaleManager;
import com.github.mahadel.demo.util.MyApplication;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SettingsFragment change language & theme of application
 */
public class SettingsFragment extends DialogFragment {
  @BindView(R.id.persian_image_view)
  AppCompatImageView persianImageView;
  @BindView(R.id.english_image_view)
  AppCompatImageView englishImageView;
  private Activity activity;
  private Prefser prefser;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
    ButterKnife.bind(this, rootView);
    initVariables();
    setUpLocale();
    return rootView;
  }

  /**
   * Setup init values of variables
   */
  private void initVariables() {
    activity = getActivity();
    prefser = new Prefser(activity);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    dialog.getWindow().setAttributes(lp);
    return dialog;
  }

  @OnClick(R.id.close_image_view)
  void close() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  /**
   * Handle change language click event
   *
   * @param view {@link View}
   */

  @OnClick({R.id.persian_image_view, R.id.english_image_view})
  void handleLanguage(View view) {
    switch (view.getId()) {
      case R.id.persian_image_view:
        restartApp(LocaleManager.LANGUAGE_PERSIAN);
        break;
      case R.id.english_image_view:
        restartApp(LocaleManager.LANGUAGE_ENGLISH);
        break;
    }
  }

  /**
   * Setup locale layout
   */
  private void setUpLocale() {
    if (MyApplication.localeManager.getLanguage().equals(LocaleManager.LANGUAGE_PERSIAN)) {
      changeLanguagePersian();
    } else {
      changeLanguageEnglish();
    }
  }

  /**
   * Set new locale & restart app
   *
   * @param language String selected language
   */
  private void restartApp(String language) {
    MyApplication.localeManager.setNewLocale(activity, language);
    Intent i = new Intent(activity, MainActivity.class);
    startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    System.exit(0);
  }

  private void changeLanguagePersian() {
    persianImageView.setBackgroundResource(R.drawable.image_border);
    englishImageView.setBackgroundResource(android.R.color.transparent);
  }

  private void changeLanguageEnglish() {
    englishImageView.setBackgroundResource(R.drawable.image_border);
    persianImageView.setBackgroundResource(android.R.color.transparent);
  }

  /**
   * Handle change theme click event
   *
   * @param view View {@link View}
   */
  @OnClick({R.id.dark_theme_button, R.id.light_theme_button})
  void handleThemeClick(View view) {
    switch (view.getId()) {
      case R.id.dark_theme_button:
        prefser.put(Constant.IS_DARK_THEME, true);
        restartActivity();
        break;
      case R.id.light_theme_button:
        prefser.put(Constant.IS_DARK_THEME, false);
        restartActivity();
        break;
    }
  }

  /**
   * Restart app after change theme of it
   */

  private void restartActivity() {
    activity.finish();
    final Intent intent = activity.getIntent();
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    activity.startActivity(intent);
  }
}