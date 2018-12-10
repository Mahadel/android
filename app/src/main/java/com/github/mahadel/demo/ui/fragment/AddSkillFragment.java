package com.github.mahadel.demo.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.listener.CallbackResult;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.SkillsItem;
import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.ui.activity.SelectSkillActivity;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * AddSkillFragment Showing Ui for select skill and submit userSkill to the server
 */
public class AddSkillFragment extends Fragment {

  private static final int REQUEST_SELECT_SKILL = 10001;
  @BindView(R.id.skill_type_text_view)
  AppCompatTextView skillTypeTextView;
  @BindView(R.id.skill_description_edit_text)
  AppCompatEditText skillDescriptionEditText;
  @BindView(R.id.select_skill_button)
  MaterialButton selectSkillButton;
  private CallbackResult callbackResult;
  private AppUtil.SkillType skillType;
  private Activity activity;
  private SkillsItem skillsItem;
  private Prefser prefser;
  private Dialog loadingDialog;
  private AuthenticationInfo info;


  /**
   * Set callback listener for handle added userSkill to the MainActivity
   *
   * @param callbackResult CallbackResult
   */
  public void setOnCallbackResult(final CallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }

  /**
   * Set SkillType
   *
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
  public void setSkillType(AppUtil.SkillType skillType) {
    this.skillType = skillType;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_add_skill, container, false);
    ButterKnife.bind(this, rootView);
    initVariables();
    return rootView;
  }

  /**
   * Setup init values of variables
   */
  private void initVariables() {
    activity = getActivity();
    if (activity != null) {
      prefser = new Prefser(activity);
      loadingDialog = AppUtil.getLoadingDialog(activity);
    }
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    if (skillType == AppUtil.SkillType.WANT_LEARN) {
      skillTypeTextView.setText(R.string.add_skill_learn_label);
    } else {
      skillTypeTextView.setText(R.string.add_skill_teach_label);
    }
  }

  /**
   * Submit click handler. Check internet connection before submit data to the server
   *
   * @param view {@link View}
   */
  @OnClick(R.id.submit_btn)
  void submit(View view) {
    if (NetworkUtils.isConnected()) {
      submitUserSkill();
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), activity, SnackbarUtils.LENGTH_LONG);
    }
  }

  /**
   * Submit userSkill to the server
   */
  private void submitUserSkill() {
    String description = skillDescriptionEditText.getText().toString();
    int skillTypeInt;
    if (skillType == AppUtil.SkillType.WANT_TEACH) {
      skillTypeInt = 1;
    } else {
      skillTypeInt = 2;
    }
    if (skillsItem != null) {
      loadingDialog.show();
      APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
      Call<UserSkill> call = apiService.addUserSkill(info.getUuid(), skillsItem.getUuid(), description, skillTypeInt);
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
        }
      });
    } else {
      Toast.makeText(activity, getString(R.string.select_skill_message_label), Toast.LENGTH_LONG).show();
    }
  }

  /**
   * Send back userSkill that added to the server to MainActivity
   *
   * @param userSkill {@link UserSkill}
   */
  private void handleUserSkill(UserSkill userSkill) {
    if (callbackResult != null) {
      callbackResult.sendResult(userSkill, skillType);
      AppUtil.hideSoftInput(activity);
    }

  }

  @OnClick(R.id.select_skill_button)
  void selectSkill() {
    showSelectSkillDialog();
  }

  /**
   * Start {@link SelectSkillActivity} for getting skill
   */

  private void showSelectSkillDialog() {
    Intent intent = new Intent(activity, SelectSkillActivity.class);
    startActivityForResult(intent, REQUEST_SELECT_SKILL);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_SELECT_SKILL && resultCode == RESULT_OK) {
      skillsItem = data.getParcelableExtra(Constant.SKILL_ITEM);
      if (AppUtil.isRTL(activity)) {
        selectSkillButton.setText(skillsItem.getFaName());
      } else {
        selectSkillButton.setText(skillsItem.getEnName());
      }

    }
  }
}