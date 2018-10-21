package com.github.bkhezry.learn2learn.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.StickyHeaderAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SelectSkillActivity extends BaseActivity {
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private Box<SkillsItem> skillsItemBox;
  private Box<Category> categoryBox;
  private Box<UserSkill> userSkillBox;
  private FastAdapter<SkillsItem> fastAdapter;

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
    //create our adapters
    final StickyHeaderAdapter<SkillsItem> stickyHeaderAdapter = new StickyHeaderAdapter<>();
    final ItemAdapter headerAdapter = new ItemAdapter();
    final ItemAdapter<SkillsItem> itemAdapter = new ItemAdapter<>();
    fastAdapter = FastAdapter.with(Arrays.asList(headerAdapter, itemAdapter));
    fastAdapter.withSelectable(true);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(stickyHeaderAdapter.wrap(fastAdapter));
    //this adds the Sticky Headers within our list
    final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
    recyclerView.addItemDecoration(decoration);
    List<SkillsItem> skillsItems = skillsItemBox.getAll();
    if (AppUtil.isRTL(this)) {
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

        } else {
          Toast.makeText(SelectSkillActivity.this, getString(R.string.skill_exists_warning), Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    });

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