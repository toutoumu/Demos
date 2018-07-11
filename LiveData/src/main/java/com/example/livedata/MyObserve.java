package com.example.livedata;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

public class MyObserve implements LifecycleObserver {

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void xxx() {

  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void xxxx() {

  }
}
