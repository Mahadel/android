package com.github.bkhezry.learn2learn;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.listener.CallbackResult;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.ui.fragment.DialogAddSkillFragment;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MainActivity extends BaseActivity {

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
  private FastAdapter<UserSkill> mFastAdapter_1;
  private ItemAdapter<UserSkill> mItemAdapter_1;
  private FastAdapter<UserSkill> mFastAdapter_2;
  private ItemAdapter<UserSkill> mItemAdapter_2;
  private Box<UserSkill> userSkillBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    BoxStore boxStore = MyApplication.getBoxStore();
    userSkillBox = boxStore.boxFor(UserSkill.class);
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
    mFastAdapter_1.withOnClickListener(new OnClickListener<UserSkill>() {
      @Override
      public boolean onClick(View v, @NonNull IAdapter<UserSkill> adapter, @NonNull UserSkill item, int position) {
        return handleRecyclerViewOnClick(v, recyclerView_1);
      }
    });

    mItemAdapter_2 = new ItemAdapter<>();
    mFastAdapter_2 = FastAdapter.with(mItemAdapter_2);
    recyclerView_2.setAdapter(mFastAdapter_2);
    recyclerView_2.addItemDecoration(dividerItemDecoration);
    new CardSnapHelper().attachToRecyclerView(recyclerView_2);
    mFastAdapter_2.withOnClickListener(new OnClickListener<UserSkill>() {
      @Override
      public boolean onClick(View v, @NonNull IAdapter<UserSkill> adapter, @NonNull UserSkill item, int position) {
        return handleRecyclerViewOnClick(v, recyclerView_2);
      }
    });
    handleLocaleDirection();
  }

  private boolean handleRecyclerViewOnClick(View v, RecyclerView recyclerView) {
    final CardSliderLayoutManager lm = (CardSliderLayoutManager) recyclerView.getLayoutManager();
    if (lm.isSmoothScrolling()) {
      return false;
    }
    final int activeCardPosition = lm.getActiveCardPosition();
    if (activeCardPosition == RecyclerView.NO_POSITION) {
      return false;
    }
    final int clickedPosition = recyclerView.getChildAdapterPosition(v);
    if (clickedPosition == activeCardPosition) {
      Toast.makeText(MainActivity.this, "Click", Toast.LENGTH_SHORT).show();

    } else if (clickedPosition > activeCardPosition) {
      recyclerView.smoothScrollToPosition(clickedPosition);
    }
    return false;
  }

  private void handleLocaleDirection() {
    if (AppUtil.isRTL(this)) {
      AppUtil.rotateYView(recyclerView_1, 180);
      AppUtil.rotateYView(recyclerView_2, 180);
    }
  }

  @OnClick(R.id.fab)
  void fabClick() {
    AppUtil.showSkillTypeDialog(this, new AppUtil.DialogClickListener() {

      @Override
      public void selectedSkillType(AppUtil.SkillType skillType) {
        showAddSkillDialog(skillType);
      }
    });

  }

  private void showAddSkillDialog(AppUtil.SkillType skillType) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    DialogAddSkillFragment skillFragment = new DialogAddSkillFragment();
    skillFragment.setSkillType(skillType);
    skillFragment.setOnCallbackResult(new CallbackResult() {
      @Override
      public void sendResult(Object obj) {
        //TODO check return object from fragment and isAdd to DB or get it.
        UserSkill userSkill = (UserSkill) obj;
        handleUserSkill(userSkill);
      }
    });
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, skillFragment).addToBackStack(null).commit();
  }

  private void handleUserSkill(UserSkill userSkill) {
    userSkillBox.put(userSkill);
    if (userSkill.getSkillType() == 1) {
      mItemAdapter_1.add(0, userSkill);
    } else {
      mItemAdapter_2.add(0, userSkill);
    }
  }


  private void requestSkills() {
    mItemAdapter_1.clear();
    mItemAdapter_1.add(DatabaseUtil.getUserSkillWithType(userSkillBox, 1).find());

    mItemAdapter_2.clear();
    mItemAdapter_2.add(DatabaseUtil.getUserSkillWithType(userSkillBox, 2).find());
  }

  @OnClick(R.id.main_layout)
  public void hideBottomDrawer() {
    bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

