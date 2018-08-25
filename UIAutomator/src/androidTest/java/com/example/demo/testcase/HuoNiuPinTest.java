package com.example.demo.testcase;

import android.support.test.uiautomator.UiObject2;
import java.util.List;

/**
 * 爱奇艺测试 1000金币=1元 提现20元起 每日最高领取400金币, 徒弟阅读一次贡献5金币 最多一百金币
 * 阅读20篇 100
 * 徒弟阅读  100
 */
public class HuoNiuPinTest extends BaseTest {

  // 次数统计
  private int readCount = 0; // 阅读次数
  private int checkCount = 0;

  public HuoNiuPinTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 播放视频(评论,分享)
    while (readCount < repCount) {
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
      if (!avliable()) return null;
      UiObject2 tabs = findById("tv_tab");// 底部导航栏ID为Text
      if (tabs == null) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[底部导航栏]:" + restartCount);
        closeDialog();
        tabs = findById("tv_tab");
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
    if (!avliable()) return false;
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
    UiObject2 comment = findById("tv_vote_count", 3);
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
      List<UiObject2> wallet = findListById("tv_tab", 3);
      if (wallet != null || wallet.size() > 0) {// 如果不在主页第一个tab
        for (UiObject2 tab : wallet) {
          if (tab.getText().equals("首页")) {
            if (tab.isSelected()) {
              logD("正在播放页面");
              return true;
            } else {
              tab.click();
              sleep(3);
              mDevice.waitForIdle(timeOut);
              logD("切换到第一个Tab");
            }
          }
        }
        continue;
      }

      // 如果向左滑动了
      UiObject2 icon = findById("cir_user_pic", 5);
      if (icon != null) {
        // 向右滑动,关闭可能的广告页面
        int startX = width / 10;
        int endX = width * 2 / 3;
        mDevice.swipe(startX, centerY, endX, centerY, 10);
        continue;
      }
      icon = findById("tv_diamond_count", 5);
      if (icon != null) {
        // 向右滑动,关闭可能的广告页面
        int startX = width / 10;
        int endX = width * 2 / 3;
        mDevice.swipe(endX, centerY, startX, centerY, 10);
        continue;
      }
      pressBack("点击返回,向右滑动,回到首页", false);
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
    sleep(15 + random.nextInt(5));

    if (readCount % 12 == 0) {
      UiObject2 comment = findById("tv_vote_count", 3);
      if (comment != null) {
        comment.click();
        sleep(1);
        mDevice.waitForIdle(timeOut);
        logE("给他砖石");
        return true;
      }
    }

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
    return "火牛视频";
  }

  @Override
  public String getPackageName() {
    return "com.waqu.android.firebull";
  }
}
