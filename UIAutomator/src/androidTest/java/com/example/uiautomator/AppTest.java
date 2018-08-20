package com.example.uiautomator;

import android.os.Bundle;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.example.uiautomator.testcase.AiQiYiTest;
import com.example.uiautomator.testcase.BaseTest;
import com.example.uiautomator.testcase.DongFangTouTiaoTest;
import com.example.uiautomator.testcase.HaoKanShiPinTest;
import com.example.uiautomator.testcase.JinRiTouTiaoTest;
import com.example.uiautomator.testcase.JuKanDianTest;
import com.example.uiautomator.testcase.QuTouTiaoTest;
import com.example.uiautomator.testcase.ZhongQingKanDianTest;
import com.example.uiautomator.testcase.log.LogUtil;
import java.io.File;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppTest {
  private final String TAG = getClass().getSimpleName();

  /**
   * 提供给已经root的设备使用
   */
  @Test
  public void testAllDevice() {
    Bundle a = InstrumentationRegistry.getArguments();
    LogUtil log = new LogUtil(TAG);
    int repeat = 1;

    try {
      repeat = Integer.parseInt(a.getString("repeat"));
      log.d("************************整体循环次数" + repeat + "************************");
    } catch (Exception e) {
      log.e("整体循环次数", e);
    }
    try {
      int aiqiyi = Integer.parseInt(a.getString("aiqiyi"));
      log.d("************************爱奇艺循环次数:" + aiqiyi + "************************");
      aiqiyi = new AiQiYiTest().start(aiqiyi); // 爱奇艺
      log.d("************************爱奇艺实际循环次数:" + aiqiyi + "************************");
    } catch (Exception e) {
      log.e("爱奇艺出错了:", e);
    }
    try {
      int jinritoutiao = Integer.parseInt(a.getString("jinritoutiao"));
      log.d("************************今日头条循环次数:" + jinritoutiao + "************************");
      jinritoutiao = new JinRiTouTiaoTest().start(jinritoutiao); //今日头条
      log.d("************************今日头条实际循环次数:" + jinritoutiao + "************************");
    } catch (Exception e) {
      log.e("今日头条出错了", e);
    }
    try {
      int haokan = Integer.parseInt(a.getString("haokan"));
      log.d("************************好看视频循环次数:" + haokan + "************************");
      haokan = new HaoKanShiPinTest().start(haokan);
      log.d("************************好看视频实际循环次数:" + haokan + "************************");
    } catch (Exception e) {
      log.e("好看视频出错了", e);
    }

    while (repeat-- > 0) {
      try {
        int dongfangtoutiao = Integer.parseInt(a.getString("dongfangtoutiao"));
        log.d("************************东方头条循环次数:" + dongfangtoutiao + "************************");
        dongfangtoutiao = new DongFangTouTiaoTest().start(dongfangtoutiao); // 东方头条
        log.d("************************东方头条实际循环次数:" + dongfangtoutiao + "************************");
      } catch (Exception e) {
        log.e("东方头条出错了", e);
      }
      try {
        int jukandian = Integer.parseInt(a.getString("jukandian"));
        log.d("************************聚看点循环次数:" + jukandian + "************************");
        jukandian = new JuKanDianTest().start(jukandian);// 聚看点
        log.d("************************聚看点实际循环次数" + jukandian + "************************");
      } catch (Exception e) {
        log.e("聚看点出错了", e);
      }
      try {
        int qutoutiao = Integer.parseInt(a.getString("qutoutiao"));
        log.d("************************趣头条循环次数:" + qutoutiao + "************************");
        qutoutiao = new QuTouTiaoTest().start(qutoutiao); // 趣头条(汇率低)
        log.d("************************趣头条实际循环次数:" + qutoutiao + "************************");
      } catch (Exception e) {
        log.e("趣头条出错了", e);
      }
    }

    // 运行结束,删除文件
    File directory = new File(Environment.getExternalStorageDirectory(), File.separator + "aaaaaa" + File.separator);
    File file = new File(directory, "shutdown.txt");
    if (file.exists()) {
      file.delete();
      log.d("************************运行结束,删除文件************************");
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
      new JinRiTouTiaoTest().start(0); //今日头条
      new AiQiYiTest().start(0); // 爱奇艺
      while (i++ < 4) {
        new JuKanDianTest().start(30 + random.nextInt(10));// 聚看点
        // new DongFangTouTiaoTest().start(30 + random.nextInt(10)); // 东方头条
        new QuTouTiaoTest().start(30 + random.nextInt(10)); // 趣头条(汇率低)
      }
      new HaoKanShiPinTest().start(33);
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

  @Test
  public void teddd() {
    BaseTest baseTest = new BaseTest() {
      @Override
      public int start(int count) {
        return 0;
      }

      @Override
      public String getAPPName() {
        return null;
      }

      @Override
      public String getPackageName() {
        return null;
      }
    };
    baseTest.logD("aksljdflkajdsf;laskdjf");
    baseTest.logE("asdfasdf");
  }
}
