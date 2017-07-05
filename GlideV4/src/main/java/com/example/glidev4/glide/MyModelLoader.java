package com.example.glidev4.glide;

import android.support.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.example.glidev4.Picture;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import timber.log.Timber;

import static com.bumptech.glide.load.DataSource.REMOTE;

/**
 * Glide 4.x 自定义GlideModel
 * 这里指定Picture类型的Model由用户处理
 */
public class MyModelLoader implements ModelLoader<Picture, InputStream> {

  public MyModelLoader() {
  }

  @Nullable @Override
  public LoadData<InputStream> buildLoadData(Picture model, int width, int height,
      Options options) {
    return new LoadData<InputStream>(new MyKey(model), new MyDataFetcher(model));
  }

  @Override public boolean handles(Picture s) {
    return true;
  }

  /**
   * 文件唯一ID
   * 这个类可以使用 {@link ObjectKey} 代替
   */
  public static class MyKey implements Key {
    Picture path;

    public MyKey(Picture path) {
      this.path = path;
    }

    @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
      messageDigest.update(path.getFileName().getBytes(CHARSET));
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MyKey myKey = (MyKey) o;
      return path != null ? path.equals(myKey.path) : myKey.path == null;
    }

    @Override public int hashCode() {
      return path != null ? path.hashCode() : 0;
    }
  }

  /**
   * 如何加载数据
   */
  public static class MyDataFetcher implements DataFetcher<InputStream> {

    private Picture file;
    private boolean isCanceled;
    InputStream mInputStream = null;

    public MyDataFetcher(Picture file) {
      this.file = file;
    }

    @Override public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
      // 可以在这里进行一些文件处理,比如根据文件路径处理,文件解密等
      Timber.e(file.getFileName());
      if (!isCanceled) {
        mInputStream = ConcealUtil.getCipherInputStream(new File(file.getFileName()));
      }
      callback.onDataReady(mInputStream);
    }

    @Override public void cleanup() {
      if (mInputStream != null) {
        try {
          mInputStream.close();
        } catch (IOException e) {
          Timber.e(e);
        }
      }
    }

    @Override public void cancel() {
      isCanceled = true;
    }

    @Override public Class<InputStream> getDataClass() {
      return InputStream.class;
    }

    @Override public DataSource getDataSource() {
      //return LOCAL;
      return REMOTE;
      //return DATA_DISK_CACHE;
      //return RESOURCE_DISK_CACHE;
      //return MEMORY_CACHE;
    }
  }

  /**
   * 构造工厂类
   */
  public static class LoaderFactory implements ModelLoaderFactory<Picture, InputStream> {

    public LoaderFactory() {
    }

    @Override public ModelLoader<Picture, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new MyModelLoader();
    }

    @Override public void teardown() {

    }
  }
}