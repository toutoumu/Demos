package com.example.demo.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class ZhongQingKanDianTest extends BaseTest {

  // 底部工具类容器ID
  private static final String TOOL_BAR_CONTAINER_ID = "qe";

  // 文章列表评论数ID
  private static final String COMMENT_COUNT_ID = "ti";
  // 文章详情页面,评论文本框ID
  private static final String ARTICLE_COMMENT_EDITVIEW_ID = "mp";
  // 文章返回按钮ID
  private static final String ARTICLE_BACK_BUTTON_ID = "i9";

  // 视频列表评论数ID
  private static final String VIDEO_COUNT_ID = "v0";
  // 视频详情页面,评论文本框ID
  private static final String VIDEO_COMMENT_EDITVIEW_ID = "mp";
  // 视频返回按钮ID
  private static final String VIDEO_BACK_BUTTON_ID = "mu";

  private int readCount = 0; // 阅读次数
  private int signCount = 0; // 阅读次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int shareMomey = 0;//晒收入,晒提现
  private int checkCount = 0;

  public ZhongQingKanDianTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 打开app
    startAPPWithPackageName();

    /*// 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 如果已经阅读完成,那么随机阅读几篇
    if (readCount >= 80) {
      repCount = readCount + random.nextInt(10);
      logD("阅读已完成,随机阅读几篇" + (repCount - readCount));
    }*/

    // 执行阅读,播放操作
    while (readCount < repCount) {
      try {
        if (!avliable()) break;

        logD("********************* 第 " + readCount + " 次 *********************");

        // 判断是否已经回到首页
        List<UiObject2> toolbars = checkInMainPage(TOOL_BAR_CONTAINER_ID);
        if (toolbars == null || toolbars.size() == 0) {
          return readCount;
        }

        // 签到 ,最多执行两次
        if (signCount++ <= 1) {
          if (sign()) {
            signCount++;
          }
        }

        // 阅读播放
        if (readCount % 10 <= 5) {
          doPlay(); // 播放
        } else {
          doRead();// 阅读
        }
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭App
    closeAPPWithPackageName();
    return readCount;
  }
  //cn.youth.news:id/qe 底部栏

  /**
   * 检查是否有指定Tab
   *
   * @param tabID tabID
   * @return {@link UiObject2}
   */
  private List<UiObject2> checkInMainPage(String tabID) {
    // 判断是否已经回到首页
    int restartCount = 0;
    while (restartCount < 10) {
      if (!avliable()) return null;
      UiObject2 tab = findById(tabID);
      if (tab == null) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[" + tabID + "]" + restartCount);
        closeDialog();
        tab = findById(tabID);
        if (tab == null) {// 关闭对话框之后再次查找是否已经回到首页
          restartCount++;
          logE("应用可能已经关闭,重新启动");
          startAPPWithPackageName();
          continue;
        }
      }
      return tab.getChildren();
    }
    logE("重启次数" + restartCount + "退出应用");
    return null;
  }

  /**
   * 使用正则表达式提取小括号中的内容
   *
   * @param msg
   * @return
   */
  public static List<String> extractMessageByRegular(String msg) {
    List<String> list = new ArrayList<String>();
    Pattern p = Pattern.compile("(?<=\\()(.+?)(?=\\))");
    Matcher m = p.matcher(msg);
    while (m.find()) {
      list.add(m.group().substring(0, m.group().length()));
    }
    return list;
  }

  /**
   * 检测已经阅读了多少次
   *
   * @return
   */
  @Override
  public boolean doCheck() {
    if (true) {
      if (!avliable()) return false;
      logD("暂时未做处理");
      return true;
    }
    try {
      startAPPWithPackageName();
      // 切换到文章列表
      List<UiObject2> toolBar = checkInMainPage(TOOL_BAR_CONTAINER_ID);
      if (toolBar == null || toolBar.size() == 0) return false;

      // 如果当前不是文章列表 ,切换到文章列表
      if (!toolBar.get(0).isSelected()) {
        toolBar.get(0).click();
        sleep(3);
        mDevice.waitForIdle(timeOut);
        logD("切换到文章列表");
      }

      // 打开查看今天阅读量
      UiObject2 tuijian = findById("tuijian_jinbi");
      if (tuijian == null) {
        logD("检测失败,没找到金币详情按钮");
        return false;
      }
      tuijian.click();
      sleep(10);
      mDevice.waitForIdle(timeOut);
      logD("打开查看今天阅读量");

      List<UiObject2> countText = mDevice.wait(Until.findObjects(By.textContains("今日奖励次数")), 1000 * 10);
      for (UiObject2 uiObject2 : countText) {
        if (uiObject2.getText() != null) {
          List<String> list = extractMessageByRegular(uiObject2.getText());
          readCount += Integer.parseInt(list.get(0).replace("次", ""));
        }
      }
      pressBack("已经阅读次数读取值:" + readCount, false);
      return true;
    } catch (Exception e) {
      logE("获取阅读次数失败", e);
      return false;
    }
  }

  /**
   * 阅读文章
   *
   * @return 成功
   */
  private boolean doRead() {
    List<UiObject2> toolbars = checkInMainPage(TOOL_BAR_CONTAINER_ID);
    if (toolbars == null || toolbars.size() == 0) {
      logE("没有文章列表,可能不在首页");
      return false;
    }
    UiObject2 tab3 = toolbars.get(0);
    if (tab3 == null) {
      logE("没有文章列表,底部栏没有");
      return false;
    }
    if (!tab3.isSelected()) {
      tab3.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("切换到[文章列表]");
    }

    // 向上滚动列表
    int startY = height / 2;
    int endY = height / 4;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("列表向上滑动");

    // 打开文章 评论数id ti
    UiObject2 read = findById(COMMENT_COUNT_ID);
    if (read == null) {
      logE("阅读失败,没有评论按钮");
      return false;
    }
    read.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);

    //如果有评论按钮 cn.youth.news:id/mp
    if (findById(ARTICLE_COMMENT_EDITVIEW_ID, 3) != null) {// 文章页面
      logD("打开文章,开始阅读");
      int count = random.nextInt(4);// 滚动次数
      while (count++ < 13) {
        long start = System.currentTimeMillis();
        mDevice.swipe(centerX, startY, centerX, endY, 50);
        long spend = System.currentTimeMillis() - start; // 滚动花费时间
        if (spend < 3000) {// 如果时间间隔小于 3 秒
          sleep(((double) (3000 - spend) / 1000.0));
        }
        mDevice.waitForIdle(timeOut);
        Log.v(TAG, "滚动花费时间:" + spend);
      }
      readCount++;

      // 点击返回按钮
      UiObject2 back = findById(ARTICLE_BACK_BUTTON_ID);
      if (back != null) {
        back.click();
        sleep(1);
      } else {
        pressBack("没有返回按钮,模拟返回按钮操作", false);
      }
      logD("阅读完成,返回首页\n:");
    } else { // 页面可能未打开
      pressBack("返回首页:可能没有打开页面\n:", false);
    }
    return true;
  }

  /**
   * 播放视频
   *
   * @return 成功
   */
  private boolean doPlay() {
    List<UiObject2> toolbars = checkInMainPage(TOOL_BAR_CONTAINER_ID);
    if (toolbars == null || toolbars.size() == 0) {
      logE("没有[视频列表],可能不在首页");
      return false;
    }
    UiObject2 tab3 = toolbars.get(1);
    if (tab3 == null) {
      logE("没有[视频列表],底部栏没有");
      return false;
    }
    if (!tab3.isSelected()) {
      tab3.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("切换到[视频列表]");
    }

    // 向上滚动列表
    int startY = height / 2;
    int endY = height / 4;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("列表向上滑动");

    // 打开文章 评论数id ti
    UiObject2 read = findById(VIDEO_COUNT_ID);
    if (read == null) {
      logE("阅读失败,没有评论按钮");
      return false;
    }
    read.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);

    //如果有评论按钮 cn.youth.news:id/mp
    if (findById(VIDEO_COMMENT_EDITVIEW_ID, 3) != null) {// 文章页面
      logD("打开文章,开始阅读");
      sleep(30 + random.nextInt(10));
      readCount++;

      // 点击返回按钮
      UiObject2 back = findById(VIDEO_BACK_BUTTON_ID);
      if (back != null) {
        back.click();
        sleep(1);
      } else {
        pressBack("没有返回按钮,模拟返回按钮操作", false);
      }
      logD("阅读完成,返回首页\n:");
    } else { // 页面可能未打开
      pressBack("返回首页:可能没有打开页面\n:", false);
    }
    return true;
  }

  /**
   * 关闭对话框
   */
  private boolean closeDialog() {
    // 可能没有回到首页,点击返回关闭对话框
    pressBack("点击返回,关闭对话框", true);
    return true;
  }

  /**
   * 签到
   *
   * @return
   */
  private boolean sign() {
    List<UiObject2> toolbars = checkInMainPage(TOOL_BAR_CONTAINER_ID);
    if (toolbars == null || toolbars.size() == 0) {
      logE("没有任务中心");
      return false;
    }
    UiObject2 tab3 = toolbars.get(3);
    if (tab3 == null) {
      logE("没有任务中心");
      return false;
    }

    if (!tab3.isSelected()) {
      tab3.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("切换到[任务中心]");
    }

    // 文本  signH1
    UiObject2 task = findByText("任务中心");
    if (task == null) {
      logE("没有任务中心,查看是不是文本内容变了");
      return true;
    }
    task.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("跳转到任务中心页面");

    UiObject2 sign = findByText("已签到");
    if (sign != null) {
      pressBack("已经签到过了", false);
      return true;
    }

    sign = findByText("签到");
    if (sign == null) {
      pressBack("没有签到按钮,或者是文本错了", true);
      return false;
    }
    sign.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击签到");

    pressBack("签到结束,返回首页", false);
    return true;
  }

  @Override
  public String getAPPName() {
    return "中青看点";
  }

  @Override
  public String getPackageName() {
    return "cn.youth.news";
  }
}
