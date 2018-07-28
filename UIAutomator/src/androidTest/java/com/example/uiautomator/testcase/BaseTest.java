package com.example.uiautomator.testcase;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.io.IOException;

public abstract class BaseTest {
  public final String TAG = this.getClass().getName();

  abstract String getPackageName();

  abstract UiDevice getDevice();

  /**
   * 根据ID查找, 超时时间为10秒
   *
   * @param id id
   * @return
   */
  public UiObject2 findById(String id) {
    BySelector selector = By.res(getPackageName(), id);
    SearchCondition<UiObject2> condition = Until.findObject(selector);
    return getDevice().wait(condition, 1000 * 10);
  }

  public void sleep(int second) {
    try {
      Thread.sleep(1000 * second);
    } catch (InterruptedException e) {
      Log.e(TAG, "sleep出错了:" + getPackageName(), e);
    }
  }

  /**
   * 启动app
   */
  public void startAPP() {
    getDevice().pressHome();
    getDevice().pressHome();
    getDevice().waitForIdle(1000);
    Context mContext = InstrumentationRegistry.getInstrumentation().getContext();
    Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(getPackageName());  //通过Intent启动app
    if (myIntent != null) {
      myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      mContext.startActivity(myIntent);
      try {
        Log.e(TAG, "尝试打开app: " + getPackageName());
        Thread.sleep(1000 * 10);
      } catch (InterruptedException e) {
        Log.e(TAG, "打开app出错了:" + getPackageName(), e);
      }
    } else {
      Log.e(TAG, "尝试打开app失败: " + getPackageName());
    }
  }

  /**
   * 关闭app
   */
  public void closeAPP() {
    try {
      Log.e(TAG, "尝试关闭app: " + getPackageName());
      getDevice().executeShellCommand("am force-stop " + getPackageName());//通过命令行关闭app
      Log.e(TAG, "app已经关闭: " + getPackageName());
    } catch (IOException e) {
      Log.e(TAG, "关闭app失败", e);
      getDevice().pressBack();
      getDevice().pressBack();
    }
  }

  /**
   * 通过命令启动App
   *
   * @param sLaunchActivity d
   */
  private void startAPP(String sLaunchActivity) {
    try {
      getDevice().executeShellCommand("am start -n " + getPackageName() + "/" + sLaunchActivity);//通过命令行启动app
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
