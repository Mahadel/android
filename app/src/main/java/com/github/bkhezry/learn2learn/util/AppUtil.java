package com.github.bkhezry.learn2learn.util;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;

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

  public static void showSelectGenderDialog(Context context, final DialogClickListener listener) {
    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
    dialog.setContentView(R.layout.dialog_skill_type);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
}
