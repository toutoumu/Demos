package com.example.uiautomator.testcase;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

/**
 * 爱奇艺测试
 */
public class AiQiYiTest extends BaseTest {
  private final static int timeOut = 1000; // 每次等待时间
  private final String packageName = "com.iqiyi.news";
  private final UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());//获取设备用例

  // com.iqiyi.news:id/comment_btn 评论按钮
  public void start() {
    // 获取宽度高度
    int h = mDevice.getDisplayHeight(); // 屏幕高度
    int w = mDevice.getDisplayWidth(); // 屏幕宽度

    int centerX = w / 2; // 中间位置
    int centerY = h / 2; // 中间位置

    int dX = w / 4; // 偏移
    int dY = (int) (h / 2.5); // 偏移 这个越大移动距离越大

    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = centerY;
    int endY = startY - dY;
    int step = 30;
    int count = 0;

    // 启动App
    startAPP();

    while (count <= 25) {
      // 打开视频
      if (startPlay()) {
        count++;

        // 分享
        share();

        // 发表评论
        comment();

        // 等待视频播放完成
        sleep(35);
      }

      // 返回视频列表
      Log.e(TAG, "返回视频列表");
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);

      // 向上滑动列表
      Log.e(TAG, "向上滑动列表");
      mDevice.swipe(centerX, startY, centerX, endY, step);
      mDevice.waitForIdle(timeOut * 3);
    }

    // 关闭应用
    closeAPP();
  }

  /**
   * 开始播放
   */
  private boolean startPlay() {
    // com.iqiyi.news:id/comment_btn
    UiObject2 playBtn = findById("comment_btn");
    if (playBtn == null) {
      Log.e(TAG, "开始播放失败");
      return false;
    }
    // 开始播放视频
    Log.e(TAG, "开始播放视频");
    playBtn.click();
    mDevice.waitForIdle(timeOut);
    sleep(3);
    return true;
  }

  /**
   * 发表评论
   *
   * @return
   */
  private boolean comment() {
    // 点击输入评论文本框 com.iqiyi.news:id/input_click
    UiObject2 commentBtn = findById("input_click");
    if (commentBtn == null) {
      Log.e(TAG, "没有评论按钮");
      return false;
    }
    Log.e(TAG, "点击评论按钮");
    commentBtn.click();
    mDevice.waitForIdle(timeOut);

    // 输入评论 com.iqiyi.news:id/input_edit_text
    UiObject2 contentText = findById("input_edit_text");
    if (contentText == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    Log.e(TAG, "填写评论内容");
    contentText.setText("爱奇艺的视频还是不错的,内容很好.");
    mDevice.waitForIdle(timeOut);

    // 点击发表评论
    UiObject2 sendBtn = findById("send_btn");
    if (sendBtn == null) {
      Log.e(TAG, "没有发表评论按钮");
      return false;
    }
    Log.e(TAG, "发表评论");
    sendBtn.click();
    mDevice.waitForIdle(timeOut);

    sleep(3);
    return true;
  }

  /**
   * 发表评论
   *
   * @return
   */
  private boolean share() {

    // 分享按钮ID  com.iqiyi.news:id/news_article_footer_shareContainer
    UiObject2 shareBtn = findById("news_article_footer_shareContainer");
    if (shareBtn == null) {
      Log.e(TAG, "没有分享按钮");
      return false;
    }
    Log.e(TAG, "点击分享按钮");
    shareBtn.click();
    mDevice.waitForIdle(timeOut);

    // 分享到QQ ID com.iqiyi.news:id/rl_share_qq
    UiObject2 share = findById("rl_share_qq");
    if (share == null) {
      Log.e(TAG, "没有打开QQ分享");
      return false;
    }
    Log.e(TAG, "打开QQ分享");
    share.click();
    mDevice.waitForIdle(timeOut);

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      Log.e(TAG, "分享到我的电脑失败");
      return false;
    }
    Log.e(TAG, "分享到我的电脑");
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      Log.e(TAG, "分享到我的电脑确认失败");
      return false;
    }
    Log.e(TAG, "分享到我的电脑确认");
    confirm.click();
    mDevice.waitForIdle(timeOut);

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      Log.e(TAG, "没有返回按钮");
      return false;
    }
    Log.e(TAG, "分享返回");
    back.click();
    mDevice.waitForIdle(timeOut);

    sleep(3);
    return true;
  }

  @Override
  String getPackageName() {
    return packageName;
  }

  @Override
  UiDevice getDevice() {
    return mDevice;
  }
}
