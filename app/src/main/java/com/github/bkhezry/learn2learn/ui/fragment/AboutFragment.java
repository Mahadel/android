package com.github.bkhezry.learn2learn.ui.fragment;

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

import com.blankj.utilcode.util.SnackbarUtils;
import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.About;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutFragment extends DialogFragment {


  @BindView(R.id.version_app_text_view)
  AppCompatTextView versionAppTextView;
  @BindView(R.id.update_button)
  MaterialButton updateButton;
  @BindView(R.id.sponsor_name)
  AppCompatTextView sponsorName;
  @BindView(R.id.sponsor_description_text_view)
  AppCompatTextView sponsorDescriptionTextView;
  private Prefser prefser;
  private Dialog loadingDialog;
  private Activity activity;
  private About about;
  private String versionName;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_about, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    prefser = new Prefser(activity);
    loadingDialog = AppUtil.getDialogLoading(activity);
    setAppVersion();
    getAbout();
    return rootView;
  }

  private void getAbout() {
    loadingDialog.show();
    AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
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
        t.printStackTrace();
        loadingDialog.dismiss();
      }
    });
  }

  private void handleAbout() {
    if (!about.getAppVersion().equals(versionName)) {
      updateButton.setVisibility(View.VISIBLE);
    }
    sponsorName.setText(about.getSponsorName());
    sponsorDescriptionTextView.setText(about.getSponsorDescription());

  }

  private void setAppVersion() {
    versionName = "";
    try {
      versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    versionAppTextView.setText(versionName);
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

  @OnClick({R.id.update_button, R.id.changelog_layout, R.id.license_layout, R.id.sponsor_website, R.id.developer_layout})
  void handleLayoutClicks(View view) {
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
  }

  private void startBrowser(String url, View view) {
    try {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(browserIntent);
    } catch (ActivityNotFoundException e) {
      AppUtil.showSnackbar(view, "برنامه مرورگر یافت نشد", activity, SnackbarUtils.LENGTH_INDEFINITE);
      e.printStackTrace();
    }

  }
}