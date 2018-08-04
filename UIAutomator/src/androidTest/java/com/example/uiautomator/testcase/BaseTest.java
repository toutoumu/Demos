package com.example.uiautomator.testcase;

import android.content.Context;
import android.content.Intent;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.io.IOException;
import java.util.Random;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.uiautomator.UiDevice.getInstance;

public abstract class BaseTest {
  public final String TAG = this.getClass().getName();

  public final static int timeOut = 1000; // 每次等待时间

  // 获取宽度高度
  public final int height; // 屏幕高度
  public final int width; // 屏幕宽度

  public final int centerX; // 中间位置
  public final int centerY; // 中间位置

  public final Random random;

  public final UiDevice mDevice;//获取设备用例

  public BaseTest() {
    mDevice = getInstance(getInstrumentation());//获取设备用例

    // 获取宽度高度
    height = mDevice.getDisplayHeight(); // 屏幕高度
    width = mDevice.getDisplayWidth(); // 屏幕宽度

    // 中间点
    centerX = width / 2; // 中间位置
    centerY = height / 2; // 中间位置

    random = new Random();
  }

  abstract public void start();

  /**
   * @return app名称 如: 微信
   */
  abstract String getAPPName();

  /**
   * @return 包名 如:com.iqiyi.news
   */
  abstract String getPackageName();

  /**
   * 根据ID查找, 超时时间为10秒
   *
   * @param id id
   * @return ui
   */
  public UiObject2 findById(String id) {
    return findById(id, 10);
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
    return mDevice.wait(condition, 1000 * second);
  }

  public UiObject2 findByClass(Class clazz) {
    return findByClass(clazz, 10);
  }

  /**
   * 根据ID查找, 超时时间为10秒
   *
   * @param clazz clazz
   * @param second 多少秒超时
   * @return
   */
  public UiObject2 findByClass(Class clazz, int second) {
    BySelector selector = By.clazz(clazz);
    SearchCondition<UiObject2> condition = Until.findObject(selector);
    return mDevice.wait(condition, 1000 * second);
  }

  /**
   * 根据文本内容查找
   *
   * @param text
   * @return
   */
  public UiObject2 findByText(String text) {
    return findByText(text, 10);
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
    UiObject2 share = mDevice.wait(condition, 1000 * second);
    return share;
  }

  /**
   * 线程等待多少秒
   *
   * @param second
   */
  public void sleep(double second) {
    try {
      Thread.sleep((long) (1000 * second));
    } catch (InterruptedException e) {
      Log.e(TAG, "sleep出错了:" + getPackageName(), e);
    }
  }

  /**
   * 根据应用名称启动app,App必须放在首页
   */
  public void startAPP() {
    // 回到桌面首页
    closeAPP();

    // 打开app
    BySelector selector = By.text(getAPPName());
    SearchCondition<UiObject2> condition = Until.findObject(selector);
    UiObject2 startIcon = mDevice.wait(condition, 1000 * 15);
    if (startIcon != null) {
      startIcon.click();
      sleep(10);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, getAPPName() + "已经启动");
    } else {
      Log.e(TAG, getAPPName() + "启动失败");
    }
  }

  /**
   * 关闭app
   */
  public void closeAPP() {
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressBack();
    mDevice.waitForIdle(1000);
    mDevice.pressBack();
    mDevice.waitForIdle(1000);
    mDevice.pressBack();
    mDevice.waitForIdle(1000);
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressBack();
    mDevice.waitForIdle(20);
    mDevice.pressHome();
    mDevice.waitForIdle(1000);
    mDevice.pressHome();
    mDevice.waitForIdle(1000);
  }

  /**
   * 构建评论内容
   *
   * @param length 长度
   * @return 内容
   */
  public String getComment(int length) {
    //定义一个字符串（A-Z，a-z，0-9）即62位；
    String seed = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    int strLen = seed.length();
    //由Random生成随机数
    StringBuilder sb = new StringBuilder();
    //长度为几就循环几次
    for (int i = 0; i < length; ++i) {
      //产生0-61的数字
      int number = random.nextInt(strLen);
      //将产生的数字通过length次承载到sb中
      sb.append(seed.charAt(number));
    }
    //将承载的字符转换成字符串
    return sb.toString();
  }

  /**
   * 根据包名启动app
   */
  public void startAPPWithPackageName() {
    mDevice.pressHome();
    mDevice.pressHome();
    sleep(1);
    mDevice.waitForIdle(1000);
    Context mContext = getInstrumentation().getContext();
    Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(getPackageName());  //通过Intent启动app
    if (myIntent != null) {
      Log.e(TAG, "尝试打开app: " + getPackageName());
      myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      mContext.startActivity(myIntent);
      sleep(10);
      mDevice.waitForIdle(timeOut);
    } else {
      Log.e(TAG, "尝试打开app失败: " + getPackageName());
    }
  }

  /**
   * 根据包名关闭app
   */
  public void closeAPPWithPackageName() {
    try {
      Log.e(TAG, "尝试关闭app: " + getPackageName());
      mDevice.executeShellCommand("am force-stop " + getPackageName());//通过命令行关闭app
      Log.e(TAG, "app已经关闭: " + getPackageName());
    } catch (Throwable e) {
      Log.e(TAG, "关闭app失败", e);
      mDevice.pressBack();
      mDevice.pressBack();
    }
  }

  /**
   * 通过命令启动App
   */
  private void startAPPWithCMD(String sLaunchActivity) {
    try {
      mDevice.executeShellCommand("am start -n " + getPackageName() + "/" + sLaunchActivity);//通过命令行启动app
    } catch (IOException e) {
      Log.e(TAG, getAPPName() + "启动出错", e);
    }
  }
}
