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
import java.util.Random;

public abstract class BaseTest {
  public final String TAG = this.getClass().getName();

  abstract String getAPPName();

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

  /**
   * 根据ID查找, 超时时间为10秒
   *
   * @param id id
   * @param second 多少秒超时
   * @return
   */
  public UiObject2 findById(String id, int second) {
    BySelector selector = By.res(getPackageName(), id);
    SearchCondition<UiObject2> condition = Until.findObject(selector);
    return getDevice().wait(condition, 1000 * second);
  }

  /**
   * 根据文本内容查找
   *
   * @param text
   * @return
   */
  public UiObject2 findByText(String text) {
    BySelector selector = By.text(text);
    SearchCondition<UiObject2> condition = Until.findObject(selector);
    UiObject2 share = getDevice().wait(condition, 1000 * 10);
    return share;
  }

  /**
   * 根据文本内容查找
   *
   * @param text
   * @param second 多少秒超时
   * @return
   */
  public UiObject2 findByText(String text, int second) {
    BySelector selector = By.text(text);
    SearchCondition<UiObject2> condition = Until.findObject(selector);
    UiObject2 share = getDevice().wait(condition, 1000 * second);
    return share;
  }

  public void sleep(int second) {
    try {
      Thread.sleep(1000 * second);
    } catch (InterruptedException e) {
      Log.e(TAG, "sleep出错了:" + getPackageName(), e);
    }
  }

  public void startAPP() {
    // 回到桌面首页
    // closeAPP();
    getDevice().pressBack();
    getDevice().waitForIdle(20);
    getDevice().pressBack();
    getDevice().waitForIdle(20);
    getDevice().pressBack();
    getDevice().waitForIdle(20);
    getDevice().pressBack();
    getDevice().waitForIdle(1000);
    getDevice().pressBack();
    getDevice().waitForIdle(1000);
    getDevice().pressBack();
    getDevice().waitForIdle(1000);
    getDevice().pressBack();
    getDevice().waitForIdle(20);
    getDevice().pressBack();
    getDevice().waitForIdle(20);
    getDevice().pressHome();
    getDevice().waitForIdle(1000);
    getDevice().pressHome();
    getDevice().waitForIdle(1000);

    // 打开app
    UiObject2 startIcon = getDevice().findObject(By.text(getAPPName()));
    if (startIcon.isClickable()) {
      startIcon.click();
    }
  }

  /**
   * 启动app
   */
  public void startAPP(String packageName) {
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
    } catch (Exception e) {
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
 /* private void startAPP(String sLaunchActivity) {
    try {
      getDevice().executeShellCommand("am start -n " + getPackageName() + "/" + sLaunchActivity);//通过命令行启动app
    } catch (IOException e) {
      e.printStackTrace();
    }
  }*/
  public String getComment(int length) {
    //定义一个字符串（A-Z，a-z，0-9）即62位；
    String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    //由Random生成随机数
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    //长度为几就循环几次
    for (int i = 0; i < length; ++i) {
      //产生0-61的数字
      int number = random.nextInt(62);
      //将产生的数字通过length次承载到sb中
      sb.append(str.charAt(number));
    }
    //将承载的字符转换成字符串
    return sb.toString();
  }
}
