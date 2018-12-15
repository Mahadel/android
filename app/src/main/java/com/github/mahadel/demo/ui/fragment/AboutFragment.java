package com.github.mahadel.demo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.About;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.FirebaseEventLog;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AboutFragment showing {@link About} instance values in the fragment
 */
public class AboutFragment extends DialogFragment {
  private static final String TAG = "AboutFragment";
  @BindView(R.id.version_app_text_view)
  AppCompatTextView versionAppTextView;
  @BindView(R.id.update_button)
  MaterialButton updateButton;
  @BindView(R.id.sponsor_name)
  AppCompatTextView sponsorName;
  @BindView(R.id.sponsor_description_text_view)
  AppCompatTextView sponsorDescriptionTextView;
  private Dialog loadingDialog;
  private Activity activity;
  private About about;
  private String versionName;
  private AuthenticationInfo info;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_about, container, false);
    ButterKnife.bind(this, rootView);
    initVariables();
    setAppVersion();
    getAbout();
    return rootView;
  }

  /**
   * Setup init values of variables
   */
  private void initVariables() {
    activity = getActivity();
    Prefser prefser = new Prefser(activity);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    loadingDialog = AppUtil.getLoadingDialog(activity);
  }

  /**
   * Get app version
   */
  private void setAppVersion() {
    versionName = "";
    try {
      versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    versionAppTextView.setText(versionName);
  }

  /**
   * Get {@link About} instance from server
   */
  private void getAbout() {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<About> call = apiService.getAbout();
    call.enqueue(new Callback<About>() {
      @Override
      public void onResponse(@NonNull Call<About> call, @NonNull Response<About> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          about = response.body();
          handleAbout();
        }
      }

      @Override
      public void onFailure(@NonNull Call<About> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        FirebaseEventLog.log("server_failure", TAG, "getAbout", t.getMessage());
      }
    });
  }

  /**
   * Showing values of {@link About} in the UI
   */
  private void handleAbout() {
    if (!about.getAppVersion().equals(versionName)) {
      updateButton.setVisibility(View.VISIBLE);
    }
    sponsorName.setText(about.getSponsorName());
    sponsorDescriptionTextView.setText(about.getSponsorDescription());

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
   * Handle view click and opening Website in the default browser app
   *
   * @param view {@link View}
   */
  @OnClick({R.id.update_button, R.id.changelog_layout, R.id.license_layout, R.id.sponsor_website, R.id.developer_layout})
  void handleLayoutClicks(View view) {
    if (about != null) {
      switch (view.getId()) {
        case R.id.update_button:
          startBrowser(about.getAppUrl(), view);
          break;
        case R.id.changelog_layout:
          startBrowser(about.getChangelogUrl(), view);
          break;
        case R.id.license_layout:
          startBrowser(about.getLicenseUrl(), view);
          break;
        case R.id.sponsor_website:
          startBrowser(about.getSponsorUrl(), view);
          break;
        case R.id.developer_layout:
          startBrowser(getString(R.string.bkhezry_twitter_url), view);
          break;
      }
    } else {
      AppUtil.showSnackbar(view, getString(R.string.error_request_message), activity, SnackbarUtils.LENGTH_LONG);
      getAbout();
    }
  }

  /**
   * Showing url in the browser
   *
   * @param url  String
   * @param view {@link View}
   */
  private void startBrowser(String url, View view) {
    try {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(browserIntent);
    } catch (ActivityNotFoundException e) {
      AppUtil.showSnackbar(view, getString(R.string.browser_not_found_label), activity, SnackbarUtils.LENGTH_INDEFINITE);
      e.printStackTrace();
    }

  }
}