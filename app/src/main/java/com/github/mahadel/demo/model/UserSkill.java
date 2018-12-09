package com.github.mahadel.demo.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.util.AppUtil;
import com.github.mahadel.demo.util.DatabaseUtil;
import com.github.mahadel.demo.util.MyApplication;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * UserSkill handle values of user skill of current user.
 * Each item of it saved in local db.
 */
@Entity
public class UserSkill extends AbstractItem<UserSkill, UserSkill.ViewHolder> {

  @Id
  private Long id;

  @SerializedName("uuid")
  private String uuid;

  @SerializedName("user_uuid")
  private String userUuid;

  @SerializedName("updated_at")
  private String updatedAt;

  @SerializedName("skill_uuid")
  private String skillUuid;

  @SerializedName("description")
  private String description;

  @SerializedName("skill_type")
  private int skillType;

  @SerializedName("created_at")
  private String createdAt;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserUuid() {
    return userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getSkillUuid() {
    return skillUuid;
  }

  public void setSkillUuid(String skillUuid) {
    this.skillUuid = skillUuid;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getSkillType() {
    return skillType;
  }

  public void setSkillType(int skillType) {
    this.skillType = skillType;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
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
    return R.layout.item_user_skill;
  }

  /**
   * ViewHolder for handle {@link UserSkill} item in the recycler view
   */
  protected static class ViewHolder extends FastAdapter.ViewHolder<UserSkill> {
    protected View view;
    @BindView(R.id.skill)
    AppCompatTextView skill;
    Box<SkillsItem> skillsItemBox;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
      BoxStore boxStore = MyApplication.getBoxStore();
      skillsItemBox = boxStore.boxFor(SkillsItem.class);
    }

    @Override
    public void bindView(@NonNull UserSkill item, @NonNull List<Object> payloads) {
      SkillsItem skillsItem = getSkill(item.skillUuid);
      if (AppUtil.isRTL(view.getContext())) {
        AppUtil.rotateYView(view, 180);
        skill.setText(skillsItem.getFaName());
      } else {
        skill.setText(skillsItem.getEnName());
      }


    }

    private SkillsItem getSkill(String skillUuid) {
      return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();

    }

    @Override
    public void unbindView(@NonNull UserSkill item) {
      skill.setText(null);
    }
  }

}