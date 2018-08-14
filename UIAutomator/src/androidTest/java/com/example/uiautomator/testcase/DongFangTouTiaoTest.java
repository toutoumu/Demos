package com.example.uiautomator.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import android.widget.RadioButton;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 东方头条测试 提成较高 1000金币=1元 徒弟阅读提成是徒弟两倍
 * 播放,阅读,晒收入(3次),推送
 */
public class DongFangTouTiaoTest extends BaseTest {

  private int readCount = 0; // 阅读次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;//重启次数

  public DongFangTouTiaoTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 打开app
    startAPP();

    // 执行阅读,播放操作
    while (readCount < repCount) {
      try {
        if (!avliable()) break;

        log(":\n********************************************\n第 "
          + readCount
          + " 次\n********************************************\n");

        // 判断是否为首页 视频 任务 我的 都是RadioButton
        List<UiObject2> radioButtons = mDevice.findObjects(By.clazz(RadioButton.class));
        if (radioButtons == null || radioButtons.size() != 3) {// 如果找不到底部导航栏有可能是有对话框在上面
          closeDialog();
          radioButtons = mDevice.findObjects(By.clazz(RadioButton.class));
          if (radioButtons == null || radioButtons.size() != 3) {// 关闭对话框之后再次查找是否已经回到首页
            if (restartCount++ < 10) {
              log("应用可能已经关闭,重新启动");
              startAPP();
              continue;
            } else {
              log("退出应用");
              break;
            }
          }
        }
        doPlay(radioButtons); // 播放
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {// 如果断开了连接
          Log.e(TAG, "阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        Log.e(TAG, "阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭App
    closeAPP();
    return readCount;
  }

  /**
   * 播放视频
   *
   * @param radioButtons 底部导航栏
   * @return 成功
   */
  private boolean doPlay(List<UiObject2> radioButtons) {
    if (radioButtons == null || radioButtons.size() != 3) {
      log("视频播放失败:没有底部栏");
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
      log("播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!videoTab.isChecked()) {
      videoTab.click();
      sleep(10);
      mDevice.waitForIdle(timeOut);
      log("切换到视频列表");
    }

    // 需要向上滚动列表
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    log("视频列表向上滑动");

    // 点击播放 播放按钮 com.songheng.eastnews:id/aez
    UiObject2 play = findById("aez");
    if (play == null) {
      log("播放失败:没有播放按钮");
      return false;
    }

    play.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("点击开始播放视频");

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
      log("播放完成,返回首页");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      log("返回首页:打开的不是视频页面");
    }
    return true;
  }

  /**
   * 阅读文章
   *
   * @return 成功
   */
  private boolean doRead() {
    // 如果当前不是文章列表 ,切换到文章列表 判断是否有发布按钮
    if (findByText("发布") == null) {// com.songheng.eastnews:id/nd
      UiObject2 toolBar = findByText("新闻"); // com.songheng.eastnews:id/l3
      // 切换到文章列表
      if (toolBar == null) {
        log("阅读失败:没有底部栏");
        return false;
      }
      toolBar.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      log("切换到文章列表");
    }

    // 向上滚动列表
    int startY = height / 2;
    int endY = height / 4;
    mDevice.swipe(centerX, startY, centerX, endY, 20);
    log("列表向上滑动");

    // 打开文章 com.songheng.eastnews:id/pz  标签 (打开不是百度的)
    int repeat = 0;
    UiObject2 read = findById("pz");
    List<String> key = Arrays.asList("百度");
    while (repeat++ < 4 && (read == null || key.contains(read.getText()))) {
      log("阅读失败,没有找到文章");
      mDevice.swipe(centerX, startY, centerX, endY, 20);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      log("列表向上滑动,向上滚动查找文章");
      read = findById("pz");
    }
    if (read == null) {
      log("阅读失败,没有找到文章,结束本次查找");
      return false;
    }
    read.click();
    sleep(4);
    mDevice.waitForIdle(timeOut);
    log("打开文章,开始阅读");

    int count = 0;
    startY = 4 * height / 5;
    endY = height / 5;
    while (count++ < 10) {
      long start = System.currentTimeMillis();
      mDevice.swipe(centerX, startY, centerX, endY, 100);
      mDevice.waitForIdle(timeOut);

      if (count == 2) {// 滑动两次之后出现 点击阅读全文 com.songheng.eastnews:id/av_
        UiObject2 seeAll = findByText("点击阅读全文", 3);
        if (seeAll != null) {
          seeAll.click();
          mDevice.waitForIdle(timeOut);
          log("点击阅读全文");
        }
        continue;
      }

      long spend = System.currentTimeMillis() - start; // 滚动花费时间
      if (spend < 4000) {// 如果时间间隔小于 4 秒
        sleep(((double) (4000 - spend) / 1000.0));
      }
      Log.w(TAG, "滚动花费时间" + spend);
    }
    readCount++;

     /* // 发表评论
      if (commentCount < 2 && commentArticle()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareArticle()) {
        shareCount++;
      }*/

    mDevice.pressBack();
    mDevice.waitForIdle(timeOut);
    log("阅读完成,返回首页");
    return true;
  }

  /**
   * 关闭对话框
   */
  private void closeDialog() {
    pressBack("点击返回,尝试关闭对话框");
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentArticle() {
    // 1.弹出输入
    UiObject2 commentBtn = findById("tv_web_comment_hint");
    if (commentBtn == null) {
      log("没有评论文本框");
      return false;
    }
    log("点击评论文本框,弹出键盘");
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    // 2.输入评论 com.xiangzi.jukandian:id/dialog_comment_content
    UiObject2 contentText = findById("dialog_comment_content");
    if (contentText == null) {
      log("没有评论文本框");
      return false;
    }
    contentText.setText(getComment(random.nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待评论填写完成
    mDevice.waitForIdle(timeOut);
    log("填写评论内容");

    // 3.点击发表评论 com.xiangzi.jukandian:id/dialog_comment_send
    UiObject2 sendBtn = findById("dialog_comment_send");
    if (sendBtn == null) {
      log("没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("******发表评论成功!******\n");

    return true;
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentVideo() {
    // 1.弹出输入
    UiObject2 commentBtn = findById("video_detail_bottom_comment_write_text");
    if (commentBtn == null) {
      log("没有评论文本框");
      return false;
    }
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    log("点击评论文本框,弹出键盘");

    // 2.输入评论 com.xiangzi.jukandian:id/dialog_comment_content
    UiObject2 contentText = findById("dialog_comment_content");
    if (contentText == null) {
      log("没有评论文本框");
      return false;
    }
    contentText.setText(getComment(new Random().nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待内容填写完成
    mDevice.waitForIdle(timeOut);
    log("填写评论内容");

    // 3.点击发表评论 com.xiangzi.jukandian:id/dialog_comment_send
    UiObject2 sendBtn = findById("dialog_comment_send");
    if (sendBtn == null) {
      log("没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("******发表评论成功!******\n");
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

    // 1.弹出分享对话框
    UiObject2 shareBtn = findById("ls");
    if (shareBtn == null) {
      log("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    log("点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      log("没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("打开QQ分享");

    // 3.点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      log("分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    log("分享到我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      log("分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      log("没有返回按钮");
      return false;
    }
    back.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    log("******分享成功返回******");

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
      log("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    log("点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      log("没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("打开QQ分享");

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      log("分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    log("分享到我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      log("分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    log("分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      log("没有返回按钮");
      return false;
    }
    back.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    log("******分享成功返回******");

    return true;
  }

  @Override
  String getAPPName() {
    return "东方头条";
  }

  @Override
  String getPackageName() {
    return "com.songheng.eastnews";
  }
}
