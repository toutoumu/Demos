package com.example.uiautomator.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.Random;

/**
 * 聚看点测试 提成较高 签到奖励高1000金币=1元 分享点击80金币一次 徒弟分享贡献16金币
 * 文章100篇
 * 视频50篇
 * 签到
 * 宝箱(宝箱分享)
 * 晒收入
 */
public class JuKanDianTest extends BaseTest {
  private int readCount = 0; // 阅读次数
  private int signCount = 0; // 阅读次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;//重启次数
  private int shareMomey = 0;//晒收入,晒提现

  public JuKanDianTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 打开app
    // startAPP();
    startAPPWithPackageName();

    // 执行阅读,播放操作
    while (readCount < repCount) {
      try {
        if (!avliable()) break;

        logD(":\n********************************************\n第 "
          + readCount
          + " 次\n********************************************\n");

        // 判断是否有看点Tab来确定是否已经回到首页
        UiObject2 tab = findById("tv_tab1");
        if (tab == null) {// 如果找不到底部导航栏有可能是有对话框在上面
          closeDialog();
          tab = findById("tv_tab1");
          if (tab == null) {// 关闭对话框之后再次查找是否已经回到首页
            if (restartCount++ < 10) {
              logE("应用可能已经关闭,重新启动");
              // startAPP();
              startAPPWithPackageName();
              continue;
            } else {
              logE("退出应用");
              break;
            }
          }
        }

        // 领取金币
        if (readCount % 8 == 0) {
          obtainJinBi();
        }

        // 签到 ,最多执行两次
        if (signCount++ <= 1) {
          if (sign()) {
            signCount++;
          }
        }

        // 阅读播放
        if (random.nextInt(4) % 4 == 0) {// 1/4
          doPlay(); // 播放
        } else {
          doRead();// 阅读
        }
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭App
    // closeAPP();
    closeAPPWithPackageName();
    return readCount;
  }

  /**
   * 阅读文章
   *
   * @return 成功
   */
  private boolean doRead() {
    UiObject2 toolBar = findById("tv_tab1");
    // 切换到文章列表
    if (toolBar == null) {
      logE("阅读失败:没有底部栏");
      return false;
    }
    // 如果当前不是文章列表 ,切换到文章列表
    if (!toolBar.getText().equals("刷新")) {
      toolBar.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到文章列表");
    }

    // 向上滚动列表
    int startY = height / 2;
    int endY = height / 4;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("列表向上滑动");

    // 打开文章 评论数id item_artical_three_read_num
    UiObject2 read = findById("item_artical_three_read_num");
    if (read == null) {
      logE("阅读失败,没有评论按钮");
      return false;
    }
    read.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);

    //如果有评论按钮 com.xiangzi.jukandian:id/image_web_comment
    if (findById("image_web_comment", 3) != null) {// 文章页面
      logD("打开文章,开始阅读");
      int count = 0;// 滚动次数
      while (count++ < 13) {
        long start = System.currentTimeMillis();
        if (count % 5 == 0 && count != 0) {
          mDevice.swipe(centerX, endY, centerX, startY, 50);
        } else {
          mDevice.swipe(centerX, startY, centerX, endY, 50);
        }
        mDevice.waitForIdle(timeOut);
        long spend = System.currentTimeMillis() - start; // 滚动花费时间
        if (spend < 3000) {// 如果时间间隔小于 3 秒
          sleep(((double) (3000 - spend) / 1000.0));
        }
        Log.w(TAG, "滚动花费时间:" + spend);
      }
      readCount++;

      // 发表评论
      if (commentCount < 2 && commentArticle()) {
        commentCount++;
      }
      // 分享
     /* if (shareCount < 10 && shareArticle()) {
        shareCount++;
      }*/

      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logD("阅读完成,返回首页");
    } else { // 页面可能未打开
      pressBack("返回首页:可能没有打开页面", false);
    }
    return true;
  }

