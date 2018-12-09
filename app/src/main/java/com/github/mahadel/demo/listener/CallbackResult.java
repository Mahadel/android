package com.github.mahadel.demo.listener;

import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.util.AppUtil;

public interface CallbackResult {
  void sendResult(UserSkill userSkill, AppUtil.SkillType skillType);
}
