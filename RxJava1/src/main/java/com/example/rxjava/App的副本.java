package com.example.rxjava;

import android.app.Application;
import timber.log.Timber;

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();
    //在这里先使用Timber.plant注册一个Tree，然后调用静态的.d .v 去使用
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      //Timber.plant(new CrashReportingTree());
    }
  }
}