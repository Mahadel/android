package com.github.bkhezry.learn2learn.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.listener.CallbackResult;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
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
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogSkillDetailFragment extends DialogFragment {

  @BindView(R.id.skill_type_text_view)
  AppCompatTextView skillTypeTextView;
  @BindView(R.id.skill_description_edit_text)
  AppCompatEditText skillDescriptionEditText;
  @BindView(R.id.skill_name_text_view)
  AppCompatTextView skillNameTextView;
  private CallbackResult callbackResult;
  private AppUtil.SkillType skillType;
  private Activity activity;
  private UserSkill userSkill;
  private Prefser prefser;
  private Box<SkillsItem> skillsItemBox;


  public void setOnCallbackResult(final CallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }

  public void setSkillType(AppUtil.SkillType skillType) {
    this.skillType = skillType;
  }

  public void setSkillItem(UserSkill item) {
    userSkill = item;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_skill_detail, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    prefser = new Prefser(activity);
    if (skillType == AppUtil.SkillType.WANT_LEARN) {
      skillTypeTextView.setText(R.string.add_skill_learn_label);
    } else {
      skillTypeTextView.setText(R.string.add_skill_teach_label);
    }
    skillDescriptionEditText.setText(userSkill.getDescription());
    SkillsItem skillItem = DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, userSkill.getSkillUuid()).findFirst();
    if (AppUtil.isRTL(activity)) {
      skillNameTextView.setText(skillItem.getFaName());
    } else {
      skillNameTextView.setText(skillItem.getEnName());
    }
    return rootView;
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

  @OnClick(R.id.submit_btn)
  void submit() {
    String description = skillDescriptionEditText.getText().toString();
    AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<UserSkill> call = apiService.editUserSkill(info.getUuid(), userSkill.getUuid(), description);
    call.enqueue(new Callback<UserSkill>() {
      @Override
      public void onResponse(@NonNull Call<UserSkill> call, @NonNull Response<UserSkill> response) {
        if (response.isSuccessful()) {
          UserSkill userSkill = response.body();
          handleUserSkill(userSkill);
        }
      }

      @Override
      public void onFailure(@NonNull Call<UserSkill> call, @NonNull Throwable t) {
        t.printStackTrace();
      }
    });
  }

  private void handleUserSkill(UserSkill userSkill) {
    if (callbackResult != null) {
      callbackResult.sendResult(userSkill);
      close();
    }

  }

  @OnClick(R.id.close_image_view)
  void close() {
    AppUtil.hideSoftInput(activity);
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }
}