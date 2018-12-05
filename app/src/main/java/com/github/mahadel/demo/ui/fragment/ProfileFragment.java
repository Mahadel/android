package com.github.mahadel.demo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.model.UserInfo;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.ui.activity.LauncherActivity;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mikhaellopez.circularimageview.CircularImageView;

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

  private Activity activity;
  private Prefser prefser;
  private GoogleApiClient mGoogleApiClient;
  private UserInfo userInfo;
  private Dialog loadingDialog;
  private AuthenticationInfo info;


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
    ButterKnife.bind(this, rootView);
    initVariables();
    showProfileInfo();
    setUpGoogleSignIn();
    requestProfileInfo();
    getFirebaseId();
    return rootView;
  }

  private void getFirebaseId() {
    FirebaseInstanceId.getInstance().getInstanceId()
        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
          @Override
          public void onComplete(@NonNull Task<InstanceIdResult> task) {
            if (!task.isSuccessful()) {
              Log.w("Firebase TOKEN", "getInstanceId failed", task.getException());
              return;
            }
            String token = task.getResult().getToken();
          }
        });
  }

  private void initVariables() {
    activity = getActivity();
    prefser = new Prefser(activity);
    loadingDialog = AppUtil.getLoadingDialog(activity);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
  }

  private void showProfileInfo() {
    emailTextView.setText(info.getEmail());
    String profilePicURL = Constant.GRAVATAR_URL + md5(info.getEmail()).toLowerCase() + "?size=400";
    Glide.with(activity).load(profilePicURL).into(profilePic);
  }

  private void requestProfileInfo() {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<UserInfo> call = apiService.getUserInfo(info.getUuid());
    call.enqueue(new Callback<UserInfo>() {
      @Override
      public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          userInfo = response.body();
          if (userInfo != null) {
            nameTextView.setText(String.format("%s %s", userInfo.getFirstName(), userInfo.getLastName()));
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
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
  void close() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  @OnClick({R.id.edit_profile_button, R.id.logout_profile_button, R.id.delete_profile_button})
  void handleButtonClick(View view) {
    switch (view.getId()) {
      case R.id.edit_profile_button:
        editProfileInfo();
        break;
      case R.id.logout_profile_button:
        revokeAccess();
        break;
      case R.id.delete_profile_button:
        deleteAccount();
        break;
    }
  }

  private void deleteAccount() {
    AppUtil.showConfirmDialog(activity.getString(R.string.delete_account_message), activity, new AppUtil.ConfirmDialogClickListener() {
      @Override
      public void ok() {
        loadingDialog.show();
        APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
        Call<ResponseMessage> call = apiService.deleteUserAccount(info.getUuid());
        call.enqueue(new Callback<ResponseMessage>() {
          @Override
          public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
            loadingDialog.dismiss();
            if (response.isSuccessful()) {
              revoke();
            }
          }

          @Override
          public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
            loadingDialog.dismiss();
            t.printStackTrace();
          }
        });
      }

      @Override
      public void cancel() {

      }
    });
  }

  private void editProfileInfo() {
    FragmentManager fragmentManager = getFragmentManager();
    EditProfileFragment editProfileFragment = new EditProfileFragment();
    editProfileFragment.setUserInfo(userInfo);
    editProfileFragment.setOnCallbackResult(new EditProfileFragment.CallbackListener() {
      @Override
      public void sendResult(UserInfo userInfo) {
        updateUI(userInfo);
      }
    });
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, editProfileFragment).addToBackStack(null).commit();
  }

  private void updateUI(UserInfo userInfoResult) {
    nameTextView.setText(String.format("%s %s", userInfoResult.getFirstName(), userInfoResult.getLastName()));
  }

  private void revokeAccess() {
    AppUtil.showConfirmDialog(activity.getString(R.string.logout_message_label), activity, new AppUtil.ConfirmDialogClickListener() {
      @Override
      public void ok() {
        revoke();
      }

      @Override
      public void cancel() {

      }
    });
  }

  private void revoke() {
    loadingDialog.show();
    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            loadingDialog.dismiss();
            prefser.remove(Constant.TOKEN);
            startActivity(new Intent(activity, LauncherActivity.class));
            activity.finish();
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


  @Override
  public void onPause() {
    super.onPause();
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && getActivity() != null) {
      mGoogleApiClient.stopAutoManage(getActivity());
      mGoogleApiClient.disconnect();
    }
  }
}