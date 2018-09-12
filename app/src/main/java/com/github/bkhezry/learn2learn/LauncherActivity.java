package com.github.bkhezry.learn2learn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LauncherActivity extends BaseActivity implements
    GoogleApiClient.OnConnectionFailedListener {
  private static final int RC_SIGN_IN = 10001;
  @BindView(R.id.login_layout)
  LinearLayout loginLayout;
  @BindView(R.id.email_text_view)
  AppCompatTextView emailTextView;
  @BindView(R.id.first_name_edit_text)
  TextInputEditText firstNameEditText;
  @BindView(R.id.til_first_name)
  TextInputLayout tilFirstName;
  @BindView(R.id.last_name_edit_text)
  TextInputEditText lastNameEditText;
  @BindView(R.id.til_last_name)
  TextInputLayout tilLastName;
  @BindView(R.id.radioMale)
  AppCompatRadioButton radioMale;
  @BindView(R.id.radioFemale)
  AppCompatRadioButton radioFemale;
  @BindView(R.id.radioGender)
  RadioGroup radioGender;
  @BindView(R.id.personal_layout)
  LinearLayout personalLayout;
  @BindView(R.id.persian_image_view)
  AppCompatImageView persianImageView;
  @BindView(R.id.english_image_view)
  AppCompatImageView englishImageView;
  @BindView(R.id.submit_info_button)
  MaterialButton submitInfoButton;
  private GoogleApiClient mGoogleApiClient;
  private Prefser prefser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_launcher);
    ButterKnife.bind(this);
    prefser = new Prefser(this);
    setUpLocale();
    setUpGoogleSignIn();
  }

  private void setUpGoogleSignIn() {
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();
  }

  private void setUpLocale() {
    if (prefser.contains(Constant.LANGUAGE)) {
      if (prefser.get(Constant.LANGUAGE, String.class, null).equals("fa")) {
        changeLanguagePersian();
      } else {
        changeLanguageEnglish();
      }
    }
  }

  @OnClick(R.id.google_login_button)
  public void login() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      handleSignInResult(result);
    }
  }

  private void handleSignInResult(GoogleSignInResult result) {
    if (result.isSuccess()) {
      // Signed in successfully, show authenticated UI.
      GoogleSignInAccount acct = result.getSignInAccount();
      if (acct != null) {
        storeUser(acct);
      }
    }
  }

  private void storeUser(final GoogleSignInAccount acct) {
    APIService apiService = RetrofitUtil.getRetrofit("").create(APIService.class);
    Call<AuthenticationInfo> call = apiService.storeUser(acct.getIdToken());
    call.enqueue(new Callback<AuthenticationInfo>() {
      @Override
      public void onResponse(@NonNull Call<AuthenticationInfo> call, @NonNull Response<AuthenticationInfo> response) {
        if (response.isSuccessful()) {
          AuthenticationInfo info = response.body();
          if (info != null) {
            prefser.put(Constant.TOKEN, info);
            handleAccount(acct);
          }
        }

      }

      @Override
      public void onFailure(@NonNull Call<AuthenticationInfo> call, @NonNull Throwable t) {
        t.printStackTrace();
      }
    });
  }

  private void handleAccount(GoogleSignInAccount acct) {
    emailTextView.setText(acct.getEmail());
    loginLayout.setVisibility(View.GONE);
    personalLayout.setVisibility(View.VISIBLE);
  }


  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onPause() {
    super.onPause();
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      mGoogleApiClient.stopAutoManage(this);
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  public void onResume() {
    if (mGoogleApiClient != null) {
      if (!mGoogleApiClient.isConnected()) {
        mGoogleApiClient.connect();
      }
    }
    super.onResume();
  }

  @OnClick(R.id.submit_info_button)
  public void submitInfo() {
    String firstName = firstNameEditText.getText().toString();
    String lastName = lastNameEditText.getText().toString();
    int gender = 0;
    if (radioGender.getCheckedRadioButtonId() == R.id.radioFemale) {
      gender = 2;
    } else {
      gender = 1;
    }

  }

  private void startMainActivity() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  @OnClick({R.id.persian_image_view, R.id.english_image_view})
  public void handleLanguage(View view) {
    switch (view.getId()) {
      case R.id.persian_image_view:
        changeLanguagePersian();
        break;
      case R.id.english_image_view:
        changeLanguageEnglish();
        break;
    }
    restartApp();
  }

  private void changeLanguagePersian() {
    persianImageView.setBackgroundResource(R.drawable.image_border);
    englishImageView.setBackgroundResource(android.R.color.transparent);
    prefser.put(Constant.LANGUAGE, "fa");
  }

  private void changeLanguageEnglish() {
    englishImageView.setBackgroundResource(R.drawable.image_border);
    persianImageView.setBackgroundResource(android.R.color.transparent);
    prefser.put(Constant.LANGUAGE, "en");
  }

  private void restartApp() {
    Intent i = new Intent(this, LauncherActivity.class);
    startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    System.exit(0);
  }
}

