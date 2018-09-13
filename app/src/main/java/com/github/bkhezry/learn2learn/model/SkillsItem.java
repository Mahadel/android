package com.github.bkhezry.learn2learn.model;

import android.view.View;
import android.widget.TextView;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class SkillsItem extends AbstractItem<SkillsItem, SkillsItem.ViewHolder> {

  @SerializedName("fa_name")
  private String faName;

  @SerializedName("category_uuid")
  private String categoryUuid;

  @SerializedName("en_name")
  private String enName;

  @Id
  @SerializedName("id")
  private Long id;

  @SerializedName("uuid")
  private String uuid;

  private String categoryName;

  public void setFaName(String faName) {
    this.faName = faName;
  }

  public String getFaName() {
    return faName;
  }

  public String getCategoryUuid() {
    return categoryUuid;
  }

  public void setCategoryUuid(String categoryUuid) {
    this.categoryUuid = categoryUuid;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public String getEnName() {
    return enName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
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
    return R.layout.item_skill;
  }

  protected static class ViewHolder extends FastAdapter.ViewHolder<SkillsItem> {
    protected View view;
    @BindView(R.id.skill)
    TextView skill;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
    }

    @Override
    public void bindView(@NonNull SkillsItem item, @NonNull List<Object> payloads) {
      if (AppUtil.isRTL(ConfigurationCompat.getLocales(view.getContext().getResources().getConfiguration()).get(0))) {
        skill.setText(item.getFaName());
      } else {
        skill.setText(item.getEnName());
      }


    }

    @Override
    public void unbindView(@NonNull SkillsItem item) {
      skill.setText(null);
    }
  }
}