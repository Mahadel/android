package com.github.mahadel.demo.util;

import android.os.Bundle;

public class FirebaseEventLog {

  public static void log(String event, String tag, String function, String message) {
    Bundle bundle = new Bundle();
    bundle.putString("tag", tag);
    bundle.putString("function", function);
    bundle.putString("error_message", message);
    MyApplication.getFirebaseAnalytics().logEvent(event, bundle);
  }
}
