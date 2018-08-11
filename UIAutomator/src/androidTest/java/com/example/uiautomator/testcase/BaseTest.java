package com.example.uiautomator.testcase;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

  // 该方法的作用是生成一个随机的int值，该值介于[0,n)的区间，也就是0到n之间的随机int值，包含0而不包含n。
  public final Random random;

  public final Calendar calendar;

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
    calendar = Calendar.getInstance(Locale.CHINA);
  }

  abstract public int start(int count);

  /**
   * @return app名称 如: 微信
   */
  abstract String getAPPName();

  /**
   * @return 包名 如:com.iqiyi.news
   */
  abstract String getPackageName();

  /**
   * @return 是否允许执行
   */
  public boolean avliable() {
    // 文件存在退出应用
    File directory = Environment.getExternalStorageDirectory();
    File file = new File(directory, "shutdown.txt");
    if (file.exists()) {
      log("shutdown文件存在,准备结束运行");
      return false;
    }

    calendar.setTime(new Date());
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    if (hour <= 6 || hour >= 22) {
      log("这个时候了该睡觉了");
      return false;
    }
    return true;
  }

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
      log(getAPPName() + "已经启动");
    } else {
      log(getAPPName() + "启动失败");
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
    log("关闭" + getAPPName());
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
   * 打印元素信息
   *
   * @param object2 子元素
   * @return 详细信息
   */
  public String printObject(UiObject2 object2) {
    StringBuilder builder = new StringBuilder();
    if (object2 != null) {
      builder.append("********************************************************\n");
      builder.append("Class:   ").append(object2.getClassName()).append("  \n");
      builder.append("ID:      ").append(object2.getResourceName()).append("  \n");
      builder.append("Text:    ").append(object2.getText()).append("  \n");
      builder.append("子节点数: ").append(object2.getChildCount()).append("  \n");
      builder.append("布局边界: ").append(object2.getVisibleBounds()).append("  \n");
      /*if (object2.getChildren() != null && object2.getChildren().size() > 0) {
        builder.append("****************************\n");
        printObjects(object2.getChildren());
        builder.append("****************************\n");
      }*/
      builder.append("   \n********************************************************");
    }
    return builder.toString();
  }

  /**
   * 打印元素列表信息
   *
   * @param object2s obj
   * @return 列表信息
   */
  public String printObjects(List<UiObject2> object2s) {
    StringBuilder builder = new StringBuilder();
    if (object2s != null && object2s.size() > 0) {
      for (UiObject2 object2 : object2s) {
        builder.append(printObject(object2));
      }
    }
    return builder.toString();
  }

  /**
   * 打印直接子节点信息
   *
   * @param object2
   * @return
   */
  public String printChidern(UiObject2 object2) {
    StringBuilder builder = new StringBuilder();
    if (object2 != null) {
      builder.append(printObjects(object2.getChildren()));
    }
    return builder.toString();
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
      log("尝试打开app: " + getPackageName());
      myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      mContext.startActivity(myIntent);
      sleep(10);
      mDevice.waitForIdle(timeOut);
    } else {
      log("尝试打开app失败: " + getPackageName());
    }
  }

  /**
   * 根据包名关闭app
   */
  public void closeAPPWithPackageName() {
    try {
      log("尝试关闭app: " + getPackageName());
      mDevice.executeShellCommand("am force-stop " + getPackageName());//通过命令行关闭app
      log("app已经关闭: " + getPackageName());
    } catch (Throwable e) {
      Log.e(TAG, "关闭app失败", e);
      mDevice.pressBack();
      mDevice.pressBack();
    }
  }

  /**
   * 打印日志
   */
  public void log(String message) {
    Log.e(TAG, message);
  }

  /**
   * 通过命令启动App
   */
  private void startAPPWithCMD(String sLaunchActivity) {
    try {
      mDevice.executeShellCommand("am start -n " + getPackageName() + "/" + sLaunchActivity);//通过命令行启动app
    } catch (IOException e) {
      log(getAPPName() + "启动出错");
    }
  }
}
