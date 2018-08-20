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
import android.widget.ImageView;
import android.widget.TextView;
import com.example.uiautomator.testcase.log.LogUtil;
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
  public LogUtil log;
  public final String TAG = this.getClass().getSimpleName();

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
    log = new LogUtil(TAG);
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

  /**
   * 分享到QQ空间
   *
   * @param share
   * @return
   */
  public boolean qqZoneShare(UiObject2 share) {
    if (share == null) {
      logE("没有打开QQ空间分享,的按钮");
      return false;
    }
    share.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("打开[QQ空间]分享,离开当前应用");

    // 点击发表 QQ空间
    UiObject2 publish = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq:id/ivTitleBtnRightText")), 1000 * 10);
    if (publish == null) {
      logE("分享到[QQ空间]失败");
      return false;
    }
    publish.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("点击[发表]选项");
    return true;
  }

  /**
   * qq分享
   *
   * @param share 掉起QQ分享的按钮
   * @return 是否分享成功
   */
  public boolean qqShare(UiObject2 share) {
    if (share == null) {
      logE("没有打开QQ分享,的按钮");
      return false;
    }
    share.click();
    sleep(4);
    mDevice.waitForIdle(timeOut);
    logD("打开QQ分享,离开当前应用");

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      logE("分享到[我的电脑]失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    logD("点击[我的电脑]选项");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      logE("分享到[我的电脑]确认失败");
      return false;
    }
    confirm.click();
    mDevice.waitForIdle(timeOut);
    logD("分享到[我的电脑]确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      logE("没有返回按钮");
      return false;
    }
    back.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("QQ完成分享返回");
    return true;
  }

  /**
   * 执行返回操作,打印日志,暂停1秒
   *
   * @param message
   */
  public void pressBack(String message, boolean error) {
    mDevice.pressBack();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    if (error) {
      logE(message);
    } else {
      logD(message);
    }
  }

  abstract public int start(int count);

  /**
   * @return app名称 如: 微信
   */
  public abstract String getAPPName();

  /**
   * @return 包名 如:com.iqiyi.news
   */
  public abstract String getPackageName();

  /**
   * @return 是否允许执行
   */
  public boolean avliable() {
    // 文件存在退出应用
    File directory = new File(Environment.getExternalStorageDirectory(), File.separator + "aaaaaa" + File.separator);
    File file = new File(directory, "shutdown.txt");
    if (file.exists()) {
      logD("shutdown文件存在,准备结束运行");
      return false;
    }

    // 晚上12点到早上6点不允许使用
    calendar.setTime(new Date());
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    if (hour <= 5 || hour == 24) {
      logD("这个时候了该睡觉了" + hour);
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

  /**
   * 根据ID查找, 超时时间为10秒
   *
   * @param id id
   * @return ui
   */
  public List<UiObject2> findListById(String id) {
    return findListById(id, 10);
  }

  /**
   * 根据ID查找, 超时时间为10秒
   *
   * @param id id
   * @param second 多少秒超时
   * @return
   */
  public List<UiObject2> findListById(String id, int second) {
    BySelector selector = By.res(getPackageName(), id);
    SearchCondition<List<UiObject2>> condition = Until.findObjects(selector);
    return mDevice.wait(condition, 1000 * second);
  }

  public UiObject2 findByClass(Class clazz) {
    return findByClass(clazz, 10);
  }

  public List<UiObject2> findListByClass(Class clazz) {
    return findListByClass(clazz, 10);
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
   * 根据ID查找, 超时时间为10秒
   *
   * @param clazz clazz
   * @param second 多少秒超时
   * @return
   */
  public List<UiObject2> findListByClass(Class clazz, int second) {
    BySelector selector = By.clazz(clazz);
    SearchCondition<List<UiObject2>> condition = Until.findObjects(selector);
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
   * @return
   */
  public List<UiObject2> findListByText(String text) {
    return findListByText(text, 10);
  }

  /**
   * 根据文本内容查找
   *
   * @param text
   * @param second 多少秒超时
   * @return
   */
  public List<UiObject2> findListByText(String text, int second) {
    BySelector selector = By.text(text);
    SearchCondition<List<UiObject2>> condition = Until.findObjects(selector);
    List<UiObject2> share = mDevice.wait(condition, 1000 * second);
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
      logE("sleep出错了:" + getPackageName(), e);
    }
  }

  /**
   * 根据应用名称启动app,App必须放在首页
   */
  public void startAPP() {
    logD("\n");
    logD("\n");
    logD("\n");
    logD("启动" + getAPPName() + "前,如果已经打开那么先关闭");
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
      logD(getAPPName() + "已经启动");
    } else {
      logD(getAPPName() + "启动失败");
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
    logD("关闭" + getAPPName());
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
        if (object2.getResourceName() != null && !object2.getResourceName().contains("com.android.systemui:id")) {
          builder.append(printObject(object2));
        }
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
    mDevice.waitForIdle(timeOut);
    Context mContext = getInstrumentation().getContext();
    Intent myIntent = mContext.getPackageManager().getLaunchIntentForPackage(getPackageName());  //通过Intent启动app
    if (myIntent != null) {
      logD("***************************************" + "尝试打开: " + getAPPName() + "***************************************");
      myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      mContext.startActivity(myIntent);
      sleep(10);
      mDevice.waitForIdle(timeOut);
    } else {
      logE("尝试打开: " + getPackageName() + "失败");
    }
  }

  /**
   * 根据包名关闭app
   * 通过命令行关闭app
   */
  public void closeAPPWithPackageName() {
    try {
      logD(" *********************" + "尝试关闭: " + getAPPName() + " *********************");
      mDevice.executeShellCommand("am force-stop " + getPackageName());
      logD(" *********************" + "已经关闭: " + getAPPName() + " *********************\n*\n");
    } catch (Exception e) {
      logE("关闭失败:" + getAPPName(), e);
      closeAPP();
    }
  }

  /**
   * 根据包名关闭app
   */
  public void closeAPPWithPackageName(String packageName) {
    try {
      logD("尝试关闭app: " + packageName);
      mDevice.executeShellCommand("am force-stop " + packageName);//通过命令行关闭app
      sleep(2);
      mDevice.waitForIdle(timeOut);
      logD(packageName + "已经关闭");
    } catch (Exception e) {
      logE("关闭:" + packageName + "失败", e);
      // mDevice.pressBack();
      // mDevice.pressBack();
      // closeAPP();
    }
  }

  public String printImage() {
    return printObjects(findListByClass(ImageView.class));
  }

  public String printTextView() {
    return printObjects(findListByClass(TextView.class));
  }

  /**
   * 打印日志
   */
  public void logD(String message) {
    log.d(message);
  }

  public void logE(Object message) {
    log.e(message);
  }

  public void logE(String message, Exception e) {
    log.e(message, e);
  }

  /**
   * 通过命令启动App
   */
  private void startAPPWithCMD(String sLaunchActivity) {
    try {
      mDevice.executeShellCommand("am start -n " + getPackageName() + "/" + sLaunchActivity);//通过命令行启动app
    } catch (IOException e) {
      logE(getAPPName() + "启动出错", e);
    }
  }
}
