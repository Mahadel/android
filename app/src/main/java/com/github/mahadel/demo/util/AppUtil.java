package com.github.mahadel.demo.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.SkillsItem;

import java.util.Locale;

import io.objectbox.Box;

public class AppUtil {

  /**
   * Converting dp to pixel
   */
  public static int dpToPx(int dp, Resources r) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
  }

  /**
   * Show select skill type dialog
   *
   * @param context  {@link Context}
   * @param listener {@link DialogClickListener}
   */
  public static void showSkillTypeDialog(Context context, final DialogClickListener listener) {
    final Dialog dialog = new Dialog(context, R.style.DialogAnimationStyle);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
    dialog.setContentView(R.layout.dialog_skill_type);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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

  /**
   * Showing confirm dialog
   *
   * @param message  String message of dialog
   * @param context  {@link Context}
   * @param listener {@link ConfirmDialogClickListener}
   */
  public static void showConfirmDialog(String message, Context context, final ConfirmDialogClickListener listener) {
    final Dialog dialog = new Dialog(context, R.style.DialogAnimationStyle);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
    dialog.setContentView(R.layout.dialog_confirm);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.ok();
          dialog.dismiss();
        }
      }
    });
    dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.cancel();
          dialog.dismiss();
        }
      }
    });
    AppCompatTextView messageTextView = dialog.findViewById(R.id.message_text_view);
    messageTextView.setText(message);
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

  public interface ConfirmDialogClickListener {
    void ok();

    void cancel();
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

  /**
   * Check current locale of app for isRTL or not
   *
   * @param context {@link Context}
   * @return Boolean true for isRTL
   */
  public static boolean isRTL(Context context) {
    Locale locale = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
    final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
    return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
        directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
  }

  /**
   * Rotate view with degree that pass to it
   *
   * @param view   {@link View} view that should be rotate
   * @param degree Int degree of rotation
   */
  public static void rotateYView(View view, int degree) {
    view.setRotationY(degree);
  }

  public static void showFragmentInBottomSheet(Fragment fragment, FragmentManager supportFragmentManager) {
    FragmentTransaction ft = supportFragmentManager.beginTransaction();
    ft.replace(R.id.contentFrameLayout, fragment);
    ft.commit();
  }

  /**
   * Showing custom snackbar with message
   *
   * @param view     {@link View}
   * @param message  String message of snackbar
   * @param context  {@link Context}
   * @param duration Int duration of snackbar
   */
  public static void showSnackbar(View view, String message, Context context, int duration) {
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        SnackbarUtils.with(view)
            .setBottomMargin(20)
            .setMessage(message)
            .setMessageColor(context.getResources().getColor(R.color.white))
            .setBgColor(context.getResources().getColor(R.color.colorAccent))
            .setDuration(duration)
            .setAction(context.getString(R.string.ok_label), Color.YELLOW, new View.OnClickListener() {
              @Override
              public void onClick(View v) {
              }
            })
            .show();
      }
    }, 100);
  }

  /**
   * Get loading dialog
   *
   * @param context {@link Context}
   * @return instance of {@link Dialog}
   */
  public static Dialog getLoadingDialog(Context context) {
    Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    dialog.setContentView(R.layout.dialog_full_screen_loading);
    dialog.setCancelable(false);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    dialog.getWindow().setAttributes(lp);
    return dialog;
  }

  /**
   * Showing passed fragment with fragment manager
   *
   * @param fragment        {@link Fragment}
   * @param fragmentManager {@link FragmentManager}
   */
  public static void showFragment(Fragment fragment, FragmentManager fragmentManager) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
  }

  /**
   * Check version of app for isLast version or not
   *
   * @param version String version of app
   * @return Boolean
   */
  public static boolean isAtLeastVersion(int version) {
    return Build.VERSION.SDK_INT >= version;
  }

  /**
   * Get skill with uuid of it
   *
   * @param skillsItemBox {@link Box} of skills that saved in local db
   * @param skillUuid     String uuid of skill
   * @return Instance of {@link SkillsItem}
   */
  public static SkillsItem getSkill(Box<SkillsItem> skillsItemBox, String skillUuid) {
    return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();
  }

  /**
   * Get colorSecondary attr with programming
   *
   * @param context {@link Context}
   * @return Int color
   */
  public static int getThemeColorSecondary(Context context) {
    int colorAttr = context.getResources().getIdentifier("colorSecondary", "attr", context.getPackageName());
    TypedValue outValue = new TypedValue();
    context.getTheme().resolveAttribute(colorAttr, outValue, true);
    return outValue.data;
  }
}
