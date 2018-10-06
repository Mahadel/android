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
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends DialogFragment {


  private CallbackResult callbackResult;
  private Activity activity;
  private Prefser prefser;


  public void setOnCallbackResult(final CallbackResult callbackResult) {
    this.callbackResult = callbackResult;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    prefser = new Prefser(activity);
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

  @OnClick(R.id.close_image_view)
  public void close() {
    AppUtil.hideSoftInput(activity);
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }
}