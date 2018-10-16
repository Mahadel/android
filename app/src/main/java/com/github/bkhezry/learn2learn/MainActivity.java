package com.github.bkhezry.learn2learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.bkhezry.learn2learn.listener.CallbackResult;
import com.github.bkhezry.learn2learn.listener.SkillDetailCallbackResult;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.ui.fragment.AboutFragment;
import com.github.bkhezry.learn2learn.ui.fragment.AddSkillFragment;
import com.github.bkhezry.learn2learn.ui.fragment.ProfileFragment;
import com.github.bkhezry.learn2learn.ui.fragment.SettingsFragment;
import com.github.bkhezry.learn2learn.ui.fragment.SkillDetailFragment;
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
import androidx.fragment.app.Fragment;
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
  @BindView(R.id.contentFrameLayout)
  View contentFrameLayout;
  private BottomSheetBehavior<View> bottomDrawerBehavior;
  private FastAdapter<UserSkill> mFastAdapter_1;
  private ItemAdapter<UserSkill> mItemAdapter_1;
  private FastAdapter<UserSkill> mFastAdapter_2;
  private ItemAdapter<UserSkill> mItemAdapter_2;
  private Box<UserSkill> userSkillBox;
  private int lastPositionClicked;
  private BottomSheetBehavior bottomSheetBehavior;

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
    setUpBottomSheet();
    initNavigationView();
    initRecyclerViews();
    requestSkills();
  }

  private void setUpBottomSheet() {
    bottomSheetBehavior = BottomSheetBehavior.from(contentFrameLayout);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  private void initNavigationView() {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.profile_item) {
              showProfileFragment();
              hideBottomDrawer();
            } else if (item.getItemId() == R.id.settings_item) {
              showSettingsFragment();
              hideBottomDrawer();
            } else if (item.getItemId() == R.id.about_item) {
              showAboutFragment();
            }
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
    bar.replaceMenu(R.menu.menu_primary);
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
        lastPositionClicked = position;
        return handleRecyclerViewOnClick(v, recyclerView_1, item, AppUtil.SkillType.WANT_TEACH);
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
        lastPositionClicked = position;
        return handleRecyclerViewOnClick(v, recyclerView_2, item, AppUtil.SkillType.WANT_LEARN);
      }
    });
    handleLocaleDirection();
  }

  private boolean handleRecyclerViewOnClick(View v, RecyclerView recyclerView, UserSkill item, AppUtil.SkillType skillType) {
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
      showSkillDetail(item, skillType);
    } else if (clickedPosition > activeCardPosition) {
      recyclerView.smoothScrollToPosition(clickedPosition);
    }
    return false;
  }

  private void showSkillDetail(UserSkill item, AppUtil.SkillType skillType) {
    Fragment fragment = createSkillDetailFragment(item, skillType);
    AppUtil.showFragmentInBottomSheet(fragment, getSupportFragmentManager());
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  }

  private SkillDetailFragment createSkillDetailFragment(UserSkill item, AppUtil.SkillType skillType) {
    SkillDetailFragment skillFragment = new SkillDetailFragment();
    skillFragment.setSkillType(skillType);
    skillFragment.setSkillItem(item);
    skillFragment.setOnCallbackResult(new SkillDetailCallbackResult() {
      @Override
      public void update(Object obj, AppUtil.SkillType skillType) {
        UserSkill userSkill = (UserSkill) obj;
        updateUserSkill(userSkill, skillType);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }

      @Override
      public void remove(Object obj, AppUtil.SkillType skillType) {
        UserSkill userSkill = (UserSkill) obj;
        removeUserSkill(userSkill, skillType);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }
    });
    return skillFragment;
  }

  private void removeUserSkill(UserSkill userSkill, AppUtil.SkillType skillType) {
    UserSkill userSkill1DB = DatabaseUtil.getUserSkillWithUUID(userSkillBox, userSkill.getUuid());
    userSkillBox.remove(userSkill1DB);
    if (skillType == AppUtil.SkillType.WANT_TEACH) {
      mItemAdapter_1.remove(lastPositionClicked);
    } else {
      mItemAdapter_2.remove(lastPositionClicked);
    }

  }

  private void updateUserSkill(UserSkill userSkill, AppUtil.SkillType skillType) {
    UserSkill userSkill1DB = DatabaseUtil.getUserSkillWithUUID(userSkillBox, userSkill.getUuid());
    userSkill1DB.setDescription(userSkill.getDescription());
    userSkill1DB.setUpdatedAt(userSkill.getUpdatedAt());
    userSkillBox.put(userSkill1DB);
    if (skillType == AppUtil.SkillType.WANT_TEACH) {
      mItemAdapter_1.remove(lastPositionClicked);
      mItemAdapter_1.add(lastPositionClicked, userSkill1DB);
      mFastAdapter_1.notifyAdapterItemChanged(lastPositionClicked);
    } else {
      mItemAdapter_2.remove(lastPositionClicked);
      mItemAdapter_2.add(lastPositionClicked, userSkill1DB);
      mFastAdapter_2.notifyAdapterItemChanged(lastPositionClicked);
    }
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
    Fragment fragment = createAddSkillFragment(skillType);
    AppUtil.showFragmentInBottomSheet(fragment, getSupportFragmentManager());
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  }

  private AddSkillFragment createAddSkillFragment(AppUtil.SkillType skillType) {
    AddSkillFragment skillFragment = new AddSkillFragment();
    skillFragment.setSkillType(skillType);
    skillFragment.setOnCallbackResult(new CallbackResult() {
      @Override
      public void sendResult(Object obj, AppUtil.SkillType skillType) {
        //TODO check return object from fragment and isAdd to DB or get it.
        UserSkill userSkill = (UserSkill) obj;
        handleUserSkill(userSkill);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }
    });
    return skillFragment;
  }

  private void showProfileFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    ProfileFragment profileFragment = new ProfileFragment();
    profileFragment.setOnCallbackResult(new ProfileFragment.ProfileCallbackResult() {
      @Override
      public void logout() {
        startActivity(new Intent(MainActivity.this, LauncherActivity.class));
        finish();
      }
    });
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, profileFragment).addToBackStack(null).commit();
  }

  private void showSettingsFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    SettingsFragment profileFragment = new SettingsFragment();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, profileFragment).addToBackStack(null).commit();
  }

  private void showAboutFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    AboutFragment profileFragment = new AboutFragment();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.add(android.R.id.content, profileFragment).addToBackStack(null).commit();
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
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_appbar, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
    return true;
  }

  @Override
  public void onBackPressed() {
    if (bottomDrawerBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN || bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
      hideBottomDrawer();
    } else {
      super.onBackPressed();
    }
  }
}