package com.github.bkhezry.learn2learn.listener;

import com.github.bkhezry.learn2learn.util.AppUtil;

public interface CallbackResult {
  void sendResult(Object obj, AppUtil.SkillType skillType);
}
