package com.github.mahadel.demo.listener;

import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.util.AppUtil;

public interface SkillDetailCallbackResult {
  void update(UserSkill userSkill, AppUtil.SkillType skillType);

  void remove(UserSkill userSkill, AppUtil.SkillType skillType);
}
