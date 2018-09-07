package com.github.bkhezry.learn2learn.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.Skill;
import com.github.pwittchen.prefser.library.rx2.Prefser;

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
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

  public static Context updateResources(Context context) {
    Prefser prefser = new Prefser(context);
    Locale locale = new Locale(prefser.get(Constant.LANGUAGE, String.class, "fa"));
    Locale.setDefault(locale);
    Resources res = context.getResources();
    Configuration config = new Configuration(res.getConfiguration());
    if (Build.VERSION.SDK_INT >= 17) {
      config.setLocale(locale);
      context = context.createConfigurationContext(config);
    } else {
      config.locale = locale;
      res.updateConfiguration(config, res.getDisplayMetrics());
    }
    return context;
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

  public static boolean isRTL(Locale locale) {
    final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
    return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
      directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
  }

  public static void rotateYView(View view, int degree) {
    view.setRotationY(degree);
  }
}
