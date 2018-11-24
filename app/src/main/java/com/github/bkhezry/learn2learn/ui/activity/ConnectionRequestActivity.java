package com.github.bkhezry.learn2learn.ui.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.SnackbarUtils;
import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.ConnectionReceiveItem;
import com.github.bkhezry.learn2learn.model.ConnectionRequest;
import com.github.bkhezry.learn2learn.model.ConnectionSendItem;
import com.github.bkhezry.learn2learn.model.ResponseMessage;
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
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
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
  @BindView(R.id.sent_image_button)
  AppCompatImageButton sentImageButton;
  @BindView(R.id.sent_text_view)
  AppCompatTextView sentTextView;
  @BindView(R.id.received_image_button)
  AppCompatImageButton receivedImageButton;
  @BindView(R.id.received_text_view)
  AppCompatTextView receivedTextView;
  @BindView(R.id.layout_empty)
  LinearLayout layoutEmpty;
  private Prefser prefser;
  private Dialog loadingDialog;
  private FastAdapter<ConnectionSendItem> mFastAdapterConnectionSend;
  private ItemAdapter<ConnectionSendItem> mItemAdapterConnectionSend;
  private FastAdapter<ConnectionReceiveItem> mFastAdapterConnectionReceive;
  private ItemAdapter<ConnectionReceiveItem> mItemAdapterConnectionReceive;
  private ConnectionRequest connectionRequest;
  private int currentConnectionType = SENT_CONNECTION;
  private AuthenticationInfo info;

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
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
    setSentSelect();
    initRecyclerView();
    getConnectionRequests();
  }

  private void getConnectionRequests() {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ConnectionRequest> call = apiService.getUserConnectionRequest(info.getUuid());
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
    if (connectionSend.size() != 0) {
      recyclerView.setAdapter(mFastAdapterConnectionSend);
      mItemAdapterConnectionSend.clear();
      mItemAdapterConnectionSend.add(connectionSend);
      hideEmptyLayout();
    } else {
      showEmptyLayout();
    }
  }

  private void displayConnectionReceive(List<ConnectionReceiveItem> connectionReceiveItems) {
    if (connectionReceiveItems.size() != 0) {
      recyclerView.setAdapter(mFastAdapterConnectionReceive);
      mItemAdapterConnectionReceive.clear();
      mItemAdapterConnectionReceive.add(connectionReceiveItems);
      hideEmptyLayout();
    } else {
      showEmptyLayout();
    }
  }

  private void hideEmptyLayout() {
    layoutEmpty.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
  }

  private void showEmptyLayout() {
    layoutEmpty.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
  }

  private void initRecyclerView() {
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
    mFastAdapterConnectionSend.withEventHook(new ConnectionSendItem.DeleteButtonClickEvent(new ConnectionSendItem.HandleDeleteClickListener() {
      @Override
      public void delete(ConnectionSendItem item, int position) {
        deleteConnection(item, position);
      }
    }));
    mFastAdapterConnectionSend.withEventHook(new ConnectionSendItem.EmailButtonClickEvent(new ConnectionSendItem.HandleEmailClickListener() {
      @Override
      public void sendEmail(ConnectionSendItem item, int position) {
        if (item.getIsDelete() == 1 || item.getIsAccept() != 1) {
          AppUtil.showSnackbar(recyclerView, "ایمیل کاربر در دسترس نیست", ConnectionRequestActivity.this, SnackbarUtils.LENGTH_LONG);
        } else {
          sendMail(item);
        }
      }
    }));
    mItemAdapterConnectionReceive = new ItemAdapter<>();
    mFastAdapterConnectionReceive = FastAdapter.with(mItemAdapterConnectionReceive);
    mFastAdapterConnectionReceive.withOnPreClickListener(new OnClickListener<ConnectionReceiveItem>() {
      @Override
      public boolean onClick(@Nullable View v, @NonNull IAdapter<ConnectionReceiveItem> adapter, @NonNull ConnectionReceiveItem item, int position) {
        return true;
      }
    });
    mFastAdapterConnectionReceive.withEventHook(new ConnectionReceiveItem.AcceptButtonClickEvent(new ConnectionReceiveItem.HandleAcceptClickListener() {
      @Override
      public void accept(ConnectionReceiveItem item, int position) {
        editConnectionRequest(item, 1, position);
      }
    }));
    mFastAdapterConnectionReceive.withEventHook(new ConnectionReceiveItem.RejectButtonClickEvent(new ConnectionReceiveItem.HandleRejectClickListener() {
      @Override
      public void reject(ConnectionReceiveItem item, int position) {
        editConnectionRequest(item, 0, position);
      }
    }));
  }

  private void sendMail(ConnectionSendItem item) {
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
        "mailto", item.getEmailTo(), null));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.request_skill_swap_label);
    try {
      startActivity(emailIntent);
    } catch (ActivityNotFoundException e) {
      AppUtil.showSnackbar(recyclerView, getString(R.string.mail_app_not_available_label), this, SnackbarUtils.LENGTH_INDEFINITE);
    }
  }

  private void editConnectionRequest(ConnectionReceiveItem item, int isAccept, int position) {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ConnectionReceiveItem> call = apiService.editConnection(info.getUuid(), item.getUuid(), isAccept);
    call.enqueue(new Callback<ConnectionReceiveItem>() {
      @Override
      public void onResponse(@NonNull Call<ConnectionReceiveItem> call, @NonNull Response<ConnectionReceiveItem> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          ConnectionReceiveItem connectionReceiveItem = response.body();
          mItemAdapterConnectionReceive.remove(position);
          mItemAdapterConnectionReceive.add(position, connectionReceiveItem);
        }
      }

      @Override
      public void onFailure(@NonNull Call<ConnectionReceiveItem> call, @NonNull Throwable t) {
        t.printStackTrace();
        loadingDialog.dismiss();
      }
    });
  }

  private void deleteConnection(ConnectionSendItem item, int position) {
    AppUtil.showConfirmDialog(getString(R.string.confirm_remove_request_label), this, new AppUtil.ConfirmDialogClickListener() {
      @Override
      public void ok() {
        delete(item, position);
      }

      @Override
      public void cancel() {

      }
    });
  }

  private void delete(ConnectionSendItem item, int position) {
    loadingDialog.show();
    APIService apiService = RetrofitUtil.getRetrofit(info.getToken()).create(APIService.class);
    Call<ResponseMessage> call = apiService.deleteConnectionRequest(info.getUuid(), item.getUuid());
    call.enqueue(new Callback<ResponseMessage>() {
      @Override
      public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
        loadingDialog.dismiss();
        if (response.isSuccessful()) {
          mItemAdapterConnectionSend.remove(position);
          connectionRequest.getConnectionSend().remove(item);
          if (mItemAdapterConnectionSend.getAdapterItemCount() == 0) {
            showEmptyLayout();
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
        loadingDialog.dismiss();
        t.printStackTrace();

      }
    });
  }

  @OnClick({R.id.sent_layout, R.id.received_layout, R.id.sent_image_button, R.id.received_image_button})
  public void handleBottomNavigationClick(View view) {
    switch (view.getId()) {
      case R.id.sent_layout:
        selectSentConnection();
        break;
      case R.id.received_layout:
        selectReceivedConnection();
        break;
      case R.id.sent_image_button:
        selectSentConnection();
        break;
      case R.id.received_image_button:
        selectReceivedConnection();
        break;
    }
  }

  private void selectReceivedConnection() {
    if (currentConnectionType != RECEIVED_CONNECTION) {
      displayConnectionReceive(connectionRequest.getConnectionReceive());
      currentConnectionType = RECEIVED_CONNECTION;
      setReceivedSelect();
      setSentDeselect();
    }
  }

  private void selectSentConnection() {
    if (currentConnectionType != SENT_CONNECTION) {
      displayConnectionSend(connectionRequest.getConnectionSend());
      currentConnectionType = SENT_CONNECTION;
      setSentSelect();
      setReceivedDeselect();
    }
  }

  private void setSentSelect() {
    sentTextView.setTextColor(getResources().getColor(R.color.colorAccent));
    sentImageButton.setColorFilter(getResources().getColor(R.color.colorAccent));
  }

  private void setReceivedSelect() {
    receivedTextView.setTextColor(getResources().getColor(R.color.colorAccent));
    receivedImageButton.setColorFilter(getResources().getColor(R.color.colorAccent));
  }

  private void setSentDeselect() {
    sentTextView.setTextColor(AppUtil.getThemeColorSecondary(this));
    sentImageButton.setColorFilter(AppUtil.getThemeColorSecondary(this));
  }

  private void setReceivedDeselect() {
    receivedTextView.setTextColor(AppUtil.getThemeColorSecondary(this));
    receivedImageButton.setColorFilter(AppUtil.getThemeColorSecondary(this));
  }

  @OnClick(R.id.close_image_view)
  public void close() {
    finish();
  }

}