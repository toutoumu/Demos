package com.example.uiautomator;

import android.util.Log;
import com.example.uiautomator.testcase.AiQiYiTest;
import com.example.uiautomator.testcase.DongFangTouTiaoTest;
import com.example.uiautomator.testcase.JinRiTouTiaoTest;
import com.example.uiautomator.testcase.JuKanDianTest;
import com.example.uiautomator.testcase.QuTouTiaoTest;
import com.example.uiautomator.testcase.ZhongQingKanDianTest;
import org.junit.Test;

public class AppTest {
  private final String TAG = getClass().getName();

  /**
   * 东方头条测试
   */
  @Test
  public void DongFangTouTiaoTest() {
    new DongFangTouTiaoTest().start();
  }

  /**
   * 聚看点测试
   */
  @Test
  public void JuKanDianTest() {
    new JuKanDianTest().start();
  }

  /**
   * 趣头条测试
   */
  @Test
  public void quTouTiaoTest() {
    new QuTouTiaoTest().start();
  }

  /**
   * 爱奇艺测试
   */
  @Test
  public void aiQiYiTest() {
    new AiQiYiTest().start();
  }

  /**
   * 今日头条测试
   */
  @Test
  public void JinRiTouTiaoTest() {
    new JinRiTouTiaoTest().start();
  }

  @Test
  public void StartTest() {
    while (true) {
      try {
        aiQiYiTest(); // 爱奇艺
        JinRiTouTiaoTest(); //今日头条
        DongFangTouTiaoTest(); // 东方头条
        JuKanDianTest(); // 聚看点
        quTouTiaoTest(); // 趣头条(汇率低)
      } catch (Exception e) {
        Log.e(TAG, "出错了", e);
      }
    }
  }

  /**
   * 中青看点测试
   */
  @Test
  public void zhongQingKanDianTest() {
    new ZhongQingKanDianTest().start();
  }
}
