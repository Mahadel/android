package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.ConnectionRequest;
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
import io.objectbox.Box;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.bkhezry.learn2learn.util.AppUtil.dpToPx;

public class ConnectionRequestActivity extends BaseActivity {
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private Prefser prefser;
  private Dialog loadingDialog;
  private FastAdapter<ConnectionRequest> mFastAdapter;
  private ItemAdapter<ConnectionRequest> mItemAdapter;
  private Box<SkillsItem> skillsItemBox;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_connection_request);
    ButterKnife.bind(this);
    BoxStore boxStore = MyApplication.getBoxStore();
    skillsItemBox = boxStore.boxFor(SkillsItem.class);
    prefser = new Prefser(this);
    loadingDialog = AppUtil.getDialogLoading(this);
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6, getResources()), true));
    mItemAdapter = new ItemAdapter<>();
    mFastAdapter = FastAdapter.with(mItemAdapter);
    recyclerView.setAdapter(mFastAdapter);
    mFastAdapter.withOnPreClickListener(new OnClickListener<ConnectionRequest>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<ConnectionRequest> adapter, @NonNull ConnectionRequest item, int position) {
        return true;
      }
    });
    getConnectionRequests();
  }

  private void getConnectionRequests() {
    loadingDialog.show();
    final AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<List<ConnectionRequest>> call = apiService.getUserConnectionRequst(info.getUuid());
    call.enqueue(new Callback<List<ConnectionRequest>>() {
      @Override
      public void onResponse(@NonNull Call<List<ConnectionRequest>> call, @NonNull Response<List<ConnectionRequest>> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          List<ConnectionRequest> connectionRequests = response.body();
          if (connectionRequests != null) {
            mItemAdapter.clear();
            mItemAdapter.add(connectionRequests);
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<List<ConnectionRequest>> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();

      }
    });
  }

  private SkillsItem getSkill(String skillUuid) {
    return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();
  }
}

