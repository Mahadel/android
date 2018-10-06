package com.github.bkhezry.learn2learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.Category;
import com.github.bkhezry.learn2learn.model.ResponseMessage;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
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
  @BindView(R.id.loading_view)
  SpinKitView loadingView;
  @BindView(R.id.get_data_layout)
  LinearLayout getDataLayout;
  private GoogleApiClient mGoogleApiClient;
  private Prefser prefser;
  private Box<Category> categoryBox;
  private Box<SkillsItem> skillsItemBox;
  private Box<UserSkill> userSkillBox;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_launcher);
    ButterKnife.bind(this);
    prefser = new Prefser(this);
    BoxStore boxStore = MyApplication.getBoxStore();
    categoryBox = boxStore.boxFor(Category.class);
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    userSkillBox = boxStore.boxFor(UserSkill.class);
    if (prefser.contains(Constant.TOKEN)) {
      AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
      if (info.getFillInfo()) {
        retrieveData();
      } else {
        showGetAccountInfoLayout(info.getEmail());
      }
    } else {
      setUpLocale();
      setUpGoogleSignIn();
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
            info.setEmail(acct.getEmail());
            prefser.put(Constant.TOKEN, info);
            if (!info.getFillInfo()) {
              showGetAccountInfoLayout(acct.getEmail());
            } else {
              retrieveData();
            }
          }
        }

      }

      @Override
      public void onFailure(@NonNull Call<AuthenticationInfo> call, @NonNull Throwable t) {
        t.printStackTrace();
      }
    });
  }

  private void showGetAccountInfoLayout(String email) {
    emailTextView.setText(email);
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
    int gender;
    if (radioGender.getCheckedRadioButtonId() == R.id.radioFemale) {
      gender = 2;
    } else {
      gender = 1;
    }
    updateUser(firstName, lastName, gender);
  }

  private void updateUser(final String firstName, final String lastName, final int gender) {
    final AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ResponseMessage> call = apiService.updateUser(info.getUuid(), firstName, lastName, gender);
    call.enqueue(new Callback<ResponseMessage>() {
      @Override
      public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
        if (response.isSuccessful()) {
          info.setFillInfo(true);
          prefser.put(Constant.TOKEN, info);
          retrieveData();
        }
      }

      @Override
      public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
        t.printStackTrace();
      }
    });

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

  private void startMainActivity() {
    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
    finish();
  }

  private void retrieveData() {
    loadingLayout();
    final AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
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
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
        t.printStackTrace();
      }
    });
  }

  private void retrieveUserSkillsData() {
    final AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<List<UserSkill>> call = apiService.getUserSkills(info.getUuid());
    call.enqueue(new Callback<List<UserSkill>>() {
      @Override
      public void onResponse(@NonNull Call<List<UserSkill>> call, @NonNull Response<List<UserSkill>> response) {
        if (response.isSuccessful()) {
          List<UserSkill> userSkills = response.body();
          storeUserSkill(userSkills);
          startMainActivity();
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<UserSkill>> call, @NonNull Throwable t) {

      }
    });
  }

  private void storeUserSkill(List<UserSkill> userSkills) {
    userSkillBox.removeAll();
    userSkillBox.put(userSkills);
  }

  private void loadingLayout() {
    loginLayout.setVisibility(View.GONE);
    personalLayout.setVisibility(View.GONE);
    getDataLayout.setVisibility(View.VISIBLE);
    loadingView.setVisibility(View.VISIBLE);
  }

  private void hiddenLoading() {
    loadingView.setVisibility(View.GONE);
  }

  private void storeCategoriesDB(List<Category> categories) {
    removeDBData();
    for (Category category : categories) {
      categoryBox.put(category);
      skillsItemBox.put(category.getSkills());
    }
  }

  private void removeDBData() {
    categoryBox.removeAll();
    skillsItemBox.removeAll();
  }
}

