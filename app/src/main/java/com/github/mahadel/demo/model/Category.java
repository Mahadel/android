package com.github.mahadel.demo.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mahadel.demo.R;
import com.github.mahadel.demo.util.AppUtil;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Category class that handle values of skill category.
 * Each item of it save in the local db.
 */
@Entity
public class Category extends AbstractItem<Category, Category.ViewHolder> {
  @SerializedName("uuid")
  private String uuid;

  @SerializedName("fa_name")
  private String faName;

  @SerializedName("skills")
  private List<SkillsItem> skills;

  @SerializedName("en_name")
  private String enName;

  @Id
  private Long id;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getFaName() {
    return faName;
  }

  public void setFaName(String faName) {
    this.faName = faName;
  }

  public List<SkillsItem> getSkills() {
    return skills;
  }

  public void setSkills(List<SkillsItem> skills) {
    this.skills = skills;
  }

  public String getEnName() {
    return enName;
  }

  public void setEnName(String enName) {
    this.enName = enName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Handle values of item in recycler view with FastAdapter library.
   */
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
    return R.layout.item_category;
  }

  /**
   * ViewHolder for handle {@link Category} item in the recycler view
   */
  protected static class ViewHolder extends FastAdapter.ViewHolder<Category> {
    protected View view;
    @BindView(R.id.skill)
    TextView skill;

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
    }

    @Override
    public void bindView(@NonNull Category item, @NonNull List<Object> payloads) {
      if (AppUtil.isRTL(view.getContext())) {
        skill.setText(item.getFaName());
      } else {
        skill.setText(item.getEnName());
      }


    }

    @Override
    public void unbindView(@NonNull Category item) {
      skill.setText(null);
    }
  }
}