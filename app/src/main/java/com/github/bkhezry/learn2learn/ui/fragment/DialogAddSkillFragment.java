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
import com.github.bkhezry.learn2learn.util.AppUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogAddSkillFragment extends DialogFragment {

  @BindView(R.id.skill_type_text_view)
  AppCompatTextView skillTypeTextView;
  @BindView(R.id.skill_description_edit_text)
  AppCompatEditText skillDescriptionEditText;
  private int requestCode;
  private CallbackResult callbackResult;
  private AppUtil.SkillType skillType;
  private Activity activity;


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

  public void setRequestCode(int request_code) {
    this.requestCode = request_code;
  }

  @OnClick(R.id.submit_btn)
  public void submit() {
  }

  @OnClick(R.id.close_image_view)
  public void close() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  @OnClick(R.id.select_skill_button)
  public void selectSkill() {
    showSelectSkillDialog();
  }

  public interface CallbackResult {
    void sendResult(int requestCode, Object obj);
  }

  private void showSelectSkillDialog() {
    FragmentManager fragmentManager = getFragmentManager();
    DialogSelectSkillFragment skillFragment = new DialogSelectSkillFragment();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, skillFragment).addToBackStack(null).commit();
  }

}