package com.example.uiautomator.testcase;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.Random;

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
    int step = 10;
    int count = 0;

    int commentCount = 0;
    int shareCount = 0;

    // 启动App
    startAPP();

    // 关注
    int followCount = 0;
    while (followCount < 3 && follow()) {
      followCount++;
    }

    while (count <= 25) {
      // 打开视频
      if (startPlay()) {
        count++;

        // 发表评论
        if (commentCount < 5 && comment()) {
          commentCount++;
        }
        // 分享
        if (shareCount < 5 && share()) {
          shareCount++;
        }
        // 等待视频播放完成
        sleep(35);
      }

      // 返回视频列表
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "返回视频列表");

      // 向上滑动列表
      mDevice.swipe(centerX, startY, centerX, endY, step);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "向上滑动列表");
    }

    // 关闭应用
    closeAPP();
  }

  // com.iqiyi.news:id/tabHome 推荐
  // com.iqiyi.news:id/tabFollow 关注
  // com.iqiyi.news:id/tabRecord +
  // com.iqiyi.news:id/tabDiscover 发现
  // com.iqiyi.news:id/tabMe 我

  /**
   * 关注
   *
   * @return
   */
  private boolean follow() {
    // 切换到关注页面
    UiObject2 follow = findById("tabFollow");
    if (follow == null) {
      Log.e(TAG, "没有关注Tab");
      return false;
    }
    // 如果未选中
    if (!follow.isSelected()) {
      follow.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到关注Tab");
    }

    // RecyclerView 只能这样查找咯
    UiObject2 addFollow = findByText("关注推荐");
    if (addFollow == null) {
      mDevice.pressBack();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "没有关注推荐按钮");
      return false;
    }
    addFollow.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击关注推荐,跳转关注页面");

    // 点击关注
    UiObject2 add = findByText("关注");
    if (add == null) {
      mDevice.pressBack();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "没有可以关注的");
      return false;
    }
    add.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击关注");

    // 如果弹出了对话框 com.iqiyi.news:id/fsg_confirm_btn
    UiObject2 confirm = findById("fsg_confirm_btn");
    if (confirm != null) {
      confirm.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "关闭关注对话框");
    }

    // 返回主页面
    mDevice.pressBack();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "返回主页面");
    return true;
  }

  /**
   * 开始播放
   */
  private boolean startPlay() {
    // 切换到主页面
    UiObject2 home = findById("tabHome");
    if (home == null) {
      Log.e(TAG, "播放失败: 可能未停留在主页");
      return false;
    }
    // 无关当前不是主页
    if (!home.isSelected()) {
      home.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到推荐Tab");
    }

    // 点击评论按钮开始播放 com.iqiyi.news:id/comment_btn
    UiObject2 playBtn = findById("comment_btn");
    if (playBtn == null) {
      Log.e(TAG, "开始播放失败");
      return false;
    }
    // 开始播放视频
    playBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "开始播放视频");
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
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击评论按钮");

    // 输入评论 com.iqiyi.news:id/input_edit_text
    UiObject2 contentText = findById("input_edit_text");
    if (contentText == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    /*"爱奇艺的视频还是不错的,内容很好."*/
    contentText.setText(getComment(new Random().nextInt(10) + 5));
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "填写评论内容");

    // 点击发表评论
    UiObject2 sendBtn = findById("send_btn");
    if (sendBtn == null) {
      Log.e(TAG, "没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击发送,发表评论");

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
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击分享按钮,调用分享对话框");

    // 分享到QQ ID com.iqiyi.news:id/rl_share_qq
    UiObject2 share = findById("rl_share_qq");
    if (share == null) {
      Log.e(TAG, "没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "打开QQ分享");

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      Log.e(TAG, "分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击分享到:我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      Log.e(TAG, "分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      Log.e(TAG, "没有返回按钮");
      return false;
    }
    back.click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享返回");

    sleep(3);
    return true;
  }

  @Override
  String getAPPName() {
    return "爱奇艺纳逗";
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
