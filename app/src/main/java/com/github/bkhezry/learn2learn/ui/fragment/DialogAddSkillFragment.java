package com.github.bkhezry.learn2learn.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.Skill;
import com.github.bkhezry.learn2learn.presenter.SkillPresenter;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePresenter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogAddSkillFragment extends DialogFragment {

  @BindView(R.id.skill_type_text_view)
  AppCompatTextView skillTypeTextView;
  @BindView(R.id.skill_name_edit_text)
  AppCompatEditText skillNameEditText;
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
    float elevation = 6f;
    Drawable backgroundDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.grey_10));
    AutocompletePresenter<Skill> presenter = new SkillPresenter(activity);
    AutocompleteCallback<Skill> callback = new AutocompleteCallback<Skill>() {
      @Override
      public boolean onPopupItemClicked(Editable editable, Skill item) {
        AppUtil.hideSoftInput(activity);
        if (item.isWantCreate()) {
          editable.clear();
          Toast.makeText(activity, item.getName(), Toast.LENGTH_SHORT).show();
        } else {
          editable.clear();
          editable.append(item.getName());
        }
        return true;
      }

      public void onPopupVisibilityChanged(boolean shown) {
      }
    };
    Autocomplete.<Skill>on(skillNameEditText)
      .with(elevation)
      .with(backgroundDrawable)
      .with(presenter)
      .with(callback)
      .build();
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

  public interface CallbackResult {
    void sendResult(int requestCode, Object obj);
  }

}