package com.github.mahadel.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.listener.CallbackResult;
import com.github.mahadel.demo.listener.SkillDetailCallbackResult;
import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.ui.fragment.AboutFragment;
import com.github.mahadel.demo.ui.fragment.AddSkillFragment;
import com.github.mahadel.demo.ui.fragment.ProfileFragment;
import com.github.mahadel.demo.ui.fragment.SettingsFragment;
import com.github.mahadel.demo.ui.fragment.UserSkillDetailFragment;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.DatabaseUtil;
import com.github.mahadel.demo.util.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * MainActivity showing list of userSkill that submit to the server in two part
 * UI of adding new userSkill
 * Access to the menu of app
 * Search for user that match with user data
 */
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
  @BindView(R.id.layout_empty_1)
  LinearLayout layoutEmpty1;
  @BindView(R.id.layout_empty_2)
  LinearLayout layoutEmpty2;
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
    setSupportActionBar(bar);
    setUpDBAccess();
    setUpBottomDrawer();
    setUpBottomSheet();
    initNavigationView();
    initRecyclerViews();
    requestSkills();
    subscribeFirebaseTopic();
  }

  private void subscribeFirebaseTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic("update_app")
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Log.d("subscribe", "success");
            } else {
              Log.d("subscribe", "failed");
            }
          }
        });
  }

  private void setUpDBAccess() {
    BoxStore boxStore = MyApplication.getBoxStore();
    userSkillBox = boxStore.boxFor(UserSkill.class);
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

  private void setUpBottomSheet() {
    View contentFrameLayout = coordinatorLayout.findViewById(R.id.contentFrameLayout);
    bottomSheetBehavior = BottomSheetBehavior.from(contentFrameLayout);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  private void initNavigationView() {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            hideBottomDrawer();
            if (item.getItemId() == R.id.profile_item) {
              showProfileFragment();
            } else if (item.getItemId() == R.id.settings_item) {
              showSettingsFragment();

            } else if (item.getItemId() == R.id.about_item) {
              showAboutFragment();
            }
            return false;
          }
        });
  }

  /**
   * Setup recycler view for two type of userSkill
   */

  private void initRecyclerViews() {
    mItemAdapter_1 = new ItemAdapter<>();
    mFastAdapter_1 = FastAdapter.with(mItemAdapter_1);
    recyclerView_1.setAdapter(mFastAdapter_1);
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

  /**
   * Handle click of recycler view items
   *
   * @param v            {@link View}
   * @param recyclerView {@link RecyclerView}
   * @param item         {@link UserSkill}
   * @param skillType    {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   * @return boolean
   */

  private boolean handleRecyclerViewOnClick(View v, RecyclerView recyclerView, UserSkill item, AppUtil.SkillType skillType) {
    final CardSliderLayoutManager lm = (CardSliderLayoutManager) recyclerView.getLayoutManager();
    if (lm != null) {
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
    }
    return false;
  }

  /**
   * Showing data of userSkill that selected in fragment
   *
   * @param item      {@link UserSkill}
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
  private void showSkillDetail(UserSkill item, AppUtil.SkillType skillType) {
    Fragment fragment = createSkillDetailFragment(item, skillType);
    AppUtil.showFragmentInBottomSheet(fragment, getSupportFragmentManager());
    ExpandBottomSheet();
  }

  private void ExpandBottomSheet() {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
      }
    }, 100);
  }

  /**
   * Create fragment for showing userSkill detail
   *
   * @param item‌     {@link UserSkill}
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   * @return UserSkillDetailFragment
   */
  private UserSkillDetailFragment createSkillDetailFragment(UserSkill item, AppUtil.SkillType skillType) {
    UserSkillDetailFragment skillFragment = new UserSkillDetailFragment();
    skillFragment.setSkillType(skillType);
    skillFragment.setSkillItem(item);
    skillFragment.setOnCallbackResult(new SkillDetailCallbackResult() {
      @Override
      public void update(UserSkill userSkill, AppUtil.SkillType skillType) {
        updateUserSkill(userSkill, skillType);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }

      @Override
      public void remove(UserSkill userSkill, AppUtil.SkillType skillType) {
        removeUserSkill(userSkill, skillType);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }
    });
    return skillFragment;
  }

  /**
   * Remove userSkill from recycler view & local db
   *
   * @param userSkill {@link UserSkill}
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
  private void removeUserSkill(UserSkill userSkill, AppUtil.SkillType skillType) {
    UserSkill userSkill1DB = DatabaseUtil.getUserSkillWithUUID(userSkillBox, userSkill.getUuid());
    userSkillBox.remove(userSkill1DB);
    if (skillType == AppUtil.SkillType.WANT_TEACH) {
      mItemAdapter_1.remove(lastPositionClicked);
      if (mItemAdapter_1.getAdapterItemCount() == 0) {
        showEmptyLayout_1();
      }
    } else {
      mItemAdapter_2.remove(lastPositionClicked);
      if (mItemAdapter_2.getAdapterItemCount() == 0) {
        showEmptyLayout_2();
      }
    }

  }

  /**
   * Update userSkill data in recycler view
   *
   * @param userSkill {@link UserSkill}
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
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

  /**
   * Show add skill dialog with selected type of skill
   *
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
  private void showAddSkillDialog(AppUtil.SkillType skillType) {
    Fragment fragment = createAddSkillFragment(skillType);
    AppUtil.showFragmentInBottomSheet(fragment, getSupportFragmentManager());
    ExpandBottomSheet();
  }

  /**
   * Create fragment of add skill
   *
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   * @return AddSkillFragment {@link Fragment}
   */
  private AddSkillFragment createAddSkillFragment(AppUtil.SkillType skillType) {
    AddSkillFragment skillFragment = new AddSkillFragment();
    skillFragment.setSkillType(skillType);
    skillFragment.setOnCallbackResult(new CallbackResult() {
      @Override
      public void sendResult(UserSkill userSkill, AppUtil.SkillType skillType) {
        handleAddedUserSkill(userSkill, skillType);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      }
    });
    return skillFragment;
  }

  private void showProfileFragment() {
    AppUtil.showFragment(new ProfileFragment(), getSupportFragmentManager());
  }

  private void showSettingsFragment() {
    AppUtil.showFragment(new SettingsFragment(), getSupportFragmentManager());
  }

  private void showAboutFragment() {
    AppUtil.showFragment(new AboutFragment(), getSupportFragmentManager());
  }

  /**
   * Add userSkill to recycler view
   *
   * @param userSkill {@link UserSkill}
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType}
   */
  private void handleAddedUserSkill(UserSkill userSkill, AppUtil.SkillType skillType) {
    userSkillBox.put(userSkill);
    if (skillType == AppUtil.SkillType.WANT_TEACH) {
      mItemAdapter_1.add(0, userSkill);
      mFastAdapter_1.notifyAdapterItemChanged(0);
      hideEmptyLayout_1();
    } else {
      hideEmptyLayout_2();
      mItemAdapter_2.add(0, userSkill);
      mFastAdapter_2.notifyAdapterItemChanged(0);
    }
  }


  /**
   * Get userSkills from local db and showing in recycler views
   */
  private void requestSkills() {
    mItemAdapter_1.clear();
    List<UserSkill> userSkills1 = DatabaseUtil.getUserSkillWithType(userSkillBox, 1).find();
    mItemAdapter_1.add(userSkills1);
    if (userSkills1.size() == 0) {
      showEmptyLayout_1();
    } else {
      hideEmptyLayout_1();
    }
    mItemAdapter_2.clear();
    List<UserSkill> userSkills2 = DatabaseUtil.getUserSkillWithType(userSkillBox, 2).find();
    mItemAdapter_2.add(userSkills2);
    if (userSkills2.size() == 0) {
      showEmptyLayout_2();
    } else {
      hideEmptyLayout_2();

    }
  }

  private void showEmptyLayout_1() {
    recyclerView_1.setVisibility(View.GONE);
    layoutEmpty1.setVisibility(View.VISIBLE);
  }

  private void hideEmptyLayout_1() {
    recyclerView_1.setVisibility(View.VISIBLE);
    layoutEmpty1.setVisibility(View.GONE);
  }


  private void showEmptyLayout_2() {
    recyclerView_2.setVisibility(View.GONE);
    layoutEmpty2.setVisibility(View.VISIBLE);
  }

  private void hideEmptyLayout_2() {
    recyclerView_2.setVisibility(View.VISIBLE);
    layoutEmpty2.setVisibility(View.GONE);
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
    if (menuItem.getItemId() == R.id.search_item) {
      startActivity(new Intent(this, SearchActivity.class));
    } else {
      startActivity(new Intent(this, ConnectionRequestActivity.class));
    }
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