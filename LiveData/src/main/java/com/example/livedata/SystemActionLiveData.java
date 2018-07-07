package com.example.livedata;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;

/**
 * 广播监听
 */
public class SystemActionLiveData extends LiveData<String> {
  private Context mContext;
  private IntentFilter intentFilter;
  private static SystemActionLiveData sInstance;

  /** 广播监听器 */
  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action == null || action.isEmpty()) {
        return;
      }
      setValue(action);
      switch (action) {
        case Intent.ACTION_SCREEN_ON: break;// 开屏
        case Intent.ACTION_SCREEN_OFF: break;// 锁屏
        case Intent.ACTION_USER_PRESENT: break;//解锁
      }
    }
  };

  /**
   * 构造函数
   *
   * @param context {@link Context}
   * @param actions 需要监听的Action
   */
  public SystemActionLiveData(Context context, String... actions) {
    mContext = context;
    intentFilter = new IntentFilter();
    for (String action : actions) {
      intentFilter.addAction(action);
    }
    // intentFilter.addAction(Intent.ACTION_SCREEN_ON);
    // intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
    // intentFilter.addAction(Intent.ACTION_USER_PRESENT);
  }

  /**
   * 获取单例
   *
   * @param context {@link Context}
   * @param actions 需要监听的Action
   * @return {@link SystemActionLiveData}
   * @Deprecated 此类监听不适合使用单例, 除非监听的action保持不变
   */
  @MainThread
  @Deprecated
  private static SystemActionLiveData get(Context context, String... actions) {
    if (sInstance == null) {
      sInstance = new SystemActionLiveData(context.getApplicationContext(), actions);
    }
    return sInstance;
  }

  @Override
  protected void onActive() {
    super.onActive();
    if (mContext != null) {
      mContext.registerReceiver(mReceiver, intentFilter);
    }
  }

  @Override
  protected void onInactive() {
    super.onInactive();
    if (mContext != null) {
      mContext.unregisterReceiver(mReceiver);
    }
  }
}
