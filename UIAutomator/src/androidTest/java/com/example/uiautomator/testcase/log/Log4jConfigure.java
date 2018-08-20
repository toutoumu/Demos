package com.example.uiautomator.testcase.log;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Level;

public class Log4jConfigure {
  private static final String TAG = "Log4jConfigure";
  private static final String DEFAULT_LOG_FILE_NAME = "u2Test.txt";
  private static final String DEFAULT_LOG_DIR =
    Environment.getExternalStorageDirectory() + File.separator + "aaaaaa" + File.separator;//HOME文件夹

  public static void configure() {
    final LogConfigurator logConfigurator = new LogConfigurator();
    try {
      File logFile = new File(DEFAULT_LOG_DIR + DEFAULT_LOG_FILE_NAME);
      /*if (logFile.exists() && !FileUtil.getIsLog4jConfigured(BaseCase.AD_CONFIG_FILE)) {
        logFile.delete();
        FileUtil.updateConfigFile(BaseCase.AD_CONFIG_FILE, "isLog4jConfigured", "true");
      }*/
      SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd");//12小时制
      logConfigurator.setFileName(DEFAULT_LOG_DIR + ss.format(new Date()) + "__" + DEFAULT_LOG_FILE_NAME);
      //以下为通用配置
      logConfigurator.setUseLogCatAppender(false);//不输出到logcat
      logConfigurator.setUseFileAppender(true);
      logConfigurator.setImmediateFlush(true);
      logConfigurator.setRootLevel(Level.DEBUG);
      logConfigurator.setFilePattern("%d\t%p\t%c:\t%m%n");
      logConfigurator.configure();
      android.util.Log.i(TAG, "Log4j config finished");
    } catch (Throwable throwable) {
      logConfigurator.setResetConfiguration(true);
      android.util.Log.e(TAG, "Log4j config error, use default config. Error:" + throwable);
    }
  }
} 