package com.github.bkhezry.learn2learn.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.blankj.utilcode.util.SnackbarUtils;
import com.github.bkhezry.learn2learn.R;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import java.util.Locale;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AppUtil {

  /**
   * Converting dp to pixel
   */
  public static int dpToPx(int dp, Resources r) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
  }

  public static InsetDrawable getInsetDrawable(Context context) {
    int[] ATTRS = new int[]{android.R.attr.listDivider};
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

  public static void showConfirmDialog(String message, Context context, final ConfirmDialogClickListener listener) {
    final Dialog dialog = new Dialog(context);
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

  public static boolean isRTL(Context context) {
    Locale locale = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
    final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
    return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
        directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
  }

  public static void rotateYView(View view, int degree) {
    view.setRotationY(degree);
  }

  public static void showFragmentInBottomSheet(Fragment fragment, FragmentManager supportFragmentManager) {
    FragmentTransaction ft = supportFragmentManager.beginTransaction();
    ft.replace(R.id.contentFrameLayout, fragment);
    ft.commit();
  }

  public static void showSnackbar(View view, String message, Context context) {
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        SnackbarUtils.with(view)
            .setBottomMargin(20)
            .setMessage(message)
            .setMessageColor(context.getResources().getColor(R.color.white))
            .setBgColor(context.getResources().getColor(R.color.colorAccent))
            .setDuration(SnackbarUtils.LENGTH_LONG)
            .setAction(context.getString(R.string.ok_label), Color.YELLOW, new View.OnClickListener() {
              @Override
              public void onClick(View v) {
              }
            })
            .show();
      }
    }, 100);
  }

  public static Dialog getDialogLoading(Context context) {
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

  public static void showFragment(Fragment fragment, FragmentManager fragmentManager) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
  }
}
