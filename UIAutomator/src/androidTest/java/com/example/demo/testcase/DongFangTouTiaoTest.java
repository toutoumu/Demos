package com.example.demo.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.widget.RadioButton;
import java.util.List;

/**
 * 东方头条测试 提成较高 1000金币=1元 徒弟阅读提成是徒弟两倍
 * 播放,阅读,晒收入(3次),推送
 */
public class DongFangTouTiaoTest extends BaseTest {

  private int readCount = 0; // 阅读次数
  private int score = 0; // 已经获得的分数
  private int checkCount; // 检查次数

  public DongFangTouTiaoTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 如果已经阅读完成(1000分),那么随机阅读几篇
    if (score >= 800) {
      repCount = readCount + random.nextInt(5);
      logD("阅读已完成,随机阅读几篇" + (repCount - readCount));
    }

    // 执行阅读,播放操作
    while (readCount < repCount) {
      try {
        if (!avliable()) break;

        logD("********************* 第 " + readCount + " 次 *********************");
        List<UiObject2> radioButtons = checkInMainPage();
        if (radioButtons == null || radioButtons.size() == 0) {
          return readCount;
        }
        doPlay(radioButtons); // 播放
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {// 如果断开了连接
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    closeAPPWithPackageName();

    return readCount;
  }

  @Override
  public boolean doCheck() {
    if (!avliable()) return false;
    try {
      startAPPWithPackageName();

      List<UiObject2> radioButtons = checkInMainPage();
      if (radioButtons == null || radioButtons.size() == 0) {
        return false;
      }

      // 找到视频tab
      UiObject2 me = null;
      for (UiObject2 radioButton : radioButtons) {
        if ("我的".equals(radioButton.getText())) {
          me = radioButton;
          break;
        }
      }
      if (me == null) {
        logE("播放失败,没有找到[我的]Tab");
        return false;
      }
      // 如果当前不是视频列表 ,切换到视频列表
      if (!me.isChecked()) {
        me.click();
        sleep(10);
        mDevice.waitForIdle(timeOut);
        logD("切换到[我的]");
      }

      // 读取分数
      UiObject2 scoreText = findById("rr");
      if (scoreText == null) {
        logE("检查出错,没有分数显示,请检查ID值是否已经变更");
        return false;
      }
      try {
        String trim = scoreText.getText().trim();
        score = Integer.parseInt(trim);
      } catch (Exception e) {
        logE("检查出错,数值转换错误", e);
        return false;
      }
      logD("读取到的分数为:" + score);
      return true;
    } catch (Exception e) {
      logE("检查失败", e);
      return false;
    }
  }

  /**
   * 检查是否有指定Tab
   *
   * @return {@link UiObject2}
   */
  private List<UiObject2> checkInMainPage() {
    // 判断是否为首页 视频 任务 我的 都是RadioButton
    // 判断是否已经回到首页
    int restartCount = 0;
    while (restartCount < 10) {
      if (!avliable()) return null;
      List<UiObject2> radioButtons = mDevice.findObjects(By.clazz(RadioButton.class));
      if (radioButtons == null || radioButtons.size() != 3) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[" + "radioButtons" + "]" + restartCount);
        closeDialog();
        radioButtons = mDevice.findObjects(By.clazz(RadioButton.class));
        if (radioButtons == null || radioButtons.size() != 3) {// 关闭对话框之后再次查找是否已经回到首页
          restartCount++;
          logE("应用可能已经关闭,重新启动");
          startAPPWithPackageName();
          continue;
        }
      }
      return radioButtons;
    }
    return null;
  }

  /**
   * 播放视频
   *
   * @param radioButtons 底部导航栏
   * @return 成功
   */
  private boolean doPlay(List<UiObject2> radioButtons) {
    if (radioButtons == null || radioButtons.size() != 3) {
      logE("视频播放失败:没有底部栏");
      return false;
    }

    // 找到视频tab
    UiObject2 videoTab = null;
    for (UiObject2 radioButton : radioButtons) {
      if ("视频".equals(radioButton.getText())) {
        videoTab = radioButton;
        break;
      }
    }
    if (videoTab == null) {
      logE("播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!videoTab.isChecked()) {
      videoTab.click();
      sleep(10);
      mDevice.waitForIdle(timeOut);
      logD("切换到视频列表");
    }

    // 需要向上滚动列表
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("视频列表向上滑动");

    // 点击播放 播放按钮 com.songheng.eastnews:id/aez
    UiObject2 play = findById("aez");
    if (play == null) {
      logE("播放失败:没有播放按钮");
      return false;
    }

    play.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击开始播放视频");

    // 标志性的 转转转的图片 com.songheng.eastnews:id/a5q
    if (findById("a5q", 3) != null) {// 视频页面
      sleep(45 + random.nextInt(10));
      readCount++;
      // 发表评论
      /*if (commentCount < 2 && commentVideo()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareVideo()) {
        shareCount++;
      }*/

      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logD("播放完成,返回首页\n:");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logE("返回首页:打开的不是视频页面\n:");
    }
    return true;
  }

  /**
   * 关闭对话框
   */
  private void closeDialog() {
    UiObject2 cancel = findByText("取  消", 5);
    if (cancel == null) {
      pressBack("点击返回,尝试关闭对话框", true);
    } else {
      while (cancel != null) {
        cancel.click();
        logD("点击[取  消]关闭推送对话框");
        cancel = findByText("取  消", 5);
      }
    }
  }

  @Override
  public String getAPPName() {
    return "东方头条";
  }

  @Override
  public String getPackageName() {
    return "com.songheng.eastnews";
  }
}
