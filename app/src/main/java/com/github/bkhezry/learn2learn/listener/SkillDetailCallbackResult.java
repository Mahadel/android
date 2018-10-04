package com.github.bkhezry.learn2learn.listener;

import com.github.bkhezry.learn2learn.util.AppUtil;

public interface SkillDetailCallbackResult {
  void update(Object obj, AppUtil.SkillType skillType);

  void remove(Object obj, AppUtil.SkillType skillType);
}
