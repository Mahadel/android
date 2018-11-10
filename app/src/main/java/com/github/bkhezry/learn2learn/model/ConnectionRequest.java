package com.github.bkhezry.learn2learn.model;

import android.view.View;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.github.bkhezry.learn2learn.util.DatabaseUtil;
import com.github.bkhezry.learn2learn.util.MyApplication;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;


public class ConnectionRequest extends AbstractItem<ConnectionRequest, ConnectionRequest.ViewHolder> {

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

  public String getEmailTo() {
    return emailTo;
  }

  public void setEmailTo(String emailTo) {
    this.emailTo = emailTo;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getTeachSkillUuidFrom() {
    return teachSkillUuidFrom;
  }

  public void setTeachSkillUuidFrom(String teachSkillUuidFrom) {
    this.teachSkillUuidFrom = teachSkillUuidFrom;
  }

  public String getLearnSkillUuidFrom() {
    return learnSkillUuidFrom;
  }

  public void setLearnSkillUuidFrom(String learnSkillUuidFrom) {
    this.learnSkillUuidFrom = learnSkillUuidFrom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUserUuidTo() {
    return userUuidTo;
  }

  public void setUserUuidTo(String userUuidTo) {
    this.userUuidTo = userUuidTo;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

  public String getUserUuidFrom() {
    return userUuidFrom;
  }

  public void setUserUuidFrom(String userUuidFrom) {
    this.userUuidFrom = userUuidFrom;
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
    return R.layout.item_connection_request;
  }

  protected static class ViewHolder extends FastAdapter.ViewHolder<ConnectionRequest> {
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
    Box<SkillsItem> skillsItemBox;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
      BoxStore boxStore = MyApplication.getBoxStore();
      skillsItemBox = boxStore.boxFor(SkillsItem.class);
    }

    @Override
    public void bindView(@NonNull ConnectionRequest item, @NonNull List<Object> payloads) {
      nameTextView.setText(item.getUserUuidTo());
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
    public void unbindView(@NonNull ConnectionRequest item) {

    }

    //TODO move to AppUtil class.
    private SkillsItem getSkill(String skillUuid) {
      return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();
    }
  }
}