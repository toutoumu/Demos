package com.example.bluetoothble;

import android.app.Application;
import android.content.Context;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.internal.RxBleLog;
import timber.log.Timber;

public class SampleApplication extends Application {

  private RxBleClient rxBleClient;

  /**
   * In practise you will use some kind of dependency injection pattern.
   */
  public static RxBleClient getRxBleClient(Context context) {
    SampleApplication application = (SampleApplication) context.getApplicationContext();
    return application.rxBleClient;
  }

  @Override public void onCreate() {
    super.onCreate();
    rxBleClient = RxBleClient.create(this);
    RxBleClient.setLogLevel(RxBleLog.VERBOSE);

    //在这里先使用Timber.plant注册一个Tree，然后调用静态的.d .v 去使用
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      //Timber.plant(new CrashReportingTree());
    }
  }
}
