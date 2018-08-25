package com.example.demo.testcase.log;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import org.apache.log4j.Logger;

public class LogUtil {
  private String tag;
  private String logPath;
  private Logger logger;

  public LogUtil(String tag) {
    this.tag = tag;
    logger = getLogger(tag);
    logPath = Environment.getExternalStorageDirectory() + File.separator + "aaaaaa" + File.separator;
    File logFile = new File(logPath);
    if (!logFile.exists()) {
      logFile.mkdir();
    }
  }

  public void i(Object msg) {
    logger.info(msg);
    Log.i(this.tag, msg + "");
  }

  public void v(Object msg) {
    logger.info(msg);
    Log.v(this.tag, msg + "");
  }

  public void d(Object msg) {
    logger.debug(msg);
    Log.d(this.tag, msg + "");
  }

  public void w(Object msg) {
    logger.warn(msg);
    Log.w(this.tag, msg + "");
  }

  public void e(Object msg) {
    logger.error(msg);
    Log.e(this.tag, msg + "");
  }

  public void e(String message, Exception e) {
    logger.error(message);
    logger.error(e);
    Log.e(this.tag, message + "", e);
  }

  private Logger getLogger(String tag) {
    Log4jConfigure.configure();
    if ("".equals(tag)) {
      return Logger.getRootLogger();
    }
    return Logger.getLogger(tag);
  }
} 