package com.github.bkhezry.learn2learn;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.model.Skill;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.recycler_view_1)
  RecyclerView recyclerView_1;
  @BindView(R.id.recycler_view_2)
  RecyclerView recyclerView_2;
  @BindView(R.id.bar)
  BottomAppBar bar;
  @BindView(R.id.fab)
  FloatingActionButton fab;
  @BindView(R.id.navigation_view)
  NavigationView navigationView;
  @BindView(R.id.bottom_drawer)
  FrameLayout bottomDrawer;
  @BindView(R.id.coordinator_layout)
  CoordinatorLayout coordinatorLayout;
  private BottomSheetBehavior<View> bottomDrawerBehavior;
  private FastAdapter<Skill> mFastAdapter_1;
  private ItemAdapter<Skill> mItemAdapter_1;
  private FastAdapter<Skill> mFastAdapter_2;
  private ItemAdapter<Skill> mItemAdapter_2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(Color.parseColor("#000000"));
    }
    setSupportActionBar(bar);
    setUpBottomDrawer();

    fab.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(MainActivity.this, fab.getContentDescription(), Toast.LENGTH_SHORT).show();
        }
      });
    final NavigationView navigationView = findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(
      new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
          return false;
        }
      });
    int[] ATTRS = new int[] {android.R.attr.listDivider};

    TypedArray a = MainActivity.this.obtainStyledAttributes(ATTRS);
    Drawable divider = a.getDrawable(0);
    int inset = getResources().getDimensionPixelSize(R.dimen.item_space);
    InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
    a.recycle();
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_1.getContext(),
      DividerItemDecoration.HORIZONTAL);
    dividerItemDecoration.setDrawable(insetDivider);
    RecyclerView.LayoutManager mLayoutManager_1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    RecyclerView.LayoutManager mLayoutManager_2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    recyclerView_1.setLayoutManager(mLayoutManager_1);
    mItemAdapter_1 = new ItemAdapter<>();
    mFastAdapter_1 = FastAdapter.with(mItemAdapter_1);
    recyclerView_1.setAdapter(mFastAdapter_1);
    recyclerView_1.addItemDecoration(dividerItemDecoration);

    recyclerView_2.setLayoutManager(mLayoutManager_2);
    mItemAdapter_2 = new ItemAdapter<>();
    mFastAdapter_2 = FastAdapter.with(mItemAdapter_2);
    recyclerView_2.setAdapter(mFastAdapter_2);
    recyclerView_2.addItemDecoration(dividerItemDecoration);
    requestSkills();
  }

  private void requestSkills() {
    List<Skill> skillList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Skill skill = new Skill();
      skill.setName(String.valueOf(i));
      skillList.add(skill);
    }
    mItemAdapter_1.clear();
    mItemAdapter_1.add(skillList);

    mItemAdapter_2.clear();
    mItemAdapter_2.add(skillList);
  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.demo_primary, menu);
//    return super.onCreateOptionsMenu(menu);
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem menuItem) {
//    Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//    return true;
//  }

  protected void setUpBottomDrawer() {
    View bottomDrawer = coordinatorLayout.findViewById(R.id.bottom_drawer);
    bottomDrawerBehavior = BottomSheetBehavior.from(bottomDrawer);
    bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    bar.setNavigationOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
      });
    bar.setNavigationIcon(R.drawable.ic_drawer_menu_24px);
    bar.replaceMenu(R.menu.demo_primary);
  }
}

