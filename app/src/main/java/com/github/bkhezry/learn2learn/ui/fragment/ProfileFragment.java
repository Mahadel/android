package com.github.bkhezry.learn2learn.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.UserInfo;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.greenrobot.essentials.StringUtils.md5;

public class ProfileFragment extends DialogFragment implements
    GoogleApiClient.OnConnectionFailedListener {


  @BindView(R.id.email_text_view)
  AppCompatTextView emailTextView;
  @BindView(R.id.name_text_view)
  AppCompatTextView nameTextView;
  @BindView(R.id.profile_pic)
  CircularImageView profilePic;
  private ProfileCallbackResult callbackResult;
  private Activity activity;
  private Prefser prefser;
  private GoogleApiClient mGoogleApiClient;


  public void setOnCallbackResult(ProfileCallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    prefser = new Prefser(activity);
    setUpGoogleSignIn();
    AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    emailTextView.setText(info.getEmail());
    String profilePicURL = Constant.GRAVATAR_URL + md5(info.getEmail()).toLowerCase() + "?size=400";
    Glide.with(activity).load(profilePicURL).into(profilePic);
    requestProfileInfo();
    return rootView;
  }

  private void requestProfileInfo() {
    AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<UserInfo> call = apiService.getUserInfo(info.getUuid());
    call.enqueue(new Callback<UserInfo>() {
      @Override
      public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
        if (response.isSuccessful()) {
          UserInfo userInfo = response.body();
          if (userInfo != null) {
            nameTextView.setText(String.format("%s %s", userInfo.getFirstName(), userInfo.getLastName()));
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
        t.printStackTrace();

      }
    });
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
  public void close() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  @OnClick({R.id.edit_profile_button, R.id.logout_profile_button, R.id.delete_profile_button})
  public void handleButtonClick(View view) {
    switch (view.getId()) {
      case R.id.edit_profile_button:
        break;
      case R.id.logout_profile_button:
        revokeAccess();
        break;
      case R.id.delete_profile_button:
        break;
    }
  }

  private void revokeAccess() {
    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            prefser.remove(Constant.TOKEN);
            if (callbackResult != null) {
              callbackResult.logout();
            }
          }
        });
  }

  private void setUpGoogleSignIn() {
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();
    mGoogleApiClient = new GoogleApiClient.Builder(activity)
        .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  public interface ProfileCallbackResult {
    void logout();
  }

}