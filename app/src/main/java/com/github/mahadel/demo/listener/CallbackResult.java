package com.github.mahadel.demo.listener;

import com.github.mahadel.demo.util.AppUtil;

public interface CallbackResult {
  void sendResult(Object obj, AppUtil.SkillType skillType);
}
