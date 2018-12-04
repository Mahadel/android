package com.github.mahadel.demo.listener;

import com.github.mahadel.demo.util.AppUtil;

public interface SkillDetailCallbackResult {
  void update(Object obj, AppUtil.SkillType skillType);

  void remove(Object obj, AppUtil.SkillType skillType);
}
