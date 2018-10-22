package com.github.bkhezry.learn2learn.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class SkillsItem extends AbstractItem<SkillsItem, SkillsItem.ViewHolder> implements Parcelable {

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
      if (AppUtil.isRTL(view.getContext())) {
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


  public static final Parcelable.Creator<SkillsItem> CREATOR = new Parcelable.Creator<SkillsItem>() {
    @Override
    public SkillsItem createFromParcel(Parcel source) {
      return new SkillsItem(source);
    }

    @Override
    public SkillsItem[] newArray(int size) {
      return new SkillsItem[size];
    }
  };

  public SkillsItem() {
  }

  protected SkillsItem(Parcel in) {
    this.faName = in.readString();
    this.categoryUuid = in.readString();
    this.enName = in.readString();
    this.id = (Long) in.readValue(Long.class.getClassLoader());
    this.uuid = in.readString();
    this.categoryName = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.faName);
    dest.writeString(this.categoryUuid);
    dest.writeString(this.enName);
    dest.writeValue(this.id);
    dest.writeString(this.uuid);
    dest.writeString(this.categoryName);
  }
}