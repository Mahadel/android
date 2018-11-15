package com.github.bkhezry.learn2learn.model;


import android.view.View;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.google.android.material.button.MaterialButton;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;


public class ConnectionSendItem extends AbstractItem<ConnectionSendItem, ConnectionSendItem.ViewHolder> {

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

  @SerializedName("user_to")
  private UserInfo userInfo;

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

  public interface HandleDeleteClickListener {
    void delete(ConnectionSendItem item, int position);
  }

  public interface HandleEmailClickListener {
    void sendEmail(ConnectionSendItem item, int position);
  }

  @NonNull
  @Override
  public ViewHolder getViewHolder(@NonNull View view) {
    return new ViewHolder(view);
  }

  @Override
  public int getType() {
    return R.id.fastadapter_sample_item_id;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_connection_sent;
  }

  protected static class ViewHolder extends FastAdapter.ViewHolder<ConnectionSendItem> {
    protected View view;
    @BindView(R.id.name_text_view)
    AppCompatTextView nameTextView;
    @BindView(R.id.email_text_view)
    AppCompatTextView emailTextView;
    @BindView(R.id.learn_skill_name_text_view)
    AppCompatTextView learnSkillNameTextView;
    @BindView(R.id.teach_skill_name_text_view)
    AppCompatTextView teachSkillNameTextView;
    @BindView(R.id.description_text_view)
    AppCompatTextView descriptionTextView;
    @BindView(R.id.delete_request_button)
    MaterialButton deleteRequestButton;
    @BindView(R.id.send_email_button)
    MaterialButton sendEmailButton;
    Box<SkillsItem> skillsItemBox;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
      BoxStore boxStore = MyApplication.getBoxStore();
      skillsItemBox = boxStore.boxFor(SkillsItem.class);
    }

    @Override
    public void bindView(@NonNull ConnectionSendItem item, @NonNull List<Object> payloads) {
      nameTextView.setText(String.format("%s %s", item.getUserInfo().getFirstName(), item.getUserInfo().getLastName()));
      emailTextView.setText(item.getEmailTo());
      if (AppUtil.isRTL(view.getContext())) {
        learnSkillNameTextView.setText(getSkill(item.getLearnSkillUuidFrom()).getFaName());
        teachSkillNameTextView.setText(getSkill(item.getTeachSkillUuidFrom()).getFaName());
      } else {
        learnSkillNameTextView.setText(getSkill(item.getLearnSkillUuidFrom()).getEnName());
        teachSkillNameTextView.setText(getSkill(item.getTeachSkillUuidFrom()).getEnName());
      }
      descriptionTextView.setText(item.getDescription());
    }

    @Override
    public void unbindView(@NonNull ConnectionSendItem item) {

    }

    //TODO move to AppUtil class.
    private SkillsItem getSkill(String skillUuid) {
      return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();
    }
  }

  public static class DeleteButtonClickEvent extends ClickEventHook<ConnectionSendItem> {
    private HandleDeleteClickListener listener;

    public DeleteButtonClickEvent(HandleDeleteClickListener handleDeleteClickListener) {
      this.listener = handleDeleteClickListener;
    }

    @Override
    public void onClick(@NonNull View view, int position, @NonNull FastAdapter<ConnectionSendItem> fastAdapter, @NonNull ConnectionSendItem item) {
      if (listener != null) {
        listener.delete(item, position);
      }
    }

    @Nullable
    @Override
    public View onBind(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof ViewHolder) {
        return ((ViewHolder) viewHolder).deleteRequestButton;
      }
      return null;
    }
  }

  public static class EmailButtonClickEvent extends ClickEventHook<ConnectionSendItem> {
    private HandleEmailClickListener listener;

    public EmailButtonClickEvent(HandleEmailClickListener handleEmailClickListener) {
      this.listener = handleEmailClickListener;
    }

    @Override
    public void onClick(@NonNull View view, int position, @NonNull FastAdapter<ConnectionSendItem> fastAdapter, @NonNull ConnectionSendItem item) {
      if (listener != null) {
        listener.sendEmail(item, position);
      }
    }

    @Nullable
    @Override
    public View onBind(RecyclerView.ViewHolder viewHolder) {
      if (viewHolder instanceof ViewHolder) {
        return ((ViewHolder) viewHolder).sendEmailButton;
      }
      return null;
    }
  }

}