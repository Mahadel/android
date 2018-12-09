package com.github.mahadel.demo.listener;

import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.util.AppUtil;

/**
 * Handle update and remove functions of user skill
 */
public interface SkillDetailCallbackResult {

  /**
   * @param userSkill {@link UserSkill} updated user skill
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType} type of skill that updated
   */
  void update(UserSkill userSkill, AppUtil.SkillType skillType);

  /**
   * @param userSkill {@link UserSkill} removed user skill
   * @param skillType {@link com.github.mahadel.demo.util.AppUtil.SkillType} type of skill that removed
   */
  void remove(UserSkill userSkill, AppUtil.SkillType skillType);
}
