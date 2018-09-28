package com.github.bkhezry.learn2learn.util;

import com.github.bkhezry.learn2learn.model.Category;
import com.github.bkhezry.learn2learn.model.Category_;
import com.github.bkhezry.learn2learn.model.SkillsItem;
import com.github.bkhezry.learn2learn.model.SkillsItem_;
import com.github.bkhezry.learn2learn.model.UserSkill;
import com.github.bkhezry.learn2learn.model.UserSkill_;

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

}
