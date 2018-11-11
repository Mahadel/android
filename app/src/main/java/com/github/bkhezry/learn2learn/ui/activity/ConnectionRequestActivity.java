package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.ConnectionReceiveItem;
import com.github.bkhezry.learn2learn.model.ConnectionRequest;
import com.github.bkhezry.learn2learn.model.ConnectionSendItem;
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
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.bkhezry.learn2learn.util.AppUtil.dpToPx;

public class ConnectionRequestActivity extends BaseActivity {
  private static final int SENT_CONNECTION = 1;
  private static final int RECEIVED_CONNECTION = 2;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private Prefser prefser;
  private Dialog loadingDialog;
  private FastAdapter<ConnectionSendItem> mFastAdapterConnectionSend;
  private ItemAdapter<ConnectionSendItem> mItemAdapterConnectionSend;
  private FastAdapter<ConnectionReceiveItem> mFastAdapterConnectionReceive;
  private ItemAdapter<ConnectionReceiveItem> mItemAdapterConnectionReceive;
  private ConnectionRequest connectionRequest;
  private int currentConnectionType = SENT_CONNECTION;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_connection_request);
    ButterKnife.bind(this);
    prefser = new Prefser(this);
    loadingDialog = AppUtil.getDialogLoading(this);
    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6, getResources()), true));
    mItemAdapterConnectionSend = new ItemAdapter<>();
    mFastAdapterConnectionSend = FastAdapter.with(mItemAdapterConnectionSend);
    mFastAdapterConnectionSend.withOnPreClickListener(new OnClickListener<ConnectionSendItem>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<ConnectionSendItem> adapter, @NonNull ConnectionSendItem item, int position) {
        return true;
      }
    });
    mItemAdapterConnectionReceive = new ItemAdapter<>();
    mFastAdapterConnectionReceive = FastAdapter.with(mItemAdapterConnectionReceive);
    mFastAdapterConnectionReceive.withOnPreClickListener(new OnClickListener<ConnectionReceiveItem>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<ConnectionReceiveItem> adapter, @NonNull ConnectionReceiveItem item, int position) {
        return true;
      }
    });
    getConnectionRequests();
  }

  private void getConnectionRequests() {
    loadingDialog.show();
    final AuthenticationInfo info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ConnectionRequest> call = apiService.getUserConnectionRequst(info.getUuid());
    call.enqueue(new Callback<ConnectionRequest>() {
      @Override
      public void onResponse(@NonNull Call<ConnectionRequest> call, @NonNull Response<ConnectionRequest> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          connectionRequest = response.body();
          if (connectionRequest != null) {
            displayConnectionSend(connectionRequest.getConnectionSend());
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<ConnectionRequest> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();

      }
    });
  }

  private void displayConnectionSend(List<ConnectionSendItem> connectionSend) {
    recyclerView.setAdapter(mFastAdapterConnectionSend);
    mItemAdapterConnectionSend.clear();
    mItemAdapterConnectionSend.add(connectionSend);
  }

  private void displayConnectionReceive(List<ConnectionReceiveItem> connectionReceiveItems) {
    recyclerView.setAdapter(mFastAdapterConnectionReceive);
    mItemAdapterConnectionReceive.clear();
    mItemAdapterConnectionReceive.add(connectionReceiveItems);
  }

  @OnClick({R.id.sent_layout, R.id.received_layout})
  public void handleBottomNavigationClick(View view) {
    switch (view.getId()) {
      case R.id.sent_layout:
        if (currentConnectionType != SENT_CONNECTION) {
          displayConnectionSend(connectionRequest.getConnectionSend());
          currentConnectionType = SENT_CONNECTION;
        }
        break;
      case R.id.received_layout:
        if (currentConnectionType != RECEIVED_CONNECTION) {
          displayConnectionReceive(connectionRequest.getConnectionReceive());
          currentConnectionType = RECEIVED_CONNECTION;
        }
        break;
    }
  }
}

