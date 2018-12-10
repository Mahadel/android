package com.github.mahadel.demo.util;

import com.github.mahadel.demo.model.SkillsItem;
import com.github.mahadel.demo.model.SkillsItem_;
import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.model.UserSkill_;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class DatabaseUtil {

  /**
   * Get userSkills with type of it
   *
   * @param userSkillBox {@link Box}
   * @param skillType    Int
   * @return Instance of {@link Query}
   */
  public static Query<UserSkill> getUserSkillWithType(Box<UserSkill> userSkillBox, int skillType) {
    return userSkillBox.query()
        .equal(UserSkill_.skillType, skillType)
        .orderDesc(UserSkill_.id)
        .build();
  }

  /**
   * Get userSkill with skill uuid
   *
   * @param userSkillBox {@link Box}
   * @param uuid         String uuid
   * @return Instance of {@link UserSkill}
   */
  public static UserSkill getUserSkillWithSkillUUID(Box<UserSkill> userSkillBox, String uuid) {
    return userSkillBox.query()
        .equal(UserSkill_.skillUuid, uuid)
        .build().findFirst();
  }

  /**
   * Get userSkill with uuid
   *
   * @param userSkillBox {@link Box}
   * @param uuid         String uuid
   * @return Instance of {@link UserSkill}
   */
  public static UserSkill getUserSkillWithUUID(Box<UserSkill> userSkillBox, String uuid) {
    return userSkillBox.query()
        .equal(UserSkill_.uuid, uuid)
        .build().findFirst();
  }

  /**
   * Get SkillItem with uuid
   *
   * @param skillsItemBox {@link Box}
   * @param uuid          String uuid
   * @return Instance of {@link Query}
   */

  public static Query<SkillsItem> getSkillItemQueryWithUUID(Box<SkillsItem> skillsItemBox, String uuid) {
    return skillsItemBox.query()
        .equal(SkillsItem_.uuid, uuid)
        .build();
  }

  /**
   * Get list of skills item with uuid of category
   *
   * @param skillsItemBox {@link Box}
   * @param categoryUUID  String uuid
   * @return List of {@link SkillsItem}
   */
  public static List<SkillsItem> getSkillItemOfCategory(Box<SkillsItem> skillsItemBox, String categoryUUID) {
    return skillsItemBox.query()
        .equal(SkillsItem_.categoryUuid, categoryUUID)
        .build().find();
  }
}
