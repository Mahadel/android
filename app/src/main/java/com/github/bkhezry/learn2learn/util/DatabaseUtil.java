package com.github.bkhezry.learn2learn.util;

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

}
