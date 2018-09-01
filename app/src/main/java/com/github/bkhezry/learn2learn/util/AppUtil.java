package com.github.bkhezry.learn2learn.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppUtil {

  public static InsetDrawable getInsetDrawable(Context context) {
    int[] ATTRS = new int[] {android.R.attr.listDivider};
    TypedArray a = context.obtainStyledAttributes(ATTRS);
    Drawable divider = a.getDrawable(0);
    int inset = context.getResources().getDimensionPixelSize(R.dimen.item_space);
    InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
    a.recycle();
    return insetDivider;
  }

  public static void showSkillTypeDialog(Context context, final DialogClickListener listener) {
    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
    dialog.setContentView(R.layout.dialog_skill_type);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    dialog.findViewById(R.id.add_skill_want_learn_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.selectedSkillType(SkillType.WANT_LEARN);
        }
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.add_skill_want_teach_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.selectedSkillType(SkillType.WANT_TEACH);
        }
        dialog.dismiss();
      }
    });
    dialog.show();
    dialog.getWindow().setAttributes(lp);
  }

  public enum SkillType {
    WANT_LEARN,
    WANT_TEACH
  }

  public interface DialogClickListener {
    void selectedSkillType(SkillType skillType);
  }

  public static List<Skill> getSkills() {
    List<Skill> skillList = new ArrayList<>();

    Skill skill = new Skill();
    skill.setName("Android developer");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("iOS developer");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("Laravel");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("Dev ops");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("NodeJs");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("UI design");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("Python Django");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("C++");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("SQL");
    skillList.add(skill);
    skill = new Skill();
    skill.setName("Material design");
    skillList.add(skill);
    return skillList;
  }

  public static void updateResources(Context context, String language) {
    Locale locale = new Locale(language);
    Locale.setDefault(locale);
    Configuration config = context.getResources().getConfiguration();
    config.setLocale(locale);
    context.getResources().updateConfiguration(config,
      context.getResources().getDisplayMetrics());
  }

  /**
   * Hide the soft input.
   *
   * @param activity The activity.
   */
  public static void hideSoftInput(final Activity activity) {
    InputMethodManager imm =
      (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (imm == null) {
      return;
    }
    View view = activity.getCurrentFocus();
    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
