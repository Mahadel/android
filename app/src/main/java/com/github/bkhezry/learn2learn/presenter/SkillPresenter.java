package com.github.bkhezry.learn2learn.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.bkhezry.learn2learn.R;
import com.github.bkhezry.learn2learn.model.Skill;
import com.github.bkhezry.learn2learn.util.AppUtil;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SkillPresenter extends RecyclerViewPresenter<Skill> {
  private MyAdapter adapter;
  private CharSequence query;
  private List<Skill> skillList;

  public SkillPresenter(Context context) {
    super(context);
    this.skillList = AppUtil.getSkills();
  }

  @Override
  protected RecyclerView.Adapter instantiateAdapter() {
    adapter = new MyAdapter();
    return adapter;
  }

  @Override
  protected void onQuery(@Nullable CharSequence query) {
    this.query = query;
    if (TextUtils.isEmpty(query)) {
      adapter.setData(skillList);
    } else {
      query = query.toString().toLowerCase();
      List<Skill> list = new ArrayList<>();
      for (Skill u : skillList) {
        if (u.getName().toLowerCase().contains(query)) {
          list.add(u);
        }
      }
      adapter.setData(list);
      Log.e("UserPresenter", "found " + list.size() + " users for query " + query);
    }
    adapter.notifyDataSetChanged();
  }


  class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {

    private List<Skill> data;

    public void setData(List<Skill> data) {
      this.data = data;
    }

    @Override
    public int getItemCount() {
      return (isEmpty()) ? 1 : data.size();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.item_skill_presenter, parent, false));
    }

    private boolean isEmpty() {
      return data == null || data.isEmpty();
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      if (isEmpty()) {
        final Skill skill = new Skill();
        skill.setName(String.valueOf(query));
        holder.name.setText("Create this skill");
        holder.root.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dispatchClick(skill);
          }
        });
        return;
      }
      final Skill skill = data.get(position);
      holder.name.setText(skill.getName());
      holder.root.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dispatchClick(skill);
        }
      });
    }

    class Holder extends RecyclerView.ViewHolder {
      private View root;
      private TextView name;

      Holder(View itemView) {
        super(itemView);
        root = itemView;
        name = itemView.findViewById(R.id.skill);
      }
    }
  }
}
