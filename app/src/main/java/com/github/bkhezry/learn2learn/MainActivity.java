package com.github.bkhezry.learn2learn;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.model.Skill;
import com.github.bkhezry.learn2learn.ui.fragment.DialogAddSkillFragment;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.ramotion.cardslider.CardSnapHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

  private static final int DIALOG_QUEST_CODE = 1001;
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
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    setSupportActionBar(bar);
    setUpBottomDrawer();
    initNavigationView();
    initRecyclerViews();
    requestSkills();
  }

  private void initNavigationView() {
    navigationView.setNavigationItemSelectedListener(
      new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
          return false;
        }
      });
  }

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

  private void initRecyclerViews() {
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_1.getContext(),
      DividerItemDecoration.HORIZONTAL);
    dividerItemDecoration.setDrawable(AppUtil.getInsetDrawable(this));

    mItemAdapter_1 = new ItemAdapter<>();
    mFastAdapter_1 = FastAdapter.with(mItemAdapter_1);
    recyclerView_1.setAdapter(mFastAdapter_1);
    recyclerView_1.addItemDecoration(dividerItemDecoration);
    new CardSnapHelper().attachToRecyclerView(recyclerView_1);

    mItemAdapter_2 = new ItemAdapter<>();
    mFastAdapter_2 = FastAdapter.with(mItemAdapter_2);
    recyclerView_2.setAdapter(mFastAdapter_2);
    recyclerView_2.addItemDecoration(dividerItemDecoration);
    new CardSnapHelper().attachToRecyclerView(recyclerView_2);

    handleLocaleDirection();
  }

  private void handleLocaleDirection() {
    if (AppUtil.isRTL(ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0))) {
      AppUtil.rotateYView(recyclerView_1, 180);
      AppUtil.rotateYView(recyclerView_2, 180);
    }
  }

  @OnClick(R.id.fab)
  void fabClick() {
    AppUtil.showSkillTypeDialog(this, new AppUtil.DialogClickListener() {

      @Override
      public void selectedSkillType(AppUtil.SkillType skillType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogAddSkillFragment skillFragment = new DialogAddSkillFragment();
        skillFragment.setRequestCode(DIALOG_QUEST_CODE);
        skillFragment.setSkillType(skillType);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, skillFragment).addToBackStack(null).commit();
        skillFragment.setOnCallbackResult(new DialogAddSkillFragment.CallbackResult() {
          @Override
          public void sendResult(int requestCode, Object obj) {
            if (requestCode == DIALOG_QUEST_CODE) {

            }
          }
        });
      }
    });

  }


  private void requestSkills() {
    List<Skill> skillList = AppUtil.getSkills();
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
}

