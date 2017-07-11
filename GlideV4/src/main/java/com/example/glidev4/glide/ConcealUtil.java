package com.example.glidev4.glide;

import android.content.Context;
import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import timber.log.Timber;

public class ConcealUtil {

  private static Crypto crypto = null;
  private static Entity entity = null;

  /**
   * 初始化
   *
   * @param context context
   * @param e 密码
   */
  public static void init(Context context, String e) {
    entity = Entity.create(e);
    crypto = AndroidConceal.get()
        .createDefaultCrypto(new MyKeyChain()/*new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)*/);

    if (!crypto.isAvailable()) {
      destroy();
    }
  }

  public static void destroy() {
    crypto = null;
    entity = null;
  }

  private static void check() {
    if (crypto == null || entity == null) {
      throw new RuntimeException("请初始化.....");
    }
  }

  public static OutputStream getCipherOutputStream(File file) {
    if (file.exists()) return null;
    try {
      OutputStream fileStream = new FileOutputStream(file);
      return getCipherOutputStream(fileStream);
    } catch (IOException e) {
      Timber.e(e);
    }
    return null;
  }

  public static OutputStream getCipherOutputStream(OutputStream fileStream) {
    check();
    try {
      return crypto.getCipherOutputStream(fileStream, entity);
    } catch (IOException e) {
      Timber.e(e);
    } catch (CryptoInitializationException e) {
      Timber.e(e);
    } catch (KeyChainException e) {
      Timber.e(e);
    }
    return null;
  }

  public static InputStream getCipherInputStream(String file) {
    return getCipherInputStream(new File(file));
  }

  public static InputStream getCipherInputStream(File file) {
    check();
    if (!file.exists()) return null;
    try {
      InputStream inputStream = new FileInputStream(file);
      return crypto.getCipherInputStream(inputStream, entity);
    } catch (FileNotFoundException e) {
      Timber.e(e);
    } catch (KeyChainException e) {
      Timber.e(e);
    } catch (CryptoInitializationException e) {
      Timber.e(e);
    } catch (IOException e) {
      Timber.e(e);
    }
    return null;
  }

  /**
   * 保存字节流
   *
   * @param data 数据
   */
  public static void saveFile(byte data[], String path) {
    String fileName = System.currentTimeMillis() + ".jpg";
    File image = new File(path, fileName);
    try {
      OutputStream outStream = getCipherOutputStream(image);
      if (outStream != null) {
        outStream.write(data);
        outStream.flush();
        outStream.close();
      }
    } catch (IOException e) {
      Timber.e(e);
    }
  }

  public static void saveFile(InputStream inputStream, String path) {
    int read;
    byte[] buffer = new byte[1024];

    File image = new File(path);
    if (image.exists()) return;
    try {
      OutputStream outputStream = getCipherOutputStream(image);
      while ((read = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, read);
      }
      inputStream.close();
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      Timber.e(e);
    }
  }
}


