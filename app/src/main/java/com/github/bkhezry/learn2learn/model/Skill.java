package com.github.bkhezry.learn2learn.model;

import android.view.View;
import android.widget.TextView;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Skill extends AbstractItem<Skill, Skill.ViewHolder> {
  private String name;
  private String description;
  private boolean wantCreate;

  public Skill() {
    this.wantCreate = false;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isWantCreate() {
    return wantCreate;
  }

  public void setWantCreate(boolean wantCreate) {
    this.wantCreate = wantCreate;
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

  protected static class ViewHolder extends FastAdapter.ViewHolder<Skill> {
    protected View view;
    @BindView(R.id.skill)
    TextView skill;


    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = view;
    }

    @Override
    public void bindView(@NonNull Skill item, @NonNull List<Object> payloads) {
      if (AppUtil.isRTL(ConfigurationCompat.getLocales(view.getContext().getResources().getConfiguration()).get(0))) {
        AppUtil.rotateYView(view, 180);
      }
      skill.setText(item.name);

    }

    @Override
    public void unbindView(@NonNull Skill item) {
      skill.setText(null);
    }
  }
}
