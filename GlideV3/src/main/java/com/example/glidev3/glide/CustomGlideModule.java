package com.example.glidev3.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.glidev3.Picture;
import com.example.glidev3.R;
import java.io.InputStream;

public class CustomGlideModule implements GlideModule {

  @Override public void applyOptions(Context context, GlideBuilder builder) {
    // 设置别的get/set tag id，以免占用View默认的
    ViewTarget.setTagId(R.id.glide_tag_id);
    builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
  }

  @Override public void registerComponents(Context context, Glide glide) {
    // 注册我们的ImageLoader
    glide.register(Picture.class, InputStream.class, new ImageLoader.Factory());
  }
}