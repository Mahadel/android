package com.github.bkhezry.learn2learn.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.listener.CallbackResult;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.ui.activity.SelectSkillActivity;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

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


  public void setOnCallbackResult(final CallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }

  public void setSkillType(AppUtil.SkillType skillType) {
    this.skillType = skillType;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_add_skill, container, false);
    ButterKnife.bind(this, rootView);
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
    return rootView;
  }

  @OnClick(R.id.submit_btn)
  void submit(View view) {
    if (NetworkUtils.isConnected()) {
      submitUserSkill();
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), activity, SnackbarUtils.LENGTH_LONG);
    }
  }

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
      AppUtil.showSnackbar(selectSkillButton, getString(R.string.select_skill_message_label), activity, SnackbarUtils.LENGTH_LONG);
    }
  }

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