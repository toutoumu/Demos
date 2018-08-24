package com.example.demo.testcase;

import android.support.test.uiautomator.UiObject2;
import java.util.List;

/**
 * 爱奇艺测试 1000金币=1元 提现20元起 每日最高领取400金币, 徒弟阅读一次贡献5金币 最多一百金币
 * 阅读20篇 100
 * 徒弟阅读  100
 */
public class HaHaShiPinTest extends BaseTest {

  // 次数统计
  private int readCount = 0; // 阅读次数
  private int checkCount = 0;

  public HaHaShiPinTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 最多一次30片
    if (repCount >= 30) {
      repCount = 30;
      logD("阅读已完成,随机阅读几篇:" + (repCount - readCount));
    }

    // 判断是否已经回到首页
    UiObject2 tabs = checkInMainPage();
    if (tabs == null) {
      logE("检查是否在首页失败,退出");
      return 0;
    }

    // 播放视频(评论,分享)
    while (readCount <= repCount) {
      try {
        if (!avliable()) break;

        logD("********************* 第 " + readCount + " 次 *********************");

        // 每次循环都检查是否在播放,不在播放那么开启播放,播放失败直接退出
        if (!checkIsPlay()) {
          logE("检查播放失败,退出");
          return readCount;
        }

        // 播放
        if (doPlay()) {
          readCount++;
        }
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭应用
    closeAPPWithPackageName();
    return readCount;
  }

  /**
   * 检查是否有底部导航栏,来判断是否回到首页
   * 注意不判断是否在哪个tab
   *
   * @return {@link UiObject2}
   */
  private UiObject2 checkInMainPage() {
    // 判断是否已经回到首页
    logD("判断是否已经回到首页");
    int restartCount = 0;
    while (restartCount < 10) {
      UiObject2 tabs = findById("tab_1_label");// 底部导航栏ID为Text
      if (tabs == null) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[底部导航栏]:" + restartCount);
        closeDialog();
        tabs = findById("tab_1_label");
        if (tabs == null) {// 关闭对话框之后再次查找是否已经回到首页
          restartCount++;
          logE("应用可能已经关闭,重新启动");
          startAPPWithPackageName();
          continue;
        }
      }
      logD("当前已经在首页");
      return tabs;
    }
    logE("重启次数" + restartCount + "退出应用");
    return null;
  }

  /**
   * 检查各项的执行情况并赋值
   */
  public boolean doCheck() {
    try {
      startAPPWithPackageName();
      // 检测是否已经回到主界面
      return checkInMainPage() != null;
    } catch (Exception e) {
      logE("检测出错了", e);
      return false;
    }
  }

  private boolean checkIsPlay() {
    // 是否正在播放
    UiObject2 comment = findById("detail_bottom_comment_input", 3);
    if (comment != null) {
      logD("正在播放界面");
      return true;
    }

    // 进行n此点击,
    int repeatCount = 0;
    while (repeatCount++ < 10) {
      // 是否在首页
      UiObject2 tabMain = checkInMainPage();
      if (tabMain == null) {
        logE("当前不在主页");
        return false;
      }

      // 检测是否在首页第一个tab
      List<UiObject2> custom_text = findListById("custom_text", 3);
      boolean found = false;
      if (custom_text != null && custom_text.size() > 0) {
        for (UiObject2 uiObject2 : custom_text) {
          if ("发现".equals(uiObject2.getText())) {
            found = true;
          }
        }
      }
      // 如果不在主页第一个tab
      if (!found) {
        logE("不在主页第一个Tab");
        UiObject2 mainTab = findById("tab_1_label");
        if (mainTab != null) {
          mainTab.click();
          sleep(5);
          logE("切换到主页,第一个tab");
          continue;
        }
      }

      // 第一次不向上滑动
      if (repeatCount != 1) {
        // 向上滑动列表
        int startY = height * 2 / 3;
        int endY = height / 10;
        mDevice.swipe(centerX, startY, centerX, endY, 20);
        sleep(1);
        mDevice.waitForIdle(timeOut);
        logD("向上滑动视频列表");
      }

      // 点击封面尝试播放
      UiObject2 itemCover = findById("index_feed_item_cover");
      if (itemCover == null) {
        logE("是不是有对话框,暂时未发现可以播放");
        closeDialog();
        continue;
      }
      itemCover.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("点击封面,尝试开始播放");

      // 检测是否打开视频播放了
      comment = findById("detail_bottom_comment_input", 3);
      if (comment != null) {
        logD("开始播放");
        return true;
      }

      // 如果没开始播放,且当前已经不在主页第一个Tab,那么点击返回
      UiObject2 mianTab = findById("tab_1_label");
      if (mianTab == null) {
        // 如果打开的不是播放页面
        pressBack("没有打开视频播放", true);
      }
    }// end of while
    return false;
  }

  /**
   * 关闭对话框
   */
  private void closeDialog() {
    pressBack("点击返回,尝试关闭对话框", true);
  }

  /**
   * 开始播放
   */
  private boolean doPlay() {
    // 等待播放
    sleep(20 + random.nextInt(5));

    // 向上滑动列表
    int startY = height * 2 / 3;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 10);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("向上滑动,播放下一个视频");

    return true;
  }

  @Override
  public String getAPPName() {
    return "全民小视频";
  }

  @Override
  public String getPackageName() {
    return "com.baidu.minivideo";
  }
}
