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
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogAddSkillFragment extends DialogFragment {

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


  public void setOnCallbackResult(final CallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }

  public void setSkillType(AppUtil.SkillType skillType) {
    this.skillType = skillType;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_add_skill, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    prefser = new Prefser(activity);
    if (skillType == AppUtil.SkillType.WANT_LEARN) {
      skillTypeTextView.setText(R.string.add_skill_learn_label);
    } else {
      skillTypeTextView.setText(R.string.add_skill_teach_label);
    }
    initSkillAutoComplete();
    return rootView;
  }

  private void initSkillAutoComplete() {
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
    int skillTypeInt;
    if (skillType == AppUtil.SkillType.WANT_TEACH) {
      skillTypeInt = 1;
    } else {
      skillTypeInt = 2;
    }
    AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<UserSkill> call = apiService.addUserSkill(info.getUuid(), skillsItem.getUuid(), description, skillTypeInt);
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
  public void close() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  @OnClick(R.id.select_skill_button)
  void selectSkill() {
    showSelectSkillDialog();
  }

  private void showSelectSkillDialog() {
    FragmentManager fragmentManager = getFragmentManager();
    DialogSelectSkillFragment skillFragment = new DialogSelectSkillFragment();
    skillFragment.setCallbackListener(new CallbackResult() {
      @Override
      public void sendResult(Object obj) {
        skillsItem = (SkillsItem) obj;
        if (AppUtil.isRTL(activity)) {
          selectSkillButton.setText(skillsItem.getFaName());
        } else {
          selectSkillButton.setText(skillsItem.getEnName());
        }
      }
    });
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, skillFragment).addToBackStack(null).commit();
  }

}