package com.example.demo.testcase;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.support.v7.widget.RecyclerView;
import android.widget.TabWidget;
import java.util.Random;
import kotlin.Unit;

/**
 * 今日头条测试 徒弟阅读一次20金币
 * 阅读,分享,晒
 */
public class JinRiTouTiaoTest extends BaseTest {

  private int readCount = 0; // 阅读次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;//重启次数
  private int checkCount = 0;

  public JinRiTouTiaoTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 如果已经阅读完成,那么随机阅读几篇
    if (readCount >= 10) {
      repCount = readCount + random.nextInt(5);
      logD("阅读已完成,随机阅读几篇" + (repCount - readCount));
    }

    // 执行阅读,播放操作
    while (readCount < repCount) {
      try {
        if (!avliable()) break;

        // 判断是否已经回到首页
        UiObject2 toolBar = checkInMainPage();
        if (toolBar == null) {
          return readCount;
        }

        logD("********************* 第 " + readCount + " 次 *********************");

        doRead(toolBar);
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭App
    closeAPPWithPackageName();

    return readCount;
  }

  public boolean doCheck() {
    if (!avliable()) return false;
    try {
      startAPPWithPackageName();

      UiObject2 toolBar = checkInMainPage();
      // 切换到文章列表
      if (toolBar == null || toolBar.getChildren().size() == 0) {
        logE("检查失败:没有底部栏");
        return false;
      }
      // 如果当前不是文章列表 ,切换到任务列表
      UiObject2 mainTab = toolBar.getChildren().get(3);
      if (mainTab != null && !mainTab.isSelected()) {
        mainTab.click();
        sleep(5);
        mDevice.waitForIdle(timeOut);
        logD("切换到任务列表");
      }

      // 签到
      UiObject2 sign = findByText("已签到");
      if (sign != null) {
        logE("已经签到");
      } else {
        sign = findByText("签到");
        if (sign != null) {
          logE("未签到");
        }
        // signCount =2;
      }

      // 向上滚动列表 滚动距离 height / 3
      int startY = height / 2;
      int endY = height / 6;
      mDevice.swipe(centerX, startY, centerX, endY, 10);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("列表向上滑动");

      // 查看分享次数
      UiObject2 shareMoney = findByText("晒收入");
      if (shareMoney != null) {
        shareMoney = shareMoney.getParent().wait(Until.findObject(By.textContains("已完成")), 1000 * 5);
        if (shareMoney != null) {
          logE("晒收入已完成");
        } else {
          logE("晒收入未完成");
        }
      } else {
        logE("检查失败,[晒收入]未找到");
      }

      // 查看阅读次数
      UiObject2 read = findByText("认真阅读文章或视频");
      if (read != null) {
        read = read.getParent().wait(Until.findObject(By.textContains("已完成")), 1000 * 5);
        if (read != null) {
          readCount = 10;
          logE("阅读已完成次数" + readCount + "/10");
        } else {
          logE("阅读未完成");
        }
      } else {
        logE("检查失败,[认真阅读文章或视频]未找到");
        return false;
      }

      // 查看分享次数
      UiObject2 share = findByText("分享文章或视频");
      if (share != null) {
        share = share.getParent().wait(Until.findObject(By.textContains("已完成")), 1000 * 5);
        if (share != null) {
          shareCount = 3;
          logE("分享已完成次数" + shareCount + "/3");
        } else {
          logE("分享未完成");
        }
      } else {
        logE("检查失败,[分享文章或视频]未找到");
        return false;
      }
      return true;
    } catch (Exception e) {
      logE("检查出错了", e);
      return false;
    }
  }

  private UiObject2 checkInMainPage() {
    int restartCount = 0;
    while (restartCount < 10) {
      if (!avliable()) return null;
      UiObject2 toolBar = findByClass(TabWidget.class);
      if (toolBar == null) {// 如果找不到底部导航栏有可能是有对话框在上面
        closeDialog();
        toolBar = findByClass(TabWidget.class);
        if (toolBar == null) { // 关闭对话框之后再次查找是否已经回到首页
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

  private boolean follow(UiObject2 toolBar) {
    // 切换到文章列表
    if (toolBar == null || toolBar.getChildren().size() == 0) {
      logD("阅读失败:没有底部栏");
      return false;
    }
    // 如果当前不是文章列表 ,切换到文章列表
    UiObject2 mainTab = toolBar.getChildren().get(3);
    if (mainTab != null && !mainTab.isSelected()) {
      mainTab.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到文章列表");
    }

    findByText("签到").click();
    findByText("开宝箱得金币").click();
    return true;
  }

  /**
   * 阅读文章
   *
   * @return 成功
   */
  private boolean doRead(UiObject2 toolBar) {
    // 切换到文章列表
    if (toolBar == null || toolBar.getChildren().size() == 0) {
      logE("阅读失败:没有底部栏");
      return false;
    }
    // 如果当前不是文章列表 ,切换到文章列表
    UiObject2 mainTab = toolBar.getChildren().get(0);
    if (mainTab != null && !mainTab.isSelected()) {
      mainTab.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到文章列表");
    }

    // 向上滚动列表 滚动距离 height / 3
    int startY = height / 2;
    int endY = height / 6;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("列表向上滑动");

    // 打开文章
    mDevice.click(width / 3, height / 3);
    sleep(3);
    mDevice.waitForIdle(timeOut);

    // 内容页面都有关注按钮
    UiObject2 follow = mDevice.wait(Until.findObject(By.textEndsWith("关注")), 1000 * 3);
    if (follow == null) {
      pressBack("该页面不是文章页面或者视频页面", true);
      return false;
    }

    //  底部区域包含 RecyclerView 那么是文章页面
    if (findByClass(RecyclerView.class, 3) != null) {// 文章页面
      logD("打开文章,开始阅读");
      startY = height * 5 / 6;
      endY = height / 6;
      int scrollCount = 0;
      while (scrollCount++ < 16) {
        sleep(2);
        mDevice.waitForIdle(timeOut);
        mDevice.swipe(centerX, startY, centerX, endY, 30);
      }
      readCount++;

      // 发表评论
      /*if (commentCount < 2 && commentArticle()) {
        commentCount++;
      }*/
      // 分享
      if (shareCount <= 3 && shareArticle()) {
        shareCount++;
      }

      pressBack("阅读完成,返回首页\n:", false);
    } else { // 页面可能未打开
      pressBack("返回首页:打开的不是文章页面\n:", true);
      return false;
    }
    return true;
  }

  private boolean sign(UiObject2 toolBar) {
    // 切换到任务Tab
    if (toolBar == null || toolBar.getChildren().size() == 0) {
      logE("任务Tab:没有底部栏");
      return false;
    }
    // 如果当前不是任务Tab ,切换到任务Tab
    UiObject2 mainTab = toolBar.getChildren().get(3);
    if (mainTab != null && !mainTab.isSelected()) {
      mainTab.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到任务Tab,签到或检测任务是否完成");
    }
    UiObject2 sign = findByText("签到");

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
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到视频列表");
    }

    // 需要向上滚动列表
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    logD("列表向上滑动");

    // com.jifen.qukan:id/a0x 评论数控件ID
    UiObject2 play = findById("a0x");
    if (play == null) {
      logE("播放失败:没有播放按钮");
      return false;
    }
    play.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("打开视频");

    // 视频评论点赞收藏容器的id为 com.jifen.qukan:id/lq
    if (findById("lq", 3) != null) {// 视频页面
      logD("开始播放");
      sleep(35);
      readCount++;
      // 发表评论
      /*if (commentCount < 10 && commentVideo()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareVideo()) {
        shareCount++;
      }*/
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logD("播放完成,返回首页");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      logD("返回首页:不是视频页面");
    }
    return true;
  }

  /**
   * 关闭对话框
   */
  private boolean closeDialog() {
    pressBack("点击返回,尝试关闭对话框", true);
    return true;
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentArticle() {
    try {
      // 1.弹出输入
      UiObject2 commentBtn = findById("kn");
      if (commentBtn == null) {
        logE("没有评论文本框");
        return false;
      }
      logD("点击评论文本框,弹出键盘");
      commentBtn.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);

      // 2.输入评论 com.ss.android.article.lite:id/ke
      UiObject2 contentText = findById("ke");
      if (contentText == null) {
        logE("没有评论文本框");
        return false;
      }
      contentText.setText(getComment(random.nextInt(5) + 5)); // 这里使用中文会出现无法填写的情况
      sleep(2); // 等待评论填写完成
      mDevice.waitForIdle(timeOut);
      logD("填写评论内容");

      // 3.点击发表评论 com.ss.android.article.lite:id/kh
      UiObject2 sendBtn = findById("kh");
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
      logD("没有评论文本框");
      return false;
    }
    logD("点击评论文本框,弹出键盘");
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    // 2.输入评论 com.jifen.qukan:id/ly
    UiObject2 contentText = findById("ly");
    if (contentText == null) {
      logD("没有评论文本框");
      return false;
    }
    contentText.setText(getComment(new Random().nextInt(5) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待内容填写完成
    mDevice.waitForIdle(timeOut);
    logD("填写评论内容");

    // 3.点击发表评论 com.jifen.qukan:id/lz
    UiObject2 sendBtn = findById("lz");
    if (sendBtn == null) {
      logD("没有发表评论按钮");
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
      logD("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    return qqShare(findByText("QQ好友"));
  }

  /**
   * 分享
   */
  private boolean shareArticle() {
    UiObject2 recyclerView = findByClass(RecyclerView.class, 3);
    if (recyclerView == null) {
      logE("没有分享控件");
      return false;
    }
    Rect rect = recyclerView.getVisibleBounds();
    int y = (rect.top + rect.bottom) / 2;

    // 滑动使QQ分享显示出来
    mDevice.swipe(width * 4 / 5, y, width / 10, y, 20);
    mDevice.waitForIdle(timeOut);

    //请求分享按钮
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      logE("分享失败");
      return false;
    }
    return qqShare(share.getParent());
  }

  @Override
  public String getAPPName() {
    return "今日头条极速版";
  }

  @Override
  public String getPackageName() {
    return "com.ss.android.article.lite";
  }
}
