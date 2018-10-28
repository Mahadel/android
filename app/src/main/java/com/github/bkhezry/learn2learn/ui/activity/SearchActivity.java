package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.SearchResult;
import com.github.bkhezry.learn2learn.service.APIService;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.Constant;
import com.github.bkhezry.learn2learn.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.mikepenz.fastadapter.FastAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
  private Prefser prefser;
  private Dialog loadingDialog;
  private FastAdapter<SearchResult> fastAdapter;

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

