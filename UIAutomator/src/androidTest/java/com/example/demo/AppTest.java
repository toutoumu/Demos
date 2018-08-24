package com.example.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.example.demo.testcase.AiQiYiTest;
import com.example.demo.testcase.BaseTest;
import com.example.demo.testcase.DongFangTouTiaoTest;
import com.example.demo.testcase.HaoKanShiPinTest;
import com.example.demo.testcase.JinRiTouTiaoTest;
import com.example.demo.testcase.JuKanDianTest;
import com.example.demo.testcase.QuTouTiaoTest;
import com.example.demo.testcase.QuanMinXiaoShiPinTest;
import com.example.demo.testcase.ZhongQingKanDianTest;
import com.example.demo.testcase.log.LogUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    List<BaseTest> tests = new ArrayList<>();
    try {
      repeat = Integer.parseInt(a.getString("repeat"));
      log.d("************************整体循环次数" + repeat + "************************");
    } catch (Exception e) {
      log.e("整体循环次数", e);
    }
    try {
      int aiqiyi = Integer.parseInt(a.getString("aiqiyi"));
      log.d("************************爱奇艺循环次数:" + aiqiyi + "************************");
      AiQiYiTest aiQiYiTest = new AiQiYiTest();
      tests.add(aiQiYiTest);
      aiqiyi = aiQiYiTest.start(aiqiyi); // 爱奇艺
      log.d("************************爱奇艺实际循环次数:" + aiqiyi + "************************");
    } catch (Exception e) {
      log.e("爱奇艺出错了:", e);
    }
    try {
      int jinritoutiao = Integer.parseInt(a.getString("jinritoutiao"));
      log.d("************************今日头条循环次数:" + jinritoutiao + "************************");
      JinRiTouTiaoTest jinRiTouTiaoTest = new JinRiTouTiaoTest();
      tests.add(jinRiTouTiaoTest);
      jinritoutiao = jinRiTouTiaoTest.start(jinritoutiao);//今日头条
      log.d("************************今日头条实际循环次数:" + jinritoutiao + "************************");
    } catch (Exception e) {
      log.e("今日头条出错了", e);
    }
    try {
      int haokan = Integer.parseInt(a.getString("haokan"));
      log.d("************************好看视频循环次数:" + haokan + "************************");
      HaoKanShiPinTest haoKanShiPinTest = new HaoKanShiPinTest();
      tests.add(haoKanShiPinTest);
      haokan = haoKanShiPinTest.start(haokan);
      log.d("************************好看视频实际循环次数:" + haokan + "************************");
    } catch (Exception e) {
      log.e("好看视频出错了", e);
    }

    while (repeat-- > 0) {
      try {
        int dongfangtoutiao = Integer.parseInt(a.getString("dongfangtoutiao"));
        log.d("************************东方头条循环次数:" + dongfangtoutiao + "************************");
        DongFangTouTiaoTest dongFangTouTiaoTest = new DongFangTouTiaoTest();
        tests.add(dongFangTouTiaoTest);
        dongfangtoutiao = dongFangTouTiaoTest.start(dongfangtoutiao); // 东方头条
        log.d("************************东方头条实际循环次数:" + dongfangtoutiao + "************************");
      } catch (Exception e) {
        log.e("东方头条出错了", e);
      }
      try {
        int jukandian = Integer.parseInt(a.getString("jukandian"));
        log.d("************************聚看点循环次数:" + jukandian + "************************");
        JuKanDianTest juKanDianTest = new JuKanDianTest();
        tests.add(juKanDianTest);
        jukandian = juKanDianTest.start(jukandian); // 聚看点
        log.d("************************聚看点实际循环次数" + jukandian + "************************");
      } catch (Exception e) {
        log.e("聚看点出错了", e);
      }
      try {
        int qutoutiao = Integer.parseInt(a.getString("qutoutiao"));
        log.d("************************趣头条循环次数:" + qutoutiao + "************************");
        QuTouTiaoTest quTouTiaoTest = new QuTouTiaoTest();
        tests.add(quTouTiaoTest);
        qutoutiao = quTouTiaoTest.start(qutoutiao); // 趣头条(汇率低)
        log.d("************************趣头条实际循环次数:" + qutoutiao + "************************");
      } catch (Exception e) {
        log.e("趣头条出错了", e);
      }
      try {
        int zhongqingkandian = Integer.parseInt(a.getString("zhongqingkandian"));
        log.d("************************中青看点循环次数:" + zhongqingkandian + "************************");
        ZhongQingKanDianTest quTouTiaoTest = new ZhongQingKanDianTest();
        // tests.add(quTouTiaoTest);
        zhongqingkandian = quTouTiaoTest.start(zhongqingkandian);
        log.d("************************中青看点实际循环次数:" + zhongqingkandian + "************************");
      } catch (Exception e) {
        log.e("中青看点出错了", e);
      }
    }

    // 阅读完成之后检测一下成果
    for (BaseTest test : tests) {
      test.doCheck();
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
      while (i++ < 1) {
        Random random = new Random();
        new HaoKanShiPinTest().start(30);//30
        new AiQiYiTest().start(25); //20
        new JinRiTouTiaoTest().start(15); //10
        new JuKanDianTest().start(40 + random.nextInt(10));//200
        new DongFangTouTiaoTest().start(50 + random.nextInt(10)); //1000
        new QuTouTiaoTest().start(50 + random.nextInt(10)); // 趣头条(汇率低)
      }
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
   * 全民小视频测试
   */
  @Test
  public void quanMinXiaoShiPinTest() {
    new QuanMinXiaoShiPinTest().start(30);
  }

  /**
   * 中青看点测试
   */
  @Test
  public void zhongQingKanDianTest() {
    new ZhongQingKanDianTest().start(100);
  }

  @Test
  public void testa() {
    IMessageServer[] greetService = new IMessageServer[1];
    ServiceConnection conn = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i("ServiceConnection", "onServiceConnected() called");
        greetService[0] = IMessageServer.Stub.asInterface(service);
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
        //This is called when the connection with the service has been unexpectedly disconnected,
        //that is, its process crashed. Because it is running in our same process, we should never see this happen.
        Log.i("ServiceConnection", "onServiceDisconnected() called");
      }
    };
    Context context = InstrumentationRegistry.getInstrumentation().getContext();
    Intent intent = new Intent("android.intent.action.MessageService");
    // intent.setClassName("com.example.uiautomator", MessageService.class.getName());
    intent.setPackage("com.example.uiautomator");
    //intent.setComponent(new ComponentName("com.example.uiautomator", "com.example.uiautomator.MessageService"));
    context.bindService(getExplicitIntent(context, intent), conn, Context.BIND_AUTO_CREATE);

    while (true) {
      try {
        greetService[0].toast("我是我");
        Thread.sleep(1000 * 3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }

  public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
    // Retrieve all services that can match the given intent
    PackageManager pm = context.getPackageManager();
    List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
    // Make sure only one match was found
    if (resolveInfo == null || resolveInfo.size() != 1) {
      return null;
    }
    // Get component info and create ComponentName
    ResolveInfo serviceInfo = resolveInfo.get(0);
    String packageName = serviceInfo.serviceInfo.packageName;
    String className = serviceInfo.serviceInfo.name;
    ComponentName component = new ComponentName(packageName, className);
    // Create a new intent. Use the old one for extras and such reuse
    Intent explicitIntent = new Intent(implicitIntent);
    // Set the component to be explicit
    explicitIntent.setComponent(component);
    return explicitIntent;
  }
}
