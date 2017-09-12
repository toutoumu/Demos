package com.example.glidev4.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.FileLoader;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.glidev4.Picture;
import com.example.glidev4.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import timber.log.Timber;

/**
 * Glide 4.x 自定义处理Picture类型和File类型的文件处理, GlideModel
 */
@GlideModule public class MyGlideModule extends AppGlideModule {

  @Override public void applyOptions(Context context, GlideBuilder builder) {
    // 设置别的get/set tag id，以免占用View默认的
    ViewTarget.setTagId(R.id.glide_tag_id);
    RequestOptions options = new RequestOptions().format(DecodeFormat.PREFER_RGB_565);
    builder.setDefaultRequestOptions(options);
  }

  @Override public void registerComponents(Context context, Glide glide, Registry registry) {
    // 指定Model类型为Picture的处理方式
    registry.append(Picture.class, InputStream.class, new MyModelLoader.LoaderFactory());

    // 指定Model类型为File的处理方式
    registry.append(File.class, InputStream.class,
        new FileLoader.Factory<InputStream>(new FileLoader.FileOpener<InputStream>() {

          @Override public InputStream open(File file) throws FileNotFoundException {
            // 可以在这里进行文件处理,比如解密等.
            Timber.e(file.getAbsolutePath());
            return ConcealUtil.getCipherInputStream(file);
            // return new FileInputStream(file);
          }

          @Override public void close(InputStream inputStream) throws IOException {
            inputStream.close();
          }

          @Override public Class<InputStream> getDataClass() {
            return InputStream.class;
          }
        }));
  }

  /**
   * 清单解析的开启
   *
   * 这里不开启，避免添加相同的modules两次
   *
   * @return
   */
  @Override public boolean isManifestParsingEnabled() {
    return false;
  }
}