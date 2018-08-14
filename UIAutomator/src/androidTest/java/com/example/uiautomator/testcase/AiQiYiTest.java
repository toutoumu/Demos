package com.example.uiautomator.testcase;

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
public class AiQiYiTest extends BaseTest {

  // 次数统计
  private int signCount = 0; // 签到调用次数
  private int readCount = 0; // 阅读次数
  private int followCount = 0; // 关注调用次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;// 重启次数

  public AiQiYiTest() {
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
        UiObject2 tab = findById("tabHome");
        if (tab == null) {// 如果找不到底部导航栏有可能是有对话框在上面
          closeDialog();
          tab = findById("tabHome");
          if (tab == null) {// 关闭对话框之后再次查找是否已经回到首页
            if (restartCount++ < 9) {
              Log.e(TAG, "应用可能已经关闭,重新启动");
              startAPP();
            } else {
              Log.e(TAG, "退出应用");
              break;
            }
          }
        }

        // 签到 第一次进来签到, 随后每隔几次查看一下任务
        if ((signCount == 0) || readCount % 5 == 0) {
          if (sign()) {
            signCount++;
          }
        }

        // 关注
        if (followCount <= 3 && follow2_7_30()) {
          followCount++;
        }

        // 关注
        /*if (followCount <= 3 && follow()) {
          followCount++;
        }*/

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
    mDevice.pressBack();
    sleep(1);
    mDevice.waitForIdle(timeOut);
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
    // 宝箱开启按钮id it 共有n个
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

    // 向下滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height / 3;
    int endY = height * 2 / 3;
    mDevice.swipe(centerX, startY, centerX, endY, 10);
    sleep(1);
    mDevice.waitForIdle(timeOut);

    // 签到 com.iqiyi.news:id/score_task_active
    UiObject2 obtain = findById("score_task_active");
    if (obtain == null) {
      mDevice.pressBack();
      Log.e(TAG, "签到失败,没有[领取]按钮");
      return false;
    }
    obtain.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击跳转到[签到]");

    // 开启宝箱
    UiObject2 open = findByText("开启");
    if (open != null) {
      open.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      log("开启宝箱,成功");

      // 关闭对话框 com.iqiyi.news:id/tv_brower_continue
      UiObject2 continueBtn = findByText("去浏览");
      if (continueBtn != null) {
        continueBtn.click();
        sleep(1);
        mDevice.waitForIdle(timeOut);
        log("回到首页继续浏览");
        return true;
      }
    }

    // 返回首页
    mDevice.pressBack();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    log("签到成功,返回首页");

    // 检测是否返回首页
    UiObject2 home = findById("tabHome");
    if (home == null) {
      mDevice.pressBack();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "签到成功,关闭对话框,返回首页");
    }
    return true;
  }

  /**
   * 关注 2.7.30 签到
   */
  private boolean follow2_7_30() {
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
    List<UiObject2> addFollows = findListById("follow_subscribed_list_user_name");
    if (addFollows == null || addFollows.size() == 0) {
      Log.e(TAG, "没有[关注推荐]按钮列表");
      return false;
    }
    boolean isOldVersion = false;
    UiObject2 addFollow = null;
    for (UiObject2 uiObject2 : addFollows) {
      if ("关注推荐".equals(uiObject2.getText())) { // 老版本才有 关注推荐 关键字
        addFollow = uiObject2;
        isOldVersion = true;
        break;
      }
    }

    if (isOldVersion) { // 老版本
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
    } else { // 新版本
      addFollow = addFollows.get(0);
      addFollow.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "点击[用户名称],跳转[用户详情]页面");

      // 展开更多
      UiObject2 dropDown = findById("media_info_related_icon");
      if (dropDown == null) {
        mDevice.pressBack();
        sleep(1);
        mDevice.waitForIdle(timeOut);
        Log.e(TAG, "没有[展开更多]按钮,返回[关注]Tab");
        return false;
      }
      dropDown.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "点击[展开更多]按钮");

      // 点击关注 那个+号
      UiObject2 add = findById("fguli_follow_btn");
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
    }
    return true;
  }

  /**
   * 关注 确保当前是播放页面才可以
   * 1. 切换到关注Tab
   * 2. 点击某个用户头像
   * 3. 展开更多
   * 4. 点击关注
   * 5. 关闭对话框
   * 6. 返回
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

    // 跳转[用户详情]页面 RecyclerView 只能这样查找咯
    UiObject2 userName = findById("follow_subscribed_list_user_name");
    if (userName == null) {
      Log.e(TAG, "没有[用户详情]按钮");
      return false;
    }
    userName.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[用户名称],跳转[用户详情]页面");

    // 展开更多
    UiObject2 dropDown = findById("media_info_related_icon");
    if (dropDown == null) {
      mDevice.pressBack();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "没有[展开更多]按钮,返回[关注]Tab");
      return false;
    }
    dropDown.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[展开更多]按钮");

    // 点击关注 那个+号
    UiObject2 add = findById("fguli_follow_btn");
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

      // 发表评论
      if (commentCount <= 3 && comment()) {
        commentCount++;
      }
      // 分享
      if (shareCount <= 3 && share()) {
        shareCount++;
      }

      mDevice.pressBack();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "播放完成,返回首页");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "返回首页:不是视频页面");
      return false;
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
    Log.e(TAG, "打开QQ分享,离开当前应用");

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      Log.e(TAG, "分享到[我的电脑]失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击[我的电脑]选项");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      Log.e(TAG, "分享到[我的电脑]确认失败");
      return false;
    }
    confirm.click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享到[我的电脑]确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      Log.e(TAG, "没有返回按钮");
      return false;
    }
    back.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "QQ完成分享返回");
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
