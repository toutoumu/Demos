package com.example.uiautomator;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.example.uiautomator.testcase.AiQiYiTest;
import com.example.uiautomator.testcase.DongFangTouTiaoTest;
import com.example.uiautomator.testcase.HaoKanShiPinTest;
import com.example.uiautomator.testcase.JinRiTouTiaoTest;
import com.example.uiautomator.testcase.JuKanDianTest;
import com.example.uiautomator.testcase.QuTouTiaoTest;
import com.example.uiautomator.testcase.ZhongQingKanDianTest;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppTest {
  private final String TAG = getClass().getName();

  /**
   * 提供给已经root的设备使用
   */
  @Test
  public void allTest() {
    Bundle a = InstrumentationRegistry.getArguments();

    int aiqiyi = 0;
    int repeat = 1;
    try {
      repeat = Integer.parseInt(a.getString("repeat"));
    } catch (Exception e) {
      Log.e(TAG, "循环次数错了", e);
    }
    try {
      aiqiyi = Integer.parseInt(a.getString("aiqiyi"));
      aiqiyi = new AiQiYiTest().start(aiqiyi); // 爱奇艺
    } catch (Exception e) {
      Log.e(TAG, "爱奇艺出错了:" + aiqiyi, e);
    }
    try {
      int jinritoutiao = Integer.parseInt(a.getString("jinritoutiao"));
      jinritoutiao = new JinRiTouTiaoTest().start(jinritoutiao); //今日头条
    } catch (Exception e) {
      Log.e(TAG, "今日头条出错了", e);
    }
    try {
      int haokan = Integer.parseInt(a.getString("haokan"));
      haokan = new JinRiTouTiaoTest().start(haokan);
    } catch (Exception e) {
      Log.e(TAG, "好看视频出错了", e);
    }

    while (repeat-- > 0) {
      try {
        int dongfangtoutiao = Integer.parseInt(a.getString("dongfangtoutiao"));
        dongfangtoutiao = new DongFangTouTiaoTest().start(dongfangtoutiao); // 东方头条
      } catch (Exception e) {
        Log.e(TAG, "东方头条出错了", e);
      }
      try {
        int jukandian = Integer.parseInt(a.getString("jukandian"));
        jukandian = new JuKanDianTest().start(jukandian);// 聚看点
      } catch (Exception e) {
        Log.e(TAG, "聚看点出错了", e);
      }
      try {
        int qutoutiao = Integer.parseInt(a.getString("qutoutiao"));
        qutoutiao = new QuTouTiaoTest().start(qutoutiao); // 趣头条(汇率低)
      } catch (Exception e) {
        Log.e(TAG, "趣头条出错了", e);
      }
    }
  }

  /**
   * 荣耀6plus
   */
  @Test
  public void R6Test() {
    try {
      int i = 0;
      Random random = new Random();
      new JinRiTouTiaoTest().start(25); //今日头条
      new AiQiYiTest().start(25); // 爱奇艺
      while (i++ < 4) {
        new DongFangTouTiaoTest().start(50 + random.nextInt(10)); // 东方头条
        new JuKanDianTest().start(50 + random.nextInt(10));// 聚看点
        new QuTouTiaoTest().start(30 + random.nextInt(10)); // 趣头条(汇率低)
      }
      new HaoKanShiPinTest().start(30);
    } catch (Exception e) {
      Log.e(TAG, "出错了", e);
    }
  }

  /**
   * 好看视频
   */
  @Test
  public void HaoKanShiPinTest() {
    new HaoKanShiPinTest().start(50);
  }

  /**
   * 东方头条测试 高提成 每天5000+金币 使用[徒弟账号]跑用例
   */
  @Test
  public void DongFangTouTiaoTest() {
    new DongFangTouTiaoTest().start(200);
  }

  /**
   * 聚看点测试 每天2000-金币 记得[签到]奖励高
   */
  @Test
  public void JuKanDianTest() {
    new JuKanDianTest().start(200);
  }

  /**
   * 爱奇艺测试 每天500金币
   */
  @Test
  public void aiQiYiTest() {
    new AiQiYiTest().start(25);
  }

  /**
   * 今日头条测试 每天300金币
   */
  @Test
  public void JinRiTouTiaoTest() {
    new JinRiTouTiaoTest().start(25);
  }

  /**
   * 趣头条测试 提成最低
   */
  @Test
  public void quTouTiaoTest() {
    new QuTouTiaoTest().start(200);
  }

  /**
   * 中青看点测试
   */
  @Test
  public void zhongQingKanDianTest() {
    new ZhongQingKanDianTest().start(100);
  }
}
