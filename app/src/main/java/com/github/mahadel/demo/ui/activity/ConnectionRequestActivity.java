package com.github.mahadel.demo.ui.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SnackbarUtils;
import com.github.mahadel.demo.R;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.ConnectionReceiveItem;
import com.github.mahadel.demo.model.ConnectionRequest;
import com.github.mahadel.demo.model.ConnectionSendItem;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.service.APIService;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.Constant;
import com.github.mahadel.demo.util.FirebaseEventLog;
import com.github.mahadel.demo.util.GridSpacingItemDecoration;
import com.github.mahadel.demo.util.RetrofitUtil;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.mahadel.demo.util.AppUtil.dpToPx;

/**
 * ConnectionRequestActivity handle sent and received connection
 * Show theme in recycler view with options for handle them
 */
public class ConnectionRequestActivity extends BaseActivity {
  private static final int SENT_CONNECTION = 1;
  private static final int RECEIVED_CONNECTION = 2;
  private static final String TAG = "ConnectionRequestActivity";
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
    initVariables();
    setSentSelect();
    initRecyclerView();
    getConnectionRequests();
  }

  /**
   * Setup init values of variables
   */
  private void initVariables() {
    Prefser prefser = new Prefser(this);
    loadingDialog = AppUtil.getLoadingDialog(this);
    info = prefser.get(Constant.TOKEN, AuthenticationInfo.class, null);
  }

  /**
   * Setup recycler view & FastAdapter events for both sent & received connection
   */
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
          AppUtil.showSnackbar(recyclerView, getString(R.string.no_email_available_label), ConnectionRequestActivity.this, SnackbarUtils.LENGTH_LONG);
        } else {
          sendMail(item.getEmailTo());
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

  /**
   * Get list of sent & received connection from server
   */
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
        FirebaseEventLog.log("server_failure", TAG, "getConnectionRequests", t.getMessage());
      }
    });
  }

  /**
   * Showing items of connection that sent to users
   *
   * @param connectionSend List of {@link ConnectionSendItem}
   */
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

  /**
   * Showing items of connection that received from users
   *
   * @param connectionReceiveItems List of {@link ConnectionReceiveItem}
   */
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

  /**
   * Open default mail application from device
   *
   * @param email String email of user
   */
  private void sendMail(String email) {
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
        "mailto", email, null));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.request_skill_swap_label);
    try {
      startActivity(emailIntent);
    } catch (ActivityNotFoundException e) {
      AppUtil.showSnackbar(recyclerView, getString(R.string.mail_app_not_available_label), this, SnackbarUtils.LENGTH_INDEFINITE);
    }
  }

  /**
   * Edit connection request status with 1 for accept or 0 for deny
   *
   * @param item     {@link ConnectionReceiveItem} connection receive item
   * @param isAccept Int status of connection is accept or deny
   * @param position Int position of item in recycler view
   */
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
        loadingDialog.dismiss();
        t.printStackTrace();
        FirebaseEventLog.log("server_failure", TAG, "editConnectionRequests", t.getMessage());
      }
    });
  }

  /**
   * Showing confirm dialog before delete connection
   *
   * @param item     {@link ConnectionSendItem} connection send item
   * @param position Int position of connection in recycler view
   */
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

  /**
   * Request delete connection from server
   *
   * @param item     {@link ConnectionSendItem} connection send item
   * @param position Int position of connection in recycler view
   */

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
        FirebaseEventLog.log("server_failure", TAG, "delete", t.getMessage());
      }
    });
  }

  /**
   * Handle bottom navigation items click
   *
   * @param view {@link View}
   */
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
    if (connectionRequest != null) {
      if (currentConnectionType != RECEIVED_CONNECTION) {
        displayConnectionReceive(connectionRequest.getConnectionReceive());
        currentConnectionType = RECEIVED_CONNECTION;
        setReceivedSelect();
        setSentDeselect();
      }
    }
  }

  private void selectSentConnection() {
    if (connectionRequest != null) {
      if (currentConnectionType != SENT_CONNECTION) {
        displayConnectionSend(connectionRequest.getConnectionSend());
        currentConnectionType = SENT_CONNECTION;
        setSentSelect();
        setReceivedDeselect();
      }
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