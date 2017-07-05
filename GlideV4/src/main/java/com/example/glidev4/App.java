package com.example.glidev4;

import android.app.Application;
import com.facebook.soloader.SoLoader;
import timber.log.Timber;

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();

    // 加载加密库的so文件
    SoLoader.init(this, false);

    //在这里先使用Timber.plant注册一个Tree，然后调用静态的.d .v 去使用
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      //Timber.plant(new CrashReportingTree());
    }
  }
}
