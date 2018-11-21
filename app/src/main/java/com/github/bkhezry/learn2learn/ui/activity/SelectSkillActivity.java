package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.Category;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.GridSpacingItemDecoration;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static com.github.bkhezry.learn2learn.util.AppUtil.dpToPx;

public class SelectSkillActivity extends BaseActivity {
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private Box<SkillsItem> skillsItemBox;
  private Box<Category> categoryBox;
  private Box<UserSkill> userSkillBox;
  private FastAdapter<SkillsItem> mFastAdapterSkill;
  private ItemAdapter<SkillsItem> mItemAdapterSkill;
  private FastAdapter<Category> mFastAdapterCategory;
  private ItemAdapter<Category> mItemAdapterCategory;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_select_skill);
    ButterKnife.bind(this);
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    categoryBox = boxStore.boxFor(Category.class);
    userSkillBox = boxStore.boxFor(UserSkill.class);
    initRecyclerViews();
  }

  private void initRecyclerViews() {
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(6, getResources()), true));
    mItemAdapterCategory = new ItemAdapter<>();
    mFastAdapterCategory = FastAdapter.with(mItemAdapterCategory);
    recyclerView.setAdapter(mFastAdapterCategory);
    mItemAdapterSkill = new ItemAdapter<>();
    mFastAdapterSkill = FastAdapter.with(mItemAdapterSkill);
    mFastAdapterCategory.withOnClickListener(new OnClickListener<Category>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<Category> adapter, @NonNull Category item, int position) {
        getSkills(item);
        return true;
      }
    });
    mFastAdapterSkill.withOnClickListener(new OnClickListener<SkillsItem>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<SkillsItem> adapter, @NonNull SkillsItem item, int position) {
        returnResult(item);
        return true;
      }
    });
    getCategory();
  }

  private void getSkills(Category item) {
    List<SkillsItem> skillsItems = DatabaseUtil.getSkillItemOfCategory(skillsItemBox, item.getUuid());
    recyclerView.setAdapter(mFastAdapterSkill);
    mItemAdapterSkill.clear();
    mItemAdapterSkill.add(skillsItems);
  }

  private void getCategory() {
    List<Category> categories = categoryBox.getAll();
    mItemAdapterCategory.clear();
    mItemAdapterCategory.add(categories);
  }

  private void returnResult(@NonNull SkillsItem item) {
    if (!isUserSkillDuplicate(item)) {
      Intent resultIntent = new Intent();
      resultIntent.putExtra(Constant.SKILL_ITEM, item);
      setResult(Activity.RESULT_OK, resultIntent);
      finish();
    } else {
      Toast.makeText(SelectSkillActivity.this, getString(R.string.skill_exists_warning), Toast.LENGTH_SHORT).show();
    }
  }

  private boolean isUserSkillDuplicate(SkillsItem item) {
    UserSkill userSkill = DatabaseUtil.getUserSkillWithSkillUUID(userSkillBox, item.getUuid());
    return userSkill != null;
  }


  @OnClick(R.id.close_image_view)
  void closeDialog() {
    finish();
  }
}