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
import com.github.bkhezry.learn2learn.StickyHeaderAdapter;
import com.github.bkhezry.learn2learn.listener.CallbackResult;
import com.github.bkhezry.learn2learn.model.Category;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
  private Box<SkillsItem> skillsItemBox;
  private Box<Category> categoryBox;
  private Box<UserSkill> userSkillBox;
  private FastAdapter<SkillsItem> fastAdapter;
  private CallbackResult callbackListener;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_select_skill, container, false);
    ButterKnife.bind(this, rootView);
    activity = getActivity();
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    categoryBox = boxStore.boxFor(Category.class);
    userSkillBox = boxStore.boxFor(UserSkill.class);
    initRecyclerViews();
    return rootView;
  }

  private void initRecyclerViews() {
    //create our adapters
    final StickyHeaderAdapter<SkillsItem> stickyHeaderAdapter = new StickyHeaderAdapter<>();
    final ItemAdapter headerAdapter = new ItemAdapter();
    final ItemAdapter<SkillsItem> itemAdapter = new ItemAdapter<>();
    fastAdapter = FastAdapter.with(Arrays.asList(headerAdapter, itemAdapter));
    fastAdapter.withSelectable(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(stickyHeaderAdapter.wrap(fastAdapter));
    //this adds the Sticky Headers within our list
    final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
    recyclerView.addItemDecoration(decoration);
    List<SkillsItem> skillsItems = skillsItemBox.getAll();
    if (AppUtil.isRTL(activity)) {
      for (SkillsItem skillsItem : skillsItems) {
        Category category = DatabaseUtil.getCategoryWithUUID(categoryBox, skillsItem.getCategoryUuid());
        skillsItem.setCategoryName(category.getFaName());
        skillsItem.withIdentifier(category.getId());
      }
    } else {
      for (SkillsItem skillsItem : skillsItems) {
        Category category = DatabaseUtil.getCategoryWithUUID(categoryBox, skillsItem.getCategoryUuid());
        skillsItem.setCategoryName(category.getEnName());
        skillsItem.withIdentifier(category.getId());
      }
    }
    itemAdapter.add(skillsItems);
    //so the headers are aware of changes
    stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onChanged() {
        decoration.invalidateHeaders();
      }
    });
    fastAdapter.withOnClickListener(new OnClickListener<SkillsItem>() {
      @Override
      public boolean onClick(View v, @NonNull IAdapter<SkillsItem> adapter, @NonNull SkillsItem item, int position) {
        if (!isUserSkillDuplicate(item)) {
          if (callbackListener != null) {
            callbackListener.sendResult(item);
            closeDialog();
          }
        } else {
          Toast.makeText(activity, getString(R.string.skill_exists_warning), Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    });

  }

  private boolean isUserSkillDuplicate(SkillsItem item) {
    UserSkill userSkill = DatabaseUtil.getUserSkillWithUUID(userSkillBox, item.getUuid());
    return userSkill != null;
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
  void closeDialog() {
    dismiss();
    if (getFragmentManager() != null) {
      getFragmentManager().popBackStackImmediate();
    }
  }

  void setCallbackListener(CallbackResult callbackResult) {
    this.callbackListener = callbackResult;

  }
}