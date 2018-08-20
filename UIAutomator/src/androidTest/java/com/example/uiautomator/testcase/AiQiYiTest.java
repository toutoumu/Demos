package com.example.uiautomator.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private int checkCount = 0;

  public AiQiYiTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    startAPPWithPackageName();

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount == 10) return 0;
    }

    // 播放视频(评论,分享)
    while (readCount <= repCount) {
      try {
        if (!avliable()) break;

        logD("********************* 第 " + readCount + " 次 *********************");

        // 执行之前的检查操作
        if (checkInMainPage("tabHome") == null) {
          return 0;
        }

        // 签到 第一次进来签到, 随后每隔几次查看一下任务 ,顺便开宝箱
        if ((signCount == 0) || readCount % 5 == 0) {
          if (sign()) {
            signCount++;
          }
        }

        // 关注
        if (followCount < 3 && follow()) {
          followCount++;
        }

        // 播放
        if (doPlay()) {
          readCount++;
        }
      } catch (Exception e) {
        if (e instanceof IllegalStateException) {
          logE("阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        logE("阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭应用
    closeAPPWithPackageName();
    return readCount;
  }

  /**
   * 检查是否有指定Tab
   *
   * @param tabID tabID
   * @return {@link UiObject2}
   */
  private UiObject2 checkInMainPage(String tabID) {
    // 判断是否已经回到首页
    int restartCount = 0;
    while (restartCount < 10) {
      UiObject2 tab = findById(tabID);
      if (tab == null) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[" + tabID + "]" + restartCount);
        closeDialog();
        tab = findById(tabID);
        if (tab == null) {// 关闭对话框之后再次查找是否已经回到首页
          restartCount++;
          logE("应用可能已经关闭,重新启动");
          startAPPWithPackageName();
          continue;
        }
      }
      return tab;
    }
    logE("重启次数" + restartCount + "退出应用");
    return null;
  }

  /**
   * 使用正则表达式提取小括号中的内容
   *
   * @param msg 史蒂夫(33)
   * @return 33
   */
  public static List<String> extractMessageByRegular(String msg) {
    List<String> list = new ArrayList<String>();
    Pattern p = Pattern.compile("(?<=\\()(.+?)(?=\\))");
    Matcher m = p.matcher(msg);
    while (m.find()) {
      list.add(m.group().substring(0, m.group().length()));
    }
    return list;
  }

  /**
   * 例如 传入 观看视频 那么找到他的上级容器, 然后查找和 观看视频 同一级的控件,查看控件的值是否包含 类似 1/2
   *
   * @param key 观看视频,分享视频,晒收入
   * @return 次数
   */
  private int getCount(String key) {
    int count = 0;
    UiObject2 keyText = mDevice.wait(Until.findObject(By.textContains(key)), 1000 * 5);
    if (keyText.getText() != null && keyText.getText().contains("/")) {
      List<String> list = extractMessageByRegular(keyText.getText());
      count = Integer.parseInt(list.get(0).split("/")[0]);
    }
    return count;
  }

  /**
   * 检查各项的执行情况并赋值
   */
  private boolean doCheck() {
    try {
      // 检查是否在首页,并切换到 [我}tab
      UiObject2 tab = checkInMainPage("tabMe");
      if (tab == null) return false;
      if (!tab.isSelected()) {
        tab.click();
        sleep(5);
        mDevice.waitForIdle(timeOut);
        logD("切换到[我]Tab,准备检查");
      }

      // 向下滑动页面使得,签到那里显示出来
      int startY = height / 3;
      int endY = height * 2 / 3;
      mDevice.swipe(centerX, startY, centerX, endY, 10);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("向下滑动页面使得,签到那里显示出来");

      // 签到 com.iqiyi.news:id/score_task_active
      UiObject2 obtain = findById("score_task_active");
      if (obtain == null) {
        logE("检测失败,没有[打开任务]按钮[领取]");
        return false;
      }
      obtain.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("点击跳转到[任务界面]");

      // 向上滑动页面使得,任务明细那里显示出来
      startY = height * 2 / 3;
      endY = height / 3;
      mDevice.swipe(centerX, startY, centerX, endY, 10);
      sleep(1);
      mDevice.waitForIdle(timeOut);

      mDevice.swipe(centerX, startY, centerX, endY, 10);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("向上滑动页面使得,任务明细那里显示出来");

      readCount = getCount("浏览内容30s及以上得");
      logD("已经[阅读]次数:" + readCount);
      commentCount = getCount("发布一条评论可得");
      logD("已经[评论]次数:" + commentCount);
      shareCount = getCount("分享一条内容可得");
      logD("已经[分享]次数:" + shareCount);
      followCount = getCount("成功关注一个爱奇艺号得");
      logD("已经[关注]次数:" + followCount);

      // 返回首页
      pressBack("检查成功,返回首页", false);

      return true;
    } catch (Exception e) {
      logE("检查出错了", e);
      return false;
    }
  }

  /**
   * 关闭对话框
   */
  private void closeDialog() {
    pressBack("点击返回,尝试关闭对话框", true);
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
      logE("签到失败,没有[我]Tab");
      return false;
    }
    if (!follow.isSelected()) {
      follow.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("切换到[我]Tab");
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
      logE("签到失败,没有[领取]按钮");
      return false;
    }
    obtain.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("点击跳转到[签到]");

    // 开启宝箱
    UiObject2 open = findByText("开启");
    if (open != null) {
      open.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("开启宝箱,成功");

      // 关闭对话框 com.iqiyi.news:id/tv_brower_continue
      UiObject2 continueBtn = findByText("去浏览");
      if (continueBtn != null) {
        continueBtn.click();
        sleep(1);
        mDevice.waitForIdle(timeOut);
        logD("回到首页继续浏览");
        return true;
      }
    }

    // 返回首页
    pressBack("签到成功,返回首页", false);

    // 检测是否返回首页
    if (findById("tabHome") == null) {
      pressBack("签到成功,关闭对话框,返回首页", false);
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
      logE("没有[关注]Tab");
      return false;
    }
    if (!follow.isSelected()) {
      follow.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("切换到[关注]Tab");
    }

    // 跳转[用户详情]页面 RecyclerView 只能这样查找咯
    UiObject2 userName = findById("follow_subscribed_list_user_name");
    if (userName == null) {
      logE("没有[用户详情]按钮");
      return false;
    }
    userName.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("点击[用户名称],跳转[用户详情]页面");

    // 展开更多
    UiObject2 dropDown = findById("media_info_related_icon");
    if (dropDown == null) {
      pressBack("没有[展开更多]按钮,返回[关注]Tab", true);
      return false;
    }
    dropDown.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击[展开更多]按钮");

    // 点击关注 那个+号
    UiObject2 add = findById("fguli_follow_btn");
    if (add == null) {
      pressBack("没有[关注]按钮,返回[关注]Tab", true);
      return false;
    }
    add.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击[关注]按钮");

    // 如果弹出了对话框 com.iqiyi.news:id/fsg_confirm_btn
    UiObject2 confirm = findById("fsg_confirm_btn", 5);
    if (confirm != null) {
      confirm.click();
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("关闭[关注]对话框");
    }

    // 返回主页面
    pressBack("返回[关注]Tab", false);
    return true;
  }

  /**
   * 开始播放
   */
  private boolean doPlay() {
    // 切换到主页面
    UiObject2 home = findById("tabHome");
    if (home == null) {
      logE("播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!home.isSelected()) {
      home.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      logD("切换到[推荐]Tab");
    }

    // 向上滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("向上滑动列表");

    // 点击播放
    UiObject2 playBtn = findById("comment_btn");
    if (playBtn == null) {
      logE("播放失败:没有播放按钮");
      return false;
    }
    // 开始播放视频
    playBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);

    // 有可能点击进来,评论区域已经出来了
    if (findById("send_btn", 5) != null) {
      pressBack("播放视频时候,评论区域已经出来了,关闭输入评论那里的对话框", false);
    }

    if (findById("input_click", 3) != null) {
      logD("开始播放视频");
      // 等待视频播放完成
      sleep(35 + random.nextInt(10));

      // 发表评论
      if (commentCount < 3 && comment()) {
        commentCount++;
      }

      // 分享
      if (shareCount < 3 && share()) {
        shareCount++;
      }
      pressBack("播放完成,返回首页\n:", false);
    } else {
      pressBack("返回首页:不是视频页面\n:", true);
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
      logE("没有评论按钮");
      return false;
    }
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击评论按钮");

    // 输入评论 com.iqiyi.news:id/input_edit_text
    UiObject2 contentText = findById("input_edit_text");
    if (contentText == null) {
      logE("没有评论文本框");
      return false;
    }
    /*"爱奇艺的视频还是不错的,内容很好."*/
    contentText.setText(getComment(new Random().nextInt(10) + 5));
    sleep(2); // 等待文本填写完成
    mDevice.waitForIdle(timeOut);
    logD("填写评论内容");

    // 点击发表评论
    UiObject2 sendBtn = findById("send_btn");
    if (sendBtn == null) {
      logE("没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击发送,发表评论");

    return true;
  }

  /**
   * 分享
   */
  private boolean share() {
    // 分享按钮ID  com.iqiyi.news:id/news_article_footer_shareContainer
    UiObject2 shareBtn = findById("news_article_footer_shareContainer");
    if (shareBtn == null) {
      logE("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击分享按钮,调用分享对话框");

    // 分享到QQ ID com.iqiyi.news:id/rl_share_qq
    return qqShare(findById("rl_share_qq"));
  }

  @Override
  public String getAPPName() {
    return "爱奇艺纳逗";
  }

  @Override
  public String getPackageName() {
    return "com.iqiyi.news";
  }
}
