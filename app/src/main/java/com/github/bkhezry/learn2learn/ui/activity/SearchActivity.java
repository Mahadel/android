package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.SearchResult;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.GridSpacingItemDecoration;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.bkhezry.learn2learn.util.AppUtil.dpToPx;

public class SearchActivity extends BaseActivity {
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
  private Prefser prefser;
  private Dialog loadingDialog;
  private FastAdapter<SearchResult> mFastAdapter;
  private ItemAdapter<SearchResult> mItemAdapter;
  private BottomSheetBehavior bottomSheetBehavior;
  private Box<SkillsItem> skillsItemBox;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    setUpBottomSheet();
    prefser = new Prefser(this);
    loadingDialog = AppUtil.getDialogLoading(this);
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
    mFastAdapter.withEventHook(new SearchResult.RequestButtonClickEvent(new SearchResult.DoClickListener() {
      @Override
      public void requestEmail(SearchResult item) {
        nameTextView.setText(String.format("%s %s", item.getUser().getFirstName(), item.getUser().getLastName()));
        if (AppUtil.isRTL(SearchActivity.this)) {
          teachSkillNameTextView.setText(getSkill(item.getTeachSkillUuid()).getFaName());
          learnSkillNameTextView.setText(getSkill(item.getLearnSkillUuid()).getFaName());
        } else {
          teachSkillNameTextView.setText(getSkill(item.getTeachSkillUuid()).getEnName());
          learnSkillNameTextView.setText(getSkill(item.getLearnSkillUuid()).getEnName());
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      }
    }));
    doSearch();
  }

  private void setUpBottomSheet() {
    bottomSheetBehavior = BottomSheetBehavior.from(requestLayout);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  private void doSearch() {
    loadingDialog.show();
    final AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<List<SearchResult>> call = apiService.search(info.getUuid());
    call.enqueue(new Callback<List<SearchResult>>() {
      @Override
      public void onResponse(@NonNull Call<List<SearchResult>> call, @NonNull Response<List<SearchResult>> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          List<SearchResult> searchResults = response.body();
          if (searchResults != null) {
            mItemAdapter.clear();
            mItemAdapter.add(searchResults);
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<SearchResult>> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
      }
    });
  }

  private SkillsItem getSkill(String skillUuid) {
    return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();
  }
}

