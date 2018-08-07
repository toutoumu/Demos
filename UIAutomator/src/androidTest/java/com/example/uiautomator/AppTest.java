package com.example.uiautomator;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.example.uiautomator.testcase.AiQiYiTest;
import com.example.uiautomator.testcase.DongFangTouTiaoTest;
import com.example.uiautomator.testcase.JinRiTouTiaoTest;
import com.example.uiautomator.testcase.JuKanDianTest;
import com.example.uiautomator.testcase.QuTouTiaoTest;
import com.example.uiautomator.testcase.ZhongQingKanDianTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppTest {
  private final String TAG = getClass().getName();

  /**
   * 华为P9
   */
  @Test
  public void StartP9Test() {
    try {
      new AiQiYiTest().start(0); // 爱奇艺 (20元提现)
      new JinRiTouTiaoTest().start(0); //今日头条 (15
      int i = 0;
      while (i++ < 3) {
        new DongFangTouTiaoTest().start(50); // 东方头条(30元提现,更低金额需要累积签到次数)
        new JuKanDianTest().start(50);// 聚看点(10元提现)
        new QuTouTiaoTest().start(5); // 趣头条(汇率低)
      }
    } catch (Exception e) {
      Log.e(TAG, "出错了", e);
    }
  }

  /**
   * 荣耀6plus
   */
  @Test
  public void StartR6Test() {
    try {
      new JinRiTouTiaoTest().start(3); //今日头条
      new AiQiYiTest().start(3); // 爱奇艺
      int i = 0;
      while (i++ < 4) {
        new JuKanDianTest().start(25);// 聚看点
        new DongFangTouTiaoTest().start(0); // 东方头条
        new QuTouTiaoTest().start(20); // 趣头条(汇率低)
      }
    } catch (Exception e) {
      Log.e(TAG, "出错了", e);
    }
  }

  /**
   * 老机器
   */
  @Test
  public void StartOldTest() {
    try {
      int i = 0;
      new AiQiYiTest().start(0); // 爱奇艺
      new JinRiTouTiaoTest().start(0); //今日头条
      while (i++ < 2) {
        new DongFangTouTiaoTest().start(25 + i); // 东方头条
        new JuKanDianTest().start(25 + i);// 聚看点
        new QuTouTiaoTest().start(25 + i); // 趣头条(汇率低)
      }
    } catch (Exception e) {
      Log.e(TAG, "出错了", e);
    }
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
