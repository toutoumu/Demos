package com.example.demo.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.List;
import java.util.Random;

/**
 * 趣头条测试 提成最低,1000金币相当于0.7元 徒弟贡献不详
 */
public class QuTouTiaoTest extends BaseTest {

  private int readCount = 0; // 阅读次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;//重启次数
  private int checkCount;
  private int score;

  public QuTouTiaoTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 如果已经阅读(300分)完成,那么随机阅读几篇
    if (score >= 300) {
      repCount = readCount + random.nextInt(5);
      logD("阅读已完成,随机阅读几篇" + (repCount - readCount));
    }

    // 执行阅读,播放操作
    while (readCount < repCount) {
      try {
        if (!avliable()) break;

        logD("********************* 第 " + readCount + " 次 *********************");

        // 执行之前的检查操作
        UiObject2 toolBar = checkInMainPage();
        if (toolBar == null) {
          return readCount;
        }

        doPlay(toolBar); //播放
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    closeAPPWithPackageName();
    return readCount;
  }

  /**
   * 检查是否有指定Tab
   *
   * @return {@link UiObject2}
   */
  private UiObject2 checkInMainPage() {
    // 判断是否有底部导航栏来区分是否已经回到首页, com.jifen.qukan:id/ij 底部tab容器

    // 判断是否已经回到首页
    int restartCount = 0;
    while (restartCount < 10) {
      UiObject2 toolBar = findById("ij");
      if (toolBar == null) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[" + "ij" + "]" + restartCount);
        closeDialog();
        toolBar = findById("ij");
        if (toolBar == null) {// 关闭对话框之后再次查找是否已经回到首页
          restartCount++;
          logE("应用可能已经关闭,重新启动");
          startAPPWithPackageName();
          continue;
        }
      }
      return toolBar;
    }
    logE("重启次数" + restartCount + "退出应用");
    return null;
  }

  public boolean doCheck() {
    try {
      startAPPWithPackageName();

      UiObject2 toolBar = checkInMainPage();
      if (toolBar == null) {
        return false;
      }
      // 如果当前不是文章列表 ,切换到文章列表
      UiObject2 refresh = toolBar.getChildren().get(0).findObject(By.text("刷新"));
      if (refresh == null) {
        toolBar.getChildren().get(0).click();
        sleep(3);
        mDevice.waitForIdle(timeOut);
        logD("切换到文章列表");
      }

      // com.jifen.qukan:id/wy 评论数id
      UiObject2 read = findById("aa3");
      int count = 0;
      while (count++ < 3 && read == null) {
        // 向上滚动列表
        int startY = height / 2;
        int endY = height / 4;
        mDevice.swipe(centerX, startY, centerX, endY, 30);
        logD("没有评论按钮,列表向上滑动文章列表");

        read = findById("aa3");
      }
      if (read == null) {
        logD("没有评论按钮,检查失败");
        return false;
      }
      read.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("打开文章,或视频");

      // 文章评论点赞收藏容器的id为 com.jifen.qukan:id/je
      if (findById("j8", 3) != null || findById("lj") != null) {// 文章页面
        UiObject2 cicle = findById("acm");
        if (cicle == null) {
          pressBack("检测失败,没有那个圆圈圈", true);
          return false;
        }
        cicle.click();
        logD("切换到分数详情页面");

        // 读取金币值
        try {
          UiObject2 allScore = mDevice.wait(Until.findObject(By.textContains("今日阅读总收益")), 1000 * 5);
          if (allScore != null) {
            int bottom = allScore.getVisibleBounds().bottom;
            List<UiObject2> coin = mDevice.wait(Until.findObjects(By.textContains("金币")), 1000 * 5);
            if (coin != null) {
              for (UiObject2 uiObject2 : coin) {
                if (Math.abs(uiObject2.getVisibleBounds().bottom - bottom) < 100) {
                  score = Integer.parseInt(uiObject2.getText().trim().replace("金币", ""));
                  logE("读取金币数量成功:" + score);
                  pressBack("关闭分数详情页面", false);
                  pressBack("关闭打开的文章", false);
                  return true;
                }
              }
            }
          }
        } catch (Exception e) {
          logE("读取金币数量失败", e);
          return false;
        }
        // 已经来到分数详情页,退出页面
        pressBack("关闭分数详情页面", false);
        pressBack("关闭打开的文章", false);
        return false;
      }// 读取金币结束
      pressBack("读取金币失败,不是文章页面", true);
      return false;
    } catch (Exception e) {
      logE("读取金币数量失败", e);
      return false;
    }
  }

