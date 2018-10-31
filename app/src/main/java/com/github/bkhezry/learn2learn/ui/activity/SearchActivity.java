package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.SearchResult;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.GridSpacingItemDecoration;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private Prefser prefser;
  private Dialog loadingDialog;
  private FastAdapter<SearchResult> mFastAdapter;
  private ItemAdapter<SearchResult> mItemAdapter;

  public static int dpToPx(int dp, Resources r) {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
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


    doSearch();
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
          mItemAdapter.clear();
          mItemAdapter.add(searchResults);
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<SearchResult>> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();
      }
    });
  }
}

