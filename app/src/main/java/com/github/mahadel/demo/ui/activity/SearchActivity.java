package com.github.mahadel.demo.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.model.SearchResult;
import com.github.mahadel.demo.model.SkillsItem;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.FirebaseEventLog;
import com.github.mahadel.demo.util.GridSpacingItemDecoration;
import com.github.mahadel.demo.util.MyApplication;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.mahadel.demo.util.AppUtil.dpToPx;

/**
 * SearchActivity searching for user skill matching with user
 * Send connection request to the other users
 */
public class SearchActivity extends BaseActivity {
  private static final String TAG = "SearchActivity";
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.requestLayout)
  FrameLayout requestLayout;
  @BindView(R.id.name_text_view)
  AppCompatTextView nameTextView;
  @BindView(R.id.teach_skill_name_text_view)
  AppCompatTextView teachSkillNameTextView;
  @BindView(R.id.learn_skill_name_text_view)
  AppCompatTextView learnSkillNameTextView;
  @BindView(R.id.request_description_edit_text)
  AppCompatEditText requestDescriptionEditText;
  @BindView(R.id.layout_empty)
  LinearLayout layoutEmpty;
  @BindView(R.id.search_again_layout)
  LinearLayout searchAgainLayout;
  private Dialog loadingDialog;
  private FastAdapter<SearchResult> mFastAdapter;
  private ItemAdapter<SearchResult> mItemAdapter;
  private BottomSheetBehavior bottomSheetBehavior;
  private Box<SkillsItem> skillsItemBox;
  private SearchResult selectedResult;
  private AuthenticationInfo info;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    initVariables();
    setUpBottomSheet();
    initRecyclerView();
    searching();
    AppUtil.hideSoftInput(this);
  }

  /**
   * Setup init values of variables
   */
  private void initVariables() {
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    Prefser prefser = new Prefser(this);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    loadingDialog = AppUtil.getLoadingDialog(this);
  }

  private void setUpBottomSheet() {
    bottomSheetBehavior = BottomSheetBehavior.from(requestLayout);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  /**
   * Setup recycler view of searched items
   */
  private void initRecyclerView() {
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6, getResources()), true));
    mItemAdapter = new ItemAdapter<>();
    mFastAdapter = FastAdapter.with(mItemAdapter);
    recyclerView.setAdapter(mFastAdapter);
    mFastAdapter.withOnPreClickListener(new OnClickListener<SearchResult>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<SearchResult> adapter, @NonNull SearchResult item, int position) {
        return true;
      }
    });
    mFastAdapter.withEventHook(new SearchResult.RequestButtonClickEvent(new SearchResult.RequestClickListener() {
      @Override
      public void requestEmail(SearchResult item) {
        selectedResult = item;
        nameTextView.setText(String.format("%s %s", item.getUser().getFirstName(), item.getUser().getLastName()));
        if (AppUtil.isRTL(SearchActivity.this)) {
          teachSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getTeachSkillUuid()).getFaName());
          learnSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getLearnSkillUuid()).getFaName());
        } else {
          teachSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getTeachSkillUuid()).getEnName());
          learnSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getLearnSkillUuid()).getEnName());
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      }
    }));
  }

  /**
   * Searching for matched userSkill from server
   */
  private void searching() {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<List<SearchResult>> call = apiService.search(info.getUuid());
    call.enqueue(new Callback<List<SearchResult>>() {
      @Override
      public void onResponse(@NonNull Call<List<SearchResult>> call, @NonNull Response<List<SearchResult>> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          List<SearchResult> searchResults = response.body();
          if (searchResults != null) {
            handleSearchResults(searchResults);
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<SearchResult>> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        AppUtil.showSnackbar(recyclerView, getString(R.string.error_request_message), SearchActivity.this, SnackbarUtils.LENGTH_LONG);
        showAgainLayout();
        FirebaseEventLog.log("server_failure", TAG, "searching", t.getMessage());
      }
    });
  }

  /**
   * Showing search result items in recycler view
   *
   * @param searchResults List of {@link SearchResult}
   */
  private void handleSearchResults(List<SearchResult> searchResults) {
    if (searchResults.size() != 0) {
      mItemAdapter.clear();
      mItemAdapter.add(searchResults);
      showRecyclerView();
    } else {
      showEmptyLayout();
    }
  }

  private void showRecyclerView() {
    layoutEmpty.setVisibility(View.GONE);
    searchAgainLayout.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
  }

  private void showEmptyLayout() {
    layoutEmpty.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
    searchAgainLayout.setVisibility(View.GONE);
  }

  private void showAgainLayout() {
    recyclerView.setVisibility(View.GONE);
    layoutEmpty.setVisibility(View.GONE);
    searchAgainLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public void onBackPressed() {
    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    } else {
      super.onBackPressed();
    }
  }

  /**
   * Submit request connection to server
   *
   * @param view {@link View}
   */
  @OnClick(R.id.submit_btn)
  public void submitRequestConnection(final View view) {
    if (NetworkUtils.isConnected()) {
      requestConnection(view);
    } else {
      AppUtil.showSnackbar(view, getString(R.string.no_internet_label), this, SnackbarUtils.LENGTH_LONG);
    }
  }

  private void requestConnection(View view) {
    loadingDialog.show();
    AppUtil.hideSoftInput(SearchActivity.this);
    String description = requestDescriptionEditText.getText().toString();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ResponseMessage> call = apiService.requestConnection(
        info.getUuid(),
        selectedResult.getUser().getUuid(),
        selectedResult.getLearnSkillUuid(),
        selectedResult.getTeachSkillUuid(),
        description);
    call.enqueue(new Callback<ResponseMessage>() {
      @Override
      public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
          AppUtil.showSnackbar(view, getString(R.string.request_sent_success), SearchActivity.this, SnackbarUtils.LENGTH_INDEFINITE);
        } else if (response.code() == 400) {
          AppUtil.showSnackbar(view, getString(R.string.request_duplicate), SearchActivity.this, SnackbarUtils.LENGTH_INDEFINITE);
        }

      }

      @Override
      public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
        FirebaseEventLog.log("server_failure", TAG, "requestConnection", t.getMessage());
      }
    });
  }

  @OnClick(R.id.close_image_view)
  public void close() {
    finish();
  }

  @OnClick(R.id.retry_button)
  public void retry() {
    searching();
  }
}

