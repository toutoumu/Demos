package com.example.uiautomator.testcase;

import android.app.Dialog;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.Random;

/**
 * 趣头条测试
 */
public class QuTouTiaoTest extends BaseTest {
  private final String packageName = "com.jifen.qukan";
  private UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());//获取设备用例
  private int timeOut = 1000; // 每次等待时间

  /**
   * 关闭对话框
   */
  private boolean closeDialog() {
    UiObject2 close = findByText("先去逛逛", 1);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "关闭对话框");
      return true;
    }

    close = findByText("忽略", 1);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "关闭对话框");
      return true;
    }
    return false;
  }

  /**
   * 评论
   *
   * @return
   */
  private boolean comment() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // com.jifen.qukan:id/jk 评论
    // com.jifen.qukan:id/ji 进入评论列表
    // com.jifen.qukan:id/jj 进入评论列表
    // com.jifen.qukan:id/jh 收藏
    // com.jifen.qukan:id/jg 分享
    // com.jifen.qukan:id/jf 调整字体
    // 点击输入评论文本框 com.iqiyi.news:id/input_click
    UiObject2 commentBtn = findById("jk");
    if (commentBtn == null) {
      Log.e(TAG, "没有评论按钮");
      return false;
    }
    Log.e(TAG, "点击评论按钮");
    commentBtn.click();
    mDevice.waitForIdle(timeOut);

    // 输入评论 com.jifen.qukan:id/jm
    UiObject2 contentText = findById("jm");
    if (contentText == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    Log.e(TAG, "填写评论内容");
    mDevice.waitForIdle(timeOut);
    contentText.setText(getComment(new Random().nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
    mDevice.waitForIdle(timeOut);

    // 点击发表评论 com.jifen.qukan:id/jn
    UiObject2 sendBtn = findById("jn");
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
   * 分享
   */
  private boolean share() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // com.jifen.qukan:id/jk 评论
    // com.jifen.qukan:id/ji 进入评论列表
    // com.jifen.qukan:id/jj 进入评论列表
    // com.jifen.qukan:id/jh 收藏
    // com.jifen.qukan:id/jg 分享
    // com.jifen.qukan:id/jf 调整字体
    UiObject2 shareBtn = findById("jg");
    if (shareBtn == null) {
      Log.e(TAG, "没有分享按钮");
      return false;
    }
    Log.e(TAG, "点击分享按钮");
    shareBtn.click();
    mDevice.waitForIdle(timeOut);

    // 分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
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

  public void start() {
    int h = mDevice.getDisplayHeight(); // 屏幕高度
    int w = mDevice.getDisplayWidth(); // 屏幕宽度

    int centerX = w / 2; // 中间位置
    int centerY = h / 2; // 中间位置

    int dX = w / 4; // 偏移
    int dY = h / 4; // 偏移

    int startY = centerY;
    int endY = startY - dY;
    int step = 30;

    int readCount = 0;
    int commentCount = 0;
    int shareCount = 0;

    // 打开app
    startAPP();

    // 如果有对话框,关闭对话框
    closeDialog();

    while (readCount < 10) {
      try {
        Log.e(TAG, "打开文章");
        mDevice.click(w / 3, h / 4);
        mDevice.waitForIdle(timeOut);

        // 检测页面是否是阅读页面,阅读页面下面的操作按钮
        // com.jifen.qukan:id/jk 评论
        // com.jifen.qukan:id/ji 进入评论列表
        // com.jifen.qukan:id/jj 进入评论列表
        // com.jifen.qukan:id/jh 收藏
        // com.jifen.qukan:id/jg 分享
        // com.jifen.qukan:id/jf 调整字体
        if (findById("jk", 3) != null) {// 文章页面
          // 发表评论
          if (commentCount < 10 && comment()) {
            commentCount++;
          }
          // 分享
          if (shareCount < 10 && share()) {
            shareCount++;
          }

          Log.e(TAG, "开始阅读");
          int count = 0;
          while (count++ < 10) {
            if (count % 5 == 0 && count != 0) {
              Log.e(TAG, "向下滑动");
              mDevice.swipe(centerX, endY, centerX, startY, step);
            } else {
              Log.e(TAG, "向上滑动");
              mDevice.swipe(centerX, startY, centerX, endY, step);
            }
            mDevice.waitForIdle(timeOut);
            Thread.sleep(1000 * 3);
          }
          Log.e(TAG, "阅读完成");
          readCount++;
        } else { // 广告页面
          Log.e(TAG, "内容可能不是新闻");
          UiObject2 close = findByText("关闭");
          if (close != null) {
            close.click();
            mDevice.waitForIdle(timeOut);
            Log.e(TAG, "关闭广告页面");
          }
        }

        // 如果不是首页则点击返回 com.jifen.qukan:id/sg 搜索框id
        int times = 0;
        while (findById("sg", 1) == null && times++ < 5) {
          // 回列表页时出现对话框
          if (closeDialog()) {
            break;
          }
          Log.e(TAG, "点击返回列表页:" + times);
          mDevice.pressBack();
          mDevice.waitForIdle(timeOut);
        }
        if (times >= 5) {
          Log.e(TAG, "应用可能已经关闭,退出阅读");
          return;
        }

        // 列表向上滑动
        Log.e(TAG, "列表向上滑动");
        mDevice.swipe(centerX, startY, centerX, endY, step);
        mDevice.waitForIdle(timeOut * 3);
      } catch (Exception e) {
        Log.e(TAG, "阅读失败", e);
      }
    }
    closeAPP();
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
