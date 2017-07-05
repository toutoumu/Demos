package com.example.glidev3.glide;

import android.content.Context;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * 加密数据加载类
 */
public class ImageLoader implements ModelLoader<IPicture, InputStream> {

  public ImageLoader() {
  }

  @Override
  public DataFetcher<InputStream> getResourceFetcher(IPicture model, int width, int height) {
    return new ImageDataFetcher(model);
  }

  /**
   * 由于被conceal加密过,因此图片文件不能直接使用Glide的各种load方法加载,因此需要自定义加载方式
   * 自定义的获取图片数据的类,这里是需要获取由Facebook(conceal)加密过的数据
   */
  public static class ImageDataFetcher implements DataFetcher<InputStream> {

    // 检查是否取消任务的标识
    private volatile boolean mIsCanceled;

    private final IPicture mPath;
    private InputStream mInputStream;

    public ImageDataFetcher(IPicture filePath) {
      mPath = filePath;
    }

    /**
     * 在后台线程中调用，用于获取图片的数据流，给Glide处理
     *
     * @param priority
     * @throws Exception
     */
    @Override public InputStream loadData(Priority priority) throws Exception {
      if (mIsCanceled) {
        return null;
      }
      mInputStream = fetchStream(mPath.getFileName());
      return mInputStream;
    }

    /**
     * 返回解密后的数据流
     *
     * @param file 文件名
     * @return inputStream
     */
    private InputStream fetchStream(String file) {
      // 返回解密数据流
      return ConcealUtil.getCipherInputStream(file);
    }

    /**
     * 在后台线程中调用，在Glide处理完{@link #loadData(Priority)}返回的数据后，进行清理和回收资源
     */
    @Override public void cleanup() {
      if (mInputStream != null) {
        try {
          mInputStream.close();
        } catch (IOException e) {
          Log.e("Glide", "Glide", e);
        } finally {
          mInputStream = null;
        }
      }
    }

    /**
     * 在UI线程中调用，返回用于区别数据的唯一id
     */
    @Override public String getId() {
      return mPath.getFileName();
    }

    /**
     * 在UI线程中调用，取消加载任务
     */
    @Override public void cancel() {
      mIsCanceled = true;
    }
  }

  /**
   * ModelLoader工厂，在向Glide注册自定义ModelLoader时使用到
   */
  public static class Factory implements ModelLoaderFactory<IPicture, InputStream> {

    @Override public ModelLoader<IPicture, InputStream> build(Context context,
        GenericLoaderFactory factories) {
      // 返回ImageLoader对象
      return new ImageLoader();
    }

    @Override public void teardown() {

    }
  }
}