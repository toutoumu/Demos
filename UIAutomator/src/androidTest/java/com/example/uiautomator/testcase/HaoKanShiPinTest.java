package com.example.uiautomator.testcase;

import android.graphics.Rect;
import android.os.Build;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.List;
import java.util.Random;

/**
 * 爱奇艺测试 1000金币=1元 提现20元起 每日最高领取400金币, 徒弟阅读一次贡献5金币 最多一百金币
 * 阅读20篇 100
 * 徒弟阅读  100
 */
public class HaoKanShiPinTest extends BaseTest {

  // 次数统计
  private int signCount = 0; // 签到调用次数
  private int readCount = 0; // 阅读次数
  private int followCount = 0; // 关注调用次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;// 重启次数
  private int openBaidu = 0;
  private int shareMomey = 0;

  public HaoKanShiPinTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) {
      return 0;
    }
    // 启动App
    startAPP();

    // 播放视频(评论,分享)
    while (readCount <= repCount) {
      try {
        if (!avliable()) {
          break;
        }
        Log.e(TAG, ":\n********************************************\n第 "
          + readCount
          + " 次\n********************************************\n");
        // 判断是否已经回到首页
        List<UiObject2> tab = findListById("text");
        if (tab == null || tab.size() == 0) {// 如果找不到底部导航栏有可能是有对话框在上面
          closeDialog();
          tab = findListById("text");
          if (tab == null || tab.size() == 0) {// 关闭对话框之后再次查找是否已经回到首页
            if (restartCount++ < 9) {
              Log.e(TAG, "应用可能已经关闭,重新启动");
              startAPP();
            } else {
              Log.e(TAG, "退出应用");
              break;
            }
          }
        }

        // android7.0才可以访问到网页内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

        }
        // 晒收入,最多只执行两次
        if (shareMomey++ <= 1 && shareMomey()) {
          shareMomey++;
        }
        // 打开百度做任务,最多只执行两次
        if (openBaidu++ <= 1 && openBaidu()) {
          openBaidu++;
        }
        // 签到,最多只执行两次
        if ((signCount++ <= 1 && sign())) {
          signCount++;
        }
        // 阅读10篇开一次宝箱
        if (readCount % 20 == 0) {
          openBox();
        }

        // 播放
        if (doPlay()) {
          readCount++;
        }
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          Log.e(TAG, "阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        Log.e(TAG, "阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭应用
    closeAPP();
    return readCount;
  }

  /**
   * 关闭对话框
   */
  private void closeDialog() {
    pressBack("关闭对话框");
  }

  /**
   * 开宝箱
   *
   * @return
   */
  private boolean openBox() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      Log.e(TAG, "签到失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(4);
    mDevice.waitForIdle(timeOut);
    log("跳转到任务页面");

    // 开宝箱
    UiObject2 openBox = findByText("开宝箱领金币");
    if (openBox == null) {
      pressBack("没到开启宝箱领金币的时候,关闭任务页面,返回首页");
      return false;
    }
    openBox.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("开宝箱领金币");

    UiObject2 ok = findByText("知道了");
    if (ok == null) {
      pressBack("开宝箱没有弹出对话框");
      return false;
    }
    ok.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    pressBack("关闭任务页面,返回首页");
    return true;
  }

  /**
   * 签到
   */
  private boolean sign() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      Log.e(TAG, "签到失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(4);
    mDevice.waitForIdle(timeOut);
    log("跳转到签到页面");

    // 签到 签到领xxx金币
    UiObject2 obtain = mDevice.wait(Until.findObject(By.textContains("签到领")), 1000 * 10);
    if (obtain == null) {
      pressBack("签到失败,没有[签到]按钮");
      return false;
    }
    obtain.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[签到]");

    // 晒收入
    UiObject2 shareMoney = mDevice.wait(Until.findObject(By.textContains("晒收入再得")), 1000 * 10);
    if (shareMoney != null) {
      shareMoney.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      // 分享到QQ
      qqShare(findById("qq_container"));
    }

    // 返回首页
    pressBack("签到成功,返回首页");

    // 检测是否返回首页
    tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      pressBack("签到成功,关闭对话框,返回首页");
    }

    return true;
  }

  /**
   * 关注 确保当前是播放页面才可以
   */
  private boolean follow() {
    // 跳转用户详情
    UiObject2 icon = findById("user_icon_url");
    if (icon == null) {
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "没有可关注的用户");
      return false;
    }
    icon.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    log("点击评论用户的头像,跳转用户详情页面");

    UiObject2 follow = findById("user_center_follow_subscribe_view");
    if (follow == null || "已关注".equals(follow.getText())) {
      mDevice.waitForIdle(timeOut);
      log("这个用户已经关注,不可以再关注");
      return false;
    }
    follow.click();
    sleep(2);
    mDevice.waitForIdle(timeOut);
    log("点击关注关注用户");

    // 返回
    pressBack("返回播放页面");
    return true;
  }

  /**
   * 开始播放
   */
  private boolean doPlay() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      Log.e(TAG, "播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!"刷新".equals(tabs.get(0).getText())) {
      tabs.get(0).click();
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
    UiObject2 playBtn = findById("start_button");
    if (playBtn == null) {
      Log.e(TAG, "播放失败:没有播放按钮");
      return false;
    }
    // 开始播放视频
    playBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "开始播放视频");
    // 等待视频播放完成
    sleep(150);

    // 分享
    if (shareCount <= 3 && share()) {
      shareCount++;
    }

    // 关闭可能的对话框???
    tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      pressBack("关闭对话框?");
    }

    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "播放完成");

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
    // 分享按钮ID share_img
    UiObject2 shareBtn = findById("more_img");
    if (shareBtn == null) {
      Log.e(TAG, "没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击分享按钮,调用分享对话框");

    return qqShare(findById("qq_container"));
  }

  /**
   * 晒收入
   *
   * @return
   */
  private boolean shareMomey() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      Log.e(TAG, "打开百度失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(5);
    mDevice.waitForIdle(timeOut);
    log("跳转到签到页面,准备打开百度");

    // 向上滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 20);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "向上滑动列表");

    // 检测是否已经做过了
    UiObject2 byText = findByText("晒收入+30金币1/1");
    if (byText != null) {
      log("已经晒过了");
      return true;
    }

    // 分享按钮ID share_img
    UiObject2 shareBtn = findByText("去晒");
    if (shareBtn == null) {
      Log.e(TAG, "没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击分享按钮,调用分享对话框");

    // 分享到QQ com.baidu.haokan:id/qq_container
    qqShare(findById("qq_container"));

    pressBack("去晒完成,返回首页");
    return true;
  }

  /**
   * 打开百度
   *
   * @return
   */
  private boolean openBaidu() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      Log.e(TAG, "打开百度失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(5);
    mDevice.waitForIdle(timeOut);
    log("跳转到签到页面,准备打开百度");

    // 向上滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 20);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "向上滑动列表");

    // 检测是否已经做过了
    UiObject2 byText = findByText("去百度+100金币1/1");
    if (byText != null) {
      log("已经晒过了");
      return true;
    }

    // 打开百度
    UiObject2 open = findByText("去打开");
    if (open == null) {
      pressBack("没有[去打开]按钮,返回[主页]");
      return false;
    }
    open.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[关注]按钮");

    // 检测是否跳转页面
    UiObject2 title = findById("titlebar_title");
    if (title == null) {
      pressBack("没有[跳转页面],返回[主页]");
      return false;
    }

    // 打开百度app
    mDevice.click(centerX, centerY);
    sleep(10);
    mDevice.waitForIdle(timeOut);
    log("打开百度APP");

    // 阅读文章
    int red = 0;
    while (red++ < 2) {
      // 向上滚动列表
      mDevice.swipe(centerX, startY, centerX, endY, 30);
      sleep(1);
      mDevice.waitForIdle(timeOut);

      // 查找文章标题
      String titleId = "com.baidu.searchbox:id/feed_template_base_title_id";
      UiObject2 baiduTitle = mDevice.wait(Until.findObject(By.res(titleId)), 1000 * 10);
      if (baiduTitle == null) {
        pressBack("点击返回回到列表顶部???");

        // 双击关闭百度App
        mDevice.pressBack();
        mDevice.pressBack();
        sleep(3);

        // 关闭前一个App打开百度App页面
        mDevice.pressBack();
        sleep(1);

        // 关闭任务页面
        pressBack("百度App没有文章?,退出百度App");
        return false;
      }
      baiduTitle.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      log("打开文章开始阅读");

      // 向上滚动三次
      int count = 0;
      startY = height / 2;
      endY = height / 5;
      while (count++ < 3) {
        mDevice.swipe(centerX, startY, centerX, endY, 30);
        sleep(3);
        mDevice.waitForIdle(timeOut);
        Log.w(TAG, "向上滑动列表");
      }

      // 关闭文章阅读
      pressBack("关闭文章阅读");
    }

    // 返回列表顶部
    mDevice.pressBack();
    sleep(1);

    // 关闭百度App
    mDevice.pressBack();
    mDevice.pressBack();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("退出百度App,返回好看视频");

    // 关闭前一个App打开百度App页面
    mDevice.pressBack();
    sleep(1);

    // 关闭任务页面
    pressBack("关闭任务页面,返回首页");

    // 返回首页
    tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      pressBack("点击返回按钮,关闭可能打开的对话框");
    }
    return true;
  }

  @Override
  String getAPPName() {
    return "好看视频";
  }

  @Override
  String getPackageName() {
    return "com.baidu.haokan";
  }
}
