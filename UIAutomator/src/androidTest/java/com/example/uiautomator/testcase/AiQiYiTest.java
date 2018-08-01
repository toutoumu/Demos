package com.example.uiautomator.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.Random;

/**
 * 爱奇艺测试
 */
public class AiQiYiTest extends BaseTest {

  // 次数统计
  private int signCount = 0;
  private int playCount = 0;
  private int followCount = 0;
  private int commentCount = 0;
  private int shareCount = 0;

  public AiQiYiTest() {
    super();
  }

  @Override
  public void start() {
    // 启动App
    startAPP();

    // 签到
    while (signCount++ < 2 && !sign()) {
      closeAPP();
      startAPP();
    }

    // 关注
    while (followCount < 5 && follow()) {
      followCount++;
    }

    // 播放视频(评论,分享)
    while (playCount <= 25) {
      doPlay();
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
   * 签到
   */
  private boolean sign() {
    // 切换到我Tab
    UiObject2 follow = findById("tabMe");
    if (follow == null) {
      Log.e(TAG, "签到失败,没有[我]Tab");
      return false;
    }
    if (!follow.isSelected()) {
      follow.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到[我]Tab");
    }

    // 签到
    UiObject2 obtain = findByText("领取");
    if (obtain == null) {
      mDevice.pressBack();
      Log.e(TAG, "签到失败,没有[领取]按钮");
      return false;
    }
    obtain.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击跳转到[签到]");

    // 返回首页
    mDevice.pressBack();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "签到成功,返回首页");
    return true;
  }

  /**
   * 关注
   */
  private boolean follow() {
    // 切换到关注Tab
    UiObject2 follow = findById("tabFollow");
    if (follow == null) {
      Log.e(TAG, "没有[关注]Tab");
      return false;
    }
    if (!follow.isSelected()) {
      follow.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到[关注]Tab");
    }

    // 跳转[爱奇艺号推荐]页面 RecyclerView 只能这样查找咯
    UiObject2 addFollow = findByText("关注推荐");
    if (addFollow == null) {
      Log.e(TAG, "没有[关注推荐]按钮");
      return false;
    }
    addFollow.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[关注推荐],跳转[爱奇艺号推荐]页面");

    // 点击关注
    UiObject2 add = findByText("关注");
    if (add == null) {
      mDevice.pressBack();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "没有[关注]按钮,返回[关注]Tab");
      return false;
    }
    add.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[关注]按钮");

    // 如果弹出了对话框 com.iqiyi.news:id/fsg_confirm_btn
    UiObject2 confirm = findById("fsg_confirm_btn");
    if (confirm != null) {
      confirm.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "关闭[关注]对话框");
    }

    // 返回主页面
    mDevice.pressBack();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "返回[关注]Tab");
    return true;
  }

  /**
   * 开始播放
   */
  private boolean doPlay() {
    // 切换到主页面
    UiObject2 home = findById("tabHome");
    if (home == null) {
      Log.e(TAG, "播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!home.isSelected()) {
      home.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到[推荐]Tab");
    }

    // 向上滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "向上滑动列表");

    // 点击播放
    UiObject2 playBtn = findById("comment_btn");
    if (playBtn == null) {
      Log.e(TAG, "播放失败:没有播放按钮");
      return false;
    }
    // 开始播放视频
    playBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    if (findById("input_click", 3) != null) {
      Log.e(TAG, "开始播放视频");
      // 等待视频播放完成
      sleep(35);
      playCount++;

      // 发表评论
      if (commentCount < 5 && comment()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 5 && share()) {
        shareCount++;
      }

      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "播放完成,返回首页");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "返回首页:不是视频页面");
    }

    return true;
  }

  /**
   * 发表评论
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
    sleep(2); // 等待文本填写完成
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
   * 分享
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
    return "com.iqiyi.news";
  }
}
