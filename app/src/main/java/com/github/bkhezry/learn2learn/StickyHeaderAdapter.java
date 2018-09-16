package com.github.bkhezry.learn2learn;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
public class StickyHeaderAdapter<Item extends IItem> extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter {
  //private AbstractAdapter mParentAdapter;
  //keep a reference to the FastAdapter which contains the base logic
  private FastAdapter<Item> mFastAdapter;

  @Override
  public long getHeaderId(int position) {
    IItem item = getItem(position);
    if (item instanceof SkillsItem) {
      return item.getIdentifier();
    }
    return -1;
  }

  @Override
  public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
    //we create the view for the header
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
    return new RecyclerView.ViewHolder(view) {
    };
  }

  @Override
  public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    IItem item = getItem(position);
    TextView textView = (TextView) holder.itemView;
    if (item instanceof SkillsItem) {
      textView.setText(((SkillsItem) item).getCategoryName());
    }
    holder.itemView.setBackgroundColor(getColor());
  }

  /*
   * GENERAL CODE NEEDED TO WRAP AN ADAPTER
   */

  //just to prettify things a bit
  private int getColor() {
    return Color.argb(200, 100, 31, 63);
  }

  /**
   * Wrap the FastAdapter with this AbstractAdapter and keep its reference to forward all events correctly
   *
   * @param fastAdapter the FastAdapter which contains the base logic
   * @return this
   */
  public StickyHeaderAdapter<Item> wrap(FastAdapter<Item> fastAdapter) {
    //this.mParentAdapter = abstractAdapter;
    this.mFastAdapter = fastAdapter;
    return this;
  }

  /**
   * overwrite the registerAdapterDataObserver to correctly forward all events to the FastAdapter
   *
   * @param observer AdapterDataObserver
   */
  @Override
  public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
    super.registerAdapterDataObserver(observer);
    if (mFastAdapter != null) {
      mFastAdapter.registerAdapterDataObserver(observer);
    }
  }

  /**
   * overwrite the unregisterAdapterDataObserver to correctly forward all events to the FastAdapter
   *
   * @param observer AdapterDataObserver
   */
  @Override
  public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
    super.unregisterAdapterDataObserver(observer);
    if (mFastAdapter != null) {
      mFastAdapter.unregisterAdapterDataObserver(observer);
    }
  }

  /**
   * overwrite the getItemViewType to correctly return the value from the FastAdapter
   *
   * @param position int
   * @return int
   */
  @Override
  public int getItemViewType(int position) {
    return mFastAdapter.getItemViewType(position);
  }

  /**
   * overwrite the getItemId to correctly return the value from the FastAdapter
   *
   * @param position int
   * @return long
   */
  @Override
  public long getItemId(int position) {
    return mFastAdapter.getItemId(position);
  }

  /**
   * @return the reference to the FastAdapter
   */
  public FastAdapter<Item> getFastAdapter() {
    return mFastAdapter;
  }

  /**
   * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
   *
   * @param position int
   * @return Item
   */
  public Item getItem(int position) {
    return mFastAdapter.getItem(position);
  }

  /**
   * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
   *
   * @return int
   */
  @Override
  public int getItemCount() {
    return mFastAdapter.getItemCount();
  }

  /**
   * the onCreateViewHolder is managed by the FastAdapter so forward this correctly
   *
   * @param parent   ViewGroup
   * @param viewType int
   * @return ViewHolder
   */
  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return mFastAdapter.onCreateViewHolder(parent, viewType);
  }

  /**
   * the onBindViewHolder is managed by the FastAdapter so forward this correctly
   *
   * @param holder   ViewHolder
   * @param position int
   */
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    mFastAdapter.onBindViewHolder(holder, position);
  }

  /**
   * the onBindViewHolder is managed by the FastAdapter so forward this correctly
   *
   * @param holder   ViewHolder
   * @param position int
   * @param payloads List
   */
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
    mFastAdapter.onBindViewHolder(holder, position, payloads);
  }

  /**
   * the setHasStableIds is managed by the FastAdapter so forward this correctly
   *
   * @param hasStableIds Boolean
   */
  @Override
  public void setHasStableIds(boolean hasStableIds) {
    mFastAdapter.setHasStableIds(hasStableIds);
  }

  /**
   * the onViewRecycled is managed by the FastAdapter so forward this correctly
   *
   * @param holder ViewHolder
   */
  @Override
  public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
    mFastAdapter.onViewRecycled(holder);
  }

  /**
   * the onFailedToRecycleView is managed by the FastAdapter so forward this correctly
   *
   * @param holder ViewHolder
   * @return Boolean
   */
  @Override
  public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
    return mFastAdapter.onFailedToRecycleView(holder);
  }

  /**
   * the onViewDetachedFromWindow is managed by the FastAdapter so forward this correctly
   *
   * @param holder ViewHolder
   */
  @Override
  public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    mFastAdapter.onViewDetachedFromWindow(holder);
  }

  /**
   * the onViewAttachedToWindow is managed by the FastAdapter so forward this correctly
   *
   * @param holder ViewHolder
   */
  @Override
  public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
    mFastAdapter.onViewAttachedToWindow(holder);
  }

  /**
   * the onAttachedToRecyclerView is managed by the FastAdapter so forward this correctly
   *
   * @param recyclerView RecyclerView
   */
  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    mFastAdapter.onAttachedToRecyclerView(recyclerView);
  }

  /**
   * the onDetachedFromRecyclerView is managed by the FastAdapter so forward this correctly
   *
   * @param recyclerView RecyclerView
   */
  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    mFastAdapter.onDetachedFromRecyclerView(recyclerView);
  }
}

