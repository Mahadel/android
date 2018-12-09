package com.github.mahadel.demo.model;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.MyApplication;
import com.google.android.material.button.MaterialButton;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * ConnectionReceiveItem handle values of connection that received from other users.
 */
public class ConnectionReceiveItem extends AbstractItem<ConnectionReceiveItem, ConnectionReceiveItem.ViewHolder> {

  @SerializedName("email_to")
  private String emailTo;

  @SerializedName("updated_at")
  private String updatedAt;

  @SerializedName("teach_skill_uuid_from")
  private String teachSkillUuidFrom;

  @SerializedName("learn_skill_uuid_from")
  private String learnSkillUuidFrom;

  @SerializedName("description")
  private String description;

  @SerializedName("created_at")
  private String createdAt;

  @SerializedName("uuid")
  private String uuid;

  @SerializedName("user_uuid_to")
  private String userUuidTo;

  @SerializedName("email_from")
  private String emailFrom;

  @SerializedName("user_uuid_from")
  private String userUuidFrom;

  @SerializedName("user_from")
  private UserInfo userInfo;

  @SerializedName("is_accept")
  private int isAccept;

  @SerializedName("is_delete")
  private int isDelete;

  public void setEmailTo(String emailTo) {
    this.emailTo = emailTo;
  }

  public String getEmailTo() {
    return emailTo;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setTeachSkillUuidFrom(String teachSkillUuidFrom) {
    this.teachSkillUuidFrom = teachSkillUuidFrom;
  }

  public String getTeachSkillUuidFrom() {
    return teachSkillUuidFrom;
  }

  public void setLearnSkillUuidFrom(String learnSkillUuidFrom) {
    this.learnSkillUuidFrom = learnSkillUuidFrom;
  }

  public String getLearnSkillUuidFrom() {
    return learnSkillUuidFrom;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUserUuidTo(String userUuidTo) {
    this.userUuidTo = userUuidTo;
  }

  public String getUserUuidTo() {
    return userUuidTo;
  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public void setUserUuidFrom(String userUuidFrom) {
    this.userUuidFrom = userUuidFrom;
  }

  public String getUserUuidFrom() {
    return userUuidFrom;
  }

  public UserInfo getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public int getIsAccept() {
    return isAccept;
  }

  public void setIsAccept(int isAccept) {
    this.isAccept = isAccept;
  }

  public int getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(int isDelete) {
    this.isDelete = isDelete;
  }

  @NonNull
  @Override
  public ViewHolder getViewHolder(@NonNull View view) {
    return new ViewHolder(view);
  }

  public interface HandleAcceptClickListener {
    void accept(ConnectionReceiveItem item, int position);
  }

  public interface HandleRejectClickListener {
    void reject(ConnectionReceiveItem item, int position);
  }

  @Override
  public int getType() {
    return R.id.fastadapter_sample_item_id;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_connection_received;
  }

  /**
   * ViewHolder for handle {@link ConnectionReceiveItem} item in the recycler view
   */
  protected static class ViewHolder extends FastAdapter.ViewHolder<ConnectionReceiveItem> {
    protected View view;
    @BindView(R.id.name_text_view)
    AppCompatTextView nameTextView;
    @BindView(R.id.learn_skill_name_text_view)
    AppCompatTextView learnSkillNameTextView;
    @BindView(R.id.teach_skill_name_text_view)
    AppCompatTextView teachSkillNameTextView;
    @BindView(R.id.description_text_view)
    AppCompatTextView descriptionTextView;
    @BindView(R.id.reject_request_button)
    MaterialButton rejectRequestButton;
    @BindView(R.id.accept_request_button)
    MaterialButton acceptRequestButton;
    Box<SkillsItem> skillsItemBox;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
      BoxStore boxStore = MyApplication.getBoxStore();
      skillsItemBox = boxStore.boxFor(SkillsItem.class);
    }

    @Override
    public void bindView(@NonNull ConnectionReceiveItem item, @NonNull List<Object> payloads) {
      if (item.getIsAccept() == -1) {
        acceptRequestButton.setVisibility(View.VISIBLE);
        rejectRequestButton.setVisibility(View.VISIBLE);
      } else if (item.getIsAccept() == 0) {
        acceptRequestButton.setVisibility(View.VISIBLE);
        rejectRequestButton.setVisibility(View.GONE);
      } else if (item.getIsAccept() == 1) {
        acceptRequestButton.setVisibility(View.GONE);
        rejectRequestButton.setVisibility(View.VISIBLE);
      }
      nameTextView.setText(String.format("%s %s", item.getUserInfo().getFirstName(), item.getUserInfo().getLastName()));
      if (AppUtil.isRTL(view.getContext())) {
        learnSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getLearnSkillUuidFrom()).getFaName());
        teachSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getTeachSkillUuidFrom()).getFaName());
      } else {
        learnSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getLearnSkillUuidFrom()).getEnName());
        teachSkillNameTextView.setText(AppUtil.getSkill(skillsItemBox, item.getTeachSkillUuidFrom()).getEnName());
      }
      descriptionTextView.setText(item.getDescription());
    }

    @Override
    public void unbindView(@NonNull ConnectionReceiveItem item) {

    }
  }

  /**
   * AcceptButtonClickEvent handle click on accept button in connection received item.
   */
  public static class AcceptButtonClickEvent extends ClickEventHook<ConnectionReceiveItem> {
    private HandleAcceptClickListener listener;

    public AcceptButtonClickEvent(HandleAcceptClickListener handleAcceptClickListener) {
      this.listener = handleAcceptClickListener;
    }

    @Override
    public void onClick(@NonNull View view, int position, @NonNull FastAdapter<ConnectionReceiveItem> fastAdapter, @NonNull ConnectionReceiveItem item) {
      if (listener != null) {
        listener.accept(item, position);
      }
    }

    @Nullable
    @Override
    public View onBind(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof ViewHolder) {
        return ((ViewHolder) viewHolder).acceptRequestButton;
      }
      return null;
    }
  }

  /**
   * RejectButtonClickEvent handle click on reject button in connection received item.
   */

  public static class RejectButtonClickEvent extends ClickEventHook<ConnectionReceiveItem> {
    private HandleRejectClickListener listener;

    public RejectButtonClickEvent(HandleRejectClickListener handleRejectButtonClickEvent) {
      this.listener = handleRejectButtonClickEvent;
    }

    @Override
    public void onClick(@NonNull View view, int position, @NonNull FastAdapter<ConnectionReceiveItem> fastAdapter, @NonNull ConnectionReceiveItem item) {
      if (listener != null) {
        listener.reject(item, position);
      }
    }

    @Nullable
    @Override
    public View onBind(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof ViewHolder) {
        return ((ViewHolder) viewHolder).rejectRequestButton;
      }
      return null;
    }
  }
}