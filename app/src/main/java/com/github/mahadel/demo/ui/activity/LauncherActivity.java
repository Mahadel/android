package com.github.mahadel.demo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.Category;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.model.SkillsItem;
import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.FirebaseEventLog;
import com.github.mahadel.demo.util.LocaleManager;
import com.github.mahadel.demo.util.MyApplication;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * First activity that launch in start application
 * Handle sign up UI & procedure of submit user info
 * Getting categories & skills data from server
 * Getting userSkill of user from server
 */
public class LauncherActivity extends BaseActivity implements
    GoogleApiClient.OnConnectionFailedListener {
  private static final int RC_SIGN_IN = 10001;
  private static final String TAG = "LauncherActivity";
  @BindView(R.id.login_layout)
  RelativeLayout loginLayout;
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
  @BindView(R.id.get_data_layout)
  LinearLayout getDataLayout;
  @BindView(R.id.google_login_button)
  MaterialButton googleLoginButton;
  @BindView(R.id.error_get_data_layout)
  MaterialCardView errorGetDataLayout;
  @BindView(R.id.retry_button)
  MaterialButton retryButton;
  @BindView(R.id.root_card_view)
  MaterialCardView rootCardView;
  private GoogleApiClient mGoogleApiClient;
  private Prefser prefser;
  private Box<Category> categoryBox;
  private Box<SkillsItem> skillsItemBox;
  private Box<UserSkill> userSkillBox;
  private Dialog loadingDialog;
  private AuthenticationInfo info;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_launcher);
    ButterKnife.bind(this);
    initVariables();
    checkIsLogin();
  }

  /**
   * Setup init value of variables
   */
  private void initVariables() {
    prefser = new Prefser(this);
    BoxStore boxStore = MyApplication.getBoxStore();
    categoryBox = boxStore.boxFor(Category.class);
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    userSkillBox = boxStore.boxFor(UserSkill.class);
    loadingDialog = AppUtil.getLoadingDialog(this);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
  }

  /**
   * Check for previous login. showing sign up UI or getting data from server
   */
  private void checkIsLogin() {
    if (prefser.contains(Constant.TOKEN)) {

      if (info.getFillInfo()) {
        retrieveData(rootCardView);
      } else {
        showGetAccountInfoLayout(info.getEmail());
      }
    } else {
      setUpLocale();
      setUpGoogleSignIn();
    }
  }

  /**
   * Setup locale language. Persian or English
   */
  private void setUpLocale() {
    if (MyApplication.localeManager.getLanguage().equals(LocaleManager.LANGUAGE_PERSIAN)) {
      changeLanguagePersian();
    } else {
      changeLanguageEnglish();
    }
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

  @OnClick(R.id.google_login_button)
  public void login(View view) {
    if (NetworkUtils.isConnected()) {
      Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
      startActivityForResult(signInIntent, RC_SIGN_IN);
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), this, SnackbarUtils.LENGTH_LONG);
    }
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
      GoogleSignInAccount acct = result.getSignInAccount();
      if (acct != null) {
        storeUser(acct);
      }
    }
  }

  /**
   * Store user token in server
   *
   * @param acct {@link GoogleSignInAccount} data of login with google
   */
  private void storeUser(final GoogleSignInAccount acct) {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit("").create(APIService.class);
    Call<AuthenticationInfo> call = apiService.storeUser(acct.getIdToken());
    call.enqueue(new Callback<AuthenticationInfo>() {
      @Override
      public void onResponse(@NonNull Call<AuthenticationInfo> call, @NonNull Response<AuthenticationInfo> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          AuthenticationInfo info = response.body();
          if (info != null) {
            info.setEmail(acct.getEmail());
            prefser.put(Constant.TOKEN, info);
            if (!info.getFillInfo()) {
              showGetAccountInfoLayout(acct.getEmail());
            } else {
              retrieveData(rootCardView);
            }
          }
        }

      }

      @Override
      public void onFailure(@NonNull Call<AuthenticationInfo> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        AppUtil.showSnackbar(emailTextView, getString(R.string.error_request_message), LauncherActivity.this, SnackbarUtils.LENGTH_LONG);
        FirebaseEventLog.log("server_failure", TAG, "storeUser", t.getMessage());
      }
    });
  }

  /**
   * Show email in personal layout UI
   *
   * @param email String email
   */
  private void showGetAccountInfoLayout(String email) {
    emailTextView.setText(email);
    loginLayout.setVisibility(View.GONE);
    personalLayout.setVisibility(View.VISIBLE);
  }

  /**
   * Handle submit info of user to server
   */
  @OnClick(R.id.submit_info_button)
  public void submitInfo() {
    String firstName = firstNameEditText.getText().toString();
    String lastName = lastNameEditText.getText().toString();
    if (!firstName.equals("") && !lastName.equals("")) {
      int gender;
      if (radioGender.getCheckedRadioButtonId() == R.id.radioFemale) {
        gender = 2;
      } else {
        gender = 1;
      }
      if (NetworkUtils.isConnected()) {
        updateUser(firstName, lastName, gender);
      } else {
        AppUtil.showSnackbar(submitInfoButton, getString(R.string.no_internet_label), this, SnackbarUtils.LENGTH_LONG);
      }
    } else {
      AppUtil.showSnackbar(submitInfoButton, getString(R.string.field_require_label), this, SnackbarUtils.LENGTH_LONG);
    }
  }

  /**
   * Update user info with information that user provided.
   *
   * @param firstName String first name of user
   * @param lastName  String last name of user
   * @param gender    Int gender of user
   */
  private void updateUser(final String firstName, final String lastName, final int gender) {
    loadingDialog.show();
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ResponseMessage> call = apiService.updateUser(info.getUuid(), firstName, lastName, gender);
    call.enqueue(new Callback<ResponseMessage>() {
      @Override
      public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          info.setFillInfo(true);
          prefser.put(Constant.TOKEN, info);
          retrieveData(rootCardView);
        }
      }

      @Override
      public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        AppUtil.showSnackbar(emailTextView, getString(R.string.error_request_message), LauncherActivity.this, SnackbarUtils.LENGTH_LONG);
        FirebaseEventLog.log("server_failure", TAG, "updateUser", t.getMessage());
      }
    });

  }

  /**
   * Handle change language of application
   *
   * @param view {@link View}
   */
  @OnClick({R.id.persian_image_view, R.id.english_image_view})
  public void handleLanguage(View view) {
    switch (view.getId()) {
      case R.id.persian_image_view:
        changeLanguagePersian();
        setNewLocale(LocaleManager.LANGUAGE_PERSIAN);
        break;
      case R.id.english_image_view:
        changeLanguageEnglish();
        setNewLocale(LocaleManager.LANGUAGE_ENGLISH);
        break;
    }
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
   * Set locale to the language & restart app
   *
   * @param language String name of language
   */
  private void setNewLocale(String language) {
    MyApplication.localeManager.setNewLocale(this, language);
    Intent i = new Intent(this, LauncherActivity.class);
    startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    System.exit(0);
  }


  private void startMainActivity() {
    loadingDialog.dismiss();
    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
    finish();
  }

  private void retrieveData(View view) {
    if (NetworkUtils.isConnected()) {
      retrieveSkillsData();
    } else {
      hiddenLoadingLayout();
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), this, SnackbarUtils.LENGTH_LONG);
    }
  }

  /**
   * Get categories & skills data from server
   */
  private void retrieveSkillsData() {
    loadingLayout();
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<List<Category>> call = apiService.getCategories();
    call.enqueue(new Callback<List<Category>>() {
      @Override
      public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
        if (response.isSuccessful()) {
          List<Category> categories = response.body();
          if (categories != null) {
            storeCategoriesDB(categories);
          }
          retrieveUserSkillsData();
        } else if (response.code() == 403) {
          removeToken();
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
        hiddenLoadingLayout();
        t.printStackTrace();
        AppUtil.showSnackbar(emailTextView, getString(R.string.error_request_message), LauncherActivity.this, SnackbarUtils.LENGTH_LONG);
        FirebaseEventLog.log("server_failure", TAG, "retrieveSkillData", t.getMessage());
      }
    });
  }

  /**
   * Get userSkill from server
   */
  private void retrieveUserSkillsData() {
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<List<UserSkill>> call = apiService.getUserSkills(info.getUuid());
    call.enqueue(new Callback<List<UserSkill>>() {
      @Override
      public void onResponse(@NonNull Call<List<UserSkill>> call, @NonNull Response<List<UserSkill>> response) {
        if (response.isSuccessful()) {
          List<UserSkill> userSkills = response.body();
          storeUserSkill(userSkills);
          startMainActivity();
        } else if (response.code() == 403) {
          removeToken();
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<UserSkill>> call, @NonNull Throwable t) {
        hiddenLoadingLayout();
        t.printStackTrace();
        AppUtil.showSnackbar(emailTextView, getString(R.string.error_request_message), LauncherActivity.this, SnackbarUtils.LENGTH_LONG);
        FirebaseEventLog.log("server_failure", TAG, "retrieveUserSkillData", t.getMessage());
      }
    });
  }

  private void removeToken() {
    loadingDialog.dismiss();
    prefser.remove(Constant.TOKEN);
    startActivity(new Intent(this, LauncherActivity.class));
    finish();
    Toast.makeText(this, getString(R.string.access_deny_label), Toast.LENGTH_LONG).show();
  }

  /**
   * Store list of userSkill in local db
   *
   * @param userSkills List of {@link UserSkill}
   */
  private void storeUserSkill(List<UserSkill> userSkills) {
    userSkillBox.removeAll();
    userSkillBox.put(userSkills);
  }

  /**
   * Store list of category in local db
   *
   * @param categories List of {@link Category}
   */
  private void storeCategoriesDB(List<Category> categories) {
    removeDBData();
    for (Category category : categories) {
      categoryBox.put(category);
      skillsItemBox.put(category.getSkills());
    }
  }

  private void loadingLayout() {
    loadingDialog.show();
    loginLayout.setVisibility(View.GONE);
    personalLayout.setVisibility(View.GONE);
    getDataLayout.setVisibility(View.VISIBLE);
    errorGetDataLayout.setVisibility(View.GONE);
  }

  private void hiddenLoadingLayout() {
    loadingDialog.dismiss();
    loginLayout.setVisibility(View.GONE);
    personalLayout.setVisibility(View.GONE);
    getDataLayout.setVisibility(View.VISIBLE);
    errorGetDataLayout.setVisibility(View.VISIBLE);
  }

  private void removeDBData() {
    categoryBox.removeAll();
    skillsItemBox.removeAll();
  }

  @OnClick(R.id.retry_button)
  public void retry() {
    retrieveData(rootCardView);
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
}

