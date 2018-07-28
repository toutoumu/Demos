package com.example.uiautomator;

import com.example.uiautomator.testcase.AiQiYiTest;
import com.example.uiautomator.testcase.QuTouTiaoTest;
import com.example.uiautomator.testcase.ZhongQingKanDianTest;
import org.junit.Test;

public class AppTest {
  private final String TAG = getClass().getName();

  /**
   * 中青看点测试
   */
  @Test
  public void zhongQingKanDianTest() {
    new ZhongQingKanDianTest().start();
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
}
