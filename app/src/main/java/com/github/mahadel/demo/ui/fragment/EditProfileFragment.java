package com.github.mahadel.demo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.model.UserInfo;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.FirebaseEventLog;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * EditProfileFragment Edit information of user
 */
public class EditProfileFragment extends DialogFragment {

  private static final String TAG = "EditProfileFragment";
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
  private Activity activity;
  private UserInfo userInfo;
  private CallbackListener listener;
  private Dialog loadingDialog;
  private AuthenticationInfo info;

  /**
   * Set callback listener for handle edit event
   *
   * @param listener {@link CallbackListener}
   */
  void setOnCallbackResult(CallbackListener listener) {
    this.listener = listener;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_edit_profile, container, false);
    ButterKnife.bind(this, rootView);
    initVariables();
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
    firstNameEditText.setText(userInfo.getFirstName());
    lastNameEditText.setText(userInfo.getLastName());
    if (userInfo.getGender() == 1) {
      radioMale.setChecked(true);
    } else {
      radioFemale.setChecked(true);
    }
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

  /**
   * Set user information to the fragment
   *
   * @param userInfo {@link UserInfo}
   */
  void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  /**
   * Handle edit profile click event
   *
   * @param view {@link View}
   */
  @OnClick(R.id.submit_info_button)
  void editProfileInfo(View view) {
    int gender;
    String firstName = firstNameEditText.getText().toString();
    String lastName = lastNameEditText.getText().toString();
    if (!firstName.equals("") && !lastName.equals("")) {
      if (radioGender.getCheckedRadioButtonId() == R.id.radioFemale) {
        gender = 2;
      } else {
        gender = 1;
      }
      if (NetworkUtils.isConnected()) {
        updateUser(firstName, lastName, gender);
      } else {
        AppUtil.showSnackbar(view, getString(R.string.no_internet_label), activity, SnackbarUtils.LENGTH_LONG);
      }
    } else {
      AppUtil.showSnackbar(view, getString(R.string.field_require_label), activity, SnackbarUtils.LENGTH_LONG);
    }
  }

  /**
   * Update user information in the server
   *
   * @param firstName String first name
   * @param lastName  String last name
   * @param gender    Int gender
   */
  private void updateUser(final String firstName, final String lastName, final int gender) {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ResponseMessage> call = apiService.updateUser(info.getUuid(), firstName, lastName, gender);
    call.enqueue(new Callback<ResponseMessage>() {
      @Override
      public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          userInfo.setFirstName(firstName);
          userInfo.setGender(gender);
          userInfo.setLastName(lastName);
          if (listener != null) {
            listener.sendResult(userInfo);
            close();
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        FirebaseEventLog.log("server_failure", TAG, "updateUser", t.getMessage());
      }
    });

  }

  @OnClick(R.id.close_image_view)
  void close() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  public interface CallbackListener {
    void sendResult(UserInfo userInfo);
  }
}