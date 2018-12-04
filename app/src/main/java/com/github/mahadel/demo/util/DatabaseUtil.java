package com.github.mahadel.demo.util;

import com.github.mahadel.demo.model.Category;
import com.github.mahadel.demo.model.Category_;
import com.github.mahadel.demo.model.SkillsItem;
import com.github.mahadel.demo.model.SkillsItem_;
import com.github.mahadel.demo.model.UserSkill;
import com.github.mahadel.demo.model.UserSkill_;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class DatabaseUtil {

  public static Query<UserSkill> getUserSkillWithType(Box<UserSkill> userSkillBox, int skillType) {
    return userSkillBox.query()
        .equal(UserSkill_.skillType, skillType)
        .orderDesc(UserSkill_.id)
        .build();
  }

  public static UserSkill getUserSkillWithSkillUUID(Box<UserSkill> userSkillBox, String uuid) {
    return userSkillBox.query()
        .equal(UserSkill_.skillUuid, uuid)
        .build().findFirst();
  }

  public static UserSkill getUserSkillWithUUID(Box<UserSkill> userSkillBox, String uuid) {
    return userSkillBox.query()
        .equal(UserSkill_.uuid, uuid)
        .build().findFirst();
  }

  public static Category getCategoryWithUUID(Box<Category> categoryBox, String uuid) {
    Query<Category> query = categoryBox.query()
        .equal(Category_.uuid, uuid)
        .build();
    return query.findFirst();
  }

  public static Query<SkillsItem> getSkillItemQueryWithUUID(Box<SkillsItem> skillsItemBox, String uuid) {
    return skillsItemBox.query()
        .equal(SkillsItem_.uuid, uuid)
        .build();
  }

  public static List<SkillsItem> getSkillItemOfCategory(Box<SkillsItem> skillsItemBox, String categoryUUID) {
    return skillsItemBox.query()
        .equal(SkillsItem_.categoryUuid, categoryUUID)
        .build().find();
  }
}
