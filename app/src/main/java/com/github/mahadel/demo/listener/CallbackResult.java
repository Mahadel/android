package com.github.mahadel.demo.listener;

import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.util.AppUtil;

/**
 * send result of UserSkill that add to the user.
 */
public interface CallbackResult {
  /**
   * @param userSkill {@link UserSkill} added user skill
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType} type of skill that added
   */
  void sendResult(UserSkill userSkill, AppUtil.SkillType skillType);
}
