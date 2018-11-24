package com.github.bkhezry.learn2learn.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.listener.SkillDetailCallbackResult;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.ResponseMessage;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkillDetailFragment extends Fragment {

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
  private Prefser prefser;
  private Box<SkillsItem> skillsItemBox;
  private Dialog loadingDialog;
  private AuthenticationInfo info;


  public void setOnCallbackResult(SkillDetailCallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }

  public void setSkillType(AppUtil.SkillType skillType) {
    this.skillType = skillType;
  }

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
    activity = getActivity();
    loadingDialog = AppUtil.getDialogLoading(activity);
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    prefser = new Prefser(activity);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
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
    return rootView;
  }

  @OnClick(R.id.submit_btn)
  void submit(View view) {
    if (NetworkUtils.isConnected()) {
      editUserSkill();
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), activity, SnackbarUtils.LENGTH_LONG);
    }
  }

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
      }
    });
  }

  private void handleUserSkill(UserSkill userSkill) {
    if (callbackResult != null) {
      callbackResult.update(userSkill, skillType);
      AppUtil.hideSoftInput(activity);
    }
  }

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
      }
    });
  }
}