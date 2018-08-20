package com.example.uiautomator.testcase;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
  private int openBaidu = 0;//打开百度
  private int shareMomey = 0;//晒收入
  private int checkCount = 0;

  public HaoKanShiPinTest() {
    super();
  }

  @Override
  public int start(int repCount) {
    if (repCount == 0 || !avliable()) return 0;

    startAPPWithPackageName();

    // 执行之前的检查操作
    while (!doCheck()) {
      if (checkCount++ == 10) return 0;
    }

    // 播放视频(评论,分享)
    while (readCount <= repCount) {
      try {
        if (!avliable()) break;

        logD("********************* 第 " + readCount + " 次 *********************");

        // 判断是否已经回到首页
        List<UiObject2> tabs = checkInMainPage("text");
        if (tabs == null || tabs.size() == 0) {
          return readCount;
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
        // 阅读20篇开一次宝箱
        if (readCount % 5 == 0) {
          openBox();
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
    // closeAPP();
    closeAPPWithPackageName();
    return readCount;
  }

  /**
   * 检查是否有指定Tab
   *
   * @param tabID tabID text
   * @return {@link UiObject2}
   */
  private List<UiObject2> checkInMainPage(String tabID) {
    // 判断是否已经回到首页
    int restartCount = 0;
    while (restartCount < 10) {
      List<UiObject2> tab = findListById(tabID);
      if (tab == null || tab.size() == 0) {// 如果找不到底部导航栏有可能是有对话框在上面
        logE("检查失败,没有[" + tabID + "]" + restartCount);
        closeDialog();
        tab = findListById(tabID);
        if (tab == null || tab.size() == 0) {// 关闭对话框之后再次查找是否已经回到首页
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
   * 例如 传入 观看视频 那么找到他的上级容器, 然后查找和 观看视频 同一级的控件,查看控件的值是否包含 类似 1/2
   *
   * @param key 观看视频,分享视频,晒收入
   * @return 次数
   */
  private int getCount(String key) {
    int count = 0;
    UiObject2 keyText = findByText(key);
    for (UiObject2 item : keyText.getParent().getChildren()) {
      if (item.getText() != null && item.getText().contains("/")) {
        count = (Integer.parseInt(item.getText().split("/")[0]));
        break;
      }
    }
    return count;
  }

  /**
   * 检查各项的执行情况并赋值
   */
  private boolean doCheck() {
    try {
      // 检测是否已经回到主界面
      List<UiObject2> tabs = checkInMainPage("text");
      if (tabs == null || tabs.size() == 0) {
        return false;
      }

      // 点击底部导航栏中间位置跳转到任务页面
      Rect rect = tabs.get(0).getVisibleBounds();
      mDevice.click(centerX, (rect.top + rect.bottom) / 2);
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("跳转到任务页面,准备检测各项任务完成情况");

      // 获取签到次数 0 表示未签到 2 表示已经签到
      UiObject2 sign = mDevice.wait(Until.findObject(By.textContains("明日可领")), 1000 * 5);
      if (sign != null) {
        signCount = 2;
      } else {
        sign = mDevice.wait(Until.findObject(By.textContains("签到领")), 1000 * 5);
        if (sign == null) {
          // 签到次数获取失败
          logE("签到次数获取失败");
          return false;
        }
        signCount = 0;
      }
      logD("已经[签到]次数:" + signCount);

      // 向上滑动列表,使得所有任务展示出来
      int startY = height * 2 / 3;
      int endY = height / 10;
      mDevice.swipe(centerX, startY, centerX, endY, 20);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      logD("向上滑动列表,使得所有任务展示出来");

      readCount = getCount("观看视频");// 阅读次数
      logD("已经[阅读]次数:" + readCount);
      shareCount = getCount("分享视频");// 分享次数
      logD("已经[分享]次数:" + shareCount);
      shareMomey = getCount("晒收入") == 1 ? 2 : 0;//晒收入 2:已完成,0;未完成
      logD("已经[晒收入]次数:" + shareMomey);
      openBaidu = getCount("去百度") == 1 ? 2 : 0;//打开百度 2:已完成,0;未完成
      logD("已经[去百度]次数:" + openBaidu);

      pressBack("检查执行次数完成", false);
      return true;
    } catch (Exception e) {
      logE("检测出错了", e);
      return false;
    }
  }

  /**
   * 关闭对话框
   */
  private void closeDialog() {
    pressBack("关闭对话框", true);
  }

  /**
   * 关注 确保当前是播放页面才可以
   */
  private boolean follow() {
    // 跳转用户详情
    UiObject2 icon = findById("user_icon_url");
    if (icon == null) {
      mDevice.waitForIdle(timeOut);
      logE("没有可关注的用户");
      return false;
    }
    icon.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("点击评论用户的头像,跳转用户详情页面");

    UiObject2 follow = findById("user_center_follow_subscribe_view");
    if (follow == null || "已关注".equals(follow.getText())) {
      mDevice.waitForIdle(timeOut);
      logE("这个用户已经关注,不可以再关注");
      return false;
    }
    follow.click();
    sleep(2);
    mDevice.waitForIdle(timeOut);
    logD("点击关注关注用户");

    // 返回
    pressBack("返回播放页面", false);
    return true;
  }

  /**
   * 开始播放
   */
  private boolean doPlay() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      logE("播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!"刷新".equals(tabs.get(0).getText())) {
      tabs.get(0).click();
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
    UiObject2 playBtn = findById("start_button");
    if (playBtn == null) {
      logE("播放失败:没有播放按钮");
      return false;
    }
    // 获取到的是ListView的每一项
    UiObject2 listItem = playBtn.getParent().getParent();
    if (listItem.hasObject(By.textContains("广告"))) {
      logE("可能是广告");
      return false;
    }
    // 开始播放视频
    playBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("开始播放视频");
    // 等待视频播放完成
    sleep(15 + random.nextInt(10));
    // 分享
    if (shareCount < 3 && share(listItem)) {
      shareCount++;
    }
    sleep(70);

    // 向右滑动,关闭可能的广告页面
    int startX = width / 10;
    int endX = width * 2 / 3;
    mDevice.swipe(startX, centerY, endX, centerY, 10);
    logD("向右滑动,关闭可能的广告页面");

    // 关闭可能的对话框???
    tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      pressBack("关闭对话框?", false);
    }

    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("播放完成\n:");

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
   *
   * @param listItem
   */
  private boolean share(UiObject2 listItem) {
    // 分享按钮ID share_img
    UiObject2 shareBtn = listItem.wait(Until.findObject(By.res("com.baidu.haokan:id/more_img")), 1000 * 10);
    if (shareBtn == null) {
      logE("没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击分享按钮,调用分享对话框");

    if (readCount % 2 == 0) {
      return qqZoneShare(findById("qzone_container"));
    }
    return qqShare(findById("qq_container"));
  }

  /**
   * 签到
   */
  private boolean sign() {
    // 切换到主页面
    List<UiObject2> tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      logE("签到失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(4);
    mDevice.waitForIdle(timeOut);
    logD("跳转到签到页面");

    // 检测是否已经签到
    UiObject2 obtain = mDevice.wait(Until.findObject(By.textContains("明日可领")), 1000 * 10);
    if (obtain != null) {
      pressBack("已经签到了", false);
      return true;
    }

    // 签到 签到领xxx金币
    obtain = mDevice.wait(Until.findObject(By.textContains("签到领")), 1000 * 10);
    if (obtain == null) {
      pressBack("签到失败,没有[签到]按钮", false);
      return false;
    }
    obtain.click();
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("点击[签到]");

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
    pressBack("签到成功,返回首页", false);

    // 检测是否返回首页
    tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      pressBack("签到成功,关闭对话框,返回首页", false);
    }

    return true;
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
      logE("签到失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(4);
    mDevice.waitForIdle(timeOut);
    logD("跳转到任务页面");

    // 开宝箱
    UiObject2 openBox = findByText("开宝箱领金币");
    if (openBox == null) {
      pressBack("没到开启宝箱领金币的时候,关闭任务页面,返回首页", true);
      return false;
    }
    openBox.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("开宝箱领金币");

    UiObject2 ok = findByText("知道了");
    if (ok == null) {
      pressBack("开宝箱没有弹出对话框", true);
      return false;
    }
    ok.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    pressBack("关闭任务页面,返回首页", false);
    return true;
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
      logE("打开百度失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("跳转到签到页面,准备晒收入");

    // 向上滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 10);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("向上滑动列表");

    // 分享按钮ID share_img
    UiObject2 shareBtn = findByText("去晒");
    if (shareBtn == null) {
      pressBack("没有分享按钮", true);
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("点击分享按钮,调用分享对话框");

    // 分享到QQ com.baidu.haokan:id/qq_container
    qqShare(findById("qq_container"));

    pressBack("去晒完成,返回首页", false);
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
      logE("打开百度失败,不在主界面");
      return false;
    }
    Rect rect = tabs.get(0).getVisibleBounds();
    mDevice.click(centerX, (rect.top + rect.bottom) / 2);
    sleep(5);
    mDevice.waitForIdle(timeOut);
    logD("跳转到签到页面,准备打开百度");

    // 检测是否跳转页面
    UiObject2 title = findById("titlebar_title");
    if (title == null || !title.getText().contains("做任务")) {
      pressBack("没有跳转[做任务,领现金]页面,返回[主页]", true);
      return false;
    }

    // 向上滑动列表
    // startY > endY 向上滚动  startY < endY 向下滚动
    int startY = height * 2 / 3;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 10);
    sleep(1);
    mDevice.waitForIdle(timeOut);
    logD("向上滑动列表");

    // 打开百度
    UiObject2 byText = findByText("去百度");
    if (byText == null) {
      pressBack("没有[去打开]按钮,返回[主页]", true);
      return false;
    }
    UiObject2 open = byText.wait(Until.findObject(By.textContains("去打开")), 1000 * 10);
    if (open == null) {
      pressBack("没有[去打开]按钮,返回[主页]", true);
      return false;
    }
    open.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    logD("点击[去打开]按钮");

    // 检测是否跳转页面
    title = findById("titlebar_title");
    if (title == null || !title.getText().contains("手机百度")) {
      pressBack("没有跳转[手机百度]页面,返回[主页]", true);
      return false;
    }

    // 打开百度app
    mDevice.click(centerX, centerY);
    sleep(10);
    mDevice.waitForIdle(timeOut);
    logD("打开百度APP");

    // 检测是否已经到了文章页面
    String titleId = "com.baidu.searchbox:id/feed_template_base_title_id";
    UiObject2 baiduTitle = mDevice.wait(Until.findObject(By.res(titleId)), 1000 * 10);
    if (baiduTitle == null) {
      // 更新提示对话框
      UiObject2 closeUpdate = mDevice.wait(Until.findObject(By.res("com.baidu.searchbox:id/update_close")), 1000 * 10);
      if (closeUpdate != null) {
        closeUpdate.click();
        sleep(1);
        mDevice.waitForIdle(timeOut);
        logD("关闭更新对话框");
      } else {
        pressBack("关闭可能的对话框", false);
      }
    }

    // 阅读文章
    int red = 0;
    while (red++ < 4) {
      // 向上滚动列表
      mDevice.swipe(centerX, startY, centerX, endY, 30);
      sleep(1);
      mDevice.waitForIdle(timeOut);

      // 查找文章标题
      baiduTitle = mDevice.wait(Until.findObject(By.res(titleId)), 1000 * 10);
      if (baiduTitle == null) {
        closeAPPWithPackageName("com.baidu.searchbox");
        logE("没找到文章标题,关闭百度APP");

        // 关闭[打开百度做任务]页面
        title = findById("titlebar_title");
        if (title != null && title.getText().contains("手机百度")) {
          pressBack("关闭[打开百度做任务]页面", false);
        }

        // 关闭任务页面
        title = findById("titlebar_title");
        if (title != null && title.getText().contains("做任务")) {
          pressBack("关闭任务页面", false);
        }

        return false;
      }
      baiduTitle.click();
      sleep(5);
      mDevice.waitForIdle(timeOut);
      logD("打开文章开始阅读");

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
      pressBack("关闭文章阅读", false);
    }

    closeAPPWithPackageName("com.baidu.searchbox");
    logD("关闭百度APP");

    // 关闭[打开百度做任务]页面
    title = findById("titlebar_title");
    if (title != null && title.getText().contains("手机百度")) {
      pressBack("关闭[打开百度做任务]页面", false);
    }

    // 关闭任务页面
    title = findById("titlebar_title");
    if (title != null && title.getText().contains("做任务")) {
      pressBack("关闭任务页面", false);
    }

    // 返回首页
    tabs = findListById("text");
    if (tabs == null || tabs.size() == 0) {
      pressBack("点击返回按钮,关闭可能打开的对话框", false);
    }
    return true;
  }

  @Override
  public String getAPPName() {
    return "好看视频";
  }

  @Override
  public String getPackageName() {
    return "com.baidu.haokan";
  }
}