  /**
   * 播放视频
   *
   * @return 成功
   */
  private boolean doPlay() {
    UiObject2 tab2 = findById("tv_tab2");
    // 切换到视频列表
    if (tab2 == null) {
      logE("播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!tab2.getText().equals("刷新")) {
      tab2.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到视频列表");
    }

    // 需要向上滚动列表
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("视频列表向上滑动");

    // 点击播放
    UiObject2 play = findById("item_video_play_num");
    if (play == null) {
      logE("播放失败:没有播放按钮");
      return false;
    }
    play.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击开始播放视频");

    // com.xiangzi.jukandian:id/video_detail_bottom_comment_write_text
    if (findById("video_detail_bottom_comment_write_text", 3) != null) {// 视频页面
      sleep(35);
      readCount++;
      // 发表评论
      if (commentCount < 2 && commentVideo()) {
        commentCount++;
      }
      /*// 分享
      if (shareCount < 10 && shareVideo()) {
        shareCount++;
      }*/

      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logD("播放完成,返回首页");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logE("返回首页:打开的不是视频页面");
    }
    return false;
  }

  /**
   * 关闭对话框
   */
  private boolean closeDialog() {
    // 退出对话框
    UiObject2 close = findById("image_user_task_pop_close");
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("关闭对话框");
      return true;
    }
    close = findByText("继续赚钱", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("关闭对话框");
      return true;
    }

    // 推送文章
    close = findByText("查看详情", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("关闭对话框,查看详情");
      return true;
    }

    // 可能没有回到首页,点击返回关闭对话框
    mDevice.pressBack();
    mDevice.waitForIdle(timeOut);
    logD("点击返回,关闭对话框");

    // 点击返回关闭对话框 可能会弹出退出对话框,因此检测一下
    close = findByText("继续赚钱", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("关闭对话框");
      return true;
    }

    // 关闭App
    close = findByText("关闭APP", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("弹出了关闭App,只能关闭了");
      return true;
    }

    return false;
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentArticle() {
    try {
      // 1.弹出输入
      UiObject2 commentBtn = findById("tv_web_comment_hint");
      if (commentBtn == null) {
        logE("没有评论文本框");
        return false;
      }
      commentBtn.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("点击评论文本框,弹出键盘");

      // 2.输入评论 com.xiangzi.jukandian:id/dialog_comment_content
      UiObject2 contentText = findById("dialog_comment_content");
      if (contentText == null) {
        logE("没有评论文本框");
        return false;
      }
      contentText.setText(getComment(random.nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
      sleep(2); // 等待评论填写完成
      mDevice.waitForIdle(timeOut);
      logD("填写评论内容");

      // 3.点击发表评论 com.xiangzi.jukandian:id/dialog_comment_send
      UiObject2 sendBtn = findById("dialog_comment_send");
      if (sendBtn == null) {
        logE("没有发表评论按钮");
        return false;
      }
      sendBtn.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("******发表评论成功!******\n");

      return true;
    } catch (Exception e) {
      if (e instanceof IllegalStateException) {// 断开连接
        logE("断开连接了??", e);
        throw e;
      }
      logE("评论失败", e);
      return false;
    }
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentVideo() {
    try {
      // 1.弹出输入
      UiObject2 commentBtn = findById("video_detail_bottom_comment_write_text");
      if (commentBtn == null) {
        logE("没有评论文本框");
        return false;
      }
      commentBtn.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("点击评论文本框,弹出键盘");

      // 2.输入评论 com.xiangzi.jukandian:id/dialog_comment_content
      UiObject2 contentText = findById("dialog_comment_content");
      if (contentText == null) {
        logE("没有评论文本框");
        return false;
      }
      contentText.setText(getComment(new Random().nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
      sleep(2); // 等待内容填写完成
      mDevice.waitForIdle(timeOut);
      logD("填写评论内容");

      // 3.点击发表评论 com.xiangzi.jukandian:id/dialog_comment_send
      UiObject2 sendBtn = findById("dialog_comment_send");
      if (sendBtn == null) {
        logE("没有发表评论按钮");
        return false;
      }
      sendBtn.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("******发表评论成功!******\n");
      return true;
    } catch (Exception e) {
      if (e instanceof IllegalStateException) {// 断开连接
        logE("断开连接了??", e);
        throw e;
      }
      logE("评论失败", e);
      return false;
    }
  }

  /**
   * 任务中心领取金币
   *
   * @return
   */
  private boolean obtainJinBi() {
    // 切换到文章列表(看点)才有金币
    UiObject2 toolBar = findById("tv_tab1");
    // 切换到文章列表
    if (toolBar == null) {
      logE("阅读失败:没有底部栏");
      return false;
    }
    // 如果当前不是文章列表 ,切换到文章列表
    if (!toolBar.getText().equals("刷新")) {
      toolBar.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到文章列表");
    }

    // 领取金币
    UiObject2 obtain = findById("rl_lingqu_par", 5);
    if (obtain == null) {
      logE("没到领取金币的时候");
      return false;
    }
    obtain.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击领取金币");

    // 分享朋友圈赚更多金币 com.xiangzi.jukandian:id/dialog_btn
    UiObject2 share = findById("dialog_btn", 5);
    if (share == null) {
      pressBack("第一次没有分享按钮", true);
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    // 第一次关闭微信分享
    UiObject2 closeWeiXin = mDevice.findObject(By.res("com.tencent.mm:id/ht"));
    if (closeWeiXin == null) {
      pressBack("第一次没有关闭微信分享按钮", true);
      return false;
    }
    closeWeiXin.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    // 分享到不同的xxx,
    share = findById("dialog_btn");
    if (share == null) {
      pressBack("第二次没有分享按钮", true);
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    closeWeiXin = mDevice.findObject(By.res("com.tencent.mm:id/ht"));
    if (closeWeiXin == null) {
      pressBack("第二次没有关闭微信分享按钮", true);
      return false;
    }
    closeWeiXin.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    // 打开任务页面
    share = findById("dialog_btn");
    if (share == null) {
      pressBack("没有打开任务页面", true);
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    pressBack("关闭任务页面", false);
    return true;
  }

  /**
   * 签到
   *
   * @return
   */
  private boolean sign() {
    UiObject2 tab3 = findById("tv_tab3");
    if (tab3 == null) {
      logE("没有任务中心");
      return false;
    }
    tab3.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("点击评论文本框,弹出键盘");

    // 文本  signH1
    UiObject2 sign = findByText("分享赚更多金币");
    if (sign != null) {
      logD("已经签到过了");
      return true;
    }
    sign = findByText("签到领金币");
    if (sign == null) {
      logE("没有签到按钮");
      return false;
    }
    sign.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击签到");

    logD("签到结束,返回首页");
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
      logE("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      logE("没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("打开QQ分享");

    // 3.点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      logE("分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    logD("分享到我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      logE("分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      logE("没有返回按钮");
      return false;
    }
    back.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("******分享成功返回******");

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
      logE("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      logE("没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("打开QQ分享");

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      logE("分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    logD("分享到我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      logE("分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      logE("没有返回按钮");
      return false;
    }
    back.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("******分享成功返回******");

    return true;
  }

  @Override
  public String getAPPName() {
    return "聚看点";
  }

  @Override
  public String getPackageName() {
    return "com.xiangzi.jukandian";
  }
}
