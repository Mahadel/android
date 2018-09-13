package com.github.bkhezry.learn2learn.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.GridSpacingItemDecoration;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class DialogSelectSkillFragment extends DialogFragment {
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private Activity activity;
  private FastAdapter<SkillsItem> mFastAdapter;
  private ItemAdapter<SkillsItem> mItemAdapter;
  private Box<SkillsItem> skillsItemBox;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_select_skill, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    initRecyclerViews();
    return rootView;
  }

  private void initRecyclerViews() {
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(activity, 1);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, AppUtil.dpToPx(1, getResources()), true));
    mItemAdapter = new ItemAdapter<>();
    mFastAdapter = FastAdapter.with(mItemAdapter);
    recyclerView.setAdapter(mFastAdapter);
    mFastAdapter.withOnClickListener(new OnClickListener<SkillsItem>() {
      @Override
      public boolean onClick(View v, @NonNull IAdapter<SkillsItem> adapter, @NonNull SkillsItem item, int position) {
        Toast.makeText(activity, "Click", Toast.LENGTH_SHORT).show();
        return true;
      }
    });
    getSkillsFromDB();
  }

  private void getSkillsFromDB() {
    List<SkillsItem> skillsItems = skillsItemBox.getAll();
    mItemAdapter.clear();
    mItemAdapter.add(skillsItems);
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
  public void closeDialog() {
  }
}