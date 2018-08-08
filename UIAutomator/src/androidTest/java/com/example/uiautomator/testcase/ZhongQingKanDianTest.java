package com.example.uiautomator.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

public class ZhongQingKanDianTest extends BaseTest {

  public ZhongQingKanDianTest() {
    super();
  }

  private void read() {

  }

  @Override
  public int start(int repCount) {
    if (repCount == 0) {
      return repCount;
    }
    int startY = height / 2;
    int endY = height / 4;

    mDevice.pressHome();
    mDevice.waitForIdle(1000);

    startAPP();

    while (true) {
      // 点击跳转到详情页
      Log.e(TAG, "点击跳转到详情页,随机数: " + " startY : " + (startY) + " endY :" + (endY));
      mDevice.click(width / 3, height / 4);
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
              mDevice.swipe(centerX, endY, centerX, startY, 30);
            } else {
              Log.e(TAG, "向上滑动");
              mDevice.swipe(centerX, startY, centerX, endY, 30);
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
        return repCount;
      }

      // 列表向上滑动
      Log.e(TAG, "列表向上滑动");
      mDevice.swipe(width / 2, startY, width / 2, endY, 30);
      mDevice.waitForIdle(timeOut * 3);
    }
  }

  @Override
  String getAPPName() {
    return "中青阅读";
  }

  @Override
  String getPackageName() {
    return "cn.youth.news";
  }
}
