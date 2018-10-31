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

public class SearchResult extends AbstractItem<SearchResult, SearchResult.ViewHolder> {

  @SerializedName("learn_skill_uuid")
  private String learnSkillUuid;

  @SerializedName("teach_skill_uuid")
  private String teachSkillUuid;

  @SerializedName("learn_description")
  private String learnDescription;

  @SerializedName("teach_description")
  private String teachDescription;

  @SerializedName("user")
  private User user;

  public String getLearnSkillUuid() {
    return learnSkillUuid;
  }

  public void setLearnSkillUuid(String learnSkillUuid) {
    this.learnSkillUuid = learnSkillUuid;
  }

  public String getTeachSkillUuid() {
    return teachSkillUuid;
  }

  public void setTeachSkillUuid(String teachSkillUuid) {
    this.teachSkillUuid = teachSkillUuid;
  }

  public String getLearnDescription() {
    return learnDescription;
  }

  public void setLearnDescription(String learnDescription) {
    this.learnDescription = learnDescription;
  }

  public String getTeachDescription() {
    return teachDescription;
  }

  public void setTeachDescription(String teachDescription) {
    this.teachDescription = teachDescription;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
    return R.layout.item_search;
  }

  protected static class ViewHolder extends FastAdapter.ViewHolder<SearchResult> {
    protected View view;
    @BindView(R.id.name_text_view)
    AppCompatTextView nameTextView;
    @BindView(R.id.gender_text_view)
    AppCompatTextView genderTextView;
    @BindView(R.id.teach_skill_name)
    AppCompatTextView teachSkillName;
    @BindView(R.id.learn_skill_name)
    AppCompatTextView learnSkillName;
    @BindView(R.id.teach_description_text_view)
    AppCompatTextView teachDescriptionTextView;
    @BindView(R.id.learn_description_text_view)
    AppCompatTextView learnDescriptionTextView;
    Box<SkillsItem> skillsItemBox;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
      BoxStore boxStore = MyApplication.getBoxStore();
      skillsItemBox = boxStore.boxFor(SkillsItem.class);
    }

    @Override
    public void bindView(@NonNull SearchResult item, @NonNull List<Object> payloads) {
      nameTextView.setText(String.format("%s %s", item.getUser().getFirstName(), item.getUser().getLastName()));
      genderTextView.setText(item.getUser().getGender() == 1 ? R.string.male_label : R.string.female_label);
      teachDescriptionTextView.setText(item.getTeachDescription());
      learnDescriptionTextView.setText(item.getLearnDescription());
      if (AppUtil.isRTL(view.getContext())) {
        teachSkillName.setText(getSkill(item.getTeachSkillUuid()).getFaName());
        learnSkillName.setText(getSkill(item.getLearnSkillUuid()).getFaName());
      } else {
        teachSkillName.setText(getSkill(item.getTeachSkillUuid()).getEnName());
        learnSkillName.setText(getSkill(item.getLearnSkillUuid()).getEnName());
      }
    }

    @Override
    public void unbindView(@NonNull SearchResult item) {
      nameTextView.setText(null);
    }

    private SkillsItem getSkill(String skillUuid) {
      return DatabaseUtil.getSkillItemQueryWithUUID(skillsItemBox, skillUuid).findFirst();
    }
  }
}