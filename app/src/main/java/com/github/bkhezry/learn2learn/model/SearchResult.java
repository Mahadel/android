package com.github.bkhezry.learn2learn.model;

import android.view.View;
import android.widget.TextView;

import com.github.bkhezry.learn2learn.R;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

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
  public SearchResult.ViewHolder getViewHolder(@NonNull View view) {
    return new SearchResult.ViewHolder(view);
  }

  @Override
  public int getType() {
    return R.id.fastadapter_sample_item_id;
  }

  @Override
  public int getLayoutRes() {
    return R.layout.item_skill;
  }

  protected static class ViewHolder extends FastAdapter.ViewHolder<SearchResult> {
    protected View view;
    @BindView(R.id.skill)
    TextView skill;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
    }

    @Override
    public void bindView(@NonNull SearchResult item, @NonNull List<Object> payloads) {

    }

    @Override
    public void unbindView(@NonNull SearchResult item) {

    }
  }
}