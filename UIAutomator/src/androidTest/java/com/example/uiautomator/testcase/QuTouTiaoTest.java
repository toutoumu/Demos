package com.example.uiautomator.testcase;

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

  private Random random = new Random();
  private int height = 0; // 屏幕高度
  private int width = 0; // 屏幕宽度

  private int centerX = 0; // 中间位置
  private int centerY = 0; // 中间位置

  private int dX = 0; // 偏移
  private int dY = 0; // 偏移

  private int readCount = 0;
  private int commentCount = 0;
  private int shareCount = 0;

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
   * 文章评论
   *
   * @return
   */
  private boolean commentArticle() {
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
   * 文章评论
   *
   * @return
   */
  private boolean commentVideo() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮// 容器的id为 com.jifen.qukan:id/lq
    // com.jifen.qukan:id/lw 评论
    // com.jifen.qukan:id/lu 进入评论列表
    // com.jifen.qukan:id/lv 进入评论列表
    // com.jifen.qukan:id/lt 收藏
    // com.jifen.qukan:id/ls 分享
    // com.jifen.qukan:id/lr 调整字体
    // 点击输入评论文本框 com.iqiyi.news:id/input_click
    UiObject2 commentBtn = findById("lw");
    if (commentBtn == null) {
      Log.e(TAG, "没有评论按钮");
      return false;
    }
    Log.e(TAG, "点击评论按钮");
    commentBtn.click();
    mDevice.waitForIdle(timeOut);

    // 输入评论 com.jifen.qukan:id/ly
    UiObject2 contentText = findById("ly");
    if (contentText == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    Log.e(TAG, "填写评论内容");
    mDevice.waitForIdle(timeOut);
    contentText.setText(getComment(new Random().nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
    mDevice.waitForIdle(timeOut);

    // 点击发表评论 com.jifen.qukan:id/lz
    UiObject2 sendBtn = findById("lz");
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
  private boolean shareVideo() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // com.jifen.qukan:id/lw 评论
    // com.jifen.qukan:id/ji 进入评论列表
    // com.jifen.qukan:id/jj 进入评论列表
    // com.jifen.qukan:id/jh 收藏
    // com.jifen.qukan:id/ls 分享
    // com.jifen.qukan:id/jf 调整字体
    UiObject2 shareBtn = findById("ls");
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

  /**
   * 分享
   */
  private boolean shareArticle() {
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

  /**
   * 阅读文章
   * // com.jifen.qukan:id/iq 底部tab容器
   *
   * @return
   */
  private boolean doRead() {
    // 切换到文章列表
    UiObject2 toolBar = findById("iq");
    if (toolBar == null) {
      Log.e(TAG, "阅读失败:没有底部栏");
      return false;
    }
    // 如果当前不是文章列表 ,切换到文章列表
    UiObject2 refresh = toolBar.getChildren().get(0).findObject(By.text("刷新"));
    if (refresh == null) {
      toolBar.getChildren().get(0).click();
      sleep(3);
    }

    // 向上滚动列表
    Log.e(TAG, "列表向上滑动");
    int startY = centerY;
    int endY = startY - dY;
    mDevice.swipe(centerX, startY, centerX, endY, 30);

    // com.jifen.qukan:id/wy 评论数id
    Log.e(TAG, "打开文章");
    UiObject2 read = findById("wy");
    if (read == null) {
      Log.e(TAG, "阅读失败,没有评论按钮");
      return false;
    }
    read.click();
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
      if (commentCount < 10 && commentArticle()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareArticle()) {
        shareCount++;
      }

      Log.e(TAG, "开始阅读");
      int count = 0;
      startY = centerY; // 滚动起点
      endY = startY - dY; // 滚动终点
      while (count++ < 10) {
        if (count % 5 == 0 && count != 0) {
          Log.e(TAG, "向下滑动");
          mDevice.swipe(centerX, endY, centerX, startY, 30);
        } else {
          Log.e(TAG, "向上滑动");
          mDevice.swipe(centerX, startY, centerX, endY, 30);
        }
        mDevice.waitForIdle(timeOut);
        sleep(3);
      }
      Log.e(TAG, "阅读完成,返回首页");
      mDevice.pressBack();
      readCount++;
    } else { // 页面可能未打开
      Log.e(TAG, "返回首页:可能没有打开页面");
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
    }
    return true;
  }

  /**
   * 播放视频
   *
   * @return
   */
  private boolean doPlay() {
    // 切换到视频列表
    UiObject2 toolBar = findById("iq");
    if (toolBar == null) {
      Log.e(TAG, "播放失败");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    UiObject2 refresh = toolBar.getChildren().get(1).findObject(By.text("刷新"));
    if (refresh == null) {
      toolBar.getChildren().get(1).click();
      sleep(3);
    }

    // 需要向上滚动列表
    int startY = centerY;
    int endY = (int) (startY - height / 2.5);
    Log.e(TAG, "列表向上滑动");
    mDevice.swipe(centerX, startY, centerX, endY, 30);

    // com.jifen.qukan:id/a0x 评论数控件ID
    Log.e(TAG, "打开视频");
    UiObject2 play = findById("a0x");
    if (play == null) {
      Log.e(TAG, "播放失败:没有播放按钮");
      return false;
    }
    play.click();
    mDevice.waitForIdle(timeOut);

    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // 容器的id为 com.jifen.qukan:id/lq

    // com.jifen.qukan:id/lw 评论
    // com.jifen.qukan:id/lu 进入评论列表
    // com.jifen.qukan:id/lv 进入评论列表
    // com.jifen.qukan:id/lt 收藏
    // com.jifen.qukan:id/ls 分享
    // com.jifen.qukan:id/lr 调整字体

    if (findById("lq", 3) != null) {// 文章页面
      // 发表评论
      if (commentCount < 10 && commentVideo()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareVideo()) {
        shareCount++;
      }

      Log.e(TAG, "开始播放");
      sleep(30);
      Log.e(TAG, "播放完成,返回首页");
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      readCount++;
    } else {
      Log.e(TAG, "返回首页:不是视频页面");
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
    }
    return true;
  }

  public QuTouTiaoTest() {
    height = mDevice.getDisplayHeight(); // 屏幕高度
    width = mDevice.getDisplayWidth(); // 屏幕宽度

    centerX = width / 2; // 中间位置
    centerY = height / 2; // 中间位置

    dX = width / 4; // 偏移
    dY = height / 4; // 偏移

    readCount = 0; // 阅读次数
    commentCount = 0; // 评论次数
    shareCount = 0; // 分享次数
  }

  public void start() {
    // 打开app
    startAPP();

    // 如果有对话框,关闭对话框
    closeDialog();

    while (readCount < 100) {
      try {
        if (random.nextInt(10) % 2 == 0) {
          doRead();// 阅读
        } else {
          doPlay(); //播放
        }

        // 判断是否有底部导航栏来区分是否已经回到首页
        UiObject2 toolbar = findById("iq");
        if (toolbar == null) {
          Log.e(TAG, "应用可能已经关闭,退出阅读");
          return;
        } else {
          // 回列表页时出现对话框
          closeDialog();
        }
      } catch (Exception e) {
        Log.e(TAG, "阅读失败", e);
      }
    }
    closeAPP();
  }

  @Override
  String getAPPName() {
    return "趣头条";
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