  /**
   * 阅读文章
   *
   * @return 成功
   */
  private boolean doRead(UiObject2 toolBar) {
    // 切换到文章列表
    if (toolBar == null) {
      logE("阅读失败:没有底部栏");
      return false;
    }
    // 如果当前不是文章列表 ,切换到文章列表
    UiObject2 refresh = toolBar.getChildren().get(0).findObject(By.text("刷新"));
    if (refresh == null) {
      toolBar.getChildren().get(0).click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到文章列表");
    }

    // 向上滚动列表
    int startY = height / 2;
    int endY = height / 4;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("列表向上滑动文章列表");

    // com.jifen.qukan:id/wy 评论数id
    UiObject2 read = findById("wy");
    if (read == null) {
      logE("阅读失败,没有评论按钮,无法打开文章");
      return false;
    }
    read.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("打开文章");

    // 文章评论点赞收藏容器的id为 com.jifen.qukan:id/je
    if (findById("je", 3) != null) {// 文章页面
      logD("开始阅读");
      int count = 0;
      while (count++ < 10) {
        long start = System.currentTimeMillis();
        if (count % 5 == 0 && count != 0) {
          mDevice.swipe(centerX, endY, centerX, startY, 20);
        } else {
          mDevice.swipe(centerX, startY, centerX, endY, 20);
        }
        mDevice.waitForIdle(timeOut);

        long spend = System.currentTimeMillis() - start; // 滚动花费时间
        if (spend < 3000) {// 如果时间间隔小于 3 秒
          sleep(((double) (3000 - spend) / 1000.0));
        }
        Log.v(TAG, "滚动花费时间:" + spend);
      }
      readCount++;

      // 发表评论
      /*if (commentCount < 10 && commentArticle()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareArticle()) {
        shareCount++;
      }*/

      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logD("阅读完成,返回首页\n:");
    } else { // 页面可能未打开
      logE("返回首页:可能没有打开文章页面\n:");
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
    }
    return true;
  }

  /**
   * 播放视频
   *
   * @return 成功
   */
  private boolean doPlay(UiObject2 toolBar) {
    // 切换到视频列表
    if (toolBar == null) {
      logE("播放失败");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    UiObject2 refresh = toolBar.getChildren().get(1).findObject(By.text("刷新"));
    if (refresh == null) {
      toolBar.getChildren().get(1).click();
      sleep(10);
      mDevice.waitForIdle(timeOut);
      logD("切换到视频列表");
    }

    // 需要向上滚动列表
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("列表向上滑动");

    // com.jifen.qukan:id/a0x 评论数控件ID
    // com.jifen.qukan:id/a36 播放按钮
    int repeat = 0;
    UiObject2 play = findById("a36");
    while (repeat++ < 4 && play == null) {
      logE("播放失败,没有找到文章");
      mDevice.swipe(centerX, startY, centerX, endY, 30);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("列表向上滑动,向上滚动查找视频");
      play = findById("a36");
    }
    if (play == null) {
      logE("播放失败,没有找到视频,结束本次查找");
      return false;
    }
    play.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("打开视频,开始播放");

    sleep(35 + random.nextInt(10));
    mDevice.waitForIdle(timeOut);
    readCount++;
    logD("播放完成\n");

    return true;
  }

  /**
   * 关闭对话框
   */
  private boolean closeDialog() {
    UiObject2 close = findByText("忽略", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("关闭对话框");
      return true;
    }
    close = findByText("先去逛逛", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      logD("关闭对话框");
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
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // com.jifen.qukan:id/jk 评论
    // com.jifen.qukan:id/ji 进入评论列表
    // com.jifen.qukan:id/jj 进入评论列表
    // com.jifen.qukan:id/jh 收藏
    // com.jifen.qukan:id/jg 分享
    // com.jifen.qukan:id/jf 调整字体

    // 1.弹出输入
    UiObject2 commentBtn = findById("jk");
    if (commentBtn == null) {
      logE("没有评论文本框");
      return false;
    }
    logD("点击评论文本框,弹出键盘");
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    // 2.输入评论 com.jifen.qukan:id/jm
    UiObject2 contentText = findById("jm");
    if (contentText == null) {
      logE("没有评论文本框");
      return false;
    }
    contentText.setText(getComment(random.nextInt(5) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待评论填写完成
    mDevice.waitForIdle(timeOut);
    logD("填写评论内容");

    // 3.点击发表评论 com.jifen.qukan:id/jn
    UiObject2 sendBtn = findById("jn");
    if (sendBtn == null) {
      logE("没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("******发表评论成功!******\n");

    return true;
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentVideo() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮// 容器的id为 com.jifen.qukan:id/lq
    // com.jifen.qukan:id/lw 评论
    // com.jifen.qukan:id/lu 进入评论列表
    // com.jifen.qukan:id/lv 进入评论列表
    // com.jifen.qukan:id/lt 收藏
    // com.jifen.qukan:id/ls 分享
    // com.jifen.qukan:id/lr 调整字体

    // 1.弹出输入
    UiObject2 commentBtn = findById("lw");
    if (commentBtn == null) {
      logE("没有评论文本框");
      return false;
    }
    logD("点击评论文本框,弹出键盘");
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    // 2.输入评论 com.jifen.qukan:id/ly
    UiObject2 contentText = findById("ly");
    if (contentText == null) {
      logE("没有评论文本框");
      return false;
    }
    contentText.setText(getComment(new Random().nextInt(5) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待内容填写完成
    mDevice.waitForIdle(timeOut);
    logD("填写评论内容");

    // 3.点击发表评论 com.jifen.qukan:id/lz
    UiObject2 sendBtn = findById("lz");
    if (sendBtn == null) {
      logE("没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("******发表评论成功!******\n");
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
    return "趣头条";
  }

  @Override
  public String getPackageName() {
    return "com.jifen.qukan";
  }
}
