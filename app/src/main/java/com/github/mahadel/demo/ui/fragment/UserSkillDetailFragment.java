package com.github.mahadel.demo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.listener.SkillDetailCallbackResult;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.model.SkillsItem;
import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.DatabaseUtil;
import com.github.mahadel.demo.util.FirebaseEventLog;
import com.github.mahadel.demo.util.MyApplication;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Showing userSkill detail
 * Provide edit or delete option
 */
public class UserSkillDetailFragment extends Fragment {

  private static final String TAG = "UserSkillDetailFragment";
  @BindView(R.id.skill_type_text_view)
  AppCompatTextView skillTypeTextView;
  @BindView(R.id.skill_description_edit_text)
  AppCompatEditText skillDescriptionEditText;
  @BindView(R.id.skill_name_text_view)
  AppCompatTextView skillNameTextView;
  private SkillDetailCallbackResult callbackResult;
  private AppUtil.SkillType skillType;
  private Activity activity;
  private UserSkill userSkill;
  private Box<SkillsItem> skillsItemBox;
  private Dialog loadingDialog;
  private AuthenticationInfo info;

  /**
   * Set callback listener  for edit or remove event
   *
   * @param callbackResult {@link SkillDetailCallbackResult}
   */

  public void setOnCallbackResult(SkillDetailCallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }

  /**
   * Set skill type
   *
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
  public void setSkillType(AppUtil.SkillType skillType) {
    this.skillType = skillType;
  }

  /**
   * Set userSkill instance to the fragment
   *
   * @param item {@link UserSkill}
   */
  public void setSkillItem(UserSkill item) {
    userSkill = item;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_skill_detail, container, false);
    ButterKnife.bind(this, rootView);
    initVariables();
    setUpValues();
    return rootView;
  }

  /**
   * Setup init values of variables
   */
  private void initVariables() {
    activity = getActivity();
    loadingDialog = AppUtil.getLoadingDialog(activity);
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    Prefser prefser = new Prefser(activity);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
  }

  /**
   * Set values of {@link UserSkill} in the UI
   */
  private void setUpValues() {
    if (skillType == AppUtil.SkillType.WANT_LEARN) {
      skillTypeTextView.setText(R.string.add_skill_learn_label);
    } else {
      skillTypeTextView.setText(R.string.add_skill_teach_label);
    }
    skillDescriptionEditText.setText(userSkill.getDescription());
    SkillsItem skillItem = DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, userSkill.getSkillUuid()).findFirst();
    if (skillItem != null) {
      if (AppUtil.isRTL(activity)) {

        skillNameTextView.setText(skillItem.getFaName());
      } else {
        skillNameTextView.setText(skillItem.getEnName());
      }
    }
  }

  /**
   * Handle submit click event
   *
   * @param view {@link View}
   */
  @OnClick(R.id.submit_btn)
  void submit(View view) {
    if (NetworkUtils.isConnected()) {
      editUserSkill();
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), activity, SnackbarUtils.LENGTH_LONG);
    }
  }

  /**
   * Edit userSkill description in the server
   */

  private void editUserSkill() {
    loadingDialog.show();
    String description = skillDescriptionEditText.getText().toString();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<UserSkill> call = apiService.editUserSkill(info.getUuid(), userSkill.getUuid(), description);
    call.enqueue(new Callback<UserSkill>() {
      @Override
      public void onResponse(@NonNull Call<UserSkill> call, @NonNull Response<UserSkill> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          UserSkill userSkill = response.body();
          handleUserSkill(userSkill);
        }
      }

      @Override
      public void onFailure(@NonNull Call<UserSkill> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        FirebaseEventLog.log("server_failure", TAG, "editUsersSkill", t.getMessage());
      }
    });
  }

  /**
   * Send back the userSkill edited to the MainActivity
   *
   * @param userSkill {@link UserSkill}
   */
  private void handleUserSkill(UserSkill userSkill) {
    if (callbackResult != null) {
      callbackResult.update(userSkill, skillType);
      AppUtil.hideSoftInput(activity);
    }
  }

  /**
   * Handle remove click event & confirm remove skill before request
   *
   * @param view {@link View}
   */
  @OnClick(R.id.remove_btn)
  void removeSkill(View view) {
    if (NetworkUtils.isConnected()) {
      AppUtil.showConfirmDialog(getString(R.string.confirm_remove_skill_label), activity, new AppUtil.ConfirmDialogClickListener() {
        @Override
        public void ok() {
          removeSkillServer();
        }

        @Override
        public void cancel() {

        }
      });
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), activity, SnackbarUtils.LENGTH_LONG);
    }

  }

  /**
   * Request remove userSkill from the server
   */
  private void removeSkillServer() {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ResponseMessage> call = apiService.deleteUserSkill(info.getUuid(), userSkill.getUuid());
    call.enqueue(new Callback<ResponseMessage>() {
      @Override
      public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          if (callbackResult != null) {
            callbackResult.remove(userSkill, skillType);
            AppUtil.hideSoftInput(activity);
          }
        }

      }

      @Override
      public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        AppUtil.showSnackbar(skillNameTextView, getString(R.string.error_request_message), activity, SnackbarUtils.LENGTH_LONG);
        FirebaseEventLog.log("server_failure", TAG, "removeUsersSkill", t.getMessage());
      }
    });
  }
}