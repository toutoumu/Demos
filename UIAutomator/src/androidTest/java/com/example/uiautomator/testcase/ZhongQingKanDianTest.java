package com.example.uiautomator.testcase;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import java.util.Random;

public class ZhongQingKanDianTest extends BaseTest {

  private final String packageName = "cn.youth.news";
  UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());//获取设备用例

  private void read() {

  }

  public void start() {

    int h = mDevice.getDisplayHeight(); // 屏幕高度
    int w = mDevice.getDisplayWidth(); // 屏幕宽度

    int timeOut = 1000; // 每次等待时间
    int centerX = w / 2; // 中间位置
    int centerY = h / 2; // 中间位置

    int dX = w / 4; // 偏移
    int dY = h / 4; // 偏移

    int startY = centerY;
    int endY = startY - dY;
    int step = 30;

    Random random = new Random();
    mDevice.pressHome();
    mDevice.waitForIdle(1000);

    startAPP();

    while (true) {
      int randomInt = random.nextInt(100);
      // 点击跳转到详情页
      Log.e(TAG, "点击跳转到详情页,随机数: " + randomInt + " startY : " + (startY + randomInt) + " endY :" + (endY + randomInt));
      mDevice.click(w / 3, h / 4);
      mDevice.waitForIdle(timeOut);

      try {
        // 检测页面是否是阅读页面
        mDevice.wait(Until.findObject(By.text("写评论...")), 1000);
        UiObject2 object = mDevice.findObject(By.text("写评论..."));
        if (object != null) {// 等待阅读完成
          Log.e(TAG, "开始阅读");
          int count = 0;
          while (count++ < 10) {
            if (count % 5 == 0 && count != 0) {
              Log.e(TAG, "向下滑动");
              mDevice.swipe(w / 2, endY + randomInt, w / 2, startY + randomInt, step + random.nextInt(10));
            } else {
              Log.e(TAG, "向上滑动");
              mDevice.swipe(w / 2, startY + randomInt, w / 2, endY + randomInt, step + random.nextInt(10));
            }
            mDevice.waitForIdle(timeOut);
            Thread.sleep(1000 * 3);
          }
          Log.e(TAG, "阅读完成");
        } else {
          Log.e(TAG, "内容可能不是新闻");
        }
      } catch (InterruptedException e) {
        Log.e(TAG, "阅读失败", e);
      }

      // 点击返回列表页, 尝试五次返回
      int times = 0;
      while (mDevice.findObject(By.text("榜单")) == null && mDevice.findObject(By.text("我的")) == null && times++ < 5) {
        // 回调列表页时出现对话框
        /*UiObject2 dialog = mDevice.findObject(By.text("忽略"));
        if (dialog != null) {
          dialog.click();
          mDevice.waitForIdle(timeOut);
          Log.e(TAG, "关闭对话框");
          break;
        }*/
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
      mDevice.swipe(w / 2, startY + randomInt, w / 2, endY + randomInt, step + random.nextInt(10));
      mDevice.waitForIdle(timeOut * 3);
    }
  }

  @Override
  String getAPPName() {
    return "中青阅读";
  }

  @Override
  String getPackageName() {
    return packageName;
  }
}
